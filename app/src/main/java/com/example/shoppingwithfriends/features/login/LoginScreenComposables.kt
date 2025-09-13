package com.example.shoppingwithfriends.features.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.shoppingwithfriends.R
import com.google.android.gms.common.SignInButton

object LoginScreenComposables {
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
}