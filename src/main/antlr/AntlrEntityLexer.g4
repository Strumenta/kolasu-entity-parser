lexer grammar AntlrEntityLexer;


options { caseInsensitive=true; }

// keywords
ENTITY: 'entity';
MODULE: 'module';
IMPORT: 'import';
RETURN: 'return';
LET: 'let';
NEW: 'new';

// symbols
COLON: ':';
SEMI: ';';
COMMA: ',';
DOT: '.';
LSQRD: '[';
RSQRD: ']';
LCRLY: '{';
RCRLY: '}';
LPAREN: '(';
RPAREN: ')';

// operators
ADD: '+';
SUB: '-';
MUL: '*';
DIV: '/';
EQ: '=';

// literals
STRING: '"' .*? '"';
INTEGER: '0'|[1-9][0-9]*;
BOOLEAN: 'true'|'false';

// identifier
ID: [A-Z]+;

// comments
COMMENT : '#' ~[\r\n]* '\r'? '\n' -> skip;

// whitespaces
WS: [ \r\n\t]+ -> channel(HIDDEN);
