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

    $usuarioId = $_GET['usuario_id'] ?? 0;

    $sql = "SELECT
                USUARIO_ID as usuarioId,
                USUARIO_NOME as usuarioNome,
                USUARIO_EMAIL as usuarioEmail,
                USUARIO_CPF as usuarioCpf,
                USUARIO_TIPO as usuarioTipo
            FROM USUARIO
            WHERE USUARIO_ID = :usuario_id";

    $stmt = $pdo->prepare($sql);
    $stmt->execute(['usuario_id' => $usuarioId]);
    $usuario = $stmt->fetch();

    header('Content-Type: application/json');
    if ($usuario) {
        echo json_encode($usuario);
    } else {
        echo json_encode(["error" => "Usuario nao encontrado"]);
    }

} catch (\PDOException $e) {
    echo json_encode([
        "success" => false,
        "message" => "Erro: " . $e->getMessage()
    ]);
}
?>
