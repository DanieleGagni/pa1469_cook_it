package com.example.cookit.screens.recipe

import androidx.lifecycle.ViewModel
import com.example.cookit.screens.components.Recipe
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class RecipeViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite

    fun loadFavoriteStatus(recipeId: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val favoritesRef = db.collection("favorites").document(userId)

        favoritesRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val currentFavorites = document.get("recipes") as? List<String> ?: listOf()
                    _isFavorite.value = recipeId in currentFavorites
                } else {
                    _isFavorite.value = false
                }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                _isFavorite.value = false
            }
    }

    fun toggleFavorite(recipe: Recipe) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val favoritesRef = db.collection("favorites").document(userId)

        // Check if the favorites document exists
        favoritesRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // If the document exists, retrieve the current list of favorite recipes
                    val currentFavorites = document.get("recipes") as? List<String> ?: listOf()

                    if (recipe.id in currentFavorites) {
                        // If the recipe is already a favorite, remove it
                        favoritesRef.update("recipes", FieldValue.arrayRemove(recipe.id))
                            .addOnSuccessListener { _isFavorite.value = false }
                            .addOnFailureListener { it.printStackTrace() }
                    } else {
                        // If the recipe is not a favorite, add it
                        favoritesRef.update("recipes", FieldValue.arrayUnion(recipe.id))
                            .addOnSuccessListener { _isFavorite.value = true }
                            .addOnFailureListener { it.printStackTrace() }
                    }
                } else {
                    // If no document exists, create a new one with the current recipe as the first favorite
                    favoritesRef.set(mapOf("recipes" to listOf(recipe.id)))
                        .addOnSuccessListener { _isFavorite.value = true }
                        .addOnFailureListener { it.printStackTrace() }
                }
            }
            .addOnFailureListener { it.printStackTrace() }
    }

    fun addIngredientsToShoppingList(
        ingredients: List<String>,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val shoppingListRef = db.collection("shoppingLists").document(userId)

        shoppingListRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val currentItems = documentSnapshot.get("items") as? List<Map<String, Any>> ?: emptyList()

                    // Add the new ingredients to the list
                    val updatedItems = currentItems.toMutableList()
                    ingredients.forEach { ingredient ->
                        val newItem = mapOf("done" to false, "entry" to ingredient)
                        updatedItems.add(newItem)
                    }

                    // Write the updated list back to Firestore
                    shoppingListRef.update("items", updatedItems)
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { exception ->
                            onFailure(exception)
                        }
                } else {
                    // No document exists for the user, create one with the ingredients
                    val initialItems = ingredients.map { ingredient ->
                        mapOf("done" to false, "entry" to ingredient)
                    }
                    shoppingListRef.set(mapOf("items" to initialItems))
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { exception ->
                            onFailure(exception)
                        }
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun deleteRecipe(recipeId: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val recipeRef = db.collection("recipes").document(recipeId)
        recipeRef.delete()
            .addOnSuccessListener {
                println("Recipe deleted successfully")
            }
            .addOnFailureListener { exception ->
                println("Error deleting recipe: ${exception.message}")
                exception.printStackTrace()
            }
    }
}