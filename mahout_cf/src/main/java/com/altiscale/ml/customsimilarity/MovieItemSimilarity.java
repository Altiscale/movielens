package com.altiscale.ml.customsimilarity;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;

public class MovieItemSimilarity implements ItemSimilarity {
	private final FastByIDMap<Movie> movieMap;
	private double similarityThresh=0.1;

	public MovieItemSimilarity(FastByIDMap<Movie> movieMap) {
		this.movieMap = movieMap;
	}

	@Override
	public double itemSimilarity(long arg0, long arg1) throws TasteException {
		// TODO Auto-generated method stub
		Movie movie1 = movieMap.get(arg0);
		if(movie1 == null){
			return 0;//TODO throw exception
		}
		
		Movie movie2 = movieMap.get(arg1);
		if(movie2 == null){
			return 0;//TODO throw exception
		}
		String bag1 = movie1.getTitle() + movie1.getActors() + movie1.getDirectors() + movie1.getLocations();
		String bag2 = movie2.getTitle() + movie2.getActors() + movie2.getDirectors() + movie2.getLocations();
		String words1[] = bag1.split("[^a-zA-Z0-9']");
		String words2[] = bag2.split("[^a-zA-Z0-9']");
		//tokenize title1, actor1, director1, location1
		Set<String> set1 = new TreeSet<String>();
		Set<String> set2 = new TreeSet<String>();

		for(String word:words1){
			set1.add(word);
		}
		for(String word:words2){
			set2.add(word);
		}
		//union
		Set<String> union = new TreeSet<String>();
		union.addAll(set1);
		union.addAll(set2);
		
		Set<String> intersection = new TreeSet<String>();
		intersection.addAll(set1);
		intersection.retainAll(set2);
		double s = 1.0*intersection.size()/union.size(); 
		if(s == 0) {
			//assume a non zero to avoid nan
			return 1.0/union.size();
		}else{
			return s;
		}
	}

	@Override
	public long[] allSimilarItemIDs(long arg0) throws TasteException {
		
	    FastIDSet allSimilarItemIDs = new FastIDSet();
	    LongPrimitiveIterator allItemIDs = movieMap.keySetIterator();
	    while (allItemIDs.hasNext()) {
	      long possiblySimilarItemID = allItemIDs.nextLong();
	      double score = itemSimilarity(arg0, possiblySimilarItemID);
	      
	      if (score > similarityThresh) {
	        allSimilarItemIDs.add(possiblySimilarItemID);
	      }
	    }
	    return allSimilarItemIDs.toArray();
	}

	@Override
	public double[] itemSimilarities(long arg0, long[] arg1)
			throws TasteException {
		double scores[] = new double[arg1.length];
		for(int i=0;i<arg1.length;i++){
			double score=itemSimilarity(arg0, arg1[i]);
			scores[i]=score;
		}
		return scores;
	}

	@Override
	public void refresh(Collection<Refreshable> arg0) {
		// TODO Auto-generated method stub

	}
}
