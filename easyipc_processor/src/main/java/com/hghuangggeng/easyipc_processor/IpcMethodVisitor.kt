package com.hghuangggeng.easyipc_processor

import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.hghuangggeng.easyipc_annotations.IpcMethod
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MUTABLE_MAP
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.ksp.writeTo

class IpcMethodVisitor(
    private val environment: SymbolProcessorEnvironment,
    private val mappings: MutableMap<String, String>
) : KSVisitorVoid() {

    override fun visitFunctionDeclaration(function: KSFunctionDeclaration, data: Unit) {
        environment.logger.warn("visitFunctionDeclaration:")
        // 查找我们感兴趣的注解
        val annotation = function.annotations.find {
            it.shortName.asString() == IpcMethod::class.simpleName
        } ?: return

        val methodName = function.simpleName.asString()
        val className = function.parentDeclaration?.qualifiedName?.asString()

        if (className != null) {
            // 获取注解的 name 属性值，如果为空则使用方法名
            val annotationValue = annotation.arguments.firstOrNull()?.value as? String
            val key = if (annotationValue.isNullOrEmpty()) methodName else annotationValue

            mappings[key] = className
        }
    }

    fun generateMappingFile() {
        // 1. 定义需要引用的类名
        val ipcFuncInterface = ClassName("com.hghuangggeng.easyipc_annotations", "IMethodRegistry")
        val injectClass = ClassName("javax.inject", "Inject")

        // 2. 生成主构造函数并添加 @Inject
        val constructor = FunSpec.constructorBuilder()
            .addAnnotation(injectClass)
            .build()

        // 3. 生成方法实现
        val registerMethodBuilder = FunSpec.builder("register")
            .addModifiers(KModifier.OVERRIDE)
            .addParameter(

                ParameterSpec.builder(
                    "map",
                    MUTABLE_MAP
                        .parameterizedBy(String::class.asTypeName(), String::class.asTypeName())
                ).build()
            )
        mappings.forEach {
            registerMethodBuilder.addStatement("map.put(\"${it.key}\", \"${it.value}\")")
        }

        // 4. 构建最终的类
        val generatedClass = TypeSpec.classBuilder("TestIpcMethodProxyImpl")
            .addModifiers(KModifier.PUBLIC)
            .primaryConstructor(constructor) // 设置主构造函数
            .addSuperinterface(ipcFuncInterface) // 实现接口
            .addFunction(registerMethodBuilder.build())
            .build()

        val packageName = "com.hghghgh.text11"
        // 5. 写入 Kotlin 源文件
        val file = FileSpec.builder(packageName, "TestIpcMethodProxyImpl")
            .addType(generatedClass)
            .build()

        // 使用 KSP 的 writeTo 方法
        file.writeTo(environment.codeGenerator, false)
    }

    fun generateHiltModule() {
        // --- Define Hilt ClassNames by their FQCN strings ---
        val moduleClassName = ClassName("dagger", "Module")
        val providesClassName = ClassName("dagger", "Provides")
        val installInClassName = ClassName("dagger.hilt", "InstallIn")
        val singletonComponentClassName = ClassName("dagger.hilt.components", "SingletonComponent")
        val intoSetClassName = ClassName("dagger.multibindings", "IntoSet")

        // Other required classes
        val generatedRegistryClassName = ClassName("com.hghghgh.text11", "TestIpcMethodProxyImpl")
        val iMethodRegistryClassName =
            ClassName("com.hghuangggeng.easyipc_annotations", "IMethodRegistry")

        val providesFun = FunSpec.builder("provideIpcFuncA")
            .addAnnotation(providesClassName)
            .addAnnotation(intoSetClassName)
            // If you need JvmSuppressWildcards for generic injection issues
            .addAnnotation(AnnotationSpec.builder(JvmSuppressWildcards::class).build())
            .returns(iMethodRegistryClassName)
            .addParameter("implA", generatedRegistryClassName)
            .addStatement("return implA")
            .build()

        val moduleClass = TypeSpec.objectBuilder("BusinessHiltModule")
            .addAnnotation(moduleClassName)
            // Use .addMember("%T::class", ...) pattern for @InstallIn
            .addAnnotation(
                AnnotationSpec.builder(installInClassName)
                    .addMember("%T::class", singletonComponentClassName).build()
            )
            .addFunction(providesFun)
            .build()

        FileSpec.builder("com.hghghgh.text11", "BusinessHiltModule")
            .addType(moduleClass)
            .build()
            .writeTo(environment.codeGenerator, false)
    }
}