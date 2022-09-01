package internlabs.dependencyinjection.notepadmvc.viewer

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import internlabs.dependencyinjection.notepadmvc.controller.Controller
import internlabs.dependencyinjection.notepadmvc.databinding.ActivityViewerBinding
import internlabs.dependencyinjection.notepadmvc.util.TextUndoRedo


class Viewer : AppCompatActivity() {
    private lateinit var binding: ActivityViewerBinding
    private var controller: Controller
    private lateinit var alertDialog: AlertDialog.Builder

    private lateinit var undoRedoManager: TextUndoRedo

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
            editText.onEditorAction(EditorInfo.IME_ACTION_DONE)
        }

        navigationView.setNavigationItemSelectedListener(controller)

        undoRedoManager = TextUndoRedo(binding.editText)
        undoRedoManager.setMaxHistorySize(100)
    }

    fun setTextFromFile(string: String) {
        getEditText().setText(string)
        getEditText().setSelection(getEditText().text.length)
    }

    fun setTextForEditor(strAdd: String) {
        if (strAdd.isEmpty() || !binding.editText.isEnabled) {
            showToast("нельзя вставить текст пока документ не создан")
            return
        }
        val old = getEditText().text.toString()
        val cursor: Int = getEditText().selectionStart
        val leftStr = old.substring(0, cursor)
        val rightStr = old.substring(cursor)
        if (getEditText().text.isEmpty())
            getEditText().setText(strAdd)
        else
            getEditText().setText(String.format("%s%s%s", leftStr, strAdd, rightStr))
        getEditText().setSelection(cursor + strAdd.length)
    }

    fun showAlertDialog() {
        alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("AboutApp")
            .setMessage(
                "Project developers:Notepad MVC pattern\n" +
                        "Team: Dependency injection\n" +
                        "Медербек Шермаматов\n" +
                        "Умут Арпидинов\n" +
                        "Атабек Шамшидинов\n" +
                        "Байыш Бегалиев\n" +
                        "Мурат Жумалиев"
            )
            .setCancelable(true)
            .setPositiveButton("Ok") { dialogInterface, _ ->
                dialogInterface.cancel()
            }
        alertDialog.show()
    }

    fun getUndoRedoManager(): TextUndoRedo {
        return undoRedoManager
    }

    fun getEditText(): EditText {
        return binding.editText
    }

    fun getDrawerLayout(): DrawerLayout {
        return binding.drawerLayout
    }

    private fun close() {
        binding.drawerLayout.close()
    }

    fun keyBoardShow() {
        getEditText().onEditorAction(EditorInfo.IME_ACTION_DONE)
    }

    fun makeEditTextEditable() {
        binding.editText.isEnabled = true
        binding.editText.isFocusable = true
    }

    fun showToast(s: String) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
    }
}