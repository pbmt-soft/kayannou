package com.pbmt.s_food_server.common

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.widget.TextView
import com.pbmt.s_food_server.model.CategoryModel
import com.pbmt.s_food_server.model.FoodModel
import com.pbmt.s_food_server.model.ServerUserModel

object Common {
    const val CATEGORY_REF: String="Category"
    const val SERVER_REFERENCE: String="Server"

    const val FULL_WIDTH_COLUMN: Int=1
    const val DEFAULT_COLUMN_COUNT: Int=0


    var currentServerUser: ServerUserModel? = null
    var foodSelected: FoodModel? =null
    var categorySelected: CategoryModel? =null

    fun setSpanString(welcome: String, name: String, txtUser: TextView?) {
        val builder= SpannableStringBuilder()
        builder.append(welcome)
        val txtSpannable= SpannableString(name)
        val boldSpan = StyleSpan(Typeface.BOLD)
        txtSpannable.setSpan(boldSpan,0,name.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        builder.append(txtSpannable)
        txtUser!!.setText(builder, TextView.BufferType.SPANNABLE)

    }

    fun convertStatusToText(orderStatus: Int): String {

        when(orderStatus) {
            0 -> return "Placed"
            1 -> return "Shipping"
            2 -> return "Shipped"
            -1 -> return "Cancelled"

            else->
                return "Unk"
        }
    }
}