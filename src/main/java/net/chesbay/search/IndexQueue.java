package net.chesbay.search;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IndexQueue extends Thread {
	private final static Logger logger = LoggerFactory.getLogger(IndexQueue.class);

	
	public IndexQueue() {}
	
	public IndexQueue(String threadName) {
		super(threadName);
	}
	
	public void prod() {
		// logger.debug("Prodding nothing -- IndexQueue instance not IndexMonitor.");
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

}
