package internlabs.dependencyinjection.notepadmvc.viewer

import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import internlabs.dependencyinjection.notepadmvc.R
import internlabs.dependencyinjection.notepadmvc.controller.Controller
import internlabs.dependencyinjection.notepadmvc.databinding.ActivityViewerBinding

class Viewer : AppCompatActivity() {
    private lateinit var binding: ActivityViewerBinding
    private var controller: Controller
    var isOpenFab = false

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
        fab.setOnClickListener(controller)
        bolt.setOnClickListener{controller.makeBold()}
        italic.setOnClickListener{controller.makeItalic()}
        underline.setOnClickListener{controller.makeUnderlined()}

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

    fun animateFab() = with(binding){
        if (isOpenFab) {
          fab.startAnimation(AnimationUtils.loadAnimation(this@Viewer,R.anim.rotat_forward))
          bolt.startAnimation(AnimationUtils.loadAnimation(this@Viewer,R.anim.fab_close))
          italic.startAnimation(AnimationUtils.loadAnimation(this@Viewer,R.anim.fab_close))
          underline.startAnimation(AnimationUtils.loadAnimation(this@Viewer,R.anim.fab_close))
          bolt.isClickable = false
          italic.isClickable = false
          underline.isClickable = false
          isOpenFab = false
        } else {
            fab.startAnimation(AnimationUtils.loadAnimation(this@Viewer,R.anim.rotat_backforward))
            bolt.startAnimation(AnimationUtils.loadAnimation(this@Viewer,R.anim.fab_open))
            italic.startAnimation(AnimationUtils.loadAnimation(this@Viewer,R.anim.fab_open))
            underline.startAnimation(AnimationUtils.loadAnimation(this@Viewer,R.anim.fab_open))
            bolt.isClickable = true
            italic.isClickable = true
            underline.isClickable = true
            isOpenFab = true
        }

    }

}