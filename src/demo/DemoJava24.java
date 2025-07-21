package demo;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Gatherer;
import java.util.stream.Gatherers;
import java.util.stream.Stream;

/**
 * DemoJava24 - Demonstrates the new Stream Gatherers feature in Java 24.
 * 
 * Stream Gatherers provide custom intermediate operations for streams,
 * allowing developers to transform data in ways not easily achievable
 * with existing built-in intermediate operations.
 */
public class DemoJava24 {

    public static void main(String[] args) {
        demoStreamGatherers();
    }

    /**
     * Main demonstration method that showcases all Stream Gatherer types.
     * Calls individual demo methods for each gatherer type.
     */
    public static void demoStreamGatherers() {
        System.out.println("=== Demo Stream Gatherers - Java 24 ===\n");

        demoStreamGathererWindowFixed();
        demoStreamGathererWindowSliding();
        demoStreamGathererFold();
        demoStreamGathererMapConcurrent();
        demoStreamGathererScan();
        demoCustomGatherer();
    }

    /**
     * Demonstrates the windowFixed gatherer.
     * 
     * Purpose: Groups input elements into fixed-size lists (windows).
     * Use Case: Batch processing, pagination, chunking data for parallel processing.
     * 
     * Example: Processing sensor readings in batches of 3 for analysis.
     */
    public static void demoStreamGathererWindowFixed() {
        System.out.println("--- Demo Window Fixed Gatherer ---");
        System.out.println("Purpose: Groups elements into fixed-size windows");
        System.out.println("Use Case: Batch processing, data chunking");

        List<List<Integer>> result = Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                .gather(Gatherers.windowFixed(3))
                .toList();

        System.out.println("Input: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]");
        System.out.println("Window Fixed (size=3): " + result);
        System.out.println("Note: Last window may contain fewer elements\n");
    }

    /**
     * Demonstrates the windowSliding gatherer.
     * 
     * Purpose: Creates overlapping windows by sliding across the input stream.
     * Use Case: Moving averages, trend analysis, pattern detection in time series.
     * 
     * Example: Calculating 3-day moving averages for stock prices.
     */
    public static void demoStreamGathererWindowSliding() {
        System.out.println("--- Demo Window Sliding Gatherer ---");
        System.out.println("Purpose: Creates overlapping sliding windows");
        System.out.println("Use Case: Moving averages, trend analysis, pattern detection");

        List<List<Integer>> result = Stream.of(1, 2, 3, 4, 5, 6)
                .gather(Gatherers.windowSliding(3))
                .toList();

        System.out.println("Input: [1, 2, 3, 4, 5, 6]");
        System.out.println("Window Sliding (size=3): " + result);
        System.out.println("Note: Each window overlaps with the previous one\n");
    }

    /**
     * Demonstrates the fold gatherer.
     * 
     * Purpose: Constructs an aggregate incrementally and emits it when no more input exists.
     * Use Case: Accumulating values, calculating totals, building complex objects from stream elements.
     * 
     * Example: Calculating running totals, concatenating strings, building summary objects.
     */
    public static void demoStreamGathererFold() {
        System.out.println("--- Demo Fold Gatherer ---");
        System.out.println("Purpose: Incrementally builds an aggregate value");
        System.out.println("Use Case: Accumulating totals, building complex objects");

        Integer sum = Stream.of(1, 2, 3, 4, 5)
                .gather(Gatherers.fold(() -> 0, Integer::sum))
                .findFirst().orElse(0);

        String concatenated = Stream.of("Hello", " ", "World", "!")
                .gather(Gatherers.fold(() -> "", String::concat))
                .findFirst().orElse("");

        System.out.println("Input numbers: [1, 2, 3, 4, 5]");
        System.out.println("Fold (sum): " + sum);
        System.out.println("Input strings: [\"Hello\", \" \", \"World\", \"!\"]");
        System.out.println("Fold (concat): \"" + concatenated + "\"\n");
    }

    /**
     * Demonstrates the mapConcurrent gatherer.
     * 
     * Purpose: Applies a function to each element concurrently with a specified concurrency limit.
     * Use Case: I/O operations, web service calls, CPU-intensive transformations that can benefit from parallelism.
     * 
     * Example: Making concurrent API calls, parallel image processing, concurrent database queries.
     */
    public static void demoStreamGathererMapConcurrent() {
        System.out.println("--- Demo Map Concurrent Gatherer ---");
        System.out.println("Purpose: Applies function concurrently with limited parallelism");
        System.out.println("Use Case: I/O operations, web calls, CPU-intensive tasks");

        // Simulate a time-consuming operation
        List<Integer> result = Stream.of(1, 2, 3, 4, 5)
                .gather(Gatherers.mapConcurrent(2, x -> {
                    // Simulate processing time
                    try { Thread.sleep(100); } catch (InterruptedException e) {}
                    return x * x;
                }))
                .toList();

        System.out.println("Input: [1, 2, 3, 4, 5]");
        System.out.println("Map Concurrent (x -> x*x, concurrency=2): " + result);
        System.out.println("Note: Processing happens concurrently with max 2 threads\n");
    }

