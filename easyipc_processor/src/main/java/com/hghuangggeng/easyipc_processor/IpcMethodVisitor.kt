package com.hghuangggeng.easyipc_processor

import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.hghuangggeng.easyipc_annotations.Constants
import com.hghuangggeng.easyipc_annotations.IMethodRegistry
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
        val injectClass = ClassName("javax.inject", "Inject")

        // 2. 生成主构造函数并添加 @Inject
        val constructor = FunSpec.constructorBuilder()
            .addAnnotation(injectClass)
            .build()

        // 3. 注册方法实现
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
        val generatedClass = TypeSpec.classBuilder(Constants.GENERATED_METHOD_REGISTRY_CLASS_NAME)
            .addModifiers(KModifier.PUBLIC)
            .primaryConstructor(constructor) // 设置主构造函数
            .addSuperinterface(IMethodRegistry::class) // 实现接口
            .addFunction(registerMethodBuilder.build())
            .build()

        // 5. 写入 Kotlin 源文件
        val file = FileSpec.builder(
            Constants.GENERATED_PACKAGE_NAME,
            Constants.GENERATED_METHOD_REGISTRY_CLASS_NAME
        )
            .addType(generatedClass)
            .build()
        file.writeTo(environment.codeGenerator, true)
    }

    fun generateHiltModule() {
        val moduleClassName = ClassName("dagger", "Module")
        val bindsClassName = ClassName("dagger", "Binds")
        val installInClassName = ClassName("dagger.hilt", "InstallIn")
        val singletonComponentClassName = ClassName("dagger.hilt.components", "SingletonComponent")
        val intoSetClassName = ClassName("dagger.multibindings", "IntoSet")

        val generatedRegistryClassName = ClassName(
            Constants.GENERATED_PACKAGE_NAME,
            Constants.GENERATED_METHOD_REGISTRY_CLASS_NAME
        )
        val iMethodRegistryClassName = IMethodRegistry::class

        val bindFunc = FunSpec.builder("bind")
            .addModifiers(KModifier.ABSTRACT)
            .addAnnotation(bindsClassName)
            .addAnnotation(intoSetClassName)
            .returns(iMethodRegistryClassName)
            .addParameter("methodRegistryImpl", generatedRegistryClassName)
            .build()

        val moduleClass =
            TypeSpec.classBuilder(Constants.GENERATED_METHOD_REGISTRY_HILT_MODULE_NAME)
                .addModifiers(KModifier.ABSTRACT)
                .addAnnotation(moduleClassName)
                .addAnnotation(
                    AnnotationSpec.builder(installInClassName)
                        .addMember("%T::class", singletonComponentClassName).build()
                )
                .addFunction(bindFunc)
                .build()

        FileSpec.builder(
            Constants.GENERATED_PACKAGE_NAME,
            Constants.GENERATED_METHOD_REGISTRY_HILT_MODULE_NAME
        )
            .addType(moduleClass)
            .build()
            .writeTo(environment.codeGenerator, true)
    }
}