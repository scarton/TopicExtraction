package net.c4analytics.trainer.source;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Random;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.c4analytics.trainer.dao.Setup;


/**
 * Provides data for the GUI - random or searched doc text.
 * @TODO refactor sources to use an interface to a specific data service.
 * @author Steve Carton, stephen.carton@aitheras.com
 *
 */
public class LuceneSource implements DocumentSource {
	final static Logger logger = LoggerFactory.getLogger(LuceneSource.class);
	private Setup setup;
	private Directory indexStore;
	IndexReader indexReader;
	public String getRandomId() throws IOException {
		int docNo = (int)(new Random().nextDouble() *  indexReader.maxDoc());
		Document doc = indexReader.document(docNo);
		String randomId = doc.get("id");
		return randomId;
	}
	public String getDocText(String id) throws IOException {
		Document doc;
		try {
			doc = docQuery(id);
		} catch (ParseException e) {
			throw new IOException(e.getMessage());
		}
		String text = doc.get("text");
		text = text.replaceAll("[\\r\\n]+", "<p/>");
		return text;
	}
	public String getCleanDocText(String id) throws IOException {
		return getDocText(id);
	}
	public Document docQuery(String id) throws ParseException, IOException {
	    IndexReader reader = DirectoryReader.open(indexStore);
		IndexSearcher searcher = new IndexSearcher(reader);
		Term t = new Term("id", id);
		Query query = new TermQuery(t);
		TopDocs docs = searcher.search(query, 10);
	    ScoreDoc[] hits = docs.scoreDocs;
    	int docId = hits[0].doc;
    	Document d = searcher.doc(docId);
	    reader.close();
	    return d;
	}
	@Override
	public void init(Setup setup) throws IOException {
		this.setup=setup;
		indexStore = new NIOFSDirectory(Paths.get(setup.getIndexPath()));
		indexReader = DirectoryReader.open(indexStore);
	}
	@Override
	public void close() throws IOException {
		indexReader.close();
	}
	@Override
	public long maxDocs() throws IOException {
		return indexReader.maxDoc();
	}
	@Override
	public String getDocTitle(String id) throws IOException {
		return id;
	}
}