    /**
     * Demonstrates the scan gatherer.
     * 
     * Purpose: Applies a function to current state and element to produce next element (running operation).
     * Use Case: Running totals, cumulative statistics, state machines, progressive calculations.
     * 
     * Example: Running sum, cumulative product, progressive averages, Fibonacci sequence.
     */
    public static void demoStreamGathererScan() {
        System.out.println("--- Demo Scan Gatherer ---");
        System.out.println("Purpose: Produces running/cumulative results");
        System.out.println("Use Case: Running totals, cumulative statistics, progressive calculations");

        // Running sum
        List<Integer> runningSum = Stream.of(1, 2, 3, 4, 5)
                .gather(Gatherers.scan(() -> 0, Integer::sum))
                .toList();

        // Running product
        List<Integer> runningProduct = Stream.of(1, 2, 3, 4)
                .gather(Gatherers.scan(() -> 1, (a, b) -> a * b))
                .toList();

        System.out.println("Input: [1, 2, 3, 4, 5]");
        System.out.println("Scan (running sum): " + runningSum);
        System.out.println("Input: [1, 2, 3, 4]");
        System.out.println("Scan (running product): " + runningProduct);
        System.out.println("Note: Each element is the cumulative result up to that point\n");
    }

    /**
     * Demonstrates a custom gatherer implementation.
     * 
     * Purpose: Shows how to create custom intermediate operations for specific business logic.
     * Use Case: Domain-specific transformations, complex filtering/mapping combinations.
     * 
     * Example: Custom business rules, specialized data transformations, domain-specific operations.
     */
    public static void demoCustomGatherer() {
        System.out.println("--- Demo Custom Gatherer ---");
        System.out.println("Purpose: Custom intermediate operations for specific business logic");
        System.out.println("Use Case: Domain-specific transformations, specialized operations");

        // Custom gatherer: distinctBy - distinct elements based on a key function
        List<String> words = List.of("cat", "dog", "rat", "elephant", "mouse", "tiger");
        List<String> distinctByLength = words.stream()
                .gather(distinctBy(String::length))
                .toList();

        // Custom fixed window gatherer
        List<List<Integer>> customWindows = Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9)
                .gather(customFixedWindow(3))
                .toList();

        System.out.println("Input words: " + words);
        System.out.println("Distinct by length: " + distinctByLength);
        System.out.println("Custom Fixed Window (size=3): " + customWindows);
        System.out.println("Note: Custom gatherers enable domain-specific operations\n");
    }

    /**
     * Custom gatherer that keeps only the first element for each unique key.
     * Similar to SQL's DISTINCT ON functionality.
     */
    static <T, K> Gatherer<T, ?, T> distinctBy(java.util.function.Function<T, K> keyExtractor) {
        return Gatherer.ofSequential(
                java.util.HashSet::new,
                Gatherer.Integrator.ofGreedy((seen, element, downstream) -> {
                    K key = keyExtractor.apply(element);
                    if (seen.add(key)) {
                        return downstream.push(element);
                    }
                    return true; // Continue processing even if not emitting
                })
        );
    }

    /**
     * Custom implementation of fixed window gatherer for demonstration.
     * Groups elements into fixed-size lists.
     */
    static <T> Gatherer<T, ?, List<T>> customFixedWindow(int windowSize) {
        if (windowSize < 1) {
            throw new IllegalArgumentException("Window size must be positive");
        }

        return Gatherer.ofSequential(
                () -> new java.util.ArrayList<T>(windowSize),
                Gatherer.Integrator.ofGreedy((window, element, downstream) -> {
                    window.add(element);
                    if (window.size() < windowSize) {
                        return true; // Continue collecting elements
                    }
                    // Window is full, emit it and start a new one
                    var result = new java.util.ArrayList<T>(window);
                    window.clear();
                    return downstream.push(result);
                }),
                (window, downstream) -> {
                    // Emit remaining elements if any
                    if (!downstream.isRejecting() && !window.isEmpty()) {
                        downstream.push(new java.util.ArrayList<T>(window));
                        window.clear();
                    }
                }
        );
    }
}