package com.maya.assistant.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Scanner : Screen("scanner")
    object Voice : Screen("voice")
    object Memories : Screen("memories")
    object Chat : Screen("chat")
    object Settings : Screen("settings")
    object AdvancedSettings : Screen("advanced_settings")

    // Part 2 Screens
    object AiProviders : Screen("ai_providers")
    object ImageStudio : Screen("image_studio")
    object CodingStudio : Screen("coding_studio")
    object Research : Screen("research")
    object Documents : Screen("documents")
    object MultiAgent : Screen("multi_agent")

    // Part 3 Screens
    object Automation : Screen("automation")
    object Skills : Screen("skills")
    object CloudJobs : Screen("cloud_jobs")
}
