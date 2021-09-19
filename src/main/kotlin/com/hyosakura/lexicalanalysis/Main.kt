package com.hyosakura.lexicalanalysis

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.application
import com.hyosakura.lexicalanalysis.common.LocalAppResources
import com.hyosakura.lexicalanalysis.common.rememberAppResources

@OptIn(ExperimentalComposeUiApi::class)
fun main() = application {
    CompositionLocalProvider(LocalAppResources provides rememberAppResources()) {
        WordSplitApplication(rememberApplicationState {
            this.exitApplication()
        })
    }
}