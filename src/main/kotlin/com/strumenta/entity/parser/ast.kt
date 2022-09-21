package com.strumenta.entity.parser

import com.strumenta.kolasu.model.Node
import com.strumenta.kolasu.model.PossiblyNamed
import com.strumenta.kolasu.model.ReferenceByName

// ogni nodo ha una sua posizione, non occorre implementare questa logica a mano
// ogni nodo tiene traccia della sua origin, e.g. parse tree
// sono a disposizione metodi per navigare e manipolare AST semplicemente estendendo Node

data class Module(
    override val name: String? = null,
    var entities: List<Entity> = listOf()
) : Node(), PossiblyNamed

data class Entity(
    override val name: String? = null,
    var features: List<Feature> = listOf()
) : Node(), PossiblyNamed

data class Feature(
    override val name: String? = null,
    var type: Type? = null
) : Node(), PossiblyNamed

open class Type : Node()

open class PrimitiveType : Type()
class StringType : PrimitiveType()
class IntegerType : PrimitiveType()
class BooleanType : PrimitiveType()

data class EntityRefType(
    var target: ReferenceByName<Entity>
) : Type()
