package com.strumenta.entity.parser

import com.strumenta.kolasu.model.Node
import com.strumenta.kolasu.model.Origin
import com.strumenta.kolasu.transformation.ASTTransformer
import com.strumenta.kolasu.traversing.*
import com.strumenta.kolasu.validation.Issue
import com.strumenta.kolasu.validation.IssueSeverity
import java.util.Stack
import kotlin.reflect.KClass

class EntityTypeComputer(issues: MutableList<Issue> = mutableListOf()) : ASTTransformer(issues) {

    init {
        registerModuleMapping()
        registerEntityMapping()
        registerFeatureMapping()
        registerTypeMappings()
        registerExpressionMappings()
    }

    private fun registerModuleMapping() {
        this.registerIdentityMapping(Module::class).withChild(Module::entities, Module::entities)
    }

    private fun registerEntityMapping() {
        this.registerIdentityMapping(Entity::class).withChild(Entity::features, Entity::features)
    }

    private fun registerFeatureMapping() {
        this.registerIdentityMapping(Feature::class)
            .withChild(Feature::type, Feature::type)
            .withChild(Feature::value, Feature::value)
    }

    private fun registerTypeMappings() {
        this.registerIdentityMapping(StringType::class)
        this.registerIdentityMapping(IntegerType::class)
        this.registerIdentityMapping(BooleanType::class)
        this.registerIdentityMapping(EntityRefType::class)
    }

    private fun registerExpressionMappings() {
        this.registerIdentityMapping(LiteralExpression::class)
        this.registerNodeFactory(BinaryExpression::class) { source ->
            source.apply {
                source.walkLeavesFirst()
                    .filter { it is BinaryExpression }
                    .map { it as BinaryExpression }
                    .forEach {
                        val left = it.left!!.type
                        val right = it.right!!.type
                        val operator = it.operator
                        when {
                            (left is IntegerType && right is IntegerType)
                                    || (left is StringType && right is StringType && operator == BinaryOperator.SUM) -> {
                                it.type = left
                            }

                            else -> issues.add(
                                Issue.semantic(
                                    "Operator $operator cannot be applied to: $left and $right",
                                    IssueSeverity.ERROR,
                                    position = this.position
                                )
                            )
                        }
                    }
            }
        }
        this.registerNodeFactory(FqnExpression::class) { source ->
            source.apply {
                if (source.target.resolved)
                    source.type = (source.target.referred as Feature).type
            }
        }
    }

    override fun asOrigin(source: Any): Origin? {
        return (source as Node).origin
    }

    private fun <S : Node> registerIdentityMapping(kclass: KClass<S>) =
        this.registerNodeFactory(kclass) { source -> source }
}
