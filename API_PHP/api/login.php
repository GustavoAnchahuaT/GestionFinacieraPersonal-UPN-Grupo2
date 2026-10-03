<?php
// ============================================================
// XAMPP — reemplaza tu login.php actual en:
// C:\xampp\htdocs\finanzas_personales_api\api\login.php
//
// CAMBIO: la contraseña ya NO se compara en texto plano. Se
// compara contra su versión hasheada con SHA-256 directamente en
// la consulta SQL (SHA2(?, 256)), usando la misma función que ya
// aplicamos a la tabla "usuarios" en
// migrar_passwords_sha256.sql. Así, la base de datos nunca vuelve
// a tener que guardar ni comparar una contraseña legible.
// ============================================================

require_once 'db_connect.php';

$conexion = obtenerConexion();

$usuario = isset($_POST['usuario']) ? trim($_POST['usuario']) : '';
$clave   = isset($_POST['clave'])   ? trim($_POST['clave'])   : '';

if ($usuario === '' || $clave === '') {
    echo json_encode(["success" => false, "message" => "Faltan datos"]);
    $conexion->close();
    exit;
}

$stmt = $conexion->prepare("SELECT id FROM usuarios WHERE usuario = ? AND clave = SHA2(?, 256)");
$stmt->bind_param("ss", $usuario, $clave);
$stmt->execute();
$resultado = $stmt->get_result();

if ($resultado->num_rows > 0) {
    echo json_encode(["success" => true, "message" => null]);
} else {
    echo json_encode(["success" => false, "message" => "Usuario o clave incorrectos"]);
}

$stmt->close();
$conexion->close();
