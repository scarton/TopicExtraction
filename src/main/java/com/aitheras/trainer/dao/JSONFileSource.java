package com.aitheras.trainer.dao;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Provides data for the GUI - random or searched doc text.
 * @TODO refactor sources to use an interface to a specific data service.
 * @author Steve Carton, stephen.carton@aitheras.com
 *
 */
public class JSONFileSource implements DocumentSource {
	final static Logger logger = LoggerFactory.getLogger(JSONFileSource.class);
	private Setup setup;
	private static final String JSON_ELEMENT="html";
	private static final String SOURCE_EXT=".json";
	private static final int MAXSOURCES = 100; // Integer.MAX_VALUE
	private ArrayList<String> sourceFiles=new ArrayList<String>();
	
	public void init() throws IOException {
		makeSources();
	}
	
	public void makeSources() throws IOException {
		File srcF = new File(setup.getDataSource());
		FileFilter fileFilter = new WildcardFileFilter("*"+SOURCE_EXT);
		File[] listOfFiles = srcF.listFiles(fileFilter);

		for (int i = 0; i < Math.min(MAXSOURCES, listOfFiles.length); i++) {
			if (listOfFiles[i].isFile()) {
				sourceFiles.add(FilenameUtils.removeExtension(listOfFiles[i].getName()));
			}
		}
	}
	private String getTextWithMarkup(String jdoc) {
		JSONObject jo = (JSONObject)JSONValue.parse(jdoc);
		String text = (String)jo.get(JSON_ELEMENT);
		return text;
	}
	private String getTextWithoutMarkup(String jdoc) {
		JSONObject jo = (JSONObject)JSONValue.parse(jdoc);
		String text = (String)jo.get(JSON_ELEMENT);
		if (text!=null && text.length()>0)
			text = Jsoup.parse(text).text();
		return text;
	}
	public String getRandomId() throws IOException {
		int r = (int)(Math.random()*sourceFiles.size());
		String randomId = sourceFiles.get(r);
		return randomId;
	}
	@Override
	public String getDocTitle(String id) throws IOException {
		return id;
	}
	public String getDocText(String id) throws IOException {
		File sourceFile = new File(setup.getDataSource()+File.separatorChar+id+SOURCE_EXT);
		String json = FileUtils.readFileToString(sourceFile);
		return getTextWithMarkup(json);
	}
	public String getCleanDocText(String id) throws IOException {
		File sourceFile = new File(setup.getDataSource()+File.separatorChar+id+SOURCE_EXT);
		String json = FileUtils.readFileToString(sourceFile);
		return getTextWithoutMarkup(json);
	}
	public void setSetup(Setup setup) {
		this.setup = setup;
	}

	@Override
	public void close() throws IOException {
	}

	@Override
	public long maxDocs() throws IOException {
		return sourceFiles.size();
	}
}
