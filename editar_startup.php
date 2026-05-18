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

    $startupId = $_POST['startup_id'] ?? '';
    $usuarioId = $_POST['usuario_id'] ?? '';
    $nome = $_POST['nome'] ?? '';
    $segmento = $_POST['segmento'] ?? '';
    $subtitulo = $_POST['subtitulo'] ?? 'Clique para ver mais';
    $tag1 = $_POST['tag1'] ?? '';
    $tag2 = $_POST['tag2'] ?? '';
    $tag3 = $_POST['tag3'] ?? '';
    $tag4 = $_POST['tag4'] ?? '';

    if (empty($startupId) || empty($usuarioId) || empty($nome) || empty($segmento)) {
        echo json_encode([
            "success" => false,
            "message" => "Preencha os campos obrigatorios"
        ]);
        exit;
    }

    $sql = "UPDATE STARTUP
            SET STARTUP_NOME = :nome,
                STARTUP_SEGMENTO = :segmento,
                STARTUP_SUBTITULO = :subtitulo,
                STARTUP_TAG1 = :tag1,
                STARTUP_TAG2 = :tag2,
                STARTUP_TAG3 = :tag3,
                STARTUP_TAG4 = :tag4
            WHERE STARTUP_ID = :startup_id
              AND USUARIO_ID = :usuario_id";

    $stmt = $pdo->prepare($sql);
    $stmt->execute([
        'startup_id' => $startupId,
        'usuario_id' => $usuarioId,
        'nome' => $nome,
        'segmento' => $segmento,
        'subtitulo' => $subtitulo,
        'tag1' => $tag1,
        'tag2' => $tag2,
        'tag3' => $tag3,
        'tag4' => $tag4
    ]);

    echo json_encode([
        "success" => true,
        "message" => "Startup atualizada com sucesso"
    ]);

} catch (Throwable $e) {
    echo json_encode([
        "success" => false,
        "message" => "Erro: " . $e->getMessage()
    ]);
    exit;
}
