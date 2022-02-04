/*
 * (c) VAP Communications Group, 2021
 */

package online.vapcom.swcomp.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import online.vapcom.swcomp.R
import online.vapcom.swcomp.data.ErrorDescription
import online.vapcom.swcomp.data.UIErrno

/**
 * Отображает описание ошибки. Отдельно отображаются сетевые ошибки.
 */
@Composable
fun ErrorBody(error: ErrorDescription, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        item {
            when (error.code) {
                UIErrno.CONNECTION_ERROR.errno -> NetworkErrorBody(
                    stringResource(R.string.connection_error), error.desc,
                    stringResource(R.string.check_network_connection)
                )

                UIErrno.CLIENT_ERROR.errno -> NetworkErrorBody(
                    stringResource(R.string.client_problem), error.desc,
                    stringResource(R.string.retry_or_update)
                )

                UIErrno.SERVER_ERROR.errno -> NetworkErrorBody(
                    stringResource(R.string.server_side_problem), error.desc,
                    stringResource(R.string.our_problem)
                )

                UIErrno.TIMEOUT.errno -> NetworkErrorBody(
                    stringResource(R.string.no_server_response), error.desc,
                    stringResource(R.string.try_later)
                )

                UIErrno.DATA_NOT_FOUND.errno -> NetworkErrorBody(
                    stringResource(R.string.word_not_found), error.desc,
                    stringResource(R.string.server_side_problem)
                )

                else -> CommonErrorBody(
                    stringResource(R.string.try_again), error,
                    stringResource(R.string.call_support), modifier
                )
            }
        }
    }
}


@Composable
fun NetworkErrorBody(firstMessage: String, desc: String, whatToDoMessage: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(horizontal = 16.dp)) {
        Text(
            text = stringResource(R.string.error),
            style = MaterialTheme.typography.h6,
            color = MaterialTheme.colors.error,
            modifier = Modifier.padding(top = 16.dp)
        )
        Text(
            text = firstMessage,
            style = MaterialTheme.typography.body1,
            modifier = Modifier.padding(top = 16.dp)
        )
        if(desc.isNotBlank()) {
            Text(
                text = desc,
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        Text(
            text = whatToDoMessage,
            style = MaterialTheme.typography.body1,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
fun CommonErrorBody(firstMessage: String, error: ErrorDescription, whatToDoMessage: String, modifier: Modifier) {
    Column(modifier = modifier.padding(horizontal = 16.dp)) {
        Row(modifier = Modifier.padding(top = 16.dp)) {
            Image(
                painter = painterResource(R.drawable.ic_error),
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )
            Text(
                text = stringResource(R.string.request_error),
                style = MaterialTheme.typography.h6,
                color = MaterialTheme.colors.error,
                modifier = Modifier
                    .padding(start = 16.dp)
                    .align(alignment = Alignment.CenterVertically)
            )
        }

        if(error.code != 0 || error.desc.isNotBlank()) {
            Text(
                text = stringResource(R.string.error_description, error.code, error.desc),
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.error,
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        Text(
            text = if(firstMessage.isNotBlank()) firstMessage else stringResource(R.string.try_again),
            style = MaterialTheme.typography.body1,
            modifier = Modifier.padding(top = 16.dp)
        )

        Text(
            text = whatToDoMessage,
            style = MaterialTheme.typography.body1,
            modifier = Modifier.padding(top = 8.dp)
        )

        //TODO: Report error button
    }
}


@Preview(showBackground = true)
@Composable
fun NetworkErrorPreview() {
    ErrorBody(error = ErrorDescription(false, UIErrno.CONNECTION_ERROR.errno,
    "Something happened to me",
    "Something I can't control"))
}

@Preview(showBackground = true)
@Composable
fun CommonErrorPreview() {
    ErrorBody(error = ErrorDescription(false, 1011,
        "Something happened to me",
        "Something I can't control"))
}