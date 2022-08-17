package internlabs.dependencyinjection.notepadmvc.viewer

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import internlabs.dependencyinjection.notepadmvc.R
import internlabs.dependencyinjection.notepadmvc.controller.Controller
import internlabs.dependencyinjection.notepadmvc.databinding.ActivityViewerBinding

class Viewer : AppCompatActivity() {
    private lateinit var binding: ActivityViewerBinding
    private var controller: Controller

    init {
        controller = Controller(viewer = this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initListeners()
    }

    private fun initListeners() = with(binding) {
        imageMenu.setNavigationOnClickListener {
            drawerLayout.open()
        }

        navigationView.setNavigationItemSelectedListener(controller)

        /*navigationView.setNavigationItemSelectedListener { menuItem ->
            // Handle menu item selected
            //menuItem.isChecked = true
            drawerLayout.close()
            true
        }*/
    }
    fun close(){
        binding.drawerLayout.close()
    }

    fun setText(string: String) {
    }

}