package es.ua.eps.chapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import es.ua.eps.chapp.databinding.ClienteBinding

class Cliente : AppCompatActivity() {
    private lateinit var bindings : ClienteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        initUI()

    }
    private fun initUI(){
        bindings = ClienteBinding.inflate(layoutInflater)
        setContentView(bindings.root)

        bindings.bConectar.setOnClickListener { conectar() }
    }
    fun conectar(){

    }
}