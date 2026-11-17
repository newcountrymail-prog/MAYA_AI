# MAYA AI — Female AI Assistant for Android

MAYA AI is an independent, native Android application built with Jetpack Compose, Material 3, and Clean Architecture (MVVM).

## Architecture & Tech Stack
- **Language**: Kotlin 1.9.23
- **UI Toolkit**: Jetpack Compose & Material 3
- **Design Style**: Soft-blue neumorphic interface with glowing animated Orb centerpiece
- **Architecture**: MVVM with Clean Architecture
- **Database**: Room (with KSP)
- **Settings & Preferences**: Jetpack DataStore Preferences
- **Voice Engine**:
  - Speech Recognition with Hindi, English, and Hinglish streaming models
  - Android neural Text-to-Speech (TTS) with warm natural female voice modulation
  - Real-time speech interruption and audio activity detection
  - 8-State Animated Orb (`Idle`, `Listening`, `Processing`, `Speaking`, `Executing`, `WaitingForApproval`, `Offline`, `Error`)

## Included Screens
1. **Home**: Personalized greeting, 1 Energy pill, animated MAYA Orb centerpiece with real-time waveform, quick action pills (`Gaane`, `Padhai`, `Journal`), info cards (`Mausam`, `Aaj`, `Mood`), chat input bar, and floating dock.
2. **Chat**: Full conversation interface with user and Maya message bubbles, text-to-speech audio replay, clipboard copy, and file attachment shortcuts.
3. **Scanner (MAYA Vision)**: Dark HUD interface with circular radar reticle, standby prompt, camera flip, shutter button with cyan bloom, and video recording.
4. **Yaadein (Memories)**: Sikhe hue tasks (`gemini se poochho`, `chatgpt se poochho`, `gemini video`, `gemini image`, `chatgpt image`) with execution runner and cloud backup.
5. **Settings**: Account (`Personal`), Assistant (`Maya` persona, `Voice engine`, `Secret settings` / Girlfriend mode, `Skills`, `Sub-agents`), and Kaam aur Messages.
6. **Advanced Settings**: Look aur Feel (`Theme`, `Appearance`, `Behaviour`, `Typing`) and Safety aur Access (`Voice Guardian`, `Chehre`, `Emergency SOS`, `Touch Guard`, `Screen lock`).
7. **MAYA Voice**: Dedicated full-screen voice assistant mode with responsive pulsating Orb, live speech transcription, and instant interruption controls.

## Automated APK Build
This repository includes `.github/workflows/android-apk-build.yml` which automatically compiles the Debug APK and uploads it on every push to `main`.
