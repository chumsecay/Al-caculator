# AI Calculator

Android calculator with a glassmorphism UI and a theatrical **AI Thinking** overlay.

Math is real. The “AI” sequence is pure UX.

## Features

**Calculator**
- Digits, decimal, `+ − × ÷ =`
- Immediate execution (phone-style chaining)
- Smart **AC / C** (iOS-style)
- `%`, `±`
- Divide-by-zero → `Error`
- `BigDecimal` precision

**Layout**
```
AC/C  ±  %  ÷
7     8  9  ×
4     5  6  −
1     2  3  +
0        .  =     (0 spans 2 columns)
```

**AI Thinking Mode**
1. User presses `=`
2. Engine computes immediately (result or `Error`)
3. Fake terminal logs play on the **display panel only** (~3s)
4. Result is revealed

Logs appear full-line, fixed order. Edit:
`app/src/main/java/com/aicalculator/calc/ai/LogRepository.kt`

## Stack

| | |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose, Material 3 |
| Min / target SDK | 26 / 35 |
| Package | `com.aicalculator.calc` |
| AGP / Gradle | 8.7.x / 8.9 |

## Requirements

- Android Studio (Ladybug+)
- **JDK 17 or 21** (not 25)
- Android SDK Platform 35

## Run

**Android Studio:** Open this folder → Sync → Run `app`.

```bash
./gradlew test
./gradlew installDebug
```

Windows: `gradlew.bat test` / `gradlew.bat installDebug`

## Structure

```
app/src/main/java/com/aicalculator/calc/
├── MainActivity.kt
├── CalculatorViewModel.kt
├── calculator/          # pure math
├── ai/                  # fake AI UX
└── ui/
```

## Customize

| What | Where |
|------|--------|
| Log text / order | `ai/LogRepository.kt` |
| Think duration | `ai/LoadingConfig.kt` |
| Colors | `ui/theme/Color.kt` |

## Notes

- Blur orbs: API 31+. Older devices still get gradient + glass.
- `local.properties` is gitignored.
- AI mode never changes the numerical result.
