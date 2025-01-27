package com.example.testcomposegeni

import android.graphics.BitmapFactory
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel

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
                    AnimateCard(selectedImageIndex, index, image)
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

            Text(
                modifier = Modifier
                    .constrainAs(results) {
                        top.linkTo(receiptRequestButton.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .padding(16.dp)
                    .verticalScroll(scrollState),
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
                    CircularProgressIndicator(modifier = Modifier)
                }
            }
        }
    }
}

    @Composable
    fun AnimateCard(
        selectedImageIndex: MutableIntState,
        index: Int,
        imageResource: Int
    ) {

        val isSelected by remember(selectedImageIndex.intValue, index) {
            derivedStateOf { index == selectedImageIndex.intValue }
        }

        var rotated by remember { mutableStateOf(false) }

        val animationProgress by animateFloatAsState(
            targetValue = if (rotated) 1f else 0f,
            animationSpec = tween(500), label = "cardRotation"
        )

        Card(
            modifier = Modifier
                .width(200.dp)
                .height(200.dp)
                .padding(16.dp)
                .graphicsLayer {
                    alpha = if (rotated) animationProgress else 1 - animationProgress
                    rotationY = animationProgress * 180f
                    cameraDistance = 8 * density
                },
            shape = RoundedCornerShape(50.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 0.dp,
            ),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
            ),
        ) {
            if (!rotated) {
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
                            rotationY = animationProgress * 180f
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