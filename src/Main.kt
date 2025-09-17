import models.*

fun main() {

    var running = true
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

                val novoItem = cadastrarItem(nome, descricao, preco, estoque)
                println("Item '${novoItem}' cadastrado com sucesso!")
            }

            2 -> {
                if(SystemControl.itensMenu.isEmpty()){
                    println("Nenhum item cadastrado\n")
                    continue
                }

                println("\n=========================")
                println("===== Atualizar Item =====")
                println("=========================\n")

                println("Itens disponíveis: ")
                for(item in SystemControl.itensMenu){
                    println("Código: " + item.codigo)
                    println("Nome: " + item.nome )
                    println("Descrição: " + item.descricao )
                    println("Preço: R$ " + item.preco)
                    println("Estoque: " + item.estoque)
                    println("----------------------\n")
                }

                print("Digite o código do item que deseja atualizar: ")
                val codigoEscolhido = readln().toInt()

                if(verificarItem(codigoEscolhido) != null){
                    println("Item encontrado: ${SystemControl.itensMenu[codigoEscolhido].nome}")

                    println("O que deseja atualizar?")
                    println("1. Nome")
                    println("2. Descrição")
                    println("3. Preço")
                    println("4. Estoque")
                    print("Escolha: ")
                    val campoAtualizar = readln().toInt()

                    when (campoAtualizar) {
                            1 -> {
                                print("Novo nome: ")
                                atualizarItem(codigoEscolhido, nome = readln())
                            }
                            2 -> {
                                print("Nova descrição: ")
                                atualizarItem(codigoEscolhido, descricao = readln())
                            }
                            3 -> {
                                print("Novo preço: R$ ")
                                atualizarItem(codigoEscolhido, preco = readln().toDouble())
                            }
                            4 -> {
                                print("Nova quantidade em estoque: ")
                                atualizarItem(codigoEscolhido, estoque = readln().toInt())
                            }
                            else -> {
                                println("Opção inválida!")
                                continue
                            }
                        }
                        println("Item atualizado!\n")
                } else {
                    println("Item não encontrado\n")
                }
            }

            3 -> {
                println("\n========================")
                println("=== Criação de Pedido ===")
                println("========================\n")


                var adicionandoItens = true
                val listaItens = mutableListOf<ItemPedido>()
                var cupom = false
                var subtotal = 0.0

                do{
                    println("Itens disponíveis:\n")
                    for(item in SystemControl.itensMenu){
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

                    if (verificarItem(codigoEscolhido) != null){
                        print("Digite a quantidade do item: ")
                        val qtdItem = readln().toInt()
                        adicionarItemPedido(codigoEscolhido, qtdItem, listaItens)
                    }

                    subtotal = listaItens.sumOf { it.qtd * it.item.preco }

                    print("Subtotal = R$${subtotal}\n")
                    print("Deseja adicionar mais itens (s/n)? ")
                    val adicionarItem = readln()[0].lowercase()
                    if (adicionarItem == "n") {
                        adicionandoItens = false
                    }
                } while(adicionandoItens)

                print("Deseja adicionar cupom de 15%?(s/n)")
                val resposta = readln()[0].lowercase()
                if (resposta == "s"){
                    cupom = true
                }

                val pedido = cadastrarPedido(listaItens, cupom, subtotal)

                println("\nPedido confirmado!")
                pedido.itens.forEach { itemPedido ->
                    println("Nome: ${itemPedido.item.nome}\n" +
                            "Quantidade: ${itemPedido.qtd}\n")
                }
                println("Status: ${pedido.status}\n" + "Total: R$${pedido.totalPedido}\n")
            }

            /*4 -> {

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

            }*/
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