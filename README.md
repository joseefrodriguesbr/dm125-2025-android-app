# Pós-Graduação em Desenvolvimento Mobile e Cloud Computing – Inatel
## DM125 – Desenvolvimento de aplicativos em Kotlin para Android com Firebase

## Projeto Final da Disciplina

### 👤 Autor: 
José Enderson Ferreira Rodrigues   
jose.rodrigues@pg.inatel.br, jose.e.f.rodrigues.br@gmail.com

## 📌 Implementação
Aplicativo android para gerenciamento de tarefas

### Requisitos atendidos:
✅ Validação do formulário antes de enviar ao servidor. O título é mandatório e a data e hora, se informados, deve estar em um formato correto.

✅ Ajuste em FormActivity para ler os detalhes da tarefa usando o id passado pela MainActivity. Ajuste em Repository e Service.

✅ Criação de configuração para escolher o formato da data que será exibida no card. Formato com números: 01/01/2021 ou formato com o nome do mês: 01 de janeiro de 2024. O ItemViewHolder deverá ler o valor dessa configuração e exibir a data de acordo.

✅ Alteração da cor do card de acordo com a data da tarefa:
* Tarefas sem data ou que ainda estão no prazo: azul
* Tarefas vencidas (ontem ou antes): vermelha
* Tarefas que vencem hoje: amarela
* Terefas completas: verde

✅ Solicitar para o usuário uma confirmação antes de apagar uma tarefa.

✅ Implementação de uma nova forma de login de acordo com a documentação do Firebase Authenticator. Implementação deLogin através do número de telefone.

✅ Customização de aplicativo com um novo ícone e cores.

✅ Customização de layout do card

## 📌 Arquitetura final do projeto 
<img style="margin-right: 30px" src="./DiagramaProjetoFinal.jpg" width="600px" alt="Diagrama de Classes"/><br>  


## 🛠️ IDE
- **Android Studio Narwhal 4 Feature Drop | 2025.1.4**

## 💻 Linguagem
- **Kotlin**
