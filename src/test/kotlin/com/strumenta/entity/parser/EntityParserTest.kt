package com.strumenta.entity.parser

import com.strumenta.kolasu.emf.serialization.JsonGenerator
import com.strumenta.kolasu.model.ReferenceByName
import com.strumenta.kolasu.serialization.computeIdsForReferencedNodes
import com.strumenta.kolasu.testing.assertASTsAreEqual
import org.junit.Test
import kotlin.test.assertNotNull

// TODO tests illustrating
// - Node and its properties (position, origin, destination)
// - traversing
// - testing
// - symbol resolution
// - issue reporting

class EntityParserTest {

    @Test
    fun testModule() {
        val parsingResult = EntityParser().parse(
            """
                module ExampleModule {
                    entity ExampleEntity {
                        name: SecondEntity;
                    }
                    
                }
            """.trimIndent(),
            // considerPosition = false
        )
        val ast = parsingResult.root!!

        // ast.walk().filterIsInstance<Entity>()
        //     .first { it.name == "ExampleEntity" }!!
        //     .let { e ->  (e.features.first { it.name == "name" }!!.type as EntityRefType).target.referred = ast.walk().filterIsInstance<Entity>().first { it.name == "SecondEntity"}!!}

        // // ast.walk().filterIsInstance<Entity>().first { it.name == "SecondEntity"}!!
        //
        // ast.walk().forEach { node -> node.origin = null }
        // assert
        // val resource = ResourceImpl()
        // EntityParser().generateMetamodel(resource, false)

        // val json = JsonGenerator().generateEMFString(
        //     parsingResult.toResult(),
        //     resource.contents.filterIsInstance<EPackage>().first { it.nsPrefix == "entity" }
        // )
        // print(json)

        print(
            com.strumenta.kolasu.serialization.JsonGenerator().generateString(
                parsingResult, withIds = parsingResult.root!!.computeIdsForReferencedNodes()
            )
        )
    }

    @Test
    fun testEntity() {
        val code = """
            module ExampleModule {
                entity ExampleEntity {
                    name: SecondEntity;
                }
                entity SecondEntity {
                    name: string;
                    SecondEntity: integer;
                }
            }
        """.trimIndent()

        val expectedAST = Module(
            name = "ExampleModule",
            entities = listOf(
                Entity(
                    name = "ExampleEntity",
                    features = listOf(
                        Feature(name = "name", type = EntityRefType(target = ReferenceByName(name = "SecondEntity")))
                    )
                ),
                Entity(
                    name = "SecondEntity",
                    features = listOf(
                        Feature(name = "name", type = StringType())
                    )

                )
            )
        )

        val parsingResult = EntityParser().parse(code)
        assert(parsingResult.issues.isEmpty())
        assertNotNull(parsingResult.root)
        val actualAST = parsingResult.root!!

        assertASTsAreEqual(expectedAST, actualAST)
    }
}
