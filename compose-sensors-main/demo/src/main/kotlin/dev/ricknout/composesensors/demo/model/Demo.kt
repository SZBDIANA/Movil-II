package dev.ricknout.composesensors.demo.model

import android.view.View
import dev.ricknout.composesensors.demo.R

enum class Demo(
    val title: String,
    val iconResourceId: Int,
) {
    NONE("", View.NO_ID),
    ACCELEROMETER("", R.drawable.ic_accelerometer_24dp),
}
