package internlabs.dependencyinjection.notepadmvc.controller

import android.view.MenuItem

interface EditTasks {
    // edit
    fun redo()
    fun undo()
    fun cut(textCut: String, startSelection: Int, endSelection: Int)
    fun copy(textCopied: String)
    fun paste(pasteItem: MenuItem): Boolean
    fun delete()
    fun find()
    fun replace()
    fun selectAll()
    fun dateAndTime()
}