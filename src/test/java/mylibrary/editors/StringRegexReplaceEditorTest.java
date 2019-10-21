package mylibrary.editors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.helpers.MessageFormatter;

import mylibrary.editors.StringRegexReplaceEditor.*;

public class StringRegexReplaceEditorTest {

    private static final String[] WHITESPACE = new String[] {" ","\t","\n","\f","\r"};

    private Replacement[] replacements;
    private StringRegexReplaceEditor editor;

    @Before
    public void setup()
    {
        editor = StringRegexReplaceEditor.whitespaceReplaceEditor();
    }

    /* ***** constructor tests *****/
    @Test(expected=IllegalArgumentException.class)
    public void noArgsThrowsIAE() {
        new StringRegexReplaceEditor();
    }

    @Test(expected=IllegalArgumentException.class)
    public void nullThrowsNPE() {
        new StringRegexReplaceEditor((Replacement[])null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void nullReplacementThrowsNPE() {
        new StringRegexReplaceEditor(new Replacement[] {null});
    }

    /* ***** object to string tests *****/
    @Test
    public void nullObjectToNullString() {
        assertNull(objectToString(null));
    }

    @Test
    public void emptyObjectToEmptyString() {
        assertEquals("", objectToString(""));
    }

    @Test
    public void objectToString() {
        assertEquals("text", objectToString("text"));
    }

    /* ***** string to object tests *****/
    @Test
    public void nullStringToNullObject() {
        assertNull(stringToObject(null));
    }

    @Test
    public void emptyStringToEmptyObject() {
        assertEquals("", stringToObject(""));
    }

    @Test
    public void stringToObject() {
        assertEquals("text", stringToObject("text"));
    }

    @Test
    public void optimisedToNotReplaceWhenNotNecessary() {
        assertSame("text text", stringToObject("text text"));
    }

    @Test
    public void stringToObjectPrefix() {
        checkStringToObject("test", "{}test", WHITESPACE);
    }

    @Test
    public void stringToObjectSuffix() {
        checkStringToObject("test", "test{}", WHITESPACE);
    }

    @Test
    public void stringToObjectMiddle() {
        checkStringToObject("test test", "test{}test", WHITESPACE);
    }

    @Test
    public void customRegexTest() {

        StringRegexReplaceEditor.Replacement replacementOne = new StringRegexReplaceEditor.Replacement("test", "replaced test");
        StringRegexReplaceEditor.Replacement replacementTwo = new StringRegexReplaceEditor.Replacement("text", "replaced text");

        replacements = new Replacement[] {replacementOne,replacementTwo};
        editor = new StringRegexReplaceEditor(replacements);

        assertEquals("replaced test replaced text", stringToObject("test text"));
        assertEquals("replaced test replaced text replaced text replaced test",
                stringToObject("test text text test"));
    }

    private String objectToString(Object object) {
        editor.setValue(object);
        return editor.getAsText();
    }

    private Object stringToObject(String string) {
        editor.setAsText(string);
        return editor.getValue();
    }

    private void checkStringToObject(String expected, String input, String... whitespaces) {
        for (int i = 0; i < whitespaces.length; i++) {
            assertEquals(expected, stringToObject(format(input, whitespaces[i])));
            for (int j = 0; j < whitespaces.length; j++) {
                assertEquals(expected, stringToObject(format(input, whitespaces[i], whitespaces[j], whitespaces[i])));
            }
        }
    }

    private String format(String input, String... strings) {
        String replacement =String.join("", strings);
        return MessageFormatter
                .arrayFormat(input, new String[] { replacement })
                .getMessage();
    }
}
