package com.fullstackyang.nlp.classifier.feature;

import com.fullstackyang.nlp.classifier.model.Category;
import com.fullstackyang.nlp.classifier.utils.Calculator;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

import static java.util.stream.Collectors.toList;

@Slf4j
@AllArgsConstructor
public class IGStrategy implements FeatureSelection.Strategy {

    // 所有分类
    private final Collection<Category> categories;

    //总文档数
    private final int total;


    public Feature estimate(Feature feature) {
        double totalEntropy = calcTotalEntropy();
        double conditionalEntrogy = calcConditionEntropy(feature);
        feature.setScore(totalEntropy - conditionalEntrogy);
        return feature;
    }

    private double calcTotalEntropy() {
        return Calculator.entropy(categories.stream().map(c -> (double) c.getDocCount() / total).collect(toList()));
    }

    private double calcConditionEntropy(Feature feature) {
        int featureCount = feature.getFeatureCount();
        double Pfeature = (double) featureCount / total;

        Map<Boolean, List<Double>> Pcondition = categories.parallelStream().collect(() -> new HashMap<Boolean, List<Double>>() {{
                    put(true, Lists.newArrayList());
                    put(false, Lists.newArrayList());
                }}, (map, category) -> {
                    int countDocWithFeature = feature.getDocCountByCategory(category);
                    //出现该特征词且属于类别key的文档数量/出现该特征词的文档总数量
                    map.get(true).add((double) countDocWithFeature / featureCount);
                    //未出现该特征词且属于类别key的文档数量/未出现该特征词的文档总数量
                    map.get(false).add((double) (category.getDocCount() - countDocWithFeature) / (total - featureCount));
                },
                (map1, map2) -> {
                    map1.get(true).addAll(map2.get(true));
                    map1.get(false).addAll(map2.get(false));
                }
        );
        return Calculator.conditionalEntrogy(Pfeature, Pcondition.get(true), Pcondition.get(false));

    }
}


