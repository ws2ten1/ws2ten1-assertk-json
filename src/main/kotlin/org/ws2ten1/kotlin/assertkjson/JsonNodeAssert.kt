package org.ws2ten1.kotlin.assertkjson

import assertk.Assert
import assertk.assertions.prop
import assertk.assertions.support.expected
import assertk.assertions.support.show
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.BigIntegerNode
import com.fasterxml.jackson.databind.node.BooleanNode
import com.fasterxml.jackson.databind.node.ContainerNode
import com.fasterxml.jackson.databind.node.DecimalNode
import com.fasterxml.jackson.databind.node.DoubleNode
import com.fasterxml.jackson.databind.node.FloatNode
import com.fasterxml.jackson.databind.node.IntNode
import com.fasterxml.jackson.databind.node.LongNode
import com.fasterxml.jackson.databind.node.MissingNode
import com.fasterxml.jackson.databind.node.NullNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.ShortNode
import com.fasterxml.jackson.databind.node.TextNode
import com.fasterxml.jackson.databind.node.ValueNode
import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.JsonPath
import com.jayway.jsonpath.PathNotFoundException
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider
import java.math.BigDecimal
import java.math.BigInteger

private val mapper = ObjectMapper()

private val jsonPathConf = Configuration.builder()
    .jsonProvider(JacksonJsonNodeJsonProvider(mapper))
    .build()

/**
 * Create [JsonNode] from [str].
 */
fun jsonNodeOf(str: String) = mapper.readTree(str)

/**
 * Assert receiver node is [IntNode] and transform to [Int] assert.
 */
fun Assert<JsonNode>.isInt(): Assert<Int> = transform { actual ->
    if (actual.isInt) return@transform actual.intValue()
    expected("class:${show(IntNode::class)} but was class:${show(actual::class)}")
}

/**
 * Assert receiver node is [ShortNode] and transform to [Short] assert.
 */
fun Assert<JsonNode>.isShort(): Assert<Short> = transform { actual ->
    if (actual.isShort) return@transform actual.shortValue()
    expected("class:${show(ShortNode::class)} but was class:${show(actual::class)}")
}

/**
 * Assert receiver node is [LongNode] and transform to [Long] assert.
 */
fun Assert<JsonNode>.isLong(): Assert<Long> = transform { actual ->
    if (actual.isLong) return@transform actual.longValue()
    expected("class:${show(LongNode::class)} but was class:${show(actual::class)}")
}

/**
 * Assert receiver node is [BigIntegerNode] and transform to [BigInteger] assert.
 */
fun Assert<JsonNode>.isBigInteger(): Assert<BigInteger> = transform { actual ->
    if (actual.isBigInteger) return@transform actual.bigIntegerValue()
    expected("class:${show(BigIntegerNode::class)} but was class:${show(actual::class)}")
}

/**
 * Assert receiver node is [TextNode] and transform to [String] assert.
 */
fun Assert<JsonNode>.isString(): Assert<String> = transform { actual ->
    if (actual.isTextual) return@transform actual.textValue()
    expected("class:${show(TextNode::class)} but was class:${show(actual::class)}")
}

/**
 * Assert receiver node is [FloatNode] and transform to [Float] assert.
 */
fun Assert<JsonNode>.isFloat(): Assert<Float> = transform { actual ->
    if (actual.isFloat) return@transform actual.floatValue()
    expected("class:${show(FloatNode::class)} but was class:${show(actual::class)}")
}

/**
 * Assert receiver node is [DoubleNode] and transform to [Double] assert.
 */
fun Assert<JsonNode>.isDouble(): Assert<Double> = transform { actual ->
    if (actual.isDouble) return@transform actual.doubleValue()
    expected("class:${show(DoubleNode::class)} but was class:${show(actual::class)}")
}

/**
 * Assert receiver node is [DecimalNode] and transform to [BigDecimal] assert.
 */
fun Assert<JsonNode>.isBigDecimal(): Assert<BigDecimal> = transform { actual ->
    if (actual.isBigDecimal) return@transform actual.decimalValue()
    expected("class:${show(DecimalNode::class)} but was class:${show(actual::class)}")
}

/**
 * Assert receiver node is [BooleanNode] and transform to [Boolean] assert.
 */
fun Assert<JsonNode>.isBoolean(): Assert<Boolean> = transform { actual ->
    if (actual.isBoolean) return@transform actual.booleanValue()
    expected("class:${show(BooleanNode::class)} but was class:${show(actual::class)}")
}

