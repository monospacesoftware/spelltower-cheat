package com.monospace;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class SpellCheat {

    private static final int TOWER_SEARCH_THREADS = 5;
    private static final int SEQUENCE_SEARCH_THREADS = 4;
    private static final int SEARCH_WIDTH = 3;

    public static void main(String args[]) {
        new SpellCheat(args[0], args[1]);
    }

    public SpellCheat(String dictionaryFile, String towerFile) {
        Dictionary dictionary = Dictionary.loadFromFile(dictionaryFile);
        Tower tower = Tower.loadFromImage(towerFile);

        System.out.println("Starting Tower:");
        System.out.println(tower.toDisplay());

        System.out.println("Searching...\n");

        ThreadPoolExecutor towerSearchThreadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(TOWER_SEARCH_THREADS);
        ThreadPoolExecutor sequenceSearchThreadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(SEQUENCE_SEARCH_THREADS);
        try {
            TowerSearcher towerSearcher = new TowerSearcher(dictionary, towerSearchThreadPool);
            SequenceSearcher sequenceSearcher = new SequenceSearcher(towerSearcher, sequenceSearchThreadPool, SEARCH_WIDTH);
            PathSequence best = sequenceSearcher.findBestPathSequence(tower);
            System.out.println("Best result:\n");
            System.out.println(best.toDisplay());
            System.out.println(best.getValue() + " total points");
        } finally {
            towerSearchThreadPool.shutdown();
            sequenceSearchThreadPool.shutdown();
        }
    }

}
