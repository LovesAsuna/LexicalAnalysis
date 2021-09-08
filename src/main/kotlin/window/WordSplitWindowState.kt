package window

import WordSplitApplicationState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Notification
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import common.Settings
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.file.Path

class WordSplitWindowState(
    private val application: WordSplitApplicationState,
    path: Path?,
    private val exit: (WordSplitWindowState) -> Unit
) {
    val settings: Settings get() = application.settings

    val window = WindowState()

    var path by mutableStateOf(path)
        private set

    var isChanged by mutableStateOf(false)
        private set

    val openDialog = DialogState<Path?>()

    private var _text by mutableStateOf("")

    var text: String
        get() = _text
        set(value) {
            check(isInit)
            _text = value
            isChanged = true
        }

    var isInit by mutableStateOf(false)
        private set

    fun toggleFullscreen() {
        window.placement = if (window.placement == WindowPlacement.Fullscreen) {
            WindowPlacement.Floating
        } else {
            WindowPlacement.Fullscreen
        }
    }

    suspend fun run() {
        if (path != null) {
            open(path!!)
        } else {
            initNew()
        }
    }

    private suspend fun open(path: Path) {
        isInit = false
        isChanged = false
        this.path = path
        try {
            _text = path.readTextAsync()
            isInit = true
        } catch (e: Exception) {
            e.printStackTrace()
            text = "Cannot read $path"
        }
    }

    private fun initNew() {
        _text = ""
        isInit = true
        isChanged = false
    }


    suspend fun open() {
        val path = openDialog.awaitResult()
        if (path != null) {
            open(path)
        }
    }

    fun exit() = exit(this)


    fun sendNotification(notification: Notification) {
        application.sendNotification(notification)
    }
}

private suspend fun Path.writeTextAsync(text: String) = withContext(Dispatchers.IO) {
    toFile().writeText(text)
}

private suspend fun Path.readTextAsync() = withContext(Dispatchers.IO) {
    toFile().readText()
}

class DialogState<T> {
    private var onResult: CompletableDeferred<T>? by mutableStateOf(null)

    val isAwaiting get() = onResult != null

    suspend fun awaitResult(): T {
        onResult = CompletableDeferred()
        val result = onResult!!.await()
        onResult = null
        return result
    }

    fun onResult(result: T) = onResult!!.complete(result)
}