package io.github.szymonbonkowski.heimdallgp.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import io.github.szymonbonkowski.heimdallgp.model.DashboardTab
import io.github.szymonbonkowski.heimdallgp.ui.theme.HeimdallColors

@Composable
fun BottomNavBar(
    currentTab: DashboardTab,
    onTabSelected: (DashboardTab) -> Unit
) {
    NavigationBar(
        containerColor = HeimdallColors.Surface,
        contentColor = Color.White
    ) {
        DashboardTab.entries.forEach { tab ->
            NavigationBarItem(
                selected = currentTab == tab,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.label
                    )
                },
                label = { Text(tab.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    selectedTextColor = Color.White,
                    indicatorColor = HeimdallColors.RedBullBlue,
                    unselectedIconColor = HeimdallColors.TextSecondary,
                    unselectedTextColor = HeimdallColors.TextSecondary
                )
            )
        }
    }
}
