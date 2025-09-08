package com.example.news.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.news.R
import com.example.news.ui.navigation.Home
import com.example.news.ui.theme.NewsTheme
import com.example.news.utils.THEME_DARK
import com.example.news.utils.THEME_LIGHT
import com.example.news.utils.THEME_SYSTEM
import com.example.news.utils.languageCodeToNameMap

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsDrawer(
    navController: NavController,
    currentThemePreference: String,
    onThemePreferenceChanged: (String) -> Unit,
    currentLanguageCode: String,
    onLanguagePreferenceChanged: (String) -> Unit,
    onCloseDrawer: () -> Unit
) {
    var selectedTheme by remember(currentThemePreference) { mutableStateOf(currentThemePreference) }
    val themeOptions = listOf(THEME_SYSTEM, THEME_LIGHT, THEME_DARK)
    var themeExpanded by remember { mutableStateOf(false) }

    var selectedLanguageCode by remember(currentLanguageCode) { mutableStateOf(currentLanguageCode) }
    val languageDropdownOptions = languageCodeToNameMap.entries.toList()
    var languageExpanded by remember { mutableStateOf(false) }

    val onLanguageChangeInternal: (String) -> Unit = { code ->
        selectedLanguageCode = code
        onLanguagePreferenceChanged(code)
        languageExpanded = false
    }

    val onThemeChange: (String) -> Unit = { themeValue ->
        selectedTheme = themeValue
        onThemePreferenceChanged(themeValue)
        themeExpanded = false
    }

    val defaultNavigationDrawerItemColors = NavigationDrawerItemDefaults.colors(
        selectedContainerColor = Color.Transparent,
        selectedIconColor = MaterialTheme.colorScheme.onBackground,
        unselectedIconColor = MaterialTheme.colorScheme.onBackground,
        selectedTextColor = MaterialTheme.colorScheme.onBackground,
        unselectedTextColor = MaterialTheme.colorScheme.onBackground
    )

    val defaultOutlinedTextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.onBackground,
        unfocusedBorderColor = MaterialTheme.colorScheme.onBackground,
    )

    val defaultDropdownMenuModifier = Modifier
        .fillMaxWidth()
        .padding(NavigationDrawerItemDefaults.ItemPadding)

    val defaultDropdownTextStyle = MaterialTheme.typography.titleLarge
    val defaultDropdownShape = MaterialTheme.shapes.large

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = navBackStackEntry?.destination?.route ?: ""
    ModalDrawerSheet(
        drawerContainerColor = MaterialTheme.colorScheme.background,
        drawerContentColor = MaterialTheme.colorScheme.onBackground
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                painterResource(R.drawable.ic_news_logo), "", Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f)
                    .background(
                        MaterialTheme.colorScheme.onBackground
                    ),
                MaterialTheme.colorScheme.background
            )

            NavigationDrawerItem(
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_home),
                        contentDescription = stringResource(id = R.string.home)
                    )
                },
                label = {
                    Text(
                        stringResource(id = R.string.home),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                colors = defaultNavigationDrawerItemColors,
                selected = currentScreen == Home::class.qualifiedName,
                onClick = {
                    navController.navigate(Home) {
                        launchSingleTop = true
                        restoreState = true
                    }
                    onCloseDrawer()
                }
            )

            HorizontalDivider(
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                thickness = DividerDefaults.Thickness,
                color = MaterialTheme.colorScheme.onBackground
            )

            NavigationDrawerItem(
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_roller_paint_brush),
                        contentDescription = stringResource(id = R.string.theme)
                    )
                },
                label = {
                    Text(
                        stringResource(id = R.string.theme),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                colors = defaultNavigationDrawerItemColors,
                selected = false,
                onClick = { themeExpanded = !themeExpanded }
            )

            ExposedDropdownMenuBox(
                expanded = themeExpanded,
                onExpandedChange = { themeExpanded = !themeExpanded }
            ) {
                OutlinedTextField(
                    value = selectedTheme,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        Icon(
                            painterResource(R.drawable.ic_exposed_drop_menu),
                            null,
                            Modifier.rotate(if (themeExpanded) 180f else 0f)
                        )
                    },
                    textStyle = defaultDropdownTextStyle,
                    shape = defaultDropdownShape,
                    colors = defaultOutlinedTextFieldColors,
                    modifier = defaultDropdownMenuModifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable)
                )
                ExposedDropdownMenu(
                    expanded = themeExpanded,
                    containerColor = MaterialTheme.colorScheme.background,
                    onDismissRequest = { themeExpanded = false }
                ) {
                    themeOptions.forEach { themeValue ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    themeValue,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            },
                            onClick = { onThemeChange(themeValue) }
                        )
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                thickness = DividerDefaults.Thickness,
                color = MaterialTheme.colorScheme.onBackground
            )

            NavigationDrawerItem(
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_globe_alt),
                        contentDescription = stringResource(id = R.string.language)
                    )
                },
                label = {
                    Text(
                        stringResource(id = R.string.language),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                colors = defaultNavigationDrawerItemColors,
                selected = false,
                onClick = { languageExpanded = !languageExpanded }
            )
            ExposedDropdownMenuBox(
                expanded = languageExpanded,
                onExpandedChange = { languageExpanded = !languageExpanded }
            ) {
                OutlinedTextField(
                    value = languageCodeToNameMap[selectedLanguageCode] ?: selectedLanguageCode,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        Icon(
                            painterResource(R.drawable.ic_exposed_drop_menu),
                            null,
                            Modifier.rotate(if (languageExpanded) 180f else 0f)
                        )
                    },
                    textStyle = defaultDropdownTextStyle,
                    shape = defaultDropdownShape,
                    colors = defaultOutlinedTextFieldColors,
                    modifier = defaultDropdownMenuModifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable)
                )
                ExposedDropdownMenu(
                    expanded = languageExpanded,
                    containerColor = MaterialTheme.colorScheme.background,
                    onDismissRequest = { languageExpanded = false }
                ) {
                    languageDropdownOptions.forEach { (code, name) ->
                        DropdownMenuItem(
                            text = { Text(name, style = MaterialTheme.typography.titleMedium) },
                            onClick = { onLanguageChangeInternal(code) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun AppDrawerContentPreview() {
    val navController = rememberNavController()
    NewsTheme {
        NewsDrawer(
            navController = navController,
            currentThemePreference = THEME_SYSTEM,
            onThemePreferenceChanged = {},
            currentLanguageCode = "en",
            onLanguagePreferenceChanged = {},
            onCloseDrawer = {}
        )
    }
}
