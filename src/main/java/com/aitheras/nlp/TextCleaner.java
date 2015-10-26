package com.aitheras.nlp;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.TextContentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.tidy.Tidy;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.sun.tools.hat.internal.parser.Reader;

public class TextCleaner {
	final static Logger logger = LoggerFactory.getLogger(TextCleaner.class);
	private static final int MINWORDLEN = 3;
	private List<String> stopwords;
	private List<String> scrubbedWords;
	public TextCleaner() throws IOException {
		loadStopwords();
	}
	private void loadStopwords() throws IOException {
		InputStream in = this.getClass().getResourceAsStream("/stopwords_en.txt");	
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        stopwords = new ArrayList<String>();
        String line = reader.readLine();
        while(line != null){
//            System.out.println(line);
        	String sw = line.trim();
        	if (sw.length()>=MINWORDLEN  && !line.startsWith("#")) {
        		logger.debug("Adding {} to stopword list",sw);
        		stopwords.add(sw);
        	}
            line = reader.readLine();
        }          
	}
	public void cleanText(String ... inboundTexts) {
		try {
		    final List<String> fields = Lists.newArrayList();
		    for (String raw : inboundTexts) {
//		        Tidy t = new Tidy();
//		        t.setErrout(new PrintWriter(new ByteArrayOutputStream()));
//		        StringWriter out = new StringWriter();
//		        t.parse(new StringReader(raw), out);
//		        String tidied = out.getBuffer().toString();
//    		    logger.debug("{}",tidied);
//		        AutoDetectParser p = new AutoDetectParser();
//		        p.parse(new ByteArrayInputStream(raw.getBytes()), 
//		        		new TextContentHandler(new DefaultHandler()
//		        {
//		            @Override
//		            public void characters(char[] ch, int start, int length) throws SAXException
//		            {
//		                CharBuffer buf = CharBuffer.wrap(ch, start, length);
//		                String s = buf.toString();
//		    		    logger.debug("{}",s);
//		                fields.add(s);
//		            }
//		        }), new Metadata());
		    }

		    Analyzer analyzer = new StandardAnalyzer();
//		    String joinedFields = Joiner.on(" ").join(fields).replaceAll("\\s+", " ");
		    String joinedFields = Joiner.on(" ").join(inboundTexts).replaceAll("\\s+", " ");
		    logger.debug("{}",joinedFields);
		    StringReader in = new StringReader(joinedFields);
		    TokenStream ts = analyzer.tokenStream("content", in);
		    ts.reset();
		    ts = new LowerCaseFilter(ts);

		    CharTermAttribute termAtt = ts.addAttribute(CharTermAttribute.class);
		    List<String> words = Lists.newArrayList();
		    while (ts.incrementToken()) {
		        char[] termBuffer = termAtt.buffer();
		        int termLen = termAtt.length();
		        String w = new String(termBuffer, 0, termLen);
		        words.add(w);
		    }
		    ts.end();
		    ts.close();
		    analyzer.close();
		    scrubbedWords = new ArrayList<String>();
		    for (String word : words) {
		    	if (word.length()>=MINWORDLEN && !stopwords.contains(word)) {
		    		scrubbedWords.add(word);
		    	} else {
		    		logger.debug("Ignoring word: {}",word);
		    	}
		    		
		    }
//		    this.scrubbedWords = words;
		}
		catch (Exception e) {
		    throw new RuntimeException(e);
		}		
	}
	public List<String> getScrubbedWords() {
		return scrubbedWords;
	}

}
