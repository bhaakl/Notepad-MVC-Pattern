package internlabs.dependencyinjection.notepadmvc.controller

import android.annotation.SuppressLint
import android.content.ClipboardManager
import android.content.ContentValues
import android.content.Context
import android.graphics.Color
import android.app.Dialog
import android.content.*
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.documentfile.provider.DocumentFile
import com.google.android.material.navigation.NavigationView

import internlabs.dependencyinjection.notepadmvc.R
import internlabs.dependencyinjection.notepadmvc.util.BMooreMatchText
import internlabs.dependencyinjection.notepadmvc.util.PrintDocument
import internlabs.dependencyinjection.notepadmvc.util.TextEditor
import internlabs.dependencyinjection.notepadmvc.viewer.Viewer
import java.io.*
import kotlin.system.exitProcess

//-
class Controller(viewer: Viewer) : OurTasks, View.OnClickListener,
    NavigationView.OnNavigationItemSelectedListener, Toolbar.OnMenuItemClickListener {
    private var viewer: Viewer
    private var uri: Uri = Uri.parse("")
    private var pasteItemInNavMenu: MenuItem? = null  //used for method paste()
    private var pasteItemInBotMenu: MenuItem? = null  //used for method paste()

    init {
        this.viewer = viewer
    }

    // DrawerLayout click handler
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.openFile -> {
                open()
                viewer.getUndoRedoManager().clearHistory()
            }
            R.id.newFile -> {
                new()
                viewer.getUndoRedoManager().clearHistory()
            }
            R.id.save -> {
                save()
                viewer.getUndoRedoManager().clearHistory()
            }
            R.id.saveAs -> {
                saveAs()
                viewer.getUndoRedoManager().clearHistory()
            }
            R.id.about_app -> {
                aboutApp()
            }

            R.id.exit -> {
                exit()
            }

            R.id.copy -> {
                copy()
            }
            R.id.paste -> {
                paste(item)
                pasteItemInNavMenu = item
            }
            R.id.cut -> {
                cut()
            }
            R.id.delete -> {
                delete()
            }
            R.id.select_all -> {
                selectAll()
            }
            R.id.undo -> {
                undo()
            }
            R.id.printDocument -> {
                print()
            }
            R.id.redo -> {
                redo()
            }
            R.id.searchText -> {
                find()
            }
        }
        item.isChecked = true
        viewer.getDrawerLayout().close()
        return true
    }

    // bottomAppBar click handler
    override fun onMenuItemClick(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.more -> {
                viewer.getDrawerLayout().open()
                true
            }
            R.id.printDocumentBtm -> {
                print()
                true
            }
            R.id.saveAsBtm -> {
                saveAs()
                viewer.getUndoRedoManager().clearHistory()
                true
            }
            R.id.openFileBtm -> {
                open()
                viewer.getUndoRedoManager().clearHistory()
                true
            }
            R.id.saveBtm -> {
                save()
                viewer.getUndoRedoManager().clearHistory()
                true
            }
            R.id.newFileBtm -> {
                new()
                viewer.getUndoRedoManager().clearHistory()
                true
            }
            R.id.searchBtm -> {
                find()
                true
            }
            R.id.redoBtm -> {
                redo()
                true
            }
            R.id.undoBtm -> {
                undo()
                true
            }
            R.id.pasteBtm -> {
                paste(item)
                pasteItemInBotMenu = item
                true
            }
            R.id.copyBtm -> {
                copy()
                true
            }
            else -> false
        }
    }

    override fun new() {
        viewer.makeEditTextEditable()
        viewer.setTextFromFile("")
        val outputFile: String =
            viewer.getExternalFilesDir("Store").toString() + "/Example.ntp"
        val file1 = File(outputFile)
        uri = FileProvider.getUriForFile(
            viewer,
            "internlabs.dependencyinjection.notepadmvc.provider",
            file1
        )
    }

    override fun open() {
        openDoc.launch(arrayOf("*/*"))
    }

    private val openDoc = viewer.registerForActivityResult(ActivityResultContracts.OpenDocument())
    { uri1 ->
        if (uri1 != null) {
            if (isOk(uri1)) {
                val byteData = getText(viewer, uri1)
                byteData?.let { String(it) }?.let {
                    println(it)
                    viewer.setTextFromFile(it)
                }
                uri = uri1
                viewer.makeEditTextEditable()
            } else {
                viewer.showToast("Файл не поддерживается!")
            }
        }
    }

    private fun getText(context: Context, uri: Uri?): ByteArray? {
        var inputStream: InputStream? = null // TODO Stream нельзя использовать
        return try {
            inputStream =
                context.contentResolver.openInputStream(uri!!) // TODO Stream нельзя использовать
            val outputStream = ByteArrayOutputStream() // TODO Stream нельзя использовать
            val bufferSize = 1024
            val buffer = ByteArray(bufferSize)
            var len = 0
            while (inputStream!!.read(buffer)
                    .also { len = it } != -1
            ) { // TODO Stream нельзя использовать
                outputStream.write(buffer, 0, len) // TODO Stream нельзя использовать
            }
            outputStream.toByteArray() // TODO Stream нельзя использовать
        } catch (ex: Exception) {
            Log.e("Error", ex.message.toString())
            viewer.showToast("getText error: ${ex.message}")
            null
        }
    }

    override fun redo() {
        val manager = viewer.getUndoRedoManager()
        if (manager.canRedo) {
            manager.redo()
        }
    }

    override fun undo() {
        val manager = viewer.getUndoRedoManager()
        if (manager.canUndo) {
            manager.undo()
        }
    }

    override fun cut() {
        val clipboardManager =
            viewer.applicationContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        if (TextEditor.cut(viewer.getEditText(), clipboardManager)) {
            viewer.showToast("Cut Out")
            if (pasteItemInNavMenu?.isEnabled == false)
                pasteItemInNavMenu?.isEnabled = true
            if (pasteItemInBotMenu?.isEnabled == false)
                pasteItemInBotMenu?.isEnabled = true
        }
    }

    override fun copy() {
        val clipboardManager =
            viewer.applicationContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        if (TextEditor.copy(viewer.getEditText(), clipboardManager)) {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2)
                viewer.showToast("Copied")
            if (pasteItemInNavMenu?.isEnabled == false || pasteItemInBotMenu?.isEnabled == false) {
                pasteItemInNavMenu?.isEnabled = true
                pasteItemInBotMenu?.isEnabled = true
            }
        }
    }

    override fun paste(pasteItem: MenuItem) {
        val clipboardManager =
            viewer.applicationContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        // Gets the clipboard as text.
        val pasteData = TextEditor.paste(clipboardManager, pasteItem)
        if (pasteData.isNotEmpty()) {
            if (!pasteItem.isEnabled) pasteItem.isEnabled = true
            // Если строка содержит данные, то выполняется операция вставки
            viewer.setTextForEditor(pasteData)
            viewer.showToast("Pasted")
        } else {
            // Что-то не так. Тип MIME был обычным текстом, но буфер обмена не
            // содержат текст. Сообщить об ошибке.
            viewer.showToast("Clipboard is empty!")
            Log.e(ContentValues.TAG, "Clipboard contains an invalid data type")
        }
    }

    override fun delete() {
        if (TextEditor.delete(viewer.getEditText()))
            viewer.showToast("Deleted")
    }

    @SuppressLint("InflateParams", "ResourceAsColor")
    override fun find() {
        val inflater: LayoutInflater = LayoutInflater.from(viewer)
        val view: View = inflater.inflate(R.layout.feature_find, null)
        val mBuilder = androidx.appcompat.app.AlertDialog.Builder(viewer)
            .setTitle("Find")
            .setIcon(R.drawable.ic_search_in)
            .setView(view)
            .setPositiveButton("Find next", null)
            .setNegativeButton("Cancel", null)
            .setCancelable(true)
            .show()

        val mPositiveButton = mBuilder.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)
        mPositiveButton.setTextColor(Color.CYAN)
        val mNegativeButton = mBuilder.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE)
        mNegativeButton.setTextColor(Color.RED)
        val editTextFind = view.findViewById<EditText>(R.id.findWhat)

        var posD = 0
        mPositiveButton.setOnClickListener {
            if (viewer.getEditText().text.isNotEmpty()) {
                val matchingAnswer = BMooreMatchText.search(
                    viewer.getEditText().text.toString().toCharArray(),
                    editTextFind.text.toString().toCharArray()
                )
                if (matchingAnswer.isNotEmpty()) {
                    if (posD == matchingAnswer.size || posD > matchingAnswer.size) posD = 0
                    val edFind = editTextFind.text.toString()
                    viewer.getEditText().setSelection(
                        matchingAnswer[posD++],
                        matchingAnswer[posD - 1] + edFind.length
                    )
                } else
                    viewer.showToast("'${editTextFind.text}' not found!")
            } else
                viewer.showToast("Text empty!")
        }
    }

    override fun replace() {
        TODO("Not yet implemented")
    }

    override fun selectAll() {
        viewer.getEditText().selectAll()
    }

    override fun dateAndTime() {
        TODO("Not yet implemented")
    }

    override fun zoomIn() {
        TODO("Not yet implemented")
    }

    override fun zoomOut() {
        TODO("Not yet implemented")
    }

    override fun zoomDefault() {
        TODO("Not yet implemented")
    }

    override fun size() {

        val editText = viewer.getEditText()
        val spinner = viewer.findViewById<Spinner>(R.id.spinner)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                when (spinner.selectedItemPosition) {
                    1 -> editText.textSize = 20f
                    2 -> editText.textSize = 25f
                    3 -> editText.textSize = 30f
                    4 -> editText.textSize = 35f
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

    }

    override fun fontFamily() {
        TODO("Not yet implemented")
    }

    override fun makeBold() {
        val editText = viewer.getEditText()
        val spannableString = SpannableStringBuilder(editText.text)
        spannableString.setSpan(StyleSpan(Typeface.BOLD),
            editText.selectionStart,
            editText.selectionEnd,
            0)
        editText.text = spannableString
    }

    override fun makeItalic() {
        val editText = viewer.getEditText()
        val spannableString = SpannableStringBuilder(editText.text)
        spannableString.setSpan(StyleSpan(Typeface.ITALIC),
            editText.selectionStart,
            editText.selectionEnd,
            0)
        editText.text = spannableString
    }

    override fun makeUnderlined() {
        val editText = viewer.getEditText()
        val spannableString = SpannableStringBuilder(editText.text)
        spannableString.setSpan(UnderlineSpan(), editText.selectionStart, editText.selectionEnd, 0)
        editText.text = spannableString
    }

    override fun makeRegularFormat() {
        val editText = viewer.getEditText()
        val stringText :String = editText.text.toString()
        editText.setText(stringText)
    }

    override fun makeCrossedOut() {
        TODO("Not yet implemented")
    }

    override fun makeSubscript() {
        TODO("Not yet implemented")
    }

    override fun makeSuperscript() {
        TODO("Not yet implemented")
    }

    override fun textColor() {
        TODO("Not yet implemented")
    }

    override fun textBackground() {
        TODO("Not yet implemented")
    }

    override fun alignLeft() {
        val editText = viewer.getEditText()
        editText.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
        val spannableString = SpannableStringBuilder(editText.text)
        editText.text = spannableString
    }

    override fun alignRight() {
        val editText = viewer.getEditText()
        editText.textAlignment = View.TEXT_ALIGNMENT_TEXT_END
        val spannableString = SpannableStringBuilder(editText.text)
        editText.text = spannableString
    }

    override fun alignCenter() {
        val editText = viewer.getEditText()
        editText.textAlignment = View.TEXT_ALIGNMENT_CENTER
        val spannableString = SpannableStringBuilder(editText.text)
        editText.text = spannableString
    }

    override fun lineSpace() {
        TODO("Not yet implemented")
    }

    override fun letterSpace() {
        TODO("Not yet implemented")
    }

    override fun changeTheme() {
        TODO("Not yet implemented")
    }

    override fun save() {
        saveToFile(uri)
        viewer.showToast("File has been saved!")
    }

    override fun saveAs() {
        val u = DocumentFile.fromSingleUri(viewer, uri)?.name.toString()
        saveAsDoc.launch(u)
    }

    override fun print() {
        val content: String = viewer.getEditText().text.toString()
        if (content != "") {
            val printDocument = PrintDocument(content, viewer)
            printDocument.doPrint()
        } else {
            viewer.showToast("Документ пустой!")
        }
    }

    override fun recent() {
        TODO("Not yet implemented")
    }

    override fun aboutApp() {
        dialog = Dialog(viewer)
        dialog.setContentView(R.layout.dialog_layout)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    override fun sentToEmail() {
        TODO("Not yet implemented")
    }

    override fun exit() {
        val alertDialog = AlertDialog.Builder(viewer)
        alertDialog.setTitle("Exit")
            .setMessage(" Do you want to exit? ")
            .setCancelable(true)
            .setPositiveButton(" Cancel") { dialogInterface, _ -> dialogInterface.cancel() }
            .setNegativeButton(" Yes") { _, _ ->
                 exitProcess(0)
           // viewer.finish()
            }
        alertDialog.show()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.editText -> {
                viewer.keyBoardShow()
            }

            R.id.fab -> {
                viewer.animateFab()
            }

            R.id.bolt -> {
                viewer.animateFab()
                makeBold()
            }

            R.id.italic -> {
                viewer.animateFab()
                makeItalic()
            }

            R.id.underline -> {
                viewer.animateFab()
                makeUnderlined()
            }

            R.id.no_format -> {
                viewer.animateFab()
                makeRegularFormat()
            }

            R.id.align_left -> {
                viewer.animateFab()
                alignLeft()
            }

            R.id.align_right -> {
                viewer.animateFab()
                alignRight()
            }

            R.id.align_center -> {
                viewer.animateFab()
                alignCenter()
            }
        }
    }

    //region  Медер Шермаматов
    //-- служебный метод Фильтр для файлов()
    private fun isOk(uri: Uri): Boolean {
        val fileName = DocumentFile.fromSingleUri(viewer, uri)?.name
        if (fileName != null) {
            if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
                val extensionOfFile = fileName.substring(fileName.lastIndexOf(".") + 1)
                if (extensionOfFile == "ntp"
                    || extensionOfFile == "kt"
                    || extensionOfFile == "swift"
                    || extensionOfFile == "java"
                ) {
                    val size = DocumentFile.fromSingleUri(viewer, uri)?.length()
                    val max = 5629273
                    if (size != null) {
                        return size < max
                    }
                }
            }
        }
        return false
    }
    // -- служебный метод Сохранение в файл()
    private fun saveToFile(uri: Uri) {
        val text = viewer.getEditText().text.toString()
        try {
            viewer.contentResolver.openFileDescriptor(uri, "rw")?.use { content ->
                FileOutputStream(content.fileDescriptor).use { fos ->
                    fos.write(text.toByteArray())
                    fos.flush()
                    fos.close()
                }
            }
        } catch (e: FileNotFoundException) {
            viewer.showToast("File not found!")
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    // -- служебный метод Сохранить как()
    private val saveAsDoc = viewer.registerForActivityResult(
        ActivityResultContracts
            .CreateDocument("application/ntp")
    ) {
        if (it != null) {
            if (isOk(it)) {
                saveToFile(it)
                uri = it
                viewer.setTextFromFile("")
            } else {
                viewer.showToast("File extension is not supported!")
            }
        }

    }
    //endregion
}