package com.hyosakura.lexicalanalysis.util

import com.hyosakura.lexicalanalysis.addon.Converter
import com.hyosakura.lexicalanalysis.enumeration.*
import com.hyosakura.lexicalanalysis.enumeration.Number
import java.io.BufferedReader
import java.io.File
import java.util.*

class Analyzer(private val converter: Converter? = null) {
    private val buffer = StringBuilder()
    private var tokenList = LinkedList<Token>()
    private var macro = false

    companion object {
        var keywordMap: HashMap<String, Token> = HashMap()

        init {
            KeyWord.values().forEach {
                keywordMap[it.value] = it
            }
            Operator.values().forEach {
                keywordMap[it.value] = it
            }
        }
    }

    private fun getToken(buffer: CharSequence): Token? {
        val realBuffer = converter?.convert(buffer) ?: buffer
        keywordMap[realBuffer.toString()]?.let {
            return it
        }
        getNumber(realBuffer)?.let {
            return it
        }
        getString(realBuffer)?.let {
            return it
        }
        return null
    }

    private fun getNumber(buffer: CharSequence): Token? {
        if (buffer.toString().isEmpty()) return null
        val num = StringBuffer()
        var isNum = true
        for (c in buffer.toString()) {
            if (c.code in 48..57) {
                num.append((c.code - 48).toString())
            } else {
                isNum = false
                break
            }
        }
        if (isNum) {
            return Number(num.toString())
        }
        return null
    }

    private fun getString(buffer: CharSequence): Token? {
        if (buffer.toString().isEmpty()) return null
        val string = buffer.toString()
        if (string.length < 2) {
            return null
        }
        if (string.startsWith("\"") && string.endsWith("\"")) {
            return TString(buffer.toString())
        }
        return null
    }

    fun split(text: String): MutableList<Token> {
        return split(text.byteInputStream().bufferedReader())
    }

    fun split(file: File): MutableList<Token> {
        return split(file.bufferedReader())
    }

    private fun split(reader: BufferedReader): MutableList<Token> {
        buffer.clear()
        tokenList.clear()
        var line: String?
        reader.use { bufferedReader ->
            while (bufferedReader.readLine().also { line = it } != null) {
                val length = line!!.length
                macro = false
                var i = 0
                while (i < length) {
                    val c = converter?.convert(line!![i].toString())?.get(0) ?: line!![i].toString()[0]
                    if (c == ' ') {
                        i++
                        continue
                    }
                    if (c == '#') {
                        i++
                        macro = true
                        tokenList.add(Operator.SHARP)
                        continue
                    }
                    if (macro && c == '<') {
                        while (i < length) {
                            buffer.append(line!![i])
                            i++
                        }
                        tokenList.add(Identifier(buffer.toString()))
                        buffer.clear()
                        break
                    }
                    buffer.append(c)
                    val token = getToken(buffer)
                    // 找不到token
                    if (token == null) {
                        while (i + 1 <= length - 1 && line!![i + 1] == ' ') {
                            i++
                        }
                        getToken(line!![i + 1].toString()).let {
                            if (it is Operator) {
                                tokenList.add(Identifier(buffer.toString()))
                                buffer.clear()
                            }
                            i++
                        }
                    } else {
                        // 忽略注释
                        if (token == Operator.DIV) {
                            while (i + 1 <= length - 1 && line!![i + 1] == ' ') {
                                i++
                            }
                            if (getToken(line!![i + 1].toString()) == Operator.DIV) {
                                buffer.clear()
                                break
                            }
                        }
                        // 分情况最大匹配
                        // 输入输出
                        if (token == KeyWord.CIN || token == KeyWord.COUT) {
                            while (i + 1 <= length - 1 && line!![i + 1] == ' ') {
                                i++
                            }
                            i += 3
                            tokenList.add(token)
                            tokenList.add(
                                if (token == KeyWord.CIN) Operator.SIN else Operator.SOUT
                            )
                            buffer.clear()
                            continue
                        }
                        // 自增自减
                        if (token == Operator.ADD || token == Operator.MINUS) {
                            if (getToken(line!![i].toString()) == token) {
                                tokenList.add(
                                    if (token == Operator.ADD) Operator.INCREASING else Operator.DECREASING
                                )
                                i++
                            } else {
                                tokenList.add(token)
                            }
                            buffer.clear()
                            continue
                        }
                        // include直接跳过
                        if (token == KeyWord.INCLUDE) {
                            i++
                            tokenList.add(token)
                            buffer.clear()
                            continue
                        }

                        tokenList.add(token)
                        i++
                        buffer.clear()
                        continue
                    }
                }
            }
        }
        return tokenList
    }
}
