package models

data class ItemMenu(
    val codigo: Int,
    var nome: String,
    var descricao: String,
    var preco: Double,
    var estoque: Int
)

enum class StatusPedido {
    ACEITO,
    FAZENDO,
    FEITO,
    ESPERANDO_ENTREGADOR,
    SAIU_PARA_ENTREGA,
    ENTREGUE,
}

data class Pedido(
    val codigo: Int,
    var itens: MutableList<ItemPedido>,
    var totalPedido: Double,
    var cupom: Boolean,
    var status: StatusPedido
)

data class ItemPedido(
    val item: ItemMenu,
    val qtd: Int
)

object SystemControl {
    var countItemMenu = 1
    var countPedido = 1
    val itensMenu = mutableListOf<ItemMenu>()
    val pedidos = mutableListOf<Pedido>()
}

fun cadastrarItem(nome: String, descricao: String, preco: Double, estoque: Int, ) : ItemMenu {

    if(SystemControl.itensMenu.any{item -> item.nome == nome}) {
        throw IllegalArgumentException("Error: Item já existe")
    } else if (nome == null || estoque < 0 || preco < 0){
        throw IllegalArgumentException("Error: Item precisa ter nome, preço e estoque válidos")
    }else {
            val novoItem = ItemMenu(
                codigo = SystemControl.countItemMenu,
                nome =  nome,
                descricao =  descricao,
                preco = preco,
                estoque = estoque
            )

            SystemControl.itensMenu.add(novoItem)
            SystemControl.countItemMenu ++

            return novoItem
        }
}

fun atualizarItem(
    codigo: Int,
    campo: String,
    atualizacao: Any
): ItemMenu {

    val item = verificarItem(codigo)
    val msgInvalido = "Item precisa ter nome, preço e estoque válidos"

    when (campo) {
        "estoque" -> {
            val novoEstoque = (atualizacao as Number).toInt()
            if (novoEstoque < 0) throw IllegalArgumentException(msgInvalido)
            item.estoque = novoEstoque
        }

        "preco" -> {
            val novoPreco = (atualizacao as Number).toDouble()
            if (novoPreco < 0) throw IllegalArgumentException(msgInvalido)
            item.preco = novoPreco
        }

        "nome" -> {
            val novoNome = atualizacao.toString()
            if (SystemControl.itensMenu.any { it.nome == novoNome }) {
                throw IllegalArgumentException("Error: Já existe um item com este nome")
            }
            item.nome = novoNome
        }
        "descricao" -> {
            item.descricao = atualizacao.toString()
        }
    }

    return item
}

fun cadastrarPedido(listaItens : MutableList<ItemPedido>,
                    subtotal : Double,
                    cupom : Boolean
                    ) : Pedido {

    var totalPedido = if (cupom) subtotal * 0.15 else subtotal

    val pedido = Pedido (
        codigo = SystemControl.countPedido,
        itens = listaItens,
        totalPedido = totalPedido,
        cupom = cupom,
        status = StatusPedido.ACEITO
    )
    SystemControl.countPedido++
    SystemControl.pedidos.add(pedido)

    return pedido
}

fun adicionarItemPedido(codigo : Int,
                        quantidade : Int,
                        listaItens: MutableList<ItemPedido>) : MutableList<ItemPedido>{

    var item = verificarItem(codigo)
    if (item.estoque <= 0 || quantidade > item.estoque) {
        throw IllegalArgumentException("Error: Item sem estoque")
    } else {
            val novoItem = ItemPedido(
                item = item,
                qtd = quantidade
            )
            listaItens.add(novoItem)
            item.estoque -= quantidade
        }
    return listaItens

}
fun verificarItem(codigo: Int) : ItemMenu {
    val item = SystemControl.itensMenu.find {it.codigo == codigo }
    if (item == null)
        throw IllegalArgumentException("\n Error:Item com código $codigo não encontrado!\n\"─────────────────────────────────────────\\n\")")

    return item
}

fun exibirItens(item: ItemMenu){
        if (SystemControl.itensMenu.isEmpty())throw IllegalStateException("Error:Não há itens cadastrados")

        println("\nItens disponíveis no menu:")
        println("┌─────────────────────────────────────────┐")

        SystemControl.itensMenu.forEach { itemAtual ->
            println("│ Código: ${itemAtual.codigo}")
            println("│ Nome: ${itemAtual.nome}")
            println("│ Descrição: ${itemAtual.descricao}")
            println("│ Preço: R$ ${String.format("%.2f", itemAtual.preco)}")
            println("│ Estoque: ${itemAtual.estoque} unidades")
            println("├─────────────────────────────────────────┤")
        }

        println("└─────────────────────────────────────────┘")
}

