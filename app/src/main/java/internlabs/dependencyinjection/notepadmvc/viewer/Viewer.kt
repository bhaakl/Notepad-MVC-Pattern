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
import internlabs.dependencyinjection.notepadmvc.printDocument.PrintDocument


class Viewer : AppCompatActivity() {
    private lateinit var binding: ActivityViewerBinding
    private var controller: Controller
    private lateinit var alertDialog: AlertDialog.Builder

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

    /**
     * setTextForEditor(): На  вход подается текст, который нужно поместить в ЕдитТекст. Задача этой функции
     * поместить этот текст: определяется позиция для вставки текста и вставляется
     * обновляя при этом курсор.
     */
    fun setTextForEditor(strAdd: String) {
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

    fun close() {
        binding.drawerLayout.close()
    }

    fun keyBoardShow() {
        // убирает клавиатуру
        binding.editText.onEditorAction(EditorInfo.IME_ACTION_DONE)
    }
}