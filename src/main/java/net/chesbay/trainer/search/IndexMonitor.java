package net.chesbay.trainer.search;

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

public class IndexMonitor extends Thread {
	private final static Logger logger = LoggerFactory.getLogger(IndexMonitor.class);

	private Indexer indexer;
	private int refreshInterval;
	private Directory indexStore;

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

	public void requestIndexing(String ... requestIDs)  {
		if (requestIDs == null) {
			logger.error("request must not be null");
			throw new IllegalArgumentException("request must not be null");
		}
				for (String requestID : requestIDs) {
					// logger.debug("Adding from list to queue: " + requestID);
				}
		prod();
	}

	/**
	 * init
	 * <p>
	 * Start up the indexer thread
	 * </p>
	 */
	public void init() {
		this.setPriority(Thread.MIN_PRIORITY+2);
		this.start();
		// VERBOSE */ logger.debug("Initializing IndexMonitor timed refresh with refreshInterval = " + refreshInterval);
		Timer timer = new Timer();
		IMTimerTask indexTask = new IMTimerTask();
		timer.scheduleAtFixedRate(indexTask, new Date(), 1000 * 60 * refreshInterval);
		// prod(); // in case there are residual requests on the queue... Now handled on a schedule.
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
			catch (Exception e) {
//				releaseIndexLock();
				logger.error("General Exception: " + e.getMessage());
				logger.debug(ExceptionUtils.getStackTrace(e));
			}
		}
		// logger.debug("Indexer has shutdown!");
	}

	private List<Map<String, String>> getNextIndexIds() {
		return null;
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

	public void setIndexer(Indexer indexer) {
		this.indexer = indexer;
	}

	public void setRefreshInterval(int refreshInterval) {
		this.refreshInterval = refreshInterval;
	}

	public void setIndexStore(String luceneIndexPath) throws IOException {
		this.indexStore = new NIOFSDirectory(Paths.get(luceneIndexPath));

	}

}