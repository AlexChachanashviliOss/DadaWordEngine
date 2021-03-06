package io.github.achacha.dada.engine.data;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Pronoun
 */
public class Pronoun extends Word {

    /**
     *  Types of pronouns
     *  A pronoun can be both or neither singular or plural
     */
    public enum Form {
        personal,
        subjective,
        objective,
        possessive,
        demonstrative,
        interrogative,
        relative,
        reflexive,
        reciprocal,
        indefinite,
        singular,
        plural
    }

    protected final Set<Form> attributes;

    protected Pronoun(ArrayList<String> attrs) {
        super(attrs.get(0));
        Preconditions.checkArgument(attrs.size() == 13, "Pronoun expects word with attributes to be in 12 parts, attrs=%s", attrs);

        this.attributes = new HashSet<>();
        for (int i=1; i<13; ++i) {
            boolean isSet = BooleanUtils.toBoolean(attrs.get(i));
            if (isSet) {
                attributes.add(Form.values()[i - 1]);
            }
        }

        LOGGER.trace("Adding pronoun=`{}` with attributes={}", this.word, this.attributes);
    }

    /**
     * @param attr Pronoun.Attribute
     * @return Checks if this pronoun is of the attribute
     */
    public boolean isA(Form attr) {
        return attributes.contains(attr);
    }

    @Override
    public String toString() {
        return "Pronoun{" +
                "attributes=" + attributes +
                ", word='" + word + '\'' +
                '}';
    }

    /*
    #Pronoun,PER,SUB,OBJ,POS,DEM,INT,REL,REF,REC,IND,PLU
     */
    @Override
    public String toCsv() {
        return word+","+
                (isA(Form.personal) ? "y," : "n,") +
                (isA(Form.subjective) ? "y," : "n,") +
                (isA(Form.objective) ? "y," : "n,") +
                (isA(Form.possessive) ? "y," : "n,") +
                (isA(Form.demonstrative) ? "y," : "n,") +
                (isA(Form.interrogative) ? "y," : "n,") +
                (isA(Form.relative) ? "y," : "n,") +
                (isA(Form.reflexive) ? "y," : "n,") +
                (isA(Form.reciprocal) ? "y," : "n,") +
                (isA(Form.indefinite) ? "y," : "n,") +
                (isA(Form.plural) ? "y," : "n,")
                ;
    }

    @Override
    public Type getType() {
        return Type.Pronoun;
    }

    @Override
    public Collection<Pair<String,String>> getAllForms() {
        ArrayList<Pair<String,String>> forms = new ArrayList<>(attributes.size());

        // Add each form
        attributes.forEach(attribute->{
            forms.add(Pair.of(attribute.name(), word));
        });

        return forms;
    }
}
