package io.github.achacha.dada.integration.tags;

import io.github.achacha.dada.engine.data.WordData;
import io.github.achacha.dada.engine.hyphen.HyphenData;

/**
 * GlobalData is needed when rendering words, however we can build a custom {@link io.github.achacha.dada.engine.render.RenderContext} with custom data
 * When we don't explicitly specify WordData or HyphenData, we lookup instance from here
 * <p>
 * Before Render engine comes up this needs to contain the WordData, HypenData, etc
 * You can preload it with specific data set using {@link #loadWordData(String)} and {@link #loadHyphenData(String)}
 * {@link #init()} will pre-load default data, otherwise default data is lazy-loaded
 * <p>
 * This is usually called when container is starting up and GlobalData.init() is enough to get good working data
 * However not required since it will lazy load
 */
public class GlobalData {
    /** Default word data path in resource directory, minimal set used as a fallback  */
    public static final String DEFAULT_WORDDATA_BASE_RESOURCE_PATH = "resource:/data/default";

    /** Dada words from 1992-2002 poetry/art project */
    public static final String DADA2002_WORDDATA_BASE_RESOURCE_PATH = "resource:/data/dada2002";

    /** Dada words from 2018 poetry/art project */
    public static final String DADA2018_WORDDATA_BASE_RESOURCE_PATH = "resource:/data/dada2018";

    /** Extended word list from 2018 rewrite */
    public static final String EXTENDED2018_WORDDATA_BASE_RESOURCE_PATH = "resource:/data/extended2018";

    /** Default hyphen data path is resource directory */
    public static final String DEFAULT_HYPHENDATA_BASE_RESOURCE_PATH = "resource:/data/hyphen";

    private static WordData instanceWordData;
    private static HyphenData instanceHypenData;

    /**
     * Initialize with default data (pre-load)
     * Can also call the individual load calls to customize data
     * Or let it lazy load default data as needed
     * @see #loadWordData(String)
     * @see #loadHyphenData(String)
     */
    public static void init() {
        loadDefaultWordData();
        loadDefaultHyphenData();
    }

    private static synchronized void loadDefaultWordData() {
        // Double gate locking to prevent multiple loads of default data
        if (instanceWordData == null)
            loadWordData(EXTENDED2018_WORDDATA_BASE_RESOURCE_PATH);
    }

    private static synchronized void loadDefaultHyphenData() {
        // Double gate locking to prevent multiple loads of default data
        if (instanceHypenData == null)
            loadHyphenData(DEFAULT_HYPHENDATA_BASE_RESOURCE_PATH);
    }

    /**
     * Load WordData from resource base path
     * @param baseResoureDir String (e.g.  resource:/data/extended2018)
     */
    public static void loadWordData(String baseResoureDir) {
        instanceWordData = new WordData(baseResoureDir);
    }

    public static void setWordData(WordData jspWordDataInstance) {
        GlobalData.instanceWordData = jspWordDataInstance;
    }

    public static void loadHyphenData(String baseResourcePath) {
        instanceHypenData = new HyphenData(baseResourcePath);
    }

    public static void setHypenData(HyphenData jspHypenDataInstance) {
        GlobalData.instanceHypenData = jspHypenDataInstance;
    }


    /**
     * Get WordData initialized via {@link #loadWordData(String)} or {@link #loadDefaultWordData()}
     * If not initialized, will lazy-load the default data
     * @return WordData
     */
    public static WordData getWordData() {
        if (instanceWordData == null) {
            loadDefaultWordData();
        }
        return instanceWordData;
    }

    /**
     * Get HyphenData initialized via {@link #loadHyphenData(String)} or {@link #loadDefaultHyphenData()}
     * @return HyphenData
     */
    public static HyphenData getHyphenData() {
        if (instanceHypenData == null) {
            loadDefaultHyphenData();
        }
        return instanceHypenData;
    }

}
