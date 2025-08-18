package models

data class ItemMenu(
    val codigo: Int,
    val nome: String,
    val descricao: String,
    val preco: Double,
    val estoque: Int
)