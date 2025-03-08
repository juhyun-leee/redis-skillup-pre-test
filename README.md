## 사전 과제 - 동시성 이슈

### 요구 사항

---

- 예제 코드는 동시성 이슈가 발생하는 코드입니다. 동시성 이슈가 발생하지 않도록 **Thread safe 한 코드**를 작성하여 동시성 이슈를 해결하세요. (단, Redis 는 사용하지 않습니다.)
    - 위 코드는 Tomcat 과 같은 WAS 에서 동작함을 가정합니다. (즉, ThreadPool 이 사용됨을 가정합니다.)
    - 멀티 스레드 환경에서 다수의 스레드가 동시에 접근하여 자원을 변경하려는 경우, 데이터 불일치 등의 문제가 발생할 수 있습니다. 이러한 환경에서도 데이터의 일관성을 유지할 수 있는 코드를 **Thread-safe** 한 코드라고 합니다.
    - 키워드: java.util.concurrent 패키지, ThreadLocal
- latestOrderDatabase 는 유저가 구매한 가장 최근의 주문 목록을 저장합니다. 편의상 key 는 ProductName 으로 설정했습니다.
    - 예를 들어 UserA 가 사과를 2개, 3개 총 2번 구매한 경우 latestOrderDatabase 에서 사과로 꺼낼 시 총 3개가 되어야 합니다.
    - latestOrderDatabase 유저별로 독립된 저장소여야 합니다.
- 작성한 코드가 Thread-safe 한 코드인지는 테스트 코드(Junit5)를 작성하여 검증해주세요. 제약 조건은 다음과 같습니다.
    - 제약 조건
        - 요청당 스레드별 주문 량은 8 로 고정 (orderAmount)
        - 스레드 카운트는 100으로 고정
        - 구매 상품은 apple 로 고정 (사과 개수는 100개)
    - 위 제약 조건에 따라 총 12번의 구매가 이뤄져야 하며 expectedStock 은 4개여야 합니다.
        - e.g `assertEquals(finalStock, expectedStock)`
    - 테스트 코드의 실행 결과를 확인할 수 있도록 아래 그림과 같이 로그가 남도록 작성해주세요.

### 기술 스택

---

- Kotlin
- Junit5
- Gradle

### 구현

---

- 분산 서버가 아닌, 단일 서버라고 가정하여 아래의 방식으로 구현
  - synchronized 블록
  - ConcurrentHashMap.compute() -> synchronized
  - ReentrantLock
