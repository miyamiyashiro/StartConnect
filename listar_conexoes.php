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

    $usuarioId = $_GET['usuario_id'] ?? '';

    if (empty($usuarioId)) {
        echo json_encode([]);
        exit;
    }

    $sql = "SELECT
                U.USUARIO_ID AS usuarioId,
                U.USUARIO_NOME AS usuarioNome,
                U.USUARIO_TIPO AS usuarioTipo,
                COUNT(F.FAVORITO_ID) AS totalFavoritos
            FROM FAVORITO F
            INNER JOIN STARTUP S ON S.STARTUP_ID = F.STARTUP_ID
            INNER JOIN USUARIO U ON U.USUARIO_ID = F.USUARIO_ID
            WHERE S.USUARIO_ID = :usuario_id
            GROUP BY U.USUARIO_ID, U.USUARIO_NOME, U.USUARIO_TIPO
            ORDER BY totalFavoritos DESC, U.USUARIO_NOME ASC";

    $stmt = $pdo->prepare($sql);
    $stmt->execute([
        'usuario_id' => $usuarioId
    ]);

    echo json_encode($stmt->fetchAll());
} catch (Throwable $e) {
    echo json_encode([
        "success" => false,
        "message" => "Erro: " . $e->getMessage()
    ]);
}
?>
