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
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp


object CommonComposables {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AppScaffold(
        modifier: Modifier = Modifier,
        // Top bar slots
        title: @Composable () -> Unit,
        navigationIcon: @Composable () -> Unit = {},
        actions: @Composable RowScope.() -> Unit = {},
        scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(
            rememberTopAppBarState()
        ),
        // Screen content (gets the inner padding from Scaffold)
        content: @Composable (PaddingValues) -> Unit
    ) {
        Scaffold(
            modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                Box(Modifier.onSizeChanged { Log.i("wxyz", "topBar px=${it.height}") }) {
                    CenterAlignedTopAppBar(
                        windowInsets = WindowInsets.statusBars,   // ✅ key change
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = title,
                        navigationIcon = navigationIcon,
                        actions = actions,
                        scrollBehavior = scrollBehavior
                    )

                }

            },
        ) { innerPadding ->
            content(innerPadding)
        }
    }
}