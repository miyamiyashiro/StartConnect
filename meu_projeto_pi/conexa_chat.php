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
    $outroUsuarioId = $_POST['outro_usuario_id'] ?? '';
    $startupId = $_POST['startup_id'] ?? '';

    if (empty($usuarioId) || empty($outroUsuarioId) || empty($startupId)) {
        echo json_encode([
            "success" => false,
            "message" => "Nao se pode mais conversar com este usuario"
        ]);
        exit;
    }

    $sql = "SELECT 1
            FROM STARTUP S
            INNER JOIN FAVORITO F ON F.STARTUP_ID = S.STARTUP_ID
            WHERE S.STARTUP_ID = :startup_id
              AND (
                    (S.USUARIO_ID = :usuario_id AND F.USUARIO_ID = :outro_usuario_id)
                 OR (S.USUARIO_ID = :outro_usuario_id AND F.USUARIO_ID = :usuario_id)
              )
            LIMIT 1";

    $stmt = $pdo->prepare($sql);
    $stmt->execute([
        'startup_id' => $startupId,
        'usuario_id' => $usuarioId,
        'outro_usuario_id' => $outroUsuarioId
    ]);

    if ($stmt->fetch()) {
        echo json_encode([
            "success" => true,
            "message" => "Conexao ativa"
        ]);
    } else {
        echo json_encode([
            "success" => false,
            "message" => "Nao se pode mais conversar com este usuario"
        ]);
    }
} catch (Throwable $e) {
    echo json_encode([
        "success" => false,
        "message" => "Erro: " . $e->getMessage()
    ]);
}
?>
