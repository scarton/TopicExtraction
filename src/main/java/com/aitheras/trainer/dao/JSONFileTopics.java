package com.aitheras.trainer.dao;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JSONFileTopics implements TopicSource{
	final static Logger logger = LoggerFactory.getLogger(JSONFileTopics.class);
	private Setup setup;
	private static final String TRUTH_EXT=".truth";
	private Map<String, Long> topics = new TreeMap<String, Long>();
	
	public void init() throws IOException {
		if (!setup.isBinaryMode())
			buildCloudTopics();
	}
	
	private void buildCloudTopics() throws IOException {
		collectTruthTopics4Id(setup.getMasterTopicsFile()); // collect all topics from the master topics collection
		if (setup.isAdditive()) { //aggregate topics from the specific key files.
			collectTruthTopics();
		}
	}
	private void collectTruthTopics() throws IOException {
		File truthF = new File(setup.getTruthPath());
		FileFilter fileFilter = new WildcardFileFilter("*"+TRUTH_EXT);
		File[] listOfFiles = truthF.listFiles(fileFilter);
		logger.debug("Number of truth files: {}",listOfFiles.length);
		for (int i = 0; i<listOfFiles.length; i++) {
			collectTruthTopics4Id(listOfFiles[i].getPath());
		}
		logger.debug("Aggregated truths: {}",topics);
	}
	private void collectTruthTopics4Id(String id) throws IOException {
		File topicsFile = new File(id);
		if (topicsFile.isFile()) {
			String truthJson = FileUtils.readFileToString(topicsFile);
			JSONArray ja = (JSONArray)JSONValue.parse(truthJson);
			logger.debug("Truth file {}: {}",topicsFile.getName(),ja.toString());
			for (int c=0; c<ja.size(); c++) {
				JSONObject jo = (JSONObject)ja.get(c);
				String key = (String)jo.get("topic");
				long weight = jo.get("weight")!=null?(Long)jo.get("weight"):1;
				if (topics.containsKey(key)) {
					topics.put(key, topics.get(key)+weight);
				} else {
					topics.put(key, weight);
				}
			}
		}
	}
	public void updateTopicsWithTruth(String[] topics) {
		if (setup.isAdditive()) {
			long weight = 1;
			for (String key : topics) {
				if (this.topics.containsKey(key)) {
					logger.debug("Updating weight in cloud topic {}",key);
					this.topics.put(key, this.topics.get(key)+weight);
				} else {
					logger.debug("Adding cloud topic {}",key);
					this.topics.put(key, weight);
				}
			}
		}
	}
	@SuppressWarnings("unchecked")
	public JSONArray getTopicsForCloud() throws IOException {
		JSONArray ja = new JSONArray();
		for (String text : topics.keySet()) {
			JSONObject jo = new JSONObject();
			jo.put("text", text);
			jo.put("weight",topics.get(text));
			ja.add(jo);
		}		
		return ja;
	}
	public void setSetup(Setup setup) {
		this.setup = setup;
	}
}
