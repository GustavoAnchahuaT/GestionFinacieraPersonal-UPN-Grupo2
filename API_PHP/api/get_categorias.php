<?php
require_once 'db_connect.php';

$conexion = obtenerConexion();

$resultado = $conexion->query("SELECT id, nombre, tipo FROM categorias ORDER BY nombre ASC");

$categorias = [];
while ($fila = $resultado->fetch_assoc()) {
    $categorias[] = [
        'id' => (int) $fila['id'],
        'nombre' => $fila['nombre'],
        'tipo' => $fila['tipo']
    ];
}

echo json_encode([
    'success' => true,
    'message' => null,
    'data' => $categorias
]);

$conexion->close();
