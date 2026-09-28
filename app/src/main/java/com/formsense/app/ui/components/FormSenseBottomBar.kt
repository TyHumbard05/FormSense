package com.formsense.app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.formsense.app.navigation.Routes
import com.formsense.app.ui.theme.FormBlue
import com.formsense.app.ui.theme.FormBlueSoft

private data class BottomDestination(
    val route: String,
    val label: String,
    val icon: ImageVector,
)

private val destinations = listOf(
    BottomDestination(Routes.Home, "Home", Icons.Rounded.Home),
    BottomDestination(Routes.Sessions, "Sessions", Icons.Rounded.History),
    BottomDestination(Routes.Progress, "Progress", Icons.Rounded.BarChart),
    BottomDestination(Routes.Profile, "Profile", Icons.Rounded.Person),
)

@Composable
fun FormSenseBottomBar(
    selectedRoute: String,
    onDestinationSelected: (String) -> Unit,
) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        destinations.forEach { destination ->
            val selected = selectedRoute == destination.route
            NavigationBarItem(
                selected = selected,
                onClick = { onDestinationSelected(destination.route) },
                icon = { Icon(destination.icon, contentDescription = destination.label) },
                label = { Text(destination.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = FormBlue,
                    selectedTextColor = FormBlue,
                    indicatorColor = FormBlueSoft,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            )
        }
    }
}
