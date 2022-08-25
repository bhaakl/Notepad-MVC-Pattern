package internlabs.dependencyinjection.notepadmvc.controller

import android.view.MenuItem

interface OurTasks {

    //region start
    fun new()
    fun open()
    fun save()
    fun saveAs()
    fun print()
    fun recent()
    fun aboutApp()
    fun sentToEmail()
    fun exit()


    // edit
    fun redo()
    fun undo()
    fun cut(textCut: String, startSelection: Int, endSelection: Int)
    fun copy(textCopied: String)
    fun paste(pasteItem: MenuItem): Boolean
    fun insert()
    fun delete()
    fun find()
    fun replace()
    fun selectAll()
    fun dateAndTime()

    // View
    fun zoomIn()
    fun zoomOut()
    fun zoomDefault()

    // Font
    fun size()
    fun fontFamily()
    fun makeBold()
    fun makeItalic()
    fun makeCursive()
    fun makeUnderlined()
    fun makeCrossedOut()
    fun makeSubscript()
    fun makeSuperscript()
    fun textColor()
    fun textBackground()

    // paragraph

    fun alignLeft()
    fun alignRight()
    fun alignLeftAndLeft()
    fun lineSpace()
    fun letterSpace()

    // Theme
    fun changeTheme()
}