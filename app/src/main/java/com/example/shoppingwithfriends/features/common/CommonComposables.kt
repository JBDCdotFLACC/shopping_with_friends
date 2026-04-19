package com.example.shoppingwithfriends.features.common

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.shoppingwithfriends.R
import com.example.shoppingwithfriends.auth.AuthViewModel
import jakarta.inject.Inject


object CommonComposables {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AppScaffold(
        modifier: Modifier = Modifier,
        scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(
            rememberTopAppBarState()
        ),
        // Screen content (gets the inner padding from Scaffold)
        content: @Composable (PaddingValues) -> Unit,
        authViewModel: AuthViewModel = hiltViewModel()
    ) {
        var menuExpanded by remember { mutableStateOf(false) }
        Scaffold(
            modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                Box(Modifier.onSizeChanged {  }) {
                    CenterAlignedTopAppBar(
                        windowInsets = WindowInsets.statusBars,
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = {
                            Text(
                                text = stringResource(R.string.app_name),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        navigationIcon = {

                            IconButton(onClick = { }) {
                                Icon(Icons.Filled.Person, contentDescription = "Profile")
                            }
                        },
                        actions = {
                            GlobalDropdownMenu(
                                expanded = menuExpanded,
                                onDismissRequest = { menuExpanded = false },
                                authViewModel = authViewModel
                            )
                            IconButton(onClick = {  menuExpanded = true }) {
                                Icon(Icons.Filled.Menu, contentDescription = "Menu")
                            }
                        },
                        scrollBehavior = scrollBehavior
                    )

                }

            },
        ) { innerPadding ->
            content(innerPadding)
        }
    }

    @Composable
    fun GlobalDropdownMenu(
        expanded: Boolean,
        onDismissRequest: () -> Unit,
        authViewModel: AuthViewModel
    ) {
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismissRequest
        ) {
            DropdownMenuItem(
                text = { Text("Profile") },
                onClick = { onDismissRequest() }
            )
            DropdownMenuItem(
                text = { Text("Logout") },

                onClick = {
                    authViewModel.logout()
                    onDismissRequest() }
            )
        }
    }
}