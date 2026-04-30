<?php
$host = 'www.thyagoquintas.com.br:3306';
$db = 'engenharia_336';
$user = 'engenharia_336';
$pass = 'capivara';
$charset = 'utf8mb4';
$dsn  =  "mysql:host=$host;dbname=$db;charset=$charset";
$options = [
PDO::ATTR_ERRMODE	=> PDO::ERRMODE_EXCEPTION, PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC, PDO::ATTR_EMULATE_PREPARES => false,
];
try {
$pdo = new PDO($dsn, $user, $pass, $options);
$usuario = $_GET['usuario'] ?? '';
$senha = $_GET['senha'] ?? '';

// Query para verificar as credenciais
$sql = "SELECT USUARIO_ID as usuarioId,
USUARIO_NOME as usuarioNome,
USUARIO_EMAIL as usuarioEmail,
USUARIO_CPF as usuarioCpf,
USUARIO_TIPO as usuarioTipo
FROM USUARIO
WHERE USUARIO_EMAIL = :usuario
AND USUARIO_SENHA = :senha";



$stmt = $pdo->prepare($sql);
$stmt->execute(['usuario' => $usuario, 'senha' => $senha]);
$usuarios = $stmt->fetchAll();

header('Content-Type: application/json'); echo json_encode($usuarios);

} catch (\PDOException $e) {
echo "Erro de conexão: " . $e->getMessage(); exit;
}
?>
