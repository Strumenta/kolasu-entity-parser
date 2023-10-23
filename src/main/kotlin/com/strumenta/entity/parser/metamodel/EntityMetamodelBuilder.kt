package com.strumenta.entity.parser.metamodel

import com.strumenta.entity.parser.ast.*
import com.strumenta.kolasu.emf.MetamodelBuilder
import org.eclipse.emf.ecore.resource.Resource
import kotlin.reflect.KClass

object EntityMetamodelBuilder : (Resource?) -> MetamodelBuilder {
    private const val packageName: kotlin.String = "com.strumenta.entity.parser.ast"
    private const val nsURI: kotlin.String = "https://strumenta.com/entity"
    private const val nsPrefix: kotlin.String = "entity"

    private val metaclasses: List<KClass<*>> = listOf(
        Workspace::class,
        Module::class,
        Entity::class,
        Feature::class,
        Operation::class,
        Statement::class,
        Expression::class,
    )

    override fun invoke(resource: Resource?): MetamodelBuilder =
        MetamodelBuilder(packageName = packageName, nsURI = nsURI, nsPrefix = nsPrefix, resource = resource)
            .apply { metaclasses.forEach(this::provideClass) }
}
