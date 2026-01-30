package com.overklassniy.stankinschedule.settings.ui.components.color

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.godaddy.android.colorpicker.ClassicColorPicker
import com.godaddy.android.colorpicker.HsvColor
import com.overklassniy.stankinschedule.core.ui.ext.parse
import com.overklassniy.stankinschedule.core.ui.ext.toHEX
import com.overklassniy.stankinschedule.settings.ui.R
import com.overklassniy.stankinschedule.core.ui.R as R_core

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorPickerDialog(
    title: String,
    color: Color,
    onColorChanged: (color: Color) -> Unit,
    onDefault: () -> Unit,
    onDismiss: () -> Unit,
) {
    var currentColor by remember { mutableStateOf(color) }

    var currentHex by remember { mutableStateOf(color.toHEX()) }
    var isHexError by remember { mutableStateOf(false) }


    Dialog(
        onDismissRequest = onDismiss,
        content = {
            Card(
                shape = MaterialTheme.shapes.extraLarge
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(24.dp)
                ) {

                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    ColorPicker(
                        color = currentColor,
                        onColorChanged = {
                            currentColor = it
                            currentHex = it.toHEX()
                        },
                        modifier = Modifier
                            .height(200.dp)
                            .fillMaxWidth()
                    )

                    TextField(
                        value = currentHex,
                        isError = isHexError,
                        singleLine = true,
                        leadingIcon = {
                            ColorIcon(color = currentColor)
                        },
                        onValueChange = { newHex ->
                            currentHex = newHex
                            if (newHex.isNotEmpty()) {
                                try {
                                    currentColor = Color.parse(newHex)
                                    isHexError = false
                                } catch (_: IllegalArgumentException) {
                                    isHexError = true
                                }
                            } else {
                                isHexError = true
                            }
                        }
                    )

                    Row {
                        TextButton(onClick = onDefault) {
                            Text(text = stringResource(R.string.default_color))
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        TextButton(onClick = onDismiss) {
                            Text(text = stringResource(R_core.string.cancel))
                        }
                        TextButton(onClick = { onColorChanged(currentColor) }) {
                            Text(text = stringResource(R_core.string.ok))
                        }
                    }
                }
            }
        }
    )
}


@Composable
private fun ColorPicker(
    color: Color,
    onColorChanged: (color: Color) -> Unit,
    modifier: Modifier = Modifier
) {
    ClassicColorPicker(
        modifier = modifier,
        color = HsvColor.from(color = color),
        showAlphaBar = false,
        onColorChanged = { onColorChanged(it.toColor()) })
}