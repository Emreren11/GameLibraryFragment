package com.emre.gamelibraryfragment.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.navigation.fragment.NavHostFragment
import com.emre.gamelibraryfragment.R
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.game_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.addGame) {

            val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
            val navController = navHostFragment.navController

            /*
            val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView)

            if (currentFragment is ListFragment) {
                val action = ListFragmentDirections.actionListFragmentToDetailFragment().setInfo("new")
                navController.navigate(action)
            } else {
                Toast.makeText(this, "You are already on the adding page", Toast.LENGTH_LONG).show()
            }

             */
            try {
                val action = ListFragmentDirections.actionListFragmentToDetailFragment().setInfo("new")
                navController.navigate(action)
            } catch (e: Exception) {
                Toast.makeText(this, "You are already on the adding page", Toast.LENGTH_LONG).show()
            }


        }
        return super.onOptionsItemSelected(item)
    }
}