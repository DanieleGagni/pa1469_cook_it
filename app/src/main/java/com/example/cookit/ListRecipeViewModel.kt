package com.example.cookit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ListRecipeViewModel : ViewModel() {

    fun searchRecipes(query: String) {
        viewModelScope.launch {
            // Implement search functionality here
            // For example, make a network request or query a database
        }
    }
}