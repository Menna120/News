package com.example.news.ui.theme

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import com.example.news.R

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val InterFontName = GoogleFont("Inter")

val InterFontFamily = FontFamily(
    Font(googleFont = InterFontName, fontProvider = provider, weight = FontWeight.Medium),
    Font(googleFont = InterFontName, fontProvider = provider, weight = FontWeight.Bold)
)
