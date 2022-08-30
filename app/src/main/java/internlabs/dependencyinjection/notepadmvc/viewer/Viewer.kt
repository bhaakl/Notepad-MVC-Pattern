package internlabs.dependencyinjection.notepadmvc.viewer

import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import androidx.drawerlayout.widget.DrawerLayout
import internlabs.dependencyinjection.notepadmvc.controller.Controller
import internlabs.dependencyinjection.notepadmvc.databinding.ActivityViewerBinding
import internlabs.dependencyinjection.notepadmvc.util.TextUndoRedo
import java.util.*


// merge
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
        undoRedoManager.setMaxHistorySize(1000)
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
        if (strAdd.isEmpty() || !binding.editText.isEnabled) { // нельзя выставить пока документ не создан
            showToast("нельзя выставить текст пока документ не создан")
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
        showToast("Pasted")
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
        binding.editText.onEditorAction(EditorInfo.IME_ACTION_DONE)
    }

    override fun onDestroy() {
        super.onDestroy()

        undoRedoManager.clearHistory()
        undoRedoManager.disconnect()
        close()
    }

    fun makeEditTextEditable() {
        binding.editText.isEnabled = true
        binding.editText.isFocusable = true
    }

    fun showToast(s: String) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
    }

    fun getFonts() : Paint {
        val paint = Paint()
        val spSize = binding.editText.textSize
        paint.textSize = spSize / 100 * 80
        paint.letterSpacing = binding.editText.letterSpacing
        paint.typeface = Typeface.create(binding.editText.typeface, getTypeface())
        paint.color = binding.editText.currentTextColor
        return paint
    }

    private fun getTypeface(): Int {
        if (binding.editText.typeface.isBold && binding.editText.typeface.isItalic){
            return Typeface.BOLD_ITALIC
        } else if (binding.editText.typeface.isItalic) {
            return Typeface.ITALIC
        } else if (binding.editText.typeface.isBold) {
            return Typeface.BOLD
        }
        return Typeface.NORMAL
    }
}