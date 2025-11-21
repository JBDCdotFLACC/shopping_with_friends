package com.example.shoppingwithfriends.features.edit_list

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.shoppingwithfriends.R
import com.example.shoppingwithfriends.features.common.CommonComposables.AppScaffold
import com.example.shoppingwithfriends.features.homescreen.HomeScreenComposables.Error
import com.example.shoppingwithfriends.features.homescreen.HomeScreenComposables.Loading
import com.example.shoppingwithfriends.features.homescreen.HomeScreenComposables.ShoppingListHomeScreen
import com.example.shoppingwithfriends.features.homescreen.HomeScreenViewModel

object EditListComposables {
    @Composable
    fun EditListRoute(vm : EditListViewModel = hiltViewModel(), listId : String){
        vm.refresh(listId)
        val uiState by vm.state.collectAsState()
        CenterAlignedTopAppBar(uiState)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun CenterAlignedTopAppBar(uiState : EditListViewModel.UiState) {
        AppScaffold(
            title = {
                Text(
                    text = stringResource(R.string.app_name),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            navigationIcon = {
                IconButton(onClick = { /* TODO */ }) {
                    Icon(Icons.Filled.Person, contentDescription = "Profile")
                }
            },
            actions = {
                IconButton(onClick = { /* open menu */ }) {
                    Icon(Icons.Filled.Menu, contentDescription = "Menu")
                }
            }
        ) { innerPadding ->
            when {
                uiState.isLoading -> Loading(innerPadding)
                uiState.error != null -> Error(innerPadding)
                else -> EditListScreen(Modifier.fillMaxSize())
            }
        }
    }

    @Composable
    fun EditListScreen(modifier: Modifier){

    }

}