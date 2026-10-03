<?php
/**
 * db_connect.php
 * Conexión centralizada a MySQL (XAMPP).
 *
 * Credenciales por defecto de XAMPP: usuario "root", sin contraseña.
 * Si en tu MySQL Workbench configuraste otro usuario/clave, cámbialos aquí.
 */

header('Access-Control-Allow-Origin: *');
header('Content-Type: application/json; charset=UTF-8');
header('Access-Control-Allow-Methods: GET, POST');

define('DB_HOST', 'localhost');
define('DB_USER', 'root');
define('DB_PASS', '');           // vacío por defecto en XAMPP
define('DB_NAME', 'finanzas_personales');

function obtenerConexion(): mysqli {
    $conexion = new mysqli(DB_HOST, DB_USER, DB_PASS, DB_NAME);

    if ($conexion->connect_error) {
        http_response_code(500);
        echo json_encode([
            'success' => false,
            'message' => 'Error de conexión a la base de datos: ' . $conexion->connect_error,
            'data' => null
        ]);
        exit;
    }

    $conexion->set_charset('utf8mb4');
    return $conexion;
}
