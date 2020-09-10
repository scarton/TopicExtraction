package net.c4analytics.trainer.source;

import java.io.IOException;

import org.json.simple.JSONArray;

import net.c4analytics.trainer.dao.Setup;

public interface TopicSource {
	public void init(Setup setup);
	public JSONArray getTopicsForCloud() throws IOException;
	public void updateTopicsWithTruth(String... topics);
}
