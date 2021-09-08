package com.hyosakura.wordsplit.util

import com.hyosakura.wordsplit.enumeration.*
import java.io.BufferedReader
import java.io.File
import java.util.*

class WordSplit {
    private val buffer = StringBuilder()
    private var tempBuffer = StringBuilder()
    private var tokenList = LinkedList<Token>()

    fun split(text: String): MutableList<Token> {
        return split(text.byteInputStream().bufferedReader())
    }

    fun split(file: File): MutableList<Token> {
        return split(file.bufferedReader())
    }

    private fun split(reader: BufferedReader): MutableList<Token> {
        var line: String?
        reader.use { bufferedReader ->
            while (bufferedReader.readLine().also { line = it } != null) {
                val length = line!!.length
                var i = 0
                var j = 0
                while (i < length && j < length) {
                    val c = line!![j++]
                    if (c == ' ') {
                        continue
                    }
                    buffer.append(c)
                    var token = getToken(buffer)
                    // 找不到token
                    if (token == null) {
                        val string = buffer.toString()
                        // 从后往前将字符加入到临时缓冲区
                        for (h in string.length - 1 downTo 0) {
                            tempBuffer.append(string[h])
                            // 判断临时缓冲区中是否有新的token
                            val tmpToken = getToken(tempBuffer.reversed())
                            // 有新的token则判断buffer除去临时缓冲区中的内容
                            if (tmpToken != null) {
                                val tmpLength = tempBuffer.length
                                val preString = buffer.toString().substring(0, buffer.length - tmpLength)
                                token = getToken(preString)
                                // preString部分的token为标识符
                                if (token == null) {
                                    token = Identifier(preString)
                                }
                                // preString部分的token为关键字
                                tokenList.add(token)
                                tokenList.add(tmpToken)
                                i += buffer.toString().length
                                buffer.clear()
                                tempBuffer.clear()
                                break
                            }
                        }
                        tempBuffer.clear()
                    } else {
                        // 贪心匹配
                        val string = StringBuffer(buffer.toString())
                        var isToken = true
                        var candidateToken = token
                        while (j < line!!.length && isToken) {
                            string.append(line!![j++])
                            val tmpToken = getToken(string.toString())
                            if (tmpToken != null) {
                                candidateToken = tmpToken
                                i += tmpToken.value.length
                            } else {
                                isToken = false
                            }
                        }
                        tokenList.add(candidateToken!!)
                        buffer.clear()
                    }
                }
            }
        }
        return tokenList
    }

    private fun getToken(buffer: CharSequence): Token? {
        getKeyWord(buffer)?.let {
            return it
        }
        getOperator(buffer)?.let {
            return it
        }
        getNumber(buffer)?.let {
            return it
        }
        getString(buffer)?.let {
            return it
        }
        return null
    }

    private fun getKeyWord(buffer: CharSequence): Token? {
        KeyWord.values().forEach {
            if (it.value == buffer.toString()) {
                return it
            }
        }
        return null
    }

    private fun getOperator(buffer: CharSequence): Token? {
        Operator.values().forEach {
            if (it.value == buffer.toString()) {
                return it
            }
        }
        return null
    }

    private fun getNumber(buffer: CharSequence): Token? {
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
        val string = buffer.toString()
        if (string.length < 2) {
            return null
        }
        if (string.startsWith("\"") && string.endsWith("\"")) {
            return TString(buffer.toString())
        }
        return null
    }

}
