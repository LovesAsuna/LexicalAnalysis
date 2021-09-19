package com.hyosakura.lexicalanalysis.addon

import com.hyosakura.lexicalanalysis.window.ReplaceWindowState

/**
 * @author LovesAsuna
 **/
interface Converter {
    fun convert(value: CharSequence): String

    class DefaultConverter(private val state: ReplaceWindowState) : Converter {

        override fun convert(value: CharSequence): String {
            for (row in state.rows) {
                if (value.toString().contains(row.first)) {
                    return value.toString().replace(row.first, row.second)
                }
            }
            return value.toString()
        }

    }
}