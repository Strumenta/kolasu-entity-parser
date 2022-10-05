package com.strumenta.entity.parser

import com.andreapivetta.kolor.green
import com.andreapivetta.kolor.yellow
import com.strumenta.kolasu.emf.saveMetamodel
import com.strumenta.kolasu.emf.saveModel
import com.strumenta.kolasu.model.Node
import com.strumenta.kolasu.model.ReferenceByName
import com.strumenta.kolasu.model.pos
import com.strumenta.kolasu.parsing.FirstStageParsingResult
import com.strumenta.kolasu.parsing.ParsingResult
import com.strumenta.kolasu.serialization.JsonGenerator
import com.strumenta.kolasu.serialization.computeIdsForReferencedNodes
import com.strumenta.kolasu.testing.IgnoreChildren
import com.strumenta.kolasu.testing.assertASTsAreEqual
import com.strumenta.kolasu.testing.assertParsingResultsAreEqual
import com.strumenta.kolasu.traversing.findAncestorOfType
import com.strumenta.kolasu.traversing.findByPosition
import com.strumenta.kolasu.traversing.searchByPosition
import com.strumenta.kolasu.traversing.searchByType
import com.strumenta.kolasu.traversing.walk
import com.strumenta.kolasu.traversing.walkAncestors
import com.strumenta.kolasu.traversing.walkChildren
import com.strumenta.kolasu.traversing.walkDescendants
import com.strumenta.kolasu.traversing.walkLeavesFirst
import com.strumenta.kolasu.validation.Issue
import com.strumenta.kolasu.validation.IssueSeverity
import org.eclipse.emf.common.util.URI
import org.junit.Test
import java.io.File
import kotlin.io.path.Path

internal class ExpressionsTest {

    private val parser: EntityParser = EntityParser()

    @Test
    fun nodeProperties() {
        getAST("/test/expression")
            .walk()
            .forEach(this::printNode)
    }

    @Test
    fun testExpressionsAST() {
        getAST("/test/expression")
            .walk()
            .filter { it is Expression }
            .forEach(this::printNode)
    }

    private fun printNode(node: Node) {
        // what type of node is this?
        println("NodeType: ".yellow() + node.nodeType)
        // what are the properties of this node?
        println("Properties: ".yellow() + node.properties.joinToString { it.name })
        // where does this node come from?
        println("Origin: ".yellow() + node.origin)
        // who is the parent of this node, if any?
        println("Parent: ".yellow() + node.parent)
        // what is the exact position of this node?
        println("Position: ".yellow() + node.position)
        // what text does this node correspond to?
        println("Source Text: ".yellow() + node.sourceText)
        // what node has been created started from this one?
        println("Destination: ".yellow() + node.destination)
        // type-specific information
        when (node) {
            is Module -> {
                println("Name: ".green() + node.name)
                println("Entities: ".green() + node.entities)
            }

            is Entity -> {
                println("Name: ".green() + node.name)
                println("Features: ".green() + node.features)
            }

            is Feature -> {
                println("Name: ".green() + node.name)
                println("Type: ".green() + node.type)
            }

            is EntityRefType -> {
                println("Target: ".green() + node.target)
            }

            is LiteralExpression -> {
                println("Value: ".green() + node.value)
            }
            is BinaryExpression -> {
                println("Left: ".green() + node.left)
                println("Right: ".green() + node.right)
                println("Operator: ".green() + node.operator)
            }
            is FqnExpression -> {
                println("Value: ".green() + node.target)
            }
        }
        println()
    }

    private fun getResourceContent(resourcePath: String): String {
        return ExpressionsTest::class.java.getResource(resourcePath).readText()
    }

    private fun getParseTree(resourcePath: String): FirstStageParsingResult<AntlrEntityParser.ModuleContext> {
        val input = getResourceContent(resourcePath)
        return parser.parseFirstStage(input)
    }

    private fun getAST(resourcePath: String): Module {
        val input = getResourceContent(resourcePath)
        val ast = this.parser.parse(input) // parse code and build AST
        return ast.root!! as Module // retrieve AST root module
    }
}