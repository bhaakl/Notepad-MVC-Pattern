package internlabs.dependencyinjection.notepadmvc.controller

import android.content.*
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.documentfile.provider.DocumentFile
import com.google.android.material.navigation.NavigationView
import internlabs.dependencyinjection.notepadmvc.BuildConfig
import internlabs.dependencyinjection.notepadmvc.R
import internlabs.dependencyinjection.notepadmvc.viewer.Viewer
import java.io.*

//-
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
            }
            R.id.newFile -> {
                new()
            }
            R.id.save -> {
                save()
            }
            R.id.saveAs -> {
                saveAs()
            }
            R.id.about_app -> {
                viewer.showAlertDialog()
            }
            R.id.copy -> {
                val startSelection: Int = viewer.getBinding().editText.selectionStart
                val endSelection: Int = viewer.getBinding().editText.selectionEnd
                val selectedText: String = viewer.getBinding().editText.text.toString()
                    .substring(startSelection, endSelection)
                println("selected text: $selectedText")
                copy(selectedText)
            }
            R.id.paste -> {
                paste(item)
            }
            R.id.cut -> {
                val startSelection: Int = viewer.getBinding().editText.selectionStart
                val endSelection: Int = viewer.getBinding().editText.selectionEnd
                val selectedText: String = viewer.getBinding().editText.text.toString()
                    .substring(startSelection, endSelection)
                cut(selectedText, startSelection, endSelection)
            }
        }
        item.isChecked = !item.isChecked;
        viewer.getBinding().drawerLayout.close()
        return true
    }

    override fun new() {
        viewer.setTextFromFile("")
        val outputFile: String =
            viewer.getExternalFilesDir("Store").toString() + "/Example.ntp"
        val file1 = File(outputFile)
        uri = FileProvider.getUriForFile(viewer,
            BuildConfig.APPLICATION_ID + ".provider",
            file1)
    }

    override fun open() {
        openDoc.launch(arrayOf("*/*"))
    }

    private val openDoc = viewer.registerForActivityResult(ActivityResultContracts.OpenDocument())
    { uri1 ->
        if (uri1 != null) {
            if (isOk(uri1)) {
                println("......................")
                val byteData = getText(viewer, uri1)
                byteData?.let { String(it) }?.let {
                    println(it)
                    viewer.setTextFromFile(it) }
                uri = uri1
            } else {
                Toast.makeText(viewer, "Файл не поддерживается!", Toast.LENGTH_LONG)
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
            Toast.makeText(context, "getText error:" + ex.message, Toast.LENGTH_LONG).show()
            null
        }
    }

    override fun redo() {
        TODO("Not yet implemented")
    }

    override fun undo() {
        TODO("Not yet implemented")
    }

    override fun cut(textCut: String, startSelection: Int, endSelection: Int) {
        copy(textCut)
        viewer.getBinding().editText.text =
            viewer.getBinding().editText.text.replace(startSelection, endSelection, "")
        viewer.getBinding().editText.setSelection(startSelection)
        viewer.toastCut()
    }

    override fun copy(textCopied: String) {
        val clipboardManager =
            viewer.applicationContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboardManager.setPrimaryClip(ClipData.newPlainText("", textCopied))
        // Only show a toast for Android 12 and lower.
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2)
            viewer.toastCopied()
        viewer.getBinding().editText.setSelection(viewer.getBinding().editText.selectionEnd)
    }

    override fun paste(pasteItem: MenuItem): Boolean {
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
            viewer.setText(pasteData)
//            viewer.getBinding().editText.setSelection(viewer.getBinding().editText.selectionStart)
            viewer.toastPasted()
            true
        } else {
            // Что-то не так. Тип MIME был обычным текстом, но буфер обмена не
            // содержат текст. Сообщить об ошибке.
            Log.e(ContentValues.TAG, "Clipboard contains an invalid data type")
            false
        }
    }

    override fun insert() {
        TODO("Not yet implemented")
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
    }

    override fun saveAs() {
        val u = DocumentFile.fromSingleUri(viewer, uri)?.name.toString()
        saveAsDoc.launch(u)
    }

    override fun print() {
        //  TODO("Not yet implemented")
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
                if (extensionOfFile == "ntp" || extensionOfFile == "kt" || extensionOfFile == "swift" || extensionOfFile == "java") {
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
        val text = viewer.getText()
        try {
            viewer.contentResolver.openFileDescriptor(uri, "rw")?.use {content ->
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
    private val saveAsDoc = viewer.registerForActivityResult(ActivityResultContracts
        .CreateDocument("application/ntp")){
        if (it != null) {
            if (isOk(it)){
                saveToFile(it)
                uri = it
                viewer.setTextFromFile("")
            } else {

                Toast.makeText(viewer, "Файл не поддерживается!", Toast.LENGTH_LONG)
            }
        }

    }
    //endregion
}