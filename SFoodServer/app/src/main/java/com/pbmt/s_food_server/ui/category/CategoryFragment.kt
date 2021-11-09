package com.pbmt.s_food_server.ui.category

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.pbmt.s_food_server.R
import com.pbmt.s_food_server.adapter.MyCategoriesAdapter
import com.pbmt.s_food_server.callback.IMyButtonCallBack
import com.pbmt.s_food_server.common.Common
import com.pbmt.s_food_server.common.MySwipeHelper

import com.pbmt.s_food_server.eventbus.ToastEvent
import com.pbmt.s_food_server.model.CategoryModel
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.layout_update_category.*
import org.greenrobot.eventbus.EventBus
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class CategoryFragment : Fragment() {

    private lateinit var categoryViewModel: CategoryViewModel


    private lateinit var dialog: AlertDialog
    private lateinit var layoutAnimationController: LayoutAnimationController
    private var adapter: MyCategoriesAdapter?=null
    private var recycler_menu: RecyclerView?=null

    internal  var categoryModels: List<CategoryModel> =ArrayList<CategoryModel>()
    internal  lateinit var storage:FirebaseStorage
    internal lateinit var storageReference: StorageReference
    private var imageuri: Uri? =null
    private var category_image:ImageView?=null

    private val PICK_IMAGE_REQUEST: Int=1234

    // This property is only valid between onCreateView and
    // onDestroyView.


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        categoryViewModel =
            ViewModelProvider(this).get(CategoryViewModel::class.java)

            val root=inflater.inflate(R.layout.fragment_category, container, false)
        initViews(root)
        categoryViewModel.getMessageError().observe(viewLifecycleOwner, Observer {s->
            Toast.makeText(context,""+s, Toast.LENGTH_SHORT).show()
        })
        categoryViewModel.getCategoryList().observe(viewLifecycleOwner, Observer {
            dialog.dismiss()
            categoryModels=it
            adapter= MyCategoriesAdapter(requireContext(),categoryModels)
            recycler_menu!!.adapter=adapter
            recycler_menu!!.layoutAnimation=layoutAnimationController
        })
        return root
    }

    private fun initViews(root:View) {
        storage= FirebaseStorage.getInstance()
        storageReference=storage.reference
        dialog= SpotsDialog.Builder().setContext(context).setCancelable(false).build()
        dialog.show()
        layoutAnimationController= AnimationUtils.loadLayoutAnimation(context,R.anim.layout_item_from_left)
        recycler_menu=root.findViewById(R.id.recycler_menu)
        recycler_menu!!.setHasFixedSize(true)
        val layoutManager= LinearLayoutManager(context)
        recycler_menu!!.layoutManager=layoutManager
        recycler_menu!!.addItemDecoration(DividerItemDecoration(context,layoutManager.orientation))

        val swipe= object : MySwipeHelper(requireContext(), recycler_menu!!,200){
            override fun instantiateMyButton(viewHolder: RecyclerView.ViewHolder, buffer: MutableList<MyButton>) {
                buffer.add(MyButton(context!!,
                    "Update",
                    30,
                    0,
                    Color.parseColor("#560027"),
                    object: IMyButtonCallBack {
                        override fun onMyButtonClick(pos: Int) {
                            Common.categorySelected=categoryModels[pos]
                            showUpdateDialog()
                        }
                    }))
            }
        }
    }

    private fun showUpdateDialog() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        builder.setTitle("Update Category")
        builder.setMessage("Please fill information")

        val itemView= LayoutInflater.from(context).inflate(R.layout.layout_update_category,null)
        val edt_category_name=itemView.findViewById<View>(R.id.edt_category_name) as EditText
        category_image=itemView.findViewById<View>(R.id.img_category) as ImageView

        edt_category_name.setText(Common.categorySelected!!.name)
        Glide.with(requireContext()).load(Common.categorySelected!!.image).into(category_image!!)


        category_image!!.setOnClickListener { view->
            val intent= Intent()
            intent.type="image/*"
            intent.action=Intent.ACTION_GET_CONTENT

            startActivityForResult(Intent.createChooser(intent,"Select Picture"),PICK_IMAGE_REQUEST)
        }

        builder.setNegativeButton("CANCEL"){dialogInterface,_->
            dialogInterface.dismiss()
        }
        builder.setPositiveButton("UPDATE"){dialogInterface,_->
            val updateData=HashMap<String,Any>()
            updateData["name"]=edt_category_name.text.toString()
            if (imageuri != null){
                dialog.setMessage("Uploading...")
                dialog.show()

                val imageName=UUID.randomUUID().toString()
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
                        dialog.dismiss()
                        imageFolder.downloadUrl.addOnSuccessListener { uri->
                            updateData["image"]=uri.toString()
                            updateCategory(updateData)
                        }

                    }
            }else{
                updateCategory(updateData)
            }
        }

        builder.setView(itemView)
        val updateDialog=builder.create()
        updateDialog.show()

    }

    private fun updateCategory(updateData: HashMap<String, Any>) {
FirebaseDatabase.getInstance().getReference(Common.CATEGORY_REF)
    .child(Common.categorySelected!!.menu_id!!).updateChildren(updateData)
    .addOnFailureListener { e->
        Toast.makeText(context,""+e.message,Toast.LENGTH_SHORT).show()
    }
    .addOnCompleteListener { task->
        if(task.isSuccessful){
            categoryViewModel.loadCategoryList()
            EventBus.getDefault().postSticky(ToastEvent(true,false))
        }
    }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==PICK_IMAGE_REQUEST && resultCode== Activity.RESULT_OK){
            if(data !=null && data.data != null){
                imageuri=data.data
                category_image!!.setImageURI(imageuri)
            }
        }
    }
}