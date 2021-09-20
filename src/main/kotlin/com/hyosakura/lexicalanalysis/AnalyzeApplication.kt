package com.hyosakura.lexicalanalysis

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.MenuScope
import androidx.compose.ui.window.Tray
import com.hyosakura.lexicalanalysis.common.LocalAppResources
import com.hyosakura.lexicalanalysis.window.AnalyzeWindow
import com.hyosakura.lexicalanalysis.window.ReplaceWindow
import kotlinx.coroutines.launch

@Composable
fun ApplicationScope.AnalyzeApplication(state: AnalyzeApplicationState) {
    ApplicationTray(state)
    AnalyzeWindow(state.analyzeWindowState)
    ReplaceWindow(state.replaceWindowState)
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun ApplicationScope.ApplicationTray(state: AnalyzeApplicationState) {
    Tray(
        LocalAppResources.current.icon,
        state = state.tray,
        hint = "LexicalAnalysis",
        menu = { ApplicationMenu(state) }
    )
}

@Composable
private fun MenuScope.ApplicationMenu(state: AnalyzeApplicationState) {
    val scope = rememberCoroutineScope()
    fun exit() = scope.launch { state.exit() }

    Item("Exit", onClick = { exit() })
}