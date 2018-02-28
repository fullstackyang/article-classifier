package com.fullstackyang.nlp.classifier.model;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Doc {

    private String id;

    private final Category category;

    private final List<Term> termVector;
}
