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
    | value=(INTEGER_VALUE | BOOLEAN_VALUE | STRING_VALUE)  #literal_expression
    | left=expression op=(MULT | DIV) right=expression      #binary_expression
    | left=expression op=(PLUS | MINUS) right=expression    #binary_expression
    | target=fqn                                            #fqn_expression
    ;
