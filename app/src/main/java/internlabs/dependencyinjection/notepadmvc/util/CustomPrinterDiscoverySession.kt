package internlabs.dependencyinjection.notepadmvc.util

import android.print.PrinterId
import android.printservice.PrinterDiscoverySession

class CustomPrinterDiscoverySession : PrinterDiscoverySession() {

    override fun onStartPrinterDiscovery(priorityList: List<PrinterId?>) {
    }

    override fun onStopPrinterDiscovery() {
    }

    override fun onValidatePrinters(printerIds: List<PrinterId?>) {
    }

    override fun onStartPrinterStateTracking(printerId: PrinterId) {
    }

    override fun onStopPrinterStateTracking(printerId: PrinterId) {
    }

    override fun onDestroy() {
    }
}