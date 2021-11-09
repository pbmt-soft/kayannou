package com.pbmt.s_food_server.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pbmt.s_food_server.R
import com.pbmt.s_food_server.callback.IRecyclerItemClickListener
import com.pbmt.s_food_server.common.Common
import com.pbmt.s_food_server.eventbus.FoodItemClick
import com.pbmt.s_food_server.model.FoodModel
import org.greenrobot.eventbus.EventBus

class MyFoodListAdapter  (internal  var context: Context,
                          internal var foodList:List<FoodModel>)
    : RecyclerView.Adapter<MyFoodListAdapter.MyViewHolder>() {

    inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), View.OnClickListener {

        var txt_food_name: TextView?=null
        var txt_food_price: TextView?=null
        var food_img: ImageView?=null


        internal var listener: IRecyclerItemClickListener?=null

        fun setListener(listener: IRecyclerItemClickListener){
            this.listener=listener
        }

        init {
            txt_food_name=itemView.findViewById(R.id.txt_food_name)
            txt_food_price=itemView.findViewById(R.id.txt_food_price)
            food_img=itemView.findViewById(R.id.img_food)
            itemView.setOnClickListener(this)

        }

        override fun onClick(view: View?) {
            listener!!.onItemClick(view!!,adapterPosition)
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_food_item,parent,false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Glide.with(context).load(foodList[position].image).into(holder.food_img!!)
        holder.txt_food_name!!.text = foodList[position].name
        holder.txt_food_price!!.text = StringBuilder("$").append(foodList[position].price.toString())

        //event
        holder.setListener(object :IRecyclerItemClickListener{
            override fun onItemClick(view: View, pos: Int) {
                Common.foodSelected= foodList[pos]
                Common.foodSelected!!.key =pos.toString()
            }
        })
    }

    override fun getItemCount(): Int {
        return foodList.size
    }

    fun getItemAtPosition(pos: Int): FoodModel {
        return foodList.get(pos)
    }

}