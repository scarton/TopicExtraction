package com.aitheras.trainer.source;

import java.io.IOException;

import com.aitheras.trainer.dao.Setup;

/**
 * General contract for getting document information from a source of data.
 * @author Steve Carton, stephen.carton@aitheras.com
 *
 */
public interface DocumentSource {

	public void init(Setup setup) throws IOException;
	public long maxDocs() throws IOException;
	public String getDocText(String id) throws IOException;
	public String getDocTitle(String id) throws IOException;
	public String getCleanDocText(String id) throws IOException;
	public String getRandomId() throws IOException;
	public void close() throws IOException;
}
