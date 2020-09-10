package net.c4analytics.nlp;

import java.io.IOException;

import org.junit.Test;

import junit.framework.Assert;
import net.c4analytics.nlp.Spelling;
import net.c4analytics.nlp.SpellingModel;

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
