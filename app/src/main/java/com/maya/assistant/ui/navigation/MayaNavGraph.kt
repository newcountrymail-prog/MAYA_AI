package com.maya.assistant.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.maya.assistant.ui.components.BottomNavBar
import com.maya.assistant.ui.components.MayaDrawerContent
import com.maya.assistant.ui.screens.agents.MultiAgentScreen
import com.maya.assistant.ui.screens.chat.ChatScreen
import com.maya.assistant.ui.screens.coding.CodingStudioScreen
import com.maya.assistant.ui.screens.documents.DocumentsScreen
import com.maya.assistant.ui.screens.home.HomeScreen
import com.maya.assistant.ui.screens.image.ImageStudioScreen
import com.maya.assistant.ui.screens.memories.MemoriesScreen
import com.maya.assistant.ui.screens.providers.ProvidersScreen
import com.maya.assistant.ui.screens.research.ResearchScreen
import com.maya.assistant.ui.screens.scanner.ScannerScreen
import com.maya.assistant.ui.screens.settings.AdvancedSettingsScreen
import com.maya.assistant.ui.screens.settings.SettingsScreen
import com.maya.assistant.ui.screens.voice.VoiceScreen
import kotlinx.coroutines.launch

@Composable
fun MayaNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Home.route

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                MayaDrawerContent(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        scope.launch { drawerState.close() }
                        navController.navigate(route) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) {
        val showBottomBar = currentRoute in listOf(
            Screen.Home.route,
            Screen.Scanner.route,
            Screen.Memories.route,
            Screen.Chat.route
        )

        Scaffold(
            bottomBar = {
                if (showBottomBar) {
                    BottomNavBar(
                        currentRoute = currentRoute,
                        onNavigate = { route ->
                            navController.navigate(route) {
                                popUpTo(Screen.Home.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onMicClick = {
                            navController.navigate(Screen.Voice.route)
                        }
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                NavHost(
                    navController = navController,
                    startDestination = Screen.Home.route
                ) {
                    composable(Screen.Home.route) {
                        HomeScreen(
                            onOpenDrawer = { scope.launch { drawerState.open() } },
                            onNavigateToChat = { navController.navigate(Screen.Chat.route) },
                            onNavigateToVoice = { navController.navigate(Screen.Voice.route) }
                        )
                    }

                    composable(Screen.Scanner.route) {
                        ScannerScreen(
                            onOpenDrawer = { scope.launch { drawerState.open() } }
                        )
                    }

                    composable(Screen.Voice.route) {
                        VoiceScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.Memories.route) {
                        MemoriesScreen(
                            onOpenDrawer = { scope.launch { drawerState.open() } }
                        )
                    }

                    composable(Screen.Chat.route) {
                        ChatScreen(
                            onOpenDrawer = { scope.launch { drawerState.open() } }
                        )
                    }

                    composable(Screen.Settings.route) {
                        SettingsScreen(
                            onBack = { navController.popBackStack() },
                            onNavigateToAdvanced = { navController.navigate(Screen.AdvancedSettings.route) },
                            onNavigateToProviders = { navController.navigate(Screen.AiProviders.route) },
                            onNavigateToSubAgents = { navController.navigate(Screen.MultiAgent.route) }
                        )
                    }

                    composable(Screen.AdvancedSettings.route) {
                        AdvancedSettingsScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }

                    // Part 2 Destinations
                    composable(Screen.AiProviders.route) {
                        ProvidersScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.ImageStudio.route) {
                        ImageStudioScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.CodingStudio.route) {
                        CodingStudioScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.Research.route) {
                        ResearchScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.Documents.route) {
                        DocumentsScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.MultiAgent.route) {
                        MultiAgentScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
