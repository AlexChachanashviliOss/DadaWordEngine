package io.github.achacha.dada.engine.data;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Main object that loads and holds all data.words by type
 */
public class WordData {
    private static final Logger LOGGER = LogManager.getLogger(WordData.class);

    private final String baseResourceDir;

    /**
     * Words to ignore
     *
     * NOTE: This does not affect loading of words it's just a Set of words that should be ignored
     *       during processing/parsing of sentence data
     */
    private final Set<String> ignore;

    /** Pronouns */
    private final Pronouns pronouns;

    /** Conjunctions */
    private final WordsByType<Conjunction> conjunctions;

    /** Nouns */
    private final WordsByType<Noun> nouns;

    /** Adjectives */
    private final WordsByType<Adjective> adjectives;

    /** Verbs */
    private final WordsByType<Verb> verbs;

    /** Adverbs */
    private final WordsByType<Adverb> adverbs;

    /** Prepositions */
    private final WordsByType<Preposition> prepositions;

    public WordData() {
        this(null);
    }

    /**
     * Load word data
     *
     * If resource: is specified will load from resource otherwise will try to open it as physical file
     *   resource:/data/default/ - opens resources at that base
     *   data/custom/            - opens files at that base
     *
     * @param baseResourceDir Base resource directory name in the current data directory, supports resource: for resource path streams
     *                        If null then resource:/data/default is used
     */
    public WordData(String baseResourceDir) {
        if (baseResourceDir == null) {
            baseResourceDir = "resource:/data/default";

            // Load default ignore words
            ignore = loadIgnoreWords(baseResourceDir+"/ignore.csv");
        }
        else {
            // Non-default provided, load default first and overlay with user-defined ignore words
            ignore = loadIgnoreWords("resource:/data/default/ignore.csv");
            ignore.addAll(loadIgnoreWords(baseResourceDir+"/ignore.csv"));
        }

        this.baseResourceDir = baseResourceDir + "/";

        pronouns = new Pronouns(this.baseResourceDir);
        conjunctions = new WordsByType<>(Word.Type.Conjunction, this.baseResourceDir);
        nouns = new WordsByType<>(Word.Type.Noun, this.baseResourceDir);
        adjectives = new WordsByType<>(Word.Type.Adjective, this.baseResourceDir);
        verbs = new WordsByType<>(Word.Type.Verb, this.baseResourceDir);
        adverbs = new WordsByType<>(Word.Type.Adverb, this.baseResourceDir);
        prepositions = new WordsByType<>(Word.Type.Preposition, this.baseResourceDir);
    }

    private Set<String> loadIgnoreWords(String resourcePath) {
        Set<String> ignoreWords = new HashSet<>();
        try {
            try (InputStream is = getStreamFromPath(resourcePath)) {
                if (is != null) {
                    LineIterator it = IOUtils.lineIterator(is, Charset.defaultCharset());

                    // Parse lines
                    while (it.hasNext()) {
                        String line = it.nextLine();
                        if (line.startsWith("#")) {
                            LOGGER.debug("Skipping comment: {}", line);
                            continue;
                        }

                        // Parse non-empty line
                        if (!line.isEmpty()) {
                            ignoreWords.add(StringUtils.strip(line));
                        }
                    }
                }
                else {
                    LOGGER.debug("No ignore file found: {}", resourcePath);
                }
            }
        } catch (IOException e) {
            LOGGER.error("Failed to read data for: " + resourcePath, e);
        }

        LOGGER.debug("Loaded ignore words: {}", ignoreWords);
        return ignoreWords;
    }

    /**
     * Open an input stream from path
     * <p>
     * FORMAT:
     * resource:/data/default/nouns.csv   - opens resource from /data/default/nouns.csv
     * data/custom/nouns.csv              - opens file with relative path
     *
     * @param path Path of the file to open (with optional resource: for resource files)
     * @return InputStream or null if unable to open stream
     * @throws FileNotFoundException when physical file specified is not found
     */
    @Nullable
    private InputStream getStreamFromPath(String path) throws FileNotFoundException {
        if (!path.startsWith("resource:")) {
            // Assume physical file path
            LOGGER.debug("Opening file input stream at path={}", path);

            File file = new File(path);
            if (file.canRead())
                return new FileInputStream(path);
            else {
                return null;
            }
        }
        else {
            // Dafault is relative stream from resource
            LOGGER.debug("Opening resource input stream at path={}", path);
            return getClass().getResourceAsStream(path.substring(9));
        }
    }

    public String getBaseResourceDir() {
        return baseResourceDir;
    }

    @Nonnull
    public Pronouns getPronouns() {
        return pronouns;
    }

    @Nonnull
    public WordsByType<Conjunction> getConjunctions() {
        return conjunctions;
    }

    @Nonnull
    public WordsByType<Noun> getNouns() {
        return nouns;
    }

    @Nonnull
    public WordsByType<Adjective> getAdjectives() {
        return adjectives;
    }

    @Nonnull
    public WordsByType<Verb> getVerbs() {
        return verbs;
    }

    @Nonnull
    public WordsByType<Adverb> getAdverbs() {
        return adverbs;
    }

    @Nonnull
    public WordsByType<Preposition> getPrepositions() {
        return prepositions;
    }

    @Nonnull
    public Set<String> getIgnore() {
        return ignore;
    }

    /**
     * Given some text (word) find any words that match it
     * @param text String MUST be in lower case
     * @return Optional SavedWord which contains Word and the form
     */
    @Nonnull
    public Optional<SavedWord> findFirstWordsByText(String text) {
        String findText = text.trim().toLowerCase();

        return getWordsByTypeStream()
                .flatMap(wbt->{
                            SavedWord w = wbt.getWordByText(findText);
                            return w == null ? Stream.empty() : Stream.of(w);
                })
                .findAny();
    }

    /**
     * List of words ordered as following: prepositions, conjunctions, pronouns, adverbs, adjectives, verbs, nouns
     * // TODO: May need better ordering and word selection
     * @return Stream of all WordsByType in this data set
     */
    @Nonnull
    public Stream<WordsByType> getWordsByTypeStream() {
        return Stream.of(prepositions, conjunctions, pronouns, adverbs, adjectives, verbs, nouns);
    }

    /**
     * WordsByType for specific type
     * @param type Word.Type
     * @return WordsByType
     */
    public WordsByType<? extends Word> getWordsByType(Word.Type type) {
        switch(type) {
            case Noun: return nouns;
            case Adjective: return adjectives;
            case Verb: return verbs;
            case Adverb: return adverbs;
            case Pronoun: return pronouns;
            case Conjunction: return conjunctions;
            case Preposition: return prepositions;
            case Unknown: return WordsByType.empty();

            default: throw new RuntimeException("Not a known type: "+type);
        }
    }

    /**
     * Get random word by type
     * @param type Word.Type
     * @return Word
     */
    public Word getRandomWordByType(Word.Type type) {
        return getWordsByType(type).getRandomWord();
    }
}
