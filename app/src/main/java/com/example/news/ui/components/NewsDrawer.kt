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
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.news.R
import com.example.news.ui.navigation.Home
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

    val defaultNavigationDrawerItemColors = NavigationDrawerItemDefaults.colors(
        selectedContainerColor = Color.Transparent,
        selectedIconColor = MaterialTheme.colorScheme.onBackground,
        unselectedIconColor = MaterialTheme.colorScheme.onBackground,
        selectedTextColor = MaterialTheme.colorScheme.onBackground,
        unselectedTextColor = MaterialTheme.colorScheme.onBackground
    )

    val defaultOutlinedTextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.onBackground,
        unfocusedBorderColor = MaterialTheme.colorScheme.onBackground
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
                painterResource(R.drawable.ic_news_logo),
                stringResource(id = R.string.app_name),
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f)
                    .background(MaterialTheme.colorScheme.onBackground),
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
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = true
                        }
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

            SettingsExposedDropdownMenu(
                currentValue = selectedTheme,
                expanded = themeExpanded,
                onExpandedChange = { themeExpanded = it },
                onDismissRequest = { themeExpanded = false },
                options = themeOptions,
                onOptionSelected = { themeValue ->
                    selectedTheme = themeValue
                    onThemePreferenceChanged(themeValue)
                    themeExpanded = false
                },
                modifier = defaultDropdownMenuModifier,
                textFieldTextStyle = defaultDropdownTextStyle,
                textFieldShape = defaultDropdownShape,
                textFieldColors = defaultOutlinedTextFieldColors,
                dropdownMenuContent = { themeValue, onOptionSelected ->
                    Text(text = themeValue, style = MaterialTheme.typography.titleMedium)
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

            SettingsExposedDropdownMenu(
                currentValue = languageCodeToNameMap[selectedLanguageCode] ?: selectedLanguageCode,
                expanded = languageExpanded,
                onExpandedChange = { languageExpanded = it },
                onDismissRequest = { languageExpanded = false },
                options = languageDropdownOptions,
                onOptionSelected = { (code, _) ->
                    selectedLanguageCode = code
                    onLanguagePreferenceChanged(code)
                    languageExpanded = false
                },
                modifier = defaultDropdownMenuModifier,
                textFieldTextStyle = defaultDropdownTextStyle,
                textFieldShape = defaultDropdownShape,
                textFieldColors = defaultOutlinedTextFieldColors,
                dropdownMenuContent = { (_, name), onOptionSelected ->
                    Text(text = name, style = MaterialTheme.typography.titleMedium)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SettingsExposedDropdownMenu(
    currentValue: String,
    expanded: Boolean,
    modifier: Modifier = Modifier,
    onExpandedChange: (Boolean) -> Unit,
    onDismissRequest: () -> Unit,
    options: List<T>,
    onOptionSelected: (T) -> Unit,
    textFieldTextStyle: TextStyle,
    textFieldShape: Shape,
    textFieldColors: TextFieldColors = OutlinedTextFieldDefaults.colors(),
    dropdownMenuContent: @Composable (T, (T) -> Unit) -> Unit
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandedChange
    ) {
        OutlinedTextField(
            value = currentValue,
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                Icon(
                    painterResource(R.drawable.ic_exposed_drop_menu),
                    stringResource(id = R.string.show_options),
                    Modifier.rotate(if (expanded) 180f else 0f)
                )
            },
            textStyle = textFieldTextStyle,
            shape = textFieldShape,
            colors = textFieldColors,
            modifier = modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismissRequest,
            modifier = Modifier.background(MaterialTheme.colorScheme.background)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { dropdownMenuContent(option, onOptionSelected) },
                    onClick = { onOptionSelected(option) },
                    colors = MenuDefaults.itemColors(
                        textColor = MaterialTheme.colorScheme.onBackground
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun AppDrawerContentPreview() {
    val navController = rememberNavController()

    NewsDrawer(
        navController = navController,
        currentThemePreference = THEME_SYSTEM,
        onThemePreferenceChanged = {},
        currentLanguageCode = "en",
        onLanguagePreferenceChanged = {},
        onCloseDrawer = {}
    )
}
