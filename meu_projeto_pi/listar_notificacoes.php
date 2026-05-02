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

    // Busca mensagens recebidas agrupadas por startup (ultima mensagem de cada)
    $sql = "SELECT
                M.MENSAGEM_ID as mensagemId,
                M.REMETENTE_ID as remetenteId,
                M.STARTUP_ID as startupId,
                M.MENSAGEM_TEXTO as texto,
                M.DATA_ENVIO as dataEnvio,
                S.STARTUP_NOME as startupNome,
                U.USUARIO_NOME as remetenteNome
            FROM MENSAGEM M
            INNER JOIN STARTUP S ON M.STARTUP_ID = S.STARTUP_ID
            INNER JOIN USUARIO U ON M.REMETENTE_ID = U.USUARIO_ID
            WHERE M.DESTINATARIO_ID = :usuario_id
            ORDER BY M.DATA_ENVIO DESC";

    $stmt = $pdo->prepare($sql);
    $stmt->execute(['usuario_id' => $usuario_id]);
    $notificacoes = $stmt->fetchAll();

    header('Content-Type: application/json');
    echo json_encode($notificacoes);

} catch (\PDOException $e) {
    header('Content-Type: application/json');
    echo json_encode(["success" => false, "message" => "Erro: " . $e->getMessage()]);
}
?>
