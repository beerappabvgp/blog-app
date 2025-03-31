import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.example.blogapp.repository.AuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun LogoutButton(navController: NavHostController) {
    val context = LocalContext.current
    val authRepository = AuthRepository(context)

    TextButton(onClick = {
        CoroutineScope(Dispatchers.IO).launch {
            authRepository.logout { success ->
                if (success) {
                    // Navigate to login and clear the backstack
                    navController.navigate("login") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            }
        }
    }) {
        Text("Logout")
    }
}