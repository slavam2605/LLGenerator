import llgen.Pair;

import java.util.*;

/**
 * @author Моклев Вячеслав
 */
public class LLGrammar {
    public Map<String, Object> options;
    public Map<String, List<List<Pair<String, String>>>> grammar;
    public Map<String, Set<String>> first;
    public Map<String, Set<String>> follow;
    public Set<String> epsilon;
    public Map<String, List<Pair<String, Type>>> returns;
    public Map<String, List<Pair<String, Type>>> params;

    public LLGrammar() {
        options = new HashMap<>();
        grammar = new HashMap<>();
        first = new HashMap<>();
        follow = new HashMap<>();
        epsilon = new HashSet<>();
        returns = new HashMap<>();
        params = new HashMap<>();
    }

    public static boolean isToken(String s) {
        return Character.isUpperCase(s.charAt(0));
    }

    public static boolean isRule(String s) {
        return Character.isLowerCase(s.charAt(0));
    }

    public static boolean isBlock(String s) {
        return s.charAt(0) == '{';
    }

    public boolean isEpsilonInducing(String rule) {
        return epsilon.contains(rule);
    }

    private void findEpsilon() {
        for (Map.Entry<String, List<List<Pair<String, String>>>> entry: grammar.entrySet()) {
            String rule = entry.getKey();
            List<List<Pair<String, String>>> list = entry.getValue();
            for (List<Pair<String, String>> production : list) {
                if (production.size() == 0) {
                    epsilon.add(rule);
                }
            }
        }
        boolean changed = true;
        while (changed) {
            changed = false;
            for (Map.Entry<String, List<List<Pair<String, String>>>> entry: grammar.entrySet()) {
                String rule = entry.getKey();
                List<List<Pair<String, String>>> list = entry.getValue();
                for (List<Pair<String, String>> production : list) {
                    boolean eps = true;
                    for (Pair<String, String> elemP: production) {
                        String elem = elemP.a;
                        if (isToken(elem)) {
                            eps = false;
                            break;
                        }
                        if (isRule(elem) && !isEpsilonInducing(elem)) {
                            eps = false;
                            break;
                        }
                    }
                    if (eps) {
                        changed |= epsilon.add(rule);
                    }
                }
            }
        }
    }

    private void findFirst() {
        for (String rule: grammar.keySet()) {
            first.put(rule, new HashSet<>());
        }
        boolean changed = true;
        while (changed) {
            changed = false;
            for (Map.Entry<String, List<List<Pair<String, String>>>> entry: grammar.entrySet()) {
                String rule = entry.getKey();
                List<List<Pair<String, String>>> list = entry.getValue();
                for (List<Pair<String, String>> production: list) {
                    for (Pair<String, String> elemP: production) {
                        String elem = elemP.a;
                        if (isToken(elem)) {
                            changed |= first.get(rule).add(elem);
                            break;
                        }
                        if (isRule(elem)) {
                            changed |= first.get(rule).addAll(first.get(elem));
                            if (!isEpsilonInducing(elem)) {
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    private void findFollow() {
        for (String rule: grammar.keySet()) {
            follow.put(rule, new HashSet<>());
        }
        //noinspection SuspiciousMethodCalls
        follow.get(options.get("start")).add("EOF");
        boolean changed = true;
        while (changed) {
            changed = false;
            for (Map.Entry<String, List<List<Pair<String, String>>>> entry: grammar.entrySet()) {
                String A = entry.getKey();
                List<List<Pair<String, String>>> list = entry.getValue();
                for (List<Pair<String, String>> production : list) {
                    for (int i = 0; i < production.size(); i++) {
                        if (isRule(production.get(i).a)) {
                            String B = production.get(i).a;
                            if (i == production.size() - 1) {
                                changed |= follow.get(B).addAll(follow.get(A));
                            } else {
                                String G = production.get(i + 1).a;
                                if (isRule(G)) {
                                    changed |= follow.get(B).addAll(first.get(G));
                                    if (isEpsilonInducing(G)) {
                                        changed |= follow.get(B).addAll(follow.get(A));
                                    }
                                } else if (isToken(G)) {
                                    changed |= follow.get(B).add(G);
                                } else if (isBlock(G)) {
                                    changed |= follow.get(B).addAll(follow.get(A));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void init() {
        findEpsilon();
        findFirst();
        findFollow();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("@options:\n");
        for (Map.Entry<String, Object> entry: options.entrySet()) {
            sb.append(entry.toString()).append("\n");
        }
        sb.append("@rules:\n");
        for (Map.Entry<String, List<List<Pair<String, String>>>> entry: grammar.entrySet()) {
            sb.append(entry.toString()).append("\n");
        }
        sb.append("@epsilon:\n");
        for (String rule: epsilon) {
            sb.append(rule).append(", ");
        }
        sb.append("\n");
        sb.append("@first:\n");
        for (String rule: first.keySet()) {
            sb.append("FIRST[").append(rule).append("] = ").append(first.get(rule)).append("\n");
        }
        sb.append("@follow:\n");
        for (String rule: follow.keySet()) {
            sb.append("FOLLOW[").append(rule).append("] = ").append(follow.get(rule)).append("\n");
        }
        return sb.toString();
    }
}
