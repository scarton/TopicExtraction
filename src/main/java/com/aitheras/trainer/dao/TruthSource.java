package com.aitheras.trainer.dao;

import java.io.IOException;

import org.json.simple.JSONArray;

public interface TruthSource {
	public JSONArray getTruthFor(String id) throws IOException;
	public String setTruthFor(String id, String[] topics) throws IOException;
}
