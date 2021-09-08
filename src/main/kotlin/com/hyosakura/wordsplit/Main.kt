package com.hyosakura.wordsplit

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.application
import com.hyosakura.wordsplit.common.LocalAppResources
import com.hyosakura.wordsplit.common.rememberAppResources

@OptIn(ExperimentalComposeUiApi::class)
fun main() = application {
    CompositionLocalProvider(LocalAppResources provides rememberAppResources()) {
        WordSplitApplication(rememberApplicationState {
            this.exitApplication()
        })
    }
}