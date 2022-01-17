package com.monospace;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SequenceSearcher {

    private final TowerSearcher towerSearcher;
    private final ThreadPoolExecutor threadPool;
    private final int width;

    public SequenceSearcher(TowerSearcher towerSearcher, ThreadPoolExecutor threadPool, int width) {
        this.towerSearcher = towerSearcher;
        this.threadPool = threadPool;
        this.width = width;
    }

    public PathSequence findBestPathSequence(Tower tower) {
        return search(tower, new PathSequence())
                .max(Comparator.naturalOrder())
                .get();
    }

    public Stream<PathSequence> search(Tower tower, PathSequence pathSequence) {
        // System.out.printf("Sequence Search (%s): %s%n", threadPool.getQueue().size(), pathSequence);
        List<Path> paths = towerSearcher.findBestPaths(tower, width);
        if (paths.isEmpty())
            return Stream.of(pathSequence);

        return paths.stream()
                .map(path -> CompletableFuture.supplyAsync(() -> {
                    PathSequence newPathSequence = pathSequence.with(path);
                    Tower newTower = tower.remove(path);
                    return search(newTower, newPathSequence);
                }, threadPool))
                .collect(Collectors.toList())
                .stream()
                .flatMap(CompletableFuture::join);
    }

}
