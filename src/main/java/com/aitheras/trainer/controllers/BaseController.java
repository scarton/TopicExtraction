package com.aitheras.trainer.controllers;

import java.io.IOException;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.aitheras.trainer.dao.DocumentSource;
import com.aitheras.trainer.dao.TopicSource;
import com.aitheras.trainer.dao.TruthSource;

@Controller
public class BaseController {

	private final static Logger logger = LoggerFactory.getLogger(BaseController.class);
	
	@Autowired
	private DocumentSource source;
	
	@Autowired
	private TruthSource truth;

	@Autowired
	private TopicSource topics;

	private static final String VIEW_INDEX = "index";

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String welcome(ModelMap model) {
		model.put("additive", topics.isAdditive());
		return VIEW_INDEX;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getDoc/{id}", method = RequestMethod.GET)
	@ResponseBody
	public String getDoc(@PathVariable String id) throws IOException {
		logger.debug("BaseController - getDoc end point: {}",id);
		JSONObject jo = new JSONObject();
		jo.put("title", id);
		jo.put("truth", truth.getTruthFor(id));
		return jo.toString();
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getRandomDoc", method = RequestMethod.GET)
	@ResponseBody
	public String getRandomDoc() throws IOException {
		logger.debug("BaseController - getRandomDoc end point");
		String randomId = source.getRandomId();
		JSONObject jo = new JSONObject();
		jo.put("title", randomId);
		jo.put("truth", truth.getTruthFor(randomId));
		return jo.toString();
	}

	@RequestMapping(value = "/getDocText/{id}", method = RequestMethod.GET)
	@ResponseBody
	public String getDocText(@PathVariable String id) throws IOException {
		logger.debug("BaseController - getDocText end point, {}.",id);
		return source.getDocText(id);
	}

	@RequestMapping(value = "/getTruthFor/{id}", method = RequestMethod.GET)
	@ResponseBody
	public String getTruthFor(@PathVariable String id) throws IOException {
		logger.debug("BaseController - getTruthFor end point, {}.",id);
		return truth.getTruthFor(id).toString();
	}

	@RequestMapping(value = "/setTruthFor/{id}", method = RequestMethod.GET)
	@ResponseBody
	public String setTruthFor(@PathVariable String id, String[] topics) throws IOException {
		logger.debug("BaseController - setTruthFor end point, {} - {}",id,topics);
		String res = truth.setTruthFor(id,topics).toString();
		if (this.topics.isAdditive())
			this.topics.updateTopicsWithTruth(topics);
		return res;
	}

	@RequestMapping(value = "/getTopicsForCloud", method = RequestMethod.GET)
	@ResponseBody
	public String getTopicsForCloud() throws IOException {
		logger.debug("BaseController - getTopicsForCloud end point.");
		return topics.getTopicsForCloud().toString();
	}

	public void setSource(DocumentSource source) {
		this.source = source;
	}
}