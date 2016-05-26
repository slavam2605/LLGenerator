package llgen;

/**
 * @author Моклев Вячеслав
 */
public class ParserAssert {

    public static void assertToken(Token actual, int typeExpected) {
        if (actual.getType() != typeExpected) {
            throw new ParsingException("Expected token type " + typeExpected + ", found: " + actual);
        }
    }

}
