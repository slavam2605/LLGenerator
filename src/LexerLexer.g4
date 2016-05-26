lexer grammar LexerLexer;

TOKEN:          [A-Z][a-zA-Z0-9_]*;
ARROW:          '->';
SEMICOLON:      ';';
DOUBLECOLON:    '::';
PACKAGE:        'package' -> pushMode(packageName);
REGEX:          '\'' (~'\'')+ '\'';
MODIFICATOR:    'skip';
COMMA:          ',';
WS:             [ \t\r\n]+ -> skip;
COMMENT:        '/*' .*? '*/' -> skip;
LINE_COMMENT:   '//' ~'\n'* '\n' -> skip;

mode packageName;

PACKAGE_NAME:   ~';'+;
PSEMICOLON:     ';' -> popMode, type(SEMICOLON);