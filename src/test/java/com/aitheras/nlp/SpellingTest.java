package com.aitheras.nlp;

import java.io.IOException;

import org.junit.Test;

import junit.framework.Assert;

public class SpellingTest {
	@Test
	public void testSpelling() throws IOException {
    	Spelling spelling = new Spelling();
    	spelling.setModel(SpellingModel.loadModel());
    	String w = "thew";
    	String c = spelling.correct(w);
		Assert.assertEquals("","");
	}
}
