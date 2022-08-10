package internlabs.dependencyinjection.notepadmvc.viewer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import internlabs.dependencyinjection.notepadmvc.controller.Controller
import internlabs.dependencyinjection.notepadmvc.databinding.ActivityViewerBinding

class Viewer : AppCompatActivity() {
    private lateinit var binding: ActivityViewerBinding
    private var controller: Controller

    init {
        controller = Controller(viewer = this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initListeners()
    }

    private fun initListeners() = with(binding) {
        btnNew.setOnClickListener(controller)
    }
}