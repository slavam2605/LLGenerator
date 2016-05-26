package tex;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Моклев Вячеслав
 */
public class TEXConstants {
    public static Map<String, String> constant;
    public static Map<String, String> plainConstant;
    public static Map<String, Integer> arity;

    static {
        constant = new LinkedHashMap<String, String>() {
            {
                put("int", "∫");
                put("sum", "Σ");
                put("infty", "∞");
                put("pm", "±");
                put("rightarrow", "→");
                put("leftarrow", "←");
                put("Rightarrow", "⇒");
                put("Leftrightarrow", "⇔");
                put("ne", "≠");
                put("le", "≤");
                put("ge", "≥");
                put("forall", "∀");
                put("exists", "∃");
                put("in", "∈");
                put("notin", "∉");
                put("subset", "⊂");
                put("cap", "∩");
                put("cup", "∪");
                put("top", "⊤");
                put("bot", "⊥");
                put("equiv", "≡");
                put("vDash", "⊨");

                // Small greek letters
                put("alpha", "α");
                put("beta", "β");
                put("varbeta", "ϐ");
                put("gamma", "γ");
                put("delta", "δ");
                put("epsilon", "ϵ");
                put("varepsilon", "ε");
                put("zeta", "ζ");
                put("eta", "η");
                put("theta", "θ");
                put("vartheta", "ϑ");
                put("iota", "ι");
                put("kappa", "κ");
                put("varkappa", "ϰ");
                put("lambda", "λ");
                put("mu", "μ");
                put("nu", "ν");
                put("xi", "ξ");
                put("omicron", "ο");
                put("pi", "π");
                put("varpi", "ϖ");
                put("rho", "ρ");
                put("varrho", "ϱ");
                put("sigma", "σ");
                put("varsigma", "ς");
                put("tau", "τ");
                put("upsilon", "υ");
                put("phi", "ϕ");
                put("varphi", "φ");
                put("chi", "χ");
                put("psi", "ψ");
                put("omega", "ω");

                // Capital greek letters
                put("Alpha", "Α");
                put("Beta", "Β");
                put("Gamma", "Γ");
                put("Delta", "Δ");
                put("Epsilon", "Ε");
                put("Zeta", "Ζ");
                put("Eta", "Η");
                put("Theta", "Θ");
                put("Iota", "Ι");
                put("Kappa", "Κ");
                put("Lambda", "Λ");
                put("Mu", "Μ");
                put("Nu", "Ν");
                put("Xi", "Ξ");
                put("Omicron", "Ο");
                put("Pi", "Π");
                put("Rho", "Ρ");
                put("Sigma", "Σ");
                put("Tau", "Τ");
                put("Upsilon", "Υ");
                put("Phi", "Φ");
                put("Chi", "Χ");
                put("Psi", "Ψ");
                put("Omega", "Ω");
            }
        };
        plainConstant = new HashMap<String, String>() {
            {
                put("textbackslash", "\\");
            }
        };
        arity = new HashMap<String, Integer>() {
            {
                put("left", 1);
                put("right", 1);
                put("not", 1);
                put("overline", 1);
                put("sqrt", 1);
            }
        };
    }

    public static int getArity(String esc) {
        String cmd = esc.substring(1);
        if (constant.containsKey(cmd))
            return 0;
        if (arity.containsKey(cmd)) {
            return arity.get(cmd);
        }
        System.err.println("Unknown function: " + esc);
        return 0;
    }

}
