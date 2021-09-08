package com.hyosakura.wordsplit.enumeration

/**
 * @author LovesAsuna
 **/
interface Token {
    val value: String
    val type: String
}

class Identifier(override val value: String) : Token {
    override val type: String = "标识符"
}

class Number(override val value: String) : Token {
    override val type: String = "数"
}

class TString(override val value: String) : Token {
    override val type: String = "串"
}

