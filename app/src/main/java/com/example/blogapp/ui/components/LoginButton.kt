import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
fun LoginButton(navController: NavHostController) {
    TextButton(onClick = { navController.navigate("login") }) {
        Text("Login")
    }
}
