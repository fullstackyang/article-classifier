package com.fullstackyang.nlp.classifier.naivebayes;

import com.fullstackyang.nlp.classifier.model.Term;
import com.fullstackyang.nlp.classifier.utils.Calculator;
import com.fullstackyang.nlp.classifier.utils.nlp.NLPTools;
import lombok.AllArgsConstructor;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

public class NaiveBayesClassifier {

    interface Model {
        String getModelPath();

        List<Double> getConditionProbability(String category, List<Term> terms, final NaiveBayesKnowledgeBase knowledgeBase);
    }

    private final Model model;

    private final NaiveBayesKnowledgeBase knowledgeBase;

    public NaiveBayesClassifier() {
        this(NaiveBayesModels.Multinomial);
    }

    public NaiveBayesClassifier(Model model) {
        this.model = model;
        this.knowledgeBase = new NaiveBayesKnowledgeBase(model.getModelPath());
    }

    public String predict(String content) {
        Set<String> allFeatures = knowledgeBase.getFeatures().keySet();
        List<Term> terms = NLPTools.instance().segment(content).stream()
                .filter(t -> allFeatures.contains(t.getWord()))
                .distinct()
                .collect(toList());

        @AllArgsConstructor
        class Result {
            final String category;
            final double probability;
        }

        Result result = knowledgeBase.getCategories().keySet().stream()
                .map(c -> new Result(c, Calculator.Ppost(knowledgeBase.getCategoryProbability(c),
                        model.getConditionProbability(c, terms, knowledgeBase))))
                .max(Comparator.comparingDouble(r -> r.probability)).orElse(new Result("unkown", 0.0));
        return result.category;
    }


}
