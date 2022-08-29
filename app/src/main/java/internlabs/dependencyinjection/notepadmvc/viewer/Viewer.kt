package internlabs.dependencyinjection.notepadmvc.viewer

import android.os.Bundle
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import internlabs.dependencyinjection.notepadmvc.R
import internlabs.dependencyinjection.notepadmvc.controller.Controller
import internlabs.dependencyinjection.notepadmvc.databinding.ActivityViewerBinding
import kotlin.system.exitProcess

class Viewer : AppCompatActivity() {

    private lateinit var binding: ActivityViewerBinding
    private var controller: Controller
    private var isOpenFab = false

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
            binding.editText.onEditorAction(EditorInfo.IME_ACTION_DONE)
        }

        navigationView.setNavigationItemSelectedListener(controller)
        fab.setOnClickListener(controller)
        bolt.setOnClickListener (controller)
        italic.setOnClickListener (controller)
        underline.setOnClickListener (controller)
        alignLeft.setOnClickListener (controller)
        alignCenter.setOnClickListener (controller)
        alignRight.setOnClickListener (controller)
        controller.size()

    }

    fun toastCopied() {
        Toast.makeText(this, "Copied", Toast.LENGTH_SHORT).show()
    }

    fun toastPasted() {
        Toast.makeText(this, "Pasted", Toast.LENGTH_SHORT).show()
    }

    fun toastCut() {
        Toast.makeText(this, "Cut Out", Toast.LENGTH_SHORT).show()
    }

    /**
     * @param setSelection  переносит курсор в конец строки у edit text
     */

    fun setTextFromFile(string: String) {
        binding.editText.setText(string)
        binding.editText.setSelection(binding.editText.text.length)
    }

    fun setText(strAdd: String) {
        if (strAdd.isEmpty()) {
            return
        }
        val old = binding.editText.text.toString()
        val cursor: Int = binding.editText.selectionStart
        val leftStr = old.substring(0, cursor)
        val rightStr = old.substring(cursor)
        if (binding.editText.text.isEmpty())
            binding.editText.setText(strAdd)
        else
            binding.editText.setText(String.format("%s%s%s", leftStr, strAdd, rightStr))
        binding.editText.setSelection(cursor + strAdd.length)
    }

    fun getEditText(): EditText {
        return binding.editText
    }

    fun getDrawerLayout(): DrawerLayout {
        return binding.drawerLayout
    }

    fun keyBoardShow() {
        // убирает клавиатуру
        binding.editText.onEditorAction(EditorInfo.IME_ACTION_DONE)
    }

    fun animateFab() = with(binding) {
        if (isOpenFab) {
            fab.startAnimation(AnimationUtils.loadAnimation(this@Viewer, R.anim.rotat_forward))
            alignCenter.startAnimation(AnimationUtils.loadAnimation(this@Viewer,R.anim.fab_close))
            alignRight.startAnimation(AnimationUtils.loadAnimation(this@Viewer,R.anim.fab_close))
            alignLeft.startAnimation(AnimationUtils.loadAnimation(this@Viewer,R.anim.fab_close))
            bolt.startAnimation(AnimationUtils.loadAnimation(this@Viewer, R.anim.fab_close))
            italic.startAnimation(AnimationUtils.loadAnimation(this@Viewer, R.anim.fab_close))
            underline.startAnimation(AnimationUtils.loadAnimation(this@Viewer, R.anim.fab_close))
            bolt.isClickable = false
            italic.isClickable = false
            underline.isClickable = false
            alignCenter.isClickable = false
            alignRight.isClickable = false
            alignLeft.isClickable = false
            isOpenFab = false
        } else {
            fab.startAnimation(AnimationUtils.loadAnimation(this@Viewer, R.anim.rotat_backforward))
            alignCenter.startAnimation(AnimationUtils.loadAnimation(this@Viewer, R.anim.fab_open))
            alignLeft.startAnimation(AnimationUtils.loadAnimation(this@Viewer, R.anim.fab_open))
            alignRight.startAnimation(AnimationUtils.loadAnimation(this@Viewer, R.anim.fab_open))
            bolt.startAnimation(AnimationUtils.loadAnimation(this@Viewer, R.anim.fab_open))
            italic.startAnimation(AnimationUtils.loadAnimation(this@Viewer, R.anim.fab_open))
            underline.startAnimation(AnimationUtils.loadAnimation(this@Viewer, R.anim.fab_open))
            bolt.isClickable = true
            italic.isClickable = true
            underline.isClickable = true
            alignCenter.isClickable = true
            alignLeft.isClickable = true
            alignRight.isClickable = true
            isOpenFab = true
        }

    }

    fun close() {
        binding.drawerLayout.close()
    }

}