package com.strumenta.entity.parser

import com.strumenta.kolasu.model.Node
import com.strumenta.kolasu.model.PossiblyNamed
import com.strumenta.kolasu.model.ReferenceByName

open class EntityNode(
    override val name: String? = null
) : Node(), PossiblyNamed

data class Module(
    override val name: String? = null,
    var entities: List<Entity> = listOf()
) : EntityNode(name)

data class Entity(
    override val name: String? = null,
    var features: List<Feature> = listOf()
) : EntityNode(name)

data class Feature(
    override val name: String? = null,
    var type: Type? = null,
    var value: Expression? = null
) : EntityNode(name)

open class Type : Node()

open class PrimitiveType : Type()
class StringType : PrimitiveType()
class IntegerType : PrimitiveType()
class BooleanType : PrimitiveType()

data class EntityRefType(
    var target: ReferenceByName<Entity>
) : Type()

open class Expression(
    var type: Type? = null
) : Node()
data class LiteralExpression(
    val value: String?
): Expression()
data class BinaryExpression(
    var left: Expression? = null,
    var right: Expression? = null,
    val operator: BinaryOperator
) : Expression()
data class FqnExpression(
    val target: ReferenceByName<EntityNode>,
    val context: FqnExpression? = null
) : Expression()
enum class BinaryOperator(val symbol: String) {
    SUM("+"),
    SUB("-"),
    MUL("*"),
    DIV("/");

    companion object {
        fun bySymbol(symbol: String) : BinaryOperator? {
            return BinaryOperator.values().firstOrNull{ it.symbol == symbol }
        }
    }
}
