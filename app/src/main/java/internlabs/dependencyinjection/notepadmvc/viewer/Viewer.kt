package internlabs.dependencyinjection.notepadmvc.viewer

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import internlabs.dependencyinjection.notepadmvc.controller.Controller
import internlabs.dependencyinjection.notepadmvc.databinding.ActivityViewerBinding


class Viewer : AppCompatActivity() {
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

    private fun initListeners() = with(binding) {
        imageMenu.setNavigationOnClickListener {
            drawerLayout.open()
            binding.editText.onEditorAction(EditorInfo.IME_ACTION_DONE)
        }

        navigationView.setNavigationItemSelectedListener(controller)
    }

    /**
     * @param setSelection  переносит курсор в конец строки у edit text
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
        if (strAdd.isEmpty()) {
            getEditText().setText(strAdd)
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

    fun getEditText(): EditText {
        return binding.editText
    }

    fun getDrawerLayout(): DrawerLayout {
        return binding.drawerLayout
    }

    fun close(){
        binding.drawerLayout.close()
    }

    fun keyBoardShow() {
        // убирает клавиатуру
        getEditText().onEditorAction(EditorInfo.IME_ACTION_DONE)
    }
}