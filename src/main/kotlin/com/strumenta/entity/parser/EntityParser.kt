package com.strumenta.entity.parser

import com.strumenta.kolasu.emf.EMFEnabledParser
import com.strumenta.kolasu.model.Node
import com.strumenta.kolasu.validation.Issue
import org.antlr.v4.runtime.CharStream
import org.antlr.v4.runtime.Lexer
import org.antlr.v4.runtime.TokenStream
import org.eclipse.emf.ecore.resource.Resource

class EntityParser : EMFEnabledParser<Node, AntlrEntityParser, AntlrEntityParser.ModuleContext>() {

    override fun createANTLRLexer(charStream: CharStream): Lexer = AntlrEntityLexer(charStream)

    override fun createANTLRParser(tokenStream: TokenStream): AntlrEntityParser = AntlrEntityParser(tokenStream)

    override fun doGenerateMetamodel(resource: Resource) = EntityMetamodelBuilder(resource).generate().let { }

    override fun parseTreeToAst(
        parseTreeRoot: AntlrEntityParser.ModuleContext,
        considerPosition: Boolean,
        issues: MutableList<Issue>
    ): Node? = EntityParseTreeToAstTransformer(issues).transform(parseTreeRoot)

    override fun postProcessAst(ast: Node, issues: MutableList<Issue>): Node =
        super.postProcessAst(ast, issues)
            .let { this.resolveSymbols(it, issues) }
            .let { this.typeChecking(it, issues) }

    private fun resolveSymbols(ast: Node, issues: MutableList<Issue>): Node {
        return EntitySymbolResolver(issues).transform(ast)!!
    }

    private fun typeChecking(ast: Node, issues: MutableList<Issue>): Node {
        return EntityTypeComputer(issues).transform(ast)!!
    }
}
