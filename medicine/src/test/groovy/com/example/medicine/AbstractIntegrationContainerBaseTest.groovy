package com.example.medicine

import org.springframework.boot.test.context.SpringBootTest
import org.testcontainers.containers.GenericContainer
import spock.lang.Specification

@SpringBootTest
abstract class AbstractIntegrationContainerBaseTest extends Specification {

    static final GenericContainer MY_REDIS_CONTAINER

    static {
        MY_REDIS_CONTAINER = new GenericContainer<>("redis:6")
                .withExposedPorts(6379) // docker에서 expose port

        MY_REDIS_CONTAINER.start() // docker container 실행 -> 실행 중이 아닌 랜덤한 포트로 mapping해줌

        System.setProperty("spring.redis.host", MY_REDIS_CONTAINER.getHost()) // 호스트 알려주기
        System.setProperty("spring.redis.port", MY_REDIS_CONTAINER.getMappedPort(6379).toString()) // 어떤 포트로 mapping되었는지 알려주기
    }
}
