package com.maya.assistant.data.model

enum class OrbState(
    val title: String,
    val subtitle: String,
    val primaryColor: Long,
    val secondaryColor: Long
) {
    Idle(
        title = "AI ASSISTANT",
        subtitle = "BOLO, KAISE HELP KARUN?",
        primaryColor = 0xFF38BDF8,
        secondaryColor = 0xFF818CF8
    ),
    Listening(
        title = "SUN RAHI HOON...",
        subtitle = "Bolte rahiye, main sun rahi hoon",
        primaryColor = 0xFF06B6D4,
        secondaryColor = 0xFF3B82F6
    ),
    Processing(
        title = "SOCH RAHI HOON...",
        subtitle = "Aapka sawaal process ho raha hai",
        primaryColor = 0xFF8B5CF6,
        secondaryColor = 0xFFEC4899
    ),
    Speaking(
        title = "MAYA BOL RAHI HAI",
        subtitle = "Tap karke interrupt kar sakte hain",
        primaryColor = 0xFF10B981,
        secondaryColor = 0xFF06B6D4
    ),
    Executing(
        title = "KAAM CHAL RAHA HAI",
        subtitle = "Task execute ho raha hai...",
        primaryColor = 0xFFF59E0B,
        secondaryColor = 0xFFEF4444
    ),
    WaitingForApproval(
        title = "APPROVAL CHAHIYE",
        subtitle = "Aapki permission ka intezar hai",
        primaryColor = 0xFFF97316,
        secondaryColor = 0xFFFBBF24
    ),
    Offline(
        title = "OFFLINE MODE",
        subtitle = "Device model ke saath taiyyar",
        primaryColor = 0xFF64748B,
        secondaryColor = 0xFF94A3B8
    ),
    Error(
        title = "KUCHH GADBAD HUI",
        subtitle = "Dobara koshish kijiye",
        primaryColor = 0xFFEF4444,
        secondaryColor = 0xFFDC2626
    )
}
