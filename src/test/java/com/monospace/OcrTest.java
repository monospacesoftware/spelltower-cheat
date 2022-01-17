package com.monospace;

import org.junit.Test;

import java.util.List;

public class OcrTest {

    @Test
    public void testOcr() {
        List<String> lines = Ocr.loadFromImage("img.jpg");
        lines.forEach(System.out::println);
    }

}
