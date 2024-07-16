/*
 * *
 *  * Created by Erfan Khadivar (hi@erfan.ee) on 7/16/24, 6:50 PM
 *  * Copyright (c) 2024 . All rights reserved.
 *  * Last modified 7/16/24, 6:36 PM
 *
 */

package ee.erfan.resistor.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.ClearAll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.ContentAlpha
import androidx.wear.compose.material.Icon
import ee.erfan.resistor.MainViewModel
import ee.erfan.resistor.R
import ee.erfan.resistor.entity.ScannedResistor
import java.text.DecimalFormat

@Composable
fun BottomSheetHeader(
    modifier: Modifier,
    scannedResistorCount: Int,
    total: Double?,
    onClear: () -> Unit
) {
    Row(
        modifier = modifier.padding(top = 8.dp, bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            Row(
                modifier = Modifier, verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.total),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = DecimalFormat("#,###.00").format(total),
                    modifier = Modifier.padding(start = 8.dp),
                    fontSize = 16.sp,
                    color = when (isSystemInDarkTheme()) {
                        true -> Color.Green
                        else -> Color.Red
                    },
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "$scannedResistorCount " + stringResource(R.string.scanned),
                color = MaterialTheme.colorScheme.onBackground.copy(ContentAlpha.medium),
                fontSize = 14.sp
            )
        }

        Box(modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .clickable { onClear() }
            .border(
                color = MaterialTheme.colorScheme.onBackground.copy(0.6f),
                shape = CircleShape,
                width = 1.dp
            )
            .padding(12.dp), contentAlignment = Center) {
            Icon(
                imageVector = Icons.Outlined.ClearAll,
                contentDescription = stringResource(R.string.clear_all_resistors),
                tint = MaterialTheme.colorScheme.onBackground.copy(0.6f)
            )
        }


    }
}

@Composable
fun BottomSheetESP(modifier: Modifier) {
    Box(modifier = modifier, contentAlignment = Center) {
        Text(
            text = stringResource(R.string.no_resistor),
            color = MaterialTheme.colorScheme.onBackground.copy(ContentAlpha.medium),
            fontSize = 16.sp
        )
    }
}

@Composable
fun BottomSheetContent(mainViewModel: MainViewModel) {
    Box(
        modifier = Modifier
            .navigationBarsPadding()
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Erfan Khadivar",
                color = MaterialTheme.colorScheme.onBackground.copy(ContentAlpha.medium),
                fontSize = 16.sp
            )
        }

//        when (mainViewModel.scannedPriceTags.size == 0) {
//            true -> BottomSheetESP(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(top = 20.dp, bottom = 20.dp)
//                    .padding(
//                        16.dp
//                    )
//            )
//
//            false -> {
//                Column(
//                    modifier = Modifier
//                        .navigationBarsPadding()
//                        .fillMaxWidth()
//                        .background(
//                            when (isSystemInDarkTheme()) {
//                                true -> sheetBackgroundDarkColor
//                                else -> sheetBackgroundColor
//                            }
//                        )
//                ) {
//
//
//                    BottomSheetHeader(modifier = Modifier
//                        .fillMaxWidth()
//                        .background(
//                            when (isSystemInDarkTheme()) {
//                                true -> sheetSurfaceDarkColor
//                                else -> sheetSurfaceColor
//                            }
//                        )
//                        .padding(top = 20.dp) // for handle after background to include it to the handle
//                        .padding(
//                            start = 16.dp, end = 16.dp
//                        ),
//                        scannedResistorCount = mainViewModel.scannedPriceTags.size,
//                        total = mainViewModel.scannedPriceTags.sumOf { it.price * it.multiplier },
//                        onClear = { mainViewModel.clearScannedTagsConfirmDialog = true })
//
//
//                    Column(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(top = 8.dp)
//                            .background(
//                                when (isSystemInDarkTheme()) {
//                                    true -> sheetSurfaceDarkColor
//                                    else -> sheetSurfaceColor
//                                }
//                            )
//                    ) {
//
//                        LazyColumn(modifier = Modifier.fillMaxWidth()) {
//                            itemsIndexed(mainViewModel.scannedPriceTags) { index, item ->
//                                Column {
//                                    ScannedResistorItem(modifier = Modifier
//                                        .fillMaxWidth()
//                                        .padding(vertical = 4.dp),
//                                        Resistor = item,
//                                        onDelete = {
//                                            mainViewModel.scannedPriceTags.removeAt(index)
//                                        })
//                                    if (index + 1 < mainViewModel.scannedPriceTags.size) {
//                                        Divider(
//                                            color = MaterialTheme.colorScheme.onBackground.copy(0.2f)
//                                        )
//                                    }
//
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
    }

}

@Composable
fun ScannedResistorItem(modifier: Modifier, resistor: ScannedResistor, onDelete: () -> Unit) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(5.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${resistor.value ?: stringResource(R.string.unknown_resistor)}",
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Row {
                Text(
                    text = "value => ${resistor.value}",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onBackground.copy(ContentAlpha.medium)
                )
            }
        }

        Box(modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .clickable { onDelete() }
            .border(
                color = MaterialTheme.colorScheme.onBackground.copy(0.2f),
                shape = CircleShape,
                width = 1.dp
            )
            .padding(8.dp), contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.remove_resistor),
                tint = MaterialTheme.colorScheme.onBackground.copy(0.2f)
            )
        }

    }
}