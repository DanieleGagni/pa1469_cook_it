package com.example.cookit.screens.listRecipes

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cookit.screens.components.Recipe
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class ListRecipesViewModel(application: Application) : AndroidViewModel(application) {

    private val db = Firebase.firestore
    private val recipesCollection = db.collection("recipes")
    private val favoritesCollection = db.collection("favorites")

    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes: StateFlow<List<Recipe>> = _recipes

    private val _favoriteStatuses = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val favoriteStatuses: StateFlow<Map<String, Boolean>> = _favoriteStatuses

    fun fetchRecipesByType(type: String) {

        val t = type.lowercase()

        if(t == "all") {
            viewModelScope.launch {
                recipesCollection
                    .get()
                    .addOnSuccessListener { documents ->
                        val recipeList = documents.map { document ->
                            val recipe = document.toObject(Recipe::class.java)
                            recipe.copy(id = document.id)  // assign recipe.id
                        }

                        _recipes.value = recipeList
                        _recipes.value.forEach { recipe ->
                            Log.d(
                                "[------------------------- DEBUG]",
                                "------------------------- ${recipe.title}"
                            )
                        }
                        fetchFavoriteStatuses()
                    }
                    .addOnFailureListener { exception ->
                        Log.e("ListRecipesViewModel", "Error fetching recipes", exception)
                    }
            }
        } else {

            viewModelScope.launch {
                recipesCollection
                    .whereEqualTo("type", t)
                    .get()
                    .addOnSuccessListener { documents ->
                        val recipeList = documents.map { document ->
                            val recipe = document.toObject(Recipe::class.java)
                            recipe.copy(id = document.id)  // assign recipe.id
                        }

                        _recipes.value = recipeList
                        _recipes.value.forEach { recipe ->
                            Log.d(
                                "[------------------------- DEBUG]",
                                "------------------------- ${recipe.title}"
                            )
                        }
                        fetchFavoriteStatuses()
                    }
                    .addOnFailureListener { exception ->
                        Log.e("ListRecipesViewModel", "Error fetching recipes", exception)
                    }
            }
        }
    }

    fun fetchRecipesByIds(ids: List<String>) {

        if (!ids.isEmpty()) {

            ids.forEach { recipeId ->
                Log.d("[------------------------- DEBUG]", "Recipe ID in ViewModel: $recipeId")
            }

            val idChunks =
                ids.chunked(10)  // Firebase "whereIn" can receive in input a list of maximum 10 elements

            viewModelScope.launch {
                val allRecipes = mutableListOf<Recipe>()

                for (chunk in idChunks) {
                    recipesCollection
                        .whereIn("id", chunk)
                        .get()
                        .addOnSuccessListener { documents ->
                            val chunkRecipes = documents.map { document ->
                                val recipe = document.toObject(Recipe::class.java)
                                recipe.copy(id = document.id)
                            }

                            // Add the chunk's results to the complete list
                            allRecipes.addAll(chunkRecipes)

                            // Update the Flow with the complete list
                            _recipes.value = allRecipes
                            fetchFavoriteStatuses()
                        }
                        .addOnFailureListener { exception ->
                            Log.e("ListRecipesViewModel", "Error fetching recipes", exception)
                        }
                }
            }
        }
    }

    fun fetchFavoriteStatuses() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        favoritesCollection.document(userId).get()
            .addOnSuccessListener { document ->
                val favoriteIds = document.get("recipes") as? List<String> ?: emptyList()
                val favoriteMap = favoriteIds.associateWith { true }
                _favoriteStatuses.value = favoriteMap
            }
            .addOnFailureListener { it.printStackTrace() }
    }

}