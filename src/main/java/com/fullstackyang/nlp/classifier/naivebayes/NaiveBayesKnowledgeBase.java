package com.fullstackyang.nlp.classifier.naivebayes;

import com.fullstackyang.nlp.classifier.feature.Feature;
import com.fullstackyang.nlp.classifier.model.Term;
import com.fullstackyang.nlp.classifier.utils.FileUtils;
import com.google.common.collect.Lists;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.*;
import java.util.function.Function;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.*;

@Slf4j
@Data
@NoArgsConstructor
public class NaiveBayesKnowledgeBase {

    @Getter(AccessLevel.PACKAGE)
    private Map<String, FeatureSummary> features;

    @Getter(AccessLevel.PACKAGE)
    private Map<String, Double> categories;

    public NaiveBayesKnowledgeBase(String modelPath) {
        log.info("加载文件，正在初始化...");
        List<String> lines = FileUtils.readLines(modelPath);
        this.categories = parseCategorySummary(lines.get(0));
        this.features = lines.stream().skip(1)
                .map(this::parseFeatureSummariy)
                .filter(Objects::nonNull)
                .collect(toMap(FeatureSummary::getWord, Function.identity()));
        log.info("初始化完成！");
    }

    private Map<String, Double> parseCategorySummary(String line) {
        if (!line.contains(" ") || !line.contains(":")) {
            log.error("格式有误");
            return null;
        }

        return Arrays.stream(line.split(" "))
                .filter(str -> str.contains(":"))
                .map(str -> str.split(":"))
                .collect(toMap(arr -> arr[0],
                        arr -> Double.parseDouble(arr[1]),
                        (u, v) -> {
                            throw new IllegalStateException(String.format("Duplicate key %s", u));
                        },
                        LinkedHashMap::new));
    }

    private FeatureSummary parseFeatureSummariy(String line) {
        try {
            return new FeatureSummary(line, categories.keySet());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    double getCategoryProbability(String category) {
        return categories.getOrDefault(category, 0.0);
    }

    double getPconditionByWord(String category, String word) {
        return features.containsKey(word) ? features.get(word).getPconditionByCategory(category) : 0.0;
    }

    public void write(String path) {
        FileUtils.write(new File(path), this.toString());
    }


    public String toString() {
        StringBuilder builder = new StringBuilder(categories.keySet().stream().map(c -> c + ":" + categories.get(c)).collect(joining(" ")));
        builder.append(System.lineSeparator());
        features.values().stream().map(f -> f.getWord() + ":" + categories.keySet().stream().map(c -> "" + f.getPconditionByCategory(c))
                .collect(joining(" ")) + System.lineSeparator()).forEach(builder::append);
        return builder.toString();
    }

    FeatureSummary createFeatureSummary(final Feature feature, final Map<String, Double> Pconditions) {
        return new FeatureSummary(feature, Pconditions);
    }

    class FeatureSummary {

        @Getter(AccessLevel.PACKAGE)
        private final String word;

        private final Map<String, Double> Pconditions;

        private FeatureSummary(final Feature feature, final Map<String, Double> Pconditions) {
            this.word = feature.getTerm().getWord();
            this.Pconditions = Pconditions;
        }

        private FeatureSummary(String str, Set<String> categorySet) throws Exception {
            if (!str.contains(":"))
                throw new Exception("invalid format");

            this.word = str.substring(0, str.indexOf(":"));
            String substring = str.substring(str.indexOf(":") + 1);
            if (!substring.contains(" "))
                throw new Exception("this feature has no Pcondition");

            String[] Pconditions = substring.split(" ");
            if (Pconditions.length != categorySet.size())
                throw new Exception("Pcondition's size doesn't match the category size");

            List<String> list = Lists.newArrayList(categorySet);
            this.Pconditions = IntStream.range(0, list.size()).boxed()
                    .collect(toMap(list::get, i -> Double.parseDouble(Pconditions[i])));
        }


        double getPconditionByCategory(String category) {
            return Pconditions.getOrDefault(category, 0.0);
        }

        public String toString() {
            return this.word + ":" + Pconditions.values().stream().map(Object::toString).collect(joining(" "));
        }

    }
}
