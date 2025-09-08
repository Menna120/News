package com.example.news.ui.screens

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.news.R
import com.example.news.navigation.CategoryNews

data class Category(@param:StringRes val name: Int, @param:DrawableRes val iconResId: Int)

val categories = listOf(
    Category(R.string.general, R.drawable.general),
    Category(R.string.business, R.drawable.busniess),
    Category(R.string.sports, R.drawable.sports),
    Category(R.string.technology, R.drawable.technology),
    Category(R.string.entertainment, R.drawable.entertainment),
    Category(R.string.health, R.drawable.health),
    Category(R.string.science, R.drawable.science)
)

@Composable
fun HomeScreen(navController: NavController, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.padding(top = 8.dp) // Add some padding at the top of the list
    ) {
        items(categories) { category ->
            val categoryName = stringResource(category.name)
            CategoryItem(
                category = category,
                onItemClick = {
                    navController.navigate(CategoryNews(categoryName = categoryName))
                }
            )
        }
    }
}

@Composable
fun CategoryItem(
    category: Category,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val categoryName = stringResource(category.name)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onItemClick(categoryName) }
            .padding(horizontal = 16.dp, vertical = 12.dp), // Padding for each item
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = category.iconResId),
            contentDescription = categoryName,
            modifier = Modifier.size(24.dp) // Adjust icon size as needed
        )
        Spacer(modifier = Modifier.width(16.dp)) // Space between icon and text
        Text(
            text = categoryName,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
