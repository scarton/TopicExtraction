package com.aitheras.nlp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


class Spelling {

    private Map<String, Integer> model;

    private final ArrayList<String> edits(String word) {
        ArrayList<String> result = new ArrayList<String>();
        for(int i=0; i < word.length(); ++i) result.add(word.substring(0, i) + word.substring(i+1));
        for(int i=0; i < word.length()-1; ++i) result.add(word.substring(0, i) + word.substring(i+1, i+2) + word.substring(i, i+1) + word.substring(i+2));
        for(int i=0; i < word.length(); ++i) for(char c='a'; c <= 'z'; ++c) result.add(word.substring(0, i) + String.valueOf(c) + word.substring(i+1));
        for(int i=0; i <= word.length(); ++i) for(char c='a'; c <= 'z'; ++c) result.add(word.substring(0, i) + String.valueOf(c) + word.substring(i));
        return result;
    }

    public final String correct(String word) {
        if(model.containsKey(word)) return word;
        ArrayList<String> list = edits(word);
        HashMap<Integer, String> candidates = new HashMap<Integer, String>();
        for(String s : list) if(model.containsKey(s)) candidates.put(model.get(s),s);
        if(candidates.size() > 0) return candidates.get(Collections.max(candidates.keySet()));
        for(String s : list) for(String w : edits(s)) if(model.containsKey(w)) candidates.put(model.get(w),w);
        return candidates.size() > 0 ? candidates.get(Collections.max(candidates.keySet())) : word;
    }

    public final String[] correct(String ... words) {
    	ArrayList<String> fixed = new ArrayList<String>();
    	for (String word : words) {
    		fixed.add(correct(word));
    	}
    	return fixed.toArray(new String[0]);
    }

    public static void main(String args[]) throws IOException {
    	Spelling spelling = new Spelling();
    	spelling.setModel(SpellingModel.loadModel());
        if(args.length > 0) {
        	String[] words = args;
        	String[] cwds = spelling.correct(words);
        	for (int i=0; i<words.length; i++)
        		System.out.println("'"+words[i]+"' - '"+cwds[i]+"'");
        }
    }

	public  void setModel(Map<String, Integer> model) {
		this.model = model;
	}
}
