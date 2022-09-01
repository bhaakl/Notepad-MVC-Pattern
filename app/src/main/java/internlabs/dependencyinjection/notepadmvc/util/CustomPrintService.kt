package internlabs.dependencyinjection.notepadmvc.util

import android.printservice.PrintJob
import android.printservice.PrintService
import android.printservice.PrinterDiscoverySession

class CustomPrintService : PrintService() {
    private var customPrinterDiscoverySession: CustomPrinterDiscoverySession? = null

    override fun onCreatePrinterDiscoverySession(): PrinterDiscoverySession? {
        return customPrinterDiscoverySession
    }

    override fun onRequestCancelPrintJob(printJob: PrintJob?) {
    }

    override fun onPrintJobQueued(printJob: PrintJob) {
        printJob.start()
        printJob.setProgress(0f)
        printJob.complete()
    }
}