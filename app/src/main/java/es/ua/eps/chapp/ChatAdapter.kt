package es.ua.eps.chapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChatAdapter(private val mensajes: MutableList<Mensaje>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    companion object {
        private const val TIPO_ENVIADO = 1
        private const val TIPO_RECIBIDO = 2
    }

    override fun getItemViewType(position: Int): Int {
        return if (mensajes[position].esEnviado) TIPO_ENVIADO else TIPO_RECIBIDO
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TIPO_ENVIADO) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_mensaje_enviado, parent, false)
            EnviadoViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_mensaje_recibido, parent, false)
            RecibidoViewHolder(view)
        }
    }

    override fun getItemCount() = mensajes.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val msg = mensajes[position]
        if (holder is EnviadoViewHolder) holder.bind(msg.texto)
        else if (holder is RecibidoViewHolder) holder.bind(msg.texto)
    }

    fun agregarMensaje(m: Mensaje) {
        mensajes.add(m)
        notifyItemInserted(mensajes.size - 1)
    }

    class EnviadoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tv = itemView.findViewById<TextView>(R.id.tvMensajeEnviado)
        fun bind(texto: String) { tv.text = texto }
    }

    class RecibidoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tv = itemView.findViewById<TextView>(R.id.tvMensajeRecibido)
        fun bind(texto: String) { tv.text = texto }
    }
}