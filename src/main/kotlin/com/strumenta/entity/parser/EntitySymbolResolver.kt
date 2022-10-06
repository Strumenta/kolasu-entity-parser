package com.strumenta.entity.parser

import com.strumenta.kolasu.model.Node
import com.strumenta.kolasu.model.Origin
import com.strumenta.kolasu.transformation.ASTTransformer
import com.strumenta.kolasu.traversing.findAncestorOfType
import com.strumenta.kolasu.traversing.searchByType
import com.strumenta.kolasu.traversing.walkChildren
import com.strumenta.kolasu.validation.Issue
import com.strumenta.kolasu.validation.IssueSeverity
import java.util.Stack
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

                if (this.target.resolved)
                    return@apply

                val fqnNames = getFqnStack(this)
                val leftmostFqn = fqnNames.pop()

                // search for the leftmost name, from current scope to outer scopes
                leftmostFqn.target.referred = this.findAncestorOfType(Entity::class.java)
                    ?.searchByType(Feature::class.java)
                    ?.find { it.name == leftmostFqn.target.name }
                    ?: this.findAncestorOfType(Module::class.java)
                        ?.searchByType(Entity::class.java)
                        ?.find { it.name == leftmostFqn.target.name }
                            ?: this.findAncestorOfType(Module::class.java)
                        .takeIf { it?.name == leftmostFqn.target.name }

                if (!leftmostFqn.target.resolved) {
                    issues.add(
                        Issue.semantic(
                            "Name not found: ${leftmostFqn.target.name} in context: ${leftmostFqn.context}",
                            IssueSeverity.ERROR,
                            position = this.position
                        )
                    )
                }

                // search into inner nodes for possible remaining names
                var scopeNode = leftmostFqn.target.referred
                while (scopeNode != null && fqnNames.isNotEmpty()) {
                    val currentFqn = fqnNames.pop()
                    currentFqn.target.referred = scopeNode.walkChildren()
                        .filter { childNode ->
                            when (childNode) {
                                is Entity -> childNode.name == currentFqn.target.name
                                is Feature -> childNode.name == currentFqn.target.name
                                else -> false
                            }
                        }
                        .map { it as EntityNode }
                        .firstOrNull()

                    scopeNode = if (currentFqn.target.resolved) {
                        currentFqn.target.referred
                    } else {
                        null
                    }

                    if (!currentFqn.target.resolved) {
                        issues.add(
                            Issue.semantic(
                                "Name not found: ${currentFqn.target.name} in context: ${currentFqn.context}",
                                IssueSeverity.ERROR,
                                position = this.position
                            )
                        )
                    }
                }
            }
        }
    }

    private fun getFqnStack(fqn: FqnExpression, stack: Stack<FqnExpression> = Stack()): Stack<FqnExpression> {
        return if (fqn == null)
            stack
        else {
            stack.add(fqn)
            fqn.context?.let{ getFqnStack(it, stack) } ?: stack
        }
    }

    override fun asOrigin(source: Any): Origin? {
        return (source as Node).origin
    }

    private fun <S : Node> registerIdentityMapping(kclass: KClass<S>) =
        this.registerNodeFactory(kclass) { source -> source }
}
