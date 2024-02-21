package com.example.project.pharmacy.cache

import com.example.project.AbstractIntegrationContainerBaseTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.HashOperations
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.SetOperations
import org.springframework.data.redis.core.ValueOperations

class RedisTemplateTest extends AbstractIntegrationContainerBaseTest {

    @Autowired
    private RedisTemplate redisTemplate

    def "RedisTemplate String operations"() {

        given:

        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue() //String 자료구조
        String key = "stringKey"
        String value = "hello"

        when:
        valueOperations.set(key, value)

        then:
        String resultValue = valueOperations.get(key)
        resultValue == "hello"
    }

    def "RedisTemplate set operations"() { //set 자료구조
        given:
        SetOperations<String, String> setOperations = redisTemplate.opsForSet()
        String key = "setKey"
        when:

        setOperations.add(key, "h", "e", "l", "l", "o") //중복값을 자동으로 제거

        Set<String> members = setOperations.members(key)
        Long size = setOperations.size(key)

        then:

        members.containsAll(["h","e","l","o"])
        size == 4
    }

    def "RedisTemplate hash operations"() { //hash 자료구조
        given:
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash()
        String key = "hashKey"

        when:
        hashOperations.put(key, "subKey", "value")

        then:
        String value = hashOperations.get(key, "subKey")
        value == "value"

        Map<String, String> entries = hashOperations.entries(key) //map으로 다 뽑아보기
        entries.keySet().contains("subKey")
        entries.values().contains("value") //subkey와 value가 존재하는지

        Long size = hashOperations.size(key)
        size == entries.size()
    }
}
