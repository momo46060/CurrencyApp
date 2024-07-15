import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import di.initialKoin

fun main()  {
    initialKoin()
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "CurrencyApp",
        ) {
            App()
        }
    }
}