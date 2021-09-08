package com.hyosakura.wordsplit.window

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import com.hyosakura.wordsplit.common.LocalAppResources
import com.hyosakura.wordsplit.enumeration.Token
import com.hyosakura.wordsplit.util.FileDialog
import com.hyosakura.wordsplit.util.WordSplit
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class, androidx.compose.ui.unit.ExperimentalUnitApi::class)
@Composable
fun WordSplitWindow(state: WordSplitWindowState) {
    val scope = rememberCoroutineScope()

    fun exit() = scope.launch { state.exit() }

    Window(
        state = state.window,
        title = titleOf(state),
        icon = LocalAppResources.current.icon,
        onCloseRequest = { exit() }
    ) {
        LaunchedEffect(Unit) { state.run() }

        WindowMenuBar(state)

        var tokens: MutableList<Token>? by remember { mutableStateOf(null) }

        Column {
            Box(modifier = Modifier.width(800.dp).height(400.dp)) {
                Row {
                    OutlinedTextField(
                        value = state.text,
                        onValueChange = {
                            state.text = it
                            tokens?.clear()
                            tokens = drawResult(state)
                        },
                        modifier = Modifier.width(400.dp).height(400.dp)
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(400.dp)) {
                        Spacer(Modifier.height(10.dp))
                        Text("单词拼装器", fontSize = TextUnit(27.5F, TextUnitType.Sp), fontFamily = FontFamily.Serif)
                        Image(
                            painter = LocalAppResources.current.icon,
                            contentDescription = "logo",
                            modifier = Modifier.width(250.dp).height(250.dp).clickable {
                                Runtime.getRuntime().exec("cmd /c start https://blog.hyosakura.com")
                            }
                        )
                        Button(onClick = {}) {
                            Text("test")
                        }
                    }
                }
            }

            Box(modifier = Modifier.width(800.dp).height(400.dp)) {
                val stateHorizontal = rememberScrollState(0)

                Box {
                    Column(modifier = Modifier.verticalScroll(stateHorizontal)) {
                        if (tokens != null) {
                            for (token in tokens!!) {
                                Row {
                                    Surface(
                                        modifier = Modifier.shadow(4.dp).width(state.window.size.width / 2),
                                        color = Color(255, 255, 210),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = token.value,
                                            modifier = Modifier.padding(5.dp)
                                        )
                                    }
                                    Surface(
                                        modifier = Modifier.shadow(4.dp).width(state.window.size.width / 2),
                                        color = Color(255, 255, 210),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = token.type,
                                            modifier = Modifier.padding(5.dp)
                                        )
                                    }
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

        if (state.openDialog.isAwaiting) {
            FileDialog(
                title = "Notepad",
                isLoad = true,
                onResult = {
                    state.openDialog.onResult(it)
                }
            )
        }

    }
}

private fun titleOf(state: WordSplitWindowState): String {
    val changeMark = if (state.isChanged) "*" else ""
    val filePath = state.path ?: "Untitled"
    return "$changeMark$filePath - WordSplit"
}

@Composable
private fun FrameWindowScope.WindowMenuBar(state: WordSplitWindowState) = MenuBar {
    val scope = rememberCoroutineScope()

    fun open() = scope.launch { state.open() }
    fun exit() = scope.launch { state.exit() }

    Menu("File") {
        Item("Open...", onClick = { open() })
        Separator()
        Item("Exit", onClick = { exit() })
    }

    Menu("Settings") {
        Item(
            if (state.settings.isTrayEnabled) "Hide tray" else "Show tray",
            onClick = state.settings::toggleTray
        )
        Item(
            if (state.window.placement == WindowPlacement.Fullscreen) "Exit fullscreen" else "Enter fullscreen",
            onClick = state::toggleFullscreen
        )
    }
}

fun drawResult(state: WordSplitWindowState): MutableList<Token> {
    val wordSplit = WordSplit()
    return wordSplit.split(state.text)
}