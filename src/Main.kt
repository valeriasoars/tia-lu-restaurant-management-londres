import models.ItemMenu
import models.ItemPedido
import models.Pedido
import models.StatusPedido

fun main() {
    println("*** London Restaurant ***")

    val itensMenu = mutableListOf<ItemMenu>()
    var proximoCodigoItem = 1
    var proximoCodigoPedido = 1

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

                val novoItem = ItemMenu(
                    codigo = proximoCodigoItem,
                    nome =  nome,
                    descricao =  descricao,
                    preco = preco,
                    estoque = estoque
                )

                itensMenu.add(novoItem)
                println("Item cadastrado!")
                println()
                proximoCodigoItem ++
            }

            2 -> {
                println("Atualizar Item")
                if(itensMenu.isEmpty()){
                    println("Nenhum item cadastrado")
                    continue
                }

                println("Itens disponíveis: ")
                for(item in itensMenu){
                    println("Código: ${item.codigo}\n" +
                            "Nome: ${item.nome}\n" +
                            "Descrição: ${item.descricao}\n" +
                            "Preço: R$ ${item.preco}\n " +
                            "Estoque: ${item.estoque}\n")
                }
                print("Digite o código do item que deseja atualizar: ")
                val codigoEscolhido = readln().toInt()

                var itemCadastrado = false
                for(i in itensMenu.indices){
                    val item = itensMenu[i]
                    if(item.codigo == codigoEscolhido){
                        itemCadastrado = true
                        println("Item encontrado: ${item.nome}")

                        println("O que deseja atualizar?")
                        println("1. Nome")
                        println("2. Descrição")
                        println("3. Preço")
                        println("4. Estoque")
                        print("Escolha: ")

                        val campoAtualizar = readln().toInt()

                        val itemAtualizado =  when (campoAtualizar) {
                            1 -> {
                                print("Novo nome: ")
                                val novoNome = readln()
                                item.copy(nome = novoNome)
                            }
                            2 -> {
                                print("Nova descrição: ")
                                val novaDescricao = readln()
                                item.copy(descricao = novaDescricao)
                            }
                            3 -> {
                                print("Novo preço: R$ ")
                                val novoPreco = readln().toDouble()
                                item.copy(preco = novoPreco)
                            }
                            4 -> {
                                print("Nova quantidade em estoque: ")
                                val novoEstoque = readln().toInt()
                                item.copy(estoque = novoEstoque)
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
                }

                if (!itemCadastrado) {
                    println("Item com código ${codigoEscolhido} não encontrado.")
                }
            }

            3 -> {
                println("Criação de pedido")

                val pedido = Pedido (
                    codigo = proximoCodigoPedido,
                    itens = mutableListOf(),
                    totalPedido = 0.00,
                    cupom = false,
                    status = StatusPedido.ACEITO
                )
                var adicionandoItens = true

                //Adicionar Itens
                while (adicionandoItens){
                    println("Itens disponíveis:\n")
                    for(item in itensMenu){
                        if(item.estoque >= 1){
                            println("Código: ${item.codigo}\n" +
                                    "Nome: ${item.nome}\n" +
                                    "Descrição: ${item.descricao}\n" +
                                    "Preço: R$ ${item.preco}\n" +
                                    "Estoque: ${item.estoque}\n\n")
                        }
                    }
                    println("Digite o código do item que você deseja adicionar: ")
                    val codigoEscolhido = readln().toInt()

                    var itemCadastrado = false
                    for(item in itensMenu){
                        if(item.codigo == codigoEscolhido) {
                            itemCadastrado = true
                            println("Digite a quantidade do item: ")
                            val qtdItem = readln().toInt()
                            if (qtdItem > item.estoque) {
                                println("Não há essa quantidade de item no estoque")
                            } else {
                                val novoItem = ItemPedido(
                                    item = item,
                                    qtd = qtdItem
                                )
                                pedido.itens.add(novoItem)
                                pedido.totalPedido += (novoItem.qtd * novoItem.item.preco)
                                item.estoque -= qtdItem
                            }
                            println("Itens já selecionados: \n")
                            for(itemPedido in pedido.itens){
                                    println(
                                            "Nome: ${itemPedido.item.nome}\n" +
                                            "Quantidade: ${itemPedido.qtd}\n")
                            }
                            print("Valor Total da Compra: R$${pedido.totalPedido}\n")
                            break
                        }
                    }

                    if (!itemCadastrado) {
                        println("Item com código ${codigoEscolhido} não encontrado.\n")
                    }

                    print("Deseja adicionar mais itens (s/n)? ")
                    val adicionarItem = readln()[0]
                    if (adicionarItem == 'n') {
                        adicionandoItens = false
                    }
                }
                println("Pedido:\n")
                for(itemPedido in pedido.itens){
                    println("Nome: ${itemPedido.item.nome}\n" +
                            "Quantidade: ${itemPedido.qtd}\n")
                }
                println("Subtotal: R$${pedido.totalPedido}\n")

                println("Deseja adicionar cupom de 15%?(s/n)")
                val cupom = readln()[0]
                if (cupom == 's'){
                    pedido.cupom = true
                    pedido.totalPedido = pedido.totalPedido - (pedido.totalPedido * 0.15)
                }

                println("Pedido confirmado: ")
                for(itemPedido in pedido.itens){
                    println("Nome: ${itemPedido.item.nome}\n" +
                            "Quantidade: ${itemPedido.qtd}\n")
                }
                print("Status: ${pedido.status}\n" + "Total: R$${pedido.totalPedido}\n")



            }

            4 -> {

            }

            5 -> {

            }
            0 -> {
                println("Saindo ...")
                break
            }

            else -> {
                println("Opção invalida! Tente novamente")
            }
        }
    }
}