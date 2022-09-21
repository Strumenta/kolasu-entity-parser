package com.strumenta.entity.parser

import com.strumenta.kolasu.model.Node
import com.strumenta.kolasu.model.Origin
import com.strumenta.kolasu.transformation.ASTTransformer
import com.strumenta.kolasu.traversing.findAncestorOfType
import com.strumenta.kolasu.traversing.searchByType
import com.strumenta.kolasu.validation.Issue
import com.strumenta.kolasu.validation.IssueSeverity
import kotlin.reflect.KClass

class EntitySymbolResolver(issues: MutableList<Issue> = mutableListOf()) : ASTTransformer(issues) {

    init {
        registerModuleMapping()
        registerEntityMapping()
        registerFeatureMapping()
        registerTypeMappings()
    }

    private fun registerModuleMapping() {
        this.registerIdentityMapping(Module::class).withChild(Module::entities, Module::entities)
    }

    private fun registerEntityMapping() {
        this.registerIdentityMapping(Entity::class).withChild(Entity::features, Entity::features)
    }

    private fun registerFeatureMapping() {
        this.registerIdentityMapping(Feature::class).withChild(Feature::type, Feature::type)
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

    override fun asOrigin(source: Any): Origin? {
        return (source as Node).origin
    }

    private fun <S : Node> registerIdentityMapping(kclass: KClass<S>) =
        this.registerNodeFactory(kclass) { source -> source }
}
