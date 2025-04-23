package com.unibo.cyberopoli.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.unibo.cyberopoli.R
import com.unibo.cyberopoli.ui.navigation.CyberopoliRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route?.substringAfterLast(".")
    CenterAlignedTopAppBar(title = {
        if (currentRoute != null) {
            Text(
                currentRoute, fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
    }, colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
        containerColor = if (currentRoute == CyberopoliRoute.Auth.toString()) MaterialTheme.colorScheme.background
        else MaterialTheme.colorScheme.onSurface
    ), navigationIcon = {
        if (navController.previousBackStackEntry != null) {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(
                    Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }, actions = {
        if (currentRoute != CyberopoliRoute.Settings.toString()) {
            IconButton(onClick = {
                navController.navigate(CyberopoliRoute.Settings)
            }) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = stringResource(R.string.settings),
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    })
}

data class BottomNavItem(
    val name: String, val route: CyberopoliRoute, val icon: ImageVector
)

@Composable
fun BottomBar(navController: NavController) {
    val bottomNavItems = listOf(
        BottomNavItem(
            name = stringResource(R.string.home),
            route = CyberopoliRoute.Home,
            icon = Icons.Filled.Home
        ), BottomNavItem(
            name = stringResource(R.string.ranking),
            route = CyberopoliRoute.Ranking,
            icon = Icons.Filled.AutoGraph
        ), BottomNavItem(
            name = stringResource(R.string.scan),
            route = CyberopoliRoute.Scan,
            icon = Icons.Filled.Image
        ), BottomNavItem(
            name = stringResource(R.string.profile),
            route = CyberopoliRoute.Profile,
            icon = Icons.Default.Person
        ), BottomNavItem(
            name = stringResource(R.string.settings),
            route = CyberopoliRoute.Settings,
            icon = Icons.Filled.Settings
        )
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route?.substringAfterLast(".")

    NavigationBar(
        modifier = Modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 0.dp,
        contentColor = MaterialTheme.colorScheme.onBackground
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            bottomNavItems.forEach { item ->
                val isSelected = currentRoute == item.route.toString()

                NavigationBarItem(icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.name,
                        tint = if (isSelected) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onBackground
                    )
                }, label = {
                    Text(
                        text = item.name,
                        color = if (isSelected) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onBackground
                    )
                }, selected = currentRoute == item.route.toString(), onClick = {
                    if (currentRoute != item.route.toString()) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                })
            }
        }
    }
}