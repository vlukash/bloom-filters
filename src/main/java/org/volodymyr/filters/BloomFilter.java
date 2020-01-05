package org.volodymyr.filters;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.BitSet;

/**
 * Implementation of a simple Bloom Filter.
 *
 * Current implementation works only with String data type and uses 32bit hash function.
 * Max bits size is limited by Integer size, i.e. 2,147,483,647.
 * TODO:
 *      - add support fot generics
 *      - use a different data structure if large bitmap range needed, like AtomicLongArray
 *      - implement custom equals() and hashCode() for data-equality check
 *      - implement factory methods that would create BloomFilter from other data structures like arrays and sets
 *      - implement method that calculates an optimal hash functions number based on the bitmap size
 */
public class BloomFilter {
    /** The bit map of the BloomFilter. */
    private BitSet bitmap;
    /** MD5 hash function. */
    private MessageDigest md5;
    /** Number of hashes. */
    private int numberOfHashes;

    /**
     * Creates an instance of BloomFilter.
     *
     * @param bitsLength bitmap size (must be positive)
     * @param numberOfHashes number of hash functions used (must be positive, up to 100)
     */
    public BloomFilter(int bitsLength, int numberOfHashes) throws IllegalArgumentException {
        if (bitsLength <= 0) {
            throw new IllegalArgumentException("Can not create a BloomFilter with bits length <= 0.");
        }
        if (numberOfHashes <= 0 || numberOfHashes > 100) {
            throw new IllegalArgumentException(
                    "Can not create a BloomFilter, number of hashes should be in the range of 1..100.");
        }

        bitmap = new BitSet(bitsLength);
        this.numberOfHashes = numberOfHashes;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch(NoSuchAlgorithmException e) {
            throw new IllegalStateException("Unable to get MD5 function: " + e.getMessage(), e);
        }
    }

    /**
     * Puts an element into the current Bloom filter.
     *
     * @param data a string that will be hashed and stored in bitmap
     */
    public void put(String data) throws IllegalArgumentException {
        if (data == null) {
            throw new IllegalArgumentException("Data can not be null.");
        }
        int capacity = bitmap.size();
        long hash64 = getHash64(data);

        for (int i = 0; i < numberOfHashes; i++) {
            int hash = getCombinedHash32(hash64, i);
            bitmap.set(hash % capacity);
        }
    }

    /**
     * Returns true if the element might have been put in this Bloom filter.
     * Returns false if element definitely was not put in the filter.
     *
     * @param data a string that might be previously hashed and put into the bitmap
     */
    public boolean mightContain(String data) throws IllegalArgumentException {
        if (data == null) {
            throw new IllegalArgumentException("Data can not be null.");
        }
        int capacity = bitmap.size();
        long hash64 = getHash64(data);

        for (int i = 0; i < numberOfHashes; i++) {
            int hash = getCombinedHash32(hash64, i);
            if (!bitmap.get(hash % capacity)) {
                return false;
            }
        }
        return true;
    }

    /** Resets current Bloom filter to its initial state. */
    public void clear() {
        bitmap.clear();
    }

    /** Returns a 64bit hash as a lower part of the 128bit MD5 hash result. */
    private long getHash64(String data) {
        // md5 returns 16 bytes of hash, but we need only lower 32 bits
        byte[] md5Hash = md5.digest(data.getBytes());
        long hash = new BigInteger(md5Hash).longValue();
        return hash;
    }

    /**
     * Calculates i-th hash portion based on the 64bit MD5 hash.
     *
     * See "Less Hashing, Same Performance: Building a Better Bloom Filter"
     * for more details.
     * This approach needs only two 32bit hash functions.
     */
    private int getCombinedHash32(long hash64, int i) {
        int hash1 = (int) hash64;
        int hash2 = (int) (hash64 >>> 32);
        int combinedHash = hash1 + (i * hash2);
        // Flip all the bits if it's negative
        if (combinedHash < 0) {
            combinedHash = ~combinedHash;
        }
        return combinedHash;
    }
}