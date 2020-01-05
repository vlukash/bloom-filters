package org.volodymyr;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.volodymyr.filters.BloomFilter;

/**
 * Tests for the Spell Checker implementation.
 */
public class SpellCheckerTest  {
    private static final String UNIX_DICTIONARY_FILE_PATH = "/usr/share/dict/words";

    private SpellChecker spellChecker;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void shouldInitWithUnixDictionaryAndContainWord() throws Exception {
        spellChecker = new SpellChecker();
        spellChecker.init(UNIX_DICTIONARY_FILE_PATH);

        boolean actual = spellChecker.isPotentialWord("filter");

        assertTrue(actual);
    }
    @Test
    public void shouldInitWithUnixDictionaryAndDoNotContainWord() throws Exception {
        spellChecker = new SpellChecker();
        spellChecker.init(UNIX_DICTIONARY_FILE_PATH);

        boolean actual = spellChecker.isPotentialWord("asdf");

        assertFalse(actual);
    }

    @Test
    public void shouldThrowExceptionWhenInvalidFilePath() throws Exception {
        thrown.expectMessage("Can't read the dictionary file: some_path");
        thrown.expect(IllegalArgumentException.class);

        spellChecker = new SpellChecker();
        spellChecker.init("some_path");
    }
}
