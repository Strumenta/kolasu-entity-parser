package com.strumenta.entity.parser

import com.strumenta.kolasu.emf.MetamodelBuilder
import org.eclipse.emf.ecore.resource.Resource
import kotlin.reflect.KClass

object EntityMetamodelBuilder : (Resource?) -> MetamodelBuilder {
    private const val packageName: String = "com.strumenta.entity.parser"
    private const val nsURI: String = "https://strumenta.com/entity"
    private const val nsPrefix: String = "entity"

    private val metaclasses: List<KClass<*>> = listOf(
        Module::class,
        Entity::class,
        Feature::class,
        Type::class,
        PrimitiveType::class,
        StringType::class,
        IntegerType::class,
        BooleanType::class,
        EntityRefType::class
    )

    override fun invoke(resource: Resource?): MetamodelBuilder =
        MetamodelBuilder(packageName = packageName, nsURI = nsURI, nsPrefix = nsPrefix, resource = resource)
            .apply { metaclasses.forEach(this::provideClass) }
}
