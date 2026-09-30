fun mainA(){
    //Coleção para armazenamento de alunos.
    val alunos = mutableListOf<String>();

    //Coleção de notas.
    val notas = mutableMapOf<String, MutableList<Double>>();

    //Variável de retorno
    var opçao = ""

    //Laço While de Menu
    while (opçao != "8") {
        println("--------------------------------")
        println("SISTEMA DE GERENCIAMENTO ESCOLAR")
        println("--------------------------------")

        println("1 - Cadastrar Alunos")
        println("2 - Listar Alunos")
        println("3 - Cadastrar Notas")
        println("4 - Ver Média")
        println("5 - Ver Situação")
        println("6 - Deletar")
        println("7 - Encontrar aluno")
        println("8 - Sair")
        println("")

        print("Escolha uma opção: ")
        opçao = readln()

        //Criando as Abas do Gerenciador pelos números
        when (opçao) {

            "1" -> {
                println(" --- CADASTRAR ALUNOS ---")
                println("")

                print("Digite o nome do Aluno: ")
                val nomeAluno = readln()
                alunos.add(nomeAluno)
                println("Aluno Cadastrado! ")
                println("")
            }

            "2" -> {
                print(" --- ALUNOS CADASTRADOS --- ")
                println("")
                for (a in alunos) {
                    println(a)
                }
                println("")
            }

            "3" -> {
                print(" --- CADASTRO DE NOTAS --- ")
                println("")

                println("Qual o nome do Aluno?: ")
                val nomeAluno = readln()
                println("")

                println("Digite a 1° nota do Aluno: ")
                val nota1 = readln().toDouble()
                println("")

                println("Digite a 2° nota do Aluno: ")
                val nota2 = readln().toDouble()
                println("")

                println("Digite a 3° nota do Aluno: ")
                val nota3 = readln().toDouble()
                println("")

                notas[nomeAluno] = mutableListOf(nota1, nota2, nota3)
                println("")
            }

            "4" -> {
                println(" --- ANÁLISE DAS MÉDIAS --- ")
                println("")

                print("Qual o nome do Aluno?: ")
                val nomeAluno = readln()

                val lista = notas[nomeAluno]

                val media = (lista!![0] + lista[1] + lista[2]) / 3

                println("A Média do Aluno é: $media")
                println("")
            }

            "5" -> {
                println(" --- SITUAÇÃO DOS ALUNOS --- ")
                println("")

                print("Qual o nome do Aluno?: ")
                val nomeAluno = readln()

                val lista = notas[nomeAluno]
                val media = (lista!![0] + lista[1] + lista[2]) / 3
                if (media >= 7.0) {
                    println("Aprovado")
                } else {
                    val falta = 7.0 - media
                    println("Recuperação! Faltam $falta pontos.")
                }
                println("")
            }

            "6" -> {
                println(" --- DELETAR ALUNO --- ")
                println("")

                print("Qual o nome do Aluno a ser removido?: ")
                val nomeAluno = readln()

                alunos.remove(nomeAluno)
                notas.remove(nomeAluno)

                println("Aluno $nomeAluno removido com sucesso!")
                println("")
            }

            "7" -> {
                println(" --- ENCONTRAR ALUNO --- ")
                println("")

                print("Qual o nome do Aluno?: ")
                val nomeAluno = readln()

                if (alunos.contains(nomeAluno)) {
                    println("Aluno $nomeAluno encontrado")
                } else{
                    println("Aluno $nomeAluno não encontrado")
                }
            }
        }
    }
}