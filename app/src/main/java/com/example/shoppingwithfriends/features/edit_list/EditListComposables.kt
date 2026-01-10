package com.example.shoppingwithfriends.features.edit_list

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.room.util.TableInfo
import com.example.shoppingwithfriends.R
import com.example.shoppingwithfriends.data.source.local.LocalProduct
import com.example.shoppingwithfriends.features.common.CommonComposables.AppScaffold
import com.example.shoppingwithfriends.features.homescreen.HomeScreenComposables.Error
import com.example.shoppingwithfriends.features.homescreen.HomeScreenComposables.Loading


object EditListComposables {
    @Composable
    fun EditListRoute(vm : EditListViewModel = hiltViewModel(), listId : String){
        LaunchedEffect(listId){
            vm.refresh(listId)
        }
        val uiState by vm.state.collectAsState()
        DisposableEffect(Unit) {
            onDispose {
                vm.onPause()   // e.g. save title, commit edits, etc.
            }
        }
        CenterAlignedTopAppBar(uiState = uiState,
            onListNameChanged = vm::onListNameChanged,
            onCommitTitleChange = vm::onPause)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun CenterAlignedTopAppBar(uiState : EditListViewModel.UiState,
                               onListNameChanged: (String) -> Unit,
                               onCommitTitleChange: () -> Unit) {
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
                else -> EditListScreen(modifier = Modifier.fillMaxSize().padding(paddingValues = innerPadding),
                    uiState = uiState,
                    onListNameChanged = onListNameChanged,
                    onCommitTitleChange = onCommitTitleChange)
            }
        }
    }

    @Composable
    fun EditListScreen(modifier: Modifier,
                       uiState : EditListViewModel.UiState,
                       onListNameChanged: (String) -> Unit,
                       onCommitTitleChange : () -> Unit){
        Column(modifier = modifier) {
        ListNameField(modifier, uiState, onListNameChanged, onCommitTitleChange)
        }
    }

    @Composable
    fun ProductRow(modifier: Modifier, product: LocalProduct){
        Row(modifier = modifier){
           // Checkbox(isChecked)
        }
    }

    @Composable
    fun ListNameField(modifier: Modifier,
                      uiState : EditListViewModel.UiState,
                      onListNameChanged: (String) -> Unit,
                      onCommitTitleChange: () -> Unit){
        TextField(value = uiState.listName,
            onValueChange = {onListNameChanged(it)},
            modifier = modifier.fillMaxWidth().onFocusChanged({if (!it.hasFocus) onCommitTitleChange()}))
        TextField(value = "", onValueChange = {})

    }
}