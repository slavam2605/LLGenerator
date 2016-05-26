parser grammar ParserParser;

options {tokenVocab = ParserLexer;}

@header {import java.util.Collections;}

file returns [LLGrammar g]
    :   {$g = new LLGrammar();} (line {
        switch ($line.branch) {
            case 0:
                if ($g.options.containsKey($line.s)) {
                    System.err.println("Warning: redefinition of option \"" + $line.s
                     + "\": from \"" + $g.options.get($line.s) + "\" to \"" + $line.o + "\"");
                }
                $g.options.put($line.s, $line.o);
                break;
            case 1:
                $g.grammar.put($line.s, (List<List<llgen.Pair<String, String>>>) $line.o);
                $g.params.put($line.s, $line.params);
                $g.returns.put($line.s, $line.returns);
                break;
            case 2:
                $g.options.put("header", $line.s);
                break;
            default:
                throw new llgen.ParsingException("Unknown $line.branch: " + $line.branch);
        }
    })*
    ;

line returns [int branch, String s, Object o, List<llgen.Pair<String, Type>> params, List<llgen.Pair<String, Type>> returns]
    locals [boolean pd, boolean rd]
    :   st=('start'|'lexer') param=(RULE|TOKEN) SEMICOLON {$branch = 0;  $s = $st.text; $o = $param.text;}
    |   'header' BRACED_CODE SEMICOLON {$branch = 2; $s = $BRACED_CODE.text.substring(1, $BRACED_CODE.text.length() - 1);}
    |   {$pd = false; $rd = false;} RULE (paramDefinition {$pd = true;})? (returnDefinition {$rd = true;})? '->' orList SEMICOLON {
        $branch = 1;
        $s = $RULE.text;
        $o = $orList.list;
        if ($pd)
            $params = $paramDefinition.paramList;
        else
            $params = Collections.emptyList();
        if ($rd)
            $returns = $returnDefinition.paramList;
        else
            $returns = Collections.emptyList();
    }
    ;

paramDefinition returns [List<llgen.Pair<String, Type>> paramList]
    :   {$paramList = new ArrayList<>();} LSQBRACKET tp1=type id1=ID {$paramList.add(new llgen.Pair($id1.text, $tp1.t));} (COMMA tp2=type id2=ID {$paramList.add(new llgen.Pair($id2.text, $tp2.t));})* RSQBRACKET
    ;

returnDefinition returns [List<llgen.Pair<String, Type>> paramList]
    :   'returns' paramDefinition {$paramList = $paramDefinition.paramList;}
    ;

type returns [Type t] locals [List<Type> list]
    :   ID {$t = Type.simpleType($ID.text);}
    |   tp1=type LSQBRACKET RSQBRACKET {$t = Type.arrayType($tp1.t);}
    |   {$list = new ArrayList<>();} ID '<' tp1=type {$list.add($tp1.t);} (COMMA tp2=type {$list.add($tp2.t);})* '>' {$t = Type.genericType($ID.text, $list);}
    ;

orList returns [List<List<llgen.Pair<String, String>>> list]
    :   {$list = new ArrayList<>();} rt1=rtList {$list.add($rt1.list);} (OR rtc=rtList {$list.add($rtc.list);})*
    ;

rtList returns [List<llgen.Pair<String, String>> list] locals [String s]
    :   {$list = new ArrayList<>();}
    (   {$s = "";} RULE (BRACKET_BLOCK {$s = $BRACKET_BLOCK.text;})? {$list.add(new llgen.Pair($RULE.text, $s));}
    |   TOKEN {$list.add(new llgen.Pair($TOKEN.text, ""));}
    |   BRACED_CODE {$list.add(new llgen.Pair($BRACED_CODE.text, ""));}
    )*
    ;