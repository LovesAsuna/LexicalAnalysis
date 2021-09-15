package com.hyosakura.wordsplit.util

import com.hyosakura.wordsplit.addon.Converter
import com.hyosakura.wordsplit.enumeration.*
import com.hyosakura.wordsplit.enumeration.Number
import java.io.BufferedReader
import java.io.File
import java.util.*

class WordSplit(private val converter: Converter? = null) {
    private val buffer = StringBuilder()
    private var tempBuffer = StringBuilder()
    private var tokenList = LinkedList<Token>()
    private var macro = false
    private var stream = false

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

        fun getToken(buffer: CharSequence): Token? {
            keywordMap[buffer.toString()]?.let {
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
    }

    fun split(text: String): MutableList<Token> {
        return split(text.byteInputStream().bufferedReader())
    }

    fun split(file: File): MutableList<Token> {
        return split(file.bufferedReader())
    }

    private fun split(reader: BufferedReader): MutableList<Token> {
        tokenList.clear()
        var line: String?
        reader.use { bufferedReader ->
            while (bufferedReader.readLine().also { line = it } != null) {
                val length = line!!.length
                macro = false
                var i = 0
                while (i < length) {
                    val c = converter?.convert(line!![i++].toString())?.get(0) ?: line!![i++].toString()[0]
                    if (c == ' ') {
                        continue
                    }
                    if (c == '#') {
                        macro = true
                    }
                    if (macro && c == '<') {
                        while (i <= length) {
                            buffer.append(line!![i - 1])
                            i++
                        }
                        tokenList.add(Identifier(buffer.toString()))
                        buffer.clear()
                        break
                    }
                    buffer.append(c)
                    val token = getToken(converter?.convert(buffer) ?: buffer)
                    // 找不到token
                    if (token == null) {
                        getSplitToken(buffer)?.forEachIndexed { _, t -> tokenList.add(t) }
                    } else {
                        // 贪心匹配
                        val string = StringBuilder(buffer.toString())
                        var add = false

                        if (token == KeyWord.CIN || token == KeyWord.COUT) {
                            stream = true
                            tokenList.add(token)
                            buffer.clear()
                            continue
                        }

                        if (stream) {
                            when (token) {
                                Operator.GTR -> tokenList.add(Operator.SOUT)
                                Operator.LSS -> tokenList.add(Operator.SIN)
                            }
                            i += 1
                            buffer.clear()
                            stream = false
                            continue
                        }

                        while (i < line!!.length) {
                            if (line!![i] == ' ') {
                                break
                            }
                            string.append(line!![i++])
                            val convertedString = converter?.convert(string) ?: string.toString()
                            val array = getSplitToken(string.clear().append(convertedString))
                            if (array != null) {
                                add = true
                                array.forEachIndexed { _, t -> tokenList.add(t) }
                                break
                            }
                        }
                        if (!add) {
                            tokenList.add(token)
                        }
                        buffer.clear()
                    }
                }
            }
        }
        return tokenList
    }

    private fun getSplitToken(buffer: StringBuilder): Array<Token>? {
        val string = buffer.toString()
        var token = getToken(converter?.convert(buffer) ?: buffer)
        if (token != null) return arrayOf(token)
        // 从后往前将字符加入到临时缓冲区
        for (h in string.length - 1 downTo 0) {
            tempBuffer.append(string[h])
            // 判断临时缓冲区中是否有新的token
            val tmpToken = getToken(tempBuffer.reversed())
            // 有新的token则判断buffer除去临时缓冲区中的内容
            if (tmpToken != null) {
                val tmpLength = tempBuffer.length
                val preString = string.substring(0, buffer.length - tmpLength)
                token = getToken(converter?.convert(preString) ?: preString)
                // preString部分的token如果不为空则为标识符
                if (token == null) {
                    if (preString.isNotEmpty()) {
                        token = Identifier(preString)
                    } else {
                        return null
                    }
                }
                // preString部分的token为关键字
                val array = arrayOf(token, tmpToken)
                buffer.clear()
                tempBuffer.clear()
                return array
            }
        }
        tempBuffer.clear()
        return null
    }

}
