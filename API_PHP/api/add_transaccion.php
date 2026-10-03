<?php
require_once 'db_connect.php';

$conexion = obtenerConexion();

// Datos enviados desde el formulario de Android (application/x-www-form-urlencoded)
$monto = isset($_POST['monto']) ? (float) $_POST['monto'] : 0;
$tipo = $_POST['tipo'] ?? '';
$categoriaId = isset($_POST['categoria_id']) ? (int) $_POST['categoria_id'] : 0;
$descripcion = $_POST['descripcion'] ?? null;
$fecha = $_POST['fecha'] ?? date('Y-m-d');
$esRecurrente = (isset($_POST['es_recurrente']) && $_POST['es_recurrente'] == '1') ? 1 : 0;

// --- Validaciones básicas de reglas de negocio ---
if ($monto <= 0) {
    echo json_encode(['success' => false, 'message' => 'El monto debe ser mayor a 0', 'data' => null]);
    exit;
}
if ($tipo !== 'ingreso' && $tipo !== 'gasto') {
    echo json_encode(['success' => false, 'message' => 'Tipo inválido (debe ser ingreso o gasto)', 'data' => null]);
    exit;
}
if ($categoriaId <= 0) {
    echo json_encode(['success' => false, 'message' => 'Debes seleccionar una categoría', 'data' => null]);
    exit;
}

$sql = "INSERT INTO transacciones (monto, tipo, categoria_id, descripcion, es_recurrente, fecha)
        VALUES (?, ?, ?, ?, ?, ?)";
$stmt = $conexion->prepare($sql);
$stmt->bind_param('dsisis', $monto, $tipo, $categoriaId, $descripcion, $esRecurrente, $fecha);

if ($stmt->execute()) {
    echo json_encode([
        'success' => true,
        'message' => 'Transacción guardada',
        'data' => [
            'id' => $stmt->insert_id,
            'monto' => $monto,
            'tipo' => $tipo,
            'categoria_id' => $categoriaId,
            'descripcion' => $descripcion,
            'es_recurrente' => $esRecurrente,
            'fecha' => $fecha
        ]
    ]);
} else {
    http_response_code(500);
    echo json_encode(['success' => false, 'message' => 'Error al guardar: ' . $stmt->error, 'data' => null]);
}

$stmt->close();
$conexion->close();
