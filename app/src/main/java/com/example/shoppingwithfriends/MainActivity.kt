package com.example.shoppingwithfriends

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.shoppingwithfriends.ui.theme.ShoppingWithFriendsTheme
import com.google.android.gms.common.SignInButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShoppingWithFriendsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LoginScreen(Modifier)
                }
            }
        }
    }
}

@Composable
fun LoginScreen(modifier: Modifier){
    Column (Modifier.padding(24.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally){
        Image(
            painter = painterResource(id = R.drawable.shoppingcart),
            contentDescription = null, // or null if purely decorative
            modifier = Modifier.fillMaxWidth(.75f),
            contentScale = ContentScale.Fit,      // Crop/Fill/Fit as needed
        )
        Text(stringResource( R.string.app_name), fontSize = 32.sp, modifier = Modifier.padding(vertical = 15.dp))
        GoogleSignInButton {  }
    }
}

@Composable
fun GoogleSignInButton(onClick: () -> Unit) {
    AndroidView(
        factory = { context ->
            SignInButton(context).apply {
                setSize(SignInButton.SIZE_WIDE)   // WIDE, STANDARD, ICON_ONLY
                setColorScheme(SignInButton.COLOR_DARK) // or COLOR_DARK
                setOnClickListener { onClick() }
            }
        },
        update = { /* optional styling */ }
    )
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ShoppingWithFriendsTheme {
        Greeting("Android")
    }
}