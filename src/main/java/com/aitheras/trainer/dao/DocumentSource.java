package com.aitheras.trainer.dao;

import java.io.IOException;

/**
 * General contract for getting document information from a source of data.
 * @author Steve Carton, stephen.carton@aitheras.com
 *
 */
public interface DocumentSource {

	public void init() throws IOException;
	public long maxDocs() throws IOException;
	public String getDocText(String id) throws IOException;
	public String getCleanDocText(String id) throws IOException;
	public String getRandomId() throws IOException;
	public void close() throws IOException;
}
