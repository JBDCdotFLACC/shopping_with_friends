package com.example.shoppingwithfriends.features.common

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.error
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.shoppingwithfriends.R
import com.example.shoppingwithfriends.auth.AuthViewModel
import com.example.shoppingwithfriends.data.FriendRepository
import com.example.shoppingwithfriends.features.search.FriendRequestViewModel


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
        authViewModel: AuthViewModel = hiltViewModel(),
        friendRequestViewModel : FriendRequestViewModel = hiltViewModel()
    ) {
        var menuExpanded by remember { mutableStateOf(false) }
        var friendMenuExpanded by remember { mutableStateOf(false) }
        var showSearchDialog by remember { mutableStateOf(false) }
        var showFriendRequestDialog by remember { mutableStateOf(false) }
        val searchState by friendRequestViewModel.state.collectAsStateWithLifecycle()
        val snackbarHostState = remember { SnackbarHostState() }
        LaunchedEffect(searchState.friendRequestResult) {
            friendRequestViewModel.clearSearchResults()
            val requestedName = searchState.searchResult?.displayName
            searchState.friendRequestResult?.let { friendRequestResponse ->
                val message = when(friendRequestResponse){
                    FriendRepository.FriendRequestResponse.PENDING_SENT -> "You have already sent a friend request to $requestedName"
                    FriendRepository.FriendRequestResponse.ALREADY_FRIEND -> "You are already friends with $requestedName"
                    FriendRepository.FriendRequestResponse.SUCCESS -> "Friend request sent!"
                    FriendRepository.FriendRequestResponse.PENDING_RECEIVED -> "$requestedName has already sent you a friend request!"

                }
                snackbarHostState.showSnackbar(
                    message = message,
                    duration = SnackbarDuration.Short
                )
                friendRequestViewModel.clearState()
            }
        }

        if(searchState.searchResult != null){
            Log.i("wxyz", "we have search results!")
            showSearchDialog = false
            showFriendRequestDialog = true
        }
        else{
            Log.i("wxyz", "we do not have search results")
            showFriendRequestDialog = false
        }
        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
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
                            FriendsDropDownMenu (
                                expanded = friendMenuExpanded,
                                onDismissRequest = { friendMenuExpanded = false },
                                openFriendSearchDialog = {
                                    showSearchDialog = true
                                    friendMenuExpanded = false // close the menu
                                }
                            )
                            IconButton(onClick = { friendMenuExpanded = true }) {
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
            if (showSearchDialog) {
                FriendSearchDialog(
                    onDismiss =
                        {
                            showSearchDialog = false
                            friendRequestViewModel.clearState()
                        },
                    onSearch = { searchTerm ->
                        friendRequestViewModel.searchForFriend(searchTerm)
                    },
                    searchState = searchState
                )
            }
            if(showFriendRequestDialog){
                SendFriendRequestDialog(
                    onConfirm = friendRequestViewModel::sendFriendRequest,
                    uiState = searchState,
                    onDismiss = {
                        showFriendRequestDialog = false
                        friendRequestViewModel.clearState()
                    }
                )
            }
        }
    }

    @Composable
    fun FriendsDropDownMenu(
        expanded: Boolean,
        onDismissRequest: () -> Unit,
        openFriendSearchDialog: () -> Unit
    ){
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismissRequest
        ){
            DropdownMenuItem(
                text = { Text("Add a Friend") },
                onClick = { openFriendSearchDialog() }
            )
        }
    }

    @Composable
    fun FriendSearchDialog(
        onDismiss: () -> Unit,
        onSearch: (String) -> Unit,
        searchState: FriendRequestViewModel.UiState
    ) {
        var textFieldValue by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Search for a Friend") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Enter your friend's email address or phone number below.")
                    OutlinedTextField(
                        value = textFieldValue,
                        onValueChange = { textFieldValue = it },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (searchState.isLoading) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }

                    searchState.error?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { onSearch(textFieldValue) }
                ) {
                    Text("Search")
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
    fun SendFriendRequestDialog(onConfirm: (String) -> Unit,
                                uiState : FriendRequestViewModel.UiState,
                                onDismiss : () -> Unit){
        val friendName = uiState.searchResult?.displayName
        if(friendName == null) return

        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Send friend request to $friendName") },
            confirmButton = {
                TextButton(
                    onClick = { onConfirm(uiState.searchResult.id) },
                ) {
                    Text("Confirm")
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