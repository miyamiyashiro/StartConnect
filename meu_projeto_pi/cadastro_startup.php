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

    $usuarioId = $_POST['usuario_id'] ?? '';
    $nome = $_POST['nome'] ?? '';
    $segmento = $_POST['segmento'] ?? '';
    $subtitulo = $_POST['subtitulo'] ?? 'Clique para ver mais';
    $tag1 = $_POST['tag1'] ?? null;
    $tag2 = $_POST['tag2'] ?? null;
    $tag3 = $_POST['tag3'] ?? null;
    $tag4 = $_POST['tag4'] ?? null;

    if (empty($usuarioId) || empty($nome) || empty($segmento)) {
        echo json_encode([
            "success" => false,
            "message" => "Preencha os campos obrigatórios"
        ]);
        exit;
    }

    $sql = "INSERT INTO STARTUP
        (USUARIO_ID, STARTUP_NOME, STARTUP_SEGMENTO, STARTUP_SUBTITULO, STARTUP_TAG1, STARTUP_TAG2, STARTUP_TAG3, STARTUP_TAG4)
        VALUES
        (:usuario_id, :nome, :segmento, :subtitulo, :tag1, :tag2, :tag3, :tag4)";

    $stmt = $pdo->prepare($sql);
    $stmt->execute([
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
        "message" => "Startup cadastrada com sucesso"
    ]);

} catch (\PDOException $e) {
    echo json_encode([
        "success" => false,
        "message" => "Erro: " . $e->getMessage()
    ]);
}
?>
