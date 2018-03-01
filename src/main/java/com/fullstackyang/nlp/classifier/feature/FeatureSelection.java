package com.fullstackyang.nlp.classifier.feature;

import com.fullstackyang.nlp.classifier.model.*;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.*;

@Slf4j
@AllArgsConstructor
public class FeatureSelection {

    interface Strategy {
        Feature estimate(Feature feature);
    }

    private final Strategy strategy;

    private final static int FEATURE_SIZE = 20000;

    public List<Feature> select(List<Doc> docs) {
        return createFeatureSpace(docs.stream())
                .stream()
                .map(strategy::estimate)
                .filter(f -> f.getTerm().getWord().length() > 1)
                .sorted(comparing(Feature::getScore).reversed())
                .limit(FEATURE_SIZE)
                .collect(toList());

    }

    private Collection<Feature> createFeatureSpace(Stream<Doc> docs) {

        @AllArgsConstructor
        class FeatureCounter {

            private final Map<Term, Feature> featureMap;

            private FeatureCounter accumulate(Doc doc) {
                Map<Term, Feature> temp = doc.getTerms().parallelStream()
                        .map(t -> new Feature(t, doc.getCategory()))
                        .collect(toMap(Feature::getTerm, Function.identity()));

                if (!featureMap.isEmpty())
                    featureMap.values().forEach(f -> temp.merge(f.getTerm(), f, Feature::merge));
                return new FeatureCounter(temp);
            }

            private FeatureCounter combine(FeatureCounter featureCounter) {
                Map<Term, Feature> temp = Maps.newHashMap(featureMap);
                featureCounter.featureMap.values().forEach(f -> temp.merge(f.getTerm(), f, Feature::merge));
                return new FeatureCounter(temp);
            }
        }

        FeatureCounter counter = docs.parallel()
                .reduce(new FeatureCounter(Maps.newHashMap()),
                        FeatureCounter::accumulate,
                        FeatureCounter::combine);


        return counter.featureMap.values();
    }

}
