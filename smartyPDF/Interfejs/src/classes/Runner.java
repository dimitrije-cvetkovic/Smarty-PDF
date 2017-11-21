package classes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.function.*;

public class Runner {
	
   String indexDir = System.getProperty("user.dir")+"\\indeks";
   String dataDir = System.getProperty("user.dir")+"\\txt";
   Indexer indexer;
   Searcher searcher;

   public Runner(){};
   //sortiraj
   
   public List<Result> Sortiraj(List<String> listaTXT, List<String> termini)
   {
	   Runner tester;
	   List<Result> results = new ArrayList<Result>();
	   for(String s:listaTXT)
 		  results.add(new Result(s));
	      try 
	      {
	         tester = new Runner();
	         tester.createIndex();
	         
	         for(String r:termini)
	        	 tester.search(r, results);
	      } 
	      catch (IOException e) 
	      {
	         e.printStackTrace();
	      } 
	      catch (ParseException e) 
	      {
	         e.printStackTrace();
	      }
	      for(int i=0; i<results.size()-1; i++)
	      {
	    	  for(int j=i+1; j<results.size(); j++)
	    	  {
	    		  if(results.get(i).factor < results.get(j).factor)
	    		  {
	    			  Result pom = new Result();
	    			  pom = results.get(i);
	    			  results.set(i, results.get(j));
	    			  results.set(j,pom);
	    		  }
	    	  }
	      }
	      return results;
   }
   
   private void createIndex() throws IOException
   {
      indexer = new Indexer(indexDir);
      int numIndexed;
      long startTime = System.currentTimeMillis();	
      numIndexed = indexer.createIndex(dataDir, new TextFileFilter());
      long endTime = System.currentTimeMillis();
      indexer.close();
      System.out.println(numIndexed+" File indexed, time taken: "
         +(endTime-startTime)+" ms");		
   }

   private void search(String searchQuery, List<Result> results) throws IOException, ParseException 
   {
      searcher = new Searcher(indexDir);
      long startTime = System.currentTimeMillis();
      TopDocs hits = searcher.search(searchQuery);
     
      long endTime = System.currentTimeMillis();
   
      System.out.println(hits.totalHits + " documents found. Time :" + (endTime - startTime));
      //System.out.println(hits.getMaxScore());
      for(ScoreDoc scoreDoc : hits.scoreDocs)
      {
         Document doc = searcher.getDocument(scoreDoc);
         //List<Fieldable> li=doc.getFields();
         //for(Fieldable f: li)
        	// System.out.println("Ovo se stampa "+f.stringValue());
         System.out.println(scoreDoc.score);
            System.out.println("File: " + doc.get(LuceneConstants.FILE_PATH));
            
            for(Result r:results)
            	if(r.name.compareTo(doc.get(LuceneConstants.FILE_NAME))==0)
            		r.factor+=scoreDoc.score;
            	
      }
      searcher.close();
   }
}