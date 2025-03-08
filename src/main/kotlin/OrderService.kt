import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock

data class Order(
    val productName: String,
    val amount: Int,
    val timestamp: Long = System.currentTimeMillis()
)

class OrderService {

    private val productDatabase = mutableMapOf(
        "apple" to 100,
        "banana" to 50,
        "orange" to 75
    )

    private val productConcurrentDatabase = ConcurrentHashMap<String, Int>().apply {
        put("apple", 100)
        put("banana", 50)
        put("orange", 75)
    }

    private val orderDatabase = mutableMapOf<String, MutableList<Order>>()

    // 주문 처리 함수 - 동시성 이슈 발생
    fun orderWithConcurrencyIssue(productName: String, amount: Int) {
        val currentStock = productDatabase[productName] ?: 0

        Thread.sleep(5) // 동시성 이슈 유발을 위한 인위적 지연 (해당 값을 조정하면서 테스트할 수 있습니다.)

        if (isPossibleOrder(currentStock, amount)) {
            println("Thread ${Thread.currentThread().threadId()} 주문 정보: ")
            println("  $productName: 1건, ([$amount])")

            productDatabase[productName] = currentStock - amount
            orderDatabase[productName] = mutableListOf(Order(productName, amount))
        }
    }

    // 주문 처리 함수 - 동시성 이슈 X (Synchronized)
    fun orderWithSynchronized(productName: String, amount: Int) {
        synchronized(this) {
            val currentStock = productDatabase[productName] ?: 0

            Thread.sleep(5) // 동시성 이슈 유발을 위한 인위적 지연 (해당 값을 조정하면서 테스트할 수 있습니다.)

            if (isPossibleOrder(currentStock, amount)) {
                println("Thread ${Thread.currentThread().threadId()} 주문 정보: ")
                println("  $productName: 1건, ([$amount])")

                productDatabase[productName] = currentStock - amount
                orderDatabase[productName] = mutableListOf(Order(productName, amount))
            }
        }
    }

    // 주문 처리 함수 - 동시성 이슈 X (ConcurrentHashMap - Synchronized)
    fun orderWithConcurrentHashMap(productName: String, amount: Int) {
        productConcurrentDatabase.compute(productName) { _, currentStock ->
            val stock = currentStock ?: 0

            Thread.sleep(5) // 동시성 이슈 유발을 위한 인위적 지연 (해당 값을 조정하면서 테스트할 수 있습니다.)

            if (isPossibleOrder(stock, amount)) {
                println("Thread ${Thread.currentThread().threadId()} 주문 정보: ")
                println("  $productName: 1건, ([$amount])")

                orderDatabase[productName] = mutableListOf(Order(productName, amount))
                return@compute stock - amount
            }

            return@compute stock
        }
    }

    // 주문 처리 함수 - 동시성 이슈 X (ReentrantLock)
    private val lock = ReentrantLock()

    fun orderWithReentrantLock(productName: String, amount: Int) {
        lock.lock()

        try {
            val currentStock = productDatabase[productName] ?: 0

            Thread.sleep(5)

            if (isPossibleOrder(currentStock, amount)) {
                println("Thread ${Thread.currentThread().threadId()} 주문 정보: ")
                println("  $productName: 1건, ([$amount])")

                productDatabase[productName] = currentStock - amount
                orderDatabase[productName] = mutableListOf(Order(productName, amount))
            }
        } finally {
            lock.unlock()
        }
    }

    private fun isPossibleOrder(currentStock: Int, amount: Int) = currentStock >= amount

    fun getStock(productName: String): Int {
        return productDatabase[productName] ?: 0
    }

    fun getStockConcurrentMap(productName: String): Int {
        return productConcurrentDatabase[productName] ?: 0
    }
}
