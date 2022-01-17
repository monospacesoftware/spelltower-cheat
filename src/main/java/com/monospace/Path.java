package com.monospace;

import com.google.common.base.Suppliers;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public class Path implements Comparable<Path> {

    private final Tower tower;
    private final Tile[] tiles;
    private Supplier<Integer> valueSupplier = Suppliers.memoize(this::calcValue);
    private Supplier<String> wordSupplier = Suppliers.memoize(this::toWord);

    public Path(Tower tower) {
        this.tower = tower;
        tiles = new Tile[0];
    }

    private Path(Path path, Tile tile) {
        this.tower = path.tower;
        this.tiles = new Tile[path.size() + 1];
        System.arraycopy(path.getTiles(), 0, tiles, 0, path.size());
        tiles[tiles.length - 1] = tile;
    }

    public Path with(Tile tile) {
        return new Path(this, tile);
    }

    public int size() {
        return tiles.length;
    }

    public Stream<Tile> tiles() {
        return Arrays.stream(tiles);
    }

    public Stream<Tile> usedTiles() {
        Set<Tile> usedTiles = new HashSet<>();

        int width = tower.getWidth();
        int len = this.size();

        tiles().forEach(tile -> {
            usedTiles.add(tile);

            if (len > 4) {
                Optional.ofNullable(tower.getAdjacent(tile, Adjacent.TM)).ifPresent(usedTiles::add);
                Optional.ofNullable(tower.getAdjacent(tile, Adjacent.MR)).ifPresent(usedTiles::add);
                Optional.ofNullable(tower.getAdjacent(tile, Adjacent.BM)).ifPresent(usedTiles::add);
                Optional.ofNullable(tower.getAdjacent(tile, Adjacent.ML)).ifPresent(usedTiles::add);
            }

            if (tile.isRowClearing()) {
                int row = tile.getY();
                for (int column = 0; column < width; column++)
                    usedTiles.add(tower.getTile(column, row));
            }
        });

        return usedTiles.stream();
    }

    public boolean contains(Tile tile) {
        return tiles()
                .anyMatch(t -> t.equals(tile));
    }

    public boolean contains(int x, int y) {
        return tiles()
                .anyMatch(t -> t.equals(x, y));
    }

    public Tile get(int x, int y) {
        return tiles()
                .filter(t -> t.equals(x, y))
                .findFirst()
                .orElse(null);
    }

    public String getWord() {
        return wordSupplier.get();
    }

    private String toWord() {
        return tiles()
                .map(Tile::getLetter)
                .map(Object::toString)
                .collect(Collectors.joining());
    }

    public int getValue() {
        return valueSupplier.get();
    }

    private int calcValue() {
//      String word = toWord();
//      System.out.println();
//      System.out.println("Calculating value of " + word);
//      System.out.println(toString());

        int width = tower.getWidth();
        int len = this.size();

        final AtomicInteger acc = new AtomicInteger();
        final Set<Tile> bonusTiles = new HashSet<>();

        tiles().forEach(tile -> {
            acc.addAndGet(tile.getValue());

            if (len > 4) {
                Tile north = tower.getAdjacent(tile, Adjacent.TM);
                if (north != null && north.hasValue() && !this.contains(north))
                    bonusTiles.add(north);

                Tile east = tower.getAdjacent(tile, Adjacent.MR);
                if (east != null && east.hasValue() && !this.contains(east))
                    bonusTiles.add(east);

                Tile south = tower.getAdjacent(tile, Adjacent.BM);
                if (south != null && south.hasValue() && !this.contains(south))
                    bonusTiles.add(south);

                Tile west = tower.getAdjacent(tile, Adjacent.ML);
                if (west != null && west.hasValue() && !this.contains(west))
                    bonusTiles.add(west);
            }

            if (tile.isRowClearing()) {
                int row = tile.getY();
                for (int column = 0; column < width; column++) {
                    Tile rowTile = tower.getTile(column, row);
                    if (!this.contains(rowTile))
                        bonusTiles.add(rowTile);
                }
            }
        });

        bonusTiles.stream()
                .map(Tile::getValue)
                .forEach(acc::addAndGet);

        return acc.get() * len;
    }

    @Override
    public String toString() {
        return String.format("%s(%s)", getWord(), getValue());
    }

//    @Override
//    public String toString() {
//        StringBuilder sb = new StringBuilder();
//
//        sb.append(toWord());
//        sb.append("(");
//        sb.append(getValue());
//        sb.append(")\n");
//
//        tiles().forEach(tile -> {
//            sb.append(" " + tile);
//            sb.append("\n");
//        });
//
//        return sb.toString();
//    }

    public String toDisplay() {
        StringBuilder sb = new StringBuilder();
        sb.append(getWord());
        sb.append("(");
        sb.append(getValue());
        sb.append(")\n");
        sb.append("  ");

        int width = tower.getWidth();
        int height = tower.getHeight();

        for (int x = 0; x < width; x++) {
            sb.append(x + 1);
        }

        sb.append("\n");

        for (int y = 0; y < height; y++) {
            if (y < 9)
                sb.append(" ");
            sb.append(y + 1);
            for (int x = 0; x < width; x++) {
                Tile tile = tower.getTile(x, y);
                if (tile.isEmpty())
                    sb.append(" ");
                else if (contains(tile))
                    sb.append(tile.getLetter());
                else
                    sb.append(".");
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    @Override
    public int compareTo(Path o) {
        return Integer.compare(o.getValue(), getValue());
    }
}
