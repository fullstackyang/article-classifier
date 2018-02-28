package com.fullstackyang.nlp.classifier.utils;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.google.common.primitives.Chars;
import com.hankcs.hanlp.corpus.io.IOUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;

@Slf4j
public class FileUtils {

    public static String readAll(String path) {
        try {
            return IOUtil.readTxt(path, Charsets.UTF_8.displayName());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    public static List<String> readLines(String path) {
        if (Strings.isNullOrEmpty(path))
            return Lists.newArrayList();

        return IOUtil.readLineList(path);

    }

    public static void write(File file, String content) {
        try {
            Files.write(content, file, Charsets.UTF_8);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
