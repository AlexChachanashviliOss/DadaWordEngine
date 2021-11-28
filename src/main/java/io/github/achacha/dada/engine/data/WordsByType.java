package io.github.achacha.dada.engine.data;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import io.github.achacha.dada.engine.phonemix.PhoneticTransformer;
import io.github.achacha.dada.engine.phonemix.PhoneticTransformerBuilder;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Given a type will contain a set of wordsData of that type and ability to load them from file
 */
public class WordsByType<T extends Word> {
    private static final Logger LOGGER = LogManager.getLogger(WordsByType.class);
    public static final String RESOURCE_DATA_DEFAULT = "resource:/data/default/";

    /**
     * Path to the words data file
     */
    protected String resourcePath;

    /**
     * Type of the word
     */
    protected final Word.Type type;

    /**
     * Word based objects with full metadata
     */
    protected ArrayList<T> wordsData = new ArrayList<>();

    /**
     * Map of base word to Word
     */
    protected Map<String, T> wordsByBaseWord = new HashMap<>();

    /**
     * Map all forms of the word to Word objects
     */
    protected Map<String, SavedWord> wordsByText = new HashMap<>();


    /**
     * Phonetic transformer
     */
    protected PhoneticTransformer xformer;

    /**
     * Multimap of phonetic to word
     */
    protected Multimap<String, SavedWord> wordsByPhonetic = LinkedListMultimap.create();

    /**
     * Phonetic transformer
     */
    protected PhoneticTransformer xformerReverse;

    /**
     * Last character phoneme mapped to Multimap of phonetic to word
     */
    protected Map<Character, Multimap<String, SavedWord>> wordBucketsByReversePhonetic = new HashMap<>();

    /**
     * Set to true if data contained duplicates, used for testings
     */
    private boolean duplicateFound = false;

    /**
     * Empty set
     */
    private static final WordsByType<Word> EMPTY = new WordsByType<>(Word.Type.Unknown, ImmutableList.of());

    /**
     * Constant list of provided words
     *
     * @param type        Word.Type
     * @param listOfWords Collection of text to parse as type provided
     */
    public WordsByType(Word.Type type, Collection<String> listOfWords) {
        this.xformer = PhoneticTransformerBuilder.getDefaultForward();
        this.xformerReverse = PhoneticTransformerBuilder.getDefaultReverse();

        this.resourcePath = null;
        this.type = type;
        listOfWords.forEach(w -> {
            T word = T.parse(type, w);
            addWord(word);
        });
    }

    /**
     * Initialize the class before loading and parsing data
     *
     * @param baseResourcePath Base resource path
     */
    public void init(String baseResourcePath) {
        Properties properties = new Properties();
        try {
            InputStream is = getConfigStream(baseResourcePath);
            if (is != null) {
                properties.load(is);
                String xformerTypeForwardName = properties.getProperty("phoneticTransformerForwardType", PhoneticTransformerBuilder.TransformerType.PhonemixCompacting.name());
                String xformerTypeReverseName = properties.getProperty("phoneticTransformerReverseType", PhoneticTransformerBuilder.TransformerType.PhonemixCompacting.name());

                LOGGER.info("Using forward phonetic transformer: "+xformerTypeForwardName);
                LOGGER.info("Using reverse phonetic transformer: "+xformerTypeReverseName);
                this.xformer = PhoneticTransformerBuilder.builder()
                        .withTransformer(PhoneticTransformerBuilder.TransformerType.valueOf(xformerTypeForwardName))
                        .build();
                this.xformerReverse = PhoneticTransformerBuilder.builder()
                        .withTransformer(PhoneticTransformerBuilder.TransformerType.valueOf(xformerTypeReverseName))
                        .withReverse().build();
            } else {
                this.xformer = PhoneticTransformerBuilder.getDefaultForward();
                this.xformerReverse = PhoneticTransformerBuilder.getDefaultReverse();
            }
        } catch (Exception e) {
            LOGGER.error("Error reading config.properties, using defaults", e);
            this.xformer = PhoneticTransformerBuilder.getDefaultForward();
            this.xformerReverse = PhoneticTransformerBuilder.getDefaultReverse();
        }
    }

