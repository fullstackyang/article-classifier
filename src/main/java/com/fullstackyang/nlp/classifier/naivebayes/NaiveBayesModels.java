package com.fullstackyang.nlp.classifier.naivebayes;

import com.fullstackyang.nlp.classifier.feature.Feature;
import com.fullstackyang.nlp.classifier.model.Category;
import com.fullstackyang.nlp.classifier.model.Term;

import java.util.List;

import static java.util.stream.Collectors.toList;

public enum NaiveBayesModels implements NaiveBayesClassifier.Model, NaiveBayesLearner.Model {

    Bernoulli {
        @Override
        public String getModelPath() {
            return "data/bernoulli_naive_bayes_model";
        }

        @Override
        public double Pprior(int total, Category category) {
            int Nc = category.getDocCount();
            return Math.log((double) Nc / total);
        }

        @Override
        public double Pcondition(Feature feature, Category category, double smoothing) {
            int Ncf = feature.getDocCountByCategory(category);
            int Nc = category.getDocCount();
            return Math.log((double) (1 + Ncf) / (Nc + smoothing));
        }

        @Override
        public List<Double> getConditionProbability(String category, List<Term> terms, final NaiveBayesKnowledgeBase knowledgeBase) {
            return terms.stream().map(term -> knowledgeBase.getPconditionByWord(category, term.getWord())).collect(toList());
        }


    },
    Multinomial {
        @Override
        public String getModelPath() {
            return "data/multinomial_naive_bayes_model";
        }

        @Override
        public double Pprior(int total, Category category) {
            int Nt = category.getTermCount();
            return Math.log((double) Nt / total);
        }

        @Override
        public double Pcondition(Feature feature, Category category, double smoothing) {
            int Ntf = feature.getTermCountByCategory(category);
            int Nt = category.getTermCount();
            return Math.log((double) (1 + Ntf) / (Nt + smoothing));
        }

        @Override
        public List<Double> getConditionProbability(String category, List<Term> terms, final NaiveBayesKnowledgeBase knowledgeBase) {
            return terms.stream().map(term -> term.getTf() * knowledgeBase.getPconditionByWord(category, term.getWord())).collect(toList());
        }
    };

}
