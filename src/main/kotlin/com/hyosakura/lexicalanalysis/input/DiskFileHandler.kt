package com.hyosakura.lexicalanalysis.input

import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException

class DiskFileHandler(fileName: String) : Handler() {
    private val file: File
    private var reader: BufferedReader? = null
    override fun open() {
        try {
            reader = BufferedReader(FileReader(file))
            var line: String?
            while (reader!!.readLine().also { line = it } != null) {
                builder.append(line)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            close()
        }
    }

    override fun close() {
        try {
            reader!!.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    init {
        file = File(fileName)
    }
}