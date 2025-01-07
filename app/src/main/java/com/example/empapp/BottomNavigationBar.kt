package com.example.empapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CandlestickChart
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Widgets
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.empapp.Data.BottomNavigation

val items = listOf(
    BottomNavigation(
        title = "Home",
        icon = Icons.Rounded.Home,
        route = "home"
    ),

    BottomNavigation(
        title = "Charts",
        icon = Icons.Rounded.CandlestickChart,
        route = "charts"
    ),

    BottomNavigation(
        title = "Explore",
        icon = Icons.Rounded.Explore,
        route = "explore"
    ),

    BottomNavigation(
        title = "Favourites",
        icon = Icons.Rounded.Star,
        route = "favourites"
    )
)


@Composable
fun BottomNavigationBar(navController: NavController) {
    NavigationBar {
        Row (
            modifier = Modifier.background(MaterialTheme.colorScheme.inverseOnSurface)
        ) {
            items.forEach { item ->
                NavigationBarItem(
                    selected =  navController.currentBackStackEntryAsState().value?.destination?.route == item.route,
                    onClick = {navController.navigate(item.route)},
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    },
                    label = {
                        Text(
                            text = item.title,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BottomNavigationBarPreview() {
    val previewNavController = rememberNavController()
    BottomNavigationBar(navController = previewNavController)
}