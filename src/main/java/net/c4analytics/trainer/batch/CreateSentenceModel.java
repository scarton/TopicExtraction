package net.c4analytics.trainer.batch;


import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.c4analytics.nlp.TextCleaner;

/**
 * Using cleanedText field in SOLR, Create Sentence Model and write to a file 
 *
 */
public class CreateSentenceModel {
	final static Logger logger = LoggerFactory.getLogger(CreateSentenceModel.class);
	final static int INDEX_LIMIT=100; // Integer.MAX_VALUE
	public static void main(String[] args) throws IOException, SolrServerException {
//		SolrIndexer indexer = new SolrIndexer("http://localhost:8983/solr/enron");
		File srcF = new File(args[0]);
		
		File[] listOfFiles = srcF.listFiles();
		TextCleaner cleaner = new TextCleaner();
		for (int i = 0; i < Math.min(INDEX_LIMIT, listOfFiles.length); i++) {
			if (listOfFiles[i].isFile()) {
				logger.debug("File " + listOfFiles[i].getName());
				String text = FileUtils.readFileToString(listOfFiles[i]);
				cleaner.cleanTextByRegex(text);
				String cleaned = cleaner.getCleanedText();
//				logger.debug(cleaned);
				if (cleaned!=null) {
					String id = FilenameUtils.removeExtension(listOfFiles[i].getName()).trim();
					logger.debug("indexing {}",id);
					cleaner.parseSentences(cleaned);
//					cleaner.extractWords();
//					indexer.index(text, id, "enron-"+i,cleaner.getCleanedText(),cleaner.getScrubbedWords());
//					if (i%500==0)
//						indexer.commit();
				}
			} 
		}
//		indexer.commit();
//		indexer.testQuery(args[1]);
//		indexer.close();
		logger.debug("Run Ended...");
	}
}
