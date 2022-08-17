package internlabs.dependencyinjection.notepadmvc.viewer

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import internlabs.dependencyinjection.notepadmvc.controller.Controller
import internlabs.dependencyinjection.notepadmvc.databinding.ActivityViewerBinding


class Viewer : AppCompatActivity() {
    private lateinit var binding: ActivityViewerBinding
    private var controller: Controller
    lateinit var alertDialog: AlertDialog.Builder

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
        imageMenu.setNavigationOnClickListener {
            drawerLayout.open()
        }

        navigationView.setNavigationItemSelectedListener(controller)
    }

    fun getBinding(): ActivityViewerBinding {
        return binding
    }

    fun toastCopied() {
        Toast.makeText(this, "Copied", Toast.LENGTH_SHORT).show()
    }

    fun toastPasted() {
        Toast.makeText(this, "Pasted", Toast.LENGTH_SHORT).show()
    }

    fun toastCut() {
        Toast.makeText(this, "Cut Out", Toast.LENGTH_SHORT).show()
    }

    fun setText(strAdd: String) {
        if (strAdd.isEmpty()) {
            return
        }
        val old = binding.editText.text.toString()
        val cursor: Int = binding.editText.selectionStart
        val leftStr = old.substring(0, cursor)
        val rightStr = old.substring(cursor)
        if (binding.editText.text.isEmpty())
            binding.editText.setText(strAdd)
        else
            binding.editText.setText(String.format("%s%s%s", leftStr, strAdd, rightStr))
        binding.editText.setSelection(cursor + strAdd.length)
    }

    fun showAlertDialog(){
        alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("AboutApp")
            .setMessage("Project developers:Notepad MVC pattern\n" +
                    "Team: Dependency injection\n" +
                    "Медербек Шермаматов\n" +
                    "Умут Арпидинов\n" +
                    "Атабек Шамшидинов\n" +
                    "Байыш Бегалиев\n" +
                    "Мурат Жумалиев")
            .setCancelable(true)
            .setPositiveButton("Ok"){ dialogInterface, _ ->
                dialogInterface.cancel()
            }
        alertDialog.show()
    }
}