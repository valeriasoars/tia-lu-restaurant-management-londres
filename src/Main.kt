import models.*

fun main() {
    var opcaoMenuPrincipal: Int?

    exibirBoasVindas()

    do {
        exibirMenu("principal")
        opcaoMenuPrincipal = lerInteiro()

        when(opcaoMenuPrincipal){
            1 -> {menuCadastrarItem()}
            2 -> {menuAtualizarItem()}
            3 -> {menuCadastrarPedido()}
            4 -> {menuAtualizarPedido()}
            5 -> {menuBuscarPedidoPorStatus()}
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


//Funções do Menu
fun menuCadastrarItem(){
    exibirCabecalho("CADASTRAR ITEM NO MENU")
    var continuarAdicionando: Boolean

    do{
        try{
            val nomeItem = lerTexto("\nNome do item: ")
            val descricaoItem = lerTexto("Descrição do item: ")
            val precoItem = lerDouble("Preço do item (R\$): ")
            val quantidadeEstoque = lerInteiro("Quantidade em estoque: ")

            cadastrarItem(nomeItem, descricaoItem, precoItem, quantidadeEstoque)
            println("\nItem cadastrado com sucesso!")

        }catch (e: IllegalArgumentException){
            println("\nErro ao cadastrar item:")
            println("┌──────────────────────────────────────────────┐")
            println("| ⚠ ${e.message}")
            println("└──────────────────────────────────────────────┘")
        }

        continuarAdicionando = perguntarSeContinua("\nQuer cadastrar mais itens? (s/n): ")

    }while(continuarAdicionando)
}
fun menuAtualizarItem() {
    exibirCabecalho("ATUALIZAR ITEM DO MENU")

    if (SystemControl.itensMenu.isEmpty()) {
        println("Não há itens cadastrados\n")
        return
    }

    var continuarAtualizando = true

    do {
        try {
            println("\nItens disponíveis no menu:")
            println("┌─────────────────────────────────────────┐")
            SystemControl.itensMenu.forEach { exibirItensMenu(it) }
            println("└─────────────────────────────────────────┘")

            val codigoItemEscolhido = lerInteiro("\nDigite o código do item que deseja atualizar: ")
            val item = verificarItem(codigoItemEscolhido)
            println("Item encontrado: ${item.nome}")

            exibirMenu("atualizarItem")

            val campoParaAtualizar = lerInteiro("Digite o número do campo que deseja atualizar: ")

            val opcoes = mapOf(
                1 to "nome",
                2 to "descricao",
                3 to "preco",
                4 to "estoque"
            )

            val campo = opcoes[campoParaAtualizar]
            if (campo == null) {
                println("Opção inválida! Tente novamente.")
                continue
            }

            val atualizacao: Any = when (campo) {
                "estoque" -> lerInteiro("Digite o novo valor para estoque: ")
                "preco" -> lerDouble("Digite o novo valor para preço: ")
                else -> lerTexto("Digite o novo(a) $campo: ")
            }

            atualizarItem(codigoItemEscolhido, campo, atualizacao)
            println("\nItem atualizado com sucesso!")
            println("─────────────────────────────────────────\n")

        } catch (e: Exception) {
            val mensagem = when (e) {
                is IllegalArgumentException,
                is IllegalStateException -> e.message
                else -> "Ocorreu um erro inesperado: ${e.message}"
            }
            println("┌──────────────────────────────────────────────┐")
            println("| ⚠ ${mensagem}")
            println("└──────────────────────────────────────────────┘")
        }

        continuarAtualizando = perguntarSeContinua("Deseja continuar atualizando itens? (s/n): ")
    } while (continuarAtualizando)
}
fun menuCadastrarPedido(){
    exibirCabecalho("CRIAR NOVO PEDIDO")

    val listaItens = mutableListOf<ItemPedido>()
    var continuarAdicionando: Boolean
    var subtotal = 0.0
    var cupom = false

    try {
        do{
            if (SystemControl.itensMenu.isEmpty()) {
                println("Não há itens cadastrados\n")
                return
            }

            SystemControl.itensMenu.filter{it.estoque >= 1 }.forEach{exibirItensMenu(it)}

            val codigoEscolhido = lerInteiro("Digite o código do item que você deseja adicionar: ")
            val item = verificarItem(codigoEscolhido)

            val qtdItem = lerInteiro("Digite a quantidade do item: ")
            adicionarItemPedido(item.codigo, qtdItem, listaItens)
            subtotal = listaItens.sumOf { it.qtd * it.item.preco }

            exibirResumoPedido(listaItens, subtotal)

            if (SystemControl.itensMenu.isNotEmpty()) {
                continuarAdicionando = perguntarSeContinua("Deseja adicionar mais itens? (s/n): ")

            } else {
                println("Não há mais itens disponíveis")
                return
            }

        } while(continuarAdicionando)

        exibirCabecalho("FINALIZAÇÃO DO PEDIDO")
        exibirResumoPedido(listaItens, subtotal)

        cupom = aplicarCupom()

        val novoPedido = cadastrarPedido(listaItens, subtotal, cupom)

        exibirCabecalho("PEDIDO CONFIRMADO ")

        println("Código do Pedido: ${novoPedido.codigo}")
        println("Status: ${novoPedido.status}")
        println("TOTAL FINAL: R$ ${String.format("%.2f", novoPedido.totalPedido)}")
        println("─────────────────────────────────────────\n")
    } catch (e: Exception) {
        val mensagem = when (e) {
            is IllegalArgumentException,
            is IllegalStateException -> e.message
            else -> "Ocorreu um erro inesperado: ${e.message}"
        }
        println("┌──────────────────────────────────────────────┐")
        println("| ⚠ ${mensagem}")
        println("└──────────────────────────────────────────────┘")
    }
}
fun menuAtualizarPedido(){
    if (SystemControl.listaPedidos.size < 1) {
        println("Error: Não existem pedidos cadastrados\n")
        return
    }

    exibirCabecalho("ATUALIZAR STATUS DO PEDIDO")

    println("\nPedidos disponíveis:")
    println("┌─────────────────────────────────────────┐")
    SystemControl.listaPedidos.forEach {exibirPedido(it,4)}
    println("└─────────────────────────────────────────┘")

    print("\nInforme o código do pedido que deseja alterar: ")
    val codigoPedidoEscolhido = readln().toInt()

    verificarPedido(codigoPedidoEscolhido)

    println("\nEscolha o novo status para o pedido:")
    exibirMenu("status")

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

    atualizarStatusPedido(codigoPedidoEscolhido, novoStatus)
    println("\nStatus alterado para: $novoStatus")
    println("─────────────────────────────────────────\n")
}
fun menuBuscarPedidoPorStatus(){
    if (SystemControl.listaPedidos.size < 1) {
        println("Error: Não existem pedidos cadastrados\n")
        return
    }

    exibirCabecalho("CONSULTAR PEDIDOS POR STATUS")

    println("\nFiltrar pedidos por status:")
    exibirMenu("status")

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
        exibirPedidosPorStatus(statusEscolhido)

    }catch (e: NumberFormatException) {
        println("Error: Digite um número válido!")
    }
}

//Funções de Interação/Exibição
fun exibirBoasVindas(){
    println("═══════════════════════════════════════")
    println("    BEM-VINDO AO LONDON RESTAURANT      ")
    println("═══════════════════════════════════════\n")
}
fun exibirMenu(menu : String) {

    if(menu == "principal"){
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

    } else if (menu == "atualizarItem"){
        println("\nO que deseja atualizar?")
        println("┌─────────────────────────┐")
        println("│ 1. Nome                 │")
        println("│ 2. Descrição            │")
        println("│ 3. Preço                │")
        println("│ 4. Estoque              │")
        println("└─────────────────────────┘")
        print("\nEscolha: ")

    } else if (menu == "status"){
        println("┌─────────────────────────────────────────┐")
        println("│ 1. ACEITO                               │")
        println("│ 2. FAZENDO                              │")
        println("│ 3. FEITO                                │")
        println("│ 4. ESPERANDO_ENTREGADOR                 │")
        println("│ 5. SAIU_PARA_ENTREGA                    │")
        println("│ 6. ENTREGUE                             │")
        println("└─────────────────────────────────────────┘")
    }

}
fun exibirCabecalho(titulo: String) {
    println("\n═══════════════════════════════════════")
    println("         $titulo       ")
    println("═══════════════════════════════════════")
}
fun exibirItensMenu(itemAtual: ItemMenu) {
    println("│ Código: ${itemAtual.codigo}")
    println("│ Nome: ${itemAtual.nome}")
    println("│ Descrição: ${itemAtual.descricao}")
    println("│ Preço: R$ ${String.format("%.2f", itemAtual.preco)}")
    println("│ Estoque: ${itemAtual.estoque} unidades")
    println("├─────────────────────────────────────────┤")
}
fun exibirItensPedido(listaPedido : MutableList<ItemPedido>) {
    listaPedido.forEach { itemPedido ->
        println("│ ${itemPedido.item.nome}")
        println("│ Quantidade: ${itemPedido.qtd}")
    }
}
fun exibirPedido(pedidoAtual : Pedido, opcaoMenu : Int) {
    println("│ Código: ${pedidoAtual.codigo}")
    println("│ Status Atual: ${pedidoAtual.status}")
    println("│ Total: R$ ${String.format("%.2f", pedidoAtual.totalPedido)}")
    if (opcaoMenu == 5){
        if (pedidoAtual.cupom) {println("│ Desconto aplicado: 15%")}
        println("│ Itens:")
        exibirItensPedido(pedidoAtual.itens)
        println("├─────────────────────────────────────────┤")
    }
}
fun exibirPedidosPorStatus(status: StatusPedido) {
    val pedidos = SystemControl.listaPedidos.filter { it.status == status }

    println("\nPedidos com status: $status")
    println("┌─────────────────────────────────────────┐")

    if (pedidos.isEmpty()) {
        println("Error: Nenhum pedido com o status $status cadastrado\n")
        return
    }

    SystemControl.listaPedidos.filter{it.status == status}.forEach{exibirPedido(it,5)}

    println("└─────────────────────────────────────────┘\n")
}
fun exibirResumoPedido(listaItens : MutableList<ItemPedido>, subtotal : Double){
    println("\nResumo do pedido:")
    println("┌─────────────────────────────────────────┐")
    listaItens.forEach { itemPedido ->
        println("│ ${itemPedido.item.nome}")
        println("│ Quantidade: ${itemPedido.qtd}")
        println("│ Valor: R$ ${String.format("%.2f", itemPedido.qtd * itemPedido.item.preco)}")
        println("├─────────────────────────────────────────┤")
    }
    println("│ SUBTOTAL DO PEDIDO: R$ ${String.format("%.2f", subtotal)}")
    println("└─────────────────────────────────────────┘")
}

//Funções auxiliares para leitura segura
fun lerInteiro(mensagem: String = ""): Int {
    var valor: Int? = null
    do {
        print(mensagem)
        val entrada = readln()
        valor = entrada.toIntOrNull()
        if (valor == null) {
            println("Entrada inválida! Digite um número inteiro.")
        }
    } while (valor == null)
    return valor
}
fun lerDouble(mensagem: String = ""): Double {
    var valor: Double? = null
    do {
        print(mensagem)
        val entrada = readln()
        valor = entrada.toDoubleOrNull()
        if (valor == null) {
            println("Entrada inválida! Digite um número decimal (ex: 12.50).")
        }
    } while (valor == null)
    return valor
}
fun lerTexto(mensagem: String = ""): String {
    var texto: String
    do {
        print(mensagem)
        texto = readln()
        if (texto.isBlank()) {
            println("Vazio? Entrada inválida colega!")
        }
    } while (texto.isBlank())
    return texto
}
fun perguntarSeContinua(mensagem: String = "Deseja continuar? (s/n): "): Boolean {
    var resposta: String
    do {
        print(mensagem)
        resposta = readln().lowercase()
        if (resposta !in listOf("s", "n")) {
            println("┌──────────────────────────────────────────────┐")
            println("| ⚠ Opção inválida! Digite apenas 's' ou 'n' ⚠ |")
            println("└──────────────────────────────────────────────┘")
        }
    } while (resposta !in listOf("s", "n"))
    return resposta == "s"
}
fun aplicarCupom() : Boolean{
    print("Deseja adicionar cupom de 15%?(s/n) ")
    val resposta = readln().firstOrNull()?.lowercase() ?: "n"
    if (resposta == "s"){
        println("Cupom de 15% aplicado!")
        return true
    }
    return false
}
