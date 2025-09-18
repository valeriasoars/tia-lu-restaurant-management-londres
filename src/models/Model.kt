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
    var countItemMenu = 0
    var countPedido = 0
    val itensMenu = mutableListOf<ItemMenu>()
    val pedidos = mutableListOf<Pedido>()
}

fun cadastrarItem(
    nome: String,
    descricao: String,
    preco: Double,
    estoque: Int, ) : ItemMenu {

    if(SystemControl.itensMenu.any{item -> item.nome == nome}) {
        throw IllegalArgumentException("Item já existe")
    } else if (nome == null || estoque < 0 || preco < 0){
        throw IllegalArgumentException("Item precisa ter nome, preço e estoque válidos")
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

fun atualizarItem(codigo: Int,
                  nome: String? = null,
                  descricao: String? = null,
                  preco: Double? = null,
                  estoque: Int? = null) : ItemMenu {

    val item = SystemControl.itensMenu.find { it.codigo == codigo }
    if (item == null){throw IllegalArgumentException("Item não encontrado")}

    if ((estoque != null && estoque < 0) || (preco != null && preco< 0)){
        throw IllegalArgumentException("Item precisa ter nome, preço e estoque válidos")}

    if (nome != null) {
        if (SystemControl.itensMenu.any { it.nome == nome }) {
            throw IllegalArgumentException("Já existe um item com este nome")
        }
        item.nome = nome
    }
    if (descricao != null) item.descricao = descricao
    if (preco != null) item.preco = preco
    if (estoque != null) item.estoque = estoque

    return item
}


fun cadastrarPedido(listaItens : MutableList<ItemPedido>,
                    cupom : Boolean,
                    subtotal : Double
                    ) : Pedido {

    var totalPedido = subtotal
    if (cupom == true){
        totalPedido -= totalPedido * 0.15
    }

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
        if (item == null){throw IllegalArgumentException("Item não encontrado")}

    if (item.estoque <= 0 || quantidade > item.estoque) {
        throw IllegalArgumentException("Item sem estoque")
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
fun verificarItem(codigo: Int) : ItemMenu? {
    return SystemControl.itensMenu.find {it.codigo == codigo }
}

fun exibirItens(opcaoMenu : Int){
    if(opcaoMenu == 2){
        if (SystemControl.itensMenu.isEmpty()){
            println("Não há itens cadastrados")
        } else {
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
    } else if (opcaoMenu == 3){
        val temItensDisponiveis = SystemControl.itensMenu.any { it.estoque >= 1 }
        if (temItensDisponiveis){
            println("\nItens disponíveis no menu:")
            println("┌─────────────────────────────────────────┐")
            SystemControl.itensMenu.filter{it.estoque >= 1}.forEach { itemMenu ->
                    println("│ Código: ${itemMenu.codigo}")
                    println("│ Nome: ${itemMenu.nome}")
                    println("│ Descrição: ${itemMenu.descricao}")
                    println("│ Preço: R$ ${String.format("%.2f", itemMenu.preco)}")
                    println("│ Estoque disponível: ${itemMenu.estoque} unidades")
                    println("├─────────────────────────────────────────┤")
                }
            println("└─────────────────────────────────────────┘")
            } else {println("\nNenhum item disponível em estoque!") }
    }
}

