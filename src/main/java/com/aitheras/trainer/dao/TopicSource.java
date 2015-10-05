package com.aitheras.trainer.dao;

import java.io.IOException;

import org.json.simple.JSONArray;

public interface TopicSource {
	public JSONArray getTopicsForCloud() throws IOException;
	public void updateTopicsWithTruth(String[] topics);
	public boolean isAdditive();
}
