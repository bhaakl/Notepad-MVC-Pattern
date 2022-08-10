package com.example.notepadmvc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.notepadmvc.databinding.ActivityMainBinding

class Viewer : AppCompatActivity() {
    private val controller = Controller(this)

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val drawerLayout: DrawerLayout = (binding.drawerLayout)



        setContentView(binding.root)

        binding.imageMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)


        }


    }
}