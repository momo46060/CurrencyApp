import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinContext
import presentation.screen.HomeScreen

@Composable
@Preview
fun App() {
    val colors = if (!isSystemInDarkTheme()) lightScheme else darkScheme
    MaterialTheme(
        colorScheme = colors
    ) {
        KoinContext {
            Navigator(HomeScreen())
        }

    }
}