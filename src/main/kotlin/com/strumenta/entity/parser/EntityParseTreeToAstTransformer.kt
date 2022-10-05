package com.strumenta.entity.parser

import com.strumenta.kolasu.mapping.ParseTreeToASTTransformer
import com.strumenta.kolasu.model.ReferenceByName
import com.strumenta.kolasu.validation.Issue

class EntityParseTreeToAstTransformer(issues: MutableList<Issue> = mutableListOf()) :
    ParseTreeToASTTransformer(issues) {

    init {
        registerModuleMapping()
        registerEntityMapping()
        registerFeatureMapping()
        registerTypeMappings()
        registerExpressionMappings()
    }

    private fun registerModuleMapping() {
        this.registerNodeFactory(AntlrEntityParser.ModuleContext::class) { ctx -> Module(name = ctx.name.text) }
            .withChild(AntlrEntityParser.ModuleContext::entities, Module::entities)
    }

    private fun registerEntityMapping() {
        this.registerNodeFactory(AntlrEntityParser.EntityContext::class) { ctx -> Entity(name = ctx.name.text) }
            .withChild(AntlrEntityParser.EntityContext::features, Entity::features)
    }

    private fun registerFeatureMapping() {
        this.registerNodeFactory(AntlrEntityParser.FeatureContext::class) { ctx -> Feature(name = ctx.name.text) }
            .withChild(AntlrEntityParser.FeatureContext::type, Feature::type)
            .withChild(AntlrEntityParser.FeatureContext::value, Feature::value)
    }

    private fun registerTypeMappings() {
        this.registerNodeFactory(AntlrEntityParser.String_typeContext::class, StringType::class)
        this.registerNodeFactory(AntlrEntityParser.Integer_typeContext::class, IntegerType::class)
        this.registerNodeFactory(AntlrEntityParser.Boolean_typeContext::class, BooleanType::class)
        this.registerNodeFactory(AntlrEntityParser.Entity_typeContext::class) { source ->
            EntityRefType(target = ReferenceByName(name = source.target.text))
        }
    }

    private fun registerExpressionMappings() {
        this.registerNodeFactory(AntlrEntityParser.Literal_expressionContext::class) { ctx -> LiteralExpression(ctx.value.text) }
        this.registerNodeFactory(AntlrEntityParser.Fqn_expressionContext::class) { ctx ->
            FqnExpression(ReferenceByName(ctx.target.text))
        }

        this.registerNodeFactory(AntlrEntityParser.Binary_expressionContext::class) { ctx ->
            BinaryExpression(operator = BinaryOperator.bySymbol(ctx.op.text)!!)
        }
            .withChild(AntlrEntityParser.Binary_expressionContext::left, BinaryExpression::left)
            .withChild(AntlrEntityParser.Binary_expressionContext::right, BinaryExpression::right)
    }
}
