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

    $usuario_id = $_POST['usuario_id'] ?? 0;
    $senha_atual = $_POST['senha_atual'] ?? '';
    $nova_senha = $_POST['nova_senha'] ?? '';

    $sqlCheck = "SELECT USUARIO_ID FROM USUARIO WHERE USUARIO_ID = :id AND USUARIO_SENHA = :senha";
    $stmtCheck = $pdo->prepare($sqlCheck);
    $stmtCheck->execute(['id' => $usuario_id, 'senha' => $senha_atual]);

    if (!$stmtCheck->fetch()) {
        header('Content-Type: application/json');
        echo json_encode(["success" => false, "message" => "Senha atual incorreta"]);
        exit;
    }

    $sql = "UPDATE USUARIO SET USUARIO_SENHA = :nova_senha WHERE USUARIO_ID = :id";
    $stmt = $pdo->prepare($sql);
    $stmt->execute(['nova_senha' => $nova_senha, 'id' => $usuario_id]);

    header('Content-Type: application/json');
    echo json_encode(["success" => true, "message" => "Senha alterada com sucesso"]);

} catch (\PDOException $e) {
    header('Content-Type: application/json');
    echo json_encode(["success" => false, "message" => "Erro: " . $e->getMessage()]);
}
?>
