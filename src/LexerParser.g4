parser grammar LexerParser;

options {tokenVocab = LexerLexer;}

@header {
import llgen.Pair;
import java.util.Collections;
import java.util.HashSet;
}

file returns [List<Pair<String, Pair<String, java.util.Set<String>>>> tokens]
    :   {$tokens = new ArrayList<>();} (line {$tokens.add($line.token);})*
    ;

line returns [Pair<String, Pair<String, java.util.Set<String>>> token]
    :   TOKEN '->' REGEX modificator SEMICOLON {$token = new Pair<>($TOKEN.text, new Pair<>(($REGEX.text.substring(1, $REGEX.text.length() - 1)), $modificator.list));}
    |   'package' PACKAGE_NAME SEMICOLON {$token = new Pair<>("package", new Pair<>($PACKAGE_NAME.text, Collections.emptySet()));}
    ;

modificator returns [java.util.Set<String> list]
    :   {$list = new HashSet<>();} '::' md1=MODIFICATOR {$list.add($md1.text);} (',' mdc=MODIFICATOR {$list.add($mdc.text);})*
    |   {$list = Collections.emptySet();}
    ;