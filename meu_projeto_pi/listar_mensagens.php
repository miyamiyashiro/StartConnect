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
    $startup_id = $_GET['startup_id'] ?? 0;

    $sql = "SELECT
                M.MENSAGEM_ID as mensagemId,
                M.REMETENTE_ID as remetenteId,
                M.DESTINATARIO_ID as destinatarioId,
                M.STARTUP_ID as startupId,
                M.MENSAGEM_TEXTO as texto,
                M.DATA_ENVIO as dataEnvio,
                U.USUARIO_NOME as remetenteNome
            FROM MENSAGEM M
            INNER JOIN USUARIO U ON M.REMETENTE_ID = U.USUARIO_ID
            WHERE M.STARTUP_ID = :startup_id
              AND (M.REMETENTE_ID = :usuario_id1 OR M.DESTINATARIO_ID = :usuario_id2)
            ORDER BY M.DATA_ENVIO ASC";

    $stmt = $pdo->prepare($sql);
    $stmt->execute([
        'startup_id' => $startup_id,
        'usuario_id1' => $usuario_id,
        'usuario_id2' => $usuario_id
    ]);
    $mensagens = $stmt->fetchAll();

    header('Content-Type: application/json');
    echo json_encode($mensagens);

} catch (\PDOException $e) {
    header('Content-Type: application/json');
    echo json_encode(["success" => false, "message" => "Erro: " . $e->getMessage()]);
}
?>
