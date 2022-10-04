parser grammar AntlrEntityParser;

options { tokenVocab=AntlrEntityLexer; }

module
    : MODULE name=ID LCRLY
        entities+=entity*
    RCRLY
    ;

entity
    : ENTITY name=ID LCRLY
        features+=feature*
    RCRLY
    ;

feature
    : name=ID (COLON type=type_spec)? (EQUAL value=expression)? SEMI
    ;

type_spec
    : INTEGER       #integer_type
    | BOOLEAN       #boolean_type
    | STRING        #string_type
    | target=ID     #entity_type
    ;

fqn
    : ID (DOT ID)*
    ;

expression
    : LPAR expression RPAR                                  #parentheses_expression
    | (INTEGER_VALUE | BOOLEAN_VALUE | STRING_VALUE)        #literal_expression
    | expression (MULT | DIV) expression                    #binary_expression
    | expression (PLUS | MINUS) expression                  #binary_expression
    | fqn                                                   #fqn_expression
    ;
