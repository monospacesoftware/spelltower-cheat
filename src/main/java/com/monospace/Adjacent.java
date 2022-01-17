package com.monospace;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Adjacent {
    TL(-1, -1),
    TM(-1, 0),
    TR(-1, 1),
    ML(0, -1),
    MR(0, 1),
    BL(1, -1),
    BM(1, 0),
    BR(1, 1);

    private final int y;
    private final int x;
}
