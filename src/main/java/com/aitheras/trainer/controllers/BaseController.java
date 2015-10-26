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
import com.aitheras.trainer.dao.Setup;
import com.aitheras.trainer.dao.TopicSource;
import com.aitheras.trainer.dao.TruthSource;

@Controller
public class BaseController {

	private final static Logger logger = LoggerFactory.getLogger(BaseController.class);
	
	@Autowired
	private Setup setup;
	
	@Autowired
	private DocumentSource source;
	
	@Autowired
	private TruthSource truth;

	@Autowired
	private TopicSource topics;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index(ModelMap model) {
		logger.debug("BaseController - main end point, Binary? {}",setup.isBinaryMode());
		model.put("name", setup.getName());
		model.put("title", setup.getTitle());
		model.put("additive", setup.isAdditive());
		model.put("binary", setup.isBinaryMode());
		if (setup.isBinaryMode()) {
			model.put("affirmativeMessage",setup.getAffirmativeMessage());
			model.put("negativeMessage", setup.getNegativeMessage());
			return "binary";
		} else {
			return "topical";
		}
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

	@RequestMapping(value = "/getTagFor/{id}", method = RequestMethod.GET)
	@ResponseBody
	public String getTagFor(@PathVariable String id) throws IOException {
		logger.debug("BaseController - getTruthFor end point, {}.",id);
		return truth.getTagFor(id).toString();
	}

	@RequestMapping(value = "/setTruthFor/{id}", method = RequestMethod.GET)
	@ResponseBody
	public String setTruthFor(@PathVariable String id, String[] topics) throws IOException {
		logger.debug("BaseController - setTruthFor end point, {} - {}",id,topics);
		String res = truth.setTruthFor(id,topics).toString();
		if (this.setup.isAdditive())
			this.topics.updateTopicsWithTruth(topics);
		return res;
	}

	@RequestMapping(value = "/setTagFor/{id}", method = RequestMethod.GET)
	@ResponseBody
	public String setTagFor(@PathVariable String id, String tag, String reason) throws IOException {
		logger.debug("BaseController - setTagFor end point, {} - {} {}",id,tag, reason);
		String res = truth.setTagFor(id,tag,reason).toString();
		if (this.setup.isAdditive())
			this.topics.updateTopicsWithTruth(tag);
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