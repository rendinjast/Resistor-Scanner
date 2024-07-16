/*
 * *
 *  * Created by Erfan Khadivar (hi@erfan.ee) on 7/16/24, 6:50 PM
 *  * Copyright (c) 2024 . All rights reserved.
 *  * Last modified 7/15/24, 8:52 PM
 *
 */

package ee.erfan.resistor.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import ee.erfan.resistor.ui.theme.AccentColor
import ee.erfan.resistor.ui.theme.sheetSurfaceColor
import ee.erfan.resistor.ui.theme.sheetSurfaceDarkColor


@Composable
fun ChoiceConfirmDialog(
    showDialog: Boolean,
    message: @Composable (() -> Unit),
    okMessage: String,
    cancelMessage: String,
    onOk: () -> Unit,
    onCancel: () -> Unit,
    onDismissRequest: () -> Unit
) {
    if (showDialog) {
        Dialog(
            onDismissRequest = { onDismissRequest() },
            DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        when (isSystemInDarkTheme()) {
                            true -> sheetSurfaceDarkColor
                            false -> sheetSurfaceColor
                        }
                    )
            ) {

                Box(
                    modifier = Modifier.padding(16.dp)
                ) {
                    message()
                }


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = { onCancel() },
                        modifier = Modifier
                            .weight(1f)
                            .padding(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = (when (isSystemInDarkTheme()) {
                                true -> sheetSurfaceDarkColor
                                false -> sheetSurfaceColor
                            }), contentColor = (when (isSystemInDarkTheme()) {
                                true -> AccentColor
                                false -> MaterialTheme.colorScheme.onBackground
                            })
                        )
                    ) {
                        Text(
                            text = cancelMessage
                        )
                    }
                    Button(
                        onClick = { onOk() }, modifier = Modifier
                            .weight(1f)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = okMessage
                        )
                    }
                }
            }
        }
    }
}