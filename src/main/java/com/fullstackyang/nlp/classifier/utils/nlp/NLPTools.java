package com.fullstackyang.nlp.classifier.utils.nlp;

import com.fullstackyang.nlp.classifier.model.Term;

import java.util.List;

public class NLPTools {

    interface Segmentor {
        List<Term> segment(String content);
    }

    interface StopWords {
        boolean isStopWord(String word);
    }

    private Segmentor segmentor;

    private StopWords stopWords;

    private NLPTools() {
//        this.segmentor = new JiebaSegmentor();
      this.segmentor = new AnsjSegmentor();
        this.stopWords = new MyStopWords();
    }

    private static class Holder {
        private static NLPTools instance = new NLPTools();
    }

    public static NLPTools instance() {
        return Holder.instance;
    }


    public List<Term> segment(String content) {
        return segmentor.segment(content);
    }

    public boolean isStopWord(String word) {
        return stopWords.isStopWord(word);
    }
}
