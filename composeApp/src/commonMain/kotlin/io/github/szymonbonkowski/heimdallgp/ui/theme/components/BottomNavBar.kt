package io.github.szymonbonkowski.heimdallgp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.szymonbonkowski.heimdallgp.model.DashboardTab
import io.github.szymonbonkowski.heimdallgp.ui.theme.HeimdallColors

@Composable
fun BottomNavBar(
    currentTab: DashboardTab,
    onTabSelected: (DashboardTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(HeimdallColors.Background)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NavButton(
            icon = Icons.Default.List,
            label = "Leaderboard",
            isSelected = currentTab == DashboardTab.LEADERBOARD,
            onClick = { onTabSelected(DashboardTab.LEADERBOARD) }
        )
        NavButton(
            icon = Icons.Default.Phone,
            label = "Team Radio",
            isSelected = currentTab == DashboardTab.TEAM_RADIO,
            onClick = { onTabSelected(DashboardTab.TEAM_RADIO) }
        )
        NavButton(
            icon = Icons.Default.Info,
            label = "Race Data",
            isSelected = currentTab == DashboardTab.RACE_DATA,
            onClick = { onTabSelected(DashboardTab.RACE_DATA) }
        )
    }
}

@Composable
fun NavButton(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val color = if (isSelected) HeimdallColors.TireSoft else HeimdallColors.TextSecondary
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }.padding(8.dp)
    ) {
        Icon(icon, contentDescription = label, tint = color, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, color = color, fontSize = 10.sp)
    }
}
