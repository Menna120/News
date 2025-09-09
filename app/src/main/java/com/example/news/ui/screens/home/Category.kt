package com.example.news.ui.screens.home

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.news.R

data class Category(
    @param:StringRes val name: Int,
    @param:DrawableRes val iconResId: Int
)

val categories = listOf(
    Category(R.string.general, R.drawable.general),
    Category(R.string.business, R.drawable.busniess),
    Category(R.string.sports, R.drawable.sports),
    Category(R.string.technology, R.drawable.technology),
    Category(R.string.entertainment, R.drawable.entertainment),
    Category(R.string.health, R.drawable.health),
    Category(R.string.science, R.drawable.science)
)
