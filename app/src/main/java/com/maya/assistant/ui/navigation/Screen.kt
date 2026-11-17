package com.maya.assistant.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Scanner : Screen("scanner")
    object Voice : Screen("voice")
    object Memories : Screen("memories")
    object Chat : Screen("chat")
    object Settings : Screen("settings")
    object AdvancedSettings : Screen("advanced_settings")
}
