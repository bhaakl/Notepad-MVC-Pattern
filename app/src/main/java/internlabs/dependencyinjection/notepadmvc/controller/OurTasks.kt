package internlabs.dependencyinjection.notepadmvc.controller

interface OurTasks {

    //region start
    fun new()
    fun open()
    fun save(fileName: String)
    fun saveAs()
    fun print()
    fun recent()
    fun aboutApp()
    fun sentToEmail()
    fun exit()

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