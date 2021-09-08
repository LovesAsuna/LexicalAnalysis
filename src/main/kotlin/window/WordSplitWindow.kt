package window

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.*
import common.LocalAppResources
import kotlinx.coroutines.launch
import util.FileDialog

@OptIn(ExperimentalComposeUiApi::class)
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

        BasicTextField(
            state.text,
            state::text::set,
            enabled = state.isInit,
            modifier = Modifier.fillMaxSize()
        )

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