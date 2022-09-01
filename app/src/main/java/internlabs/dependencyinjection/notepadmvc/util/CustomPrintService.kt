package internlabs.dependencyinjection.notepadmvc.util

import android.printservice.PrintJob
import android.printservice.PrintService
import android.printservice.PrinterDiscoverySession

class CustomPrintService : PrintService() {
    private var customPrinterDiscoverySession: CustomPrinterDiscoverySession? = null

    override fun onCreatePrinterDiscoverySession(): PrinterDiscoverySession? {
        //println("*******************CPS    onCreatePrinterDiscoverySession()")
        return customPrinterDiscoverySession
    }

    override fun onRequestCancelPrintJob(printJob: PrintJob?) {
        //println("*******************CPS    onRequestCancelPrintJob()")
    }

    override fun onPrintJobQueued(printJob: PrintJob) {
       // println("*******************CPS    onPrintJobQueued")
        printJob.start()
        printJob.setProgress(0f)
        printJob.complete()
    }
}