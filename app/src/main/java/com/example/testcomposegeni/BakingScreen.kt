package com.example.testcomposegeni

import android.graphics.BitmapFactory
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay

val images = arrayOf(
    R.drawable.baked_goods_1,
    R.drawable.baked_goods_2,
    R.drawable.baked_goods_3,
)
val imageDescriptions = arrayOf(
    R.string.image1_description,
    R.string.image2_description,
    R.string.image3_description,
)

@Composable
fun BakingScreen(
    bakingViewModel: BakingViewModel = viewModel(),
    selectedImageIndex: MutableIntState = mutableIntStateOf(0)
) {
    val placeholderPrompt = stringResource(R.string.prompt_placeholder)
    val request = stringResource(R.string.prompt_request_placeholder)
    var prompt by rememberSaveable { mutableStateOf(placeholderPrompt) }
    val placeholderResult = stringResource(R.string.results_placeholder)
    var result by rememberSaveable { mutableStateOf(placeholderResult) }
    val uiState by bakingViewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    
    // Animation for button
    val infiniteTransition = rememberInfiniteTransition(label = "buttonPulse")
    val buttonScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "buttonPulseAnim"
    )

    Column(
        modifier = Modifier
            .fillMaxHeight()
    ) {
        ConstraintLayout(
            modifier = Modifier
        ) {
            val (loading, title, card, receiptRequestButton, results) = createRefs()

            Text(
                modifier = Modifier
                    .constrainAs(title) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .padding(top = 16.dp),
                text = stringResource(R.string.baking_title),
                style = MaterialTheme.typography.titleLarge,
            )

            LazyRow(
                modifier = Modifier
                    .constrainAs(card) {
                        top.linkTo(title.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .padding(top = 50.dp)
                    .fillMaxWidth()
            ) {
                itemsIndexed(images) { index, image ->
                    // Add staggered entrance animation
                    var visible by remember { mutableStateOf(false) }
                    LaunchedEffect(Unit) {
                        delay(index * 100L)
                        visible = true
                    }
                    
                    // Use AnimateCard with initialVisibility parameter
                    AnimateCard(
                        selectedImageIndex = selectedImageIndex,
                        index = index,
                        imageResource = image,
                        initialVisibility = visible
                    )
                }
            }

            Row(
                modifier = Modifier
                    .constrainAs(receiptRequestButton) {
                        top.linkTo(card.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .padding(all = 16.dp)
            ) {
                TextField(
                    value = prompt,
                    label = { Text(stringResource(R.string.label_prompt)) },
                    onValueChange = { prompt = it },
                    modifier = Modifier
                        .weight(0.8f)
                        .padding(end = 16.dp)
                        .align(Alignment.CenterVertically)
                )

                Button(
                    onClick = {
                        val bitmap = BitmapFactory.decodeResource(
                            context.resources,
                            images[selectedImageIndex.intValue]
                        )
                        bakingViewModel.sendPrompt(bitmap, request)
                    },
                    enabled = prompt.isNotEmpty(),
                    modifier = Modifier
                        .scale(if (prompt.isNotEmpty()) buttonScale else 1f)
                        .align(Alignment.CenterVertically)
                ) {
                    Text(text = stringResource(R.string.action_go))
                }
            }
            var textColor = MaterialTheme.colorScheme.onSurface
            if (uiState is UiState.Error) {
                textColor = MaterialTheme.colorScheme.error
                result = (uiState as UiState.Error).errorMessage
            } else if (uiState is UiState.Success) {
                textColor = MaterialTheme.colorScheme.onSurface
                result = (uiState as UiState.Success).outputText
            }

            // Replace Box+AnimatedVisibility with Text directly
            Text(
                modifier = Modifier
                    .constrainAs(results) {
                        top.linkTo(receiptRequestButton.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .padding(16.dp)
                    .verticalScroll(scrollState)
                    .graphicsLayer {
                        alpha = if (result.isNotEmpty()) 1f else 0f
                        translationX = if (result.isNotEmpty()) 0f else 100f
                    },
                text = result,
                textAlign = TextAlign.Start,
                color = textColor,
            )

            if (uiState is UiState.Loading) {
                Column(
                    modifier = Modifier
                        .constrainAs(loading) {
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Enhanced loading animation
                    val rotationAnim = rememberInfiniteTransition(label = "loadingRotation")
                    val rotationAngle by rotationAnim.animateFloat(
                        initialValue = 0f,
                        targetValue = 360f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(2000),
                            repeatMode = RepeatMode.Restart
                        ),
                        label = "loadingRotationAnim"
                    )
                    
                    val scaleAnim = rememberInfiniteTransition(label = "loadingScale")
                    val loaderScale by scaleAnim.animateFloat(
                        initialValue = 0.8f,
                        targetValue = 1.2f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1000),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "loadingScaleAnim"
                    )
                    
                    CircularProgressIndicator(
                        modifier = Modifier
                            .scale(loaderScale)
                            .graphicsLayer { 
                                rotationZ = rotationAngle
                            },
                        color = MaterialTheme.colorScheme.primaryContainer
                    )
                    
                    // Replace AnimatedVisibility with Text with animation properties
                    Text(
                        text = "Getting response from Gemini...",
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .graphicsLayer {
                                alpha = 1f  // Always visible when loading
                            },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun AnimateCard(
    selectedImageIndex: MutableIntState,
    index: Int,
    imageResource: Int,
    initialVisibility: Boolean = true
) {
    val isSelected by remember(selectedImageIndex.intValue, index) {
        derivedStateOf { index == selectedImageIndex.intValue }
    }

    var rotated by remember { mutableStateOf(false) }
    
    // Add slide-in animation using graphicsLayer based on initialVisibility
    var animatedTranslation by remember { mutableStateOf(if (initialVisibility) 0f else 200f) }
    var animatedAlpha by remember { mutableStateOf(if (initialVisibility) 1f else 0f) }
    
    // Animate the entrance when initialVisibility becomes true
    LaunchedEffect(initialVisibility) {
        if (initialVisibility) {
            delay(index * 100L) // Staggered delay
            animatedTranslation = 0f
            animatedAlpha = 1f
        }
    }

    // Simplify rotation animation to use tween for more predictable behavior
    val rotationAnimation by animateFloatAsState(
        targetValue = if (rotated) 1f else 0f,
        animationSpec = tween(500), 
        label = "cardRotation"
    )
    
    // Add scale animation for selected card
    val scaleAnimation by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1.0f,
        animationSpec = tween(300),
        label = "cardScale"
    )
    
    // Add elevation animation
    val elevationAnimation by animateFloatAsState(
        targetValue = if (isSelected) 8f else 2f,
        animationSpec = tween(300),
        label = "cardElevation"
    )
    
    // Add entrance animations
    val slideAnimation by animateFloatAsState(
        targetValue = animatedTranslation,
        animationSpec = tween(500),
        label = "slideAnimation"
    )
    
    val fadeAnimation by animateFloatAsState(
        targetValue = animatedAlpha,
        animationSpec = tween(500),
        label = "fadeAnimation"
    )

    Card(
        modifier = Modifier
            .width(200.dp)
            .height(200.dp)
            .padding(16.dp)
            .scale(scaleAnimation)
            .graphicsLayer {
                alpha = fadeAnimation * (if (rotated) rotationAnimation else 1 - rotationAnimation)
                rotationY = rotationAnimation * 180f
                cameraDistance = 8 * density
                shadowElevation = elevationAnimation
                translationX = slideAnimation
            },
        shape = RoundedCornerShape(50.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = elevationAnimation.dp,
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ),
    ) {
        // Use simple conditional based on rotation threshold
        if (rotationAnimation < 0.5f) {
            Image(
                modifier = Modifier
                    .padding(start = 8.dp, end = 8.dp)
                    .requiredSize(200.dp)
                    .clickable {
                        selectedImageIndex.intValue = index
                        rotated = !rotated
                    },
                painter = painterResource(imageResource),
                contentDescription = stringResource(imageDescriptions[index]),
            )
        } else {
            Text(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .graphicsLayer {
                        // Fix the rotation for the back side
                        rotationY = 180f
                    }
                    .clickable {
                        rotated = !rotated
                    }
                    .fillMaxHeight()
                    .fillMaxSize(),
                text = stringResource(imageDescriptions[index]),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun BakingScreenPreview() {
    BakingScreen()
}