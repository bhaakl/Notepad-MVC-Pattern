package internlabs.dependencyinjection.notepadmvc.util

import android.content.Context
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.*
import android.print.pdf.PrintedPdfDocument
import java.io.FileOutputStream
import kotlin.math.ceil


class PrintDocument(private var text: String, context: Context, fonts: Paint) {

    private var context: Context
    private var fonts: Paint

    init {
        this.context = context
        this.fonts = fonts
    }

    fun doPrint() {
        context.also { context: Context ->
            val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
            val newAttributes =
                PrintAttributes.Builder().setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                    .build()
            val jobName = "NotepadMVC Document"
            printManager.print(jobName, MyPrintDocumentAdapter(context), newAttributes)
        }
    }


    inner class MyPrintDocumentAdapter(private var context: Context) : PrintDocumentAdapter() {
        private var rawString = ""
        private val stringLines: List<String> = text.lines()
        private val finalStringLines = mutableListOf<String>()
        private var pageHeight: Int = 0
        private var pageWidth: Int = 0
        private var myPdfDocument: PdfDocument? = null
        private var totalPages = 2
        private var titleBaseline = 1
        private val leftMargin = 1
        private var textHeight = 39
        private var itemsPerPage = 0

        override fun onLayout(
            oldAttributes: PrintAttributes,
            newAttributes: PrintAttributes,
            cancellationSignal: CancellationSignal,
            callback:
            PrintDocumentAdapter.LayoutResultCallback,
            metadata: Bundle,
        ) {
            myPdfDocument = PrintedPdfDocument(context, newAttributes)
            val height = newAttributes.mediaSize?.heightMils
            val width = newAttributes.mediaSize?.widthMils
            height?.let {
                pageHeight = it / 1000 * 72
                println(pageHeight)
            }
            width?.let {
                pageWidth = it / 1000 * 72
                println(pageWidth)
            }
            makeCorrectLines()
            totalPages = computePageCount()

            if (cancellationSignal.isCanceled) {
                callback.onLayoutCancelled()
                println("cancelled")
                return
            }
            if (totalPages > 0) {
                val builder =
                    PrintDocumentInfo.Builder("print.pdf").setContentType(
                        PrintDocumentInfo.CONTENT_TYPE_DOCUMENT
                    ).setPageCount(totalPages)
                val info = builder.build()
                callback.onLayoutFinished(info, true)
            } else {
                callback.onLayoutFailed("Page count is zero.")
            }

        }

        private fun makeCorrectLines() {
            for (index in stringLines.indices) {
                if (fonts.measureText(stringLines[index]).toInt() > pageWidth) {
                    rawString = stringLines[index]
                    while (fonts.measureText(rawString).toInt() > pageWidth) {
                        newLine(rawString)
                    }
                    finalStringLines.add(rawString)
                    rawString=""
                } else {
                    finalStringLines.add(stringLines[index])
                }
            }
        }

        override fun onWrite(
            pageRanges: Array<PageRange>,
            destination: ParcelFileDescriptor,
            cancellationSignal: CancellationSignal,
            callback: WriteResultCallback,
        ) {
            println("*******************ON WRITE*****************")
            for (i in 0 until totalPages) {
                if (pageInRange(pageRanges, i)) {
                    val newPage = PageInfo.Builder(pageWidth, pageHeight, i).create()
                    val page = myPdfDocument?.startPage(newPage)

                    if (cancellationSignal.isCanceled) {
                        callback.onWriteCancelled()
                        myPdfDocument?.close()
                        myPdfDocument = null
                        return
                    }
                    page?.let {
                        drawPage(it, i)
                    }
                    myPdfDocument?.finishPage(page)
                }
            }
            try {
                myPdfDocument?.writeTo(FileOutputStream(destination.fileDescriptor))

            } catch (e: Exception) {
                callback.onWriteFailed(e.toString())
                return
            } finally {
                myPdfDocument?.close()
                myPdfDocument = null
                /*rawString = ""
                stringLines
                finalStringLines.clear()
                pageHeight = null
                pageWidth: Int =
                myPdfDocument: Pd
                totalPages = 2
                titleBaseline = 1
                leftMargin = 1
                textHeight = 39
                itemsPerPage = 0
*/
            }
            callback.onWriteFinished(pageRanges)

        }

        private fun computePageCount(): Int {
            val bounds = Rect()

            fonts.getTextBounds(text, 0, text.length, bounds)
            textHeight = bounds.height()
            val printArea = pageHeight - titleBaseline * 2
            itemsPerPage = printArea / (textHeight + 1) // default item count for portrait mode
            /*if (pageSize != null) {
                if (!pageSize.isPortrait) {
                    // Six items per page in landscape orientation
                    itemsPerPage = 6
                }
            }*/

            // Determine number of print items
            val printItemCount: Int = finalStringLines.size
            return ceil((printItemCount / itemsPerPage.toDouble())).toInt()
        }

        private fun goToHardNewLine(beforeLast: String) {
            var tmp = ""
            beforeLast.forEach {
                tmp += it.toString()
                if (fonts.measureText(tmp).toInt() > (pageWidth - 40)) {
                    finalStringLines.add("$tmp -")
                    tmp = ""
                }
            }
            if (tmp.isNotEmpty()){
                finalStringLines.add(tmp)
            }
        }

        private fun newLine(s: String) {
            var beforeLast = ""
            beforeLast = if (s.contains(' ')) {
                s.substringBeforeLast(' ')
            } else {
                s
            }
            if (fonts.measureText(beforeLast).toInt() > pageWidth) {
                if (beforeLast.contains(' ')) {
                    newLine(beforeLast)
                } else {
                    goToHardNewLine(beforeLast)
                    rawString = rawString.substring(beforeLast.length)
                }
            } else {
                finalStringLines.add(beforeLast)
                rawString = rawString.substring(beforeLast.length+1)
            }
        }

        private fun pageInRange(pageRanges: Array<PageRange>, page: Int): Boolean {
            for (i in pageRanges.indices) {
                if (page >= pageRanges[i].start && page <= pageRanges[i].end)
                    return true
            }
            return false
        }

        private var index = 0
        private fun drawPage(page: PdfDocument.Page, pageNumber: Int) {
            val canvas = page.canvas
            var maxItem = 0
            titleBaseline = 1

            for (i in index until finalStringLines.size) {
                if (maxItem == itemsPerPage) {
                    break
                }
                canvas.drawText(finalStringLines[i], leftMargin.toFloat(),
                    (textHeight * titleBaseline).toFloat(), fonts)
                index++
                maxItem++
                titleBaseline++
            }

            canvas.drawText((pageNumber + 1).toString(),
                (page.info.pageWidth / 2).toFloat(),
                pageHeight.toFloat(),
                fonts)
        }
    }
}