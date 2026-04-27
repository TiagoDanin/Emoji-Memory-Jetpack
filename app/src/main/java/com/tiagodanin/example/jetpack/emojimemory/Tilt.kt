package com.tiagodanin.example.jetpack.emojimemory

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext

@Composable
fun rememberTilt(): State<Offset> {
    val context = LocalContext.current
    val tilt = remember { mutableStateOf(Offset.Zero) }

    DisposableEffect(Unit) {
        val sensorManager =
            context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        val sensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                    val x = (event.values[0] / 9.81f).coerceIn(-1f, 1f)
                    val y = (event.values[1] / 9.81f).coerceIn(-1f, 1f)
                    val prev = tilt.value
                    tilt.value = Offset(
                        x = prev.x + (-x - prev.x) * 0.15f,
                        y = prev.y + (y - prev.y) * 0.15f,
                    )
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        if (sensor != null) {
            sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_GAME)
        }
        onDispose { sensorManager?.unregisterListener(listener) }
    }

    return tilt
}
