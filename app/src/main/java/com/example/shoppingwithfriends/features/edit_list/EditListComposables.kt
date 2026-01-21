package com.example.shoppingwithfriends.features.edit_list

import android.util.Log
import android.widget.ImageButton
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
    @Stable
    data class EditListActions(
        val onListNameChanged: (String) -> Unit,
        val onCommitTitleChange: () -> Unit,
        val onAddItem: () -> Unit,
        val onProductCheckedChanged: (String, Boolean) -> Unit,
        val onProductNameChanged: (String, String) -> Unit,
        val onDeleteProduct: (String) -> Unit
    )

    @Composable
    fun EditListRoute(vm : EditListViewModel = hiltViewModel(), listId : String){
        LaunchedEffect(listId){
            vm.refresh(listId)
        }
        val uiState by vm.state.collectAsStateWithLifecycle(
            minActiveState = Lifecycle.State.CREATED
        )
        val products by vm.products.collectAsStateWithLifecycle(
            minActiveState = Lifecycle.State.CREATED
        )
        DisposableEffect(Unit) {
            onDispose {
                vm.onPause()
            }
        }

        val actions = remember(vm) {
            EditListActions(
                onListNameChanged = vm::onListNameChanged,
                onCommitTitleChange = vm::onPause,
                onAddItem = vm::addItem,
                onProductCheckedChanged = vm::onCheckChanged,
                onProductNameChanged = vm::onProductNameChanged,
                onDeleteProduct = vm::deleteProduct
            )
        }

        CenterAlignedTopAppBar(uiState = uiState,
            actions = actions,
            products = products)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun CenterAlignedTopAppBar(uiState : EditListViewModel.UiState,
                               actions: EditListActions,
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
                    actions = actions,
                    uiState = uiState,
                    products = products)
                }
            }
        }
    }

    @Composable
    fun EditListScreen(modifier: Modifier,
                       actions: EditListActions,
                       uiState : EditListViewModel.UiState,
                       products : List<LocalProduct>){
        Column(modifier = modifier.wrapContentHeight()) {
            ListNameField(uiState, actions = actions)
            LazyColumn( modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)) {
                items(items = products, key = { product -> product.id }) { product ->
                    ProductRow(product = product, actions = actions)
                }
            }
            AddItemButton(actions.onAddItem)
        }
    }

    @Composable
    fun ProductRow(product : LocalProduct, actions : EditListActions){
        var text by rememberSaveable(product.id) { mutableStateOf(product.content) }
        var isEditing by remember(product.id) { mutableStateOf(false) } // I don't want my text to get overwritten if the databse emits while we are editing
        LaunchedEffect(product.id, product.content, isEditing) {
            //I don't want the text field to be overwritten if the database emits while we are editing
            if (!isEditing) text = product.content
        }
        OutlinedCard {
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp)){
                Checkbox(checked = product.isChecked,
                    onCheckedChange = { isChecked ->
                        actions.onProductCheckedChanged(product.id, isChecked)
                })
                OutlinedTextField(value = text,
                    onValueChange = {text = it},
                    modifier = Modifier.onFocusChanged{
                        focusState ->
                        if(focusState.isFocused){
                            isEditing = true
                        }
                        else{
                            isEditing = false
                            val trimmed = text.trim()
                            if(trimmed != product.content) actions.onProductNameChanged(product.id, trimmed)
                        }
                    }
                    )
                IconButton(onClick = {actions.onDeleteProduct(product.id)}) {
                    Icon(Icons.Filled.Delete, contentDescription = "Menu")
                }

            }
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
                      actions: EditListActions){
        TextField(value = uiState.listName,
            onValueChange = {actions.onListNameChanged(it)},
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .onFocusChanged { if (!it.hasFocus) actions.onCommitTitleChange() })
    }
}