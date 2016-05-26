package tex;

/**
 * @author Моклев Вячеслав
 */
public class TexPostprocessor {

    public static boolean capitalGreekLetter(char c) {
        return c >= 'Α' && c <= 'Ω';
    }

    public static boolean letter(char c) {
        return c >= 'a' && c <= 'z'
                || c >= 'A' && c <= 'Z'
                || c >= 'α' && c <= 'ω'
                || c >= 'Α' && c <= 'Ω'
                || c >= '0' && c <= '9';
    }

    public static String enfont(char c) {
        if (letter(c) && !capitalGreekLetter(c) || c == '\'')
            return "<i>" + c + "</i>";
        return "" + c;
    }

    public static boolean expandable(char c) {
        switch (c) {
            case '=':
            case '≠':
            case '≥':
            case '≤':
            case '∈':
            case '+':
            case '-':
            case '⊂':
            case '≡':
            case '⇒':
                return true;
            default:
                return false;
        }
    }

    public static String unescape(String s) {
        return s.replace("\\\\", "\n").replace("\\", "");
    }

    public static String transform(char c) {
        if (c == '<') return " &lt; ";
        if (c == '>') return " &gt; ";
        String s = enfont(c);
        if (expandable(c))
            return " " + s + " ";
        else if (c == ',')
            return s + " ";
        else
            return s;
    }

    public static String transform(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            sb.append(transform(s.charAt(i)));
        }
        return sb.toString();
    }

    public static String getEscapedChar(String s) {
        char c = s.charAt(1);
        switch (c) {
            case '\\': return "<br>";
            default: return "" + c;
        }
    }

    public static String getEscapedString(String s) {
        return TEXConstants.constant.get(s.substring(1));
    }

    public static String enspan(String s) {
        int left = 0;
        while (left < s.length() && s.charAt(left) == ' ')
            left++;
        int right = 0;
        while (right < s.length() && s.charAt(s.length() - right - 1) == ' ')
            right++;
        if (left == s.length())
            return s;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < left; i++)
            sb.append(' ');
        sb.append(" <span class=\"strikethrough\">").append(s.substring(left, s.length() - right)).append("</span>");
        for (int i = 0; i < right; i++)
            sb.append(' ');
        return sb.toString();
    }

}
