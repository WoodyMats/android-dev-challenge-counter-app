/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProgressIndicatorDefaults
import androidx.compose.material.Snackbar
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androiddevchallenge.ui.theme.MyTheme
import java.text.NumberFormat

class MainActivity : AppCompatActivity() {
    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                MyApp()
            }
        }
    }
}

// Start building your app here!
@ExperimentalAnimationApi
@Composable
fun MyApp() {
    Surface(color = MaterialTheme.colors.background) {
        var totalCountingTime by remember { mutableStateOf(0) }
        var timeIsRunning by remember { mutableStateOf(false) }
        Crossfade(targetState = timeIsRunning) { screen ->
            when (screen) {
                true -> TimeCounter(
                    counterTime = totalCountingTime
                ) {
                    timeIsRunning = false
                }
                false -> CountDown { totalDuration ->
                    totalCountingTime = totalDuration
                    timeIsRunning = true
                }
            }
        }
    }
}

@ExperimentalAnimationApi
@Composable
fun TimeCounter(counterTime: Int, onTimerStop: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(R.color.black)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var countingDown by remember { mutableStateOf(false) }
        var timeIsOver by remember { mutableStateOf(false) }
        val countDownTime by animateIntAsState(
            targetValue = if (countingDown) 1 else counterTime + 1,
            animationSpec = tween(durationMillis = counterTime * 1000, easing = LinearEasing),
            finishedListener = {
                timeIsOver = true
            }
        )
        LaunchedEffect(0) {
            countingDown = true
        }
        val infiniteTransition = rememberInfiniteTransition()
        val color by infiniteTransition.animateColor(
            initialValue = Color.White,
            targetValue = Red,
            animationSpec = infiniteRepeatable(
                animation = tween(500, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
        Text(
            text = if (timeIsOver) "Time is over!" else showTime(countDownTime),
            fontSize = 52.sp,
            textAlign = TextAlign.Center,
            color = if (countDownTime > 5 || timeIsOver) Color.White else color
        )
        val progress = (countDownTime.toFloat() / counterTime.toFloat())
        val animatedProgress = animateFloatAsState(
            targetValue = progress,
            animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
        ).value
        Log.d("Hello", "TimeCounter: $progress")
        AnimatedVisibility(visible = !timeIsOver) {
            LinearProgressIndicator(
                progress = animatedProgress,
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(start = 48.dp, end = 48.dp),
                color = if (countDownTime > 5 || timeIsOver) Color.Green else color
            )
        }
        Spacer(modifier = Modifier.padding(top = 6.dp))
        Row(horizontalArrangement = Arrangement.Center) {
            Button(
                onClick = { onTimerStop() },
                modifier = Modifier.padding(4.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Cyan,
                    contentColor = Color.Cyan,
                ),
                shape = CircleShape
            ) {
                Image(
                    painterResource(id = R.drawable.ic_restart_white),
                    contentDescription = null
                )
            }
        }
    }
}

fun showTime(countDownTime: Int): String {
    val hours = (countDownTime / 3600)
    val minutes = (countDownTime / 60) % 60
    val seconds = countDownTime % 60
    val nf: NumberFormat = NumberFormat.getInstance()
    nf.minimumIntegerDigits = 2
    return "${if (hours > 0) "${nf.format(hours)}:" else ""}${
    if (hours > 0 || minutes > 0) "${
    nf.format(
        minutes
    )
    }:" else ""
    }${nf.format(seconds)}"
}

@ExperimentalAnimationApi
@Composable
fun CountDown(onTimerStartCounting: (Int) -> (Unit)) {
    var invalidTime by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(R.color.black)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val (hoursRemaining, setHoursToRun) = remember { mutableStateOf(0) }
        val (minutesRemaining, setMinutesToRun) = remember { mutableStateOf(0) }
        val (secondsRemaining, setSecondsToRun) = remember { mutableStateOf(0) }
        Row {
            TimeColumn(
                totalDuration = hoursRemaining,
                setTotalDuration = setHoursToRun,
                timeUnit = "h",
                range = IntRange(0, 23)
            )
            TimeColumn(
                totalDuration = minutesRemaining,
                setTotalDuration = setMinutesToRun,
                timeUnit = "m",
                range = IntRange(0, 59)
            )
            TimeColumn(
                totalDuration = secondsRemaining,
                setTotalDuration = setSecondsToRun,
                timeUnit = "s",
                range = IntRange(0, 59)
            )
        }
        Button(
            onClick = {
                if (hoursRemaining > 0 || minutesRemaining > 0 || secondsRemaining > 0) {
                    onTimerStartCounting(hoursRemaining * 3600 + minutesRemaining * 60 + secondsRemaining)
                } else {
                    invalidTime = true
                }
            },
            modifier = Modifier.padding(4.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Cyan,
                contentColor = Color.Cyan,
            ),
            shape = CircleShape
        ) {
            Image(
                painterResource(id = R.drawable.ic_timer_white),
                contentDescription = null,
                modifier = Modifier.padding(8.dp)
            )
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
    ) {
        CustomSnackBar(invalidTime = invalidTime) {
            invalidTime = it
        }
    }
}

@ExperimentalAnimationApi
@Composable
fun CustomSnackBar(invalidTime: Boolean, onDismissPressed: (Boolean) -> (Unit)) {
    AnimatedVisibility(visible = invalidTime) {
        Snackbar(
            content = {
                Text(
                    text = "Select a valid amount of time!",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            },
            action = {
                TextButton(
                    onClick = {
                        onDismissPressed(!invalidTime)
                    }
                ) {
                    Text(
                        text = "Dismiss",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Magenta
                    )
                }
            }
        )
    }
}

@Composable
fun TimeColumn(
    totalDuration: Int,
    setTotalDuration: (Int) -> Unit,
    timeUnit: String,
    range: IntRange
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painterResource(id = R.drawable.ic_arrow_up_white),
            contentDescription = null,
            modifier = Modifier
                .clickable { setTotalDuration(totalDuration.incrementWithinRange(range)) }
                .width(120.dp)
        )
        Text(
            fontSize = 52.sp,
            text = "$totalDuration$timeUnit",
            textAlign = TextAlign.Center,
            color = Color.White,
            modifier = Modifier.width(120.dp)
        )
        Image(
            painterResource(id = R.drawable.ic_arrow_down_white),
            contentDescription = null,
            modifier = Modifier
                .clickable { setTotalDuration(totalDuration.decrementWithinRange(range)) }
                .width(120.dp)
        )
    }
}

fun Int.incrementWithinRange(range: IntRange) = if (this == range.last) {
    range.first
} else {
    this + 1
}

fun Int.decrementWithinRange(range: IntRange) = if (this == range.first) {
    range.last
} else {
    this - 1
}

@ExperimentalAnimationApi
@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun LightPreview() {
    MyTheme {
        MyApp()
    }
}

@ExperimentalAnimationApi
@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun DarkPreview() {
    MyTheme(darkTheme = true) {
        MyApp()
    }
}
