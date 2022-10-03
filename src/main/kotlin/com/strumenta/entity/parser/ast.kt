package com.strumenta.entity.parser

import com.strumenta.kolasu.model.Node
import com.strumenta.kolasu.model.PossiblyNamed
import com.strumenta.kolasu.model.ReferenceByName

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
