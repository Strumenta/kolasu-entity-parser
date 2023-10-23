parser grammar AntlrEntityParser;

options { tokenVocab=AntlrEntityLexer; }

module_declaration:
    MODULE name=ID
    imports+=module_import*
    entities+=entity_declaration*
;

module_import
    : IMPORT name=ID
    ;

entity_declaration:
    ENTITY name=ID (LCRLY
        features+=feature_declaration*
        operations+=operation_declaration*
    RCRLY)?
;

feature_declaration:
    name=ID COLON type=ID
;

operation_declaration:
    name=ID
    LPAREN (parameters+=parameter_declaration
        (COMMA parameters+=parameter_declaration)*)? RPAREN
    (COLON type=ID)?
    LCRLY statements+=statement* RCRLY
;

parameter_declaration:
    name=ID COLON type=ID
;

statement
    : binding_statement
    | return_statement
    ;

binding_statement:
    LET variable=variable_declaration EQ value=expression
;

variable_declaration:
    name=ID COLON type=ID
;

return_statement:
    RETURN value=expression
;

expression
    : context=expression DOT target=ID argument_list    #invocation_expression
    | context=expression DOT target=ID                  #reference_expression
    | left=expression op=ADD right=expression           #operator_expression
    | left=expression op=SUB right=expression           #operator_expression
    | left=expression op=MUL right=expression           #operator_expression
    | left=expression op=DIV right=expression           #operator_expression
    | target=ID argument_list                           #invocation_expression
    | target=ID                                         #reference_expression
    | NEW name=ID argument_list                         #constructor_expression
    | value=STRING                                      #literal_expression
    | value=INTEGER                                     #literal_expression
    | value=BOOLEAN                                     #literal_expression
    ;

argument_list:
    LPAREN (arguments+=expression
        (COMMA arguments+=expression)*)? RPAREN
;

literal
    : value=STRING  #string_literal
    | value=INTEGER #integer_literal
    | value=BOOLEAN #boolean_literal
    ;
