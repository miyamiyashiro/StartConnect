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

    $usuarioId = $_POST['usuario_id'] ?? '';
    $nome = trim($_POST['nome'] ?? '');
    $email = trim($_POST['email'] ?? '');

    if (empty($usuarioId) || empty($nome) || empty($email)) {
        echo json_encode([
            "success" => false,
            "message" => "Preencha nome e email"
        ]);
        exit;
    }

    $sqlVerifica = "SELECT USUARIO_ID FROM USUARIO WHERE USUARIO_EMAIL = :email AND USUARIO_ID <> :usuario_id";
    $stmtVerifica = $pdo->prepare($sqlVerifica);
    $stmtVerifica->execute([
        'email' => $email,
        'usuario_id' => $usuarioId
    ]);

    if ($stmtVerifica->fetch()) {
        echo json_encode([
            "success" => false,
            "message" => "Este email ja esta em uso"
        ]);
        exit;
    }

    $sql = "UPDATE USUARIO
            SET USUARIO_NOME = :nome,
                USUARIO_EMAIL = :email
            WHERE USUARIO_ID = :usuario_id";

    $stmt = $pdo->prepare($sql);
    $stmt->execute([
        'nome' => $nome,
        'email' => $email,
        'usuario_id' => $usuarioId
    ]);

    echo json_encode([
        "success" => true,
        "message" => "Perfil atualizado com sucesso"
    ]);
} catch (Throwable $e) {
    echo json_encode([
        "success" => false,
        "message" => "Erro: " . $e->getMessage()
    ]);
}
?>
