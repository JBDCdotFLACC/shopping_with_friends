package com.example.shoppingwithfriends

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.shoppingwithfriends.auth.AuthRepository
import com.example.shoppingwithfriends.auth.AuthViewModel
import com.example.shoppingwithfriends.features.edit_list.EditListComposables
import com.example.shoppingwithfriends.features.homescreen.HomeScreenComposables
import com.example.shoppingwithfriends.features.login.LoginScreenComposables.LoginScreen
import com.example.shoppingwithfriends.ui.theme.ShoppingWithFriendsTheme
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import kotlinx.serialization.Serializable


@AndroidEntryPoint
class MainActivity @Inject constructor()
    : ComponentActivity() {
    @Inject lateinit var authRepo: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShoppingWithFriendsTheme {
                MyApp()
            }
        }
    }

    @Composable
    fun MyApp(authViewModel: AuthViewModel = hiltViewModel()) {
        val user by authViewModel.currentUser.collectAsStateWithLifecycle()
        // IMPORTANT: key the backstack by auth state so it resets when login/logout happens
        val start = if (user == null) Login else Home
        key(start) {
            val backStack = rememberNavBackStack(start)
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
                            LoginScreen(onSuccess = {backStack.add(Home)})
                        }
                        is Home -> NavEntry(key) {
                            HomeScreenComposables.HomeRoute(goToEditList = {id -> backStack.add(EditList(id))})
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

}
@Serializable data object Login : NavKey
@Serializable data object Home : NavKey
@Serializable data class EditList(val listId: String) : NavKey