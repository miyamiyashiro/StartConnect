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

    $usuario_id = $_GET['usuario_id'] ?? 0;

    $sql = "SELECT
                S.STARTUP_ID as startupId,
                S.STARTUP_NOME as nome,
                S.STARTUP_SEGMENTO as segmento,
                S.STARTUP_SUBTITULO as subtitulo,
                S.STARTUP_TAG1 as tag1,
                S.STARTUP_TAG2 as tag2,
                S.STARTUP_TAG3 as tag3,
                S.STARTUP_TAG4 as tag4
            FROM FAVORITO F
            INNER JOIN STARTUP S ON F.STARTUP_ID = S.STARTUP_ID
            WHERE F.USUARIO_ID = :usuario_id
            ORDER BY F.DATA_FAVORITO DESC";

    $stmt = $pdo->prepare($sql);
    $stmt->execute(['usuario_id' => $usuario_id]);
    $favoritos = $stmt->fetchAll();

    header('Content-Type: application/json');
    echo json_encode($favoritos);

} catch (\PDOException $e) {
    header('Content-Type: application/json');
    echo json_encode(["success" => false, "message" => "Erro: " . $e->getMessage()]);
}
?>
