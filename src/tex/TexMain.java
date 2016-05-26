package tex;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

/**
 * @author Моклев Вячеслав
 */
public class TexMain {
    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter pw = new PrintWriter("out.html", "UTF-8");
        pw.print("<html><body><meta charset=\"utf-8\">" +
                "<style type=\"text/css\">" +
                ".strikethrough {\n" +
                "  position: relative;\n" +
                "}\n" +
                ".strikethrough:before {\n" +
                "  position: absolute;\n" +
                "  content: \"\";\n" +
                "  left: -3;\n" +
                "  top: 60%;\n" +
                "  right: -3;\n" +
                "  border-top: 1px solid;\n" +
                "  border-color: inherit;\n" +
                "\n" +
                "  -webkit-transform:rotate(-45deg);\n" +
                "  -moz-transform:rotate(-45deg);\n" +
                "  -ms-transform:rotate(-45deg);\n" +
                "  -o-transform:rotate(-45deg);\n" +
                "  transform:rotate(-45deg);\n" +
                "}" +
                "overline {\n" +
                "    text-decoration: overline;\n" +
                "}" + "</style>" +
                "<pre><font face=\"CMU Serif\">" + new Lab3Parser(new Lab3Lexer("a_2 + b^3 = c^{44} - a_22\\\\\\sqrt{lel} + \\left<a\\right>\\ne 2")).parseMathTextStart().s + "</font></pre></body></html>");
        pw.close();
    }
}
