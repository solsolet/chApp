package es.ua.eps.chapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import es.ua.eps.chapp.databinding.ServidorBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.DataInput
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket

class Servidor : AppCompatActivity(){

    private lateinit var bindings: ServidorBinding
    private lateinit var serverSocket : ServerSocket
    private lateinit var cliente : Socket
    private lateinit var input: BufferedReader
    private lateinit var output: PrintWriter
    val socketServerPORT = 6000     // Puerto fijo del servidor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        initUI()

    }
    private fun initUI(){
        bindings = ServidorBinding.inflate(layoutInflater)
        setContentView(bindings.root)

        bindings.bIniciarServidor.setOnClickListener { initServidor() }
        bindings.bEnviarMensajeServidor.setOnClickListener { enviarMensaje() }
    }
    fun initServidor(){
        // Abrimos hilo (corrutina)
        GlobalScope.launch(Dispatchers.IO) {   // Tareas Entrada/Salida
            try {
                serverSocket = ServerSocket(socketServerPORT)
                withContext(Dispatchers.Main){        // Bloquea por completo el hilo hasta hacerse
                    bindings.tvEstadoServidor.append("Esperando cliente...")
                }
                cliente = serverSocket.accept()
                // Abrir strams i/o
                input = BufferedReader(InputStreamReader(cliente.getInputStream()))
                output = PrintWriter(cliente.getOutputStream(), true)
                withContext(Dispatchers.Main){
                    bindings.tvEstadoServidor.append("Se ha conectado Cliente")
                }
                launch(Dispatchers.Default) {   // Tarea en 2o plano (sin interrumpir)
                    escucharMensajes()
                }
            }
            catch (e: Exception){
                e.printStackTrace()
                launch(Dispatchers.Main){
                    bindings.tvEstadoServidor.append("Error: ${e.message}")
                }
            }
        }
    }
    private fun enviarMensaje(){
        val msg = bindings.etMensajeServidor.text.toString() // Texto que has escrito en "Mensaje"
        if (msg.isNotEmpty()) {
            output.println(msg)
            bindings.tvChat.append("Servidor: $msg\n")      // Se añade al textView del Chat
            bindings.etMensajeServidor.setText("")          // Borra editText del mensaje
        }
    }
    private suspend fun escucharMensajes(){
        try {
            var line: String?
            while (cliente != null && cliente!!.isConnected) {
                line = input.readLine()
                if (line != null) {
                    withContext(Dispatchers.Main) {
                        bindings.tvChat.append("Cliente: $line\n")
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                bindings.tvChat.append("Conexión cerrada\n")
            }
        }
    }
    //public fun getPort() : Int{ return socketServerPort }

}