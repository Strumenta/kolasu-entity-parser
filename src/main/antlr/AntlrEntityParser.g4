parser grammar AntlrEntityParser;

options { tokenVocab=AntlrEntityLexer; }

module
    : entities+=entity* EOF
    ;

entity
    : ENTITY name=ID LCRLY
        features+=feature*
    RCRLY
    ;

feature
    : name=ID COLON SEMI
    ;

