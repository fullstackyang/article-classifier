package com.fullstackyang.nlp.classifier.feature;

import com.fullstackyang.nlp.classifier.feature.FeatureSelection.Strategy;
import com.fullstackyang.nlp.classifier.model.Category;
import com.fullstackyang.nlp.classifier.utils.Calculator;
import lombok.AllArgsConstructor;

import java.util.Collection;
import java.util.Comparator;

@AllArgsConstructor
public class ChiSquaredStrategy implements Strategy {

    private final Collection<Category> categories;

    private final int total;

    @Override
    public Feature estimate(Feature feature) {

        class ContingencyTable {
            private final int A, B, C, D;

            private ContingencyTable(Feature feature, Category category) {
                A = feature.getDocCountByCategory(category);
                B = feature.getFeatureCount() - A;
                C = category.getDocCount() - A;
                D = total - A - B - C;
            }
        }

        Double chisquared = categories.stream()
                .map(c -> new ContingencyTable(feature, c))
                .map(ct -> Calculator.chisquare(ct.A, ct.B, ct.C, ct.D))
                .max(Comparator.comparingDouble(Double::valueOf)).get();
        feature.setScore(chisquared);
        return feature;
    }
}
