package com.aitheras.trainer.dao;

import java.io.IOException;
import java.util.Properties;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aitheras.trainer.source.DocumentSource;
import com.aitheras.trainer.source.JSONFileTopics;
import com.aitheras.trainer.source.JSONFileTruth;
import com.aitheras.trainer.source.SolrSource;
import com.aitheras.trainer.source.TopicSource;
import com.aitheras.trainer.source.TruthSource;
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
	
	private DocumentSource documentSource;
	private TopicSource topicSource;
	private TruthSource truthSource;

	public void init(JSONObject jo) throws IOException {
		setup = new Properties();
		solrUrl=(String)jo.get("solrUrl");
		masterTopicsFile=(String)jo.get("masterTopicsFile");
		affirmativeMessage=(String)jo.get("affirmativeMessage");
		negativeMessage=(String)jo.get("negativeMessage");
		truthPath=(String)jo.get("truthPath");
		name=(String)jo.get("name");
		title=(String)jo.get("title");
		indexPath=(String)jo.get("indexPath");
		dataSource=(String)jo.get("dataSource");
		stopwordsFile=(String)jo.get("stopwordsFile");
		masterTopicsFile=(String)jo.get("masterTopicsFile");
		
		binaryMode=(Boolean)jo.get("binaryMode");
		additive=(Boolean)jo.get("additive");
		
		makeDocumentSource((String)jo.get("documentSource"));
		makeTopicSource((String)jo.get("topicSource"));
		makeTruthSource((String)jo.get("truthSource"));
				
//		logger.debug(this.toString());
	}
	private void makeDocumentSource(String c) throws IOException  {
		try {
			String cl = c.contains(".")?c:"com.aitheras.trainer.source."+c;
			@SuppressWarnings("unchecked")
			Class<DocumentSource> clazz = (Class<DocumentSource>) Class.forName(cl);
			documentSource = clazz.newInstance();
			
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			logger.error("Unable to make an instance of {}. Using SolrSource...",c);
			logger.error(e.getMessage());
			documentSource = new SolrSource();
		}
		documentSource.init(this);
	}
	private void makeTopicSource(String c)  {
		try {
			String cl = c.contains(".")?c:"com.aitheras.trainer.source."+c;
			@SuppressWarnings("unchecked")
			Class<TopicSource> clazz = (Class<TopicSource>) Class.forName(cl);
			topicSource = clazz.newInstance();
			
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			logger.error("Unable to make an instance of {}. Using JSONFileTopics...",c);
			logger.error(e.getMessage());
			topicSource = new JSONFileTopics();
		}
		topicSource.init(this);
	}
	private void makeTruthSource(String c)  {
		try {
			String cl = c.contains(".")?c:"com.aitheras.trainer.source."+c;
			@SuppressWarnings("unchecked")
			Class<TruthSource> clazz = (Class<TruthSource>) Class.forName(cl);
			truthSource = clazz.newInstance();
			
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			logger.error("Unable to make an instance of {}. Using JSONFileTruth...",c);
			logger.error(e.getMessage());
			truthSource = new JSONFileTruth();
		}
		truthSource.init(this);
	}
	
	public String toString() {
		XStream xstream = new XStream(new SunUnsafeReflectionProvider(
				  new FieldDictionary(new ImmutableFieldKeySorter())),
				  new DomDriver("utf-8"));
		return xstream.toXML(this);
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

	public DocumentSource getDocumentSource() {
		return documentSource;
	}

	public void setDocumentSource(DocumentSource documentSource) {
		this.documentSource = documentSource;
	}

	public TopicSource getTopicSource() {
		return topicSource;
	}

	public void setTopicSource(TopicSource topicSource) {
		this.topicSource = topicSource;
	}

	public TruthSource getTruthSource() {
		return truthSource;
	}

	public void setTruthSource(TruthSource truthSource) {
		this.truthSource = truthSource;
	}
}
