import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.window.Notification
import androidx.compose.ui.window.TrayState
import common.Settings
import window.WordSplitWindowState

@Composable
fun rememberApplicationState(exit: (WordSplitWindowState) -> Unit) = remember { WordSplitApplicationState(exit) }


class WordSplitApplicationState(exit: (WordSplitWindowState) -> Unit) {
    val settings = Settings()
    val tray = TrayState()

    val window = WordSplitWindowState(
        application = this,
        path = null,
        exit = exit
    )


    fun sendNotification(notification: Notification) {
        tray.sendNotification(notification)
    }

    fun exit() = window.exit()
}