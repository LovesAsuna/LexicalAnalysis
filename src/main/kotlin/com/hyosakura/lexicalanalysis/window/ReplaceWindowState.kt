package com.hyosakura.lexicalanalysis.window

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import com.hyosakura.lexicalanalysis.AnalyzeApplicationState

class ReplaceWindowState(
    private val exit: (ReplaceWindowState) -> Unit
) {
    var visible by mutableStateOf(false)

    val window = WindowState(height = 450.dp, width = 345.dp)

    val rows = mutableStateListOf<RowState>()

    fun exit() = exit(this)

    fun newRow() {
        rows.add(
            RowState(
                this,
                "",
                ""
            )
        )
    }
}