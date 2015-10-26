package com.aitheras.trainer.dao;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JSONFileTruth implements TruthSource {
	final static Logger logger = LoggerFactory.getLogger(JSONFileTruth.class);
	private Setup setup;
	private static final String TRUTH_EXT=".truth";

	private List<String> getTruthTopics4Id(String id) throws IOException {
		String rfn = FilenameUtils.removeExtension(id);
		File truthF = new File(setup.getTruthPath()+File.separatorChar+rfn+TRUTH_EXT);
		List<String> truth = new ArrayList<String>();
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
	private JSONArray getTruthTag4Id(String id) throws IOException {
		String rfn = FilenameUtils.removeExtension(id);
		File truthF = new File(setup.getTruthPath()+File.separatorChar+rfn+TRUTH_EXT);
		JSONArray ja = new JSONArray();
		if (truthF.isFile()) {
			String truthJson = FileUtils.readFileToString(truthF);
			ja = (JSONArray)JSONValue.parse(truthJson);
		}
		return ja;
	}
	@SuppressWarnings("unchecked")
	public JSONArray getTruthFor(String id) throws IOException {
		JSONArray ja = new JSONArray();
		List<String> truth = getTruthTopics4Id(id);
		for (String t : truth) {
			ja.add(t);
		}
		return ja;
	}
	public JSONArray getTagFor(String id) throws IOException {
		return getTruthTag4Id(id);
	}
	@SuppressWarnings("unchecked")
	public String setTruthFor(String id, String... topics) throws IOException {
		File truthF = new File(setup.getTruthPath()+File.separatorChar+id+TRUTH_EXT);
		JSONArray ja = new JSONArray();
		for (String t : topics) {
			JSONObject jo = new JSONObject();
			jo.put("topic", t);
			jo.put("weight", 1);
			ja.add(jo);
		}
		FileUtils.writeStringToFile(truthF, ja.toString());
		return id+" topics saved.";
	}
	@SuppressWarnings("unchecked")
	public String setTagFor(String id, String tag, String reason) throws IOException {
		File truthF = new File(setup.getTruthPath()+File.separatorChar+id+TRUTH_EXT);
		JSONArray ja = new JSONArray();
		JSONObject jo = new JSONObject();
		jo.put("tag", tag);
		jo.put("reason", reason);
		jo.put("weight", 1);
		ja.add(jo);
		FileUtils.writeStringToFile(truthF, ja.toString());
		return id+" tag/reason saved.";
	}
	public void setSetup(Setup setup) {
		this.setup = setup;
	}
}
