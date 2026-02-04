# MPlayer – Embedded Music Player

Android music player prototype built with **Kotlin + Jetpack Compose**.

## Video Demo

[Watch demo](https://drive.google.com/file/d/1Ng20yeW_YsLYGktVncqwuNbRHe0Cikzn/view?usp=sharing)

---

## Features

### Core Requirements (Spec)
- **Track context:** Title, artist, album art with slide animation on track change
- **Playback control:** Play, pause, previous, next, circular track list
- **Timeline navigation:** Progress bar with seek-on-release, live time preview while dragging
- **User feedback:** Like/favorite with visual state and persistence (SharedPreferences)

### Extras & Polish
- **Repeat mode:** Off → All → One (with floating label)
- **Album art fallback:** MusicNote icon when art is missing
- **Directional animations:** Album slides from right (next) or left (previous), including circular wrap
- **Preview time:** Displays target position while seeking

---

## Design Considerations

### Feel & Motion
- Lottie animations for play/pause and like
- Album art slide on track change (direction matches user action)
- Repeat mode label floats below button without shifting layout

### Accessibility
- Content descriptions for all controls (TalkBack)
- Minimum 48dp touch targets
- Seek bar semantics with current/total time
- Ripple feedback on interactive elements

### Edge Cases
- Long text: `basicMarquee` on title/artist
- Missing album art: MusicNote icon placeholder
- Empty track list: Clear messaging

---

## How to Run

1. Clone the repo
2. Open in Android Studio
3. Run on device or emulator

---

## Stack

- Kotlin, Jetpack Compose, Material 3
- MediaPlayer (Android)
- Lottie Compose
- MVVM + StateFlow
