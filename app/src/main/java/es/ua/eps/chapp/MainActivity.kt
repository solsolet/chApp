package es.ua.eps.chapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import es.ua.eps.chapp.databinding.MainActivityBinding

class MainActivity : AppCompatActivity() {
    private lateinit var bindings : MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        initUI()
    }
    private fun initUI(){
        bindings = MainActivityBinding.inflate(layoutInflater)
        setContentView(bindings.root)

        bindings.bAccedeServidor.setOnClickListener { irServidor() }
        bindings.bAccedeCliente.setOnClickListener { irCliente() }
    }
    private fun irServidor(){
        val ir = Intent(this@MainActivity, Servidor::class.java)
        startActivity(ir)
    }
    private fun irCliente(){
        val ir = Intent(this@MainActivity, Cliente::class.java)
        startActivity(ir)
    }

}