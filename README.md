# 🏫 Sistema de Gerenciamento Escolar

Um sistema desktop para gerenciamento de alunos e notas, desenvolvido em **Kotlin** com interface gráfica em **Java Swing** e persistência de dados no **MySQL**.

O projeto permite cadastrar alunos, lançar e atualizar notas, realizar o cálculo automático de médias e acompanhar a situação acadêmica de cada estudante em tempo real.

---

## 🚀 Funcionalidades

* **Cadastro de Alunos:** Adiciona novos alunos ao banco de dados com validação de duplicidade.
* **Lançamento de Notas:** Registra e atualiza 3 notas por aluno.
* **Cálculo Automático de Média:** Determina automaticamente a média aritmética e define o status:
    * **Aprovado:** Média >= 7.0
    * **Recuperação:** Média < 7.0 (exibe a pontuação restante necessária)
* **Remoção de Alunos:** Exclui o cadastro do aluno e remove em cascata suas notas associadas (`ON DELETE CASCADE`).
* **Interface Navegável:** Layout dinâmico utilizando `CardLayout` para transição entre Tela Inicial e Gerenciador.
* **Teste de Conexão:** Ferramenta integrada na interface para verificar a integridade da comunicação com o banco MySQL.

---

## 🛠️ Tecnologias Utilizadas

* **Linguagem:** [Kotlin](https://kotlinlang.org/) (JVM)
* **Interface Gráfica:** Java Swing (`JFrame`, `JTable`, `CardLayout`)
* **Banco de Dados:** [MySQL](https://www.mysql.com/)
* **Driver JDBC:** `mysql-connector-j`
* **IDE:** IntelliJ IDEA

---

## 🗄️ Estrutura do Banco de Dados

O banco de dados `gerenciador_escolar` é composto por duas tabelas relacionadas por chave estrangeira:

```sql
CREATE DATABASE IF NOT EXISTS gerenciador_escolar;
USE gerenciador_escolar;

CREATE TABLE IF NOT EXISTS alunos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS notas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    aluno_id INT NOT NULL,
    nota1 DOUBLE NOT NULL,
    nota2 DOUBLE NOT NULL,
    nota3 DOUBLE NOT NULL,
    FOREIGN KEY (aluno_id) REFERENCES alunos(id) ON DELETE CASCADE
);