package net.c4analytics.trainer.search;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Indexer {
	private final static Logger logger = LoggerFactory.getLogger(Indexer.class);
	private String indexPath;
	private Directory indexStore;

	public void init() throws IOException {
		File indexF = new File(indexPath);
		if (!indexF.exists()) {
			FileUtils.forceMkdir(indexF);
		}
		if (!indexF.exists() || !indexF.isDirectory()) {
			throw new IOException("indexPath must be a directory with appropriate permissions.");
		}
		indexStore = new NIOFSDirectory(Paths.get(indexPath));
		if (!DirectoryReader.indexExists(indexStore)) { // we need to index data. Launch indexing thread in source data.
			;
		}
	}

	public void setIndexPath(String indexPath) {
		this.indexPath = indexPath;
	}

}
