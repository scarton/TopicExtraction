package com.aitheras.trainer.batch;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.lucene.queryparser.classic.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 *
 *
 */
public class IndexENRON extends IndexBase {
	final static Logger logger = LoggerFactory.getLogger(IndexENRON.class);
	final static int INDEX_LIMIT=100; // Integer.MAX_VALUE

	public static void main(String[] args) throws IOException, ParseException {
		IndexENRON indexer = new IndexENRON();
		File srcF = new File(args[0]);
		indexer.makeIndexerWriter(args[1]);
		
		File[] listOfFiles = srcF.listFiles();

		for (int i = 0; i < Math.min(INDEX_LIMIT, listOfFiles.length); i++) {
			if (listOfFiles[i].isFile()) {
				logger.debug("File " + listOfFiles[i].getName());
				String text = FileUtils.readFileToString(listOfFiles[i]);
				if (text!=null && text.length()>500) {
					String id = FilenameUtils.removeExtension(listOfFiles[i].getName()).trim();
					indexer.index(text, id);
					logger.debug("indexing {}",id);
				}
			} 
		}
		indexWriter.commit();
		indexWriter.close();
		indexer.testQuery(args[2]);
	}
}
