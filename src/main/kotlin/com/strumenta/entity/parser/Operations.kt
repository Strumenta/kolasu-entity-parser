package com.strumenta.entity.parser

class Operations {
    private val operations : MutableMap<String, (Type, Type) -> Type> = hashMapOf()

    init {
        operations[BinaryOperator.SUM.symbol] = { a : Type, b : Type ->
            when {
                a is IntegerType && b is IntegerType -> IntegerType()
                a is StringType && b is StringType -> StringType()
                else -> throw RuntimeException("Operator ${BinaryOperator.SUM.symbol} cannot be applied to: ${a.javaClass.simpleName} and ${b.javaClass.simpleName}")
            }
        }
        operations[BinaryOperator.SUB.symbol] = { a : Type, b : Type ->
            when {
                a is IntegerType && b is IntegerType -> IntegerType()
                else -> throw RuntimeException("Operator ${BinaryOperator.SUB.symbol} cannot be applied to: ${a.javaClass.simpleName} and ${b.javaClass.simpleName}")
            }
        }
        operations[BinaryOperator.MUL.symbol] = { a : Type, b : Type ->
            when {
                a is IntegerType && b is IntegerType -> IntegerType()
                else -> throw RuntimeException("Operator ${BinaryOperator.MUL.symbol} cannot be applied to: ${a.javaClass.simpleName} and ${b.javaClass.simpleName}")
            }
        }
        operations[BinaryOperator.DIV.symbol] = { a : Type, b : Type ->
            when {
                a is IntegerType && b is IntegerType -> IntegerType()
                else -> throw RuntimeException("Operator ${BinaryOperator.DIV.symbol} cannot be applied to: ${a.javaClass.simpleName} and ${b.javaClass.simpleName}")
            }
        }
    }

    fun applyFunction(operator: BinaryOperator, a: Type, b: Type): Type {
        if (!operations.contains(operator.symbol))
            throw RuntimeException("Unknown operator: $operator")

        return operations[operator.symbol]!!.invoke(a, b)
    }
}