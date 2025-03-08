import org.junit.jupiter.api.Assertions.assertNotEquals
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import kotlin.system.measureTimeMillis
import kotlin.test.Test
import kotlin.test.assertEquals

class OrderWithConcurrencyIssueServiceTest {

    private val service = OrderService()

    @Test
    fun `동시 주문 시 재고 불일치 발생 테스트`() {
        val productName = "apple"
        val initialStock = service.getStock(productName)

        val orderAmount = 8
        val threadCount = 100

        val executor = Executors.newFixedThreadPool(threadCount)
        val latch = CountDownLatch(threadCount)

        // 각 스레드에서 주문을 수행하는 작업 생성
        repeat(threadCount) {
            executor.execute {
                try {
                    service.orderWithConcurrencyIssue(productName, orderAmount)
                } finally {
                    latch.countDown() // 작업 완료 후 카운트 감소
                }
            }
        }

        // 모든 스레드가 작업을 완료할 때까지 대기
        latch.await()
        executor.shutdown()

        // 최종 재고 값 확인
        val expectedStock = initialStock % orderAmount
        val actualStock = service.getStock(productName)

        assertNotEquals(expectedStock, actualStock, "재고 불일치 발생!")
    }

    @Test
    fun `동시 주문 시 재고 일치 테스트1`() {
        val productName = "apple"
        val initialStock = service.getStock(productName)

        val orderAmount = 8
        val threadCount = 100

        val executor = Executors.newFixedThreadPool(threadCount)
        val latch = CountDownLatch(threadCount)

        // 각 스레드에서 주문을 수행하는 작업 생성
        repeat(threadCount) {
            executor.execute {
                try {
                    service.orderWithSynchronized(productName, orderAmount)
                } finally {
                    latch.countDown() // 작업 완료 후 카운트 감소
                }
            }
        }

        // 모든 스레드가 작업을 완료할 때까지 대기
        latch.await()
        executor.shutdown()

        val expectedStock = initialStock % orderAmount
        val actualStock = service.getStock(productName)

        assertEquals(expectedStock, actualStock, "재고 일치!")
    }

    @Test
    fun `동시 주문 시 재고 일치 테스트2`() {
        val productName = "apple"
        val initialStock = service.getStock(productName)

        val orderAmount = 8
        val threadCount = 100

        val executor = Executors.newFixedThreadPool(threadCount)
        val latch = CountDownLatch(threadCount)

        // 각 스레드에서 주문을 수행하는 작업 생성
        repeat(threadCount) {
            executor.execute {
                try {
                    service.orderWithConcurrentHashMap(productName, orderAmount)
                } finally {
                    latch.countDown() // 작업 완료 후 카운트 감소
                }
            }
        }

        // 모든 스레드가 작업을 완료할 때까지 대기
        latch.await()
        executor.shutdown()

        val expectedStock = initialStock % orderAmount
        val actualStock = service.getStockConcurrentMap(productName)

        assertEquals(expectedStock, actualStock, "재고 일치!")
    }

    @Test
    fun `동시 주문 시 재고 일치 테스트3`() {
        val productName = "apple"
        val initialStock = service.getStock(productName)

        val orderAmount = 8
        val threadCount = 100

        val executor = Executors.newFixedThreadPool(threadCount)
        val latch = CountDownLatch(threadCount)

        // 각 스레드에서 주문을 수행하는 작업 생성
        repeat(threadCount) {
            executor.execute {
                try {
                    service.orderWithReentrantLock(productName, orderAmount)
                } finally {
                    latch.countDown() // 작업 완료 후 카운트 감소
                }
            }
        }

        // 모든 스레드가 작업을 완료할 때까지 대기
        latch.await()
        executor.shutdown()

        val expectedStock = initialStock % orderAmount
        val actualStock = service.getStock(productName)

        assertEquals(expectedStock, actualStock, "재고 일치!")
    }
}
