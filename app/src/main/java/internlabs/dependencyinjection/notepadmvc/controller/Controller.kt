package internlabs.dependencyinjection.notepadmvc.controller

import android.view.View
import internlabs.dependencyinjection.notepadmvc.R
import internlabs.dependencyinjection.notepadmvc.viewer.Viewer
import java.io.File


class Controller(viewer: Viewer) : OurTasks, View.OnClickListener {
    private var viewer: Viewer

    init {
        this.viewer = viewer
    }


    override fun onClick(v: View) {
        when(v.id) {
            R.id.btn_new -> {
                new()
            }
            R.id.btn_save -> {

            }
        }
    }

    override fun new() {
        val textFromFile =  File("fileName.txt")
            .bufferedReader()
            .use { it.readText(); }
    }

    private fun isOk(file: File): Boolean {
        val fileName = file.name
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
            val extensionOfFile = fileName.substring(fileName.lastIndexOf(".") + 1)
            // System.out.println(extensionOfFile);
            if (extensionOfFile == "ntp" || extensionOfFile == "kt" || extensionOfFile == "swift" || extensionOfFile == "java") {
                val erkin = file.length().toInt()
                val max = 5629273
                return if (erkin < max) {
                    true
                } else {
                    false
                }
            }
        }
        return false
    }

    override fun open(fileName: String) {
        TODO("Not yet implemented")
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