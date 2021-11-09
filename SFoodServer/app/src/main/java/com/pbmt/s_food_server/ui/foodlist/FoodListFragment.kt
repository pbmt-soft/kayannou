package com.pbmt.s_food_server.ui.foodlist

import android.app.Activity
import android.app.AlertDialog
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.*
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.pbmt.s_food_server.R
import com.pbmt.s_food_server.SizeAddonEditActivity
import com.pbmt.s_food_server.adapter.MyFoodListAdapter
import com.pbmt.s_food_server.callback.IMyButtonCallBack
import com.pbmt.s_food_server.common.Common
import com.pbmt.s_food_server.common.MySwipeHelper

import com.pbmt.s_food_server.eventbus.AddonSizeEditEvent
import com.pbmt.s_food_server.eventbus.ChangeMenuClick
import com.pbmt.s_food_server.eventbus.ToastEvent
import com.pbmt.s_food_server.model.FoodModel
import dmax.dialog.SpotsDialog
import org.greenrobot.eventbus.EventBus
import java.lang.StringBuilder
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class FoodListFragment : Fragment() {

    private lateinit var foodlistViewModel: FoodListViewModel


    private lateinit var dialog: AlertDialog
    private lateinit var layoutAnimationController: LayoutAnimationController
    private var adapter: MyFoodListAdapter?=null
    private var recycler_food_list: RecyclerView?=null
    var foodModelList:List<FoodModel> = ArrayList<FoodModel>()

    internal  lateinit var storage: FirebaseStorage
    internal lateinit var storageReference: StorageReference
    private var imageuri: Uri? =null
    private var food_image: ImageView?=null

    private val PICK_IMAGE_REQUEST: Int=1234

    // This property is only valid between onCreateView and
    // onDestroyView.


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        foodlistViewModel =
            ViewModelProvider(this).get(FoodListViewModel::class.java)

        val root=inflater.inflate(R.layout.fragment_food_list, container, false)

        initViews(root)
        foodlistViewModel.getFoodMutableListaData().observe(viewLifecycleOwner, Observer {
            if(it !=null){
                dialog.dismiss()
                foodModelList= it
                adapter= MyFoodListAdapter(requireContext(),foodModelList)
                recycler_food_list!!.adapter=adapter
                recycler_food_list!!.layoutAnimation=layoutAnimationController
            }

        })
        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.food_list_menu,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_search) {
            val searchManager=requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager
            val searchView = item.actionView  as SearchView
            searchView.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().componentName))
            searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(search: String): Boolean {
                    startSearchFood(search)
                    return true
                }

                override fun onQueryTextChange(p0: String): Boolean {
                    return false
                }

            })

            val closeButton= searchView.findViewById<View>(R.id.search_close_btn) as ImageView
            closeButton.setOnClickListener {
                val ed=searchView.findViewById<View>(R.id.search_src_text) as EditText
                ed.setText("")

                searchView.setQuery("",false)
                searchView.onActionViewCollapsed()
                item.collapseActionView()
                foodlistViewModel.getFoodMutableListaData().value=Common.categorySelected!!.foods!!
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.action_search).setVisible(true)
        super.onPrepareOptionsMenu(menu)
    }

    private fun startSearchFood(search: String) {
        val resultFood:MutableList<FoodModel> =ArrayList()
        for (i in Common.categorySelected!!.foods!!.indices){
            val foodModel=Common.categorySelected!!.foods!![i]
            if (foodModel.name!!.toLowerCase(Locale.ROOT).contains(search.toLowerCase(Locale.ROOT))) {
                foodModel.positionInList= i
                resultFood.add(foodModel)
            }
        }
        foodlistViewModel.getFoodMutableListaData().value=resultFood
    }

    private fun initViews(root: View?) {

        setHasOptionsMenu(true)
        storage= FirebaseStorage.getInstance()
        storageReference=storage.reference
        dialog= SpotsDialog.Builder().setContext(context).setCancelable(false).build()
        dialog.show()
        layoutAnimationController= AnimationUtils.loadLayoutAnimation(context,R.anim.layout_item_from_left)
        recycler_food_list=root!!.findViewById(R.id.recycler_food_list)
        recycler_food_list!!.setHasFixedSize(true)
        recycler_food_list!!.layoutManager= LinearLayoutManager(context)

        (activity as AppCompatActivity).supportActionBar!!.title= Common.categorySelected!!.name

        val displayMetrics=DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width=displayMetrics.widthPixels

        val swipe= object : MySwipeHelper(requireContext(), recycler_food_list!!,width/6){
            override fun instantiateMyButton(viewHolder: RecyclerView.ViewHolder, buffer: MutableList<MyButton>) {
                buffer.add(MyButton(context!!,
                    "Update",
                    30,
                    0,
                    Color.parseColor("#560027"),
                    object: IMyButtonCallBack {
                        override fun onMyButtonClick(pos: Int) {
                            val foodModel=adapter!!.getItemAtPosition(pos)
                            if (foodModel.positionInList == -1)
                                showUpdateDialog(pos,foodModel)
                            else{
                                showUpdateDialog(foodModel.positionInList,foodModel)
                            }
                        }
                    }))
                buffer.add(MyButton(context!!,
                    "Delete",
                    30,
                    0,
                    Color.parseColor("#9b0000"),
                    object: IMyButtonCallBack {
                        override fun onMyButtonClick(pos: Int) {
                            Common.foodSelected=foodModelList[pos]
                            val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
                            builder.setTitle("Delete")
                            builder.setMessage("Do you really want to delete food?")
                            builder.setNegativeButton("CANCEL"){dialogInterface,_->
                                dialogInterface.dismiss()
                            }
                                .setPositiveButton("DELETE"){ dialogInterface,i->
                                    val foodModel=adapter!!.getItemAtPosition(pos)
                                    if (foodModel.positionInList == -1)
                                        Common.categorySelected!!.foods!!.removeAt(pos)
                                    else{
                                        Common.categorySelected!!.foods!!.removeAt(foodModel.positionInList)
                                    }
                                    updateFood(Common.categorySelected!!.foods!!,true)

                                }

                            val deleteDialog=builder.create()
                            deleteDialog.show()
                        }
                    }))
                buffer.add(MyButton(context!!,
                    "Size",
                    30,
                    0,
                    Color.parseColor("#12005e"),
                    object: IMyButtonCallBack {
                        override fun onMyButtonClick(pos: Int) {
                            val foodModel=adapter!!.getItemAtPosition(pos)
                            if (foodModel.positionInList == -1)
                                Common.foodSelected=foodModelList!![pos]
                            else{
                                Common.foodSelected=foodModel
                            }
                            startActivity(Intent(context,SizeAddonEditActivity::class.java))
                            if (foodModel.positionInList == -1)
                                EventBus.getDefault().postSticky(AddonSizeEditEvent(false,pos))
                            else{
                                EventBus.getDefault().postSticky(AddonSizeEditEvent(false,foodModel.positionInList))
                            }
                        }
                    }))
                buffer.add(MyButton(context!!,
                    "AddOn",
                    30,
                    0,
                    Color.parseColor("#333639"),
                    object: IMyButtonCallBack {
                        override fun onMyButtonClick(pos: Int) {
                            val foodModel=adapter!!.getItemAtPosition(pos)
                            if (foodModel.positionInList == -1)
                                Common.foodSelected=foodModelList!![pos]
                            else{
                                Common.foodSelected=foodModel
                            }
                            startActivity(Intent(context,SizeAddonEditActivity::class.java))
                            if (foodModel.positionInList == -1)
                                EventBus.getDefault().postSticky(AddonSizeEditEvent(true,pos))
                            else{
                                EventBus.getDefault().postSticky(AddonSizeEditEvent(true,foodModel.positionInList))
                            }
                        }
                    }))
            }
        }
    }

    private fun showUpdateDialog(pos: Int,foodModel: FoodModel) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        builder.setTitle("Update Food List")
        builder.setMessage("Please fill information")

        val itemView= LayoutInflater.from(context).inflate(R.layout.layout_update_food,null)
        val edt_food_name=itemView.findViewById<View>(R.id.edt_food_name) as EditText
        val edt_food_price=itemView.findViewById<View>(R.id.edt_food_price) as EditText
        val edt_food_description=itemView.findViewById<View>(R.id.edt_food_description) as EditText
        food_image=itemView.findViewById<View>(R.id.img_food_list) as ImageView

        edt_food_name.setText(StringBuilder("").append(foodModel.name))
        edt_food_price.setText(StringBuilder("").append(foodModel.price))
        edt_food_description.setText(StringBuilder("").append(foodModel.description))
        Glide.with(requireContext()).load(foodModel.image).into(food_image!!)

        food_image!!.setOnClickListener { view->
            val intent= Intent()
            intent.type="image/*"
            intent.action= Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent,"Select Picture"),PICK_IMAGE_REQUEST)
        }

        builder.setNegativeButton("CANCEL"){dialogInterface,_->
            dialogInterface.dismiss()
        }
        builder.setPositiveButton("UPDATE"){dialogInterface,_->
            val updateFood=foodModel
            updateFood.name=edt_food_name.text.toString()
            updateFood.price=if(TextUtils.isEmpty(edt_food_price.text)){
                0
            }else{
                edt_food_price.text.toString().toLong()
            }
            updateFood.description=edt_food_description.text.toString()
            if (imageuri != null){
                dialog.setMessage("Uploading...")
                dialog.show()

                val imageName= UUID.randomUUID().toString()
                val imageFolder=storageReference.child("images/$imageName")
                imageFolder.putFile(imageuri!!)
                    .addOnFailureListener{e->
                        dialog.dismiss()
                        Toast.makeText(context,""+e.message,Toast.LENGTH_SHORT).show()
                    }
                    .addOnProgressListener{taskSnapshot->
                        val progress=100.0 *taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
                        dialog.setMessage("Uploading $progress%")
                    }
                    .addOnSuccessListener { taskSnapshot->
                        dialogInterface.dismiss()
                        imageFolder.downloadUrl.addOnSuccessListener { uri->
                            dialog.dismiss()
                            updateFood.image=uri.toString()
                            Common.categorySelected!!.foods!![pos]=updateFood
                            updateFood(Common.categorySelected!!.foods!!,false)
                        }

                    }
            }else{
                Common.categorySelected!!.foods!![pos]=updateFood
                updateFood(Common.categorySelected!!.foods!!,false)

            }
        }

        builder.setView(itemView)
        val updateDialog=builder.create()
        updateDialog.show()

    }

    private fun updateFood(foods: MutableList<FoodModel>?,isDelete: Boolean) {
        val updateData=HashMap<String,Any>()
        updateData["foods"]= foods!!
        FirebaseDatabase.getInstance().getReference(Common.CATEGORY_REF)
            .child(Common.categorySelected!!.menu_id!!).updateChildren(updateData)
            .addOnFailureListener { e->
                Toast.makeText(context,""+e.message, Toast.LENGTH_SHORT).show()
            }
            .addOnCompleteListener { task->
                if(task.isSuccessful){
                    foodlistViewModel.getFoodMutableListaData()
                    EventBus.getDefault().postSticky(ToastEvent(!isDelete,true))
                }
            }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==PICK_IMAGE_REQUEST && resultCode== Activity.RESULT_OK){
            if(data !=null && data.data != null){
                imageuri=data.data
                food_image!!.setImageURI(imageuri)
            }
        }
    }

    override fun onDestroy() {
        EventBus.getDefault().postSticky(ChangeMenuClick(true))
        super.onDestroy()

    }


}