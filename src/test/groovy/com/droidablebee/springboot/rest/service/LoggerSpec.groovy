package com.droidablebee.springboot.rest.service

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.LoggingEvent
import ch.qos.logback.core.Appender
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import spock.lang.Specification

class LoggerSpec extends Specification {

    ch.qos.logback.classic.Logger logger = LoggerFactory.getLogger(TestService.class) as ch.qos.logback.classic.Logger
    Appender mockAppender = Mock()

    void setup() {
        logger.addAppender(mockAppender)
    }

    void cleanup() {
        logger.detachAppender(mockAppender)
    }

    def "process logs either an info or an error level message with MDC"() {

        given:
        TestService testService = new TestService()

        when:
        testService.convert(input)

        then:
        1 * mockAppender.doAppend({ LoggingEvent e ->
            assert e.MDCPropertyMap[TestService.ID] == input
            if (message == TestService.INF) {
                assert e.level == Level.INFO
                assert e.message == TestService.INF
                assert e.argumentArray.toList() == [input, expected]
                assert e.throwableProxy == null
            } else {
                assert e.level == Level.ERROR
                assert e.message == TestService.ERR
                assert e.argumentArray.toList() == [input]
                assert e.throwableProxy.className == NumberFormatException.name
            }
        })

        where:
        input  | expected              | message
        "1"    | new BigDecimal(input) | TestService.INF
        "3.14" | new BigDecimal(input) | TestService.INF
        "a"    | null                  | TestService.ERR
    }

    static class TestService {

        static final Logger logger = LoggerFactory.getLogger(TestService.class)
        static final String INF = "successfully converted input: {} to BigDecimal: {}"
        static final String ERR = "Failed to convert input: {}"
        static final String ID = "testID"

        BigDecimal convert(String input) {

            BigDecimal result = null

            try {
                MDC.put(ID, input)
                result = new BigDecimal(input)
                logger.info(INF, input, result)
            } catch (Exception e) {
                logger.error(ERR, input, e)
            } finally {
                MDC.remove(ID)
            }

            return result
        }
    }
}
