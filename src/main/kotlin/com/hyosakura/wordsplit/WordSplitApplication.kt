package com.hyosakura.wordsplit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.MenuScope
import androidx.compose.ui.window.Tray
import com.hyosakura.wordsplit.common.LocalAppResources
import com.hyosakura.wordsplit.window.WordSplitWindow
import kotlinx.coroutines.launch

@Composable
fun WordSplitApplication(state: WordSplitApplicationState) {
    ApplicationTray(state)
    WordSplitWindow(state.window)
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun ApplicationTray(state: WordSplitApplicationState) {
    Tray(
        LocalAppResources.current.icon,
        state = state.tray,
        hint = "WordSplit",
        menu = { ApplicationMenu(state) }
    )
}

@Composable
private fun MenuScope.ApplicationMenu(state: WordSplitApplicationState) {
    val scope = rememberCoroutineScope()
    fun exit() = scope.launch { state.exit() }

    Item("Exit", onClick = { exit() })
}