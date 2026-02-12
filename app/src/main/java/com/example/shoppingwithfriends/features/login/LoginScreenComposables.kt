package com.example.shoppingwithfriends.features.login

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.shoppingwithfriends.R
import com.example.shoppingwithfriends.features.homescreen.HomeScreenViewModel
import com.google.android.gms.common.SignInButton
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

object LoginScreenComposables {
    @Composable
    fun LoginScreen(vm : LoginViewModel = hiltViewModel(),
                    onSuccess: () -> Unit){
        val uiState by vm.uiState.collectAsState()
        LoginScreenLayout( isLoading = uiState == LoginState.Loading, onClick = { })
    }


    @Composable
    fun LoginScreenLayout(isLoading : Boolean, onClick: () -> Unit){
        Box {
            LoginScreenContent(onClick)
            if (isLoading) Box(Modifier
                .fillMaxSize()
                .background(scrimColor)
                .clickable(enabled = false) {})
            {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            } }
    }


    @Composable
    fun LoginScreenContent(onClick: () -> Unit){
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
                onClick
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
}