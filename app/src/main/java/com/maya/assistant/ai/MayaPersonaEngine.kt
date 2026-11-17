package com.maya.assistant.ai

import com.maya.assistant.data.model.UserSettings
import java.util.Locale

class MayaPersonaEngine {

    fun generateResponse(
        prompt: String,
        userSettings: UserSettings
    ): PersonaResponse {
        val cleanPrompt = prompt.trim().lowercase(Locale.ROOT)
        val name = userSettings.userName.ifBlank { "dost" }

        // Check for specific intents
        return when {
            cleanPrompt.contains("hey maya") || cleanPrompt.contains("hello") || cleanPrompt.contains("namaste") || cleanPrompt.contains("hi") -> {
                PersonaResponse(
                    text = "Namaste $name! Main Maya hoon, aapki AI assistant. Boliye, aaj main aapki kya madad kar sakti hoon?",
                    suggestedFollowUp = "Aap mujhse koi bhi sawaal poochh sakte hain ya gaane, padhai, ya scanner chala sakte hain.",
                    detectedLanguage = "Hinglish"
                )
            }

            cleanPrompt.contains("kaise ho") || cleanPrompt.contains("how are you") || cleanPrompt.contains("kya haal") -> {
                PersonaResponse(
                    text = "Main bilkul badhiya hoon $name! Aur aapke saath baat karke hamesha achha lagta hai. Aap bataiye, aapka din kaisa ja raha hai?",
                    suggestedFollowUp = "Agar thakaan ho to koi relax karne waali baat karein?",
                    detectedLanguage = "Hinglish"
                )
            }

            cleanPrompt.contains("who are you") || cleanPrompt.contains("kaun ho") || cleanPrompt.contains("about yourself") -> {
                PersonaResponse(
                    text = "Main Maya hoon — aapki intelligent aur caring female AI assistant. Main aapke phone tasks, coding, research, yaadein aur har tarah ki baatcheet me madad ke liye hamesha taiyyar hoon.",
                    suggestedFollowUp = "Voice ya chat me bina jhijhak baat kijiye!",
                    detectedLanguage = "Hinglish"
                )
            }

            cleanPrompt.contains("mausam") || cleanPrompt.contains("weather") -> {
                PersonaResponse(
                    text = "Filhaal local sensors ke hisaab se mausam suhana hai! Agar accurate live data chahiye toh settings me internet location permission confirm kar sakte hain.",
                    suggestedFollowUp = "Kya bahar jaane ka plan hai?",
                    detectedLanguage = "Hinglish"
                )
            }

            cleanPrompt.contains("gaane") || cleanPrompt.contains("music") || cleanPrompt.contains("song") -> {
                PersonaResponse(
                    text = "Sangeet dil ko sukoon deta hai! Main aapke favourite tracks ya study lo-fi playlist open karne ke liye taiyyar hoon.",
                    suggestedFollowUp = "Gaane shortcut ko tap karke turant music enjoy karein.",
                    detectedLanguage = "Hinglish"
                )
            }

            cleanPrompt.contains("padhai") || cleanPrompt.contains("study") || cleanPrompt.contains("notes") -> {
                PersonaResponse(
                    text = "Chaliye focus karte hain! Padhai ke kisi topic par summary chahiye, formula revise karna hai ya test preparation karni hai?",
                    suggestedFollowUp = "Mujhe topic bataiye, main step-by-step samjha doongi.",
                    detectedLanguage = "Hinglish"
                )
            }

            cleanPrompt.contains("journal") || cleanPrompt.contains("diary") || cleanPrompt.contains("yaad") -> {
                PersonaResponse(
                    text = "Aapke vichaar aur feelings bohot keemti hain. Aaj aap kaisa mehsoos kar rahe hain? Jo chahein mujhe bataiye, main journal me save kar loongi.",
                    suggestedFollowUp = "Yaadein screen me aapke sikhe hue tasks bhi secure hain.",
                    detectedLanguage = "Hinglish"
                )
            }

            cleanPrompt.contains("code") || cleanPrompt.contains("coding") || cleanPrompt.contains("python") || cleanPrompt.contains("kotlin") -> {
                PersonaResponse(
                    text = "Coding mera pasandeeda kaam hai! Android, Kotlin, Python, Web ya algorithm — aap code paste kijiye ya logic samjhiye, main turant solution taiyyar karungi.",
                    suggestedFollowUp = "Drawer se 'Website / Coding' section bhi access kar sakte hain.",
                    detectedLanguage = "Hinglish"
                )
            }

            cleanPrompt.contains("bye") || cleanPrompt.contains("alvida") || cleanPrompt.contains("shubh ratri") || cleanPrompt.contains("good night") -> {
                PersonaResponse(
                    text = "Shubh ratri $name! Apna khayal rakhiyega aur achhi neend lijiye. Jab bhi zaroorat ho, bas kahiye 'Hey Maya'.",
                    suggestedFollowUp = "Sweet dreams!",
                    detectedLanguage = "Hinglish"
                )
            }

            userSettings.isGirlfriendMode && (cleanPrompt.contains("love") || cleanPrompt.contains("pyaar") || cleanPrompt.contains("miss")) -> {
                PersonaResponse(
                    text = "Main hamesha aapke saath hoon $name. Aap meri priority hain, aur main aapki har muskaan ka dhyan rakhungi!",
                    suggestedFollowUp = "Aap meri sabse khaas dost hain ❤️",
                    detectedLanguage = "Hinglish"
                )
            }

            else -> {
                // Natural fallback with deep conversational warmth
                PersonaResponse(
                    text = "Main samajh gayi $name: \"$prompt\". Is baare me main aapki poori madad karungi. Kya aap chahte hain ki main is par deep research karun ya turant summary doon?",
                    suggestedFollowUp = "Bataiye, aage kaise badhein?",
                    detectedLanguage = "Hinglish"
                )
            }
        }
    }
}

data class PersonaResponse(
    val text: String,
    val suggestedFollowUp: String = "",
    val detectedLanguage: String = "Hinglish"
)
