package com.example.cookit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore
import android.util.Log

class ListRecipeViewModel : ViewModel() {

    private val _recipes = MutableLiveData<List<Recipe>>()   // can be modified by this class
    val recipes: LiveData<List<Recipe>> = _recipes           // can be observed by the UI but not modified

    fun searchRecipes() {
        val db = Firebase.firestore
        val recipesRef = db.collection("recipes")

        viewModelScope.launch { // launch an asynchronous coroutine in ViewModelScope
            recipesRef
                .whereEqualTo("type", "quick")
                .get()
                .addOnSuccessListener { documents ->
                    val recipeList = mutableListOf<Recipe>()
                    for (document in documents) {
                        val recipe = document.toObject(Recipe::class.java)
                        recipeList.add(recipe)
                    }
                    _recipes.value = recipeList
                }
                .addOnFailureListener { exception ->
                    Log.w("ListRecipeViewModel", "Error getting documents: ", exception)
                }
        }
    }
}