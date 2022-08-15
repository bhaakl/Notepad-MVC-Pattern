package internlabs.dependencyinjection.notepadmvc.viewer

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.allViews
import androidx.core.view.get
import androidx.lifecycle.livedata.core.ktx.R
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

        /*navigationView.setNavigationItemSelectedListener { menuItem ->
            // Handle menu item selected
            //menuItem.isChecked = true
            drawerLayout.close()
            true
        }*/
    }
    fun close(){
        binding.drawerLayout.close()
    }

    fun setText(string: String) {

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