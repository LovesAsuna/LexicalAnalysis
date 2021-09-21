package com.hyosakura.lexicalanalysis.enumeration

/**
 * @author LovesAsuna
 **/
interface Token {
    val value: String
    val type: String

    abstract class PrintableToken : Token {
        override fun toString(): String {
            return "<$type,$value>"
        }
    }
}

class Identifier(override val value: String) : Token.PrintableToken() {
    override val type: String = "标识符"
}

class Number(override val value: String) : Token.PrintableToken() {
    override val type: String = "数"
}

class TString(override val value: String) : Token.PrintableToken() {
    override val type: String = "串"
}