import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.imhere.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope

@Composable
fun GoogleSignInButton(
    onIdTokenReceived: (String) -> Unit
) {
    val context = LocalContext.current

    // Google Sign-In Options
    // TODO: NOTE to ZIO, the REQUEST ID token for my google calendar api is :
    // 482684834489-p50fskjgsii7jjpbgn8hvac68h254shv.apps.googleusercontent.com
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestEmail()
        .requestScopes(Scope("https://www.googleapis.com/auth/calendar"))
        .build()

    val googleSignInClient = remember { GoogleSignIn.getClient(context, gso) }

    // Activity Result Launcher
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account.idToken
            if (idToken != null) {
                onIdTokenReceived(idToken)
            } else {
                Log.e("GoogleSignIn", "ID Token is null")
            }
        } catch (e: ApiException) {
            Log.e("GoogleSignIn", "Sign in failed", e)
        }
    }

    // Sign-In Button UI
    Button(onClick = {
        launcher.launch(googleSignInClient.signInIntent)
    }) {
        Text("Sign in with Google")
    }
}
