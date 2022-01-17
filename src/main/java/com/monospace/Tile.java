package com.monospace;

import com.google.common.base.Suppliers;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.Supplier;

@Getter
@RequiredArgsConstructor
public class Tile {

    private final Letter letter;
    private final int y;
    private final int x;
    private final Supplier<Integer> hashCodeSupplier = Suppliers.memoize(this::calcHash);

    public boolean isSpace() {
        return letter == Letter.SPACE;
    }

    public boolean isEmpty() {
        return letter == Letter.EMPTY;
    }

    public boolean hasValue() {
        return letter.getValue() > 0;
    }

    public int getValue() {
        return letter.getValue();
    }

    public boolean isRowClearing() {
        return letter.isRowClearing();
    }

    @Override
    public String toString() {
        return String.format("%s(%s)", letter, getValue());
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Tile))
            return false;
        return equals(((Tile) object).getX(), ((Tile) object).getY());
    }

    public boolean equals(int x, int y) {
        return this.x == x && this.y == y;
    }

    @Override
    public int hashCode() {
        return hashCodeSupplier.get();
    }

    private int calcHash() {
        return String.format("%s,%s", x, y).hashCode();
    }

}
