package com.example.cookit.screens.searchRecipe

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.cookit.screens.components.Recipe
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class SearchRecipeViewModel(application: Application) : AndroidViewModel(application) {

    private val db = Firebase.firestore
    private val recipesCollection = db.collection("recipes")

    private val gson = Gson()

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

    fun searchByTitle(navController: NavHostController, title: String) {
        if(title.isEmpty()) {
            return
        } else {
            val keywords = extractKeywords(title)
            // TODO exact match on keywords -> improvement: fuzzy search on keywords

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
                                val recipeIdsJson = gson.toJson(recipeIds) // serialize
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
                        val recipeIdsJson = gson.toJson(recipeIds) // serialize
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

    // extract meaningful words from user input query
    private fun extractKeywords(title: String): List<String> {

        val TITLE_STOP_WORDS = setOf(
            "a", "an", "and", "as", "at", "by", "for", "from", "in", "into", "of",
            "on", "or", "the", "to", "with", "your", "my", "our", "their", "this",
            "that", "these", "those", "over", "under", "around", "up", "down", "out",
            "inside", "outside", "through", "onto", "off"
        )

        // \b([A-Za-z]+)\b --> matches only alphabetic characters
        val wordPattern = "\\b([A-Za-z]+)\\b".toRegex()

        val  extractedKeywords =  wordPattern.findAll(title)
            .map { it.groupValues[1] } //extracts ONLY the captured word group
            .map { it.lowercase() }
            .filter { it !in TITLE_STOP_WORDS }
            .toList()

        extractedKeywords.forEach { keyword ->
            Log.d("[------------------------- DEBUG]", "------------------------- $keyword")
        }

        return extractedKeywords
    }

    // ======================================================================================================================

    fun storeRecipesInFirebase() {

        Log.d("[------------------------- DEBUG]", "------------------------------ INIZIO")

        // Open the JSON file from the assets folder
        val assetManager = getApplication<Application>().assets
        val inputStream = assetManager.open("recipes_final_version.json")

        // Read the file content
        val json = inputStream.bufferedReader().use { it.readText() }

        // Parse the JSON into a list of Recipe objects
        val gson = Gson()
        val recipeType = object : TypeToken<List<Recipe>>() {}.type
        val recipes: List<Recipe> = gson.fromJson(json, recipeType)

        Log.d("[------------------------- DEBUG]", "Parsed recipes: $recipes")

        // Store each recipe in Firebase Firestore
        recipes.forEach { recipe ->
            // Create a new document for each recipe
            db.collection("recipes")
                .add(recipe)
                .addOnSuccessListener {
                    Log.d("[------------------------- DEBUG]", "Recipe '${recipe.title}' added successfully. || RecipeID = ${recipe.id}")
                }
                .addOnFailureListener { e ->
                    Log.e("[------------------------- DEBUG]", "Error adding recipe '${recipe.title}': ${e.message}")
                }
        }
    }

    /*
    GET ID WHILE YOU ARE INSERTING A RECIPE TO FIRESTORE
    ====================================================

    viewModelScope.launch {
        // Reference to your Firestore collection
        val recipesCollection = firestore.collection("recipes")

        // Add a new document to Firestore
        recipesCollection.add(recipe)
            .addOnSuccessListener { documentReference ->
                // The document is successfully added, and Firestore generates an ID for the new document.
                Log.d("[------------------------- DEBUG]", "Recipe added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                // Handle errors
                Log.e("[------------------------- DEBUG]", "Error adding recipe", e)
            }
    }
     */

    /*

    MAP FIRESTORE ID WHILE YOU ARE RETRIEVING A RECIPE FROM FIRESTORE

    viewModelScope.launch {
    recipesCollection
        .whereArrayContainsAny("title_keywords", keywords)
        .get()
        .addOnSuccessListener { documents ->
            if (!documents.isEmpty) {
                Log.w(
                    "[------------------------- DEBUG]",
                    "------------------------- aaaaa"
                )

                val recipeList = documents.map { document ->
                    // Map the Firestore document to Recipe and include the document ID
                    val recipe = document.toObject(Recipe::class.java)
                    // Set the Firestore document ID to the recipe
                    recipe.copy(id = document.id)  // Assign the ID here
                }

                // Update StateFlows with the fetched data
                _recipes.value = recipeList
                _recipes.value.forEach { recipe ->
                    Log.d(
                        "[------------------------- DEBUG]",
                        "------------------------- ${recipe.title}"
                    )
                }
            }
        }
}
     */
}