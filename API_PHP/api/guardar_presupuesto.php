<?php
header('Content-Type: application/json');
require_once 'db_connect.php';
$conexion = obtenerConexion();

$anio  = isset($_POST['anio'])  ? (int)$_POST['anio']  : 0;
$mes   = isset($_POST['mes'])   ? (int)$_POST['mes']   : 0;
$monto = isset($_POST['monto']) ? (float)$_POST['monto'] : 0;

if ($anio <= 0 || $mes <= 0 || $monto <= 0) {
    echo json_encode(["success" => false, "message" => "Datos inválidos", "data" => null]);
    $conexion->close();
    exit;
}

// INSERT ... ON DUPLICATE KEY UPDATE: si ya existe presupuesto para ese mes/año, lo actualiza
$stmt = $conexion->prepare(
    "INSERT INTO presupuestos (anio, mes, monto) VALUES (?, ?, ?)
     ON DUPLICATE KEY UPDATE monto = VALUES(monto)"
);
$stmt->bind_param("iid", $anio, $mes, $monto);

if ($stmt->execute()) {
    echo json_encode([
        "success" => true,
        "message" => null,
        "data" => ["anio" => $anio, "mes" => $mes, "monto" => $monto]
    ]);
} else {
    echo json_encode(["success" => false, "message" => "No se pudo guardar el presupuesto", "data" => null]);
}

$stmt->close();
$conexion->close();
