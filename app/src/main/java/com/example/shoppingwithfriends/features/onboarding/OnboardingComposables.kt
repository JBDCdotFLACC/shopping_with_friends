package com.example.shoppingwithfriends.features.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.shoppingwithfriends.features.common.Constants

object OnboardingComposables {
    @Composable
    fun OnboardingScreen(vm : OnboardingViewModel = hiltViewModel(),
                         onSuccess: () -> Unit){
        val uiState by vm.state.collectAsState()
        if(uiState.isSubmitted){
            onSuccess()
        }
        else{
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background // Uses the theme's dark color
            ){
                Column(modifier = Modifier.padding(12.dp))  {
                    Text(fontSize = Constants.LARGER_TEXTSIZE, text = "Enter the name you want displayed to your friends (you can change it later)", modifier = Modifier.padding(
                        start = 16.dp,
                        top = 64.dp,
                        end = 16.dp,
                        bottom = 8.dp
                    ))
                    TextField(value = uiState.displayName, onValueChange = vm::onDisplayNameChanged, modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp))
                    Text(fontSize = Constants.LARGER_TEXTSIZE, text = "Enter your phone number if you would like your friends to search for you by your phone number (optional)",
                        modifier = Modifier.padding(
                            start = 16.dp,
                            top = 32.dp,
                            end = 16.dp,
                            bottom = 8.dp
                        ))
                    TextField(value = uiState.phoneNumber, onValueChange = vm::onPhoneNumberChanged, modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )
                    Button(onClick = vm::onSubmitUser, modifier = Modifier.align(Alignment.CenterHorizontally).fillMaxWidth(0.5f)) { Text("Submit") }
                }
            }
        }
    }
}