package com.aitheras.trainer.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LFUCache {
	final static Logger logger = LoggerFactory.getLogger(LFUCache.class);
	private Map<String, Object> cache;
	private LinkedList<String> mfulist;
	private int cachesize;

	public LFUCache() {
		cache = new HashMap<String, Object>();
		mfulist = new LinkedList<String>();
	}

	public final Object get(String key) {
		// move the key to the front and return the object
		if (this.cachesize > 0) {
			synchronized (cache) {
				if (cached(key)) {
					mfulist.remove(key);
					mfulist.addFirst(key);
					return cache.get(key);
				}
			}
		}
		return null;
	}

	public final boolean cached(String key) {
		return cache.containsKey(key);
	}

	public final void put(String key, Object obj) {
		if (this.cachesize > 0) {
			synchronized (cache) {
				// is the cache full? pop off the bottom
				if (mfulist.size() == this.cachesize) {
					cache.remove(mfulist.getLast());
					mfulist.removeLast();
				}
				// Add the object and the key at the front
				cache.put(key, obj);
				mfulist.addFirst(key);
			}
		}
	}

	public final String toString() {
		String ts = "Cache Size: " + this.cachesize + " mfuList Size: " + mfulist.size() + "\n";
		if (this.cachesize > 0 && mfulist != null) {
			for (int i = 0; i < mfulist.size(); i++) {
				ts += "item " + i + "=" + (mfulist.get(i) == null ? "null" : mfulist.get(i)) + " Class: "
						+ cache.get(mfulist.get(i)).getClass() + "\n";
			}
		}
		return ts;
	}

	public void setCachesize(int cachesize) {
		this.cachesize = cachesize;
	}

}
