package internlabs.dependencyinjection.notepadmvc.viewer

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import internlabs.dependencyinjection.notepadmvc.controller.Controller
import internlabs.dependencyinjection.notepadmvc.databinding.ActivityViewerBinding

class Viewer:  AppCompatActivity() {
    private lateinit var binding: ActivityViewerBinding
    private var controller: Controller
    lateinit var alertDialog: AlertDialog.Builder

    init {
        controller = Controller(viewer = this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initListeners()
    }

    fun showAlertDialog(){
        alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("AboutApp")
            .setMessage("Project developers:Notepad MVC pattern\n" +
                    "Team: Dependency injection\n" +
                    "Медербек Шермаматов\n" +
                    "Умут Арпидинов\n" +
                    "Атабек Шамшидинов\n" +
                    "Байыш Бегалиев\n" +
                    "Мурат Жумалиев")
            .setCancelable(true)
            .setPositiveButton("Ok"){ dialogInterface, _ ->
                dialogInterface.cancel()
            }
        alertDialog.show()
    }

    private fun initListeners() = with(binding) {
        imageMenu.setNavigationOnClickListener {
            drawerLayout.open()
            binding.etMainField.onEditorAction(EditorInfo.IME_ACTION_DONE)

        }
        navigationView.setNavigationItemSelectedListener (controller)
    }

    fun close(){
        binding.drawerLayout.close()
    }
    /**
     * @param setSelection  переносит курсор в конец строки у edit text
     */
    fun setText(string: String) {
        binding.etMainField.setText(string)
        binding.etMainField.setSelection(binding.etMainField.text.length)
    }

    fun getText(): String {
        return binding.etMainField.text.toString()
    }

    fun keyBoardShow() {
        // убирает клавиатуру
        binding.etMainField.onEditorAction(EditorInfo.IME_ACTION_DONE)
    }
}