package net.c4analytics.nlp;

import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class Sentences {
	private LoadingCache<String, Integer> cache=null;
	public Sentences() {
		
	}
	public void makeCache() {
		cache = CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(1000)
                .build(new CacheLoader<String, Integer>() {
                    @Override
                    public Integer load(String sentHash) throws Exception {
                        return 0;
                    }
                });
	}
	public static void main(String[] args) throws ExecutionException {
		Sentences sent = new Sentences();
		sent.makeCache();
		String s = "The quick brown dog jumps over the lazy dog";
		String[] tks = s.split(" ");
		Random rand = new Random();
		
		int l = tks.length;
		for (int i=0; i<10000; i++) {
			int r = rand.nextInt(l);
			String k = tks[r];
			sent.cache.put(k, sent.cache.get(k)+1);
		}
		for (String k : sent.cache.asMap().keySet()) {
			System.out.println(k+": "+sent.cache.get(k));
		}
	}
}
