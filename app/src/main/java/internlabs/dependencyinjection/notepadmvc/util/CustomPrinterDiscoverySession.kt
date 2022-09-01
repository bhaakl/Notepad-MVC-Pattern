package internlabs.dependencyinjection.notepadmvc.util

import android.print.PrinterId
import android.printservice.PrinterDiscoverySession

class CustomPrinterDiscoverySession : PrinterDiscoverySession() {

    override fun onStartPrinterDiscovery(priorityList: List<PrinterId?>) {
       /* println("**************CPDS  onStartPrinterDiscovery()")
        println("**************CPDS  onStartPrinterDiscovery()$priorityList")*/
    }

    override fun onStopPrinterDiscovery() {
        //println("**************CPDS  onStopPrinterDiscovery()")
    }

    override fun onValidatePrinters(printerIds: List<PrinterId?>) {
        //println("**************CPDS  onValidatePrinters()")
    }

    override fun onStartPrinterStateTracking(printerId: PrinterId) {
        //println("**************CPDS  onStartPrinterStateTracking()")
    }

    override fun onStopPrinterStateTracking(printerId: PrinterId) {
       // println("**************CPDS  onStopPrinterStateTracking()")
    }

    override fun onDestroy() {
       // println("**************CPDS  onDestroy()")
    }
}