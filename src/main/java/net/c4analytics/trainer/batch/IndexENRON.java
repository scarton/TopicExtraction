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
 * Multiple passes over the data to index, clean and load:
 * 1. Load the raw text and index for display and searching. 
 * 2. Go over text again, perform regex cleaning and remove common sentences, At the same time, compute sentence Hash frequencies. Save to binary SentenceHashModel
 * 3. 3rd pass, remove common sentences, tokenize, spell correct and stem words and word frequencies. Save to Bag of Words
 * 4. 4th Pass - compute IDF for Bag of words.
 * 
 *
 *
 */
public class IndexENRON {
	final static Logger logger = LoggerFactory.getLogger(IndexENRON.class);
	final static int INDEX_LIMIT=100; // Integer.MAX_VALUE
	public static void main(String[] args) throws IOException, SolrServerException {
		SolrIndexer indexer = new SolrIndexer("http://localhost:2181");
		indexer.eraseIndex();
		File srcF = new File(args[0]);
		
		File[] listOfFiles = srcF.listFiles();
		TextCleaner cleaner = new TextCleaner();
		for (int i = 0; i < Math.min(INDEX_LIMIT, listOfFiles.length); i++) {
			if (listOfFiles[i].isFile()) {
				logger.debug("File " + listOfFiles[i].getName());
				String text = FileUtils.readFileToString(listOfFiles[i]);
				if (text!=null && text.length()>500) {
					String id = FilenameUtils.removeExtension(listOfFiles[i].getName()).trim();
//					cleaner.parseSentences(text);
//					cleaner.cleanTextByRegex(text);
					cleaner.extractWords();
					indexer.index(text, id, "enron-"+i,cleaner.getCleanedText(),cleaner.getScrubbedWords());
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
