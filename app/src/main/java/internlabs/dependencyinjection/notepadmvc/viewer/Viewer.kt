package internlabs.dependencyinjection.notepadmvc.viewer

import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import androidx.drawerlayout.widget.DrawerLayout
import internlabs.dependencyinjection.notepadmvc.R
import internlabs.dependencyinjection.notepadmvc.controller.Controller
import internlabs.dependencyinjection.notepadmvc.databinding.ActivityViewerBinding
import internlabs.dependencyinjection.notepadmvc.util.TextUndoRedo


class Viewer : AppCompatActivity() {
    private lateinit var binding: ActivityViewerBinding
    private var controller: Controller
    private var isOpenFab = false

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
        editText.breakStrategy = editText.width-30

        navigationView.setNavigationItemSelectedListener(controller)
        fab.setOnClickListener(controller)
        bolt.setOnClickListener(controller)
        italic.setOnClickListener(controller)
        underline.setOnClickListener(controller)
        noFormat.setOnClickListener(controller)
        alignLeft.setOnClickListener(controller)
        alignCenter.setOnClickListener(controller)
        alignRight.setOnClickListener(controller)
        controller.size()

        undoRedoManager = TextUndoRedo(binding.editText)
        undoRedoManager.setMaxHistorySize(1000)
        editText.add
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
     * поместить этот текст: определяется позиция для вставки текста и вставляется
     * обновляя при этом курсор.
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

    fun getUndoRedoManager(): TextUndoRedo {
        return undoRedoManager
    }


    fun getEditText(): EditText {
        return binding.editText
    }

    fun getDrawerLayout(): DrawerLayout {
        return binding.drawerLayout
    }

    fun keyBoardShow() {
        // убирает клавиатуру
        getEditText().onEditorAction(EditorInfo.IME_ACTION_DONE)
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

    fun animateFab() = with(binding) {
        if (isOpenFab) {
            fab.startAnimation(AnimationUtils.loadAnimation(this@Viewer, R.anim.rotat_forward))
            alignCenter.startAnimation(AnimationUtils.loadAnimation(this@Viewer, R.anim.fab_close))
            alignRight.startAnimation(AnimationUtils.loadAnimation(this@Viewer, R.anim.fab_close))
            alignLeft.startAnimation(AnimationUtils.loadAnimation(this@Viewer, R.anim.fab_close))
            bolt.startAnimation(AnimationUtils.loadAnimation(this@Viewer, R.anim.fab_close))
            italic.startAnimation(AnimationUtils.loadAnimation(this@Viewer, R.anim.fab_close))
            underline.startAnimation(AnimationUtils.loadAnimation(this@Viewer, R.anim.fab_close))
            noFormat.startAnimation(AnimationUtils.loadAnimation(this@Viewer, R.anim.fab_close))
            bolt.isClickable = false
            italic.isClickable = false
            underline.isClickable = false
            noFormat.isClickable = false
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
            noFormat.startAnimation(AnimationUtils.loadAnimation(this@Viewer, R.anim.fab_open))
            bolt.isClickable = true
            italic.isClickable = true
            underline.isClickable = true
            noFormat.isClickable = true
            alignCenter.isClickable = true
            alignLeft.isClickable = true
            alignRight.isClickable = true
            isOpenFab = true
        }

    }

    private fun close() {
        binding.drawerLayout.close()
    }


    fun getFonts() : Paint {
        val paint = Paint()
        val spSize = binding.editText.textSize
        paint.textAlign = getTextAlignment()
        paint.textSize = spSize / 100 * 74
        paint.textLocale = binding.editText.textLocale
        paint.letterSpacing = binding.editText.letterSpacing
        paint.typeface = Typeface.create(binding.editText.typeface, getTypeface())
        paint.color = binding.editText.currentTextColor
        return paint
    }

    private fun getTextAlignment(): Paint.Align {
        when(binding.editText.textAlignment){
            View.TEXT_ALIGNMENT_TEXT_START -> return Paint.Align.LEFT
            View.TEXT_ALIGNMENT_CENTER -> return Paint.Align.CENTER
            View.TEXT_ALIGNMENT_TEXT_END -> return Paint.Align.RIGHT
            View.TEXT_ALIGNMENT_GRAVITY -> {
                println("There is no corresponding in Paint.Align")
            }
            View.TEXT_ALIGNMENT_INHERIT -> {
                println("There is no corresponding in Paint.Align")
            }
            View.TEXT_ALIGNMENT_VIEW_END -> return Paint.Align.RIGHT
            View.TEXT_ALIGNMENT_VIEW_START -> return Paint.Align.LEFT
        }
        return Paint.Align.LEFT
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