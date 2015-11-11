package com.aitheras.trainer.source;

import java.io.IOException;

import org.json.simple.JSONArray;

import com.aitheras.trainer.dao.Setup;

public interface TruthSource {
	public void init(Setup setup);
	public JSONArray getTruthFor(String id) throws IOException;
	public String setTruthFor(String id, String... topics) throws IOException;
	public JSONArray getTagFor(String id) throws IOException;
	public String setTagFor(String id, String tag, String reason) throws IOException;
}
