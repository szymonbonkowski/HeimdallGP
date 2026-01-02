package io.github.szymonbonkowski.heimdallgp

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import io.github.szymonbonkowski.heimdallgp.ui.screens.DashboardScreen
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        DashboardScreen()
    }
}
