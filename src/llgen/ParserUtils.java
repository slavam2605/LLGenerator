package llgen;

/**
 * @author Моклев Вячеслав
 */
public class ParserUtils {

    public static boolean contains(int tokenType, int... tokenTypes) {
        for (int type: tokenTypes) {
            if (tokenType == type)
                return true;
        }
        return false;
    }

}
