package internlabs.dependencyinjection.notepadmvc.viewer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import internlabs.dependencyinjection.notepadmvc.controller.Controller
import internlabs.dependencyinjection.notepadmvc.databinding.ActivityMainBinding

class Viewer : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var controller: Controller

    init {
        controller = Controller(viewer = this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initListeners()

    }

    private fun initListeners() = with(binding) {
        btnNew.setOnClickListener(controller)
        btnOpen.setOnClickListener (controller)
    }

    fun setText(string: String) {
        binding.etMailField.setText(string)
    }

}