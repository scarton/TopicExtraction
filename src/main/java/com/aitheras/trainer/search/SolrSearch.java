package com.aitheras.trainer.search;

import java.io.IOException;

import org.apache.lucene.queryparser.classic.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aitheras.trainer.dao.Setup;

public class SolrSearch {
	private final static Logger logger = LoggerFactory.getLogger(SolrSearch.class);
	Setup setup;
	
	public void init() throws IOException {
	}

	public void query(String qstr) throws ParseException, IOException {
	}

	public void setSetup(Setup setup) {
		this.setup = setup;
	}
}
