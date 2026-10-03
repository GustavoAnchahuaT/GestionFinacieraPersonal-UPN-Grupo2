<?php
header('Content-Type: application/json');
require_once 'db_connect.php';
$conexion = obtenerConexion();

$anio = isset($_GET['anio']) ? (int)$_GET['anio'] : 0;
$mes  = isset($_GET['mes'])  ? (int)$_GET['mes']  : 0;

if ($anio <= 0 || $mes <= 0) {
    echo json_encode(["success" => false, "message" => "Faltan parámetros anio/mes", "data" => null]);
    $conexion->close();
    exit;
}

$stmt = $conexion->prepare("SELECT id, anio, mes, monto FROM presupuestos WHERE anio = ? AND mes = ?");
$stmt->bind_param("ii", $anio, $mes);
$stmt->execute();
$resultado = $stmt->get_result();

if ($fila = $resultado->fetch_assoc()) {
    echo json_encode([
        "success" => true,
        "message" => null,
        "data" => [
            "id" => (int)$fila['id'],
            "anio" => (int)$fila['anio'],
            "mes" => (int)$fila['mes'],
            "monto" => (float)$fila['monto']
        ]
    ]);
} else {
    // No es un error: simplemente no se ha definido presupuesto todavía
    echo json_encode(["success" => true, "message" => "Sin presupuesto definido", "data" => null]);
}

$stmt->close();
$conexion->close();
