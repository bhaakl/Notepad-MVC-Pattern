package internlabs.dependencyinjection.notepadmvc.controller

import android.content.*
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.navigation.NavigationView
import internlabs.dependencyinjection.notepadmvc.R
import internlabs.dependencyinjection.notepadmvc.viewer.Viewer
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream


class Controller(viewer: Viewer) : OurTasks, View.OnClickListener,
    NavigationView.OnNavigationItemSelectedListener {
    private var viewer: Viewer

    init {
        this.viewer = viewer
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.newFile -> {
                new()
                item.isChecked = !item.isChecked
                viewer.getBinding().drawerLayout.close()
                item.isEnabled = false
                true
            }
            R.id.openFile -> {
                open()
                item.isChecked = !item.isChecked
                viewer.getBinding().drawerLayout.close()
                true
            }
            R.id.copy -> {
                item.isChecked = !item.isChecked
                viewer.getBinding().drawerLayout.close()
                val startSelection: Int = viewer.getBinding().editText.selectionStart
                val endSelection: Int = viewer.getBinding().editText.selectionEnd
                val selectedText: String = viewer.getBinding().editText.text.toString()
                    .substring(startSelection, endSelection)
                println("selected text: $selectedText")
                copy(selectedText)
                true
            }
            R.id.paste -> {
                item.isChecked = !item.isChecked
                viewer.getBinding().drawerLayout.close()
                paste(item)
            }
            R.id.cut -> {
                item.isChecked = !item.isChecked
                viewer.getBinding().drawerLayout.close()
                val startSelection: Int = viewer.getBinding().editText.selectionStart
                val endSelection: Int = viewer.getBinding().editText.selectionEnd
                val selectedText: String = viewer.getBinding().editText.text.toString()
                    .substring(startSelection, endSelection)
                cut(selectedText, startSelection, endSelection)
                true
            }
            else -> false

        }
    }

    override fun new() {
        val textFromFile = File("fileName.txt")
    }

    override fun open() {
        try {
            val fileIntent = Intent(Intent.ACTION_GET_CONTENT)
            fileIntent.addCategory(Intent.CATEGORY_OPENABLE)
            fileIntent.type = "*/*"
            // TODO .kt, .java, .kotlin, .swift, .ntp нужно
            //TODO здесь поставить такие фильтры
            filePicker!!.launch(fileIntent)
        } catch (ex: Exception) {
            Log.e("Error", ex.message!!)
            Toast.makeText(viewer, ex.message.toString(), Toast.LENGTH_LONG).show()
        }

    }

    private var filePicker: ActivityResultLauncher<Intent>? = viewer.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val intent1 = result.data
            val uri = intent1!!.data
            val byteData = getText(viewer, uri)
            // TODO
            viewer.setText(String(byteData!!))
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

    override fun save(fileName: String) {
        // TODO("Not yet implemented")
    }

    override fun saveAs() {
        // TODO("Not yet implemented")
    }

    override fun print() {
        //  TODO("Not yet implemented")
    }

    override fun recent() {
        //TODO("Not yet implemented")
    }

    override fun aboutApp() {
        //TODO("Not yet implemented")
    }

    override fun sentToEmail() {
        //TODO("Not yet implemented")
    }

    override fun exit() {
        //TODO("Not yet implemented")
    }

    override fun zoomIn() {
        //TODO("Not yet implemented")
    }

    override fun zoomOut() {
        //TODO("Not yet implemented")
    }

    override fun zoomDefault() {
        //TODO("Not yet implemented")
    }

    override fun size() {
        //TODO("Not yet implemented")
    }

    override fun fontFamily() {
        //TODO("Not yet implemented")
    }

    override fun makeBold() {
        //TODO("Not yet implemented")
    }

    override fun makeItalic() {
        //    TODO("Not yet implemented")
    }

    override fun makeCursive() {
        //  TODO("Not yet implemented")
    }

    override fun makeUnderlined() {
        //TODO("Not yet implemented")
    }

    override fun makeCrossedOut() {
        //    TODO("Not yet implemented")
    }

    override fun makeSubscript() {
        //  TODO("Not yet implemented")
    }

    override fun makeSuperscript() {
        //TODO("Not yet implemented")
    }

    override fun textColor() {
        //    TODO("Not yet implemented")
    }

    override fun textBackground() {
        //  TODO("Not yet implemented")
    }

    override fun alignLeft() {
        //TODO("Not yet implemented")
    }

    override fun alignRight() {
        //    TODO("Not yet implemented")
    }

    override fun alignLeftAndLeft() {
        //  TODO("Not yet implemented")
    }

    override fun lineSpace() {
        //TODO("Not yet implemented")
    }

    override fun letterSpace() {
        //    TODO("Not yet implemented")
    }

    override fun changeTheme() {
        //  TODO("Not yet implemented")
    }

    override fun onClick(v: View?) {
        TODO("Not yet implemented")
    }


}