package com.aitheras.trainer.batch;

import java.io.IOException;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SolrIndexer {
	final static Logger logger = LoggerFactory.getLogger(SolrIndexer.class);
	private HttpSolrClient server;
	public SolrIndexer(String baseURL) {
		server = new HttpSolrClient(baseURL);
	}
	public void index(String text, String id, String guid) throws IOException, SolrServerException {
		index(text,id, guid, null, null);
	}
	public void index(String text, String id, String guid, String cleanText, List<String> terms) throws SolrServerException, IOException {
		SolrInputDocument  doc = new SolrInputDocument ();
		doc.addField("doctext", text);
		doc.addField("cleantext", cleanText);
		doc.addField("docid", id);
		doc.addField("guid", guid);
		if (terms!=null) {
			doc.addField("_terms_", terms);
//			logger.debug("{}",Joiner.on(", ").join(terms));
		}
		server.add(doc);
	}
	public void eraseIndex() throws SolrServerException, IOException {
		deleteByQuery("*:*");
	}
	public void deleteByQuery(String q) throws SolrServerException, IOException {
		server.deleteByQuery(q);
	}
	public void testQuery(String qstr) throws IOException, SolrServerException {
	    SolrQuery query = new SolrQuery();
	    query.setQuery(qstr);
//	    query.addFilterQuery("cat:electronics","store:amazon.com");
//	    query.setFields("id","price","merchant","cat","store");
	    query.setStart(0);    
	    query.set("defType", "edismax");

	    QueryResponse response = server.query(query);
	    SolrDocumentList results = response.getResults();
	    for (int i = 0; i < results.size(); ++i) {
	      System.out.println(results.get(i));
	    }
    }
	public void commit() throws SolrServerException, IOException {
		server.commit();
	}
	public void close() throws IOException {
		server.close();
	}
}
