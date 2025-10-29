package com.example.shoppingwithfriends.features.homescreen

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
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.shoppingwithfriends.R
import com.example.shoppingwithfriends.models.ShoppingList
import com.example.shoppingwithfriends.viewmodels.HomeScreenViewModel


object HomeScreenComposables {
    @Composable
    fun HomeRoute(vm : HomeScreenViewModel = hiltViewModel()){
        val uiState by vm.state.collectAsStateWithLifecycle()
        CenterAlignedTopAppBar(uiState)
    }
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun CenterAlignedTopAppBar(uiState : HomeScreenViewModel.UiState) {
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),

            topBar = {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
                    title = {
                        Text(
                            stringResource(R.string.app_name),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { /* do something */ }) {
                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = "Localized description"
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* do something */ }) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = "Localized description"
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior,
                )
            },
        ) { innerPadding ->

            when {
                uiState.isLoading -> Loading(innerPadding)
                uiState.error != null -> Error(innerPadding)
                else -> ShoppingListHomeScreen(innerPadding, uiState)
            }
        }
    }

    @Composable
    fun ShoppingListHomeScreen(innerPadding: PaddingValues, uiState: HomeScreenViewModel.UiState){
        Column(Modifier.padding(innerPadding)) {
            ShoppingListLists(innerPadding, uiState)
            AddListButton(innerPadding)
        }

    }

    @Composable
    fun AddListButton(innerPadding: PaddingValues) {
        Button(onClick = { }, modifier = Modifier.padding(innerPadding)) {
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
    fun ShoppingListRow(shoppingList: ShoppingList) {
        Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primary).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(shoppingList.name, color = Color.White)
            Text("Date: ${shoppingList.date}" , color = Color.White)
            Text("${shoppingList.list.size} Items", color = Color.White)
        }

    }

}
