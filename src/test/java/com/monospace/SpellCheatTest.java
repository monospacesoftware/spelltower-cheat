package com.monospace;

import org.junit.AfterClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import static org.assertj.core.api.Assertions.*;

public class SpellCheatTest {
    
    private static ThreadPoolExecutor threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
    
    @AfterClass
    public static void shutdown() {
        threadPool.shutdown();
    }

    @Test
    public void testLoadDictionary() {
        Dictionary dictionary = Dictionary.loadFromFile("dictionary2.txt");
        assertThat(dictionary).isNotNull();
    }

    @Test
    public void testLoadTower() {
        Tower tower = Tower.loadFromTextFile("src/test/resources/input.txt");
        assertThat(tower).isNotNull();
        assertThat(tower.getWidth()).isEqualTo(9);
        assertThat(tower.getHeight()).isEqualTo(13);
    }

    @Test
    public void testFindBestPath() {
        Dictionary dictionary = Dictionary.loadFromFile("dictionary2.txt");
        Tower tower = Tower.loadFromTextFile("src/test/resources/serialized.txt");
        TowerSearcher searcher = new TowerSearcher(dictionary, threadPool);
        Path path = searcher.findBestPath(tower);
        System.out.println(path.toDisplay());
        assertThat(path.getWord()).isEqualTo("serialized");
        assertThat(path.getValue()).isEqualTo(700);
    }

    @Test
    public void testFindBestPaths() {
        Dictionary dictionary = Dictionary.loadFromFile("dictionary2.txt");
        Tower tower = Tower.loadFromTextFile("src/test/resources/serialized.txt");
        TowerSearcher searcher = new TowerSearcher(dictionary, threadPool);
        List<Path> paths = searcher.findBestPaths(tower, 10);
        assertThat(paths)
                .extractingResultOf("getWord")
                .containsExactly("serialized",
                        "serialize",
                        "acromicria",
                        "piercers",
                        "atomizers",
                        "atomized",
                        "tridermic",
                        "croziers",
                        "coalized",
                        "atomizer");
    }

    @Test
    public void testSearch() {
        Dictionary dictionary = Dictionary.loadFromFile("dictionary2.txt");
        Tower tower = Tower.loadFromTextFile("src/test/resources/serialized.txt");
        TowerSearcher searcher = new TowerSearcher(dictionary, threadPool);
        Path path = searcher.findBestPath(tower);
        System.out.println(path.toDisplay());
        assertThat(path.getWord()).isEqualTo("serialized");
        assertThat(path.getValue()).isEqualTo(700);
    }
    

    @Test
    public void testToDisplay() {
        Dictionary dictionary = Dictionary.loadFromFile("dictionary2.txt");
        Tower tower = Tower.loadFromTextFile("src/test/resources/input.txt");
        System.out.println(tower.toDisplay());
    }

    @Test
    public void testRemove() {
        Dictionary dictionary = Dictionary.loadFromFile("dictionary2.txt");
        Tower tower = Tower.loadFromTextFile("src/test/resources/remove.txt");
        TowerSearcher searcher = new TowerSearcher(dictionary, threadPool);
        tower = searchAndRemove(searcher, tower, "cylindric", 684);
        tower = searchAndRemove(searcher, tower, "condensator", 737);
        tower = searchAndRemove(searcher, tower, "semiopacous", 649);
        tower = searchAndRemove(searcher, tower, "asklepios", 360);
        tower = searchAndRemove(searcher, tower, "felon", 65);
        tower = searchAndRemove(searcher, tower, "bit", 21);
        tower = searchAndRemove(searcher, tower, "hes", 21);
        assertThat(searcher.findBestPath(tower)).isNull();
    }
    
    private Tower searchAndRemove(TowerSearcher searcher, Tower tower, String expectedWord, int expectedValue) {
        Path path = searcher.findBestPath(tower);
        
        System.out.println(path.toDisplay());
        assertThat(path.getWord()).isEqualTo(expectedWord);
        assertThat(path.getValue()).isEqualTo(expectedValue);

        System.out.println("Pre remove:");
        System.out.println(tower.toDisplay());

        tower = tower.remove(path);

        System.out.println("Post remove:");
        System.out.println(tower.toDisplay());
        
        return tower;
    }
    
    @Test
    public void removeTest() {
        Dictionary dictionary = Dictionary.loadFromFile("dictionary.txt");
        Tower tower1 = Tower.loadFromTextFile("src/test/resources/input.txt");
        TowerSearcher searcher = new TowerSearcher(dictionary, threadPool);
        Path path1 = searcher.findBestPath(tower1);
        System.out.println(path1.toDisplay());
        Tower tower2 = tower1.remove(path1);
        System.out.println("Tower1: \n" + tower1.toDisplay());
        System.out.println("Tower2: \n" + tower2.toDisplay());
    }

    @Test
    public void cloneTest2D() {
        String[][] matrix1 = {{"foo","bar"},{"baz","bip"}};
        String[][] matrix2 = Arrays.stream(matrix1)
                .map(String[]::clone)
                .toArray(String[][]::new);
        
        print(matrix1);
        print(matrix2);
        
        matrix2[1][1] = "ASDF";

        print(matrix1);
        print(matrix2);
    }
    
    private void print(String[][] matrix) {
        for(int x=0; x< matrix.length; x++) {
            for(int y=0; y<matrix[x].length; y++) {
                System.out.printf("%s,%s=%s%n", x, y, matrix[x][y]);
            }
        }
        System.out.println();
    }

}
