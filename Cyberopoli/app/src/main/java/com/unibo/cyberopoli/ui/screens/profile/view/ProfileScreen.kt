package com.unibo.cyberopoli.ui.screens.profile.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.unibo.cyberopoli.ui.components.BottomBar
import com.unibo.cyberopoli.ui.components.TopBar
import com.unibo.cyberopoli.ui.screens.loading.view.LoadingScreen
import com.unibo.cyberopoli.ui.screens.profile.viewmodel.ProfileParams
import com.unibo.cyberopoli.ui.screens.profile.view.composables.EditProfileSection
import com.unibo.cyberopoli.ui.screens.profile.view.composables.GameStatisticsSection
import com.unibo.cyberopoli.ui.screens.profile.view.composables.ProfileHeader

@Composable
fun ProfileScreen(
    navController: NavHostController,
    profileParams: ProfileParams,
) {
    val user = profileParams.user.value

    Scaffold(
        topBar = { TopBar(navController) },
        bottomBar = { BottomBar(navController) },
        content = { paddingValues ->
            Column(
                modifier =
                    Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(0.dp),
            ) {
                if (user == null) {
                    LoadingScreen()
                } else {
                    ProfileHeader(
                        user = user,
                        onEditProfileClick = { profileParams.changeAvatar() },
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        GameStatisticsSection(user = user)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    EditProfileSection(
                        user = user,
                        updateUserInfo = profileParams.updateUserInfo,
                        updatePasswordWithOldPassword = profileParams.updatePasswordWithOldPassword,
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        },
    )
}
