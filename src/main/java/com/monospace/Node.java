package com.monospace;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public class Node {

    private final Letter letter;
    private final Map<Letter, Node> children = new EnumMap<>(Letter.class);
    private String word;

    public Node addChild(char c) {
        Letter letter = Letter.from(c);
        Node child = children.get(letter);
        if (child != null)
            return child;

        child = new Node(letter);
        children.put(letter, child);

        return child;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public boolean isRoot() {
        return letter == Letter.ROOT;
    }

    public Stream<Node> children() {
        return children.values().stream();
    }

    public Node get(char c) {
        return get(Letter.from(c));
    }

    public Node get(Letter letter) {
        return children.get(letter);
    }

    public boolean isWord() {
        return word != null;
    }

    public boolean matches(String word) {
        return word.equals(this.word);
    }

    public boolean hasChildren() {
        return !children.isEmpty();
    }

    @Override
    public String toString() {
        return letter + "[" + children.size() + "]" + (isWord() ? ":" + word : "");
    }
}