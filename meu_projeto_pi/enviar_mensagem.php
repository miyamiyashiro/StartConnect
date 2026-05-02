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

    $remetente_id = $_POST['remetente_id'] ?? 0;
    $destinatario_id = $_POST['destinatario_id'] ?? 0;
    $startup_id = $_POST['startup_id'] ?? 0;
    $texto = $_POST['texto'] ?? '';

    if (empty($texto)) {
        header('Content-Type: application/json');
        echo json_encode(["success" => false, "message" => "Mensagem vazia"]);
        exit;
    }

    $sql = "INSERT INTO MENSAGEM (REMETENTE_ID, DESTINATARIO_ID, STARTUP_ID, MENSAGEM_TEXTO)
            VALUES (:remetente_id, :destinatario_id, :startup_id, :texto)";
    $stmt = $pdo->prepare($sql);
    $stmt->execute([
        'remetente_id' => $remetente_id,
        'destinatario_id' => $destinatario_id,
        'startup_id' => $startup_id,
        'texto' => $texto
    ]);

    header('Content-Type: application/json');
    echo json_encode(["success" => true, "message" => "Mensagem enviada"]);

} catch (\PDOException $e) {
    header('Content-Type: application/json');
    echo json_encode(["success" => false, "message" => "Erro: " . $e->getMessage()]);
}
?>
