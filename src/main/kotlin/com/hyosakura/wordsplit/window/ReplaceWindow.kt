package com.hyosakura.wordsplit.window

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Window
import com.hyosakura.wordsplit.common.LocalAppResources
import kotlinx.coroutines.launch

@Composable
fun ReplaceWindow(state: ReplaceWindowState) {
    val scope = rememberCoroutineScope()

    fun exit() = scope.launch { state.exit() }

    Window(
        state = state.window,
        visible = state.visible,
        title = "Replace Control",
        resizable = false,
        icon = LocalAppResources.current.icon,
        onCloseRequest = { exit() }
    ) {
        WindowMenuBar(state)

        Box {
            val stateHorizontal = rememberScrollState(0)

            Column(modifier = Modifier.background(Color(255, 255, 210)).fillMaxSize().verticalScroll(stateHorizontal)) {
                for (row in state.rows) {
                    key(row) {
                        Row(row) {
                            if (it.first.isEmpty() && it.second.isEmpty()) {
                                state.rows.remove(it)
                                // state.application.wordSplitWindowState.converter.removeMapping(index)
                            }
                            false
                        }
                    }
                }
            }
            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd),
                adapter = rememberScrollbarAdapter(stateHorizontal)
            )
        }
    }
}

@Composable
private fun FrameWindowScope.WindowMenuBar(state: ReplaceWindowState) = MenuBar {
    val scope = rememberCoroutineScope()

    fun new() = scope.launch { state.newRow() }

    Menu("Operation") {
        Item("New...", onClick = { new() })
    }

}

@Composable
fun Row(state: RowState, delete: (RowState) -> Boolean) {
    Row {
        @Composable
        fun holder(content: @Composable () -> Unit) {
            Surface(
                modifier = Modifier.shadow(4.dp).width(state.replaceWindow.window.size.width / 2),
                color = Color(215, 200, 170),
                shape = RoundedCornerShape(4.dp)
            ) {
                content()
            }
        }
        holder {
            OutlinedTextField(
                value = state.first,
                onValueChange = {
                    state.first = it
                    if (delete(state)) {
                        return@OutlinedTextField
                    }
                }
            )
        }
        holder {
            OutlinedTextField(
                value = state.second,
                onValueChange = {
                    state.second = it
                    if (delete(state)) {
                        return@OutlinedTextField
                    }
                }
            )
        }
    }
}

class RowState(
    val replaceWindow: ReplaceWindowState,
    first: String,
    second: String
) {
    var first by mutableStateOf(first)
    var second by mutableStateOf(second)
}