🚀 StartConnect - Projeto Integrador
Bem-vindos ao repositório do StartConnect! Este projeto é uma plataforma de networking para startups e investidores.

📌 Status do Projeto
Atualmente, o fluxo de Login está funcional e conectado ao banco de dados local. O design das telas de Intro e Login foi baseado no protótipo do Figma.

🛠️ Como configurar o projeto no seu computador
Para que o app funcione e o login valide os usuários, sigam estes passos:

1. Preparação do Servidor (Banco de Dados)
O app depende de um servidor local para "conversar" com o banco de dados.

Abram o XAMPP e deem Start no Apache e no MySQL.

Certifiquem-se de que o arquivo login.php está dentro da pasta C:/xampp/htdocs/meu_projeto_pi/.

O banco de dados deve ter a tabela usuarios com as colunas usuario_email e usuario_senha.

2. Ajuste do IP (Passo Crítico!)
Como estamos usando o servidor local (seu PC), o Android Studio precisa saber o endereço da sua máquina na rede.

Abram o terminal (CMD) e digitem ipconfig.

Anotem o seu Endereço IPv4 (ex: 192.168.0.XX).

No Android Studio, abram o arquivo LoginActivity.kt.

Localizem a linha do Retrofit e atualizem o IP:

Kotlin
.baseUrl("http://SEU_NOVO_IP_AQUI/meu_projeto_pi/")
3. Rodando o App
Conectem o celular via USB (com Depuração USB ativa) ou usem o Emulador.

Cliquem no Play (triângulo verde) no topo do Android Studio.

📂 Estrutura de Telas Atuais
IntroActivity: Tela inicial com logo e botões de Entrar/Cadastrar.

LoginActivity: Tela de autenticação integrada ao MySQL via Retrofit.

MainActivity: Tela de destino após o login bem-sucedido.

📝 Próximas Tarefas (Backlog)
[ ] Implementar a lógica do botão Cadastrar.

[ ] Criar a tela de perfil do usuário.

[ ] Refinar o layout da MainActivity (Home).

[ ] Ajustar as fontes para Montserrat conforme o Figma.

Dica: Sempre que fizerem uma mudança importante, não esqueçam de dar o Commit e Push pelo GitHub Desktop para as outras verem!
