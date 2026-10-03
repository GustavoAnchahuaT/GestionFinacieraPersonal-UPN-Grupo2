<?php
require_once 'db_connect.php';

$conexion = obtenerConexion();

$fechaInicio = isset($_GET['fecha_inicio']) ? $_GET['fecha_inicio'] : null;
$fechaFin    = isset($_GET['fecha_fin'])    ? $_GET['fecha_fin']    : null;

if ($fechaInicio !== null && $fechaFin !== null) {
    // ---- Modo RANGO (dia especifico, ultimos 3/6 meses, año completo, etc.) ----
    $sql = "SELECT t.id, t.monto, t.tipo, t.categoria_id, c.nombre AS categoria_nombre,
                   t.descripcion, t.es_recurrente, t.fecha
            FROM transacciones t
            INNER JOIN categorias c ON c.id = t.categoria_id
            WHERE t.fecha BETWEEN ? AND ?
            ORDER BY t.fecha DESC, t.id DESC";
    $stmt = $conexion->prepare($sql);
    $stmt->bind_param('ss', $fechaInicio, $fechaFin);
} else {
    // ---- Modo MES/AÑO (comportamiento original, mes actual por defecto) ----
    $mes = isset($_GET['mes']) ? (int) $_GET['mes'] : (int) date('n');
    $anio = isset($_GET['anio']) ? (int) $_GET['anio'] : (int) date('Y');

    $sql = "SELECT t.id, t.monto, t.tipo, t.categoria_id, c.nombre AS categoria_nombre,
                   t.descripcion, t.es_recurrente, t.fecha
            FROM transacciones t
            INNER JOIN categorias c ON c.id = t.categoria_id
            WHERE MONTH(t.fecha) = ? AND YEAR(t.fecha) = ?
            ORDER BY t.fecha DESC, t.id DESC";
    $stmt = $conexion->prepare($sql);
    $stmt->bind_param('ii', $mes, $anio);
}

$stmt->execute();
$resultado = $stmt->get_result();

$transacciones = [];
while ($fila = $resultado->fetch_assoc()) {
    $transacciones[] = [
        'id' => (int) $fila['id'],
        'monto' => (float) $fila['monto'],
        'tipo' => $fila['tipo'],
        'categoria_id' => (int) $fila['categoria_id'],
        'categoria_nombre' => $fila['categoria_nombre'],
        'descripcion' => $fila['descripcion'],
        'es_recurrente' => (int) $fila['es_recurrente'],
        'fecha' => $fila['fecha']
    ];
}

echo json_encode([
    'success' => true,
    'message' => null,
    'data' => $transacciones
]);

$stmt->close();
$conexion->close();
