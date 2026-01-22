package com.example.shoppingwithfriends.features.edit_list

import android.util.Log
import android.widget.ImageButton
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.TextFieldLineLimits
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
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
import androidx.compose.ui.text.TextStyle
import com.example.shoppingwithfriends.features.common.Constants


object EditListComposables {
    @Stable
    data class EditListActions(
        val onListNameChanged: (String) -> Unit,
        val onCommitTitleChange: () -> Unit,
        val onAddItem: () -> Unit,
        val onProductCheckedChanged: (String, Boolean) -> Unit,
        val onProductNameChanged: (String, String) -> Unit,
        val onDeleteProduct: (String) -> Unit,
        val onClearFocusRequest: () -> Unit,
    )

    @Composable
    fun EditListRoute(vm : EditListViewModel = hiltViewModel(), listId : String){
        LaunchedEffect(listId){
            vm.refresh(listId)
        }
        val uiState by vm.state.collectAsStateWithLifecycle(
            minActiveState = Lifecycle.State.CREATED
        )
        val focusProductId by vm.focusProductId.collectAsState()

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
                onDeleteProduct = vm::deleteProduct,
                onClearFocusRequest = vm::clearFocusRequest
            )
        }

        CenterAlignedTopAppBar(uiState = uiState,
            actions = actions,
            products = products,
            focusProductId = focusProductId)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun CenterAlignedTopAppBar(uiState : EditListViewModel.UiState,
                               actions: EditListActions,
                               products : List<LocalProduct>,
                               focusProductId : String?) {
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
                    products = products,
                    focusProductId = focusProductId)
                }
            }
        }
    }

    @Composable
    fun EditListScreen(modifier: Modifier,
                       actions: EditListActions,
                       uiState : EditListViewModel.UiState,
                       products : List<LocalProduct>,
                       focusProductId: String?){
        Column(modifier = modifier.wrapContentHeight()) {
            ListNameField(uiState, actions = actions)
            LazyColumn( modifier = Modifier.weight(1f).padding(horizontal = 4.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)) {
                items(items = products, key = { product -> product.id }) { product ->
                    ProductRow(product = product,
                        actions = actions,
                        shouldRequestFocus = focusProductId == product.id,
                        modifier = Modifier.animateItem())
                }
            }
            AddItemButton(actions.onAddItem)
        }
    }

    @Composable
    fun ProductRow(product : LocalProduct, actions : EditListActions, shouldRequestFocus : Boolean, modifier : Modifier){
        var text by rememberSaveable(product.id) { mutableStateOf(product.content) }
        var isEditing by rememberSaveable(product.id) { mutableStateOf(false) } // I don't want my text to get overwritten if the databse emits while we are editing
        val focusRequester = remember { FocusRequester() }

        LaunchedEffect(product.id, product.content) {
            if (!isEditing && text != product.content) {
                text = product.content
            }
        }
        LaunchedEffect(shouldRequestFocus) {
            if (shouldRequestFocus) {
                isEditing = true
                focusRequester.requestFocus()
                actions.onClearFocusRequest()
            }
        }
        LaunchedEffect(isEditing) {
            if (isEditing) focusRequester.requestFocus()
        }
        OutlinedCard(modifier = modifier) {
            Row(modifier = Modifier
                .fillMaxWidth()){
                Checkbox(checked = product.isChecked,
                    modifier = Modifier.weight(1f),
                    onCheckedChange = { isChecked ->
                        actions.onProductCheckedChanged(product.id, isChecked)
                })
                Box(modifier = Modifier.weight(8f)) {
                    if(isEditing){
                        OutlinedTextField(value = text,
                            singleLine = true,
                            onValueChange = {text = it},
                            modifier = Modifier
                                .onFocusChanged { focusState ->
                                    if (focusState.isFocused) {
                                        isEditing = true
                                    } else {
                                        isEditing = false
                                        val trimmed = text.trim()
                                        if (trimmed != product.content) actions.onProductNameChanged(
                                            product.id,
                                            trimmed
                                        )
                                    }
                                }
                                .focusRequester(focusRequester),
                            textStyle = TextStyle(fontSize = Constants.REGULAR_TEXTSIZE)
                        )
                    }
                    else{
                        Text(
                            text = text,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .clickable { isEditing = true },
                            fontSize = Constants.REGULAR_TEXTSIZE
                        )
                    }
                }
                IconButton(onClick = {actions.onDeleteProduct(product.id)}, Modifier.weight(1f)) {
                    Icon(Icons.Filled.Delete, contentDescription = "Delete button")
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
                .onFocusChanged { if (!it.hasFocus) actions.onCommitTitleChange() },
            textStyle = TextStyle(fontSize = Constants.LARGER_TEXTSIZE))
    }
}