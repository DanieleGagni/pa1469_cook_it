package com.example.cookit.screens.createRecipe

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.navigation.NavHostController
import com.example.cookit.screens.components.Recipe
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson

class CreateRecipeViewModel(application: Application) : AndroidViewModel(application) {

    private val db = Firebase.firestore
    private val recipesCollection = db.collection("recipes")

    // extract keywords from recipe title
    private fun extractTitleKeywords(title: String): List<String> {

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
    private fun extractIngredientsKeywords(ingredients: List<String>): MutableList<String> {

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

    fun Recipe.withTitleKeywords(titleKeywords: List<String>): Recipe {
        return this.copy(title_keywords = titleKeywords)
    }

    fun Recipe.withIngredientsKeywords(ingredientsKeywords: List<String>): Recipe {
        return this.copy(ingredients_keywords = ingredientsKeywords)
    }

    fun addRecipe(navHostController: NavHostController, recipe: Recipe) {

        val id = recipesCollection.document().id // Generates a random document ID
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

        if (currentUserId == null) {
            Log.e("[ERROR]", "User is not authenticated.")
            return
        }

        Log.d("viewModel -------- ", "${recipe.id}, ${recipe.title}")

        val updatedRecipe = recipe
            .copy(
                id = id,
                createdBy = currentUserId
            )
            .withTitleKeywords(extractTitleKeywords(recipe.title))
            .withIngredientsKeywords(extractIngredientsKeywords(recipe.ingredients))

        recipesCollection
            .document(id)
            .set(updatedRecipe)
            .addOnSuccessListener {
                Log.d("[DEBUG]", "Recipe '${updatedRecipe.title}' added successfully with ID: $id")
                //Log.d("[------------------------- DEBUG]", "Recipe '${updatedRecipe.title}' added successfully.")
                //Log.d("[------------------------- DEBUG]", "Recipe added with ID: ${documentReference.id}")

                // upon creation, go to RecipeScreen
                val recipeJson = Uri.encode(Gson().toJson(recipe))
                navHostController.navigate("showRecipe/$recipeJson")

            }
            .addOnFailureListener { e ->
                Log.e(
                    "[------------------------- DEBUG]",
                    "Error adding recipe '${updatedRecipe.title}': ${e.message}"
                )
            }
    }
}



