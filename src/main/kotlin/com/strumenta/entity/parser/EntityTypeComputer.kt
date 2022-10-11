package com.strumenta.entity.parser

import com.strumenta.kolasu.model.Node
import com.strumenta.kolasu.model.Origin
import com.strumenta.kolasu.transformation.ASTTransformer
import com.strumenta.kolasu.traversing.*
import com.strumenta.kolasu.validation.Issue
import com.strumenta.kolasu.validation.IssueSeverity
import kotlin.RuntimeException
import kotlin.reflect.KClass

class EntityTypeComputer(issues: MutableList<Issue> = mutableListOf()) : ASTTransformer(issues) {

    private val operations = Operations()

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
                    .forEach { binaryExpression ->
                        binaryExpression.type = try {
                            operations.applyFunction(
                                binaryExpression.operator,
                                binaryExpression.left!!.type!!,
                                binaryExpression.right!!.type!!)
                        } catch (e : RuntimeException) {
                            issues.add(Issue.semantic(
                                e.message ?: e.toString(),
                                IssueSeverity.ERROR,
                                position = this.position))
                            null
                        }
                    }
            }
        }
        this.registerNodeFactory(FqnExpression::class) { source ->
            source.apply {
                if (source.target.resolved)
                    source.type = getFeatureType(source.target.referred as Feature)
            }
        }
    }

    override fun asOrigin(source: Any): Origin? {
        return (source as Node).origin
    }

    private fun <S : Node> registerIdentityMapping(kclass: KClass<S>) =
        this.registerNodeFactory(kclass) { source -> source }

    private fun getFeatureType(feature: Feature) : Type? {
        return feature.type ?: feature.value?.type
    }
}
