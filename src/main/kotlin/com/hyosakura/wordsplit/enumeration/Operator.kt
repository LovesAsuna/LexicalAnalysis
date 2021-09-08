package com.hyosakura.wordsplit.enumeration

/**
 * @author LovesAsuna
 **/
enum class Operator(override val value: String) : Token {
    SHARP("#"),
    ADD("+"),
    MINUS("-"),
    MULTIPLY("*"),
    DIV("/"),
    MODULO("%"),
    INCREASING("++"),
    DECREASING("--"),
    EQU("=="),
    NEQ("!="),
    GTR(">"),
    LSS("<"),
    GEQ(">="),
    LEQ("<="),
    AND("&&"),
    OR("||"),
    NOT("!"),
    ASSIGN("="),
    LPAREN("("),
    RPAREN(")"),
    LBRACKET("{"),
    RBRACKET("}"),
    COMMA(","),
    SEMICOLON(";"),
    SIN("<<"),
    SOUT(">>");

    override val type: String = "特殊字符"
}