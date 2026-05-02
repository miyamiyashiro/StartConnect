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

    // Busca conversas unicas agrupadas por startup
    $sql = "SELECT
                S.STARTUP_ID as startupId,
                S.STARTUP_NOME as startupNome,
                M.MENSAGEM_TEXTO as ultimaMensagem,
                M.DATA_ENVIO as dataUltimaMensagem,
                CASE
                    WHEN M.REMETENTE_ID = :uid1 THEN M.DESTINATARIO_ID
                    ELSE M.REMETENTE_ID
                END as outroUsuarioId,
                U2.USUARIO_NOME as outroUsuarioNome
            FROM MENSAGEM M
            INNER JOIN STARTUP S ON M.STARTUP_ID = S.STARTUP_ID
            INNER JOIN (
                SELECT STARTUP_ID, MAX(DATA_ENVIO) as MAX_DATA
                FROM MENSAGEM
                WHERE REMETENTE_ID = :uid2 OR DESTINATARIO_ID = :uid3
                GROUP BY STARTUP_ID
            ) LATEST ON M.STARTUP_ID = LATEST.STARTUP_ID AND M.DATA_ENVIO = LATEST.MAX_DATA
            INNER JOIN USUARIO U2 ON U2.USUARIO_ID = CASE
                WHEN M.REMETENTE_ID = :uid4 THEN M.DESTINATARIO_ID
                ELSE M.REMETENTE_ID
            END
            WHERE M.REMETENTE_ID = :uid5 OR M.DESTINATARIO_ID = :uid6
            ORDER BY M.DATA_ENVIO DESC";

    $stmt = $pdo->prepare($sql);
    $stmt->execute([
        'uid1' => $usuario_id,
        'uid2' => $usuario_id,
        'uid3' => $usuario_id,
        'uid4' => $usuario_id,
        'uid5' => $usuario_id,
        'uid6' => $usuario_id
    ]);
    $chats = $stmt->fetchAll();

    header('Content-Type: application/json');
    echo json_encode($chats);

} catch (\PDOException $e) {
    header('Content-Type: application/json');
    echo json_encode(["success" => false, "message" => "Erro: " . $e->getMessage()]);
}
?>
