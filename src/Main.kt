import models.ItemMenu
import models.ItemPedido
import models.Pedido
import models.StatusPedido

fun main() {


    val itensMenu = mutableListOf<ItemMenu>()
    val pedidos = mutableListOf<Pedido>()
    var running = true

    var countItem = 1
    var countPedido = 1

    var optStatusPedido = -1

    println("=========================")
    println("=== London Restaurant ===")
    println("=========================\n")

    do {
        println("MENU PRINCIPAL")
        println("1. Cadastrar Item")
        println("2. Atualizar Item")
        println("3. Criar Pedido")
        println("4. Atualizar Pedido")
        println("5. Consultar Pedidos")
        println("0. Sair\n")

        print("Escolha uma opção: ")
        var opcao = readln().toInt()

        when(opcao){
            1 -> {
                println("\n=========================")
                println("===== Cadastrar Item =====")
                println("=========================\n")
                print("Nome do item: ")
                val nome = readln()

                print("Descrição: ")
                val descricao = readln()

                print("Preço do item: R$")
                val preco = readln().toDouble()

                print("Quantidade em estoque: ")
                val estoque = readln().toInt()

                val novoItem = ItemMenu(
                    codigo = countItem,
                    nome =  nome,
                    descricao =  descricao,
                    preco = preco,
                    estoque = estoque
                )

                itensMenu.add(novoItem)
                countItem ++
                println("Item cadastrado!\n\n")
            }

            2 -> {
                if(itensMenu.isEmpty()){
                    println("Nenhum item cadastrado\n")
                    continue
                }

                println("\n=========================")
                println("===== Atualizar Item =====")
                println("=========================\n")

                println("Itens disponíveis: ")
                for(item in itensMenu){
                    println("Código: " + item.codigo)
                    println("Nome: " + item.nome )
                    println("Descrição: " + item.descricao )
                    println("Preço: R$ " + item.preco)
                    println("Estoque: " + item.estoque)
                    println("----------------------\n")
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
                println("\n========================")
                println("=== Criação de Pedido ===")
                println("========================\n")

                val pedido = Pedido (
                    codigo = countPedido,
                    itens = mutableListOf(),
                    totalPedido = 0.00,
                    cupom = false,
                    status = StatusPedido.ACEITO
                )
                countPedido++

                var adicionandoItens = true
                do{
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
                    print("Digite o código do item que você deseja adicionar: ")
                    val codigoEscolhido = readln().toInt()

                    val item = itensMenu.find { it.codigo == codigoEscolhido }
                    if (item != null && item.estoque != 0){
                        print("Digite a quantidade do item: ")
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
                    } else if (item != null && item.estoque == 0) {
                        println("Item sem estoque")}
                    else {
                        println("Item com código ${codigoEscolhido} não encontrado.\n")}

                    println("Itens já selecionados: \n")
                    pedido.itens.forEach{ itemPedido ->
                        println("Nome: ${itemPedido.item.nome}\n" + "Quantidade: ${itemPedido.qtd}\n")
                    }
                    println("Subtotal: R$${pedido.totalPedido}")


                    print("Deseja adicionar mais itens (s/n)? ")
                    val adicionarItem = readln()[0].lowercase()
                    if (adicionarItem == "n") {
                        adicionandoItens = false
                    }
                } while(adicionandoItens)

                print("Deseja adicionar cupom de 15%?(s/n)")
                val cupom = readln()[0].lowercase()
                if (cupom == "s"){
                    pedido.cupom = true
                    pedido.totalPedido = pedido.totalPedido - (pedido.totalPedido * 0.15)
                }

                println("\nPedido confirmado!")
                pedido.itens.forEach { itemPedido ->
                    println("Nome: ${itemPedido.item.nome}\n" +
                            "Quantidade: ${itemPedido.qtd}\n")
                }
                println("Status: ${pedido.status}\n" + "Total: R$${pedido.totalPedido}\n")

                pedidos.add(pedido)
            }

            4 -> {

                if (pedidos.size < 1) {
                    println("Não existem pedidos cadastrados")
                    continue
                }

                println("Edicao de Pedido")

                println("Pedidos disponíveis: ")
                println("========================")
                for (pedido in pedidos){
                    println("Código: "+ pedido.codigo)
                    println("Status: "+ pedido.status)
                    println("========================")
                }

                println("Informe o código do pedido que você quer alterar: ")

                countPedido = readln().toInt()
                if ( countItem > pedidos.size){
                    println("Esse código não é válido colega")
                    continue
                }

                println("Informe o status que vc quer dar ao pedido: \n ")
                println("Possíveis status: ")
                println("===================")
                println("Status disponíveis: ")
                println("1. ACEITO")
                println("2. FAZENDO")
                println("3. FEITO")
                println("4. ESPERANDO_ENTREGADOR")
                println("5. SAIU_PARA_ENTREGA")
                println("6. ENTREGUE")
                println("===================\n")

                println("Informe o numero do status que você quer atribuir: ")
                optStatusPedido = readln().toInt()

                when (optStatusPedido){
                     1 -> pedidos[countPedido+1].status = StatusPedido.ACEITO
                     2 -> pedidos[countPedido+1].status = StatusPedido.FAZENDO
                     3 -> pedidos[countPedido+1].status = StatusPedido.FEITO
                     4 -> pedidos[countPedido+1].status = StatusPedido.ESPERANDO_ENTREGADOR
                     5 -> pedidos[countPedido+1].status = StatusPedido.SAIU_PARA_ENTREGA
                     6 -> pedidos[countPedido+1].status = StatusPedido.ENTREGUE
                    else -> {
                        println("Essa opção não é válida")
                        continue
                    }
                }
                println("Pedido $countPedido Editado!")
            }

            5 -> {

            }
            0 -> {
                println("Saindo...")
                running = false
            }
            else -> {
                println("Opção invalida! Tente novamente")
            }
        }
    } while(running)
}