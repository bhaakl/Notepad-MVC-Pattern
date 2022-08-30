package internlabs.dependencyinjection.notepadmvc.viewer

import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import internlabs.dependencyinjection.notepadmvc.controller.Controller
import internlabs.dependencyinjection.notepadmvc.databinding.ActivityViewerBinding
import internlabs.dependencyinjection.notepadmvc.util.TextUndoRedo
import java.util.*


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
        //in landscape mode hide bottomAppBar
        binding.bottomAppBar.isVisible =
            resources.configuration.orientation != Configuration.ORIENTATION_LANDSCAPE
        initListeners()
    }

    private fun initListeners() = with(binding) {
        imageMenu.setNavigationOnClickListener {
            drawerLayout.open()
            editText.onEditorAction(EditorInfo.IME_ACTION_DONE)
        }

        navigationView.setNavigationItemSelectedListener(controller)

        bottomAppBar.setOnMenuItemClickListener(controller)

        undoRedoManager = TextUndoRedo(binding.editText)
        undoRedoManager.setMaxHistorySize(1000)
    }

    override fun onDestroy() {
        super.onDestroy()

        undoRedoManager.clearHistory()
        undoRedoManager.disconnect()
        close()
    }

    /**
     * setSelection: переносит курсор в конец строки у edit text
     */
    fun setTextFromFile(string: String) {
        getEditText().setText(string)
        getEditText().setSelection(getEditText().text.length)
    }

    /**
     * setTextForEditor(): На  вход подается текст, который нужно поместить в ЕдитТекст. Задача этой функции
     * поместить этот текст: определяется позиция для вставки текста, т.е где курсор, туда и вставляется
     * обновляя при этом курсор в конец вставляемого текста
     */
    fun setTextForEditor(strAdd: String) {
        if (strAdd.isEmpty() || !binding.editText.isEnabled) { // нельзя выставить пока документ не создан
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
        // убирает клавиатуру
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