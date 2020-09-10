package net.c4analytics.trainer.controllers;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

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

import net.c4analytics.trainer.dao.ProjectFactory;
import net.c4analytics.trainer.dao.Setup;
import net.c4analytics.trainer.source.DocumentSource;
import net.c4analytics.trainer.source.TopicSource;
import net.c4analytics.trainer.source.TruthSource;

@Controller
public class BaseController {

	private final static Logger logger = LoggerFactory.getLogger(BaseController.class);
	
	@Autowired
	private ProjectFactory projectFactory;

	private DocumentSource source;
	private TruthSource truth;
	private TopicSource topics;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index(ModelMap model) {
		logger.debug("BaseController - workspace selection end point");
		return "index";
	}

	@RequestMapping(value = "/getWorkspaces", method = RequestMethod.GET)
	@ResponseBody
	public String getWorkspaces(ModelMap model) {
		logger.debug("BaseController - get workspaces end point");
		return projectFactory.getWorkspaceList();
	}

	private Setup getSetup(String project) {
		Setup setup = projectFactory.getSetup(project);
		this.source = setup.getDocumentSource();
		this.truth = setup.getTruthSource();
		this.topics = setup.getTopicSource();
		return setup;
	}

	@RequestMapping(value = "/{project}", method = RequestMethod.GET)
	public String load(@PathVariable String project, ModelMap model, HttpServletRequest request) {
		logger.debug("BaseController - main end point, WS: {}",project);
		Setup setup = getSetup(project);
		logger.debug("Binary? {}",setup.isBinaryMode());
		model.put("project", project);
		model.put("name", setup.getName());
		model.put("title", setup.getTitle());
		model.put("additive", setup.isAdditive());
		model.put("binary", setup.isBinaryMode());
		model.put("root", request.getContextPath());
		if (setup.isBinaryMode()) {
			model.put("affirmativeMessage",setup.getAffirmativeMessage());
			model.put("negativeMessage", setup.getNegativeMessage());
			return "binary";
		} else {
			return "topical";
		}
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/{project}/getDoc/{id}", method = RequestMethod.GET)
	@ResponseBody
	public String getDoc(@PathVariable String project, @PathVariable String id) throws IOException {
		logger.debug("BaseController - getDoc end point: {}",id);
		getSetup(project);
		JSONObject jo = new JSONObject();
		jo.put("title", source.getDocTitle(id));
		jo.put("guid", id);
		return jo.toString();
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/{project}/getRandomDoc", method = RequestMethod.GET)
	@ResponseBody
	public String getRandomDoc(@PathVariable String project) throws IOException {
		logger.debug("BaseController - getRandomDoc end point");
		getSetup(project);
		String randomId = source.getRandomId();
		JSONObject jo = new JSONObject();
		jo.put("guid", randomId);
		jo.put("title", source.getDocTitle(randomId));
//		jo.put("truth", truth.getTruthFor(randomId));
		return jo.toString();
	}

	@RequestMapping(value = "/{project}/getDocText/{id}", method = RequestMethod.GET)
	@ResponseBody
	public String getDocText(@PathVariable String project, @PathVariable String id) throws IOException {
		logger.debug("BaseController - getDocText end point, {}.",id);
		getSetup(project);
		return source.getDocText(id);
	}

	@RequestMapping(value = "/{project}/getTruthFor/{id}", method = RequestMethod.GET)
	@ResponseBody
	public String getTruthFor(@PathVariable String project, @PathVariable String id) throws IOException {
		logger.debug("BaseController - getTruthFor end point, {}.",id);
		getSetup(project);
		return truth.getTruthFor(id).toString();
	}

	@RequestMapping(value = "/{project}/getTagFor/{id}", method = RequestMethod.GET)
	@ResponseBody
	public String getTagFor(@PathVariable String project, @PathVariable String id) throws IOException {
		logger.debug("BaseController - getTruthFor end point, {}.",id);
		getSetup(project);
		return truth.getTagFor(id).toString();
	}

	@RequestMapping(value = "/{project}/setTruthFor/{id}", method = RequestMethod.GET)
	@ResponseBody
	public String setTruthFor(@PathVariable String project, @PathVariable String id, String[] topics) throws IOException {
		logger.debug("BaseController - setTruthFor end point, {} - {}",id,topics);
		Setup setup = getSetup(project);
		String res = truth.setTruthFor(id,topics).toString();
		if (setup.isAdditive())
			this.topics.updateTopicsWithTruth(topics);
		return res;
	}

	@RequestMapping(value = "/{project}/setTagFor/{id}", method = RequestMethod.GET)
	@ResponseBody
	public String setTagFor(@PathVariable String project, @PathVariable String id, String tag, String reason) throws IOException {
		logger.debug("BaseController - setTagFor end point, {} - {} {}",id,tag, reason);
		Setup setup = getSetup(project);
		String res = truth.setTagFor(id,tag,reason).toString();
		if (setup.isAdditive())
			this.topics.updateTopicsWithTruth(tag);
		return res;
	}

	@RequestMapping(value = "/{project}/getTopicsForCloud", method = RequestMethod.GET)
	@ResponseBody
	public String getTopicsForCloud(@PathVariable String project) throws IOException {
		logger.debug("BaseController - getTopicsForCloud end point.");
		getSetup(project);
		return topics.getTopicsForCloud().toString();
	}
}