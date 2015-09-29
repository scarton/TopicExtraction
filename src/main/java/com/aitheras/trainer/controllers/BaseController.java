package com.aitheras.trainer.controllers;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.aitheras.trainer.dao.Sources;

@Controller
public class BaseController {

	private final static Logger logger = LoggerFactory.getLogger(BaseController.class);
	
	@Autowired
	private Sources sources;

	private static final String VIEW_INDEX = "index";

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String welcome(ModelMap model) {
		model.put("additive", sources.isAdditive());
		return VIEW_INDEX;
	}

	@RequestMapping(value = "/getDoc/{id}", method = RequestMethod.GET)
	@ResponseBody
	public String getDoc(@PathVariable String id) throws IOException {
		logger.debug("BaseController - getDoc end point: {}",id);
		return sources.getDoc(id).toString();
	}

	@RequestMapping(value = "/getRandomDoc", method = RequestMethod.GET)
	@ResponseBody
	public String getRandomDoc() throws IOException {
		logger.debug("BaseController - getRandomDoc end point");
		return sources.getRandomDoc().toString();
	}

	@RequestMapping(value = "/getDocText/{id}", method = RequestMethod.GET)
	@ResponseBody
	public String getDocText(@PathVariable String id) throws IOException {
		logger.debug("BaseController - getDocText end point, {}.{}",id);
		return sources.getDocText(id);
	}

	@RequestMapping(value = "/getTopicsFor/{id}", method = RequestMethod.GET)
	@ResponseBody
	public String getTopicsFor(@PathVariable String id) throws IOException {
		logger.debug("BaseController - getTopicsFor end point, {}.{}",id);
		return sources.getTopicsFor(id).toString();
	}

	@RequestMapping(value = "/setTopicsFor/{id}", method = RequestMethod.GET)
	@ResponseBody
	public String setTopicsFor(@PathVariable String id, String[] topics) throws IOException {
		logger.debug("BaseController - setTopicsFor end point, {} - {}",id,topics);
		String res = sources.setTopicsFor(id,topics).toString();
		sources.updateTruth(topics);
		return res;
	}

	@RequestMapping(value = "/getTopicsForCloud", method = RequestMethod.GET)
	@ResponseBody
	public String getTopicsForCloud() throws IOException {
		logger.debug("BaseController - getTopicsForCloud end point.");
		return sources.getTopicsForCloud().toString();
	}

	public void setSources(Sources sources) {
		this.sources = sources;
	}
}