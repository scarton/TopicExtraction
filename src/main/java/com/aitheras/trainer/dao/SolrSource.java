package com.aitheras.trainer.dao;

import java.io.IOException;
import java.util.Random;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.TextFormat.ParseException;


/**
 * Provides data for the GUI - random or searched doc text.
 * @TODO refactor sources to use an interface to a specific data service.
 * @author Steve Carton, stephen.carton@aitheras.com
 *
 */
public class SolrSource implements DocumentSource {
	final static Logger logger = LoggerFactory.getLogger(SolrSource.class);
	private Setup setup;
	private HttpSolrClient solr;
	
	public long maxDocs() throws IOException {
	    SolrQuery q = new SolrQuery("*:*");
	    q.setRows(0);  // don't actually request any data
	    try {
			return solr.query(q).getResults().getNumFound();
		} catch (SolrServerException e) {
			throw new IOException(e.getMessage());
		}	
	}
	public String getDocTitle(String guid) throws IOException {
		SolrQuery query = new SolrQuery("guid:"+guid);
		logger.debug("getting doc title for {}",guid);
		query.setStart(0);
		query.setRows(1);
		
		QueryResponse response;
		try {
			response = solr.query(query);
		} catch (SolrServerException e) {
			throw new IOException(e.getMessage());
		}
		SolrDocumentList results = response.getResults();
		SolrDocument d = results.get(0);
		return (String)d.getFieldValue("docid");
	}
	public String getRandomId() throws IOException {
		int docNo = (int)(new Random().nextDouble() * maxDocs());
		SolrQuery query = new SolrQuery("*:*");
		logger.debug("Looking for random doc {}",docNo);
		query.setStart(docNo);
		query.setRows(1);
		
		QueryResponse response;
		try {
			response = solr.query(query);
		} catch (SolrServerException e) {
			throw new IOException(e.getMessage());
		}
		SolrDocumentList results = response.getResults();
		if (results.size()==1) {
			SolrDocument d = results.get(0);
			String randomId = (String)d.getFieldValue("guid");
			logger.debug("Random doc ID {}",randomId);
			return randomId;
		} else 
			throw new IOException("Query for random doc "+docNo+" has "+results.getNumFound()+" results.");
	}
	public String getDocText(String guid) throws IOException {
		SolrDocument doc;
		try {
			doc = docQuery(guid);
		} catch (ParseException | SolrServerException e) {
			throw new IOException(e.getMessage());
		}
		String text = (String)doc.getFieldValue("doctext");
		text = text.replaceAll("[\\r\\n]+", "<p/>");
		return text;
	}
	public String getCleanDocText(String guid) throws IOException {
		return getDocText(guid);
	}
	public SolrDocument docQuery(String guid) throws ParseException, IOException, SolrServerException {
		SolrQuery query = new SolrQuery("guid:"+guid);
		QueryResponse response = solr.query(query);
		SolrDocumentList results = response.getResults();
		if (results.getNumFound()==1) {
			SolrDocument d = results.get(0);
		    return d;
		} else throw new IOException("Query for "+guid+" has "+results.getNumFound()+" results.");
	}
	@Override
	public void init() {
	}
	@Override
	public void close() throws IOException {
		solr.close();
	}
	public void setSetup(Setup setup) {
		this.setup=setup;
		this.solr = new HttpSolrClient(setup.getSolrUrl());
	}
}
