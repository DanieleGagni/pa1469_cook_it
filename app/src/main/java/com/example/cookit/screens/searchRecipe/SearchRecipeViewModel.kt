package com.example.cookit.screens.searchRecipe

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.google.gson.Gson


class SearchRecipeViewModel(application: Application) : AndroidViewModel(application) {

    private val db = Firebase.firestore
    private val recipesCollection = db.collection("recipes")

    private val _searchHistory = MutableStateFlow<List<String>>(emptyList())
    val searchHistory: StateFlow<List<String>> get() = _searchHistory

    private val _ingredients = MutableStateFlow<List<String>>(emptyList())
    val ingredients: StateFlow<List<String>> get() = _ingredients

    private val _recipeIds = MutableStateFlow<List<String>>(emptyList())

    fun updateSearchHistory(text: String) {
        if (text.isNotBlank() && !_searchHistory.value.contains(text)) {
            _searchHistory.update { currentList ->
                // only the last 10 items
                (listOf(text) + currentList).take(10)
            }
        }
    }

    // extract meaningful words from user input query
    private fun extractKeywords(title: String): List<String> {

        // \b[\w]+\b --> match alphanumeric words
        val regex = "\\b[\\w]+\\b".toRegex()

        val STOP_WORDS = setOf(
            "a", "an", "and", "at", "by", "for", "from", "in", "into", "of",
            "on", "or", "the", "to", "with", "your", "my", "our", "their", "this",
            "that", "these", "those", "over", "under", "around", "inside", "outside",
            "through", "onto", "off", "how", "make", "recipe", "easy", "quick",
            "best", "delicious", "simple", "perfect", "classic", "homemade",
            "ultimate", "basic", "favorite", "authentic"
        )

        val extractedKeywords = regex.findAll(title)
            .map { it.value }
            .map { it.lowercase() }
            .filter { it.any { char -> char.isLetter() } } // exclude numbers
            .filter { it !in STOP_WORDS }
            .toList()

        extractedKeywords.forEach { keyword ->
            Log.d("extractTitleKeywords ------------ ", keyword)
        }

        return extractedKeywords
    }

    fun searchByTitle(navController: NavHostController, title: String) {
        if(title.isEmpty()) {
            return
        } else {
            val keywords = extractKeywords(title)

            if(keywords.isEmpty()) {
                navController.navigate("listRecipes/[]")
            } else {
                viewModelScope.launch {
                    recipesCollection
                        .whereArrayContainsAny("title_keywords", keywords)
                        .get()
                        .addOnSuccessListener { documents ->
                            if (documents.isEmpty) {
                                navController.navigate("listRecipes/[]")
                            } else {

                                val recipeIds = documents.map { document -> document.id }
                                recipeIds.forEach { recipeId ->
                                    Log.d("[------------------------- DEBUG]", "Recipe ID: $recipeId")
                                }

                                _recipeIds.value = recipeIds
                                val recipeIdsJson = Uri.encode(Gson().toJson(recipeIds)) // serialize
                                Log.d("[------------------------- DEBUG]", "Recipe IDs JSON: $recipeIdsJson")

                                navController.navigate("listRecipes/$recipeIdsJson")
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.w(
                                "[------------------------- DEBUG]",
                                "Error getting documents: ",
                                exception
                            )
                        }
                }
            }
        }

    }

    fun addIngredient(ingredient: String) {
        _ingredients.update { currentList ->
            (listOf(ingredient) + currentList)
        }
    }

    fun removeIngredient(ingredient: String) {
        _ingredients.update { currentList ->
            currentList.filter { it != ingredient }
        }

    }

    fun searchByIngredients(navController: NavHostController, ingredients: List<String>) {

        ingredients.forEach { ingredient ->
            Log.d(
                "[------------------------- DEBUG]",
                "------------------------- ${ingredient}"
            )
        }

        viewModelScope.launch {
            recipesCollection
                .whereArrayContainsAny("ingredients_keywords", ingredients)
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        navController.navigate("listRecipes/[]")
                    } else {

                        val recipeIds = documents.map { document -> document.id }
                        recipeIds.forEach { recipeId ->
                            Log.d("[------------------------- DEBUG]", "Recipe ID: $recipeId")
                        }

                        _recipeIds.value = recipeIds
                        val recipeIdsJson = Uri.encode(Gson().toJson(recipeIds)) // serialize
                        Log.d("[------------------------- DEBUG]", "Recipe IDs JSON: $recipeIdsJson")

                        navController.navigate("listRecipes/$recipeIdsJson")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(
                        "[------------------------- DEBUG]",
                        "Error getting documents: ",
                        exception
                    )
                }
        }
    }
}