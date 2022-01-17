package com.monospace;

import com.google.common.base.Strings;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class Ocr {

    public static List<String> loadFromImage(String imageFile) {
        com.asprise.ocr.Ocr.setUp();
        com.asprise.ocr.Ocr ocr = new com.asprise.ocr.Ocr();
        ocr.startEngine("eng", com.asprise.ocr.Ocr.SPEED_SLOW);

        Properties props = new Properties();
        props.setProperty("PROP_PAGE_TYPE", "single_block");
        props.setProperty("PROP_OUTPUT_SEPARATE_WORDS", "false");
        props.setProperty("PROP_TABLE_SKIP_DETECTION", "true");
        props.setProperty("PROP_IMG_PREPROCESS_TYPE", "custom");
        props.setProperty("PROP_IMG_PREPROCESS_CUSTOM_CMDS", "grayscale()");

        String result = ocr.recognize(new File[]{new File(imageFile)},
                com.asprise.ocr.Ocr.RECOGNIZE_TYPE_TEXT,
                com.asprise.ocr.Ocr.OUTPUT_FORMAT_PLAINTEXT,
                props);
        ocr.stopEngine();

        System.out.println(result);

        List<String> lines = Arrays.stream(result.split("\n"))
                .filter(line -> !line.contains("DailyTower"))
                .filter(line -> !line.contains("I'MDONE"))
                .map(line -> line.replaceAll("l", "I"))
                .map(line -> line.replaceAll("\\\\X/", "W"))
                .map(line -> line.replaceAll("\\.", " "))
                .map(line -> line.replaceAll("-", " "))
                .map(line -> Strings.padEnd(line, 9, ' '))
                .collect(Collectors.toList());

        ocr.stopEngine();
        
        return lines;
        
    }
    
}
