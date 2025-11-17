package com.example.shoppingwithfriends.features.homescreen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.shoppingwithfriends.R
import com.example.shoppingwithfriends.data.source.local.LocalShoppingList
import com.example.shoppingwithfriends.features.common.CommonComposables.AppScaffold
import com.example.shoppingwithfriends.features.homescreen.HomeScreenViewModel


object HomeScreenComposables {
    @Composable
    fun HomeRoute(vm : HomeScreenViewModel = hiltViewModel(), goToAddList: () -> Unit){
        val uiState by vm.state.collectAsState()
        CenterAlignedTopAppBar(uiState, goToAddList)
    }
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun CenterAlignedTopAppBar(uiState : HomeScreenViewModel.UiState, goToAddList: () -> Unit) {
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
                else -> ShoppingListHomeScreen(innerPadding, uiState, goToAddList)
            }
        }
    }

    @Composable
    fun ShoppingListHomeScreen(innerPadding: PaddingValues, uiState: HomeScreenViewModel.UiState, goToAddList: () -> Unit){
        Column(Modifier.padding(innerPadding)) {
            ShoppingListLists(innerPadding, uiState)
            AddListButton(innerPadding, goToAddList)
        }

    }

    @Composable
    fun AddListButton(innerPadding: PaddingValues, goToAddList: () -> Unit) {
        Button(onClick = {goToAddList() }, modifier = Modifier.padding(innerPadding)) {
            Text("Add new list")
        }
    }

    @Composable
    fun ShoppingListLists(innerPadding: PaddingValues, uiState: HomeScreenViewModel.UiState){
        LazyColumn(modifier = Modifier.padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),) {
            items(uiState.items.size) { item ->
                ShoppingListRow(uiState.items[item])
            }
        }
    }

    @Composable
    fun Loading(innerPadding: PaddingValues){
        Text("Is Loading", modifier = Modifier.padding(innerPadding))
    }

    @Composable
    fun Error(innerPadding: PaddingValues) {
        Text("Is Error", modifier = Modifier.padding(innerPadding))
    }

    @Composable
    fun ShoppingListRow(shoppingList: LocalShoppingList) {
        Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primary).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(shoppingList.name, color = Color.White)
            Text("Date: ${shoppingList.date}" , color = Color.White)
        }

    }

}
