package com.strumenta.entity.parser

import com.andreapivetta.kolor.cyan
import com.andreapivetta.kolor.green
import com.andreapivetta.kolor.yellow
import com.strumenta.kolasu.model.Node
import com.strumenta.kolasu.parsing.FirstStageParsingResult
import com.strumenta.kolasu.parsing.ParsingResult
import com.strumenta.kolasu.traversing.walk
import org.junit.Test

internal class TypeResolutionTest {

    private val parser: EntityParser = EntityParser()

    @Test
    fun testTypesAST() {
        val sourceFilePath = "/test/expression_types"
        val ast = getAST(sourceFilePath)
        val expressionNodes = ast.root!!
            .walk()
            .filter { it is Expression }
            .map { it as Expression }
            .toList()

        expressionNodes.forEach { printNode(it, sourceFilePath) }

        println(ast.issues.joinToString(
            separator = "\n",
            prefix = "Issues list:\n",
            postfix = "") { "> " + it.toString().cyan() })

        // TODO: checks
    }

    @Test
    fun testBinaryExpressionTypesAST() {
        val sourceFilePath = "/test/expression_types"
        val ast = getAST(sourceFilePath)
        val expressionNodes = ast.root!!
            .walk()
            .filter { it is BinaryExpression }
            .map { it as BinaryExpression }
            .toList()

        expressionNodes.forEach { printNode(it, sourceFilePath) }

        println(ast.issues.joinToString(
            separator = "\n",
            prefix = "Issues list:\n",
            postfix = "") { "> " + it.toString().cyan() })

        // TODO: checks
    }

    @Test
    fun testFqnExpressionTypesAST() {
        val sourceFilePath = "/test/expression_types"
        val ast = getAST(sourceFilePath)
        val expressionNodes = ast.root!!
            .walk()
            .filter { it is FqnExpression }
            .map { it as FqnExpression }
            .toList()

        expressionNodes.forEach { printNode(it, sourceFilePath) }

        println(ast.issues.joinToString(
            separator = "\n",
            prefix = "Issues list:\n",
            postfix = "") { "> " + it.toString().cyan() })

        // TODO: checks
    }

    private fun printNode(node: Node, sourceFilePath: String? = null) {
        // what type of node is this?
        println("NodeType: ".yellow() + node.nodeType)
        // what are the properties of this node?
        println("Properties: ".yellow() + node.properties.joinToString { it.name })
        // where does this node come from?
        println("Origin: ".yellow() + node.origin)
        // who is the parent of this node, if any?
        println("Parent: ".yellow() + node.parent)
        // what is the exact position of this node?
        print("Position: ".yellow() + node.position)
        // link to source file, if specified
        if (sourceFilePath == null || node.position == null) {
            println()
        }
        else {
            val filePath = TypeResolutionTest::class.java.getResource(sourceFilePath).path
            val positionString = "${node.position!!.start.line}:${node.position!!.start.column + 1}:" +
                    "${node.position!!.end.line}:${node.position!!.end.column + 1}"
            println(" @file://${filePath}:${positionString}")
        }
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
                println("Type: ".green() + node.type)
                println("Value: ".green() + node.value)
            }

            is BinaryExpression -> {
                println("Type: ".green() + node.type)
                println("Left: ".green() + node.left)
                println("Right: ".green() + node.right)
                println("Operator: ".green() + node.operator)
            }

            is FqnExpression -> {
                println("Type: ".green() + node.type)
                print("Target: ".green() + node.target)
                if (node.target.resolved)
                    println(" -> " + "${node.target.referred}".cyan())
                else
                    println()
                println("Context: ".green() + node.context)
            }
        }
        println()
    }

    private fun getResourceContent(resourcePath: String): String {
        return TypeResolutionTest::class.java.getResource(resourcePath).readText()
    }

    private fun getParseTree(resourcePath: String): FirstStageParsingResult<AntlrEntityParser.ModuleContext> {
        val input = getResourceContent(resourcePath)
        return parser.parseFirstStage(input)
    }

    private fun getAST(resourcePath: String): ParsingResult<Node> {
        val input = getResourceContent(resourcePath)
        return parser.parse(input)
    }
}
