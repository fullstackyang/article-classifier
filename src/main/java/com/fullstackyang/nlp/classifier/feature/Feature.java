package com.fullstackyang.nlp.classifier.feature;

import com.fullstackyang.nlp.classifier.model.Category;
import com.fullstackyang.nlp.classifier.model.Term;
import com.google.common.collect.Maps;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;

@Slf4j
@Data
@ToString(of = {"term", "score"})
@EqualsAndHashCode(of = {"term"})
public class Feature {

    private final Term term;

    private double score;

    @Setter(AccessLevel.NONE)
    private Map<Category, Integer> categoryDocCounter;

    @Setter(AccessLevel.NONE)
    private Map<Category, Integer> categoryTermCounter;

    public Feature(Term term) {
        this.term = term;
    }

    public Feature(Term term, Category category) {
        this.term = term;
        this.categoryDocCounter = Maps.newHashMap();
        this.categoryDocCounter.put(category, 1);

        this.categoryTermCounter = Maps.newHashMap();
        this.categoryTermCounter.put(category, term.getTf());

    }

    public Feature merge(Feature feature) {
        if (this.term.equals(feature.getTerm())) {
            this.term.setTf(this.term.getTf() + feature.getTerm().getTf());
            feature.getCategoryDocCounter()
                    .forEach((k, v) -> categoryDocCounter.merge(k, v, (oldValue, newValue) -> oldValue + newValue));
            feature.getCategoryTermCounter()
                    .forEach((k, v) -> categoryTermCounter.merge(k, v, (oldValue, newValue) -> oldValue + newValue));
        }
        return this;
    }


    /**
     * 所有包含Feature的文档的数量
     * @return
     */
    int getFeatureCount() {
        return categoryDocCounter.values().stream().mapToInt(Integer::intValue).sum();
    }

    public int getDocCountByCategory(Category category) {
        return categoryDocCounter.getOrDefault(category, 0);
    }

    public int getTermCountByCategory(Category category) {
        return categoryTermCounter.getOrDefault(category, 0);
    }


}
