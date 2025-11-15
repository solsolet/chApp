package es.ua.eps.chapp

data class Mensaje(
    val texto: String,
    val esEnviado: Boolean   // true = este dispositivo envía, false = recibe
)
