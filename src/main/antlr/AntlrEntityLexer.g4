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
LPAR: '(';
RPAR: ')';
LSQRD: '[';
RSQRD: ']';
LCRLY: '{';
RCRLY: '}';
EQUAL: '=';
DQUOTE: '"';
MINUS: '-';
PLUS: '+';
MULT: '*';
DIV: '/';
DOT: '.';

// identifier
ID: [A-Z]+;

// whitespaces
WS: [ \r\n\t]+ -> channel(HIDDEN);

// comments
COMMENT: INLINE_COMMENT -> channel(HIDDEN);
fragment INLINE_COMMENT: '//' .*? [\r\n] ;

// values
INTEGER_VALUE: (MINUS | PLUS)? [0-9]+ ;
STRING_VALUE: DQUOTE .*? DQUOTE ;
BOOLEAN_VALUE: 'true' | 'false' ;
