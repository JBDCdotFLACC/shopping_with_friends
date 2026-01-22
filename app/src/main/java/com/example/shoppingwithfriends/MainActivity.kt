package com.example.shoppingwithfriends

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.shoppingwithfriends.features.add_list.AddListComposables
import com.example.shoppingwithfriends.features.edit_list.EditListComposables
import com.example.shoppingwithfriends.features.homescreen.HomeScreenComposables
import com.example.shoppingwithfriends.features.login.LoginScreenComposables
import com.example.shoppingwithfriends.ui.theme.ShoppingWithFriendsTheme
import com.example.shoppingwithfriends.features.login.LoginScreenComposables.LoginScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.Serializable


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShoppingWithFriendsTheme {
                    MyApp()
            }
        }
    }

    @Composable
    fun MyApp() {
        // Create a back stack, specifying the key the app should start with
        val backStack = rememberNavBackStack(Login)
        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            transitionSpec = {
                slideInHorizontally(
                    animationSpec = tween(220),
                    initialOffsetX = { fullWidth -> fullWidth }
                ) togetherWith slideOutHorizontally(
                    animationSpec = tween(220),
                    targetOffsetX = { fullWidth -> -fullWidth / 3 }
                )
            },

            // Back navigation (pop)
            popTransitionSpec = {
                slideInHorizontally(
                    animationSpec = tween(220),
                    initialOffsetX = { fullWidth -> -fullWidth / 3 }
                ) togetherWith slideOutHorizontally(
                    animationSpec = tween(220),
                    targetOffsetX = { fullWidth -> fullWidth }
                )
            },
            entryProvider = { key ->
                when (key) {
                    is Login -> NavEntry(key) {
                        LoginScreen({backStack.add(Home)})
                    }
                    is Home -> NavEntry(key) {
                        HomeScreenComposables.HomeRoute(goToEditList = {id -> backStack.add(EditList(id))})
                    }
                    is AddList -> NavEntry(key){
                        AddListComposables.AddListRoute(goToList = { id ->
                            backStack.add(EditList(id))
                        })
                    }
                    is EditList -> NavEntry(key){
                        EditListComposables.EditListRoute(listId = key.listId)
                    }
                    else -> NavEntry(key) { Text("Unknown route") }
                }
            }
        )
    }
}
@Serializable data object Login : NavKey
@Serializable data object Home : NavKey
@Serializable data object AddList : NavKey
@Serializable data class EditList(val listId: String) : NavKey