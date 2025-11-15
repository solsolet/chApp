package es.ua.eps.chapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
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
    private var serverSocket : ServerSocket? = null
    private var cliente : Socket? = null
    private var input: BufferedReader? = null
    private var output: PrintWriter? = null
    val socketServerPORT = 6000     // Puerto fijo del servidor
    private lateinit var adapter: ChatAdapter
    private val mensajes = mutableListOf<Mensaje>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        supportActionBar?.title = getString(R.string.servidor)
        initUI()
    }
    // INICIAMOS LA INTERFAZ
    // Infla el layout con servidor.xml
    private fun initUI(){
        bindings = ServidorBinding.inflate(layoutInflater)
        setContentView(bindings.root)

        // Configura recyclerView
        adapter = ChatAdapter(mensajes)
        bindings.rvChat.adapter = adapter
        bindings.rvChat.layoutManager = LinearLayoutManager(this)

        // botones con listeners
        bindings.bIniciarServidor.setOnClickListener { initServidor() }
        bindings.bEnviarMensajeServidor.setOnClickListener { enviarMensaje() }
    }
    // la funcion mas importante: pone en marcha el chat
    // Comunicacion del Socket
    fun initServidor(){
        // Abrimos hilo (corrutina) = evita bloqueo del main thread
        GlobalScope.launch(Dispatchers.IO) {
            try {
                serverSocket = ServerSocket(socketServerPORT) // escucha al puerto 6000
                withContext(Dispatchers.Main){             // Bloquea por completo el hilo hasta hacerse
                    bindings.tvEstadoServidor.append("Esperando cliente...")
                }
                cliente = serverSocket!!.accept()   // se bloquea hasta que el cliente se conecta
                // Abrir streams i/o
                input = BufferedReader(InputStreamReader(cliente!!.getInputStream()))
                output = PrintWriter(cliente!!.getOutputStream(), true)
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
        GlobalScope.launch(Dispatchers.IO) { // Debe estar en otro hilo o da conflicto por el onClick (evita NetworkOnMainThread)
            try {
                val msg = bindings.etMensajeServidor.text.toString() // Texto que has escrito en "Mensaje"
                if (msg.isNotEmpty()) {
                    output?.println(msg)    // envia al cliente

                    // Actualiza RecyclerView
                    withContext(Dispatchers.Main) {
                        adapter.agregarMensaje(Mensaje(msg, true)) // true = enviado
                        bindings.rvChat.scrollToPosition(mensajes.size - 1)
                        bindings.etMensajeServidor.setText("") // borra editTex mensaje
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    // Escucha mientras el cliente este conectadof
    private suspend fun escucharMensajes(){
        try {
            var line: String?
            while (cliente != null && cliente!!.isConnected) {
                line = input!!.readLine()
                if (line != null) {

                    withContext(Dispatchers.Main) {
                        adapter.agregarMensaje(Mensaje(line, false)) // false = recibido
                        bindings.rvChat.scrollToPosition(mensajes.size - 1)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun getPort() : Int {
        return socketServerPORT
    }
}