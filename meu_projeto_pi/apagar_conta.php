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

    // Apagar startups do usuario primeiro
    $sql1 = "DELETE FROM STARTUP WHERE USUARIO_ID = :id";
    $stmt1 = $pdo->prepare($sql1);
    $stmt1->execute(['id' => $usuario_id]);

    // Apagar usuario
    $sql2 = "DELETE FROM USUARIO WHERE USUARIO_ID = :id";
    $stmt2 = $pdo->prepare($sql2);
    $stmt2->execute(['id' => $usuario_id]);

    header('Content-Type: application/json');
    if ($stmt2->rowCount() > 0) {
        echo json_encode(["success" => true, "message" => "Conta apagada com sucesso"]);
    } else {
        echo json_encode(["success" => false, "message" => "Usuario nao encontrado"]);
    }

} catch (\PDOException $e) {
    header('Content-Type: application/json');
    echo json_encode(["success" => false, "message" => "Erro: " . $e->getMessage()]);
}
?>
