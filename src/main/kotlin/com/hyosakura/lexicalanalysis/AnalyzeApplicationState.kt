package com.hyosakura.lexicalanalysis

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.window.Notification
import androidx.compose.ui.window.TrayState
import com.hyosakura.lexicalanalysis.common.Settings
import com.hyosakura.lexicalanalysis.window.ReplaceWindowState
import com.hyosakura.lexicalanalysis.window.AnalyzeWindowState

@Composable
fun rememberApplicationState(applicationExit: () -> Unit) = remember {
    AnalyzeApplicationState {
        applicationExit()
    }
}


class AnalyzeApplicationState(exit: (AnalyzeWindowState) -> Unit) {
    val settings = Settings()
    val tray = TrayState()

    val replaceWindowState = ReplaceWindowState {
        it.visible = false
    }

    val analyzeWindowState = AnalyzeWindowState(
        application = this,
        path = null,
        exit = {
            exit(it)
        }
    )

    fun sendNotification(notification: Notification) {
        tray.sendNotification(notification)
    }

    fun exit() {
        analyzeWindowState.exit()
    }
}