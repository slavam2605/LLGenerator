package ru.ifmo.ctddev.parsing;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import static ru.ifmo.ctddev.parsing.Token.*;

/**
 * @author Моклев Вячеслав
 */
public class Lexer {
    private Reader reader;
    private int curChar;
    private int curPos;
    private Token curToken;

    public Lexer(Reader reader) {
        this.reader = reader;
        curPos = -1;
        nextChar();
    }

    private boolean isBlank(int c) {
        return c == ' ' || c == '\r' || c == '\n' || c == '\t';
    }

    private void nextChar() {
        curPos++;
        try {
            curChar = reader.read();
        } catch (IOException e) {
            throw new RuntimeParseException("Exception while reading char at pos " + curPos + ": \n" + e.getMessage());
        }
    }

    public void nextToken() {
        while (isBlank(curChar)) {
            nextChar();
        }
        if (curChar == -1) {
            curToken = END; return;
        }
        int oldChar = curChar;
        int oldPos = curPos;
        nextChar();
        if (oldChar >= 'a' && oldChar <= 'z' || oldChar >= 'A' && oldChar <= 'Z') {
            curToken = VAR; return;
        }
        switch (oldChar) {
            case '&': curToken = AND; return;
            case '|': curToken = OR; return;
            case '^': curToken = XOR; return;
            case '!': curToken = NOT; return;
            case '(': curToken = LEFT; return;
            case ')': curToken = RIGHT; return;
            default: throw new RuntimeParseException("Unknown character #" + oldChar + " at position " + oldPos);
        }
    }

    public boolean end() {
        while (isBlank(curChar)) {
            nextChar();
        }
        return curChar == -1;
    }

    public int getPos() {
        return curPos;
    }

    public Token getToken() {
        return curToken;
    }
}
