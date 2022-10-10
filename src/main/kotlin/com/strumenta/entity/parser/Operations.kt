package com.strumenta.entity.parser

class Operations {
    private val operations : MutableMap<String, (Type, Type) -> Type?> = hashMapOf()

    init {
        operations[BinaryOperator.SUM.symbol] = { a : Type, b : Type ->
            when {
                a is IntegerType && b is IntegerType -> IntegerType()
                a is StringType && b is StringType -> StringType()
                else -> null
            }
        }
        operations[BinaryOperator.SUB.symbol] = { a : Type, b : Type ->
            when {
                a is IntegerType && b is IntegerType -> IntegerType()
                else -> null
            }
        }
        operations[BinaryOperator.MUL.symbol] = { a : Type, b : Type ->
            when {
                a is IntegerType && b is IntegerType -> IntegerType()
                else -> null
            }
        }
        operations[BinaryOperator.DIV.symbol] = { a : Type, b : Type ->
            when {
                a is IntegerType && b is IntegerType -> IntegerType()
                else -> null
            }
        }
    }

    fun applyFunction(operator: BinaryOperator, a: Type, b: Type): Type? {
        if (!operations.contains(operator.symbol))
            throw RuntimeException("Unknown operator: $operator")

        return operations[operator.symbol]!!.invoke(a, b)
    }
}