package io.github.achacha.dada.engine.data;

import org.apache.commons.lang3.RandomUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Extends the default collection of words with additional indexes
 *
 * see Pronouns.md
 * see pronouns.csv
 */
public class Pronouns extends WordsByType<Pronoun> {

    protected Map<Pronoun.Form, List<Pronoun>> wordByForm;

    /**
     * Create a map of adjective type to a set of words belonging to that type
     * @return Map
     */
    private static Map<Pronoun.Form, List<Pronoun>> createIndexCollection() {
        Map<Pronoun.Form, List<Pronoun>> wordByForm = new HashMap<>();
        for (Pronoun.Form attr : Pronoun.Form.values()) {
            wordByForm.put(attr, new ArrayList<>());
        }
        return wordByForm;
    }

    public Pronouns() {
        super(Word.Type.Pronoun, RESOURCE_DATA_DEFAULT);
    }

    public Pronouns(String resourcePath) {
        super(Word.Type.Pronoun, resourcePath);
    }

    @Override
    public void init(String baseResourcePath) {
        super.init(baseResourcePath);
        wordByForm = createIndexCollection();
    }

    @Override
    protected void addWord(Pronoun word) {
        super.addWord(word);
        word.attributes.forEach(form -> wordByForm.get(form).add(word));
    }

    /**
     * Get pronouns of a given form
     * @param form Pronoun.Form
     * @return Set of Pronoun
     */
    @Nonnull
    public List<Pronoun> getPronounsByForm(Pronoun.Form form) {
        return wordByForm.get(form);
    }

    /**
     * Get random pronoun of given form
     * @param form Pronoun.Form
     * @return Pronoun or null if none of that type
     */
    @Nullable
    public Pronoun getRandomPronounByForm(Pronoun.Form form) {
        List<Pronoun> pronouns = wordByForm.get(form);
        return pronouns.get(RandomUtils.nextInt(0, pronouns.size()));
    }

    @Override
    public String toString() {
        return "Pronouns{" +
                "resourcePath='" + resourcePath + '\'' +
                ", type=" + type +
                ", size=" + wordsData.size() +
                ", wordByForm=" + wordByForm +
                '}';
    }
}
