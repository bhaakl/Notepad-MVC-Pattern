package internlabs.dependencyinjection.notepadmvc.util

import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipboardManager
import android.view.MenuItem
import android.widget.EditText

class TextEditor {

    companion object {

        fun copy(editText: EditText, clipboardManager: ClipboardManager): Boolean {
            val startSelection: Int = editText.selectionStart
            val endSelection: Int = editText.selectionEnd
            val cTextCopied: String = editText.text.toString()
                .substring(startSelection, endSelection)
            return if (cTextCopied.isEmpty()) false
            else {
                clipboardManager.setPrimaryClip(ClipData.newPlainText("", cTextCopied))
                editText.setSelection(editText.selectionEnd)
                true
            }
        }

        fun cut(editText: EditText, clipboardManager: ClipboardManager): Boolean {
            val startSelection: Int = editText.selectionStart
            val endSelection: Int = editText.selectionEnd
            val selectedText: String = editText.text.toString()
                .substring(startSelection, endSelection)
            return if (selectedText.isEmpty()) false
            else {
                clipboardManager.setPrimaryClip(ClipData.newPlainText("", selectedText))
                editText.text =
                    editText.text.replace(startSelection, endSelection, "")
                editText.setSelection(startSelection)
                true
            }
        }

        fun paste(clipboardManager: ClipboardManager, pasteItem: MenuItem): String {
            var pasteData: String = ""

            // Если в буфере обмена нет данных, отключаем пункт меню вставки.
            // Если он содержит данные, решите, можете ли вы обрабатывать данные.
            pasteItem.isEnabled = when {
                !clipboardManager.hasPrimaryClip() -> {
                    false
                }
                !(clipboardManager.primaryClipDescription?.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN))!! -> {
                    // Это отключает пункт меню вставки, так как в буфере обмена есть данные, но это не обычный текст
                    false
                }
                else -> {
                    // Это включает пункт меню вставки, так как буфер обмена содержит обычный текст.
                    true
                }
            }
            val item = clipboardManager.primaryClip?.getItemAt(0)

            // Gets the clipboard as text.
            pasteData = item?.text.toString()
            return pasteData
        }

        fun delete(editText: EditText): Boolean {
            val startSelection: Int = editText.selectionStart
            val endSelection: Int = editText.selectionEnd
            val selectedText: String = editText.text.toString()
                .substring(startSelection, endSelection)
            return if (selectedText.isEmpty()) false
            else {
                editText.text =
                    editText.text.replace(startSelection, endSelection, "")
                editText.setSelection(startSelection)
                true
            }
        }
    }
}
