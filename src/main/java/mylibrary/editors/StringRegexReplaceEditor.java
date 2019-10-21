package mylibrary.editors;

import java.beans.PropertyEditorSupport;

import org.springframework.util.Assert;

/**
 * StringRegexReplaceEditor extends PropertyEditorSupport and allows
 * the editor to process the arbitrary amount of replacements. Each replacement
 * consists of a regex and replacement and is a necessary argument
 * for StringRegexReplacementEditor's constructor.
 *
 */
public class StringRegexReplaceEditor extends PropertyEditorSupport {

    public static final String WHITESPACE_PREFIX_REGEX = "^\\s+";
    public static final String WHITESPACE_SUFFIX_REGEX = "\\s+$";
    public static final String MULTIPLE_SPACE_REGEX = "[ ]{2,}+";
    public static final String MULTIPLE_WHITESPACE_REGEX = "[\t\n\\x0B\r\f]+";


    public static final String EMPTY_STRING_REPLACEMENT = "";
    public static final String SINGLE_SPACE_REPLACEMENT = " ";

    private Replacement[] replacements;

    /**
     * Replacement is a static nested class in StringRegexReplaceEditor.
     * The varargs of Replacement objects is a necessary argument
     * in StringRegexReplaceEditor's constructor.
     *
     */
    public static class Replacement {

        private String regex;
        private String replacement;

        public Replacement(String regex, String replacement) {
            this.regex = regex;
            this.replacement = replacement;
        }

        public String replace(String input) {
            return input.replaceAll(regex, replacement);
        }
    }

    /**
     * whitespaceReplaceEditor is a static factory that allows out of the box
     * construction of a StringRegexReplaceEditor with simple functionality:
     * the editor will trim all prefix, suffix whitespace as well as replace all multiple
     * whitespace in between strings with one space.
     * @return StringRegexReplaceEditor
     */
    public static StringRegexReplaceEditor whitespaceReplaceEditor() {
        return new StringRegexReplaceEditor(
                new Replacement(WHITESPACE_PREFIX_REGEX, EMPTY_STRING_REPLACEMENT),
                new Replacement(WHITESPACE_SUFFIX_REGEX, EMPTY_STRING_REPLACEMENT),
                new Replacement(MULTIPLE_WHITESPACE_REGEX, SINGLE_SPACE_REPLACEMENT),
                new Replacement(MULTIPLE_SPACE_REGEX, SINGLE_SPACE_REPLACEMENT)

        );
    }

    public StringRegexReplaceEditor(Replacement... replacements ) {

        Assert.notNull(replacements, "Replacements must not be null");
        Assert.noNullElements(replacements, "Replacement must not be null");
        Assert.notEmpty(replacements, "Replacement must not be empty");

        this.replacements = replacements;
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {

        String newValue = text;
        if (newValue != null) {
            for(int i = 0; i < replacements.length ; i++ ) {
                newValue = replacements[i].replace(newValue);
            }
        }
        setValue(newValue);
        return;
    }

    @Override
    public String getAsText() {
        return (String) getValue();
    }

}
