package org.volodymyr.filters;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests for the Bloom Filter implementation.
 */
public class BloomFilterTest {
    private static final String TEST_WORD1 = "test";
    private static final String TEST_WORD2 = "filter";
    private static final List<String> TEST_STRINGS = Arrays.asList("Some","test","string","to","test","bloom","filter");

    private BloomFilter bloomFilter;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void shouldReturnFalseForAnEmptyFilter() throws Exception {
        bloomFilter = new BloomFilter(100, 4);

        boolean actual = bloomFilter.mightContain(TEST_WORD1);

        assertFalse(actual);
    }

    @Test
    public void shouldReturnTrueForASingleItem() throws Exception {
        bloomFilter = new BloomFilter(100, 4);
        bloomFilter.put(TEST_WORD1);

        boolean actual = bloomFilter.mightContain(TEST_WORD1);

        assertTrue(actual);
    }

    @Test
    public void shouldReturnFalseForNonEmptyFilterNoItemsAdded() throws Exception {
        bloomFilter = new BloomFilter(100, 4);
        bloomFilter.put(TEST_WORD2);

        boolean actual = bloomFilter.mightContain(TEST_WORD1);

        assertFalse(actual);
    }

    @Test
    public void shouldReturnTrueForFiterWIthMultipleItemsAdded() throws Exception {
        bloomFilter = new BloomFilter(100, 4);
        TEST_STRINGS.stream().forEach(e -> bloomFilter.put(e));

        boolean actual = bloomFilter.mightContain(TEST_WORD2);

        assertTrue(actual);
    }

    @Test
    public void shouldReturnFalseAfterFilterReset() throws Exception {
        bloomFilter = new BloomFilter(100, 4);
        TEST_STRINGS.stream().forEach(e -> bloomFilter.put(e));
        bloomFilter.clear();

        boolean actual = bloomFilter.mightContain(TEST_WORD2);

        assertFalse(actual);
    }

    @Test
    public void shouldThrowExceptionOnInvalidBitmapLen() throws Exception {
        thrown.expectMessage("Can not create a BloomFilter with bits length <= 0.");
        thrown.expect(IllegalArgumentException.class);

        bloomFilter = new BloomFilter(0, 4);
    }

    @Test
    public void shouldThrowExceptionOnInvalidHashFunctionsNumber() throws Exception {
        thrown.expectMessage("Can not create a BloomFilter, number of hashes should be in the range of 1..100.");
        thrown.expect(IllegalArgumentException.class);

        bloomFilter = new BloomFilter(100, 200);
    }

    @Test
    public void shouldThrowExceptionWhenPutNull() throws Exception {
        thrown.expectMessage("Data can not be null.");
        thrown.expect(IllegalArgumentException.class);

        bloomFilter = new BloomFilter(100, 4);
        bloomFilter.put(null);
    }

    @Test
    public void shouldThrowExceptionWhenQueryNull() throws Exception {
        thrown.expectMessage("Data can not be null.");
        thrown.expect(IllegalArgumentException.class);

        bloomFilter = new BloomFilter(100, 4);
        bloomFilter.put(TEST_WORD1);

        bloomFilter.mightContain(null);
    }
}