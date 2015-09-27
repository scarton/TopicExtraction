package net.chesbay.search;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IndexMonitor extends IndexQueue {
	private final static Logger logger = LoggerFactory.getLogger(IndexMonitor.class);

	private Index indexer;
	private int refreshInterval;
	private Directory indexStore;

	private String luceneIndexPath; // injected

	private final BlockingQueue<String> requestQueue = new LinkedBlockingQueue<String>();
	private volatile boolean shutdown;

	class IMTimerTask extends TimerTask {
		public void run() {
			 logger.debug("===> IMTimerTask: Prodding indexer()");
			prod();
		}
	}

	/**
	 * Register this thread
	 */
	public IndexMonitor() {
		super("IndexMonitor");
		this.shutdown = false;
	}

	/**
	 * releaseIndexLock
	 * 
	 * Delete the Lucene "write.lock" file in the index directory.
	 * 
	 */
	public void releaseIndexLock() {
		File lockFile = new File(luceneIndexPath + "/write.lock");
		try {
			if (lockFile.exists()) {
				if (lockFile.delete()) {
					logger.error("releaseIndexLock: Index Lock file deleted.");
				}
				else {
					logger.error("releaseIndexLock: Found Index Lock file, but could not delete");
				}
			}
		}
		catch (Exception e) {
			logger.error("releaseIndexLock: Error trying to delete Lucene index lock.");
		}
	}

	/**
	 * init
	 * <p>
	 * Start up the indexer thread
	 * </p>
	 */
	public void init() {
		// VERBOSE */ logger.debug("Initializing index monitor...");
		try {
			if (indexer.need2Reindex()) { // if we need to reindex, pull all the topic IDs and place in the queue
				logger.debug("Resources need re-indexing, setting up request.");
				List<String> ids = dataUtil.getAllResourceIDs();
				logger.debug("Placing "+ids.size()+" resource id's into the queue.");
				// queue up all ids as if they are new so the indexer will not attempt to unindex any of them.
				requestNewIndexing(backgroundUserGuid, ids, "N", IndexQueue.Type.low);
			}
			else {
				// logger.debug("No resources need re-indexing, optimize.");
				indexer.optimize();
			}
			this.setPriority(Thread.MIN_PRIORITY+2);
			this.start();
			// VERBOSE */ logger.debug("Initializing IndexMonitor timed refresh with refreshInterval = " + refreshInterval);
			Timer timer = new Timer();
			IMTimerTask indexTask = new IMTimerTask();
			timer.scheduleAtFixedRate(indexTask, new Date(), 1000 * 60 * refreshInterval);
			// prod(); // in case there are residual requests on the queue... Now handled on a schedule.
		}
		catch (IndexException e) {
			releaseIndexLock();
			logger.error("Index Monitor could not start.");
			logger.error(e.getMessage());
			// logger.debug(StringH.StackTrace(e));
		}
	}

	/**
	 * run - Waits for a request to perform some indexing. Reads the first entry by priority from the indexer queue in
	 * the DB, indexes that resource (which also deletes it from the DB queue). Looks for children of this resource and
	 * adds them to the indexer queue.
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		while (!this.shutdown) {
			String request = null;
			try {
				logger.debug("Indexer is waiting for a request...");
				request = this.requestQueue.take(); // Wait for a request
				logger.debug("Indexer got a request!" + ((request == null) ? "  BUT it was null so we will ignore it." : ""));

				if (request != null) { // request is null if we were interrupted
					this.requestQueue.clear(); // This empties the waitqueue -- which should already be empty.
					// -----------------------------------------------------
					// fetch a block of up to 1000 ids to index from the DB
					// -----------------------------------------------------
					List<Map<String, String>> requestIDs = getNextIndexIds();
					logger.debug("Processing " + requestIDs.size() + " request ID's");

					if (requestIDs.size() > 0) {
						logger.debug("Indexer completed indexing of current subset of queue!");
					}
					// ------------------------------------------------------------------------
					// the requestIDS list size corresponds to the value hard-wired value in
					// psGETNEXTINDEXIDS statement (sql.mysql.xml). Both MUST be kept in sync.
					// -------------------------------------------------------------------------
					if (requestIDs.size() == 1000) // we may need to go round again...
						prod(); // in case there are more...
				}
			}
			catch (InterruptedException e) { // Ignore
				logger.warn("Indexer interrupted");
			}
			catch (IndexException e) {
				releaseIndexLock();
				logger.error("IndexException: " + e.getMessage());
				// logger.debug(StringH.StackTrace(e));
			}
			catch (Exception e) {
				releaseIndexLock();
				logger.error("General Exception: " + e.getMessage());
				logger.debug(ExceptionUtils.getStackTrace(e));
			}
		}
		// logger.debug("Indexer has shutdown!");
	}

	private List<Map<String, String>> getNextIndexIds() {
		return (List<Map<String, String>>) jdbcTemplate.queryForList(SQLP.get("IndexMonitor.psGETNEXTINDEXIDS"));
	}

	/**
	 * shutdown
	 * <p>
	 * terminate the indexer thread
	 * </p>
	 * 
	 * @throws InterruptedException
	 */
	public void shutdown() throws InterruptedException {
		// logger.debug("Indexer is shutting down...");
		this.shutdown = true;
		this.interrupt();
		this.join();
	}

	public void waitAndShutdown() throws InterruptedException {
		// logger.debug("Indexer is waiting and the shutting down...");
		this.shutdown = true;
		this.join();
	}

	public void waitNoShutdown() throws InterruptedException {
		// logger.debug("Indexer is waiting...");
		this.join();
	}

	/**
	 * prod
	 * <p>
	 * triggers the indexing process to look for work to do in the queue. This would be called from an admin function
	 * after a crash.
	 * </p>
	 */
	public void prod() {
		// logger.debug("Prodding indexer into running");
		this.requestQueue.add("IndexingToDo");// this is simply to let the indexer start -- the actual IDs are in the DB
	}

	public void setIndexer(Index indexer) {
		this.indexer = indexer;
	}

	public void setRefreshInterval(int refreshInterval) {
		this.refreshInterval = refreshInterval;
	}

	public void setIndexStore(String luceneIndexPath) throws IOException {
		this.indexStore = new NIOFSDirectory(Paths.get(luceneIndexPath));

	}

}