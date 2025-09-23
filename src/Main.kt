import models.*

fun main() {
    var opcaoMenuPrincipal: Int?
    var optStatusPedido = -1

    exibirBoasVindas()

    do {
        exibirMenu()
        print("\nEscolha uma opção: ")
        opcaoMenuPrincipal = readln().toIntOrNull()

        when(opcaoMenuPrincipal){
            1 -> {
                exibirCabecalho("CADASTRAR ITEM NO MENU")
                var adicionarItem: String
                do{
                    try{
                        print("\nNome do item: ")
                        val nomeItem = readln()
                        print("Descrição do item: ")
                        val descricaoItem = readln()
                        print("Preço do item (R$): ")
                        val precoItem = readln().toDouble()
                        print("Quantidade em estoque: ")
                        val quantidadeEstoque = readln().toInt()

                        cadastrarItem(nomeItem, descricaoItem, precoItem, quantidadeEstoque)
                        println("Item cadastrado com sucesso!")
                    }catch (e: IllegalArgumentException){
                        println("Erro ao cadastrar item: ${e.message}")
                    }

                    println("Quer cadastrar mais item? (s/n)")
                    adicionarItem = readln().lowercase()
                }while(adicionarItem != "n")
            }
            2 -> {
                try {
                    exibirCabecalho("ATUALIZAR ITEM DO MENU")

                    SystemControl.itensMenu.forEach{exibirItens(it)}

                    println("\nDigite o código do item que deseja atualizar: ")
                    val codigoItemEscolhido = readln().toInt()


                    val item = verificarItem(codigoItemEscolhido)
                    println("Item encontrado: ${item.nome}")

                    println("\nO que deseja atualizar?")
                    println("┌─────────────────────────┐")
                    println("│ 1. Nome                 │")
                    println("│ 2. Descrição            │")
                    println("│ 3. Preço                │")
                    println("│ 4. Estoque              │")
                    println("└─────────────────────────┘")
                    print("\nEscolha: ")

                    val campoParaAtualizar = readln().toInt()
                    if (campoParaAtualizar in 1..4) {
                        val opcoes = mapOf(
                            1 to "nome",
                            2 to "descricao",
                            3 to "preco",
                            4 to "estoque"
                        )

                        val campo = opcoes[campoParaAtualizar]
                        if (campo != null) {
                            print("Digite o $campo atualizado: ")
                            atualizarItem(codigoItemEscolhido, campo, atualizacao = readln())
                        }

                        println("\nItem atualizado com sucesso!")
                        println("─────────────────────────────────────────\n")
                    } else {
                        println("Opção inválida!")
                    }

                } catch (e: IllegalArgumentException) {
                    println(e.message)
                } catch (e: IllegalStateException){
                println(e.message)
                }
            }
            3 -> {
                exibirCabecalho("CRIAR NOVO PEDIDO")

                var adicionandoItens = true
                val listaItens = mutableListOf<ItemPedido>()
                var subtotal = 0.0
                var cupom = false
                try {
                        do{
                            SystemControl.itensMenu.filter{it.estoque >= 1 }.forEach{exibirItens(it)}
                            print("Digite o código do item que você deseja adicionar: ")
                            val codigoEscolhido = readln().toInt()
                            val item = verificarItem(codigoEscolhido)

                            print("Digite a quantidade do item: ")
                            val qtdItem = readln().toInt()
                            adicionarItemPedido(item.codigo, qtdItem, listaItens)


                            println("\nResumo do pedido atual:")
                            println("┌─────────────────────────────────────────┐")

                            listaItens.forEach { itemPedido ->
                                println("│ ${itemPedido.item.nome}")
                                println("│ Quantidade: ${itemPedido.qtd}")
                            }

                            subtotal = listaItens.sumOf { it.qtd * it.item.preco }
                            println("│ SUBTOTAL: R$ ${String.format("%.2f", subtotal)}")
                            println("└─────────────────────────────────────────┘")

                            if (SystemControl.itensMenu.isNotEmpty()) {
                                print("Deseja adicionar mais itens (s/n)? ")
                                val adicionarItem = readln().firstOrNull()?.lowercase() ?: "n"
                                if (adicionarItem == "n") {
                                    adicionandoItens = false
                                }
                            } else {
                                println("Não há mais itens disponíveis")
                                adicionandoItens = false
                            }

                        } while(adicionandoItens)
                            exibirCabecalho("FINALIZAÇÃO DO PEDIDO")

                            println("\nResumo final do pedido:")
                            println("┌─────────────────────────────────────────┐")

                            listaItens.forEach { itemPedido ->
                            println("│ ${itemPedido.item.nome}")
                            println("│ Quantidade: ${itemPedido.qtd}")
                            println("│ Subtotal: R$ ${String.format("%.2f", itemPedido.qtd * itemPedido.item.preco)}")
                            println("├─────────────────────────────────────────┤")
                            }

                            println("│ SUBTOTAL: R$ ${String.format("%.2f", subtotal)}")
                            println("└─────────────────────────────────────────┘")

                            print("Deseja adicionar cupom de 15%?(s/n)")
                            val resposta = readln().firstOrNull()?.lowercase() ?: "n"
                            if (resposta == "s"){
                                println("Cupom de 15% aplicado!")
                                cupom = true
                            }

                            val novoPedido = cadastrarPedido(listaItens, subtotal, cupom)

                            exibirCabecalho("PEDIDO CONFIRMADO ")

                            println("Código do Pedido: ${novoPedido.codigo}")
                            println("Status: ${novoPedido.status}")
                            println("TOTAL FINAL: R$ ${String.format("%.2f", novoPedido.totalPedido)}")
                            println("─────────────────────────────────────────\n")
                            break

                } catch (e: IllegalArgumentException) {
                    println(e.message)
                } catch (e: IllegalStateException){
                    println(e.message)
                }

            }
            4 -> {
                if (SystemControl.pedidos.size < 1) {
                    println("Não existem pedidos cadastrados")
                    continue
                }

                exibirCabecalho("ATUALIZAR STATUS DO PEDIDO")

                println("\nPedidos disponíveis:")
                exibirPedidos()

                print("\nInforme o código do pedido que deseja alterar: ")
                val codigoPedidoEscolhido = readln().toInt()

                var indicePedidoEncontrado = -1
                for (indicePedido in SystemControl.pedidos.indices) {
                    if (SystemControl.pedidos[indicePedido].codigo == codigoPedidoEscolhido) {
                        indicePedidoEncontrado = indicePedido
                        break
                    }
                }

                if (indicePedidoEncontrado == -1) {
                    println("\nPedido com código $codigoPedidoEscolhido não encontrado!")
                    println("─────────────────────────────────────────\n")
                    continue
                }

                println("\nEscolha o novo status para o pedido:")
                exibirMenuStatusPedidos()

                print("\nInforme o número do status: ")
                val opcaoStatusPedido = readln().toInt()

                val novoStatus = when (opcaoStatusPedido) {
                    1 -> StatusPedido.ACEITO
                    2 -> StatusPedido.FAZENDO
                    3 -> StatusPedido.FEITO
                    4 -> StatusPedido.ESPERANDO_ENTREGADOR
                    5 -> StatusPedido.SAIU_PARA_ENTREGA
                    6 -> StatusPedido.ENTREGUE
                    else -> {
                        println("\nOpção de status inválida!")
                        return
                    }
                }

                atualizarStatusPedido(indicePedidoEncontrado, novoStatus)
                println("\nStatus alterado para: $novoStatus")
                println("─────────────────────────────────────────\n")
            }
            5 -> {
                if (SystemControl.pedidos.isEmpty()) {
                    println("\nNão existem pedidos cadastrados no sistema!\n")
                    continue
                }

                exibirCabecalho("CONSULTAR PEDIDOS POR STATUS")

                println("\nFiltrar pedidos por status:")
                exibirMenuStatusPedidos()

                try{
                    print("\nInforme o número do status: ")
                    val opcaoStatusPedido = readln().toInt()

                    val statusEscolhido = when (opcaoStatusPedido) {
                        1 -> StatusPedido.ACEITO
                        2 -> StatusPedido.FAZENDO
                        3 -> StatusPedido.FEITO
                        4 -> StatusPedido.ESPERANDO_ENTREGADOR
                        5 -> StatusPedido.SAIU_PARA_ENTREGA
                        6 -> StatusPedido.ENTREGUE
                        else -> {
                            println("\nOpção de status inválida!")
                            return
                        }
                    }

                    val pedidosFiltrados = buscarPedidosPorStatus(statusEscolhido)
                    exibirPedidosPorStatus(pedidosFiltrados, statusEscolhido)

                }catch (e: NumberFormatException) {
                    println("Erro: Digite um número válido!")
                }
            }
            0 -> {
                exibirCabecalho("ENCERRANDO O SISTEMA ")
                println("  Obrigado por usar o London Restaurant!")
                println("───────────────────────────────────────\n")
            }
            else -> {
                println("\n  Opção inválida! Tente novamente.")
                println("─────────────────────────────────────────\n")
            }
        }
    } while(opcaoMenuPrincipal != 0)
}


