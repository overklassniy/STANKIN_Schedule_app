package com.overklassniy.stankinschedule.schedule.parser.ui.forms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.overklassniy.stankinschedule.core.ui.components.Stateful
import com.overklassniy.stankinschedule.core.ui.theme.Dimen
import com.overklassniy.stankinschedule.schedule.parser.ui.R
import com.overklassniy.stankinschedule.schedule.parser.ui.model.ParserState
import com.overklassniy.stankinschedule.schedule.parser.ui.util.ParserErrorMapper

@Composable
fun FinishForm(
    state: ParserState.ImportFinish,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Stateful(
            state = state.state,
            onSuccess = {
                Text(
                    text = stringResource(R.string.successfully_imported),
                    style = MaterialTheme.typography.titleMedium
                )
                Icon(
                    painter = painterResource(R.drawable.ic_done_outline),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(64.dp)
                        .padding(Dimen.ContentPadding)
                )
            },
            onLoading = {
                CircularProgressIndicator()
            },
            onFailed = { throwable ->
                Text(
                    text = ParserErrorMapper.getErrorMessage(context, throwable),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
            }
        )
    }
}