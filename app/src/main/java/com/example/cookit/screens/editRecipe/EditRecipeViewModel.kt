package com.example.cookit.screens.editRecipe

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.example.cookit.screens.components.Recipe
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class EditRecipeViewModel(application: Application) : AndroidViewModel(application) {

    private val db = Firebase.firestore
    private val recipesCollection = db.collection("recipes")

    private val _editingRecipe = MutableStateFlow<Recipe?>(null)
    val editingRecipe: StateFlow<Recipe?> = _editingRecipe

    fun loadRecipe(recipeId: String) {
        recipesCollection
            .document(recipeId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val recipe = documentSnapshot.toObject(Recipe::class.java)
                _editingRecipe.value = recipe

                Log.d(" ---------------------------- EditRecipeViewModel - loadRecipe -------- ",
                    "ID ${_editingRecipe.value!!.id}, TITLE ${_editingRecipe.value!!.title}")
            }
            .addOnFailureListener { e ->
                Log.e("---------------------------- EditRecipeScreen, loadRecipe ------------------- ", "FIREBASE ERROR", e)
                // TODO error handling (Snackbar)}
            }
    }

    fun updateRecipe(
        recipeId: String,
        updatedRecipe: Recipe,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {

        Log.d("---------------------------- EditRecipeScreen, updateRecipe ------------------- ",
            "${updatedRecipe.title} - ${updatedRecipe.id} - $recipeId  well well well")

        recipesCollection.document(recipeId).set(updatedRecipe, SetOptions.merge())
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e ->
                Log.e("---------------------------- EditRecipeScreen, updateRecipe ------------------- ", "FIREBASE ERROR", e)
                // TODO error handling (Snackbar)}
            }
    }

    // extract keywords from recipe title
    fun extractTitleKeywords(title: String): List<String> {

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

    // extract keywords from ingredients
    fun extractIngredientsKeywords(ingredients: List<String>): MutableList<String> {

        val regex = "\\b[\\w]+\\b".toRegex()

        val STOP_WORDS = setOf(
            "cup",
            "teaspoon",
            "tablespoon",
            "small",
            "medium",
            "large",
            "chopped",
            "diced",
            "minced",
            "fresh",
            "optional",
            "to",
            "taste",
            "and",
            "or",
            "any",
            "half",
            "cooked",
            "ounces",
            "pounds",
            "g",
            "mg",
            "ml",
            "liter",
            "kilogram",
            "gram",
            "liter",
            "tsp",
            "tbsp",
        )

        val allIngredientsKeywords = mutableListOf<String>()

        ingredients.forEach { ingredient ->

            val keywords = regex.findAll(ingredient)
                .map { it.value } //extracts ONLY the captured word group
                .map { it.lowercase() }
                .filter { it.any { char -> char.isLetter() } } // exclude numbers
                .filter { it !in STOP_WORDS }
                .toList()

            allIngredientsKeywords.addAll(keywords)

        }

        allIngredientsKeywords.forEach{ keyword ->
            Log.d("extractIngredientsKeywords ------------ " , keyword)
        }

        return allIngredientsKeywords
    }
}