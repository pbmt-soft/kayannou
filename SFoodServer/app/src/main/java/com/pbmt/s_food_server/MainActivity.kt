package com.pbmt.s_food_server

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.pbmt.s_food_server.common.Common
import com.pbmt.s_food_server.model.ServerUserModel
import dmax.dialog.SpotsDialog
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var  listener: FirebaseAuth.AuthStateListener
    private lateinit var  dialog: AlertDialog
    private lateinit var serverRef: DatabaseReference
    private var providers:List<AuthUI.IdpConfig>? =null

    companion object{
        private val APP_REQUEST_CODE=7171
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        init()
    }

    private fun init(){
        providers= Arrays.asList<AuthUI.IdpConfig>(AuthUI.IdpConfig.PhoneBuilder().build())
        serverRef= FirebaseDatabase.getInstance().getReference(Common.SERVER_REFERENCE)
        mAuth= FirebaseAuth.getInstance()
        dialog= SpotsDialog.Builder().setContext(this).setCancelable(false).build()
        listener=object :FirebaseAuth.AuthStateListener{
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                val user=mAuth.currentUser
                if (user !=null){
                    checkServerUserFromFirebase(user)
                }else{
                    phoneLogin()
                }
            }

        }
    }

    private fun checkServerUserFromFirebase(user: FirebaseUser) {
        dialog.show()
        serverRef.child(user.uid)
            .addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                                dialog.dismiss()
                                val serverUserModel=snapshot.getValue(ServerUserModel::class.java)
                                if (serverUserModel!!.isActive){
                                    gotoHomeActivity(serverUserModel)
                                }else{
                                    dialog.dismiss()
                                    Toast.makeText(this@MainActivity,"You must be allowed from Admin to access this App",Toast.LENGTH_SHORT).show()
                                }
                    }else{
                        dialog.dismiss()
                        showRegisterDialog(user)
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@MainActivity,""+error.message,Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun showRegisterDialog(user: FirebaseUser) {
        val builder=androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("REGISTER")
        builder.setMessage("Please fill information")

        val itemView= LayoutInflater.from(this@MainActivity).inflate(R.layout.layout_register,null)
        val edt_name=itemView.findViewById<EditText>(R.id.edt_name)
        val edt_phone=itemView.findViewById<EditText>(R.id.edt_phone)

        edt_phone.setText(user.phoneNumber)

        builder.setView(itemView)
        builder.setNegativeButton("CANCEL"){ dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        builder.setPositiveButton("REGISTER"){ dialogInterface, _ ->
            if (TextUtils.isDigitsOnly(edt_name.text.toString())){
                Toast.makeText(this@MainActivity,"Please enter your name",Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }
            val serverUserModel=ServerUserModel()
            serverUserModel.uid=user.uid
            serverUserModel.name=edt_name.text.toString()
            serverUserModel.phone=edt_phone.text.toString()
            serverUserModel.isActive=false

            serverRef.child(user.uid).setValue(serverUserModel).addOnCompleteListener { task->
                if (task.isSuccessful){
                    dialogInterface.dismiss()
                    Toast.makeText(this@MainActivity,"Congratulations Register success!, Admin will check and active user soon",Toast.LENGTH_SHORT).show()
                }
            }
        }
        val dialog=builder.create()
        dialog.show()
    }

    private fun gotoHomeActivity(serverUserModel: ServerUserModel?) {
        dialog.dismiss()
        Common.currentServerUser=serverUserModel
        startActivity(Intent(this@MainActivity,HomeActivity::class.java))
        finish()
    }

    override fun onStart() {
        super.onStart()
        mAuth.addAuthStateListener(listener)
    }

    override fun onStop() {
        if (listener !=null)
            mAuth.removeAuthStateListener(listener)
        super.onStop()
    }

    private fun phoneLogin() {
        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers!!).build(), APP_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode== APP_REQUEST_CODE){
            val response= IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK){
                val user=FirebaseAuth.getInstance().currentUser

            }else{
                Toast.makeText(this,"Failed to sign in", Toast.LENGTH_SHORT).show()
            }
        }

    }
}

