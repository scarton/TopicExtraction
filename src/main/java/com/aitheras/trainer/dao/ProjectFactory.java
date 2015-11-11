package com.aitheras.trainer.dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aitheras.trainer.util.Util;
import com.google.common.cache.Cache;

/**
 * @author Steve Carton, stephen.carton@aitheras.com
 */
public class ProjectFactory {
	final static Logger logger = LoggerFactory.getLogger(ProjectFactory.class);
	private static String projectsFile="projects.json";
	private JSONArray projects;
	private Cache<String, Setup> cache;
	

	
	/**
	 * Init method called by Spring. The projectsFile field can work a couple of ways:
	 * - It can be injected as a path/file on the file system (prefered).
	 * - it can be defined as a System property which supplies a file-system path to an projects json file
	 * - it can be a classpath resource json file.
	 * @throws IOException
	 * @throws ParseException 
	 */
	public void init() throws IOException, ParseException {
		File pf = new File(projectsFile);
		InputStream in;
		String projFile = System.getProperty(projectsFile);
		if (pf.exists() && pf.isFile()) {
			logger.debug("Loading projects from {}",projectsFile);
			in = new FileInputStream(projectsFile);
		} else if (projFile!=null) {
			logger.debug("Loading projects from {}",projFile);
			in = new FileInputStream(projFile);
		} else if (this.getClass().getResource("/"+projectsFile)!=null) {
			logger.debug("Loading projects from classpath resource {}",projectsFile);
			in = this.getClass().getResourceAsStream("/"+projectsFile);	
		} else {
			logger.error("Cannot find projects resource {}",projectsFile);
			throw new IOException("Cannot find projects resource '"+projectsFile+"'");
		}
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		projects = (JSONArray)new JSONParser().parse(reader);
		logger.debug("Projects and settings: {}",projects.toJSONString());
		in.close();
	}
	public Setup getSetup(String project) {
		Setup setup = cache.getIfPresent(project);
		if (setup==null) {
			setup=makeSetup(project);
			cache.put(project, setup);
		}
		return setup;
	}
	public Setup makeSetup(String project) {
		Setup setup = new Setup();
		try {
			for (int i=0; i<projects.size(); i++) {
				JSONObject jo = (JSONObject)projects.get(i);
				if (project.equals(jo.get("project"))) {
						setup.init(jo);
						break;
				}
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
			logger.debug(Util.stackTrace(e));
		}
		return setup;
	}
	public String getWorkspaceList() {
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<projects.size(); i++) {
			JSONObject jo = (JSONObject)projects.get(i);
			sb.append("<li><a href='#' class='workspace-link' id='"+jo.get("project")+"'>"+jo.get("name")+": "+jo.get("title")+"</a></li>\n");
		}
		return sb.toString();
	}

	public static void setProjects(String projects) {
		ProjectFactory.projectsFile = projects;
	}
	public void setCache(Cache<String, Setup> cache) {
		this.cache = cache;
	}
}
