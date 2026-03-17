<p align="center">
  <img src="logo.png" alt="SenseKeyboard" width="128" />
</p>

<h1 align="center">SenseKeyboard 🎮</h1>

<p align="center"><strong>PS5-style on-screen keyboard for Android TV</strong>, designed for DualSense and other game controllers.</p>

<p align="center">
  <img src="banner.png" alt="SenseKeyboard Banner" width="480" />
</p>

## Features

- **PS5 Visual Style** — Dark translucent overlay with blue glow focus, matching the PS5 aesthetic
- **Full Controller Support** — DualSense, Xbox, and generic gamepad button mapping
- **Word Suggestions** — Learns your words and predicts next-word completions
- **Speech-to-Text** — Voice input via controller button press
- **Multiple Layouts** — English (US), Danish, with more coming
- **Customizable** — Color presets (PS5, Dark, Xbox, Steam), opacity, size, position sliders
- **Wind Effect** — Translucent particle trail when navigating keys
- **Wrap Navigation** — Optional horizontal/vertical key wrapping

## Controller Mapping

| Button | Action |
|--------|--------|
| ✕ Cross | Select character |
| △ Triangle | Space |
| □ Square | Backspace |
| ○ Circle | Close keyboard |
| D-pad / Left Stick | Navigate grid |
| L1 / R1 | Cursor left / right |
| L2 (hold) | Shift |
| R2 | Enter / Submit |
| L2 + R2 | New line |
| L3 (left stick click) | Toggle symbols |
| R3 (right stick click) | Voice input |
| Options | Done |

## Installation

1. Download the APK from [Releases](../../releases)
2. Install on your Android TV device
3. Open SenseKeyboard → Enable in keyboard settings → Select as active keyboard
4. Connect a controller and start typing!

## Building

```bash
./gradlew assembleDebug
```

APK output: `app/build/outputs/apk/debug/app-debug.apk`

## Tech Stack

- Kotlin + Jetpack Compose (settings UI)
- Custom Android View with Canvas rendering (keyboard)
- Android InputMethodService (IME)
- Android SpeechRecognizer API

## License

MIT