    /**
     * Create a set of data.words based on CSV file in resourcePath
     *
     * @param type             Word.Type
     * @param baseResourcePath String base resource path with trailing /
     */
    public WordsByType(Word.Type type, String baseResourcePath) {
        this.resourcePath = baseResourcePath + type.getTypeName() + ".csv";
        this.type = type;

        init(baseResourcePath);

        try {
            try (InputStream is = getStreamFromPath(type, resourcePath)) {
                if (is != null) {
                    LineIterator it = IOUtils.lineIterator(is, Charset.defaultCharset());

                    // Parse lines
                    Set<T> uniqueCheck = new HashSet<>();
                    while (it.hasNext()) {
                        String line = it.nextLine().toLowerCase();
                        if (line.startsWith("#")) {
                            LOGGER.debug("Skipping comment: {}", line);
                            continue;
                        }

                        // Parse non-empty line
                        if (!line.isEmpty()) {
                            T word = T.parse(type, line);
                            if (uniqueCheck.contains(word)) {
                                // Check all forms for true duplicate, some words have identical base form but difference is seen in other forms
                                // e.g. bear - to have something, to give birth to something
                                LOGGER.error("Duplicate word found, skipping, word={} in resourcePath={} with line={}", word, resourcePath, line);
                                duplicateFound = true;
                            } else {
                                addWord(word);
                                uniqueCheck.add(word);
                            }
                        }
                    }
                } else
                    throw new IOException("Failed to find resource type=" + type + " path=" + resourcePath);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to read data for: " + resourcePath, e);
        }

        // Must have at least 1 word of a given type
        if (wordsData.isEmpty()) {
            throw new RuntimeException("Did not find any words for type=" + type + " at resourcePath=" + resourcePath);
        }
        LOGGER.debug("Loaded: {}", this);
    }

    /**
     * Open an input stream from path
     * <p>
     * FORMAT:
     * resource:/data/default/nouns.csv   - opens resource from /data/default/nouns.csv
     * data/custom/nouns.csv              - opens file with relative path
     *
     * @param type Word.Type
     * @param path Path of the file to open (with optional resource: for resource files)
     * @return InputStream or null if unable to open stream
     * @throws FileNotFoundException when physical file specified is not found
     */
    @Nullable
    private InputStream getStreamFromPath(Word.Type type, String path) throws FileNotFoundException {
        if (!path.startsWith("resource:")) {
            // Assume physical file path
            LOGGER.debug("Opening file input stream at path={}", path);

            File file = new File(path);
            if (file.canRead())
                return new FileInputStream(file);
            else {
                path = RESOURCE_DATA_DEFAULT + type.getTypeName() + ".csv";
                resourcePath = path;
                return getClass().getResourceAsStream(path.substring(9));
            }
        } else {
            // Default is relative stream from resource
            LOGGER.debug("Opening resource input stream at path={}", path);
            InputStream is = getClass().getResourceAsStream(path.substring(9));
            if (is == null) {
                path = RESOURCE_DATA_DEFAULT + type.getTypeName() + ".csv";
                LOGGER.debug("Failed to locate resource at path={}, falling back to default: {}", resourcePath, path);
                resourcePath = path;
                return getClass().getResourceAsStream(path.substring(9));
            } else {
                return is;
            }
        }

    }

    /**
     * Try to open as {@link InputStream} config.properties in words directory or use default
     *
     * @param baseResourcePath base resource path which may have overridden config
     * @return {@link InputStream} or null if not found
     * @throws FileNotFoundException file not found
     */
    private InputStream getConfigStream(String baseResourcePath) throws FileNotFoundException {
        InputStream is = null;
        if (!baseResourcePath.startsWith("resource:")) {
            // Physical file
            File file = new File(baseResourcePath + "config.properties");
            if (file.canRead()) {
                return new FileInputStream(file);
            }
        } else {
            // Resource file
            is = getClass().getResourceAsStream(baseResourcePath.substring(9) + "config.properties");
            if (is == null) {
                // Try default location
                is = getClass().getResourceAsStream("/data/default/config.properties");
            }
            if (is == null) {
                LOGGER.debug("Unable to find /data/default/config.properties, using defaults");
            }
        }
        return is;
    }

    /**
     * Add word to collection
     *
     * @param word Word
     */
    protected void addWord(T word) {
        wordsData.add(word);

        wordsByBaseWord.put(word.getWordString(), word);

        word.getAllForms().forEach(pair -> {
            // Word mapped to text
            String formOfWord = pair.getRight();
            String formName = pair.getLeft();
            wordsByText.put(formOfWord, new SavedWord(word, formName));  // Save the Word and it's form instance

            // Word mapped to phonetic
            String phonetic = xformer.transform(formOfWord);
            if (phonetic.length() > 0) {
                wordsByPhonetic.put(phonetic, new SavedWord(word, formOfWord));
            }

            // Word mapped to withReverse phonetic
            String reversePhonetic = xformerReverse.transform(formOfWord);
            if (reversePhonetic.length() > 0) {
                Character last = reversePhonetic.charAt(0);
                Multimap<String, SavedWord> bucket = wordBucketsByReversePhonetic.computeIfAbsent(last, LinkedListMultimap::create);
                bucket.put(reversePhonetic, new SavedWord(word, formOfWord));
            }
        });
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public Word.Type getType() {
        return type;
    }

    public List<T> getWordsData() {
        return wordsData;
    }

    /**
     * @param <T> extends Word
     * @return Empty set of words of type Unknown
     */
    @SuppressWarnings("unchecked")
    public static <T extends Word> WordsByType<T> empty() {
        return (WordsByType<T>) EMPTY;
    }

    /**
     * Check all word forms to find Word
     *
     * @param text word text (some form of the word)
     * @return Word or null
     */
    @Nullable
    public SavedWord getWordByText(String text) {
        return wordsByText.get(text);
    }

    /**
     * Get Word object by base word, does not check other forms
     *
     * @param base String
     * @return Word or null if not found
     */
    @Nullable
    public T getWordByBase(String base) {
        return wordsByBaseWord.get(base);
    }

    /**
     * @return XFormer used for phonetic forward form
     */
    public PhoneticTransformer getXformer() {
        return xformer;
    }

    /**
     * @return Multimap of phonetic form to Word list
     */
    public Multimap<String, SavedWord> getWordsByPhonetic() {
        return wordsByPhonetic;
    }

    /**
     * @return XFormer used for phonetic withReverse form
     */
    public PhoneticTransformer getXformerReverse() {
        return xformerReverse;
    }

    /**
     * @return Last character mapped to Multimap of phonetic form to Word list
     */
    public Map<Character, Multimap<String, SavedWord>> getWordBucketsByReversePhonetic() {
        return wordBucketsByReversePhonetic;
    }

    /**
     * Find all words that rhyme provided word based on just the last phonem
     *
     * @param source word
     * @return List of SavedWord containing Word and form that rhymes
     */
    public List<SavedWord> findRhymes(String source) {
        String reversePhonetic = getXformerReverse().transform(source);
        Multimap<String, SavedWord> bucket = wordBucketsByReversePhonetic.get(reversePhonetic.charAt(0));
        if (bucket != null) {
            // Match last phonem
            return bucket.entries().stream()
                    .filter(
                            entry -> entry.getKey().charAt(0) == reversePhonetic.charAt(0)
                    )
                    .map(Map.Entry::getValue)
                    .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        } else
            return Collections.emptyList();

    }

    /**
     * @return One random word
     */
    public Word getRandomWord() {
        return wordsData.get(RandomUtils.nextInt(0, wordsData.size()));
    }

    /**
     * Duplicates are ignored, this is used in unit testing
     *
     * @return true if duplicate words were found during parsing of the data
     */
    public boolean isDuplicateFound() {
        return duplicateFound;
    }

    @Override
    public String toString() {
        return "WordsByType{" +
                "resourcePath='" + resourcePath + '\'' +
                ", type=" + type +
                ", size=" + wordsData.size() +
                '}';
    }

    /**
     * @return Header comment for each specific word type used in the CSV file as column header
     */
    protected String getOutputHeader() {
        switch (type) {
            case Noun:
                return "#singular,plural";
            case Adjective:
                return "#Adjective,Comparative(-er),Superlative(-est)";
            case Verb:
                return "#Verb,Past,PastParticiple,Singular,Present";
            case Adverb:
                return "#Adverb";
            case Pronoun:
                return "#Pronoun,PER,SUB,OBJ,POS,DEM,INT,REL,REF,REC,IND,SIN,PLU";
            case Conjunction:
                return "#Conjunction";
            case Preposition:
                return "#Preposition";
            default:
                throw new RuntimeException("Unknown word type");
        }
    }

    /**
     * Write data to file
     *
     * @param writer BufferedWriter
     * @throws IOException if unable to write file
     */
    public void writeWords(BufferedWriter writer) throws IOException {
        writer.write(getOutputHeader());
        writer.write("\n");

        List<Word> sorted = wordsData.stream().sorted(Word::compareToForSave).collect(Collectors.toList());
        for (Word word : sorted) {
            writer.write(word.toCsv());
            writer.write("\n");
        }
        writer.flush();
    }
}
