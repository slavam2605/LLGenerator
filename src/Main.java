import llgen.Pair;
import llgen.TokenSource;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.TokenStream;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Моклев Вячеслав
 */
public class Main {

    private static String camelize(String s) {
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private static void generateRule(String rule, LLGrammar grammar, PrintWriter out) {
        List<List<Pair<String, String>>> list = grammar.grammar.get(rule);
        out.println("    ");
        String ruleContext = camelize(rule) + "Context";
        out.println("    public class " + ruleContext + " {");
        List<Pair<String, Type>> returns = grammar.returns.get(rule);
        for (Pair<String, Type> def : returns) {
            out.println("        public " + def.b + " " + def.a + ";");
        }
        Set<Pair<String, String>> subContexts = new TreeSet<>(); // for neat sorted list
        for (List<Pair<String, String>> production : list) {
            Map<String, Integer> multiCont = new HashMap<>();
            for (Pair<String, String> elem: production) {
                if (LLGrammar.isRule(elem.a)) {
                    if (!multiCont.containsKey(elem.a)) {
                        multiCont.put(elem.a, 0);
                    } else {
                        multiCont.compute(elem.a, (s, a) -> a + 1);
                    }
                    Integer count = multiCont.get(elem.a);
                    String suffix = count == 0 ? "" : count.toString();
                    subContexts.add(new Pair<>(elem.a + suffix, camelize(elem.a) + "Context"));
                }
                if (LLGrammar.isToken(elem.a)) {
                    if (!multiCont.containsKey(elem.a)) {
                        multiCont.put(elem.a, 0);
                    } else {
                        multiCont.compute(elem.a, (s, a) -> a + 1);
                    }
                    Integer count = multiCont.get(elem.a);
                    String suffix = count == 0 ? "" : count.toString();
                    subContexts.add(new Pair<>(elem.a + suffix, "Token"));
                }
            }
        }
        for (Pair<String, String> subCtx: subContexts) {
            out.println("        public " + subCtx.b + " " + subCtx.a + ";");
        }
        out.println("    }");
        out.println("    ");
        out.print("    public " + ruleContext + " parse" + camelize(rule) + "(");
        List<Pair<String, Type>> params = grammar.params.get(rule);
        out.print(params.stream().map(st -> st.b + " " + st.a).collect(Collectors.joining(", ")));
        out.println(") {");
        out.println("        " + ruleContext + " _context = new " + ruleContext + "();");
        Set<String> uniqueCheck = new HashSet<>();
        for (List<Pair<String, String>> production : list) {
            out.print("        if (ParserUtils.contains(tokens.curToken().getType()");
            Set<String> first = getFirst(rule, production, grammar);
            if (!Collections.disjoint(uniqueCheck, first)) {
                System.err.println("Grammar is not LL(1): problems with \"" + rule + "\", production: " + production);
            }
            uniqueCheck.addAll(first);
            first.forEach(s -> out.print(", " + s));
            out.println(")) {");
            Map<String, Integer> multiCont = new HashMap<>();
            for (Pair<String, String> elem : production) {
                if (LLGrammar.isToken(elem.a)) {
                    if (!multiCont.containsKey(elem.a)) {
                        multiCont.put(elem.a, 0);
                    } else {
                        multiCont.compute(elem.a, (s, a) -> a + 1);
                    }
                    Integer count = multiCont.get(elem.a);
                    String suffix = count == 0 ? "" : count.toString();
                    out.println("            ParserAssert.assertToken(tokens.curToken(), " + elem.a + ");");
                    out.println("            _context." + elem.a + suffix + " = tokens.curToken();");
                    out.println("            tokens.nextToken();");
                }
                if (LLGrammar.isRule(elem.a)) {
                    if (!multiCont.containsKey(elem.a)) {
                        multiCont.put(elem.a, 0);
                    } else {
                        multiCont.compute(elem.a, (s, a) -> a + 1);
                    }
                    Integer count = multiCont.get(elem.a);
                    String suffix = count == 0 ? "" : count.toString();
                    String paramList = elem.b;
                    if (paramList.length() > 1) {
                        paramList = process(elem.b, rule, production, grammar);
                    }
                    out.println("            _context." + elem.a + suffix + " = parse" + camelize(elem.a) + "(" + paramList + ");");
                }
                if (LLGrammar.isBlock(elem.a)) {
                    String processed = process(elem.a, rule, production, grammar);
                    out.println("            {" + processed + "}");
                }
            }
            out.println("            return _context;");
            out.println("        }");
        }
        out.println("        throw new ParsingException(\"Unexpected token: \" + tokens.curToken());");
        out.println("    }");
    }

    private static String process(String block, String rule, List<Pair<String, String>> production, LLGrammar grammar) {
        StringBuilder sb = new StringBuilder();
        boolean inSingleQuotes = false;
        boolean inDoubleQuotes = false;
        boolean isEscaped = false;
        for (int i = 1; i < block.length() - 1; i++) { // remove braces
            try {
                if (block.charAt(i) != '$' || inSingleQuotes || inDoubleQuotes) {
                    sb.append(block.charAt(i));
                    continue;
                }
            } finally {
                if (!isEscaped) {
                    if (block.charAt(i) == '\'')
                        inSingleQuotes ^= true;
                    if (block.charAt(i) == '"')
                        inDoubleQuotes ^= true;
                    if (block.charAt(i) == '\\')
                        isEscaped = true;
                } else {
                    isEscaped = false;
                }
            }
            i++;
            StringBuilder local = new StringBuilder();
            while (isIDSymbol(block.charAt(i))) {
                local.append(block.charAt(i++));
            }
            i--;
            String id = local.toString();
            boolean flag = false;
            for (Pair<String, Type> param: grammar.params.get(rule)) {
                if (param.a.equals(id)) {
                    sb.append(id);
                    flag = true;
                    break;
                }
            }
            if (flag) continue;
            for (Pair<String, Type> ret: grammar.returns.get(rule)) {
                if (ret.a.equals(id)) {
                    sb.append("_context.").append(id);
                    flag = true;
                    break;
                }
            }
            if (flag) continue;
            for (Pair<String, String> elem: production) {
                if (LLGrammar.isRule(elem.a)) {
                    if (id.startsWith(elem.a)) {
                        String suffix = id.substring(elem.a.length());
                        try {
                            if (suffix.length() > 0) {
                                int ignored = Integer.valueOf(suffix);
                            }
                            sb.append("_context.").append(id);
                            break;
                        } catch (NumberFormatException ignored) {}
                    }
                }
                if (LLGrammar.isToken(elem.a)) {
                    if (id.startsWith(elem.a)) {
                        String suffix = id.substring(elem.a.length());
                        try {
                            if (suffix.length() > 0) {
                                int ignored = Integer.valueOf(suffix);
                            }
                            sb.append("_context.").append(id).append(".getText()");
                            break;
                        } catch (NumberFormatException ignored) {}
                    }
                }
            }
        }
        return sb.toString();
    }

    private static boolean isIDSymbol(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || c == '_';
    }

    private static Set<String> getFirst(String rule, List<Pair<String, String>> production, LLGrammar grammar) {
        Set<String> first = new HashSet<>();
        boolean wholeEpsilon = true;
        for (Pair<String, String> elemP : production) {
            String elem = elemP.a;
            if (LLGrammar.isToken(elem)) {
                first.add(elem);
                wholeEpsilon = false;
                break;
            }
            if (LLGrammar.isRule(elem)) {
                first.addAll(grammar.first.get(elem));
                if (!grammar.isEpsilonInducing(elem)) {
                    wholeEpsilon = false;
                    break;
                }
            }
        }
        if (wholeEpsilon) {
            first.addAll(grammar.follow.get(rule));
        }
        return first;
    }

    public static void generateParser(LLGrammar grammar, String className, PrintWriter out) throws FileNotFoundException {
        out.println(grammar.options.getOrDefault("header", ""));
        out.println("import llgen.ParserAssert;");
        out.println("import llgen.ParserUtils;");
        out.println("import llgen.ParsingException;");
        out.println("import llgen.TokenSource;");
        out.println("import llgen.Token;");
        out.println();
        out.println("public class " + className + " {");
        BufferedReader bf = new BufferedReader(new FileReader("mygen\\" + grammar.options.get("lexer") + ".tokens"));
        bf.lines().forEach(s -> {
            out.println("    public static final int " + s.replace("=", " = ") + ";");
        });
        out.println();
        out.println("    private TokenSource tokens;");
        out.println();
        out.println("    public " + className + "(TokenSource tokens) {");
        out.println("        this.tokens = tokens;");
        out.println("        tokens.nextToken();");
        out.println("    }");
        for (String rule : grammar.grammar.keySet()) {
            generateRule(rule, grammar, out);
        }
        out.println("}");
        out.close();
    }
    public static void generateLexer(List<Pair<String, Pair<String, Set<String>>>> list, String className, PrintWriter out, PrintWriter tokOut) {
        for (Pair<String, Pair<String, Set<String>>> pps: list) {
            if (pps.a.equals("package")) {
                out.println("package" + pps.b.a + ";");
                break;
            }
        }
        list = list.stream().filter(pps -> !pps.a.equals("package")).collect(Collectors.toList());
        out.print("import llgen.LexerException;\n" +
                "import llgen.Token;\n" +
                "import llgen.TokenSource;\n" +
                "\n" +
                "import java.util.ArrayList;\n" +
                "import java.util.List;\n" +
                "import java.util.regex.Matcher;\n" +
                "import java.util.regex.Pattern;\n" +
                "\n" +
                "public class " + className + " implements TokenSource {\n" +
                "    public static final int EOF = -1;\n" +
                "    public static final boolean[] skip = new boolean[] {");
        out.print(
                list.stream()
                        .map(pps -> pps.b.b.contains("skip") + "")
                        .collect(Collectors.joining(", "))
        );
        out.println("};\n" +
                "\n" +
                "    private String s;\n" +
                "    private Token token;\n" +
                "    private int pos;\n" +
                "    private List<Matcher> matchers;\n" +
                "\n" +
                "    public " + className + "(String s) {\n" +
                "        this.s = s;\n" +
                "        pos = 0;\n" +
                "        matchers = new ArrayList<>();");
        list.forEach(pps -> out.println("        matchers.add(Pattern.compile(\"" + pps.b.a.replace("\\", "\\\\") + "\").matcher(s));"));
        out.println("    }\n" +
                "\n" +
                "    @Override\n" +
                "    public void nextToken() {\n" +
                "        if (pos < s.length()) {\n" +
                "            int tokenId = -1;\n" +
                "            int end = -1;\n" +
                "            for (int i = 0; i < matchers.size(); i++) {\n" +
                "                Matcher matcher = matchers.get(i);\n" +
                "                if (matcher.find(pos)) {\n" +
                "                    if (matcher.start() == pos && matcher.end() > end) {\n" +
                "                        tokenId = i;\n" +
                "                        end = matcher.end();\n" +
                "                    }\n" +
                "                }\n" +
                "            }\n" +
                "            if (tokenId >= 0) {\n" +
                "                if (skip[tokenId]) {\n" +
                "                    pos = end;\n" +
                "                    nextToken();\n" +
                "                } else {\n" +
                "                    token = new Token(s.substring(pos, end), tokenId);\n" +
                "                    pos = end;\n" +
                "                }\n" +
                "            } else {\n" +
                "                throw new LexerException(\"Can't get token at pos \" + pos + \" (from char \" + s.charAt(pos) + \")\");\n" +
                "            }\n" +
                "        } else {\n" +
                "            token = new Token(\"\", EOF);\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public Token curToken() {\n" +
                "        return token;\n" +
                "    }\n" +
                "}\n");
        out.close();
        tokOut.println("EOF=-1");
        for (int i = 0; i < list.size(); i++) {
            tokOut.println(list.get(i).a + "=" + i);
        }
        tokOut.close();
    }

    private static void gen(String packageName, String lexName, String parsName) throws IOException {
        // Generate lexer & tokens
        ANTLRInputStream is = new ANTLRInputStream(new FileReader(lexName + ".lexer"));
        Lexer lexer = new LexerLexer(is);
        TokenStream ts = new CommonTokenStream(lexer);
        List<Pair<String, Pair<String, Set<String>>>> tokens = new LexerParser(ts).file().tokens;
        generateLexer(tokens, camelize(lexName), new PrintWriter("mygen\\" + packageName + camelize(lexName) + ".java"), new PrintWriter("mygen\\" + camelize(lexName) + ".tokens"));

        // Generate parser
        ANTLRInputStream isp = new ANTLRInputStream(new FileReader(parsName + ".parser"));
        Lexer lexerp = new ParserLexer(isp);
        TokenStream tsp = new CommonTokenStream(lexerp);
        LLGrammar g = new ParserParser(tsp).file().g;
        g.init();
        generateParser(g, camelize(parsName), new PrintWriter("mygen\\" + packageName + camelize(parsName) + ".java"));
    }

    public static void main(String[] args) throws IOException {
//        gen("dummyLexer", "dummyParser");
//        gen("testLexer", "testParser");
//        gen("gen\\", "lab2Lexer", "lab2Parser");
        gen("tex\\", "lab3Lexer", "lab3Parser");

//        TokenSource tsrc = new TestLexer("someshit10somemoreshit11andmore");
//        TestParser parser = new TestParser(tsrc);
//        parser.parseA();

//        TokenSource tscr = new DummyLexer("1 + 3 * 4");
//        DummyParser parser = new DummyParser(tscr);
//        System.out.println(parser.parseExpr().a);
    }
}
