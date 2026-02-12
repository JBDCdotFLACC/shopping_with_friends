package com.example.shoppingwithfriends.features.login

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerDefaults.scrimColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.credentials.CustomCredential
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.shoppingwithfriends.R
import com.google.android.gms.common.SignInButton
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch

object LoginScreenComposables {
    @Composable
    fun LoginScreen(vm : LoginViewModel = hiltViewModel(),
                    onSuccess: () -> Unit){
        val uiState by vm.uiState.collectAsState()
        LoginScreenLayout( isLoading = uiState == LoginState.Loading,
            vm::firebaseAuthWithGoogle)
    }


    @Composable
    fun LoginScreenLayout(isLoading : Boolean, sendTokenToVm : (String) -> Unit){
        Box {
            LoginScreenContent(sendTokenToVm)
            if (isLoading) Box(Modifier
                .fillMaxSize()
                .background(scrimColor)
                .clickable(enabled = false) {})
            {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            } }
    }


    @Composable
    fun LoginScreenContent(sendTokenToVm : (String) -> Unit){
        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()
        Column (Modifier
            .padding(24.dp)
            .fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally){
            Image(
                painter = painterResource(id = R.drawable.shoppingcart),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth(.75f),
                contentScale = ContentScale.Fit,
            )
            Text(stringResource( R.string.app_name), fontSize = 32.sp, modifier = Modifier.padding(vertical = 15.dp))
            GoogleSignInButton {
                coroutineScope.launch {
                    val idToken = getGoogleIdToken(context)
                    sendTokenToVm(idToken)
                }
            }
        }
    }
    @Composable
    fun GoogleSignInButton(onClick: () -> Unit) {
        AndroidView(
            factory = { context ->
                SignInButton(context).apply {
                    setSize(SignInButton.SIZE_WIDE)
                    setColorScheme(SignInButton.COLOR_DARK)
                    setOnClickListener { onClick() }
                }
            },
            update = { /* optional styling */ }
        )
    }

    suspend fun getGoogleIdToken(context: Context): String {
        val credentialManager = CredentialManager.create(context)

        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId(context.getString(R.string.default_web_client_id))
            .setFilterByAuthorizedAccounts(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val result = credentialManager.getCredential(
            context = context,
            request = request
        )

        val credential = result.credential

        if (credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            val googleIdTokenCredential =
                GoogleIdTokenCredential.createFrom(credential.data)

            return googleIdTokenCredential.idToken
        } else {
            throw IllegalStateException("Unexpected credential type")
        }
    }
}