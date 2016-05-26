lexer grammar ParserLexer;

START_KEYWORD:      'start';
RETURNS_KEYWORD:    'returns';
LEXER_KEYWORD:      'lexer';
HEADER_KEYWORD:     'header' -> pushMode(ruleDefinition);
SEMICOLON:          ';';
RULE:               [a-z][a-zA-Z0-9_]*;
TOKEN:              [A-Z][a-zA-Z0-9_]*;
ARROW:              '->' -> pushMode(ruleDefinition);
BEGIN_SQBRACKET:    '[' -> pushMode(attributes), type(LSQBRACKET);
WS:                 [ \t\n\r]+ -> skip;
COMMENT:            '/*' .*? '*/' -> skip;
LINE_COMMENT:       '//' ~'\n'* '\n' -> skip;

mode ruleDefinition;

OR:                 '|';
RSEMICOLON:         ';' -> popMode, type(SEMICOLON);
RRULE:              [a-z][a-zA-Z0-9_]* -> type(RULE);
RTOKEN:             [A-Z][a-zA-Z0-9_]* -> type(TOKEN);
BRACED_CODE:        LCBRACE CODE RCBRACE;
BRACKET_BLOCK:      '[' BRACKET_CONTENT ']';
RWS:                [ \t\n\r]+ -> skip;
RCOMMENT:           '/*' .*? '*/' -> skip;
RLINE_COMMENT:      '//' ~'\n'* '\n' -> skip;

fragment BRACKET_CONTENT:   PTEXT+ BRACKET_BLOCK BRACKET_CONTENT | PTEXT+;
fragment STRING_LITERAL:    '"' (ESC | ~["\\])* '"';
fragment CHAR_LITERAL:      '\'' (ESC | ~['\\]) '\'';
fragment ESC:               '\\' (["\\/bfnrt] | UNICODE);
fragment UNICODE:           'u' HEX HEX HEX HEX;
fragment HEX:               [0-9a-fA-F];
fragment PTEXT:             (~['"[\]])+ | STRING_LITERAL | CHAR_LITERAL;

mode attributes;

LSQBRACKET:         '[' -> pushMode(attributes);
RSQBRACKET:         ']' -> popMode;
LESS:               '<';
GREATER:            '>';
COMMA:              ',';
ID:                 [a-zA-Z_][a-zA-Z0-9_]*;
AWS:                [ \t\n\r]+ -> skip;

fragment LCBRACE:   '{';
fragment RCBRACE:   '}';
fragment ELCBRACE:  '\\{';
fragment ERCBRACE:  '\\}';
fragment CODE:      TEXT BRACED_CODE CODE | TEXT;
fragment TEXT:      (~[{}] | ELCBRACE | ERCBRACE)*;