/**
 * Assert receiver node is [ObjectNode] and transform to [ObjectNode] assert.
 */
fun Assert<JsonNode>.isObject(): Assert<ObjectNode> = transform { actual ->
    if (actual.isObject) return@transform actual as ObjectNode
    expected("class:${show(ObjectNode::class)} but was class:${show(actual::class)}")
}

/**
 * Assert receiver node is [ArrayNode] and transform to [ArrayNode] assert.
 */
fun Assert<JsonNode>.isArray(): Assert<ArrayNode> = transform { actual ->
    if (actual.isArray) return@transform actual as ArrayNode
    expected("class:${show(ArrayNode::class)} but was class:${show(actual::class)}")
}

/**
 * Assert [ContainerNode] has size [expected].
 */
fun Assert<ContainerNode<*>>.hasSize(expected: Int) = given { actual ->
    if (actual.size() == expected) return
    expected("size:${show(expected)} but was size:${show(actual.size())}")
}

/**
 * Transform to [ContainerNode.size] assert.
 */
fun Assert<ContainerNode<*>>.size() = prop("size", ContainerNode<*>::size)

/**
 * Asserts the [ContainerNode] is empty.
 */
fun Assert<ContainerNode<*>>.isEmpty() = given { actual ->
    if (actual.size() == 0) return
    expected("to be empty but was:${show(actual.size())}")
}

/**
 * Asserts the [ContainerNode] is not empty.
 */
fun Assert<ContainerNode<*>>.isNotEmpty() = given { actual ->
    if (actual.size() > 0) return
    expected("to not be empty")
}

/**
 * Assert receiver node is [ArrayNode], all elements are [ValueNode], and transform to [List] assert.
 */
fun Assert<JsonNode>.isValueNodeArray(): Assert<List<Any?>> = transform { actual ->
    if (actual.isArray) {
        return@transform Sequence { actual.elements() }.onEach {
            if (it is ValueNode == false) {
                expected("element class:${show(ValueNode::class)} but was element class:${show(it::class)}")
            }
        }.map {
            @Suppress("IMPLICIT_CAST_TO_ANY")
            when (it) {
                is DoubleNode -> it.doubleValue()
                is FloatNode -> it.floatValue()
                is IntNode -> it.intValue()
                is BigIntegerNode -> it.bigIntegerValue()
                is DecimalNode -> it.decimalValue()
                is ShortNode -> it.shortValue()
                is LongNode -> it.longValue()
                is NullNode -> null
                is BooleanNode -> it.booleanValue()
                is TextNode -> it.textValue()
                else -> throw AssertionError("Unexpected node type ${it::class.java}")
            }
        }.toList()
    }
    expected("class:${show(ArrayNode::class)} but was class:${show(actual::class)}")
}

/**
 * Assert receiver node is [NullNode] and transform to [NullNode] assert.
 */
fun Assert<JsonNode>.isNullLiteral(): Assert<NullNode> = transform { actual ->
    if (actual.isNull) return@transform actual as NullNode
    expected("class:${show(NullNode::class)} but was class:${show(actual::class)}")
}

/**
 * Assert receiver node is not [NullNode].
 */
fun Assert<JsonNode>.isNotNullLiteral(): Assert<JsonNode> = transform { actual ->
    if (actual.isNull == false) return@transform actual
    expected("class:not ${show(NullNode::class)} but was class:${show(actual::class)}")
}

/**
 * Assert receiver node is defined.
 */
fun Assert<JsonNode>.isDefined(): Assert<JsonNode> = transform { actual ->
    if (actual.isMissingNode == false) return@transform actual
    expected("class:${show(JsonNode::class)} but was class:${show(actual::class)}")
}

/**
 * Assert receiver node is not defined ([MissingNode]).
 */
fun Assert<JsonNode>.isNotDefined(): Assert<MissingNode> = transform { actual ->
    if (actual.isMissingNode) return@transform actual as MissingNode
    expected("class:${show(MissingNode::class)} but was class:${show(actual::class)}")
}

/**
 * Transform to [path] specified [JsonNode] assert.
 */
fun Assert<JsonNode>.jsonPath(path: String): Assert<JsonNode> = transform { actual ->
    try {
        return@transform JsonPath.using(jsonPathConf).parse(actual).read<JsonNode>(path)
    } catch (e: PathNotFoundException) {
        return@transform MissingNode.getInstance()
    }
}
