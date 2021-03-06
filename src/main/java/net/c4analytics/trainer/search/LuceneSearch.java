package net.c4analytics.trainer.search;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
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

import net.c4analytics.trainer.dao.Setup;

public class LuceneSearch {
	private final static Logger logger = LoggerFactory.getLogger(LuceneSearch.class);
	Setup setup;
	private Directory indexStore;
	
	public void init() throws IOException {
		File indexF = new File(setup.getIndexPath());
		if (!indexF.exists()) {
			FileUtils.forceMkdir(indexF);
		}
		if (!indexF.exists() || !indexF.isDirectory()) {
			throw new IOException("indexPath must be a directory with appropriate permissions.");
		}
		indexStore = new NIOFSDirectory(Paths.get(setup.getIndexPath()));
	}

	public void query(String qstr) throws ParseException, IOException {
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

	public void setSetup(Setup setup) {
		this.setup = setup;
	}
}
