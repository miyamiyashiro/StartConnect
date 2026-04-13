# 🚀 StartConnect - Projeto Integrador

Bem-vindos ao repositório do **StartConnect**! Este projeto é uma plataforma de networking voltada para conectar startups e investidores, desenvolvida para o Projeto Integrador.

---

## 🛠️ Como rodar o projeto no seu computador

Como o app utiliza um servidor local para o banco de dados, cada uma de vocês precisará configurar o ambiente para que o login funcione:

### 1. Preparação do Servidor (XAMPP)
* Abram o **XAMPP Control Panel**.
* Deem **Start** nos módulos **Apache** e **MySQL**.
* Certifiquem-se de que a pasta `meu_projeto_pi` (com o arquivo `login.php`) está dentro de: `C:\xampp\htdocs\`.

### 2. Configuração do Banco de Dados
* O banco de dados deve se chamar `banco_pi` (ou o nome que definimos).
* A tabela deve ser `usuarios` com os campos: `usuario_email` e `usuario_senha`.

### 3. Atualização do IP (Muito Importante!) ⚠️
Sempre que mudamos de rede ou de computador, o endereço do servidor muda. Para o Android Studio "achar" o banco de dados de vocês:
1.  Abram o **CMD** (Prompt de Comando) e digitem `ipconfig`.
2.  Procurem por **Endereço IPv4** (ex: `192.168.0.XX`).
3.  No Android Studio, abram o arquivo `LoginActivity.kt`.
4.  Substituam o IP na linha da `baseUrl` pelo IP atual da sua máquina:
    > `.baseUrl("http://SEU_IP_AQUI/meu_projeto_pi/")`

---

## 📂 O que já foi feito
* **IntroActivity**: Tela de boas-vindas com o logo e botões de navegação.
* **LoginActivity**: Tela de login com integração ao Retrofit e validação via PHP/MySQL.
* **Design**: Layout atualizado seguindo o protótipo do Figma (cores, botões arredondados e sombras).
* **Permissões**: Configuração de Internet e Cleartext Traffic adicionadas ao Manifest.

---

## 📝 Próximos Passos (Backlog)
- [ ] Implementar a lógica da tela de **Cadastro**.
- [ ] Finalizar a estilização da frase com a fonte **Montserrat**.
- [ ] Criar a interface principal da **MainActivity** após o login.

---

## 👩‍💻 Integrantes do Grupo
* Isabelle Vitoria Matos
* Laís Lívia
* Luana Miyashiro
* Yasmin Carolina

---
*Dica: Sempre que terminarem uma alteração, façam o **Commit** e o **Push** pelo GitHub Desktop para manter o projeto atualizado para todas!*
