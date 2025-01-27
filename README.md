# jetNews

# Animated Cards in Jetpack Compose

This project demonstrates how to create animated cards using Jetpack Compose. It features a simple UI with cards that flip to reveal additional information when clicked.

## Features

- **Card Flip Animation:** Cards smoothly rotate 180 degrees when clicked, revealing content on the back.
- **State Management:** Uses `MutableState` and `remember` to manage the animation state and selected card.
- **Animation API:** Leverages Jetpack Compose's animation APIs like `animateFloatAsState` and `tween` for smooth transitions.
- **Material Design:** Implements Material Design components like `Card`, `Image`, and `Text`.

## Getting Started

1. **Clone the repository:**
2.  **Open the project in Android Studio:**
      Import the project into Android Studio.
3. **Build and run the app:**
   Build the project and run it on an emulator or physical device.

## Usage

- Tap on a card to flip it and reveal the content on the back.
- Tap again to flip it back to the front.

## Code Highlights

- **`AnimateCard` Composable:** This composable function handles the card animation and state management. It uses `animateFloatAsState` to animate the rotation and alpha of the card.
- **`Card`:** The Material Design `Card` component is used to create the cards.
- **`Image` and `Text`:** These composables are used to display the content on the front and back of the cards.

## Contributing

Contributions are welcome! If you find any issues or have suggestions for improvements, please feel free to open an issue or submit a pull request.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.