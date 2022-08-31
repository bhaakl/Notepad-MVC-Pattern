package internlabs.dependencyinjection.notepadmvc.controller

import android.content.*
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.documentfile.provider.DocumentFile
import com.google.android.material.navigation.NavigationView
import internlabs.dependencyinjection.notepadmvc.R
import internlabs.dependencyinjection.notepadmvc.util.PrintDocument
import internlabs.dependencyinjection.notepadmvc.viewer.Viewer
import java.io.*


class Controller(viewer: Viewer) : OurTasks, View.OnClickListener,
    NavigationView.OnNavigationItemSelectedListener {

    private var viewer: Viewer
    private var uri: Uri = Uri.parse("")

    init {
        this.viewer = viewer
    }

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
                viewer.showAlertDialog()
            }
            R.id.copy -> {
                copy("")
            }
            R.id.paste -> {
                paste(item)
            }
            R.id.cut -> {
                cut()
            }
            R.id.printDocument ->{
                print()
            }
            R.id.undo -> {
                undo()
            }
            R.id.redo -> {
                redo()
            }
        }
        item.isChecked = true
        viewer.getDrawerLayout().close()
        return true
    }

    override fun new() {
        viewer.makeEditTextEditable()
        viewer.setTextFromFile("")
        val fileReader: FileReader = FileReader()
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
        if(manager.canRedo) {
            manager.redo()
        }
    }

    override fun undo() {
        val manager = viewer.getUndoRedoManager()
        if(manager.canUndo) {
            manager.undo()
        }
    }

    override fun cut() {
        val startSelection: Int = viewer.getEditText().selectionStart
        val endSelection: Int = viewer.getEditText().selectionEnd
        val selectedText: String = viewer.getEditText().text.toString()
            .substring(startSelection, endSelection)

        copy(selectedText)
        viewer.getEditText().text =
            viewer.getEditText().text.replace(startSelection, endSelection, "")
        viewer.getEditText().setSelection(startSelection)
        //viewer.toastCut()
        viewer.showToast("Cut Out")
    }

    override fun copy(textCopied: String) {
        val startSelection: Int = viewer.getEditText().selectionStart
        val endSelection: Int = viewer.getEditText().selectionEnd
        val cTextCopied: String = viewer.getEditText().text.toString()
        .substring(startSelection, endSelection)

        println("selected text: $cTextCopied")
        val clipboardManager =
            viewer.applicationContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboardManager.setPrimaryClip(ClipData.newPlainText("", cTextCopied))
        // Only show a toast for Android 12 and lower.
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2)
            //viewer.toastCopied()
            viewer.showToast("Copied")
        viewer.getEditText().setSelection(viewer.getEditText().selectionEnd)
    }

    override fun paste(pasteItem: MenuItem): Boolean {
        viewer.makeEditTextEditable() //TODO
        val clipboard =
            viewer.applicationContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        var pasteData: String = ""

        // Если в буфере обмена нет данных, отключаем пункт меню вставки.
        // Если он содержит данные, решите, можете ли вы обрабатывать данные.
        pasteItem.isEnabled = when {
            !clipboard.hasPrimaryClip() -> {
                false
            }
            !(clipboard.primaryClipDescription?.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN))!! -> {
                // Это отключает пункт меню вставки, так как в буфере обмена есть данные, но это не обычный текст
                false
            }
            else -> {
                // Это включает пункт меню вставки, так как буфер обмена содержит обычный текст.
                true
            }
        }
        val item = clipboard.primaryClip?.getItemAt(0)

        // Gets the clipboard as text.
        pasteData = item?.text.toString()
        println("PASTED text: $pasteData")
        return if (pasteData.isNotEmpty()) {
            // Если строка содержит данные, то выполняется операция вставки
            viewer.setTextForEditor(pasteData)
            //viewer.toastPasted()
            //viewer.showToast("Pasted")
            true
        } else {
            // Что-то не так. Тип MIME был обычным текстом, но буфер обмена не
            // содержат текст. Сообщить об ошибке.
            Log.e(ContentValues.TAG, "Clipboard contains an invalid data type")
            false
        }
    }

    override fun delete() {
        TODO("Not yet implemented")
    }

    override fun find() {
        TODO("Not yet implemented")
    }

    override fun replace() {
        TODO("Not yet implemented")
    }

    override fun selectAll() {
        TODO("Not yet implemented")
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
        TODO("Not yet implemented")
    }

    override fun fontFamily() {
        TODO("Not yet implemented")
    }

    override fun makeBold() {
        TODO("Not yet implemented")
    }

    override fun makeItalic() {
        TODO("Not yet implemented")
    }

    override fun makeCursive() {
        TODO("Not yet implemented")
    }

    override fun makeUnderlined() {
        TODO("Not yet implemented")
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
        TODO("Not yet implemented")
    }

    override fun alignRight() {
        TODO("Not yet implemented")
    }

    override fun alignLeftAndLeft() {
        TODO("Not yet implemented")
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
        val content : String = viewer.getEditText().text.toString()
        if(content != "" && contentIsNormal(content)){
            val printDocument = PrintDocument(content,this.viewer, viewer.getFonts())
            printDocument.doPrint()
        }
        else {
            viewer.showToast("Документ пустой!")
        }
    }

    private fun contentIsNormal(content: String): Boolean {
        content.forEach {
            if (it != ' ' && it != '\n') return true
        }
        return false
    }

    override fun recent() {
        TODO("Not yet implemented")
    }

    override fun aboutApp() {
        TODO("Not yet implemented")
    }

    override fun sentToEmail() {
        TODO("Not yet implemented")
    }

    override fun exit() {
        TODO("Not yet implemented")
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.editText -> {
                viewer.keyBoardShow()
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
                    || extensionOfFile == "java") {
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
            println("нетуу")
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
            if (isOk(it)){
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