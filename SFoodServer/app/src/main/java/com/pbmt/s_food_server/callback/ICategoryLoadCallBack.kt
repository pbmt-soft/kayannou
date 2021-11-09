package com.pbmt.s_food_server.callback

import com.pbmt.s_food_server.model.CategoryModel

interface ICategoryLoadCallBack {

    fun onCategoryLoadSuccess(categoryModelList:List<CategoryModel>)
    fun onCategoryLoadFailed(message:String)
}