package com.fullstackyang.nlp.classifier.model;

import com.fullstackyang.nlp.classifier.utils.Calculator;
import lombok.*;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

@Data
@AllArgsConstructor
@EqualsAndHashCode(of = "name")
@ToString(of = {"name", "docCount","termCount"})
public class Category {

    private final String name;

    private final String path;

    private final int docCount;

    private int termCount;

    public Category(String name, String path, int docCount) {
        this.name = name;
        this.path = path;
        this.docCount = docCount;
    }

}
