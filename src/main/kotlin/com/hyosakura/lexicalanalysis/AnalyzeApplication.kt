package com.hyosakura.lexicalanalysis

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.MenuScope
import androidx.compose.ui.window.Tray
import com.hyosakura.lexicalanalysis.common.LocalAppResources
import com.hyosakura.lexicalanalysis.window.WordSplitWindow
import com.hyosakura.lexicalanalysis.window.ReplaceWindow
import kotlinx.coroutines.launch

@Composable
fun ApplicationScope.WordSplitApplication(state: WordSplitApplicationState) {
    ApplicationTray(state)
    WordSplitWindow(state.wordSplitWindowState)
    ReplaceWindow(state.replaceWindowState)
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun ApplicationScope.ApplicationTray(state: WordSplitApplicationState) {
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