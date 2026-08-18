# Three Dimension Compose 🚀

A high-performance Jetpack Compose library for creating realistic **3D extrusion effects**, dynamic **colored glows**, and soft **physical shadows**.

Bring your UI to life with a "popped out" look that feels tangible and responds smoothly to interactions.

---

## ✨ Features

- **Realistic 3D Extrusion**: Create solid-looking 3D objects from any Compose `Shape`.
- **Performance Optimized**: Uses `drawWithCache` and deferred state reads (lambdas) to ensure 60fps scrolling and animations.
- **Dynamic Colored Glow (Bloom)**: Adds an ambient glow around objects that can match your gradient or solid color.
- **Soft Physical Shadows**: Casts realistic shadows at the base of your 3D objects, with blur that scales with elevation.
- **Animated Gradients**: Built-in support for shifting RGB and custom gradient "walls".
- **Interactive**: Easily animate elevation changes for satisfying click/press effects.

---

<img width="514" height="1141" alt="Screenshot_20260819_012649" src="https://github.com/user-attachments/assets/6dec2bf0-72f9-4c76-9c47-c01d47a61bf1" />


## 📦 Installation

Add the dependency to your `build.gradle.kts` file:

```kotlin
dependencies {
    implementation("io.github.leochrish:3D:1.0.0")
}
```

---

## 🚀 Usage

### Basic 3D Effect

Apply the `to3D` modifier to any composable.

```kotlin
Box(
    modifier = Modifier
        .size(100.dp)
        .to3D(
            elevation = 12.dp,
            paint = Brush.verticalGradient(listOf(Color.Blue, Color.Cyan)),
            shape = RoundedCornerShape(16.dp)
        )
        .background(Color.White, RoundedCornerShape(16.dp))
)
```

### Performance-Optimized Animation (Click Effect)

For animations, use the lambda-based overload to prevent unnecessary recompositions.

```kotlin
val interactionSource = remember { MutableInteractionSource() }
val isPressed by interactionSource.collectIsPressedAsState()

// Animate elevation
val elevation by animateDpAsState(if (isPressed) 0.dp else 12.dp)

// Create an animated gradient
val auroraBrush = rememberAnimatedGradientBrush(
    colors = listOf(Color.Green, Color.Blue, Color.Green)
)

Box(
    modifier = Modifier
        .size(200.dp, 60.dp)
        .to3D(
            elevation = { elevation }, // Lambda provider for performance
            paint = auroraBrush,        // Lambda provider for performance
            shape = RoundedCornerShape(12.dp),
            degree = 45f
        )
        .background(Color.DarkGray, RoundedCornerShape(12.dp))
        .clickable(interactionSource = interactionSource, indication = null) { 
            /* Handle click */ 
        }
)
```

---

## 🛠️ Configuration

| Parameter | Type | Description |
| :--- | :--- | :--- |
| `elevation` | `Dp` or `() -> Dp` | The depth of the 3D extrusion. |
| `paint` | `Brush` or `() -> Brush` | The fill used for the 3D walls (can be solid, gradient, etc). |
| `shape` | `Shape` | The geometry of the object (Rectangle, Circle, etc). |
| `degree` | `Float` | The angle of the extrusion (0° = Up, 90° = Right). Default is 0°. |
| `shadowColor` | `Color` | The color of the main cast shadow. |
| `glowAlpha` | `Float` | The intensity of the ambient colored bloom (0.0 to 1.0). |

---

## 📄 License

```
Copyright 2026 Leoni Christopher

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
