import java.awt.*
import javax.swing.*
import javax.swing.table.DefaultTableModel

fun main() {
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())

    SwingUtilities.invokeLater {
        JanelaEscolar().isVisible = true
    }
}

class JanelaEscolar : JFrame("Sistema de Gerenciamento Escolar - com MySQL") {

    private val db = BancoDeDados()

    private val gerenciadorDeTelas = CardLayout()
    private val painelPrincipal = JPanel(gerenciadorDeTelas)

    private val colunas = arrayOf("Aluno", "Nota 1", "Nota 2", "Nota 3", "Média", "Situação")
    private val modeloTabela = DefaultTableModel(colunas, 0)
    private val tabela = JTable(modeloTabela)

    private val txtNomeAluno = JTextField(15)
    private val txtAlunoNota = JTextField(15)
    private val txtNota1 = JTextField(4)
    private val txtNota2 = JTextField(4)
    private val txtNota3 = JTextField(4)

    init {
        title = "Sistema de Gerenciamento Escolar - MySQL"
        defaultCloseOperation = EXIT_ON_CLOSE
        size = Dimension(850, 580)
        setLocationRelativeTo(null)

        painelPrincipal.add(criarPaginaInicial(), "INICIAL")
        painelPrincipal.add(criarPaginaGerenciador(), "GERENCIADOR")

        add(painelPrincipal)

        atualizarTabela()
    }

    private fun criarPaginaInicial(): JPanel {
        val painel = JPanel(GridBagLayout())
        painel.background = Color(245, 245, 245)

        val conteudos = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            isOpaque = false
        }

        val lblTitulo = JLabel("Bem-vindo ao Sistema Escolar").apply {
            font = Font("Arial", Font.BOLD, 22)
            alignmentX = Component.CENTER_ALIGNMENT
        }

        val lblSubtitulo = JLabel("Conectado ao Banco de Dados MySQL").apply {
            font = Font("Arial", Font.PLAIN, 14)
            alignmentX = Component.CENTER_ALIGNMENT
        }

        val btnEntrar = JButton("Acessar o Sistema").apply {
            font = Font("Arial", Font.BOLD, 14)
            alignmentX = Component.CENTER_ALIGNMENT
            addActionListener {
                gerenciadorDeTelas.show(painelPrincipal, "GERENCIADOR")
            }
        }

        conteudos.add(lblTitulo)
        conteudos.add(Box.createRigidArea(Dimension(0, 10)))
        conteudos.add(lblSubtitulo)
        conteudos.add(Box.createRigidArea(Dimension(0, 30)))
        conteudos.add(btnEntrar)

