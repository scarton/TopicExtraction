package com.aitheras.trainer.batch;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

public class IndexBase {
	final static Logger logger = LoggerFactory.getLogger(IndexBase.class);
	static IndexWriter indexWriter;
	Directory indexStore;
	
	public void makeIndexerWriter(String indexPath) throws IOException {
		File indexF = new File(indexPath);
		if (!indexF.exists()) {
			FileUtils.forceMkdir(indexF);
		}
		if (!indexF.exists() || !indexF.isDirectory()) {
			throw new IOException("indexPath must be a directory with appropriate permissions.");
		}
		indexStore = new NIOFSDirectory(Paths.get(indexPath));

		StandardAnalyzer analyzer = new StandardAnalyzer();
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		indexWriter = new IndexWriter(indexStore, config);
	}
	public void index(String text, String id) throws IOException {
		index(text,id,null);
	}
	public void index(String text, String id, List<String> terms) throws IOException {
		Document doc = new Document();
		doc.add(new TextField("text", text, Field.Store.YES));
		doc.add(new StringField("id", id, Field.Store.YES));
		if (terms!=null) {
			for (String term : terms) {
			    Field field = new StringField("terms", term, Field.Store.YES);
			    doc.add(field);
			}
			logger.debug("{}",Joiner.on(", ").join(terms));
		}
		indexWriter.addDocument(doc);
	}
	public void testQuery(String qstr) throws ParseException, IOException {
		Analyzer analyzer = new KeywordAnalyzer();
	    Query q = new QueryParser("text", analyzer).parse(qstr);
	    int hitsPerPage = 10;
	    IndexReader reader = DirectoryReader.open(indexStore);
	    IndexSearcher searcher = new IndexSearcher(reader);
	    TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage);
	    searcher.search(q, collector);
	    ScoreDoc[] hits = collector.topDocs().scoreDocs;
	   
	    System.out.println("Found " + hits.length + " hits.");
	    for(int i=0;i<hits.length;++i) {
	      int docId = hits[i].doc;
	      Document d = searcher.doc(docId);
	      System.out.println((i + 1) + ". " + d.get("id") + "\t" + d.get("text"));
	    }

	    // reader can only be closed when there
	    // is no need to access the documents any more.
	    reader.close();
	}


}
