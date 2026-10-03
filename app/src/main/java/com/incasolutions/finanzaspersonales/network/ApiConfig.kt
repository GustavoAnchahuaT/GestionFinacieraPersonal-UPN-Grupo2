package com.incasolutions.finanzaspersonales.network

/**
 * IMPORTANTE (prototipo académico):
 *
 * - Si pruebas en el EMULADOR de Android Studio, usa 10.0.2.2 (es un alias
 *   especial que el emulador traduce a "localhost" de TU computadora, donde
 *   corre XAMPP). NO uses "localhost" directamente, porque dentro del
 *   emulador "localhost" apunta al propio emulador, no a tu PC.
 *
 * - Si pruebas en un CELULAR FÍSICO conectado por USB o por la misma red
 *   Wi-Fi que tu PC, reemplaza la IP por la IP local de tu computadora
 *   (ej: "192.168.1.50"). La obtienes con `ipconfig` (Windows) en la
 *   sección "Adaptador de LAN inalámbrica Wi-Fi" -> "Dirección IPv4".
 *
 * - El puerto 80 es el que usa Apache por defecto en XAMPP. Si en tu XAMPP
 *   Apache corre en otro puerto (ej. 8080), agrégalo a la URL.
 */
object ApiConfig {

    // Cambia esta constante según el escenario de prueba (ver notas arriba)
    private const val HOST = "10.0.2.2"

    // Carpeta donde se copió el backend dentro de htdocs de XAMPP
    private const val BASE_PATH = "finanzas_personales_api/api"

    const val BASE_URL = "http://$HOST/$BASE_PATH/"
}
