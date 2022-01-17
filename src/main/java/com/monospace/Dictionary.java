package com.monospace;

import com.google.common.io.CharSource;
import com.google.common.io.Files;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static java.lang.String.format;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Dictionary {

    private final Node root;

    public static Dictionary loadFromFile(String file) {
        System.out.println("Loading dictionary from " + file);

        try {
            CharSource charSource = Files.asCharSource(new File(file), StandardCharsets.US_ASCII);

            Node root = new Node(Letter.ROOT);
            charSource.forEachLine(word -> {
                if (!word.startsWith("#")) {
                    Node node = root;
                    for (char c : word.toCharArray())
                        node = node.addChild(c);
                    node.setWord(word);
                }
            });

            return new Dictionary(root);
        } catch (IOException e) {
            throw new IllegalStateException(format("Failed to load dictionary from %s: %s", file, e.getMessage()), e);
        }
    }

}
