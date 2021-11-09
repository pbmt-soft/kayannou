package com.pbmt.s_food_server

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.pbmt.s_food_server.common.Common
import com.pbmt.s_food_server.eventbus.CategoryClick
import com.pbmt.s_food_server.eventbus.ChangeMenuClick
import com.pbmt.s_food_server.eventbus.ToastEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class HomeActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var drawer: DrawerLayout
    private lateinit var navView: NavigationView

    private var menuClick=-1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_home)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawer = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
         navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_category,R.id.nav_order
            ), drawer
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        var headerView = navView.getHeaderView(0)
        var txt_user=headerView.findViewById<TextView>(R.id.txt_user)
        Common.setSpanString("Hey, ",Common.currentServerUser!!.name!!, txt_user)

        navView.setNavigationItemSelectedListener { item ->
            item.isChecked = true
            drawer.closeDrawers()
            if (item.itemId == R.id.nav_sign_out) {
                signOut()
            } else if (item.itemId == R.id.nav_category) {
                if(menuClick != item.itemId)
                    navController.navigate(R.id.nav_category)
            }
            else if (item.itemId == R.id.nav_order) {
                if(menuClick != item.itemId)
                    navController.navigate(R.id.nav_order)
            }
            menuClick=item.itemId
            true
        }
    }
    private fun signOut() {
        val builder= androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Sign out")
            .setMessage("Do you really want to exit?")
            .setNegativeButton("CANCEL"){dialogInterface, _->
                dialogInterface.dismiss()
            }
            .setPositiveButton("OK"){dialogInterface, _->
                Common.foodSelected = null
                Common.categorySelected = null
                Common.currentServerUser = null

                FirebaseAuth.getInstance().signOut()

                val intent= Intent(this@HomeActivity,MainActivity::class.java)
                intent.flags= Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        val dialog=builder.create()
        dialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.home, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }
    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    fun onCategorySelected(event: CategoryClick){
        if (event.isSuccess){
            //Toast.makeText(this@HomeActivity,"Click to"+event.category.name,Toast.LENGTH_SHORT).show()
            if (menuClick != R.id.nav_food_list){
                navController.navigate(R.id.nav_food_list)
                menuClick=R.id.nav_food_list
            }
        }
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    fun onChangeMenuEvent(event: ChangeMenuClick){
        if (!event.isFromFoodList){
            //Toast.makeText(this@HomeActivity,"Click to"+event.category.name,Toast.LENGTH_SHORT).show()
            navController.popBackStack(R.id.nav_category,true)
            navController.navigate(R.id.nav_category)
        }
        menuClick = -1
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    fun onToastEvent(event: ToastEvent){
        if (event.isUpdate){
           Toast.makeText(this@HomeActivity,"Update Success",Toast.LENGTH_SHORT).show()

        }else{
            Toast.makeText(this@HomeActivity,"Delete Success",Toast.LENGTH_SHORT).show()
        }
        EventBus.getDefault().postSticky(ChangeMenuClick(event.isBackFromFoodList))
    }
}