package com.monospace;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class TowerSearcher {

    private final Dictionary dictionary;
    private final ThreadPoolExecutor threadPool;

    public TowerSearcher(Dictionary dictionary, ThreadPoolExecutor threadPool) {
        this.dictionary = dictionary;
        this.threadPool = threadPool;
    }

    public Path findBestPath(Tower tower) {
        return StreamSupport.stream(tower.spliterator(), false)
                .map(tile -> new PathSearcher(tower, tile))
                .map(pathSearcher -> CompletableFuture.supplyAsync(pathSearcher::findBestPath, threadPool))
                .collect(Collectors.toList())
                .stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                //.peek(path -> System.out.println("Best path thread found was " + path))
                .max(Comparator.reverseOrder())
                .orElse(null);
    }

    public List<Path> findBestPaths(Tower tower, int limit) {
        return StreamSupport.stream(tower.spliterator(), false)
                .map(tile -> new PathSearcher(tower, tile))
                .map(pathSearcher -> CompletableFuture.supplyAsync(pathSearcher::findAllPaths, threadPool))
                .collect(Collectors.toList())
                .stream()
                .map(CompletableFuture::join)
                .flatMap(List::stream)
                .filter(Objects::nonNull)
                .sorted()
                .limit(limit)
                .collect(Collectors.toList());
    }

    private class PathSearcher {
        private final Tower tower;
        private final Tile startingTile;
        private PriorityQueue<Path> paths = new PriorityQueue<>();

        private PathSearcher(Tower tower, Tile startingTile) {
            this.tower = tower;
            this.startingTile = startingTile;
        }

        private Path findBestPath() {
            search(new Path(tower), startingTile, dictionary.getRoot());
            if (paths.isEmpty())
                return null;
            return paths.iterator().next();
        }

        private List<Path> findAllPaths() {
            search(new Path(tower), startingTile, dictionary.getRoot());
            return paths.stream()
                    .collect(Collectors.toList());
        }

        private void search(Path path, Tile tile, Node parent) {
            if (tile == null || tile.isEmpty() || path.contains(tile))
                return;

            Letter letter = tile.getLetter();
            Node node = parent.get(letter);
            if (node == null)
                return;

            Path newPath = path.with(tile);
            if (node.isWord() && newPath.size() > 2) {
                paths.add(newPath);
            }

            if (node.hasChildren())
                tower.adjacents(tile).forEach(adjacent -> search(newPath, adjacent, node));
        }
    }
}