package com.example.blogapp.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.example.blogapp.R
import com.example.blogapp.viewmodel.ThemeViewModel

@Composable
fun ThemeToggleButton(themeViewModel: ThemeViewModel) {
    val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()

    IconButton(onClick = { themeViewModel.toggleTheme() }) {
        Icon(
            imageVector = ImageVector.vectorResource(if (isDarkTheme) R.drawable.ic_sun else R.drawable.ic_moon),
            contentDescription = "Toggle Theme"
        )
    }
}
