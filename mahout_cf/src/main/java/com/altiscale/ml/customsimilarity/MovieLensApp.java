package com.altiscale.ml.customsimilarity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.slopeone.SlopeOneRecommender;
import org.apache.mahout.cf.taste.impl.similarity.CityBlockSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.TanimotoCoefficientSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;

public class MovieLensApp {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String datamodel = args[0];
		String movieMetaDataFile = args[1];
		String testfile = args[2];
		String predictionsOutput = args[3];
		String similarityClassType = args[4];

		DataModel model = null;

		try {
			model = new FileDataModel(new File(datamodel));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}

		/*
		 * create this from the file args[1]
		 */
		FastByIDMap<Movie> movieMap = loadMovieMap(movieMetaDataFile);

		ItemSimilarity itemsimilarity = null;
		Recommender recommender = null;
		
		if(similarityClassType.compareTo("custom") ==0) {
			//custom content based similarity
			itemsimilarity = new MovieItemSimilarity(movieMap);
		} else if(similarityClassType.compareTo("pearson") == 0){
			try {
				itemsimilarity = new PearsonCorrelationSimilarity(model);
			} catch (TasteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(1);
			} 
		}else if(similarityClassType.compareTo("cb") == 0){
			itemsimilarity = new CityBlockSimilarity(model);
		}else if(similarityClassType.compareTo("euclidean") == 0){
			try {
				itemsimilarity = new EuclideanDistanceSimilarity(model);
			} catch (TasteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(1);
			}
		}else if(similarityClassType.compareTo("ll") == 0){
				itemsimilarity = new LogLikelihoodSimilarity(model);
		}else if(similarityClassType.compareTo("tan") == 0){
			itemsimilarity = new TanimotoCoefficientSimilarity(model);
		}
		
		
		
		if(similarityClassType.compareTo("slopeone") == 0){
			try {
				recommender = new SlopeOneRecommender(model);
			} catch (TasteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(1);
			}
		} else {
			// UserSimilarity similarityNearestNeighborCF = new
			// PearsonCorrelationSimilarity(model);
			// UserNeighborhood neighborhood = new NearestNUserNeighborhood(10,
			// similarityNearestNeighborCF, model);
			recommender = new GenericItemBasedRecommender(model, itemsimilarity);
		}

		/*
		 * 
		 * UserSimilarity similarity; similarity = new
		 * PearsonCorrelationSimilarity(model); UserNeighborhood neighborhood =
		 * new NearestNUserNeighborhood(2, similarity, model); Recommender
		 * recommender = new GenericUserBasedRecommender(model, neighborhood,
		 * similarity);
		 */

		Writer writer = null;

		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(predictionsOutput), "utf-8"));

			ArrayList<Rating> testset = getTestSet(testfile);
			int numRecs = 12000;// more than the total number of movies in
								// the
								// database
			float s = 0;
			int nanCount = 0;
			int exceptions=0;
			for (Rating r : testset) {


				float predictedScore = 0;
				try {
					predictedScore = recommender.estimatePreference(r.user, r.item);
				} catch (Exception e) {
					// TODO: handle exception
					//average rating of training set
					predictedScore = 3.50f;
//					e.printStackTrace();
					exceptions++;
//					continue;
				}
				
				if (Float.compare(Float.NaN, predictedScore) == 0) {
					predictedScore = 3.50f;
					nanCount++;
				}
				
				s = s + (predictedScore - r.rating) * (predictedScore - r.rating);
				writer.write(r.user + "," + r.item + "," + predictedScore + "\n");
				writer.flush();
			}
			System.out.println("similarityClassType="+similarityClassType);
			System.out.println("Test set file #num samples = " + testset.size());
			System.out.println("Num of NaN predictions = " + nanCount);
			System.out.println("Num of java exceptions = " + exceptions);
			System.out.println("RMSE = " + Math.sqrt(s / testset.size()));
			writer.close();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static ArrayList<Rating> getTestSet(String file) {
		ArrayList<Rating> testset = new ArrayList<Rating>();
		InputStream fis = null;
		BufferedReader br;

		try {
			fis = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		br = new BufferedReader(new InputStreamReader(fis,
				Charset.forName("UTF-8")));
		try {
			String line;
			while ((line = br.readLine()) != null) {
				// Deal with the line
				String fields[] = line.split(",");
				Rating r = new Rating();
				r.user = Long.parseLong(fields[0]);
				r.item = Long.parseLong(fields[1]);
				r.rating = Float.parseFloat(fields[2]);

				testset.add(r);
			}
			br.close();
			fis.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return testset;
	}

	private static FastByIDMap<Movie> loadMovieMap(String file) {
		InputStream fis = null;
		BufferedReader br;
		String line;

		FastByIDMap<Movie> movieMap = new FastByIDMap<Movie>();
		try {
			fis = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		br = new BufferedReader(new InputStreamReader(fis,
				Charset.forName("UTF-8")));
		try {
			line = br.readLine();// skip header
			while ((line = br.readLine()) != null) {
				// Deal with the line
				String fields[] = line.split("\t");

				Movie movie = new Movie(fields[1], "", "", "");
				movieMap.put(Long.parseLong(fields[0]), movie);
			}
			br.close();
			fis.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return movieMap;
	}

}
