package com.aitheras.trainer.batch;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aitheras.nlp.TextCleaner;

/**
 * 
 *
 *
 */
public class IndexENRON {
	final static Logger logger = LoggerFactory.getLogger(IndexENRON.class);
	final static int INDEX_LIMIT=10000; // Integer.MAX_VALUE
	public static void main(String[] args) throws IOException, SolrServerException {
		SolrIndexer indexer = new SolrIndexer("http://localhost:8983/solr/enron");
		File srcF = new File(args[0]);
		
		File[] listOfFiles = srcF.listFiles();
		TextCleaner cleaner = new TextCleaner();
		for (int i = 0; i < Math.min(INDEX_LIMIT, listOfFiles.length); i++) {
			if (listOfFiles[i].isFile()) {
				logger.debug("File " + listOfFiles[i].getName());
				String text = FileUtils.readFileToString(listOfFiles[i]);
				if (text!=null && text.length()>500) {
					String id = FilenameUtils.removeExtension(listOfFiles[i].getName()).trim();
					cleaner.cleanText(text);
					indexer.index(text, id, "enron-"+i,cleaner.getScrubbedWords());
					logger.debug("indexing {}",id);
					if (i%500==0)
						indexer.commit();
				}
			} 
		}
		indexer.commit();
		indexer.testQuery(args[1]);
		indexer.close();
	}
}
