package com.hyosakura.lexicalanalysis.input

import java.util.*

class StdInHandler : Handler() {
    private lateinit var scanner: Scanner
    override fun open() {
        scanner = Scanner(System.`in`)
        while (true) {
            val line = scanner.nextLine()
            if (line == "end") {
                break
            }
            builder.append(line)
        }
        close()
    }

    override fun close() {
        scanner.close()
    }
}