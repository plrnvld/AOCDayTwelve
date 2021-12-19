import java.io.*;
import java.util.*;
import java.util.stream.*;
import java.nio.file.*;

class Main {
    public static void main(String[] args) {
        List<List<String>> segments = new LinkedList<>();
    
        try (Stream<String> stream = Files.lines(Paths.get("Input.txt"))) {
            stream.forEach(line -> { 
                String[] parts = line.split("-"); 
                segments.add(Arrays.asList(parts[0], parts[1])); 
            });
        }
        catch (IOException ex) {
            System.out.println("Exception: " + ex);
        }

        Network network = new Network(segments);
        List<List<String>> allPaths = network.getAllPaths();

        System.out.println("Number of paths: " + allPaths.size());
    }
}

class Network {
    Map<String, List<String>> map;

    public Network(List<List<String>> segments) {
        map = new HashMap<String, List<String>>();

        for (List<String> segment : segments) {
            String from = segment.get(0);
            String to = segment.get(1);

            upsertSegment(from, to);
        }
    }

    void upsertSegment(String from, String to) {
        upsertOneSide(from, to);
        upsertOneSide(to, from);
    }

    void upsertOneSide(String from, String to) {
        if (map.containsKey(from) && !map.get(from).contains(to)) {
            map.get(from).add(to);            
        } else {
            List<String> destinations = new LinkedList<String>();
            destinations.add(to);
            map.put(from, destinations);
        }
    }

    public List<String> getPossibilities(List<String> pathSoFar) {
        String last = getLast(pathSoFar);

        List<String> all = map.get(last);

        return all.stream().filter(next -> isTraversable(next, pathSoFar)).collect(Collectors.toList());
    }

    boolean isTraversable(String next, List<String> pathSoFar) {
        boolean secondSmallVisit = (isSmall(next) && pathSoFar.contains(next));

        return !secondSmallVisit;
    }

    boolean isSmall(String cave) {
        return cave.toLowerCase().equals(cave);
    }

    List<List<String>> getContinuations(List<String> pathSoFar) {
        List<String> possibilities = getPossibilities(pathSoFar);

        List<List<String>> continuations = new LinkedList<List<String>>();

        for (String next : possibilities) {
            List<String> longerPath = new LinkedList<String>(pathSoFar);
            longerPath.add(next);

            continuations.add(longerPath);
        }

        return continuations;
    }

    public List<List<String>> getAllPaths() {
        List<String> start = new LinkedList<String>();
        start.add("start");

        return getAllPathsFrom(start);
    }

    public List<List<String>> getAllPathsFrom(List<String> pathSoFar) {
        List<List<String>> allPaths = new LinkedList<List<String>>();

        List<List<String>> continuations = getContinuations(pathSoFar);

        for (List<String> continuation: continuations) {
            String last = getLast(continuation);

            if (last.equals("end")) {
                allPaths.add(continuation);
            } else {
                allPaths.addAll(getAllPathsFrom(continuation));
            }
        }    

        return allPaths;
    }

    public <T> T getLast(List<T> list) {   
        return list.get(list.size() - 1);
    }
}