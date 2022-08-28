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
            // Get a PrintManager instance
            val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
            val newAttributes =
                PrintAttributes.Builder().setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                    .setMinMargins(PrintAttributes.Margins(54, 40, 30, 30))
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
        private val titleBaseline = 1
        private val leftMargin = 1
        var textHeight = 39
        private val rightMargin = 30

        override fun onLayout(
            oldAttributes: PrintAttributes,
            newAttributes: PrintAttributes,
            cancellationSignal: CancellationSignal,
            callback:
            PrintDocumentAdapter.LayoutResultCallback,
            metadata: Bundle,
        ) {
            println(" onLayout()  $oldAttributes")
            println(" onLayout()  ${newAttributes}")
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
            totalPages = computePageCount(newAttributes)

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
            println(stringLines.toString())
            for (index in stringLines.indices){
                println(stringLines[index])
                println(fonts.measureText(stringLines[index]))
                if (fonts.measureText(stringLines[index]).toInt() > pageWidth ){
                    println("kkkkkkkkk")
                    rawString = stringLines[index]
                    while (fonts.measureText(rawString).toInt() > pageWidth){
                        println("remainText1   $rawString")
                        newLine(rawString)
                    }
                    finalStringLines.add(rawString)
                    //newLine(rawString)
                } else {
                    println("ttttttttt")
                    finalStringLines.add(stringLines[index])
                }
                println("finalStringLines    ${finalStringLines.toString()}")
            }
        }

        override fun onWrite(
            pageRanges: Array<PageRange>,
            destination: ParcelFileDescriptor,
            cancellationSignal: CancellationSignal,
            callback: WriteResultCallback,
            ) {
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

            }
            callback.onWriteFinished(pageRanges)

        }

        private fun computePageCount(printAttributes: PrintAttributes): Int {
            val bounds = Rect()
            fonts.getTextBounds(text, 0, text.length, bounds)
            textHeight = bounds.height()
            val printArea = pageHeight - titleBaseline * 2
            var itemsPerPage = printArea / textHeight // default item count for portrait mode

            println("textHeight  $textHeight")
            println("textHeight  ${printArea / textHeight}")
            val pageSize = printAttributes.mediaSize
            println("pageSze ${pageSize.toString()}")
            if (pageSize != null) {
                if (!pageSize.isPortrait) {
                    // Six items per page in landscape orientation
                    itemsPerPage = 6
                }
            }

            // Determine number of print items
            val printItemCount: Int = getPrintItemCount()

            return ceil((printItemCount / itemsPerPage.toDouble())).toInt()
        }

        private fun getPrintItemCount(): Int {
            println("/////// ${finalStringLines.size}")
            return finalStringLines.size
        }

        private fun newLine(s: String) {
            val beforeLast = s.substringBeforeLast(' ')
            println("beforeLast    $beforeLast")
            if (fonts.measureText(beforeLast).toInt() > pageWidth-60){
                println("beforeLast1    $beforeLast")
                newLine(beforeLast)
            }
            else {
                println("beforeLast2    $beforeLast")
                finalStringLines.add(beforeLast)
                rawString = rawString.substring(beforeLast.length)
                println("remainText   $rawString")
                //println("remainText0   ${rawString.substring(beforeLast.length)}")
            }
        }

        private fun pageInRange(pageRanges: Array<PageRange>, page: Int): Boolean {
            for (i in pageRanges.indices) {
                if (page >= pageRanges[i].start && page <= pageRanges[i].end)
                    return true
            }
            return false
        }

        private fun drawPage(page: PdfDocument.Page, pageNumber: Int) {
            val canvas = page.canvas

            println(fonts.measureText(text))

            canvas.drawText(text, leftMargin.toFloat(), (titleBaseline).toFloat(), fonts)
            canvas.drawText(text, leftMargin.toFloat(), (titleBaseline + textHeight).toFloat(), fonts)
            canvas.drawText(text, leftMargin.toFloat(), (titleBaseline + 80).toFloat(), fonts)
            canvas.drawText(text, leftMargin.toFloat(), (titleBaseline + 120).toFloat(), fonts)
            canvas.drawText(text, leftMargin.toFloat(), (titleBaseline + 160).toFloat(), fonts)
            canvas.drawText(pageNumber.toString(),
                leftMargin.toFloat(),
                (titleBaseline + 700).toFloat(),
                fonts)
        }
    }
}