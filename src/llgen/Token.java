package llgen;

/**
 * @author Моклев Вячеслав
 */
public class Token {
    private String text;
    private int type;

    public Token(String text, int type) {
        this.text = text;
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public int getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Token{" +
                "text='" + text + '\'' +
                ", type=" + type +
                '}';
    }
}
