package com.example.news.ui.drawer

import android.app.LocaleManager
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.news.R
import com.example.news.navigation.Home
import com.example.news.utils.AppPreferences
import com.example.news.utils.THEME_DARK
import com.example.news.utils.THEME_LIGHT
import com.example.news.utils.THEME_SYSTEM
import com.example.news.utils.languageCodeToNameMap

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDrawerContent(
    navController: NavController,
    currentThemePreference: String,
    onThemePreferenceChanged: (String) -> Unit,
    currentLanguageCode: String,
    onCloseDrawer: () -> Unit
) {
    val context = LocalContext.current

    var selectedTheme by remember(currentThemePreference) { mutableStateOf(currentThemePreference) }
    val themeOptions = listOf(THEME_SYSTEM, THEME_LIGHT, THEME_DARK)
    var themeExpanded by remember { mutableStateOf(false) }

    var selectedLanguageCode by remember(currentLanguageCode) { mutableStateOf(currentLanguageCode) }
    val languageDropdownOptions = languageCodeToNameMap.entries.toList()
    var languageExpanded by remember { mutableStateOf(false) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = navBackStackEntry?.destination?.route ?: ""

    Column(modifier = Modifier) {
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
                    painterResource(id = R.drawable.ic_home),
                    contentDescription = stringResource(id = R.string.home)
                )
            },
            label = { Text(stringResource(id = R.string.home)) },
            selected = currentScreen == Home::class.qualifiedName,
            onClick = {
                navController.navigate(Home) {
                    launchSingleTop = true
                    restoreState = true
                }
                onCloseDrawer()
            },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            thickness = DividerDefaults.Thickness,
            color = DividerDefaults.color
        )

        Text(
            text = stringResource(id = R.string.theme),
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
        )
        ExposedDropdownMenuBox(
            expanded = themeExpanded,
            onExpandedChange = { themeExpanded = !themeExpanded }
        ) {
            OutlinedTextField(
                value = selectedTheme,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = themeExpanded) },
                modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable, true)
            )
            ExposedDropdownMenu(
                expanded = themeExpanded,
                onDismissRequest = { themeExpanded = false }
            ) {
                themeOptions.forEach { themeValue ->
                    DropdownMenuItem(
                        text = { Text(themeValue) },
                        onClick = {
                            selectedTheme = themeValue
                            onThemePreferenceChanged(themeValue)
                            themeExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            thickness = DividerDefaults.Thickness,
            color = DividerDefaults.color
        )

        Text(
            text = stringResource(id = R.string.language),
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
        )
        ExposedDropdownMenuBox(
            expanded = languageExpanded,
            onExpandedChange = { languageExpanded = !languageExpanded }
        ) {
            OutlinedTextField(
                value = languageCodeToNameMap[selectedLanguageCode] ?: selectedLanguageCode,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = languageExpanded) },
                modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable, true)
            )
            ExposedDropdownMenu(
                expanded = languageExpanded,
                onDismissRequest = { languageExpanded = false }
            ) {
                languageDropdownOptions.forEach { (code, name) ->
                    DropdownMenuItem(
                        text = { Text(name) },
                        onClick = {
                            selectedLanguageCode = code
                            AppPreferences.saveLanguagePreference(context, code)
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                val localeManager =
                                    context.getSystemService(LocaleManager::class.java)
                                localeManager?.applicationLocales =
                                    LocaleList.forLanguageTags(code)
                            } else {
                                AppCompatDelegate.setApplicationLocales(
                                    LocaleListCompat.forLanguageTags(code)
                                )
                            }
                            languageExpanded = false
                        }
                    )
                }
            }
        }
    }
}
