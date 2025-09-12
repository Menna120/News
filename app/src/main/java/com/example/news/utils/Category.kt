package com.example.news.utils

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.news.R

enum class Category(
    @field:StringRes val title: Int,
    @field:DrawableRes val iconResId: Int
) {
    GENERAL(R.string.general, R.drawable.general),
    BUSINESS(R.string.business, R.drawable.busniess),
    SPORTS(R.string.sports, R.drawable.sports),
    TECHNOLOGY(R.string.technology, R.drawable.technology),
    ENTERTAINMENT(R.string.entertainment, R.drawable.entertainment),
    HEALTH(R.string.health, R.drawable.health),
    SCIENCE(R.string.science, R.drawable.science);

    companion object {
        fun String.toCategory(): Category {
            return when (this.uppercase()) {
                GENERAL.name -> GENERAL
                BUSINESS.name -> BUSINESS
                SPORTS.name -> SPORTS
                TECHNOLOGY.name -> TECHNOLOGY
                ENTERTAINMENT.name -> ENTERTAINMENT
                HEALTH.name -> HEALTH
                SCIENCE.name -> SCIENCE
                else -> GENERAL
            }
        }
    }
}
