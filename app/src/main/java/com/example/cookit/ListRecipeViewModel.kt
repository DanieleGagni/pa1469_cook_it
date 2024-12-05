package com.example.cookit

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ListRecipeViewModel : ViewModel() {

    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes: StateFlow<List<Recipe>> get() = _recipes

    fun fetchRecipeTitles() {
        val db = Firebase.firestore
        val recipesRef = db.collection("recipes")

        viewModelScope.launch {
            recipesRef
                .get()
                .addOnSuccessListener { documents ->
                    val recipeList = documents.map { document ->
                        document.toObject(Recipe::class.java)
                    }
                    _recipes.value = recipeList // Update StateFlow with the fetched data
                }
                .addOnFailureListener { exception ->
                    Log.w("ListRecipeViewModel", "Error getting documents: ", exception)
                }
        }
    }

    //TODO search logic
    //fun matchRecipeTitles() {}
}