        painel.add(conteudos)
        return painel
    }

    private fun criarPaginaGerenciador(): JPanel {
        val painel = JPanel(BorderLayout(10, 10)).apply {
            border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
        }

        val painelFormularios = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
        }

        // Bloco 1: Cadastrar Aluno
        val boxAluno = JPanel(FlowLayout(FlowLayout.LEFT)).apply {
            border = BorderFactory.createTitledBorder("1. Cadastrar Aluno")
            add(JLabel("Nome:"))
            add(txtNomeAluno)

            val btnCadastrar = JButton("Cadastrar").apply {
                addActionListener { cadastrarAluno() }
            }
            add(btnCadastrar)
        }

        // Bloco 2: Lançar Notas
        val boxNotas = JPanel(GridLayout(5, 2, 5, 5)).apply {
            border = BorderFactory.createTitledBorder("2. Lançar / Atualizar Notas")
            add(JLabel("Aluno:"))
            add(txtAlunoNota)
            add(JLabel("Nota 1:"))
            add(txtNota1)
            add(JLabel("Nota 2:"))
            add(txtNota2)
            add(JLabel("Nota 3:"))
            add(txtNota3)

            val btnSalvarNotas = JButton("Salvar Notas").apply {
                addActionListener { cadastrarNotas() }
            }
            add(JLabel(""))
            add(btnSalvarNotas)
        }

        // Bloco 3: Opções (Deletar, Testar BD e Voltar)
        val boxOpcoes = JPanel(FlowLayout(FlowLayout.LEFT)).apply {
            border = BorderFactory.createTitledBorder("3. Opções")

            val btnDeletar = JButton("Deletar Aluno").apply {
                addActionListener { deletarAluno() }
            }

            // ⬇️ BOTÃO PARA TESTAR A CONEXÃO
            val btnTestarConexao = JButton("Testar Conexão BD").apply {
                addActionListener {
                    if (db.testarConexao()) {
                        exibirMensagem("Conexão com o MySQL realizada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE)
                    } else {
                        exibirMensagem("Falha na conexão com o MySQL.\nVerifique se o servidor está ativo.", "Erro", JOptionPane.ERROR_MESSAGE)
                    }
                }
            }

            val btnVoltar = JButton("Voltar").apply {
                addActionListener {
                    gerenciadorDeTelas.show(painelPrincipal, "INICIAL")
                }
            }

            add(btnDeletar)
            add(btnTestarConexao)
            add(btnVoltar)
        }

        painelFormularios.add(boxAluno)
        painelFormularios.add(Box.createRigidArea(Dimension(0, 10)))
        painelFormularios.add(boxNotas)
        painelFormularios.add(Box.createRigidArea(Dimension(0, 10)))
        painelFormularios.add(boxOpcoes)

        val painelTabela = JPanel(BorderLayout()).apply {
            border = BorderFactory.createTitledBorder("Alunos e Desempenho (Dados do MySQL)")
            add(JScrollPane(tabela), BorderLayout.CENTER)
        }

        painel.add(painelFormularios, BorderLayout.WEST)
        painel.add(painelTabela, BorderLayout.CENTER)

        return painel
    }

    private fun cadastrarAluno() {
        val nome = txtNomeAluno.text.trim()

        if (nome.isEmpty()) {
            exibirMensagem("Digite o nome do aluno.", "Aviso", JOptionPane.WARNING_MESSAGE)
            return
        }

        val sucesso = db.cadastrarAluno(nome)
        if (sucesso) {
            txtNomeAluno.text = ""
            atualizarTabela()
            exibirMensagem("Aluno '$nome' salvo no banco!", "Sucesso", JOptionPane.INFORMATION_MESSAGE)
        } else {
            exibirMensagem("Erro ao cadastrar. Verifique se o aluno já existe no banco.", "Erro", JOptionPane.ERROR_MESSAGE)
        }
    }

    private fun cadastrarNotas() {
        val nome = txtAlunoNota.text.trim()
        val n1 = txtNota1.text.toDoubleOrNull()
        val n2 = txtNota2.text.toDoubleOrNull()
        val n3 = txtNota3.text.toDoubleOrNull()

        if (nome.isEmpty() || n1 == null || n2 == null || n3 == null) {
            exibirMensagem("Preencha o nome e as 3 notas com valores válidos.", "Erro", JOptionPane.ERROR_MESSAGE)
            return
        }

        val sucesso = db.salvarNotas(nome, n1, n2, n3)
        if (sucesso) {
            txtAlunoNota.text = ""
            txtNota1.text = ""
            txtNota2.text = ""
            txtNota3.text = ""

            atualizarTabela()
            exibirMensagem("Notas salvas no banco com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE)
        } else {
            exibirMensagem("Aluno não encontrado no banco de dados. Cadastre o aluno primeiro.", "Erro", JOptionPane.ERROR_MESSAGE)
        }
    }

    private fun deletarAluno() {
        val linhaSelecionada = tabela.selectedRow

        if (linhaSelecionada != -1) {
            val nome = modeloTabela.getValueAt(linhaSelecionada, 0).toString()
            val sucesso = db.deletarAluno(nome)

            if (sucesso) {
                atualizarTabela()
                exibirMensagem("Aluno '$nome' removido do banco!", "Sucesso", JOptionPane.INFORMATION_MESSAGE)
            } else {
                exibirMensagem("Erro ao deletar aluno do banco de dados.", "Erro", JOptionPane.ERROR_MESSAGE)
            }
        } else {
            exibirMensagem("Selecione uma linha na tabela para deletar.", "Aviso", JOptionPane.WARNING_MESSAGE)
        }
    }

    private fun atualizarTabela() {
        modeloTabela.rowCount = 0

        val alunosComNotas = db.listarAlunosComNotas()

        for (item in alunosComNotas) {
            if (item.nota1 != null && item.nota2 != null && item.nota3 != null) {
                val media = (item.nota1 + item.nota2 + item.nota3) / 3.0
                val situacao = if (media >= 7.0) {
                    "Aprovado"
                } else {
                    "Recuperação (Faltam %.2f pt)".format(7.0 - media)
                }

                modeloTabela.addRow(
                    arrayOf<Any>(
                        item.nome,
                        item.nota1,
                        item.nota2,
                        item.nota3,
                        "%.2f".format(media),
                        situacao
                    )
                )
            } else {
                modeloTabela.addRow(
                    arrayOf<Any>(item.nome, "-", "-", "-", "-", "Notas não lançadas")
                )
            }
        }
    }

    private fun exibirMensagem(mensagem: String, titulo: String, tipo: Int) {
        JOptionPane.showMessageDialog(this, mensagem, titulo, tipo)
    }
}