parser grammar SystemVerilogParser;

options { tokenVocab = SystemVerilogLexer; }

project
    : top* EOF
    ;

top
    : moduleDeclaration
    ;

moduleDeclaration
    : MODULE identifier SEMICOLON ENDMODULE
    ;

identifier
    : SimpleIdentifier
    ;
