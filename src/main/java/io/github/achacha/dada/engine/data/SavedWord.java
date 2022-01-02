package io.github.achacha.dada.engine.data;

import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Saved word data
 */
public class SavedWord {
    /**
     * Word and all of its forms
     */
    private final Word word;

    /**
     * Form used to render the saved word
     * This is only useful in knowing which form was used at save time so that
     * rhyme based matching knows which form to rhyme against
     */
    private final String form;

    /**
     * @param word Word
     * @param form String form of the Word saved or "" for base
     */
    public SavedWord(@Nonnull Word word, @Nonnull String form) {
        this.word = word;
        this.form = form;
        Preconditions.checkNotNull(form, "Form must not be null, base is specified by empty string");
    }

    @Override
    public String toString() {
        return word.getWordString();
    }

    /**
     * @return Word
     */
    @Nonnull
    public Word getWord() {
        return word;
    }

    /**
     * @return String of the Word
     * @see Word#getWordString()
     */
    public String getWordString() {
        return word.getWordString();
    }

    /**
     * @return form String used when this word was saved
     */
    @Nonnull
    public String getFormName() {
        return form;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SavedWord)) return false;
        SavedWord savedWord = (SavedWord) o;
        return Objects.equals(word, savedWord.word) && Objects.equals(form, savedWord.form);
    }

    @Override
    public int hashCode() {
        return Objects.hash(word, form);
    }
}
