package internlabs.dependencyinjection.notepadmvc.controller

import android.content.*
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.MenuItem
import internlabs.dependencyinjection.notepadmvc.viewer.Viewer

class Editor(_viewer: Viewer) : EditTasks {
    private var viewer: Viewer

    init {
        viewer = _viewer
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

}