package com.altiscale.ml.itembasedSimilarity;

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

import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;

import com.altiscale.ml.customsimilarity.Rating;

public class MovieLensAppLogLiklyhood {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String datamodel = args[0];
		String testfile = args[1];
		String predictionsOutput = args[2];

		DataModel model = null;

		try {
			model = new FileDataModel(new File(datamodel));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

		ItemSimilarity similarityLogLiklyhood = new LogLikelihoodSimilarity(
				model);

		Recommender recommender = new GenericItemBasedRecommender(model,
				similarityLogLiklyhood);

		Writer writer = null;

		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(predictionsOutput), "utf-8"));

			ArrayList<Rating> testset = getTestSet(testfile);

			float s = 0;
			int count = 0;
			for (Rating r : testset) {
				float predictedScore = 0;
				try {
					predictedScore = recommender.estimatePreference(r.user,
							r.item);
				} catch (Exception e) {
					// TODO: handle exception
					continue;
				}

				if (Float.compare(Float.NaN, predictedScore) != 0) {
					s = s + (predictedScore - r.rating)
							* (predictedScore - r.rating);
					// System.out.println(r.rating + "\t" + predictedScore
					// + "\t" + s);
					count++;
				} else {
					// System.out.println("NaN");
				}
				writer.write(r.user + "," + r.item + "," + predictedScore
						+ "\n");
				writer.flush();
			}
			System.out.println("Num of non NaN ratings = " + count + "\n");
			System.out.println("Tot Num of ratings = " + testset.size() + "\n");
			System.out.println("RMSE = " + Math.sqrt(s / count) + "\n");
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

}
