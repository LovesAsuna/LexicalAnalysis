package com.hyosakura.lexicalanalysis

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.window.Notification
import androidx.compose.ui.window.TrayState
import com.hyosakura.lexicalanalysis.common.Settings
import com.hyosakura.lexicalanalysis.window.ReplaceWindowState
import com.hyosakura.lexicalanalysis.window.WordSplitWindowState

@Composable
fun rememberApplicationState(applicationExit: () -> Unit) = remember {
    WordSplitApplicationState {
        applicationExit()
    }
}


class WordSplitApplicationState(exit: (WordSplitWindowState) -> Unit) {
    val settings = Settings()
    val tray = TrayState()

    val replaceWindowState = ReplaceWindowState(
        application = this,
        exit = {
            it.visible = false
        }
    )

    val wordSplitWindowState = WordSplitWindowState(
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
        wordSplitWindowState.exit()
    }
}