package org.volodymyr;

import org.volodymyr.filters.BloomFilter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * A simple spell checker that uses Bloom Filter as internal data structure.
 *
 */
public class SpellChecker
{
    private BloomFilter bloomFilter;

    /**
     * Creates an instance of SpellChecker.
     */
    public SpellChecker() {
        bloomFilter = new BloomFilter(1000000, 4);
    }

    /**
     * Reads a dictionary file and initializes the Bloom Filter with data.
     * File should contain one word per line.
     *
     * @param dictionaryFilePath path to the dictionary file (ex: "/usr/share/dict/words")
     */
    public void init(String dictionaryFilePath) {
        // feed the the dictionary file line by line into the Blooom Filter.
        try (Stream<String> stream = Files.lines(Paths.get(dictionaryFilePath))) {
            stream.forEach(word -> bloomFilter.put(word));
        } catch (IOException e) {
            throw new IllegalArgumentException("Can't read the dictionary file: " + e.getMessage(), e);
        }
    }

    /**
     * Checks if the word is in the dictionary.
     * Could return false positive, if too many, increase BloomFilter bitmap size.
     * Never returns true if word is not in the dictionary.
     *
     * @param word the search word that will be looked up in the Bloom Filter
     */
    public boolean isPotentialWord(String word) {
        return bloomFilter.mightContain(word);
    }
}
