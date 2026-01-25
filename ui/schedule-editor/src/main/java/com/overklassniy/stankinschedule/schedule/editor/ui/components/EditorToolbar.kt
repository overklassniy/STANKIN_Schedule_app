package com.overklassniy.stankinschedule.schedule.editor.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.overklassniy.stankinschedule.core.ui.components.BackButton
import com.overklassniy.stankinschedule.schedule.editor.ui.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorToolbar(
    onApplyClicked: () -> Unit,
    onDeleteClicked: () -> Unit,
    onBackClicked: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    TopAppBar(
        title = {
            Text(text = stringResource(R.string.editor_title))
        },
        navigationIcon = {
            BackButton(onClick = onBackClicked)
        },
        actions = {
            IconButton(onClick = onDeleteClicked) {
                Icon(
                    painter = painterResource(R.drawable.ic_delete_pair),
                    contentDescription = null
                )
            }
            IconButton(onClick = onApplyClicked) {
                Icon(
                    painter = painterResource(R.drawable.ic_done),
                    contentDescription = null
                )
            }
        },
        scrollBehavior = scrollBehavior
    )
}