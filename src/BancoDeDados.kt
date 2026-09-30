import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class BancoDeDados {

    // Configurações de acesso ao MySQL
    // Se o seu MySQL tiver senha (como no Workbench), insira na variável 'senha' abaixo.
    private val url = "jdbc:mysql://localhost:3306/gerenciador_escolar?useSSL=false&serverTimezone=UTC"
    private val usuario = "root"
    private val senha = "julio123"

    // Estabelece a conexão com o banco
    private fun conectar(): Connection {
        return DriverManager.getConnection(url, usuario, senha)
    }

    // 1. Método de Teste de Conexão
    fun testarConexao(): Boolean {
        return try {
            conectar().use { conn ->
                !conn.isClosed
            }
        } catch (e: Exception) {
            println("❌ [ERRO DE CONEXÃO]: ${e.message}")
            e.printStackTrace()
            false
        }
    }

    // 2. Cadastrar novo aluno no banco de dados
    fun cadastrarAluno(nome: String): Boolean {
        val sql = "INSERT INTO alunos (nome) VALUES (?)"
        return try {
            conectar().use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setString(1, nome)
                    stmt.executeUpdate()
                    true
                }
            }
        } catch (e: SQLException) {
            println("❌ [ERRO AO CADASTRAR ALUNO]: ${e.message}")
            e.printStackTrace()
            false
        }
    }

    // 3. Salvar ou Atualizar as 3 notas do aluno
    fun salvarNotas(nomeAluno: String, n1: Double, n2: Double, n3: Double): Boolean {
        val idAluno = buscarIdPorNome(nomeAluno) ?: return false
        val possuiNotas = temNotasCadastradas(idAluno)

        val sql = if (possuiNotas) {
            "UPDATE notas SET nota1 = ?, nota2 = ?, nota3 = ? WHERE aluno_id = ?"
        } else {
            "INSERT INTO notas (nota1, nota2, nota3, aluno_id) VALUES (?, ?, ?, ?)"
        }

        return try {
            conectar().use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setDouble(1, n1)
                    stmt.setDouble(2, n2)
                    stmt.setDouble(3, n3)
                    stmt.setInt(4, idAluno)
                    stmt.executeUpdate()
                    true
                }
            }
        } catch (e: SQLException) {
            println("❌ [ERRO AO SALVAR NOTAS]: ${e.message}")
            e.printStackTrace()
            false
        }
    }

    // 4. Deletar aluno (a chave estrangeira ON DELETE CASCADE apaga as notas automaticamente)
    fun deletarAluno(nome: String): Boolean {
        val sql = "DELETE FROM alunos WHERE nome = ?"
        return try {
            conectar().use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setString(1, nome)
                    stmt.executeUpdate() > 0
                }
            }
        } catch (e: SQLException) {
            println("❌ [ERRO AO DELETAR ALUNO]: ${e.message}")
            e.printStackTrace()
            false
        }
    }

    // 5. Listar todos os alunos com suas respectivas notas
    fun listarAlunosComNotas(): List<DadosAluno> {
        val lista = mutableListOf<DadosAluno>()
        val sql = """
            SELECT a.nome, n.nota1, n.nota2, n.nota3 
            FROM alunos a 
            LEFT JOIN notas n ON a.id = n.aluno_id
            ORDER BY a.nome ASC
        """.trimIndent()

        try {
            conectar().use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    val rs = stmt.executeQuery()
                    while (rs.next()) {
                        val nome = rs.getString("nome")
                        val n1 = rs.getObject("nota1") as? Double
                        val n2 = rs.getObject("nota2") as? Double
                        val n3 = rs.getObject("nota3") as? Double

                        lista.add(DadosAluno(nome, n1, n2, n3))
                    }
                }
            }
        } catch (e: SQLException) {
            println("❌ [ERRO AO LISTAR ALUNOS]: ${e.message}")
            e.printStackTrace()
        }
        return lista
    }

    // --- MÉTODOS AUXILIARES ---

    private fun buscarIdPorNome(nome: String): Int? {
        val sql = "SELECT id FROM alunos WHERE nome = ?"
        try {
            conectar().use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setString(1, nome)
                    val rs = stmt.executeQuery()
                    if (rs.next()) return rs.getInt("id")
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return null
    }

    private fun temNotasCadastradas(alunoId: Int): Boolean {
        val sql = "SELECT id FROM notas WHERE aluno_id = ?"
        try {
            conectar().use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setInt(1, alunoId)
                    val rs = stmt.executeQuery()
                    return rs.next()
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return false
    }
}

// Classe de modelo para transferência dos dados para a tabela
data class DadosAluno(
    val nome: String,
    val nota1: Double?,
    val nota2: Double?,
    val nota3: Double?
)