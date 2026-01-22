package com.example.shoppingwithfriends.features.homescreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.shoppingwithfriends.R
import com.example.shoppingwithfriends.data.source.local.LocalShoppingList
import com.example.shoppingwithfriends.features.common.CommonComposables.AppScaffold
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter


object HomeScreenComposables {
    @Composable
    fun HomeRoute(vm : HomeScreenViewModel = hiltViewModel(),
                  goToAddList: () -> Unit,
                  goToEditList: (id : String) -> Unit){
        val uiState by vm.state.collectAsState()
        val shoppingLists by vm.shoppingLists.collectAsStateWithLifecycle(
            minActiveState = Lifecycle.State.CREATED
        )
        CenterAlignedTopAppBar(uiState = uiState, shoppingLists, goToAddList, goToEditList)
    }
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun CenterAlignedTopAppBar(uiState : HomeScreenViewModel.UiState,
                                shoppingLists: List<LocalShoppingList>,
                               goToAddList: () -> Unit,
                               goToEditList: (String) -> Unit) {
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
                else -> ShoppingListHomeScreen(innerPadding, goToAddList, goToEditList, shoppingLists)
            }
        }
    }

    @Composable
    fun ShoppingListHomeScreen(innerPadding: PaddingValues,
                               goToAddList: () -> Unit,
                               goToEditList: (String) -> Unit,
                               shoppingLists: List<LocalShoppingList>){
        Column(Modifier.padding(innerPadding)) {
            ShoppingListLists(innerPadding, goToEditList, shoppingLists)
            AddListButton(innerPadding, goToAddList)
        }

    }

    @Composable
    fun AddListButton(innerPadding: PaddingValues, goToAddList: () -> Unit) {
        Button(onClick = {goToAddList() }, modifier = Modifier.padding(innerPadding)) {
            Text("Add new list")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun ShoppingListLists(innerPadding: PaddingValues,
                          goToEditList: (String) -> Unit,
                          shoppingLists: List<LocalShoppingList>){
        LazyColumn(modifier = Modifier.padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),) {
            items(items = shoppingLists, key = { shoppingList -> shoppingList.id }) { shoppingList ->
                ShoppingListRow(shoppingList, goToEditList)
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

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun ShoppingListRow(shoppingList: LocalShoppingList, goToEditList: (String) -> Unit) {
        val formattedDate = Instant.ofEpochMilli(shoppingList.date)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
            .format(DateTimeFormatter.ofPattern("MMM d, yyyy"))
        Column(Modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
            .padding(16.dp)
            .clickable(true){
            goToEditList(shoppingList.id)
        },
            verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(shoppingList.name, color = Color.White)
            Text("Date: $formattedDate" , color = Color.White)
        }

    }

}
