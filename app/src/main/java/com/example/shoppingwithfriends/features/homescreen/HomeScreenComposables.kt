package com.example.shoppingwithfriends.features.homescreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
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
                  goToEditList: (id : String) -> Unit){
        val uiState by vm.state.collectAsState()
        val shoppingLists by vm.shoppingLists.collectAsStateWithLifecycle(
            minActiveState = Lifecycle.State.CREATED
        )
        LaunchedEffect(Unit) {
            vm.events.collect { event ->
                when (event) {
                    is AddListEvent.Success -> {
                        goToEditList(event.id)
                    }
                    is AddListEvent.Error -> {
                        //TODO

                    }
                }
            }
        }
        CenterAlignedTopAppBar(uiState = uiState, shoppingLists, goToEditList,
            vm::submit, vm::deleteShoppingList)
    }
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun CenterAlignedTopAppBar(uiState : HomeScreenViewModel.UiState,
                                shoppingLists: List<LocalShoppingList>,
                                goToEditList: (String) -> Unit,
                                submitFormName : (String) -> Unit,
                               deleteShoppingList: (String) -> Unit) {
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
                else -> ShoppingListHomeScreen(Modifier.padding(innerPadding),
                    goToEditList,
                    shoppingLists,
                    submitFormName,
                    deleteShoppingList)
            }
        }
    }

    @Composable
    fun ShoppingListHomeScreen(modifier: Modifier,
                               goToEditList: (String) -> Unit,
                               shoppingLists: List<LocalShoppingList>,
                               submitFormName : (String) -> Unit,
                               deleteShoppingList: (String) -> Unit){
        Column(modifier) {
            ShoppingListLists( goToEditList, shoppingLists, deleteShoppingList, Modifier.weight(9f))
            AddListButton( submitFormName, Modifier.weight(1f))
        }

    }

    @Composable
    fun AddListButton(submitFormName : (String) -> Unit, modifier : Modifier) {
        var showDialog by rememberSaveable { mutableStateOf(false) }

        Button(onClick = {
            showDialog = true
        }, modifier = modifier.padding(2.dp)) {
            Text("Add new list")
        }
        if(showDialog){
            AddNewListDialog(initialValue = "", onConfirm = {name->
                submitFormName(name)
                showDialog = false
                }, onDismiss = {showDialog = false})
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun ShoppingListLists(goToEditList: (String) -> Unit,
                          shoppingLists: List<LocalShoppingList>,
                          deleteShoppingList: (String) -> Unit,
                          modifier: Modifier){
        LazyColumn(modifier = modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),) {
            items(items = shoppingLists, key = { shoppingList -> shoppingList.id }) { shoppingList ->
                ShoppingListRow(shoppingList, goToEditList, deleteShoppingList, Modifier.animateItem())
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
    fun AddNewListDialog(
        initialValue: String = "",
        onConfirm: (String) -> Unit,
        onDismiss: () -> Unit
    ) {
        var text by rememberSaveable { mutableStateOf(initialValue) }

        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Enter list name") },
            text = {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Name") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { onConfirm(text) },
                    enabled = text.isNotBlank()
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        )
    }

    @Composable
    fun DeleteListAlertDialog(
        onConfirm: (String) -> Unit,
        onDismiss: () -> Unit,
        shoppingList: LocalShoppingList
    ) {
        val message = if(shoppingList.name.isBlank()){
            "Are you sure you want to delete this list?"
        }  else {
            "Are you sure you want to delete the list ${shoppingList.name}?"
        }
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(message) },
            confirmButton = {
                TextButton(
                    onClick = { onConfirm(shoppingList.id) },
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun ShoppingListRow(shoppingList: LocalShoppingList,
                        goToEditList: (String) -> Unit,
                        deleteShoppingList: (String) -> Unit,
                        modifier: Modifier) {
        val haptics = LocalHapticFeedback.current
        var showDialog by rememberSaveable { mutableStateOf(false) }
        val formattedDate = Instant.ofEpochMilli(shoppingList.date)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
            .format(DateTimeFormatter.ofPattern("MMM d, yyyy"))
        Card{
            Column(modifier
                .fillMaxSize()
                .padding(8.dp)
                .combinedClickable(
                    onClick = { goToEditList(shoppingList.id) },
                    onLongClick = {
                        showDialog = true
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                ),
                verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(shoppingList.name)
                Text("Date: $formattedDate")
            }
        }

        if(showDialog){
            DeleteListAlertDialog(
                onConfirm = {id->
                    deleteShoppingList(id)
                showDialog = false
            }, onDismiss = {showDialog = false},
                shoppingList = shoppingList)
        }
    }
}
