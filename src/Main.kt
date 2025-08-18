
fun main() {
    println("*** London Restaurant ***")

    val itensMenu = mutableListOf<Map<String, Any>>()
    var codigoItem = 1


    while (true) {
        println("MENU PRINCIPAL")
        println("1. Cadastrar Item")
        println("2. Atualizar Item")
        println("3. Criar Pedido")
        println("4. Atualizar Pedido")
        println("5. Consultar Pedidos")
        println("0. Sair")

        print("Escolha uma opção: ")
        val opcao = readln().toInt()

        when(opcao){
            1 -> {
                println("Cadastrar Item")
                print("Nome do item: ")
                val nome = readln()

                print("Descrição: ")
                val descricao = readln()

                print("Preço do item: R$")
                val preco = readln().toDouble()

                print("Quantidade em estoque: ")
                val estoque = readln().toInt()

                val novoItem = mapOf(
                    "codigo" to codigoItem,
                    "nome" to nome,
                    "descricao" to descricao,
                    "preco" to preco,
                    "estoque" to estoque
                )

                itensMenu.add(novoItem)
                println("Item cadastrado!")
                println()
                codigoItem ++
            }

            2 -> {
                println("Atualizar Item")
                if(itensMenu.isEmpty()){
                    println("Nenhum item cadastrado")
                    continue
                }

                println("Itens disponíveis: ")
                for(item in itensMenu){
                    println("Código: ${item["codigo"]} - Nome: ${item["nome"]} - Descrição: ${item["descricao"]} - Preço: R$ ${item["preco"]} - Estoque: ${item["estoque"]}")
                }

                print("Digite o código do item que deseja atualizar: ")
                val codigoEscolhido = readln().toInt()

                var itemCadastrado = false
                for(i in itensMenu.indices){
                    val item = itensMenu[i]
                    if(item["codigo"] == codigoEscolhido){
                        itemCadastrado = true
                        println("Item encontrado: ${item["nome"]}")

                        println("O que deseja atualizar?")
                        println("1. Nome")
                        println("2. Descrição")
                        println("3. Preço")
                        println("4. Estoque")
                        print("Escolha: ")

                        val campoAtualizar = readln().toInt()
                        val itemAtualizado = item.toMutableMap()

                        when (campoAtualizar) {
                            1 -> {
                                print("Novo nome: ")
                                val novoNome = readln()
                                itemAtualizado["nome"] = novoNome
                            }
                            2 -> {
                                print("Nova descrição: ")
                                val novaDescricao = readln()
                                itemAtualizado["descricao"] = novaDescricao
                            }
                            3 -> {
                                print("Novo preço: R$ ")
                                val novoPreco = readln().toDouble()
                                itemAtualizado["preco"] = novoPreco
                            }
                            4 -> {
                                print("Nova quantidade em estoque: ")
                                val novoEstoque = readln().toInt()
                                itemAtualizado["estoque"] = novoEstoque
                            }
                            else -> {
                                println("Opção inválida!")
                                continue
                            }
                        }

                        itensMenu[i] = itemAtualizado
                        println("Item atualizado!")
                        break
                    }
                    if (!itemCadastrado) {
                        println("Item com código $codigoEscolhido não encontrado.")
                    }
                }
            }

            3 -> {

            }

            4 -> {

            }

            5 -> {

            }

        }
    }
}