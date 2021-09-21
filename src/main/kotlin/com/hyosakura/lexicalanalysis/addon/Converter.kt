package com.hyosakura.lexicalanalysis.addon

import com.hyosakura.lexicalanalysis.window.ReplaceWindowState
import org.slf4j.LoggerFactory

/**
 * @author LovesAsuna
 **/
interface Converter {
    fun convert(value: CharSequence): String

    class DefaultConverter(private val state: ReplaceWindowState) : Converter {
        private val log = LoggerFactory.getLogger(this::class.java)

        override fun convert(value: CharSequence): String {
            for (row in state.rows) {
                if (value.toString().contains(row.first)) {
                    log.debug("convert {} to {}", row.first, row.second)
                    return value.toString().replace(row.first, row.second)
                }
            }
            return value.toString()
        }

    }
}