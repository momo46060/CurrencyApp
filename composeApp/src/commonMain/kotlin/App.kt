import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import org.jetbrains.compose.ui.tooling.preview.Preview
import presentation.HomeScreen

@Composable
@Preview
fun App() {
    val colors = if(!isSystemInDarkTheme()) lightScheme else darkScheme

    MaterialTheme (
        colorScheme = colors
    ){
        Navigator(HomeScreen())
    }
}