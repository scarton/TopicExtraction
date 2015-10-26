package com.aitheras.trainer.dao;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.FieldDictionary;
import com.thoughtworks.xstream.converters.reflection.ImmutableFieldKeySorter;
import com.thoughtworks.xstream.converters.reflection.SunUnsafeReflectionProvider;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Sets up the training application. Looks for a trainer.props file either as a system variable (-Dtrainer.props=xxx) or on the classpath.
 * this provides various paths and settings needed for training a particular model, like 
 * <ul>
 * <li>The URL of the Solr instance for the data content.
 * <li>Binary or N-ary classification mode, </li>
 * <li>Location of master topics (for N-Ary)</li>
 * <li>Labels for affirmative or negative responses for binary classifier.</li>
 * <li>Path for writing out truth sets.</li>
 * </ul>
 * @author Steve Carton, stephen.carton@aitheras.com
 *
 */
public class Setup {
	final static Logger logger = LoggerFactory.getLogger(Setup.class);
	private static final String PROPNAME="trainer.props";
	private Properties setup;
	private String name;
	private String title;
	private String solrUrl;
	private String indexPath;
	private String masterTopicsFile;
	private String affirmativeMessage;
	private String negativeMessage;
	private String truthPath;
	private String dataSource;
	private String stopwordsFile;
	private boolean binaryMode=false;
	private boolean additive=true;

	
	public void init() throws IOException {
		setup = new Properties();
		String propFile = System.getProperty(PROPNAME);
		InputStream in;
		if (propFile!=null) {
			logger.debug("Loading setup from {}",propFile);
			in = new FileInputStream(propFile);
		} else {
			logger.debug("Loading setup from classpath resource {}",PROPNAME);
			in = this.getClass().getResourceAsStream("/"+PROPNAME);	
		}
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		setup.load(reader);
		solrUrl=initializeProperty("solrUrl");
		masterTopicsFile=initializeProperty("masterTopicsFile");
		affirmativeMessage=initializeProperty("affirmativeMessage");
		negativeMessage=initializeProperty("negativeMessage");
		truthPath=initializeProperty("truthPath");
		name=initializeProperty("name");
		title=initializeProperty("title");
		indexPath=initializeProperty("indexPath");
		dataSource=initializeProperty("dataSource");
		stopwordsFile=initializeProperty("stopwordsFile");
		binaryMode=initializeBooleanProperty("binaryMode");
		additive=initializeBooleanProperty("additive");
//		logger.debug(this.toString());
	}
	public String toString() {
		XStream xstream = new XStream(new SunUnsafeReflectionProvider(
				  new FieldDictionary(new ImmutableFieldKeySorter())),
				  new DomDriver("utf-8"));
		return xstream.toXML(this);
	}
	private String initializeProperty(String name){
		String r = null;
		if (has(name)) {
			r = get(name);
		} else {
			logger.info(name + " not present in setup properties. Is that correct?");
		}
		return r;
	}
	private Boolean initializeBooleanProperty(String name){
		Boolean r = false;
		if (has(name)) {
			r = Boolean.parseBoolean(get(name));
		} else {
			logger.info(name + " not present in setup properties. Is that correct?");
		}
		return r;
	}
	public String get(String prop, String dflt) {
		return setup.getProperty(prop, dflt);
	}
	public String get(String prop) {
		return setup.getProperty(prop);
	}
	public boolean has(String prop) {
		return setup.getProperty(prop)!=null;
	}
	public String getSolrUrl() {
		return solrUrl;
	}
	public boolean isBinaryMode() {
		return binaryMode;
	}
	public String getMasterTopicsFile() {
		return masterTopicsFile;
	}
	public String getAffirmativeMessage() {
		return affirmativeMessage;
	}
	public String getNegativeMessage() {
		return negativeMessage;
	}
	public String getTruthPath() {
		return truthPath;
	}
	public String getIndexPath() {
		return indexPath;
	}
	public String getDataSource() {
		return dataSource;
	}
	public boolean isAdditive() {
		return additive;
	}
	public String getName() {
		return name;
	}
	public String getTitle() {
		return title;
	}
	public String getStopwordsFile() {
		return stopwordsFile;
	}
}
