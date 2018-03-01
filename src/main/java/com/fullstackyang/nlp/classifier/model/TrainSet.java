package com.fullstackyang.nlp.classifier.model;

import com.fullstackyang.nlp.classifier.utils.FileUtils;
import com.fullstackyang.nlp.classifier.utils.nlp.NLPTools;
import com.fullstackyang.nlp.classifier.utils.nlp.TermFilter;
import com.google.common.io.PatternFilenameFilter;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.*;

import static java.util.stream.Collectors.*;

@Slf4j
public class TrainSet {

    @Getter
    private Set<Category> categorySet;


    @Getter
    private List<Doc> docs;

    private static final NLPTools nlpTools = NLPTools.instance();

    private static final PatternFilenameFilter filenameFilter = new PatternFilenameFilter("(\\w+)\\.txt$");

    /**
     * 总文档数
     */
    @Getter
    private int totalDoc;

    /**
     * 总词数
     */
    @Getter
    private int totalTerm;

    public TrainSet(String path) {
        File root = new File(path);
        if (root.listFiles() == null) {
            log.error("未发现训练集");
            return;
        }

        log.info("开始读取训练集...");
        this.categorySet = createCategorySet(root);
        log.info("类别集合创建完成！");
        this.docs = categorySet.parallelStream()
                .map(c -> createDocs(c, new File(c.getPath()).listFiles(filenameFilter)))
                .flatMap(Collection::stream).collect(toList());

        log.info("所有训练语料读取完成！开始统计...");
        this.totalDoc = categorySet.stream().mapToInt(Category::getDocCount).sum();
        this.totalTerm = docs.parallelStream().map(Doc::getTerms).flatMap(List::stream).mapToInt(Term::getTf).sum();
        log.info("统计完成, 总文档数:" + totalDoc + ", 总类别数:" + categorySet.size() + ", 总字词数:" + totalTerm);

        log.info("各类别文档数分布:");
        categorySet.stream()
                .sorted(Comparator.comparing(Category::getDocCount).reversed())
                .map(c -> c.getName() + "/" + c.getDocCount())
                .forEach(log::info);
    }


    private Set<Category> createCategorySet(File root) {
        return Arrays.stream(root.listFiles())
                .filter(File::isDirectory)
                .map(f -> new Category(f.getName(), f.getAbsolutePath(), f.listFiles(filenameFilter).length))
                .collect(toSet());
    }

    private List<Doc> createDocs(final Category category, File[] files) {
        return Arrays.stream(files).parallel()
                .map(f -> new Doc(f.getName(), category,getTerms(f.getAbsolutePath())))
                .collect(toList());
    }

    private List<Term> getTerms(String path) {
        return nlpTools.segment(FileUtils.readAll(path)).stream().filter(TermFilter::filter).distinct().collect(toList());
    }


    public Optional<Category> getCategory(String name) {
        return categorySet.stream().filter(c -> c.getName().equals(name)).findFirst();
    }


}