// Funções de interação

fun exibirBoasVindas(){
    println("═══════════════════════════════════════")
    println("    BEM-VINDO AO LONDON RESTAURANT      ")
    println("═══════════════════════════════════════\n")
}

fun exibirMenu(){
    println("┌─────────────────────────────────────┐")
    println("│            MENU PRINCIPAL           │")
    println("├─────────────────────────────────────┤")
    println("│ 1. Cadastrar Item                   │")
    println("│ 2. Atualizar Item                   │")
    println("│ 3. Criar Novo Pedido                │")
    println("│ 4. Atualizar Pedido                 │")
    println("│ 5. Consultar Pedidos                │")
    println("│ 0. Sair do Sistema                  │")
    println("└─────────────────────────────────────┘")
}

fun exibirCabecalho(titulo: String) {
    println("\n═══════════════════════════════════════")
    println("         $titulo       ")
    println("═══════════════════════════════════════")
}

fun exibirMenuStatusPedidos(){
    println("┌─────────────────────────────────────────┐")
    println("│ 1. ACEITO                               │")
    println("│ 2. FAZENDO                              │")
    println("│ 3. FEITO                                │")
    println("│ 4. ESPERANDO_ENTREGADOR                 │")
    println("│ 5. SAIU_PARA_ENTREGA                    │")
    println("│ 6. ENTREGUE                             │")
    println("└─────────────────────────────────────────┘")
}

