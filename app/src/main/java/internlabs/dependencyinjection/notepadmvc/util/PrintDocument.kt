package internlabs.dependencyinjection.notepadmvc.util

import android.os.Bundle

import java.io.FileOutputStream

import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.content.Context
import android.print.PrintDocumentInfo
import android.print.pdf.PrintedPdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.graphics.pdf.PdfDocument
import java.lang.Exception
import android.graphics.Color
import android.graphics.Paint
import android.print.PrintManager
import internlabs.dependencyinjection.notepadmvc.R
import internlabs.dependencyinjection.notepadmvc.viewer.Viewer


class PrintDocument(private var text: String, viewer: Viewer) {

        private val context = viewer



          fun doPrint() {
              context.also { context: Context ->
                  // Get a PrintManager instance
                  val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
                  // Set job name, which will be displayed in the print queue
                  val jobName = "${context.getString(R.string.app_name)} Document"
                  // Start a print job, passing in a PrintDocumentAdapter implementation
                  // to handle the generation of a print document
                  printManager.print(jobName, MyPrintDocumentAdapter(context), null)
              }
          }



    inner class MyPrintDocumentAdapter(private var context: Context) : PrintDocumentAdapter() {
        private var pageHeight: Int = 0
        private var pageWidth: Int = 0
        private var myPdfDocument: PdfDocument? = null
        private var totalPages = 1


        private fun pageInRange(pageRanges: Array<PageRange>, page: Int): Boolean {
            for (i in pageRanges.indices) {
                if (page >= pageRanges[i].start && page <= pageRanges[i].end)
                    return true
            }
            return false
        }

        private fun drawPage(page: PdfDocument.Page, pageNumber: Int) {
            var pagenum = pageNumber
            val canvas = page.canvas

          //make sure page numbers start at 1

            val titleBaseline = 72
            val lefMargin = 54

            val paint = Paint()
            paint.color = Color.BLACK
            paint.textSize = 40f
            paint.textSize = 20f

            canvas.drawText(text, lefMargin.toFloat(), (titleBaseline + 35).toFloat(), paint)

        }


        override fun onLayout(
            oldAttributes: PrintAttributes,
            newAttributes: PrintAttributes,
            cancellationSignal: CancellationSignal,
            callback:
            PrintDocumentAdapter.LayoutResultCallback,
            metadata: Bundle
        ) {
            myPdfDocument = PrintedPdfDocument(context, newAttributes)

            val height = newAttributes.mediaSize?.heightMils
            val width = newAttributes.mediaSize?.widthMils

            height?.let {
                pageHeight = it / 1000 * 72
            }
            width?.let {
                pageWidth = it / 1000 * 72
            }
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

        override fun onWrite(
            pageRanges: Array<PageRange>,
            destination: ParcelFileDescriptor,
            cancellationSignal: CancellationSignal,
            callback: WriteResultCallback

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
    }}