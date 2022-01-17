package com.monospace;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Letter {
    a('a', 1),
    b('b', 4),
    c('c', 4),
    d('d', 3),
    e('e', 1),
    f('f', 5),
    g('g', 3),
    h('h', 5),
    i('i', 1),
    j('j', 9, true), // BLUE
    k('k', 6),
    l('l', 2),
    m('m', 4),
    n('n', 2),
    o('o', 1),
    p('p', 4),
    q('q', 12, true), // BLUE
    r('r', 2),
    s('s', 1),
    t('t', 2),
    u('u', 1),
    v('v', 5),
    w('w', 5),
    x('x', 9, true), // BLUE
    y('y', 5),
    z('z', 11, true), // BLUE
    SPACE(' ', 0), // BLACK
    EMPTY('.', 0),
    ROOT((char) 0, 0);

    private final char letter;
    private final int value;
    private final boolean rowClearing;

    Letter(char letter, int value) {
        this(letter, value, false);
    }

    public static Letter from(char c) {
        if (c == SPACE.letter)
            return SPACE;
        if (c == EMPTY.letter)
            return EMPTY;
        if (c == ROOT.letter)
            return ROOT;
        return Letter.valueOf(Character.toString(c).toLowerCase());
    }
}