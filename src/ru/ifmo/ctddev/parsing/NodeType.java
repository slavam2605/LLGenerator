package ru.ifmo.ctddev.parsing;

/**
 * @author Моклев Вячеслав
 */
public enum NodeType {
    E, E_ {
        @Override
        public String toString() {
            return "E'";
        }
    },
    T, T_ {
        @Override
        public String toString() {
            return "T'";
        }
    },
    F, F_ {
        @Override
        public String toString() {
            return "F'";
        }
    },
    A, VAR, AND, OR, XOR, NOT, LEFT, RIGHT
}
