package net.chesbay.trainer.controllers;

import java.io.IOException;

import net.chesbay.trainer.dao.Sources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

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

	@RequestMapping(value = "/getRandomDoc", method = RequestMethod.GET)
	@ResponseBody
	public String getRandomDoc() throws IOException {
		logger.debug("BaseController - getRandomDoc end point");
		return sources.getRandomDoc().toString();
	}

	@RequestMapping(value = "/getDocText/{name}.{type}", method = RequestMethod.GET)
	@ResponseBody
	public String getDocText(@PathVariable String name, @PathVariable String type) throws IOException {
		logger.debug("BaseController - getDocText end point, {}.{}",name,type);
		return sources.getDocText(name+"."+type);
	}

	@RequestMapping(value = "/getTopicsFor/{name}.{type}", method = RequestMethod.GET)
	@ResponseBody
	public String getTopicsFor(@PathVariable String name, @PathVariable String type) throws IOException {
		logger.debug("BaseController - getTopicsFor end point, {}.{}",name,type);
		return sources.getTopicsFor(name+"."+type).toString();
	}

	@RequestMapping(value = "/setTopicsFor/{name}", method = RequestMethod.GET)
	@ResponseBody
	public String setTopicsFor(@PathVariable String name, String[] topics) throws IOException {
		logger.debug("BaseController - getTopicsFor end point, {} - {}",name,topics);
		return sources.setTopicsFor(name,topics).toString();
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