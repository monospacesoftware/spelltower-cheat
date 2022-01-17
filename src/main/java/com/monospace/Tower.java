package com.monospace;

import com.google.common.io.Files;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Getter
public class Tower implements Iterable<Tile>, Cloneable {

    private final Tile[][] matrix;
    private final int height;
    private final int width;

    public static Tower loadFromTextFile(String file) {
        System.out.println("Loading tower from text file " + file);

        try {
            return loadFromLines(Files.readLines(new File(file), StandardCharsets.US_ASCII));
        } catch (IOException e) {
            throw new IllegalStateException(format("Failed to load tower from %s: %s", file, e.getMessage()), e);
        }
    }

    public static Tower loadFromImage(String file) {
        System.out.println("Loading tower from image " + file);

        try {
            return loadFromLines(Ocr.loadFromImage(file));
        } catch (Exception e) {
            throw new IllegalStateException(format("Failed to load tower from %s: %s", file, e.getMessage()), e);
        }
    }

    public static Tower loadFromLines(List<String> lines) {
        int width = -1;
        for (String line : lines) {
            if (width == -1)
                width = line.length();
            else if (width != line.length())
                throw new IllegalArgumentException("all lines must be the same length: " + lines.stream().collect(Collectors.joining("\n")));
        }

        int height = lines.size();

        Tile[][] matrix = new Tile[height][width];

        for (int h = 0; h < height; h++) {
            String line = lines.get(h);
            for (int w = 0; w < width; w++) {
                matrix[h][w] = new Tile(Letter.from(line.charAt(w)), h, w);
            }
        }

        return new Tower(matrix);
    }

    private Tower(Tile[][] matrix) {
        this.matrix = matrix;
        this.height = matrix.length;
        this.width = matrix[0].length;
    }

    public Tile getTile(int x, int y) {
        if (y < 0 || y >= height || x < 0 || x >= width)
            return null;
        return matrix[y][x];
    }

    public Tile getAdjacent(Tile tile, Adjacent adjacent) {
        int x = tile.getX() + adjacent.getX();
        int y = tile.getY() + adjacent.getY();
        return getTile(x, y);
    }

    public Iterable<Tile> adjacents(final Tile tile) {
        return () -> new Iterator<>() {
            private int i = 0;

            public Tile next() {
                return Tower.this.getAdjacent(tile, Adjacent.values()[i++]);
            }

            public boolean hasNext() {
                return i < Adjacent.values().length;
            }
        };
    }

    private void setTile(int x, int y, Letter letter) {
        matrix[y][x] = new Tile(letter, y, x);
    }

    public Iterator<Tile> iterator() {
        return new Iterator<>() {
            private int y = 0;
            private int x = 0;

            public Tile next() {
                if (!hasNext())
                    return null;

                Tile tile = matrix[y][x];
                if (x == width - 1) {
                    x = 0;
                    y++;
                } else {
                    x++;
                }

                return tile;
            }

            public boolean hasNext() {
                return y < height;
            }
        };
    }

    public String toDisplay() {
        StringBuilder sb = new StringBuilder();
        sb.append("  ");

        for (int x = 0; x < width; x++) {
            sb.append(x + 1);
        }

        sb.append("\n");

        for (int y = 0; y < height; y++) {
            if (y < 9)
                sb.append(" ");
            sb.append(y + 1);
            for (int x = 0; x < width; x++) {
                char c = Optional.ofNullable(this.getTile(x, y))
                        .map(Tile::getLetter)
                        .map(Letter::getLetter)
                        .orElse(Letter.EMPTY.getLetter());
                sb.append(c);
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    public Tower remove(Path path) {
        Tower tower = this.clone();
        
        int height = tower.getHeight();
        int width = tower.getWidth();

        path.usedTiles().forEach(tile -> tower.setTile(tile.getX(), tile.getY(), Letter.EMPTY));

        for (int y = height - 1; y >= 0; y--) {
            for (int x = 0; x < width; x++) {
                Tile emptyTile = tower.getTile(x, y);
                if (!emptyTile.isEmpty())
                    continue;

                for (int replacementY = y; replacementY >= 0; replacementY--) {
                    Tile replacementTile = tower.getTile(x, replacementY);
                    if (replacementTile.isEmpty())
                        continue;

                    tower.setTile(x, y, replacementTile.getLetter());
                    tower.setTile(x, replacementY, Letter.EMPTY);
//                    System.out.printf("Moved %s from [%s][%s] to [%s][%s]:%n", 
//                            replacementTile.getLetter(),
//                            replacementY+1, x+1,
//                            y+1, x+1);
                    //System.out.println(this.toDisplay());
                    break;
                }
            }
        }
        
        return tower;
    }

    @Override
    public Tower clone() {
        Tile[][] clonedMatrix = Arrays.stream(matrix)
                .map(Tile[]::clone)
                .toArray(Tile[][]::new);
        return new Tower(clonedMatrix);
    }
}