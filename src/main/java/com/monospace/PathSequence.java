package com.monospace;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
public class PathSequence implements Comparable<PathSequence> {
    
    @Getter
    private List<Path> paths = new ArrayList<>();
    
    private PathSequence(PathSequence from, Path with) {
        paths = new ArrayList<>(from.paths);
        paths.add(with);
    }
    
    public void addPath(Path path) {
        paths.add(path);
    }
    
    public PathSequence with(Path path) {
        return new PathSequence(this, path);
    }
    
    @Override
    public String toString() {
        return paths.stream()
                .map(path -> path.getWord())
                .collect(Collectors.joining(" -> "));
    }
    
    public int getValue() {
        return paths.stream()
                .mapToInt(Path::getValue)
                .sum();
    }

    @Override
    public int compareTo(PathSequence o) {
        return Integer.compare(this.getValue(), o.getValue());
    }
    
    public String toDisplay() {
        return paths.stream()
                .map(Path::toDisplay)
                .collect(Collectors.joining("\n"));
    }
}
