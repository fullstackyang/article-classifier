package com.fullstackyang.nlp.classifier.naivebayes;

import com.fullstackyang.nlp.classifier.feature.ChiSquaredStrategy;
import com.fullstackyang.nlp.classifier.feature.Feature;
import com.fullstackyang.nlp.classifier.feature.FeatureSelection;
import com.fullstackyang.nlp.classifier.feature.IGStrategy;
import com.fullstackyang.nlp.classifier.model.Category;
import com.fullstackyang.nlp.classifier.model.Doc;
import com.fullstackyang.nlp.classifier.model.Term;
import com.fullstackyang.nlp.classifier.model.TrainSet;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static com.fullstackyang.nlp.classifier.naivebayes.NaiveBayesModels.Bernoulli;
import static com.fullstackyang.nlp.classifier.naivebayes.NaiveBayesModels.Multinomial;
import static java.util.stream.Collectors.*;

@Slf4j
public class NaiveBayesLearner {

    private int total;

    private NaiveBayesKnowledgeBase knowledgeBase;

    interface Model {
        /**
         * 计算类别C的先验概率
         *
         * @param total    (多项式模型)总特征数/(伯努利模型)总文档数
         * @param category (多项式模型)该类别的特征总数/(伯努利模型)该类别的文档总数
         * @return 类别C的先验概率
         */
        double Pprior(int total, final Category category);

        /**
         * 计算类别C的条件概率
         *
         * @param feature   获取文档中出现了feature且属于类别category的数量
         * @param category  获取类别category的文档数量
         * @param smoothing 平滑参数
         * @return 类别C的条件概率
         */
        double Pcondition(final Feature feature, final Category category, double smoothing);
    }

    private Model model;

    private Set<Category> categorySet;
    private Set<Feature> featureSet;

    private TrainSet trainSet;

    public NaiveBayesLearner(Model model, TrainSet trainSet, Set<Feature> selectedFeatures) {
        this.model = model;
        this.trainSet = trainSet;
        this.featureSet = selectedFeatures;
        this.knowledgeBase = new NaiveBayesKnowledgeBase();
    }

    public NaiveBayesLearner statistics() {
        log.info("开始统计...");
        this.total = total();
        log.info("total : " + total);
        this.categorySet = trainSet.getCategorySet();
        featureSet.forEach(f -> f.getCategoryTermCounter().forEach((category, count) -> category.setTermCount(category.getTermCount() + count)));
        categorySet.stream().map(Category::toString).forEach(log::info);
        return this;
    }

    public NaiveBayesKnowledgeBase build() {
        this.knowledgeBase.setCategories(createCategorySummaries(categorySet));
        this.knowledgeBase.setFeatures(createFeatureSummaries(featureSet, categorySet));
        return knowledgeBase;
    }

    private Map<String, NaiveBayesKnowledgeBase.FeatureSummary> createFeatureSummaries(final Set<Feature> featureSet, final Set<Category> categorySet) {
        return featureSet.parallelStream()
                .map(f -> knowledgeBase.createFeatureSummary(f, getPconditions(f, categorySet)))
                .collect(toMap(NaiveBayesKnowledgeBase.FeatureSummary::getWord, Function.identity()));
    }

    private Map<String, Double> createCategorySummaries(final Set<Category> categorySet) {
        return categorySet.stream().collect(toMap(Category::getName, c -> model.Pprior(total, c)));
    }

    private Map<String, Double> getPconditions(final Feature feature, final Set<Category> categorySet) {
        final double smoothing = smoothing();
        return categorySet.stream()
                .collect(toMap(Category::getName, c -> model.Pcondition(feature, c, smoothing)));
    }

    private int total() {
        if (model == Multinomial)
            return featureSet.parallelStream()
                    .map(Feature::getTerm)
                    .mapToInt(Term::getTf)
                    .sum();
        else if (model == Bernoulli)
            return trainSet.getTotalDoc();
        return 0;
    }

    private double smoothing() {
        if (model == Multinomial)
            return this.featureSet.size();
        else if (model == Bernoulli)
            return 2.0;
        return 0.0;
    }


    public static void main(String[] args) {
        TrainSet trainSet = new TrainSet(System.getProperty("user.dir") + "/trainset/");

        log.info("特征选择开始...");
        FeatureSelection featureSelection = new FeatureSelection(new ChiSquaredStrategy(trainSet.getCategorySet(), trainSet.getTotalDoc()));
        List<Feature> features = featureSelection.select(trainSet.getDocs());
        log.info("特征选择完成,特征数:[" + features.size() + "]");
        features.forEach(System.out::println);

        NaiveBayesModels model = NaiveBayesModels.Multinomial;
        NaiveBayesLearner learner = new NaiveBayesLearner(model, trainSet, Sets.newHashSet(features));
        learner.statistics().build().write(model.getModelPath());
        log.info("模型文件写入完成,路径:" + model.getModelPath());
    }

}
