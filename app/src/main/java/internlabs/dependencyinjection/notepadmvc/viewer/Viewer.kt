package internlabs.dependencyinjection.notepadmvc.viewer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import internlabs.dependencyinjection.notepadmvc.R

class Viewer : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}