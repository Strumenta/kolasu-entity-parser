package com.strumenta.entity.parser

import com.strumenta.kolasu.model.Node
import com.strumenta.kolasu.model.Origin
import com.strumenta.kolasu.transformation.ASTTransformer
import com.strumenta.kolasu.traversing.findAncestorOfType
import com.strumenta.kolasu.traversing.searchByType
import com.strumenta.kolasu.traversing.walkAncestors
import com.strumenta.kolasu.traversing.walkLeavesFirst
import com.strumenta.kolasu.validation.Issue
import com.strumenta.kolasu.validation.IssueSeverity
import java.util.LinkedList
import java.util.Queue
import kotlin.reflect.KClass

class EntitySymbolResolver(issues: MutableList<Issue> = mutableListOf()) : ASTTransformer(issues) {

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
        this.registerNodeFactory(EntityRefType::class) { source ->
            source.apply {
                this.target.referred = this.findAncestorOfType(Module::class.java)?.searchByType(Entity::class.java)
                    ?.find { it.name == this.target.name }
                if (!this.target.resolved) {
                    this@EntitySymbolResolver.issues.add(
                        Issue.semantic(
                            "Entity ${this.target.name} not found",
                            IssueSeverity.ERROR,
                            position = this.position
                        )
                    )
                }
            }
        }
    }

    private fun registerExpressionMappings() {
        this.registerIdentityMapping(LiteralExpression::class)
        this.registerIdentityMapping(BinaryExpression::class)
            .withChild(BinaryExpression::left, BinaryExpression::left)
            .withChild(BinaryExpression::right, BinaryExpression::right)
        this.registerNodeFactory(FqnExpression::class) { source ->
            source.apply {
                val fqnNames = getRightToLeftFqnQueue(source)
                val rightmostFqn = fqnNames.poll()
                var candidateFeatures = source.findAncestorOfType(Module::class.java)!!
                    .searchByType(Feature::class.java)
                    .filter { feature ->
                        feature.name == rightmostFqn.target.name
                    }
                    .filter { feature ->
                        val fqnNamesList = fqnNames.asSequence()
                            .map { it.target.name }
                            .toList()
                        val ancestorNamesList = feature.walkAncestors()
                            .map { (it as EntityNode).name }
                            .toList()
                            .subList(0, fqnNamesList.size)
                        fqnNamesList == ancestorNamesList
                    }
                    .toList()

                if (candidateFeatures.isEmpty()) {
                    issues.add(Issue.semantic(
                        "No features found for name: ${rightmostFqn.sourceText}",
                        IssueSeverity.ERROR,
                        position = this.position
                    ))
                } else if (candidateFeatures.count() > 1) {
                    issues.add(Issue.semantic(
                        "Ambiguous name: ${rightmostFqn.sourceText} matches with multiple nodes: "
                    + candidateFeatures.joinToString(
                            separator = "\n",
                            prefix = "\n") { f -> "> $f" },
                        IssueSeverity.ERROR,
                        position = this.position))
                } else {
                    var node : EntityNode? = candidateFeatures.first() as EntityNode
                    var fqn : FqnExpression? = source

                    while (fqn != null) {
                        fqn.target.referred = node
                        fqn = fqn.context
                        node = node?.parent as? EntityNode
                    }
                }
            }
        }
    }

    private fun getRightToLeftFqnQueue(fqn: FqnExpression, fqnList: Queue<FqnExpression> = LinkedList()): Queue<FqnExpression> {
        return if (fqn == null)
            fqnList
        else {
            fqnList.add(fqn)
            fqn.context?.let{ getRightToLeftFqnQueue(it, fqnList) } ?: fqnList
        }
    }

    override fun asOrigin(source: Any): Origin? {
        return (source as Node).origin
    }

    private fun <S : Node> registerIdentityMapping(kclass: KClass<S>) =
        this.registerNodeFactory(kclass) { source -> source }
}
