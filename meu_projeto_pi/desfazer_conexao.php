<?php
header('Content-Type: application/json; charset=utf-8');

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

    $usuarioId = $_POST['usuario_id'] ?? '';
    $conectadoId = $_POST['conectado_id'] ?? '';

    if (empty($usuarioId) || empty($conectadoId)) {
        echo json_encode([
            "success" => false,
            "message" => "Dados invalidos para desfazer conexao"
        ]);
        exit;
    }

    $sql = "DELETE F FROM FAVORITO F
            INNER JOIN STARTUP S ON S.STARTUP_ID = F.STARTUP_ID
            WHERE S.USUARIO_ID = :usuario_id
              AND F.USUARIO_ID = :conectado_id";

    $stmt = $pdo->prepare($sql);
    $stmt->execute([
        'usuario_id' => $usuarioId,
        'conectado_id' => $conectadoId
    ]);

    if ($stmt->rowCount() > 0) {
        echo json_encode([
            "success" => true,
            "message" => "Conexao desfeita com sucesso"
        ]);
    } else {
        echo json_encode([
            "success" => false,
            "message" => "Nenhuma conexao encontrada para desfazer"
        ]);
    }
} catch (Throwable $e) {
    echo json_encode([
        "success" => false,
        "message" => "Erro: " . $e->getMessage()
    ]);
}
?>
