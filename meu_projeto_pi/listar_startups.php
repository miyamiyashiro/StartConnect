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

    $usuarioId = $_GET['usuario_id'] ?? null;

    if ($usuarioId) {
        $sql = "SELECT
                    STARTUP_ID as startupId,
                    STARTUP_NOME as nome,
                    STARTUP_SEGMENTO as segmento,
                    STARTUP_SUBTITULO as subtitulo,
                    STARTUP_TAG1 as tag1,
                    STARTUP_TAG2 as tag2,
                    STARTUP_TAG3 as tag3,
                    STARTUP_TAG4 as tag4
                FROM STARTUP
                WHERE USUARIO_ID = :usuario_id";
        $stmt = $pdo->prepare($sql);
        $stmt->execute(['usuario_id' => $usuarioId]);
    } else {
        $sql = "SELECT
                    STARTUP_ID as startupId,
                    STARTUP_NOME as nome,
                    STARTUP_SEGMENTO as segmento,
                    STARTUP_SUBTITULO as subtitulo,
                    STARTUP_TAG1 as tag1,
                    STARTUP_TAG2 as tag2,
                    STARTUP_TAG3 as tag3,
                    STARTUP_TAG4 as tag4
                FROM STARTUP";
        $stmt = $pdo->query($sql);
    }

    $startups = $stmt->fetchAll();

    header('Content-Type: application/json');
    echo json_encode($startups);

} catch (\PDOException $e) {
    echo json_encode([
        "success" => false,
        "message" => "Erro: " . $e->getMessage()
    ]);
    exit;
}
?>

