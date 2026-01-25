package com.overklassniy.stankinschedule.schedule.table.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.overklassniy.stankinschedule.core.ui.components.OutlinedSelectField
import com.overklassniy.stankinschedule.core.ui.theme.Dimen
import com.overklassniy.stankinschedule.schedule.table.domain.model.TableMode
import com.overklassniy.stankinschedule.schedule.table.ui.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsSheet(
    tableMode: TableMode,
    onTableModeChanged: (mode: TableMode) -> Unit,
    onSendCopyClicked: () -> Unit,
    onSaveClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(vertical = Dimen.ContentPadding)
    ) {
        Text(
            text = stringResource(R.string.table_settings_title),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimen.ContentPadding * 2)
        )

        OutlinedSelectField(
            value = tableMode,
            onValueChanged = onTableModeChanged,
            items = TableMode.values().toList(),
            menuLabel = { mode ->
                when (mode) {
                    TableMode.Full -> stringResource(R.string.table_full)
                    TableMode.Weekly -> stringResource(R.string.table_weekly)
                }
            },
            label = {
                Text(text = stringResource(R.string.table_view_mode))
            },
            modifier = Modifier
                .padding(horizontal = Dimen.ContentPadding)
                .fillMaxWidth()
        )

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimen.ContentPadding)
        )

        ListItem(
            headlineContent = { Text(text = stringResource(R.string.send_copy)) },
            leadingContent = {
                Icon(
                    painter = painterResource(R.drawable.ic_send_copy),
                    contentDescription = null
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onSendCopyClicked)
        )

        ListItem(
            headlineContent = { Text(text = stringResource(R.string.save_as)) },
            leadingContent = {
                Icon(
                    painter = painterResource(R.drawable.ic_save),
                    contentDescription = null
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onSaveClicked)
        )
    }
}