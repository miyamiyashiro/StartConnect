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

    $nome = $_POST['nome'] ?? '';
    $email = $_POST['email'] ?? '';
    $senha = $_POST['senha'] ?? '';
    $tipo = $_POST['tipo'] ?? '';

    if (empty($nome) || empty($email) || empty($senha) || empty($tipo)) {
        echo json_encode([
            "success" => false,
            "message" => "Preencha todos os campos"
        ]);
        exit;
    }

    $sqlVerifica = "SELECT USUARIO_ID FROM USUARIO WHERE USUARIO_EMAIL = :email";
    $stmtVerifica = $pdo->prepare($sqlVerifica);
    $stmtVerifica->execute(['email' => $email]);

    if ($stmtVerifica->fetch()) {
        echo json_encode([
            "success" => false,
            "message" => "Email já cadastrado"
        ]);
        exit;
    }

    $sql = "INSERT INTO USUARIO (USUARIO_NOME, USUARIO_EMAIL, USUARIO_SENHA, USUARIO_TIPO)
            VALUES (:nome, :email, :senha, :tipo)";

    $stmt = $pdo->prepare($sql);
    $stmt->execute([
        'nome' => $nome,
        'email' => $email,
        'senha' => $senha,
        'tipo' => $tipo
    ]);

    echo json_encode([
        "success" => true,
        "message" => "Cadastro realizado com sucesso"
    ]);

} catch (\PDOException $e) {
    echo json_encode([
        "success" => false,
        "message" => "Erro: " . $e->getMessage()
    ]);
    exit;
}
?>

