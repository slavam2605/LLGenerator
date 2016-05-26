package ru.ifmo.ctddev.parsing;

import java.io.StringReader;

import static ru.ifmo.ctddev.parsing.NodeType.*;

/**
 * @author Моклев Вячеслав
 */
public class Parser {
    private Lexer lexer;

    public Parser(String s) {
        lexer = new Lexer(new StringReader(s));
    }

    public Tree parse() {
        lexer.nextToken();
        Tree tree = parseE();
        if (!lexer.end()) {
            throw new RuntimeParseException("Unexpected characters after pos " + lexer.getPos());
        }
        return tree;
    }

    private Tree parseE() {
        // E → ET'
        Tree t = parseT();
        Tree eList = parseEList();
        return new Tree(E, t, eList);
    }

    private Tree parseEList() {
        switch (lexer.getToken()) {
            // E' → |TE', match by FIRST
            case OR:
                Tree or = new Tree(OR);
                lexer.nextToken();
                Tree t = parseT();
                Tree eList = parseEList();
                return new Tree(E_, or, t, eList);
            // E' → ε, match by FOLLOW
            case RIGHT:
            case END:
                return new Tree(E_);
            default:
                throw new RuntimeParseException("Unexpected token " + lexer.getToken() + " at pos " + lexer.getPos());
        }
    }

    private Tree parseT() {
        // T → FT'
        Tree f = parseF();
        Tree tList = parseTList();
        return new Tree(T, f, tList);
    }

    private Tree parseTList() {
        switch (lexer.getToken()) {
            // T' → ^FT', match by FIRST
            case XOR:
                Tree xor = new Tree(XOR);
                lexer.nextToken();
                Tree f = parseF();
                Tree tList = parseTList();
                return new Tree(T_, xor, f, tList);
            // T' → ε, match by FOLLOW
            case OR:
            case RIGHT:
            case END:
                return new Tree(T_);
            default:
                throw new RuntimeParseException("Unexpected token " + lexer.getToken() + " at pos " + lexer.getPos());
        }
    }

    private Tree parseF() {
        // F → AF'
        Tree a = parseA();
        Tree fList = parseFList();
        return new Tree(F, a, fList);
    }

    private Tree parseFList() {
        switch (lexer.getToken()) {
            // F' → &AF', match by FIRST
            case AND:
                Tree and = new Tree(AND);
                lexer.nextToken();
                Tree a = parseA();
                Tree fList = parseFList();
                return new Tree(F_, and, a, fList);
            // F' → ε, match by FOLLOW
            case XOR:
            case OR:
            case RIGHT:
            case END:
                return new Tree(F_);
            default:
                throw new RuntimeParseException("Unexpected token " + lexer.getToken() + " at pos " + lexer.getPos());
        }
    }

    private Tree parseA() {
        switch (lexer.getToken()) {
            // A → !A
            case NOT:
                Tree not = new Tree(NOT);
                lexer.nextToken();
                Tree a = parseA();
                return new Tree(A, not, a);
            // A → v
            case VAR:
                lexer.nextToken();
                return new Tree(VAR);
            // A → (E)
            case LEFT:
                lexer.nextToken();
                Tree e = parseE();
                if (lexer.getToken() != Token.RIGHT) {
                    throw new RuntimeParseException("Expected closing bracket at pos " + lexer.getPos());
                }
                lexer.nextToken();
                return new Tree(A, new Tree(LEFT), e, new Tree(RIGHT));
            default:
                throw new RuntimeParseException("Unexpected token " + lexer.getToken() + " at pos " + lexer.getPos());
        }
    }

}
