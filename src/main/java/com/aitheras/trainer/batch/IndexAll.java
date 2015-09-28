package com.aitheras.trainer.batch;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.lucene.analysis.Analyzer;
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
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JSON Parsing
 * date_blocked, time_retrieved, extracted_by_ocr, local_path, absolute_url, source, 
 * docket, nature_of_suit, blocked, download_url, html, id, 
 * precedential_status, html_with_citations, citation_count, plain_text, citation, html_lawbox, 
 * resource_uri, court, supreme_court_db_id, date_filed, sha1, date_modified, judges
 *
 *
 */
public class IndexAll {
	final static Logger logger = LoggerFactory.getLogger(IndexAll.class);
	final static int INDEX_LIMIT=Integer.MAX_VALUE; // Integer.MA
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
		Document doc = new Document();
		doc.add(new TextField("text", text, Field.Store.YES));
		doc.add(new StringField("id", id, Field.Store.YES));
		indexWriter.addDocument(doc);
	}
	public void testQuery(String qstr) throws ParseException, IOException {
		Analyzer analyzer = new StandardAnalyzer();
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

	public static void main(String[] args) throws IOException, ParseException {
		IndexAll indexer = new IndexAll();
		File srcF = new File(args[0]);
		indexer.makeIndexerWriter(args[1]);
		
		File[] listOfFiles = srcF.listFiles();

		for (int i = 0; i < Math.min(INDEX_LIMIT, listOfFiles.length); i++) {
			if (listOfFiles[i].isFile()) {
				logger.debug("File " + listOfFiles[i].getName());
				String jsonS = FileUtils.readFileToString(listOfFiles[i]);
				JSONObject jo = (JSONObject)JSONValue.parse(jsonS);
//				@SuppressWarnings("unchecked")
//				Set<String> jsonKeys = jo.keySet();
//				logger.debug("{}",jsonKeys);
//				for (String key : jsonKeys) 
//					if (jo.get(key)!=null && ((String)(""+jo.get(key))).length()>100)
//						logger.debug("{}: {}",key,jo.get(key));
				String key = "html";
				String text = (String)jo.get(key);
				if (text!=null && text.length()>0)
					indexer.index(text, FilenameUtils.removeExtension(listOfFiles[i].getName()));
//				logger.debug("{}",text);
			} 
		}
		indexWriter.close();
		indexer.testQuery(args[2]);
	}
}
