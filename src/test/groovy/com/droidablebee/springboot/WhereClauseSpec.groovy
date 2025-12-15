package com.droidablebee.springboot


import spock.lang.Specification

class WhereClauseSpec extends Specification {

    def "reference variable in where clause more than once"() {

        expect:
        true

        where:
        a    | b
        null | a
        1    | a
        2    | a + a
    }

}