fun exibirPedidosPorStatus(pedidos: List<Pedido>, status: StatusPedido){
    println("\nPedidos com status: $status")
    println("┌─────────────────────────────────────────┐")

    if (pedidos.isEmpty()) {
        println("│ Nenhum pedido encontrado com esse status │")
    } else {
        pedidos.forEach { pedido ->
            println("│ Código do Pedido: ${pedido.codigo}")
            println("│ Status: ${pedido.status}")
            println("│ Total: R$ ${String.format("%.2f", pedido.totalPedido)}")
            if (pedido.cupom) {
                println("│ Desconto aplicado: 15%")
            }
            println("│ Itens:")
            pedido.itens.forEach { itemPedido ->
                println("│   - ${itemPedido.item.nome} (Qtd: ${itemPedido.qtd})")
            }
            println("├─────────────────────────────────────────┤")
        }
    }

    println("└─────────────────────────────────────────┘\n")
}

fun exibirPedidos(){
    println("┌─────────────────────────────────────────┐")

    SystemControl.pedidos.forEach { pedidoAtual ->
        println("│ Código: ${pedidoAtual.codigo}")
        println("│ Status Atual: ${pedidoAtual.status}")
        println("│ Total: R$ ${String.format("%.2f", pedidoAtual.totalPedido)}")
        println("├─────────────────────────────────────────┤")
    }
    println("└─────────────────────────────────────────┘")
}