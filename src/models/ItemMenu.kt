package models

data class ItemMenu(
    val codigo: Int,
    val nome: String,
    val descricao: String,
    val preco: Double,
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
    val itens: MutableList<ItemPedido>,
    var totalPedido: Double,
    var cupom: Boolean,
    val status: StatusPedido
)

data class ItemPedido(
    val item: ItemMenu,
    val qtd: Int
)

