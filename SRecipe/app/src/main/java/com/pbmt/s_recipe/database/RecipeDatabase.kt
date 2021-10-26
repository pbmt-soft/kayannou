package com.pbmt.s_recipe.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.pbmt.s_recipe.dao.RecipeDAO
import com.pbmt.s_recipe.entities.*
import com.pbmt.s_recipe.entities.converter.CategoryListConverter
import com.pbmt.s_recipe.entities.converter.MealListConverter

@Database(entities = [Recipes::class, CategoryItems::class, Category::class, Meal::class, MealsItems::class],version=1,exportSchema = false)
@TypeConverters(CategoryListConverter::class, MealListConverter::class)
abstract class RecipeDatabase : RoomDatabase(){

    companion object{
        var recipesDatabase:RecipeDatabase? = null

        @Synchronized
        fun getDatabase(context: Context): RecipeDatabase{
            if (recipesDatabase ==null){
                recipesDatabase=Room.databaseBuilder(
                    context,
                    RecipeDatabase::class.java,
                    "recipe.db"
                ).build()
            }
            return recipesDatabase!!
        }
    }
    abstract fun recipeDao():RecipeDAO
}