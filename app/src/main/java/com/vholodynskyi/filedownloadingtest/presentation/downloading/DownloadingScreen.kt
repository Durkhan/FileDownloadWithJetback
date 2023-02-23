package com.vholodynskyi.filedownloadingtest.presentation.downloading

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf

@Composable
fun DownloadingScreen(
    context: LifecycleOwner,
    viewModel: MainViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            text = state.file,
            color = MaterialTheme.colors.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .align(Alignment.Center)
        )

        if (state.error.isNotBlank()) {
            Text(
                text = state.error,
                color = MaterialTheme.colors.error,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .align(Alignment.Center)
            )
        }
        if (state.isLoading) {

            CircularProgressBar(
                context = context,
                viewModel = viewModel,
                number = 100,
                modifier = Modifier.align(Alignment.Center)
            )
            Button(
                modifier = Modifier.align(Alignment.BottomCenter),
                onClick = { viewModel.cancelDownloading() }) {
                Text(text = "Cancel downloading")
            }
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun CircularProgressBar(
    context: LifecycleOwner,
    viewModel: MainViewModel,
    number: Int,
    modifier: Modifier,
    fontSize: TextUnit = 28.sp,
    radius: Dp = 50.dp,
    strokeWidth: Dp = 8.dp,
    animDelay: Int = 1000
) {
    var percentage = mutableStateOf<Float>(0.0f)

    var animDuration = mutableStateOf<Int>(10000)

    viewModel.progress.observe(context, Observer {
        percentage.value = it.coerceIn(0f, 1f).toFloat()
        animDuration.value = (100 - percentage.value.toInt()) * 1000
    })
    var animationPlayed by remember {
        mutableStateOf(false)
    }


    val currentPercentage = animateFloatAsState(

        targetValue = if (animationPlayed) percentage.value else 0f,
        animationSpec = tween(
            durationMillis = animDuration.value,
            delayMillis = animDelay
        )
    )

    LaunchedEffect(key1 = true) {
        animationPlayed = true
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(radius * 2f)
            .padding(10.dp)
            .fillMaxSize()
    ) {
        Canvas(modifier = Modifier.size(radius * 2f)) {
            val color = lerp(
                start = Color.Red,
                stop = Color.Green,
                fraction = currentPercentage.value
            )
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = 360 * currentPercentage.value,
                useCenter = false,
                style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round)
            )
        }
        Text(
            text = (currentPercentage.value * number).toInt().toString(),
            color = Color.Yellow,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold
        )

    }

}










