// ■ 普通のenum（Javaとかでもできるやつ）
// 定数を並べるだけ(まあこれはどの言語でもある)
enum class Color { RED, GREEN, BLUE }


// ここからSwiftのenumっぽいことをやってみる
// 「注文」をsealed classで表現する

// 普通に考えると注文データってクラスで作るけど
// でも注文の「種類」によって持つデータが全然違う
//
// テイクアウト → 商品名だけでいい
// デリバリー  → 商品名 + 届け先の住所がいる
// イートイン  → 商品名 + テーブル番号がいる
//
// これを1個のクラスで作ると、使わないフィールドが出てきてダサい
// ↓こうなる（やりたくない）
// class Order(val item: String, val address: String?, val tableNumber: Int?)
//
// Swiftのenum（= Kotlinのsealed class）なら種類ごとに必要なデータだけ持てる ↓

sealed class Order(val item: String) {
    // テイクアウトは商品名だけ
    class TakeOut(item: String) : Order(item)

    // デリバリーは商品名 + 住所
    class Delivery(item: String, val address: String) : Order(item)

    // イートインは商品名 + テーブル番号
    class EatIn(item: String, val tableNumber: Int) : Order(item)
}

// で、処理するときはwhenで分岐する
// コンパイラが「全パターン書いたか？」をチェックしてくれる
// → 新しい種類を追加したとき、書き忘れたらエラーになるし安全。
fun processOrder(order: Order) {
    when (order) {
        is Order.TakeOut -> {
            println("【テイクアウト】${order.item} を袋に詰めます")
        }
        is Order.Delivery -> {
            println("【デリバリー】${order.item} を ${order.address} に届けます")
        }
        is Order.EatIn -> {
            println("【イートイン】${order.item} をテーブル${order.tableNumber}番に運びます")
        }
    }
}

// もう一個よくある例
// API通信の結果って3パターンしかないよね
// 読み込み中 / 成功 / 失敗
// これも種類ごとに持つデータが違う
sealed class ApiResult {
    // 読み込み中 → データなし
    data object Loading : ApiResult()

    // 成功 → 取得したデータを持つ
    data class Success(val data: String) : ApiResult()

    // 失敗 → エラーメッセージを持つ
    data class Error(val message: String) : ApiResult()
}

fun showScreen(result: ApiResult) {
    when (result) {
        ApiResult.Loading -> println("ぐるぐる回してる...")
        is ApiResult.Success -> println("表示: ${result.data}")
        is ApiResult.Error -> println("エラー画面: ${result.message}")
    }
}

fun main() {
    println("=== 注文の例 ===")
    val orders = listOf(
        Order.TakeOut("ハンバーガー"),
        Order.Delivery("ピザ", "東京都渋谷区1-2-3"),
        Order.EatIn("パスタ", 5)
    )
    orders.forEach { processOrder(it) }

    println()
    println("=== API通信の例 ===")
    val results = listOf(
        ApiResult.Loading,
        ApiResult.Success("ユーザー一覧を取得しました"),
        ApiResult.Error("タイムアウトしました")
    )
    results.forEach { showScreen(it) }
}
