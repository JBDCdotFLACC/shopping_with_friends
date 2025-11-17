package com.example.shoppingwithfriends

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.example.shoppingwithfriends.features.add_list.AddListComposables
import com.example.shoppingwithfriends.features.homescreen.HomeScreenComposables
import com.example.shoppingwithfriends.features.login.LoginScreenComposables
import com.example.shoppingwithfriends.ui.theme.ShoppingWithFriendsTheme
import com.example.shoppingwithfriends.features.login.LoginScreenComposables.LoginScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShoppingWithFriendsTheme {
                Scaffold(modifier = Modifier.fillMaxSize().background(color = Color.White)) { innerPadding ->
                    MyApp()
                }
            }
        }
    }

    @Composable
    fun MyApp() {
        // Create a back stack, specifying the key the app should start with
        val backStack = remember { mutableStateListOf<Any>(Login) }
        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            entryProvider = { key ->
                when (key) {
                    is Login -> NavEntry(key) {
                        LoginScreen({backStack.add(Home)})
                    }
                    is Home -> NavEntry(key) {
                        HomeScreenComposables.HomeRoute(goToAddList = {backStack.add(AddList)})
                    }
                    is List -> NavEntry(key){

                    }
                    is AddList -> NavEntry(key){
                        AddListComposables.AddListRoute(goToHome = {backStack.removeLastOrNull()})
                    }
                    else -> NavEntry(Unit) { Text("Unknown route") }
                }
            }
        )
    }
}
data object Login
data object Home
data object List
data object AddList
