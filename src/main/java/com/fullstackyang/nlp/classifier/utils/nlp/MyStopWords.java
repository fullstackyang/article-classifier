package com.fullstackyang.nlp.classifier.utils.nlp;

import com.fullstackyang.nlp.classifier.utils.FileUtils;
import com.google.common.base.Charsets;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.hankcs.hanlp.corpus.io.IOUtil;
import com.hankcs.hanlp.dictionary.stopword.StopWordDictionary;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

public class MyStopWords implements NLPTools.StopWords {

    private Set<String> set;

    private final static String PATH = "data/stopwords.txt";

    MyStopWords() {
        set = Sets.newHashSet(FileUtils.readLines(PATH));
    }


    @Override
    public boolean isStopWord(String word) {
        return set.contains(word);
    }
}
