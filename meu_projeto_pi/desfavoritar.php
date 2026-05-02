<?php
$host = 'www.thyagoquintas.com.br:3306';
$db = 'engenharia_336';
$user = 'engenharia_336';
$pass = 'capivara';
$charset = 'utf8mb4';

$dsn = "mysql:host=$host;dbname=$db;charset=$charset";
$options = [
    PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
    PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC
];

try {
    $pdo = new PDO($dsn, $user, $pass, $options);

    $usuario_id = $_POST['usuario_id'] ?? 0;
    $startup_id = $_POST['startup_id'] ?? 0;

    $sql = "DELETE FROM FAVORITO WHERE USUARIO_ID = :usuario_id AND STARTUP_ID = :startup_id";
    $stmt = $pdo->prepare($sql);
    $stmt->execute(['usuario_id' => $usuario_id, 'startup_id' => $startup_id]);

    header('Content-Type: application/json');
    echo json_encode(["success" => true, "message" => "Startup desfavoritada com sucesso"]);

} catch (\PDOException $e) {
    header('Content-Type: application/json');
    echo json_encode(["success" => false, "message" => "Erro: " . $e->getMessage()]);
}
?>
