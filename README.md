# jetNews

# Animated Cards in Jetpack Compose

This project demonstrates how to create animated cards using Jetpack Compose. It features a simple UI with cards that flip to reveal additional information when clicked.

## Gemini Integration

**API Key Setup:**

To use Gemini's features, you need to obtain an API key and configure it in your local environment. Follow these steps:

1. **Obtain an API key:** Visit the Google Cloud Console and create an API key for the Gemini API.
2. **Go to  `local.properties` file:** In the root directory of your project.
3. **Add your API key:** Add the following line to the `local.properties` file, replacing `apiKey` with your actual API key:

## Features

- **Card Flip Animation:** Cards smoothly rotate 180 degrees when clicked, revealing content on the back.
- **State Management:** Uses `MutableState` and `remember` to manage the animation state and selected card.
- **Animation API:** Leverages Jetpack Compose's animation APIs like `animateFloatAsState` and `tween` for smooth transitions.
- **Material Design:** Implements Material Design components like `Card`, `Image`, and `Text`.


## Getting Started

1. **Clone the repository:**

   bash git clone https://github.com/GeoSId/jetNews

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
- **`Image` and `Text`:** These composable are used to display the content on the front and back of the cards.

## Contributing

Contributions are welcome! If you find any issues or have suggestions for improvements, please feel free to open an issue or submit a pull request.
## Video

Check the video [test_compose_gen_animate](https://github.com/GeoSId/jetNews/blob/master/test_compose_gen_animate.mp4)

[compose_test.webm](https://github.com/user-attachments/assets/a7e0e0d1-0802-4fd0-9dea-04acea12b834)


## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
