package com.droidablebee.springboot.rest.service


import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import spock.lang.Specification

@SpringBootTest
class CacheableServiceSpec extends Specification {

    @Autowired
    CacheableService cacheableService

    @SpringBean
    CacheableService.CacheableServiceClient cacheableServiceClient = Mock()

    void setup() {

        0 * _
    }

    def "retrieve - cached"() {

        given:
        PageRequest pageRequest1 = PageRequest.of(0, 10)
        PageRequest pageRequest2 = PageRequest.of(0, 20)
        Page<CacheableService.Result> page1 = Mock()
        Page<CacheableService.Result> page2 = Mock()

        when: "first service call is made"
        Page<CacheableService.Result> result = cacheableService.retrieve(pageRequest1)

        then: "client method is called"
        1 * cacheableServiceClient.retrieve(pageRequest1) >> page1

        result == page1

        when: "subsequent service call is made"
        Page<CacheableService.Result> cached = cacheableService.retrieve(pageRequest1)

        then: "cached value is returned w/out client method call"
        cached == result

        when: "different parameter is used"
        result = cacheableService.retrieve(pageRequest2)

        then: "client method is called"
        1 * cacheableServiceClient.retrieve(pageRequest2) >> page2

        result == page2

        when: "subsequent service call is made"
        cached = cacheableService.retrieve(pageRequest2)

        then: "cached value is returned w/out client method call"
        cached == result
    }

}
