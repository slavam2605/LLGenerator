import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Моклев Вячеслав
 */
public class MatcherExperimants {
    public static void main(String[] args) {
        String s = "mamamalapapapala";

        Pattern pattern = Pattern.compile("(ma|pa)*la");
        Matcher matcher = pattern.matcher(s);
        System.out.println(matcher.find() + ", " + matcher.start() + ", " + matcher.end());
        System.out.println(matcher.find() + ", " + matcher.start() + ", " + matcher.end());
    }
}
