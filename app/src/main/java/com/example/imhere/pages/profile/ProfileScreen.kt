import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.imhere.pages.profile.ProfileViewModel

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel(),
    navController: NavHostController
) {

    var profile = viewModel.profile

    fun onSignOut() {
        viewModel.signOut {
            navController.navigate("login")
        }
    }

    Column {
        profile?.name?.let { Text(it) }
        Button(onClick = {}) {
            Text("Login")
        }
        Spacer(Modifier.height(8.dp))
        Button(onClick = {}) {
            Text("Register")
        }
        Button(onClick = { onSignOut() }) {
            Text("Log Out")
        }
    }
}