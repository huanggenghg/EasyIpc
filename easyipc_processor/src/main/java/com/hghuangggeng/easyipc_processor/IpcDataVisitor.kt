package com.hghuangggeng.easyipc_processor

import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.writeTo

class IpcDataVisitor(private val environment: SymbolProcessorEnvironment) : KSVisitorVoid() {
    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
        environment.logger.warn("visitClassDeclaration:")
        val originalClassName = classDeclaration.simpleName.asString()
        val originalPackageName = classDeclaration.packageName.asString()

        // 获取注解参数：目标协议路径
        val protocolPackage = "com.hghghgh.text"

        val protocolClassName = "${originalClassName}Protocol"
        val protocolClass = ClassName(protocolPackage, protocolClassName)
        val originalClass = ClassName(originalPackageName, originalClassName)

        val typeSpecBuilder = TypeSpec.classBuilder(protocolClassName)
            .addModifiers(KModifier.DATA)

        val constructorParamsNames = mutableListOf<String>()
        val primaryConstructor = classDeclaration.primaryConstructor
            ?: return environment.logger.error("Class must have a primary constructor.", classDeclaration)

        // 遍历参数并构建属性列表
        primaryConstructor.parameters.forEach { parameter ->
            val paramName = parameter.name!!.asString()
            val paramType = parameter.type.resolve().toClassName()

            typeSpecBuilder.addProperty(
                PropertySpec.builder(paramName, paramType).initializer(paramName).build()
            )
            constructorParamsNames.add(paramName)
        }

        // 生成主构造函数
        val constructorBuilder = FunSpec.constructorBuilder()
        primaryConstructor.parameters.forEach { p ->
            val paramType = p.type.resolve().toClassName()
            constructorBuilder.addParameter(p.name!!.asString(), paramType)
        }
        typeSpecBuilder.primaryConstructor(constructorBuilder.build())

        // 生成 toOriginal 方法
        val toOriginalFun = FunSpec.builder("toOriginal")
            .returns(originalClass)
            .addStatement("return %T(%L)", originalClass, constructorParamsNames.joinToString())
            .build()
        typeSpecBuilder.addFunction(toOriginalFun)

        // 生成 fromOriginal 伴生工厂方法
        val companionSpecBuilder = TypeSpec.companionObjectBuilder()
            .addFunction(
                FunSpec.builder("fromOriginal")
                    .addParameter("original", originalClass)
                    .returns(protocolClass)
                    .addStatement("return %T(%L)", protocolClass, constructorParamsNames.joinToString { "original.$it" })
                    .build()
            )
        typeSpecBuilder.addType(companionSpecBuilder.build())

        // 写入文件
        FileSpec.builder(protocolPackage, protocolClassName)
            .addType(typeSpecBuilder.build())
            .build()
            .writeTo(environment.codeGenerator, false, listOf(classDeclaration.containingFile!!))

    }
}