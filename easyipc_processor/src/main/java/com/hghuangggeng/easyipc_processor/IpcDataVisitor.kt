package com.hghuangggeng.easyipc_processor

import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.hghuangggeng.easyipc_annotations.Constants
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
        val ipcDataWrapperClassName =
            "${originalClassName}_${Constants.GENERATED_IPC_DATA_WRAPPER_SUFFIX}"
        val ipcDataWrapperClass =
            ClassName(Constants.GENERATED_PACKAGE_NAME, ipcDataWrapperClassName)
        val originalClass = ClassName(originalPackageName, originalClassName)
        val typeSpecBuilder = TypeSpec.classBuilder(ipcDataWrapperClassName)
            .addModifiers(KModifier.DATA)
        val constructorParamsNames = mutableListOf<String>()
        val primaryConstructor = classDeclaration.primaryConstructor
            ?: return environment.logger.error(
                "Class must have a primary constructor.",
                classDeclaration
            )

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
        val toOriginalFun = FunSpec.builder(Constants.GENERATED_IPC_DATA_WRAPPER_FUNC_TO_ORIGINAL)
            .returns(originalClass)
            .addStatement("return %T(%L)", originalClass, constructorParamsNames.joinToString())
            .build()
        typeSpecBuilder.addFunction(toOriginalFun)

        // 生成 fromOriginal 伴生工厂方法
        val companionSpecBuilder = TypeSpec.companionObjectBuilder()
            .addFunction(
                FunSpec.builder(Constants.GENERATED_IPC_DATA_WRAPPER_FUNC_FROM_ORIGINAL)
                    .addParameter(
                        Constants.GENERATED_IPC_DATA_WRAPPER_PARAMETER_ORIGINAL,
                        originalClass
                    )
                    .returns(ipcDataWrapperClass)
                    .addStatement(
                        "return %T(%L)",
                        ipcDataWrapperClass,
                        constructorParamsNames.joinToString { "${Constants.GENERATED_IPC_DATA_WRAPPER_PARAMETER_ORIGINAL}.$it" })
                    .build()
            )
        typeSpecBuilder.addType(companionSpecBuilder.build())

        // 写入文件
        FileSpec.builder(Constants.GENERATED_PACKAGE_NAME, ipcDataWrapperClassName)
            .addType(typeSpecBuilder.build())
            .build()
            .writeTo(environment.codeGenerator, false, listOf(classDeclaration.containingFile!!))

    }
}