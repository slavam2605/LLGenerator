package llgen;

/**
 * @author Моклев Вячеслав
 */
public interface TokenSource {
    void nextToken();
    Token curToken();
}
