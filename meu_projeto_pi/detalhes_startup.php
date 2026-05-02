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

    $startup_id = $_GET['startup_id'] ?? 0;
    $usuario_id = $_GET['usuario_id'] ?? 0;

    $sql = "SELECT
                S.STARTUP_ID as startupId,
                S.USUARIO_ID as donoId,
                S.STARTUP_NOME as nome,
                S.STARTUP_SEGMENTO as segmento,
                S.STARTUP_SUBTITULO as subtitulo,
                S.STARTUP_TAG1 as tag1,
                S.STARTUP_TAG2 as tag2,
                S.STARTUP_TAG3 as tag3,
                S.STARTUP_TAG4 as tag4,
                U.USUARIO_NOME as donoNome,
                CASE WHEN F.FAVORITO_ID IS NOT NULL THEN 1 ELSE 0 END as favoritado
            FROM STARTUP S
            INNER JOIN USUARIO U ON S.USUARIO_ID = U.USUARIO_ID
            LEFT JOIN FAVORITO F ON F.STARTUP_ID = S.STARTUP_ID AND F.USUARIO_ID = :usuario_id
            WHERE S.STARTUP_ID = :startup_id";

    $stmt = $pdo->prepare($sql);
    $stmt->execute(['startup_id' => $startup_id, 'usuario_id' => $usuario_id]);
    $startup = $stmt->fetch();

    header('Content-Type: application/json');
    if ($startup) {
        echo json_encode($startup);
    } else {
        echo json_encode(["error" => "Startup nao encontrada"]);
    }

} catch (\PDOException $e) {
    header('Content-Type: application/json');
    echo json_encode(["success" => false, "message" => "Erro: " . $e->getMessage()]);
}
?>
