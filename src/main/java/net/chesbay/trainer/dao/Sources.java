package net.chesbay.trainer.dao;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Sources {
	final static Logger logger = LoggerFactory.getLogger(Sources.class);
	private String sourcePath;
	private String truthPath;
	private static final String JSON_ELEMENT="html";
	private static final String TRUTH_EXT=".key";
	private static final String SOURCE_EXT=".json";
	
	private static final int MAXSOURCES = 100; // Integer.MAX_VALUE
//	private List<String> sourceFiles = new ArrayList<String>();
	private Map<String, ArrayList<String>> sourceFiles = new TreeMap<String, ArrayList<String>>();
	private Map<String, Long> truths = new TreeMap<String, Long>();
	
	public void init() throws IOException {
		makeSources();
		collectTruth();
	}
	
	public void makeSources() throws IOException {
		File srcF = new File(sourcePath);
		FileFilter fileFilter = new WildcardFileFilter("*"+SOURCE_EXT);
		File[] listOfFiles = srcF.listFiles(fileFilter);

		for (int i = 0; i < Math.min(MAXSOURCES, listOfFiles.length); i++) {
			if (listOfFiles[i].isFile()) {
				sourceFiles.put(listOfFiles[i].getName(),getTruth4File(listOfFiles[i].getName()));
			}
		}
	}

	public void collectTruth() throws IOException {
		File truthF = new File(truthPath);
		FileFilter fileFilter = new WildcardFileFilter("*"+TRUTH_EXT);
		File[] listOfFiles = truthF.listFiles(fileFilter);
		logger.debug("Number of truth files: {}",listOfFiles.length);
		for (int i = 0; i<listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				String truthJson = FileUtils.readFileToString(listOfFiles[i]);
				JSONArray ja = (JSONArray)JSONValue.parse(truthJson);
				logger.debug("Truth file {}: {}",listOfFiles[i].getName(),ja.toString());
				for (int c=0; c<ja.size(); c++) {
					JSONObject jo = (JSONObject)ja.get(c);
					String key = (String)jo.get("topic");
					long weight = (Long)jo.get("weight");
					if (truths.containsKey(key)) {
						truths.put(key, truths.get(key)+weight);
					} else {
						truths.put(key, weight);
					}
				}
			}
		}
		logger.debug("Aggregated truths: {}",truths);
	}
	public ArrayList<String> getTruth4File(String fileName) throws IOException {
		String rfn = FilenameUtils.removeExtension(fileName);
		File truthF = new File(truthPath+File.separatorChar+rfn+TRUTH_EXT);
		ArrayList<String> truth = new ArrayList<String>();
		if (truthF.isFile()) {
			String truthJson = FileUtils.readFileToString(truthF);
			JSONArray ja = (JSONArray)JSONValue.parse(truthJson);
			for (int i=0; i<ja.size(); i++) {
				JSONObject jo = (JSONObject)ja.get(i);
				String key = (String)jo.get("topic");
				truth.add(key);
			}
		}
		return truth;
	}
	public String getTextWithMarkup(String jdoc) {
		JSONObject jo = (JSONObject)JSONValue.parse(jdoc);
		String text = (String)jo.get(JSON_ELEMENT);
		return text;
	}
	public String getTextWithoutMarkup(String jdoc) {
		JSONObject jo = (JSONObject)JSONValue.parse(jdoc);
		String text = (String)jo.get(JSON_ELEMENT);
		if (text!=null && text.length()>0)
			text = Jsoup.parse(text).text();
		return text;
	}
	@SuppressWarnings("unchecked")
	public JSONObject getRandomDoc() throws IOException {
		int r = (int)(Math.random()*(sourceFiles.size()+1));
		String randomFile = sourceFiles.keySet().toArray(new String[0])[r];
		JSONObject jo = new JSONObject();
		jo.put("title", randomFile);
		jo.put("truth", getTruth4File(randomFile));
		return jo;
	}
	@SuppressWarnings("unchecked")
	public JSONArray getTopicsForCloud() {
		JSONArray ja = new JSONArray();
		JSONObject ca = new JSONObject();
		ca.put("class","cloud-word");
		for (String text : truths.keySet()) {
			JSONObject jo = new JSONObject();
			jo.put("text", text);
			jo.put("weight",truths.get(text));
			jo.put("html", ca);
			ja.add(jo);
		}		
		return ja;
	}
	public String getTopicsFor(String file) throws IOException {
		StringBuilder sb = new StringBuilder();
		ArrayList<String> truth = getTruth4File(file);
		for (String t : truth) {
			sb.append("<li>"+t+"</li>");
		}
		return sb.toString();
	}
	public String getDocText(String file) throws IOException {
		File sourceFile = new File(sourcePath+File.separatorChar+file);
		String json = FileUtils.readFileToString(sourceFile);
		return getTextWithMarkup(json);
	}
	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}
	public void setTruthPath(String truthPath) {
		this.truthPath = truthPath;
	}
}
