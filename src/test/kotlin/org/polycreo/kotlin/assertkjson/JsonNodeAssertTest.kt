package org.polycreo.kotlin.assertkjson

import assertk.all
import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.containsExactly
import assertk.assertions.hasSize
import assertk.assertions.isBetween
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isLessThan
import assertk.assertions.isNotEmpty
import assertk.assertions.isTrue
import assertk.assertions.size
import com.fasterxml.jackson.databind.node.TextNode
import org.junit.Test
import java.math.BigInteger

class JsonNodeAssertTest {

    @Test
    fun testExample() {
        val objectJson = """
            {
              "foo": "aaa",
              "bar": 1,
              "baz": true,
              "qux": null,
              "quux": [ "bbb", "ccc" ],
              "corge": {
                "grault": "ddd",
                "garply": ""
              },
              "waldo": [
                {
                  "intmin": ${Int.MIN_VALUE},
                  "intmax": ${Int.MAX_VALUE},
                  "longmin": ${Long.MIN_VALUE},
                  "longmax": ${Long.MAX_VALUE},
                  "bigint": ${Long.MAX_VALUE.toBigInteger().plus(BigInteger.ONE)}
                },
                {
                  "doublemin": ${Double.MIN_VALUE},
                  "doublemax": ${Double.MAX_VALUE}
                },
                {}
              ]
            }
        """

        assertThat(jsonNodeOf(objectJson)).isObject().all {
            jsonPath("$.foo").isString().isEqualTo("aaa")
            jsonPath("$.foo").isEqualTo(TextNode.valueOf("aaa"))

            jsonPath("$.bar").isInt().isLessThan(10)
            jsonPath("$.baz").isBoolean().isTrue()
            jsonPath("$.qux").isNullLiteral()
            jsonPath("$.quux").isNotNullLiteral().isValueNodeArray().all {
                isNotEmpty()
                size().isLessThan(10)
                hasSize(2)
                containsExactly("bbb", "ccc")
            }
            jsonPath("$.corge").isObject().all {
                isNotEmpty()
                size().isBetween(1, 10)
                jsonPath("$.grault").isString().contains("DD", ignoreCase = true)
                jsonPath("$.garply").isString().isEmpty()
            }
            jsonPath("$.missing").isNotDefined()
            jsonPath("$.waldo").isDefined().isArray().all {
                size().isLessThan(10)
                hasSize(3)
            }
            jsonPath("$.waldo[0]").isObject().all {
                jsonPath("$.intmin").isInt()
                jsonPath("$.intmax").isInt()
                jsonPath("$.longmin").isLong()
                jsonPath("$.longmax").isLong()
                jsonPath("$.bigint").isBigInteger()
            }
            jsonPath("$.waldo[1]").isObject().all {
                jsonPath("$.doublemin").isDouble()
                jsonPath("$.doublemax").isDouble()
            }
            jsonPath("$.waldo[2]").isObject().isEmpty()
        }
    }
}
