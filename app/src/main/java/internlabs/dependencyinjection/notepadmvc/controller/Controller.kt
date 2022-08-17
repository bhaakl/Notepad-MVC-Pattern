package internlabs.dependencyinjection.notepadmvc.controller

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.telecom.PhoneAccount.builder
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.navigation.NavigationView
import internlabs.dependencyinjection.notepadmvc.R
import internlabs.dependencyinjection.notepadmvc.viewer.Viewer
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream


class Controller(viewer: Viewer) : OurTasks, View.OnClickListener,
    NavigationView.OnNavigationItemSelectedListener {
    private var viewer: Viewer
    private lateinit var dialog: Dialog

    init {
        this.viewer = viewer
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.openFile -> {
                open()
            }
            R.id.saveAs -> {
                saveAs()
            }
            R.id.about_app -> {
                aboutApp()
            }
        }
        viewer.close()
        item.isChecked = !item.isChecked
        println(".....................")
        return true
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

    var filePicker: ActivityResultLauncher<Intent>? = viewer.registerForActivityResult(
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
        dialog = Dialog(viewer)
        dialog.setContentView(R.layout.dialog_layout)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    override fun sentToEmail() {
        //TODO("Not yet implemented")
    }

    override fun exit() {
        //TODO("Not yet implemented")
    }

    override fun redo() {
        //  TODO("Not yet implemented")
    }

    override fun undo() {
        //TODO("Not yet implemented")
    }

    override fun cut() {
        // TODO("Not yet implemented")
    }

    override fun copy() {
        //TODO("Not yet implemented")
    }

    override fun insert() {
        // TODO("Not yet implemented")
    }

    override fun delete() {
        // TODO("Not yet implemented")
    }

    override fun find() {
        //TODO("Not yet implemented")
    }

    override fun replace() {
        //  TODO("Not yet implemented")
    }

    override fun selectAll() {
        // TODO("Not yet implemented")
    }

    override fun dateAndTime() {
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