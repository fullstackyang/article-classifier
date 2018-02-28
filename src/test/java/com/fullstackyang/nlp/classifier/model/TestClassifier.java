package com.fullstackyang.nlp.classifier.model;

import com.fullstackyang.nlp.classifier.naivebayes.NaiveBayesClassifier;
import com.fullstackyang.nlp.classifier.naivebayes.NaiveBayesModels;
import org.junit.Test;

public class TestClassifier {

    @Test
    public void test() {
        NaiveBayesClassifier classifier = new NaiveBayesClassifier(NaiveBayesModels.Multinomial);
        String text = "明日赛事推荐：切尔西巴萨冤家路窄，恒大申花再战亚冠";
        String category = classifier.predict(text);
        System.out.println(category);
    }


}
