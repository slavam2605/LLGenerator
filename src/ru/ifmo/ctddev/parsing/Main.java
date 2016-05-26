package ru.ifmo.ctddev.parsing;

import java.io.StringReader;

/**
 * @author Моклев Вячеслав
 */
public class Main {
    public static void main(String[] args) {
        System.out.println(new Parser("a").parse());
//        Lexer lexer = new Lexer(new StringReader("a | b & c"));
//        lexer.nextToken();
//        while (lexer.getToken() != Token.END) {
//            System.out.println(lexer.getToken());
//            lexer.nextToken();
//        }
    }
}
