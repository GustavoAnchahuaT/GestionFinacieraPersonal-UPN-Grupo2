<?php
require_once 'db_connect.php';

$conexion = obtenerConexion();

$id = isset($_POST['id']) ? (int) $_POST['id'] : 0;

if ($id <= 0) {
    echo json_encode(['success' => false, 'message' => 'ID inválido', 'data' => null]);
    exit;
}

$stmt = $conexion->prepare("DELETE FROM transacciones WHERE id = ?");
$stmt->bind_param('i', $id);

if ($stmt->execute()) {
    echo json_encode(['success' => true, 'message' => 'Transacción eliminada', 'data' => null]);
} else {
    http_response_code(500);
    echo json_encode(['success' => false, 'message' => 'Error al eliminar: ' . $stmt->error, 'data' => null]);
}

$stmt->close();
$conexion->close();
