package com.example.shoppingwithfriends.features.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

object OnboardingComposables {
    @Composable
    fun OnboardingScreen(vm : OnboardingViewModel = hiltViewModel(),
                         onSuccess: () -> Unit){
        val uiState by vm.state.collectAsState()
        if(uiState.isSubmitted){
            onSuccess()
        }
        else{
            Column {
                Text("Enter the name you want displayed to your friends (you can change it later)")
                TextField(value = uiState.displayName, onValueChange = vm::onDisplayNameChanged, modifier = Modifier.fillMaxWidth())
                Button(onClick = vm::onSubmitUser) { Text("Submit") }
            }
        }
    }
}