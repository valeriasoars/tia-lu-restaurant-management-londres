import models.*

fun main() {

    var running = true
    var optStatusPedido = -1

    println("=========================")
    println("=== London Restaurant ===")
    println("=========================\n")

    do {
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
        print("\nEscolha uma opção: ")
        var opcaoMenuPrincipal = readln().toInt()

        when(opcaoMenuPrincipal){
            1 -> {

                println("\n═══════════════════════════════════════")
                println("         CADASTRAR ITEM NO MENU       ")
                println("═══════════════════════════════════════")

                print("\nNome do item: ")
                val nomeDoItem = readln()

                print("Descrição do item: ")
                val descricaoDoItem = readln()

                print("Preço do item (R$): ")
                val precoDoItem = readln().toDouble()

                print("Quantidade em estoque: ")
                val quantidadeEmEstoque = readln().toInt()

                val novoItem = cadastrarItem(nomeDoItem, descricaoDoItem, precoDoItem, quantidadeEmEstoque)
                println("Item '${novoItem}' cadastrado com sucesso!")
            }

            2 -> {
                try {
                    println("\n═══════════════════════════════════════")
                    println("         ATUALIZAR ITEM DO MENU        ")
                    println("═══════════════════════════════════════")

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
                println("\n═══════════════════════════════════════")
                println("            CRIAR NOVO PEDIDO           ")
                println("═══════════════════════════════════════")

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

                            println("\n═══════════════════════════════════════")
                            println("         FINALIZAÇÃO DO PEDIDO          ")
                            println("═══════════════════════════════════════")
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

                            println("\n═══════════════════════════════════════")
                            println("          PEDIDO CONFIRMADO             ")
                            println("═══════════════════════════════════════")

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

                println("\n═══════════════════════════════════════")
                println("       ATUALIZAR STATUS DO PEDIDO      ")
                println("═══════════════════════════════════════")

                println("\nPedidos disponíveis:")
                println("┌─────────────────────────────────────────┐")

                SystemControl.pedidos.forEach { pedidoAtual ->
                println("│ Código: ${pedidoAtual.codigo}")
                println("│ Status Atual: ${pedidoAtual.status}")
                println("│ Total: R$ ${String.format("%.2f", pedidoAtual.totalPedido)}")
                println("├─────────────────────────────────────────┤")
                }
                println("└─────────────────────────────────────────┘")

                println("Informe o código do pedido que você quer alterar: ")
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
                println("┌─────────────────────────────────────────┐")
                println("│ 1. ACEITO                               │")
                println("│ 2. FAZENDO                              │")
                println("│ 3. FEITO                                │")
                println("│ 4. ESPERANDO_ENTREGADOR                 │")
                println("│ 5. SAIU_PARA_ENTREGA                    │")
                println("│ 6. ENTREGUE                             │")
                println("└─────────────────────────────────────────┘")

                print("\nInforme o número do status: ")
                val opcaoStatusPedido = readln().toInt()

                when (opcaoStatusPedido) {
                    1 -> {
                        SystemControl.pedidos[indicePedidoEncontrado].status = StatusPedido.ACEITO
                        println("\nStatus alterado para: ACEITO")
                    }
                    2 -> {
                        SystemControl.pedidos[indicePedidoEncontrado].status = StatusPedido.FAZENDO
                        println("\nStatus alterado para: FAZENDO")
                    }
                    3 -> {
                        SystemControl.pedidos[indicePedidoEncontrado].status = StatusPedido.FEITO
                        println("\nStatus alterado para: FEITO")
                    }
                    4 -> {
                        SystemControl.pedidos[indicePedidoEncontrado].status = StatusPedido.ESPERANDO_ENTREGADOR
                        println("\nStatus alterado para: ESPERANDO_ENTREGADOR")
                    }
                    5 -> {
                        SystemControl.pedidos[indicePedidoEncontrado].status = StatusPedido.SAIU_PARA_ENTREGA
                        println("\nStatus alterado para: SAIU_PARA_ENTREGA")
                    }
                    6 -> {
                        SystemControl.pedidos[indicePedidoEncontrado].status = StatusPedido.ENTREGUE
                        println("\nStatus alterado para: ENTREGUE")
                    }
                    else -> {
                        println("\nOpção de status inválida!")
                        continue
                    }
                }
                println("─────────────────────────────────────────\n")
            }

            5 -> {
                if (SystemControl.pedidos.isEmpty()) {
                    println("\nNão existem pedidos cadastrados no sistema!\n")
                    continue
                }

                println("\n═══════════════════════════════════════")
                println("       CONSULTAR PEDIDOS POR STATUS    ")
                println("═══════════════════════════════════════")

                println("\nFiltrar pedidos por status:")
                println("┌─────────────────────────────────────────┐")
                println("│ 1. ACEITO                               │")
                println("│ 2. FAZENDO                              │")
                println("│ 3. FEITO                                │")
                println("│ 4. ESPERANDO_ENTREGADOR                 │")
                println("│ 5. SAIU_PARA_ENTREGA                    │")
                println("│ 6. ENTREGUE                             │")
                println("└─────────────────────────────────────────┘")

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
                        continue
                    }
                }

                println("\nPedidos com status: $statusEscolhido")
                println("┌─────────────────────────────────────────┐")

                var encontrouPedidosComStatus = false
                for (pedidoAtual in SystemControl.pedidos) {
                    if (pedidoAtual.status == statusEscolhido) {
                        encontrouPedidosComStatus = true

                        println("│ Código do Pedido: ${pedidoAtual.codigo}")
                        println("│ Status: ${pedidoAtual.status}")
                        println("│ Total: R$ ${String.format("%.2f", pedidoAtual.totalPedido)}")
                        if (pedidoAtual.cupom) {
                            println("│ Desconto aplicado: 15%")
                        }
                        println("│ Itens:")
                        for (itemPedido in pedidoAtual.itens) {
                            println("│   - ${itemPedido.item.nome} (Qtd: ${itemPedido.qtd})")
                        }
                        println("├─────────────────────────────────────────┤")
                    }
                }

                if (!encontrouPedidosComStatus) {
                    println("│Nenhum pedido encontrado com esse status │")
                }

                println("└─────────────────────────────────────────┘\n")
            }
            0 -> {
                println("\n═══════════════════════════════════════")
                println("         ENCERRANDO O SISTEMA           ")
                println("═══════════════════════════════════════")
                println("  Obrigado por usar o London Restaurant!")
                println("───────────────────────────────────────\n")
                running = false
            }
            else -> {
                println("\n  Opção inválida! Tente novamente.")
                println("─────────────────────────────────────────\n")
            }
        }
    } while(running)
}