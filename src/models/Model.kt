package models

enum class StatusPedido {
    ACEITO,
    FAZENDO,
    FEITO,
    ESPERANDO_ENTREGADOR,
    SAIU_PARA_ENTREGA,
    ENTREGUE,
}
data class ItemMenu(
    val codigo: Int,
    var nome: String,
    var descricao: String,
    var preco: Double,
    var estoque: Int
)
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
    val listaPedidos = mutableListOf<Pedido>()
    val msgDadosInvalidos = "Error: Item precisa ter nome, preço e estoque válidos"
}

//Funções de Manipulação
fun cadastrarItem(nome: String, descricao: String, preco: Double, estoque: Int, ) : ItemMenu {

    if(SystemControl.itensMenu.any{item -> item.nome == nome}) {
        throw IllegalArgumentException("Error: Um item com esse nome já existe")
    } else if (nome.isBlank()|| estoque < 0 || preco < 0){
        throw IllegalArgumentException(SystemControl.msgDadosInvalidos)
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
fun buscarItem(codigo: Int) : ItemMenu {
    val item = SystemControl.itensMenu.find {it.codigo == codigo }
    if (item == null)
        throw IllegalArgumentException("Error:Item com código $codigo não encontrado!")

    return item
}
fun atualizarItem(codigo: Int, campo: String, atualizacao: Any): ItemMenu {

    val item = buscarItem(codigo)

    when (campo) {
        "estoque" -> {
            val novoEstoque = (atualizacao as Number).toInt()
            if (novoEstoque < 0) throw IllegalArgumentException(SystemControl.msgDadosInvalidos)
            item.estoque = novoEstoque
        }

        "preco" -> {
            val novoPreco = (atualizacao as Number).toDouble()
            if (novoPreco < 0) throw IllegalArgumentException(SystemControl.msgDadosInvalidos)
            item.preco = novoPreco
        }

        "nome" -> {
            val novoNome = atualizacao.toString()
            if (SystemControl.itensMenu.any { it.nome == novoNome }) {
                throw IllegalArgumentException("Error: Já existe um item com este nome")
            }else if (novoNome.isBlank()){
                throw IllegalArgumentException(SystemControl.msgDadosInvalidos)
            }
            item.nome = novoNome
        }
        "descricao" -> {
            item.descricao = atualizacao.toString()
        }
    }

    return item
}
fun adicionarItemPedido(codigo : Int, quantidade : Int, listaItens: MutableList<ItemPedido>) : MutableList<ItemPedido> {

    val item = buscarItem(codigo)
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
fun cadastrarPedido(listaItens : MutableList<ItemPedido>, subtotal : Double, cupom : Boolean) : Pedido {

    val totalPedido = if (cupom) subtotal - (subtotal * 0.15) else subtotal

    val pedido = Pedido (
        codigo = SystemControl.countPedido,
        itens = listaItens,
        totalPedido = totalPedido,
        cupom = cupom,
        status = StatusPedido.ACEITO
    )
    SystemControl.countPedido++
    SystemControl.listaPedidos.add(pedido)

    return pedido
}
fun rollbackCadastrarPedido(listaItens : MutableList<ItemPedido>) {
    listaItens.map {
        buscarItem(it.item.codigo).estoque += it.qtd
    }
}
fun verificarPedido(codigo: Int) : Pedido {
    val pedido = SystemControl.listaPedidos.find {it.codigo == codigo }
    if (pedido == null)
        throw IllegalArgumentException("Error: Pedido com código $codigo não encontrado!")

    return pedido
}
fun atualizarStatusPedido(codigo: Int, novoStatus: StatusPedido){
    val pedido = SystemControl.listaPedidos.find{it.codigo == codigo}
    if (pedido != null) {pedido.status = novoStatus}
}



