package es.ua.eps.chapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import es.ua.eps.chapp.databinding.ClienteBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

class Cliente : AppCompatActivity() {
    private lateinit var bindings : ClienteBinding

    private var socket: Socket? = null
    private var output: PrintWriter? = null
    private var input: BufferedReader? = null
    private var socketServerPORT = 6000
    // Adaptador para mejora visual del chat
    private lateinit var adapter: ChatAdapter
    private val mensajes = mutableListOf<Mensaje>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        supportActionBar?.title = getString(R.string.cliente)
        initUI()

    }
    // INICIAMOS LA INTERFAZ
    // enlazamos con cliente.xml
    private fun initUI(){
        bindings = ClienteBinding.inflate(layoutInflater)
        setContentView(bindings.root)

        // Adaptador
        adapter = ChatAdapter(mensajes)
        bindings.rvChat.adapter = adapter
        bindings.rvChat.layoutManager = LinearLayoutManager(this)

        // botones
        bindings.bConectar.setOnClickListener { conectar() }
        bindings.bEnviarMensajeCliente.setOnClickListener { enviarMensaje() }
    }
    private fun conectar(){
        val ip = bindings.etIP.text.toString() // IP escrita por cliente
        // abrimos hilo (corrutina)
        GlobalScope.launch(Dispatchers.IO) {
            try {
                socket = Socket(ip, socketServerPORT) // intenta conectar el socket al servidor

                // Streams E/S
                input = BufferedReader(InputStreamReader(socket!!.getInputStream()))
                output = PrintWriter(socket!!.getOutputStream(), true)
                // Actualiza estado
                withContext(Dispatchers.Main) {
                    bindings.tvEstadoCliente.text = getString(R.string.conectadoExito)
                }
                launch(Dispatchers.Default) {   // Tarea en 2o plano (sin interrumpir)
                    escucharMensajes()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                launch(Dispatchers.Main){
                    bindings.tvEstadoCliente.append("\nError: ${e.message}")
                }
            }
        }
    }
    private suspend fun escucharMensajes(){
        try {
            var line: String?
            while (socket != null && socket!!.isConnected) {
                line = input?.readLine() // Lee lineas entrantes
                if (line != null) {
                    // Muestra en el RecyclerView
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
    // Funciona igual que en el servidor
    private fun enviarMensaje(){
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val msg = bindings.etMensajeCliente.text.toString()
                if (msg.isNotEmpty()) {
                    output?.println(msg)    // Envia texto
                    // Actualiza UI
                    withContext(Dispatchers.Main) {
                        adapter.agregarMensaje(Mensaje(msg, true)) // true = enviado
                        bindings.rvChat.scrollToPosition(mensajes.size - 1)
                        bindings.etMensajeCliente.setText("")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    // Cierra Conexiones y streams para evitar memoria fildrada o sockets abiertos
    override fun onDestroy() {
        super.onDestroy()
        GlobalScope.launch(Dispatchers.IO){}.cancel()
        input?.close()
        output?.close()
        socket?.close()
    }
}