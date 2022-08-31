package internlabs.dependencyinjection.notepadmvc.controller

import android.content.*
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.documentfile.provider.DocumentFile
import com.google.android.material.navigation.NavigationView
import internlabs.dependencyinjection.notepadmvc.R
import internlabs.dependencyinjection.notepadmvc.util.BMooreMatchText
import internlabs.dependencyinjection.notepadmvc.util.PrintDocument
import internlabs.dependencyinjection.notepadmvc.util.TextEditor
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
                copy()
            }
            R.id.paste -> {
                paste(item)
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
                viewer.showToast("File extension is not supported")
            }
        }
    }

    private fun getText(context: Context, uri: Uri): CharArray? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val bfr = BufferedReader(InputStreamReader(inputStream))
            val buffer = StringBuilder()
            var buf: CharArray? = CharArray(10)
            var numRead: Int
            while (bfr.read(buf).also { numRead = it } != -1) {
                val readData = String(buf!!, 0, numRead)
                buffer.append(readData)
                buf = CharArray(1024)
            }
            inputStream?.close()
            bfr.close()
            buffer.toString().toCharArray()
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
        val clipboardManager =
            viewer.applicationContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        if (TextEditor.cut(viewer.getEditText(), clipboardManager))
            viewer.showToast("Cut Out")
    }

    override fun copy() {
        val clipboardManager =
            viewer.applicationContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        if (TextEditor.copy(viewer.getEditText(), clipboardManager))
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2)
            viewer.showToast("Copied")
    }

    override fun paste(pasteItem: MenuItem) {
        val clipboardManager =
            viewer.applicationContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val pasteData = TextEditor.paste(clipboardManager, pasteItem)
        if (pasteData.isNotEmpty()) {
            viewer.setTextForEditor(pasteData)
            viewer.showToast("Pasted")
        } else {
            Log.e(ContentValues.TAG, "Clipboard contains an invalid data type")
        }
    }

    override fun delete() {
        if(TextEditor.delete(viewer.getEditText()))
            viewer.showToast("Deleted")
    }

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
        val editTextFind = view.findViewById<EditText>(R.id.findWhat)

        var posD = 0
        mPositiveButton.setOnClickListener {
            if (viewer.getEditText().text.isNotEmpty()) {
                val matchingAnswer = BMooreMatchText.search(
                    viewer.getEditText().text.toString().toCharArray(),
                    editTextFind.text.toString().toCharArray()
                )
                if (matchingAnswer.isNotEmpty()) {
                    if (posD == matchingAnswer.size || posD > matchingAnswer.size ) posD = 0
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
        if(content != ""){
            val printDocument = PrintDocument(content,viewer)
            printDocument.doPrint()
        }
        else {
            viewer.showToast("Document is empty")
        }
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
            viewer.showToast("File not found")
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

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
}