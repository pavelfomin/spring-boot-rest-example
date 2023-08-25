package com.droidablebee.springboot.rest

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import spock.lang.Specification

@SpringBootTest
class ApplicationSpec extends Specification {

    @Autowired
    ApplicationContext context

    def "context initializes successfully"() {

        expect:
        context
        context.environment
    }
}
