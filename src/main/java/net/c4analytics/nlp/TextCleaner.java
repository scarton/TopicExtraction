package net.c4analytics.nlp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.Span;

public class TextCleaner {
	final static Logger logger = LoggerFactory.getLogger(TextCleaner.class);
	private static final int MINWORDLEN = 3;
	private List<String> stopwords;
	private List<String> scrubbedWords;
	private List<String> regexes;
	private Map<String,Span> sentences;
	private String cleanedText;
	private Spelling spelling;
	SentenceModel model;
	public TextCleaner() throws IOException {
		loadSpelling();
		loadStopwords();
		loadSentenceModel();
		loadRegexes();
	}
	private void loadRegexes() throws IOException {
		InputStream in = this.getClass().getResourceAsStream("/cleaning.regexes.txt");	
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        regexes = new ArrayList<String>();
        String line = reader.readLine();
        while(line != null){
        	String rx = line.trim();
        	if (!line.startsWith("#")) {
        		regexes.add(rx);
        	}
            line = reader.readLine();
        }          
	}
	private void loadSpelling() throws IOException {
		spelling = new Spelling();
		spelling.setModel(SpellingModel.loadModel());
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
//        		logger.debug("Adding {} to stopword list",sw);
        		stopwords.add(sw);
        	}
            line = reader.readLine();
        }          
	}
	public void loadSentenceModel() throws InvalidFormatException, IOException {
		InputStream modelIn = this.getClass().getClassLoader().getResourceAsStream("en-sent.bin");
		logger.debug("Loading sentence model...");
		model = new SentenceModel(modelIn);
	}
	public void parseSentences(String ... inboundTexts) {
		sentences = new HashMap<String,Span>();
		SentenceDetectorME sentenceDetector = new SentenceDetectorME(model);
	    for (String raw : inboundTexts) {
	    	Span[] sentences = sentenceDetector.sentPosDetect(raw);
	    	for (Span sentence : sentences) {
	    		CharSequence senText = sentence.getCoveredText(raw);
	    		logger.debug("Sentence {}/{}: {}",sentence.getStart(),sentence.getEnd(),senText);
	    	}
	    }
	}
	public void cleanTextByRegex(String raw) { 
		this.cleanedText=raw!=null?raw:"";
		for (String regex : regexes) {
			this.cleanedText = this.cleanedText.replaceAll(regex, "");
		}
	}
	public void extractWords() {
		try {
		    final List<String> fields = Lists.newArrayList();
//		    for (String raw : inboundTexts) {
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
//		    }

		    Analyzer analyzer = new StandardAnalyzer();
//		    String joinedFields = Joiner.on(" ").join(fields).replaceAll("\\s+", " ");
		    StringReader in = new StringReader(this.cleanedText);
		    TokenStream ts = analyzer.tokenStream("content", in);
		    ts.reset();
		    ts = new LowerCaseFilter(ts);

		    CharTermAttribute termAtt = ts.addAttribute(CharTermAttribute.class);
		    List<String> words = Lists.newArrayList();
		    while (ts.incrementToken()) {
		        char[] termBuffer = termAtt.buffer();
		        int termLen = termAtt.length();
		        String w = new String(termBuffer, 0, termLen);
		        words.add(spelling.correct(w));
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
	public String getCleanedText() {
		return cleanedText;
	}

}
