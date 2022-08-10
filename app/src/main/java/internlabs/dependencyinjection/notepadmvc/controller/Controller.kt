package internlabs.dependencyinjection.notepadmvc.controller

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import internlabs.dependencyinjection.notepadmvc.R
import internlabs.dependencyinjection.notepadmvc.viewer.Viewer
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream


class Controller(viewer: Viewer) : OurTasks, View.OnClickListener {
    private var viewer: Viewer
    var filePicker: ActivityResultLauncher<Intent>? = viewer.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val intent1 = result.data
            val uri = intent1!!.data
            val byteData = getText(viewer, uri)
            // TODO
            viewer.setText(String(byteData!!))
            // binding.etMailField.setText(String(byteData!!))
        }
    }

    init {
        this.viewer = viewer
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

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_new -> {
                new()
            }
            R.id.btn_save -> {

            }
            R.id.btn_open -> {
                open()
            }
        }
    }

    override fun new() {
        val textFromFile = File("fileName.txt")
            .bufferedReader()
            .use { it.readText(); }
    }

    private fun isOk(file: File): Boolean {
        val fileName = file.name
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
            val extensionOfFile = fileName.substring(fileName.lastIndexOf(".") + 1)
            // System.out.println(extensionOfFile);
            if (extensionOfFile == "ntp" || extensionOfFile == "kt" || extensionOfFile == "swift"
                || extensionOfFile == "java") {
                val erkin = file.length().toInt()
                val max = 5629273
                return erkin < max
            }
        }
        return false
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

    override fun save(fileName: String) {
        TODO("Not yet implemented")
    }

    override fun saveAs() {
        TODO("Not yet implemented")
    }

    override fun print() {
        TODO("Not yet implemented")
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

    override fun redo() {
        TODO("Not yet implemented")
    }

    override fun undo() {
        TODO("Not yet implemented")
    }

    override fun cut() {
        TODO("Not yet implemented")
    }

    override fun copy() {
        TODO("Not yet implemented")
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


}