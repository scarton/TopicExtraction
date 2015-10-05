package com.aitheras.trainer.batch;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.lucene.queryparser.classic.ParseException;
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
public class IndexSCOTUS extends IndexBase {
	final static Logger logger = LoggerFactory.getLogger(IndexSCOTUS.class);
	final static int INDEX_LIMIT=Integer.MAX_VALUE; // Integer.MAX_VALUE
	public static void main(String[] args) throws IOException, ParseException {
		IndexSCOTUS indexer = new IndexSCOTUS();
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
