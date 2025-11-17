package com.example.shoppingwithfriends.features.add_list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.shoppingwithfriends.R
import com.example.shoppingwithfriends.features.common.CommonComposables.AppScaffold

object AddListComposables {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AddListRoute(vm : AddListViewModel = hiltViewModel(), goToHome: () -> Unit){
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
            Column(Modifier.padding(innerPadding)) {
                Row {
                    Text("List Name")
                    TextField(value = vm.listName, onValueChange = {vm.onListNameChanged(it)}, modifier = Modifier.fillMaxWidth())
                }
                Row {
                    Button(onClick = {
                        vm.submit()
                    }) {
                        Text("Create List")
                    }
                }
            }
        }
    }
}