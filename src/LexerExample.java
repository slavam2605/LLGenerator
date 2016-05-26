import llgen.LexerException;
import llgen.Token;
import llgen.TokenSource;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LexerExample implements TokenSource {
    public static final int EOF = -1;
    public static final boolean[] skip = new boolean[] {false, false, false, false, false, true};

    private String s;
    private Token token;
    private int pos;
    private List<Matcher> matchers;

    public LexerExample(String s) {
        this.s = s;
        pos = 0;
        matchers = new ArrayList<>();
        matchers.add(Pattern.compile("\\+").matcher(s));
        matchers.add(Pattern.compile("\\*").matcher(s));
        matchers.add(Pattern.compile("[0-9]+").matcher(s));
        matchers.add(Pattern.compile("\\(").matcher(s));
        matchers.add(Pattern.compile("\\)").matcher(s));
        matchers.add(Pattern.compile("[ \t\r\n]+").matcher(s));
    }

    @Override
    public void nextToken() {
        if (pos < s.length()) {
            int tokenId = -1;
            int end = -1;
            for (int i = 0; i < matchers.size(); i++) {
                Matcher matcher = matchers.get(i);
                if (matcher.find(pos)) {
                    if (matcher.start() == pos && matcher.end() > end) {
                        tokenId = i;
                        end = matcher.end();
                    }
                }
            }
            if (tokenId >= 0) {
                if (skip[tokenId]) {
                    pos = end;
                    nextToken();
                } else {
                    token = new Token(s.substring(pos, end), tokenId);
                    pos = end;
                }
            } else {
                throw new LexerException("Can't get token at pos " + pos + " (from char " + s.charAt(pos) + ")");
            }
        } else {
            token = new Token("", EOF);
        }
    }

    @Override
    public Token curToken() {
        return token;
    }
}
