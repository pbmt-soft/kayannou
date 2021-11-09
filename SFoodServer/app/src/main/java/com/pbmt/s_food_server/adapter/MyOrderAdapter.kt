package com.pbmt.s_food_server.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pbmt.s_food_server.R
import com.pbmt.s_food_server.common.Common
import com.pbmt.s_food_server.model.OrderModel
import java.text.SimpleDateFormat
import java.util.*

class MyOrderAdapter (internal  var context: Context,
                      internal var orderList:List<OrderModel>)
    : RecyclerView.Adapter<MyOrderAdapter.MyViewHolder>()   {

    internal var simpleDateFormat: SimpleDateFormat

    init {
        simpleDateFormat= SimpleDateFormat("dd-MM-yyyy HH:mm:ss")

    }

    inner  class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        internal var txt_order_date: TextView?=null
        internal var txt_order_status: TextView?=null
        internal var txt_order_number: TextView?=null
        internal var txt_order_name: TextView?=null
        internal var txt_order_nb_item: TextView?=null
        internal  var order_img: ImageView?=null

        init {
            order_img = itemView.findViewById(R.id.order_img) as ImageView
            txt_order_date=itemView.findViewById(R.id.txt_order_date) as TextView
            txt_order_status=itemView.findViewById(R.id.txt_order_status) as TextView
            txt_order_number=itemView.findViewById(R.id.txt_order_number) as TextView
            txt_order_name=itemView.findViewById(R.id.txt_order_name) as TextView
            txt_order_nb_item=itemView.findViewById(R.id.txt_order_nb_item) as TextView

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_order_item,parent,false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Glide.with(context).load(orderList[position].cartItemList!![0].foodImage).into(holder.order_img!!)
       // holder.txt_order_number!!.text=StringBuilder("Order Number: ").append(orderList[position].key)
        holder.txt_order_name!!.text=StringBuilder("Comment: ").append(orderList[position].userName)
        holder.txt_order_status!!.text=StringBuilder("Status: ").append(Common.convertStatusToText(orderList[position].orderStatus))

    }

    override fun getItemCount(): Int {
        return orderList.size
    }
}