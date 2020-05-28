package com.component.firebaseauthdemo.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.component.firebaseauthdemo.R
import com.component.firebaseauthdemo.util.logout
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {
    private lateinit var myNavController : NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setSupportActionBar(home_toolbar)

        myNavController = Navigation.findNavController(this, R.id.fragment) // pass id of Nav HOst Fragment

        NavigationUI.setupWithNavController(nav_view, myNavController)

        //To CHnage toolbar title on chnage of fragment use the following
        NavigationUI.setupActionBarWithNavController(
            this,
            myNavController,
            navigation_drawer_id_from_layout
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(myNavController,navigation_drawer_id_from_layout)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_menu,menu)
        return true

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.action_logout){
            AlertDialog.Builder(this)
                .apply {
                    setTitle("Are you sure to Logout?")
                    setPositiveButton("Yes"){_,_ ->
                        FirebaseAuth.getInstance().signOut()
                        logout()
                    }
                    setNegativeButton("Cancel"){_,_ ->

                    }
                    setCancelable(false)
                }.create().show()

        }

        return super.onOptionsItemSelected(item)
    }

}
