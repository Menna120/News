package com.example.news.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.example.news.R
import com.example.news.ui.theme.NewsTheme

@Composable
fun NewsSearchBar(
    modifier: Modifier = Modifier,
    onSendSearchQueryClick: (String) -> Unit
) {
    var query by remember { mutableStateOf("") }
    OutlinedTextField(
        value = query,
        onValueChange = { query = it },
        textStyle = MaterialTheme.typography.titleLarge,
        placeholder = {
            Text(
                stringResource(id = R.string.search),
                style = MaterialTheme.typography.titleMedium
            )
        },
        leadingIcon = {
            IconButton(onClick = { onSendSearchQueryClick(query) }) {
                Icon(
                    painter = painterResource(R.drawable.ic_search),
                    contentDescription = stringResource(id = R.string.search)
                )
            }
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { query = "" }) {
                    Icon(
                        painterResource(R.drawable.ic_clear),
                        contentDescription = stringResource(id = R.string.clear)
                    )
                }
            }
        },
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(onSearch = { onSendSearchQueryClick(query) }
        ),
        shape = MaterialTheme.shapes.large,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onBackground,
            unfocusedTextColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            focusedBorderColor = MaterialTheme.colorScheme.onBackground,
            unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            cursorColor = MaterialTheme.colorScheme.onBackground,
            focusedLeadingIconColor = MaterialTheme.colorScheme.onBackground,
            unfocusedLeadingIconColor = MaterialTheme.colorScheme.onBackground,
            focusedTrailingIconColor = MaterialTheme.colorScheme.onBackground,
            unfocusedTrailingIconColor = MaterialTheme.colorScheme.onBackground,
            focusedPlaceholderColor = MaterialTheme.colorScheme.onBackground,
            unfocusedPlaceholderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
    )
}

@Preview
@Composable
fun NewsSearchBarPreview() {
    NewsTheme {
        NewsSearchBar {}
    }
}
