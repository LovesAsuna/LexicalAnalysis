package com.hyosakura.lexicalanalysis

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.window.application
import com.hyosakura.lexicalanalysis.common.LocalAppResources
import com.hyosakura.lexicalanalysis.common.rememberAppResources

lateinit var applicationState: AnalyzeApplicationState

fun main() = application {
    applicationState = rememberApplicationState {
        this.exitApplication()
    }
    CompositionLocalProvider(LocalAppResources provides rememberAppResources()) {
        AnalyzeApplication(applicationState)
    }
}