package me.mlynch.domain

import spock.lang.Specification

class BoardSpec extends Specification {

    def 'can be instantiated'() {
        given:
        def letters = [
                ['a',  'b', 'c', 'd'],
                ['a',  'b', 'c', 'd'],
                ['a',  'b', 'c', 'd'],
                ['a',  'b', 'c', 'd'],
        ]

        when:
        new Board(letters)

        then:
        noExceptionThrown()
    }

}
