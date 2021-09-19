package com.hyosakura.lexicalanalysis.input

import java.nio.charset.StandardCharsets

/**
 * 提供接口用于从输入流中获取信息，由于输入对象可以是磁盘文件，也可以是控制台标准输入，
 * 所以提供一组接口，将具体的输入方式与输入系统隔离开来，简化输入系统的设计
 */
abstract class Handler {
    protected val builder = StringBuilder()
    abstract fun open()
    abstract fun close()

    /**
     * 从输入流中读取字符，并返回实际读取的字符长度
     *
     * @param buffer 缓冲区
     * @param begin  起始位置
     * @param length 读取长度
     * @return 实际读取的字符长度
     */
    fun read(buffer: ByteArray, begin: Int, length: Int): Int {
        val currentPos = 0
        if (currentPos >= builder.length) {
            return 0
        }
        var readCnt = 0
        val inputBuf = builder.toString().toByteArray(StandardCharsets.UTF_8)
        while (currentPos + readCnt < builder.length && readCnt < length) {
            buffer[begin + readCnt] = inputBuf[currentPos + readCnt]
            readCnt++
        }
        return readCnt
    }
}