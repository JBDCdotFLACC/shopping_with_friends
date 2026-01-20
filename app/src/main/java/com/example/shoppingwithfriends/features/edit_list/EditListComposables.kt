package com.example.shoppingwithfriends.features.edit_list

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
        val products by vm.products.collectAsStateWithLifecycle(emptyList())
        DisposableEffect(Unit) {
            onDispose {
                vm.onPause()   // e.g. save title, commit edits, etc.
            }
        }
        CenterAlignedTopAppBar(uiState = uiState,
            onListNameChanged = vm::onListNameChanged,
            onCommitTitleChange = vm::onPause,
            onAddItem = vm::addItem,
            products = products)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun CenterAlignedTopAppBar(uiState : EditListViewModel.UiState,
                               onListNameChanged: (String) -> Unit,
                               onCommitTitleChange: () -> Unit,
                               onAddItem: () -> Unit,
                               products : List<LocalProduct>) {
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
            },
        ) { innerPadding ->
            when {
                uiState.isLoading -> Loading(innerPadding)
                uiState.error != null -> Error(innerPadding)
                else -> {EditListScreen(modifier = Modifier.padding(paddingValues = innerPadding),
                    uiState = uiState,
                    onListNameChanged = onListNameChanged,
                    onCommitTitleChange = onCommitTitleChange,
                    onAddItem = onAddItem,
                    products = products)
                }
            }
        }
    }

    @Composable
    fun EditListScreen(modifier: Modifier,
                       uiState : EditListViewModel.UiState,
                       onListNameChanged: (String) -> Unit,
                       onCommitTitleChange : () -> Unit,
                       onAddItem : () -> Unit,
                       products : List<LocalProduct>){
        Column(modifier = modifier.wrapContentHeight()) {
            Log.i("wxyz", products.size.toString())
            ListNameField(uiState, onListNameChanged, onCommitTitleChange)
            LazyColumn {
                items(products.size) { key ->
                    ProductRow(products[key])
                }
            }
            AddItemButton(onAddItem)
        }
    }

    @Composable
    fun ProductRow(product : LocalProduct){
        Row(modifier = Modifier){
           Text("test")
        }
    }

    @Composable
    fun AddItemButton(onClick : () -> Unit){
        Button(onClick = onClick){
            Text("Add a List")
        }

    }

    @Composable
    fun ListNameField(uiState : EditListViewModel.UiState,
                      onListNameChanged: (String) -> Unit,
                      onCommitTitleChange: () -> Unit){
        TextField(value = uiState.listName,
            onValueChange = {onListNameChanged(it)},
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .onFocusChanged({ if (!it.hasFocus) onCommitTitleChange() }))

    }
}