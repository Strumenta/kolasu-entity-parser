lexer grammar AntlrEntityLexer;


options { caseInsensitive=true; }


// types
INTEGER: 'integer';
BOOLEAN: 'boolean';
STRING: 'string';

// keywords
ENTITY: 'entity';
MODULE: 'module';

// symbols
COLON: ':';
SEMI: ';';
LSQRD: '[';
RSQRD: ']';
LCRLY: '{';
RCRLY: '}';

// identifier
ID: [A-Z]+;

// whitespaces
WS: [ \r\n\t]+ -> channel(HIDDEN);