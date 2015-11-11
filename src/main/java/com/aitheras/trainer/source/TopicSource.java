package com.aitheras.trainer.source;

import java.io.IOException;

import org.json.simple.JSONArray;

import com.aitheras.trainer.dao.Setup;

public interface TopicSource {
	public void init(Setup setup);
	public JSONArray getTopicsForCloud() throws IOException;
	public void updateTopicsWithTruth(String... topics);
}
