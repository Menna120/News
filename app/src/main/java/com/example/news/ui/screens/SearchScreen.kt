package com.example.news.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.news.ui.theme.NewsTheme

@Composable
fun SearchScreen(
    navController: NavController,
    searchQuery: String?, // Accepts searchQuery from AppNavHost
    modifier: Modifier = Modifier
) {
    // Placeholder for search results - replace with actual search logic
    val currentQuery = searchQuery ?: "" // Use passed query, default to empty if null
    val searchResults = remember(currentQuery) { // Re-calculates when currentQuery changes
        if (currentQuery.isNotBlank()) {
            // Replace with your actual search data fetching logic
            getSampleNewsForCategory("Search results for '$currentQuery'").take(5)
        } else {
            emptyList()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // OutlinedTextField has been moved to the TopAppBar in MainAppContent.kt
        // Spacer(modifier = Modifier.height(16.dp)) // Original spacer might not be needed or adjusted

        if (currentQuery.isNotBlank() && searchResults.isEmpty()) {
            Text("No results found for '$currentQuery'")
        } else if (searchResults.isNotEmpty()) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(searchResults) { article ->
                    NewsArticleItem(article = article)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        } else { // Handles both null/blank searchQuery from parameter
            Text("Type something in the search bar above to see results.")
        }
    }
}

@Preview
@Composable
fun SearchScreenPreview() {
    NewsTheme {
        SearchScreen(
            navController = rememberNavController(),
            searchQuery = "example query" // Pass a sample query for preview
        )
    }
}
