package com.hyosakura.lexicalanalysis.input

import java.nio.charset.StandardCharsets

class Input {
    // lookAhead操作最大长度
    private val maxLookAhead = 16

    // 分词后字符串的最大长度
    private val wordMaxLength = 1024

    // 缓冲区大小(3个词的最大长度+2个maxLookAhead长度)
    private val bufferSize = wordMaxLength * 3 + 2 * maxLookAhead

    // 缓冲区的逻辑结束位置
    private var bufferEnd = bufferSize

    // 缓冲区警戒线
    private val danger = bufferEnd - maxLookAhead

    // 缓冲区结束位置
    private val end = bufferSize

    // 缓冲区
    private val buffer = ByteArray(bufferSize)

    // 指向当前要读入的字符位置
    private var next = 0

    // 当前被词法分析器分析的字符串位置
    private var currentStart = 0

    // 当前被词法分析器分析的字符串结束位置
    private var currentEnd = 0

    // 上一个被词法分析器分析的字符串起始位置
    private var prevStart = 0

    // 上一个被词法分析器分析的字符串所在的行号
    private var prevLine = 0

    // 上一个被词法分析器分析的字符串长度
    private var prevLength = 0
    private var handler: Handler? = null

    // 当前被词法分析器分析的字符串的行号
    private var currentLine = 0

    // 输入流是否结束
    private var readEOF = false

    /**
     * 缓冲区中是否还有可读的字符
     *
     * 当且仅当输入流结束且缓冲区的next越过bufferEnd时才返回true
     */
    private fun noMoreChars(): Boolean {
        return readEOF && next >= bufferEnd
    }

    private fun getHandler(fileName: String?): Handler {
        return fileName?.let { DiskFileHandler(it) } ?: StdInHandler()
    }

    fun newHandler(fileName: String?) {
        if (handler != null) {
            handler!!.close()
        }
        handler = getHandler(fileName)
        handler!!.open()
        readEOF = false
        bufferEnd = end
        currentLine = 1
    }

    val currentText: String
        get() = String(buffer.copyOfRange(currentStart, currentEnd), StandardCharsets.UTF_8)
    val prevText: String
        get() = String(buffer.copyOfRange(prevStart, prevStart + prevLength), StandardCharsets.UTF_8)

    /**
     * 标记一个新的单词开始
     *
     * @return 当前分析的单词的起始位置
     */
    fun markStart(): Int {
        currentEnd = next
        currentStart = currentEnd
        return currentStart
    }

    /**
     * 标记当前单词结束
     */
    fun markEnd() {
        currentEnd = next
    }

    /**
     * 执行这个函数后，上一个被词法解析器解析的字符串将无法在缓冲区中找到
     */
    fun markPrev() {
        prevStart = currentStart
        prevLine = currentLine
        prevLength = currentEnd - currentStart
    }

    /**
     * doRead()是真正的获取输入函数，它将数据从输入流中读入缓冲区，并从缓冲区中返回要读取的字符
     * 并将next加一，从而指向下一个要读取的字符，如果next的位置距离缓冲区的逻辑末尾(bufferEnd)不到
     * MAX_LOOK_HEAD时，将会对缓冲区进行一次flush操作
     */
    fun doRead(): Byte {
        if (noMoreChars()) {
            return 0
        }
        doFlush()
        if (!readEOF) {
            fillBuffer(currentEnd)
        }
        if (buffer[next].toInt().toChar() == '\n') {
            currentLine++
        }
        return buffer[next++]
    }

    /**
     * flush缓冲区，如果next没有越过DANGER的话，那就什么都不做
     * 要不然像上一节所说的一样将数据进行平移，并从输入流中读入数据，写入平移后
     * 所产生的空间
     * ```
     * prevStart                DANGER
     * |                          |
     * buffer               | currentStart currentEnd  |  next  bufferEnd
     * |                    | |           |            |  |      |
     * V                    V V           V            V  V      V
     * +---------------------------------------------------------+---------+
     * |    已经读取的区域    |          未读取的区域              | 浪费的区域|
     * +--------------------------------------------------------------------
     * |<------offset------>|<-----------copy_amt------------->|
     * |<-------------------------BUFFER_SIZE----------------------------->|
     *  ```
     * 未读取区域的左边界是prevStart或currentStart(两者较小的那个)，把未读取区域平移到最左边覆盖已经读取区域，返回1
     * 如果flush操作成功，-1如果操作失败，0 如果输入流中已经没有可以读取的多余字符。如果force 为 true
     * 那么不管Next有没有越过Danger,都会引发Flush操作
     */
    private fun doFlush() {
        // 缓冲区将满，将未读取区域复制至缓冲区左端
        if (next > danger) {
            val offset = prevStart.coerceAtMost(currentStart)
            System.arraycopy(buffer, 0, buffer, offset, bufferEnd - offset)
            prevStart -= offset
            currentStart -= offset
            currentEnd -= offset
        }
    }

    /**
     * 从输入流中读取信息，填充缓冲区平移后的可用空间，可用空间的长度是从start一直到bufferEnd
     * 每次从输入流中读取的数据长度是WORD_MAX_LENGTH写整数倍
     *
     * @param start 起始读取位置
     * @return 实际填充的长度
     */
    private fun fillBuffer(start: Int): Int {
        // 需要从输入流中读入的数据长度
        // 实际上从输入流中读到的数据长度
        var n: Int
        val need = (end - start) / wordMaxLength * wordMaxLength
        if (need < 0) {
            System.err.println("Internal Error (fillBuffer): Bad read-request starting addr.")
        }
        if (need == 0) {
            return 0
        }
        if (handler!!.read(buffer, start, need).also { n = it } == -1) {
            System.err.println("Can't read input file")
        }
        bufferEnd = start + n
        // 输入流已经到末尾
        if (n < need) {
            readEOF = true
        }
        return n
    }

    /**
     * 预读取若干个字符
     *
     * @param n 字符数
     * @return 字符
     */
    fun lookAhead(n: Int): Byte {
        val p = buffer[next + n - 1]
        if (readEOF && next + n - 1 >= bufferEnd) {
            return -1
        }
        return if (next + n - 1 < 0 || next + n - 1 >= bufferEnd) 0 else p
    }
}