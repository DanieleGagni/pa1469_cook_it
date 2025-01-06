package com.example.cookit.screens.components

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID


@Parcelize
data class Recipe(
    val id: String = "",
    val title: String = "",
    val title_keywords: List<String> = emptyList(),
    val ingredients: List<String> = emptyList(),
    val ingredients_keywords: List<String> = emptyList(),
    val estimatedTime: Int = 0,
    val serves: Int = 0,
    val steps: List<String> = emptyList(),
    val type: String = "",
    val createdBy: String = ""
) : Parcelable {
    companion object {
        fun create(
            title: String = "",
            ingredients: List<String> = emptyList(),
            estimatedTime: Int = 0,
            serves: Int = 0,
            steps: List<String> = emptyList(),
            type: String = "",
            createdBy: String = ""
        ): Recipe {
            return Recipe(
                id = UUID.randomUUID().toString(),
                title = title,
                title_keywords = emptyList(),
                ingredients = ingredients,
                ingredients_keywords = emptyList(),
                estimatedTime = estimatedTime,
                serves = serves,
                steps = steps,
                type = type,
                createdBy = createdBy
            )
        }
    }
}
