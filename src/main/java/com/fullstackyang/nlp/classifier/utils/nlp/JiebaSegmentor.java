package com.fullstackyang.nlp.classifier.utils.nlp;

import com.fullstackyang.nlp.classifier.model.Term;
import com.fullstackyang.nlp.classifier.utils.nlp.NLPTools.Segmentor;
import com.huaban.analysis.jieba.JiebaSegmenter;

import java.util.List;
import java.util.function.Function;

import static java.util.stream.Collectors.*;

public class JiebaSegmentor implements Segmentor {

   private final JiebaSegmenter segmenter = new JiebaSegmenter();
    @Override
    public List<Term> segment(String content) {
        return segmenter.process(content, JiebaSegmenter.SegMode.INDEX).stream().map(t->new Term(t.word,"n",1))
                .collect(collectingAndThen(toList(),
                        list -> {
                            //词频统计
                            list.parallelStream().collect(groupingBy(Function.identity(), counting())).forEach((term, count) -> {
                                list.stream().filter(t -> t.getWord().equals(term.getWord())).forEach(t -> t.setTf(count.intValue()));
                            });
                            return list;
                        }));
    }
}
