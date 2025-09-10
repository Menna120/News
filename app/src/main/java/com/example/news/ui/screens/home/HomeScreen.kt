package com.example.news.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.news.R
import com.example.news.ui.theme.NewsTheme
import java.util.Calendar

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onCategoryNavigate: (String) -> Unit
) {
    LazyColumn(
        modifier = modifier.padding(top = 8.dp),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            val calendar = Calendar.getInstance()
            val greeting = when (calendar.get(Calendar.HOUR_OF_DAY)) {
                in 0..11 -> stringResource(R.string.good_morning)
                in 12..17 -> stringResource(R.string.good_afternoon)
                else -> stringResource(R.string.good_evening)
            }

            Text(
                text = greeting,
                style = MaterialTheme.typography.headlineSmall
            )
        }

        item {
            Text(
                stringResource(R.string.home_intro),
                style = MaterialTheme.typography.headlineSmall
            )
        }

        itemsIndexed(categories) { index, category ->
            CategoryItem(
                category = category,
                index = index,
                onItemClick = onCategoryNavigate
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun HomeScreenPreview() {
    NewsTheme {
        HomeScreen {}
    }
}

@Composable
fun CategoryItem(
    category: Category,
    index: Int,
    modifier: Modifier = Modifier,
    onItemClick: (String) -> Unit,
) {
    val categoryName = stringResource(category.name)

    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.onBackground,
            contentColor = MaterialTheme.colorScheme.background
        )
    ) {
        val isEven = index % 2 == 0
        val imageAlign = if (isEven) Alignment.CenterStart else Alignment.CenterEnd
        val textAlign = if (isEven) Alignment.TopEnd else Alignment.TopStart
        val buttonAlign = if (isEven) Alignment.BottomEnd else Alignment.BottomStart

        Box(
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = category.iconResId),
                contentDescription = categoryName,
                modifier = Modifier
                    .fillMaxHeight()
                    .align(imageAlign),
                contentScale = ContentScale.Fit
            )

            Text(
                text = categoryName,
                modifier = Modifier
                    .padding(16.dp, 32.dp)
                    .fillMaxWidth(.5f)
                    .wrapContentWidth(unbounded = true)
                    .align(textAlign),
                style = MaterialTheme.typography.displaySmall,
                textAlign = TextAlign.Center
            )

            Button(
                onClick = { onItemClick(categoryName) },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(.5f)
                    .align(buttonAlign),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.background.copy(alpha = .5f),
                    contentColor = MaterialTheme.colorScheme.onBackground
                )
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(R.string.view_all),
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .align(imageAlign),
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Icon(
                        painter = painterResource(R.drawable.ic_caret_right),
                        contentDescription = null,
                        modifier = Modifier
                            .align(textAlign)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.background)
                            .padding(16.dp)
                            .graphicsLayer { rotationY = if (isEven) 0f else 180f },
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun CategoryItemPreview() {
    NewsTheme {
        CategoryItem(
            category = categories[0],
            index = 0,
            onItemClick = {}
        )
    }
}
