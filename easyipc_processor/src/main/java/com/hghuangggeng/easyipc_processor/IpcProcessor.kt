package com.hghuangggeng.easyipc_processor

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.validate
import com.hghuangggeng.easyipc_annotations.IpcData
import com.hghuangggeng.easyipc_annotations.IpcMethod

class IpcProcessor(private val environment: SymbolProcessorEnvironment) : SymbolProcessor {

    private val methodMappings = mutableMapOf<String, String>()

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val ret = mutableListOf<KSAnnotated>()

        val ipcDataSymbols = resolver.getSymbolsWithAnnotation(IpcData::class.qualifiedName!!)
        environment.logger.warn("process:IpcData:${ipcDataSymbols.toList().size}")
        ipcDataSymbols.toList().forEach {
            if (!it.validate())
                ret.add(it)
            else
                it.accept(IpcDataVisitor(environment), Unit)//处理符号
        }

        val ipcMethodVisitor = IpcMethodVisitor(environment, methodMappings)
        val ipcMethodSymbols = resolver.getSymbolsWithAnnotation(IpcMethod::class.qualifiedName!!)
        environment.logger.warn("process:IpcMethod:${ipcMethodSymbols.toList().size}")
        ipcMethodSymbols.toList().forEach {
            if (!it.validate())
                ret.add(it)
            else
                it.accept(ipcMethodVisitor, Unit) // 处理符号
        }
        if (methodMappings.isNotEmpty()) {
            ipcMethodVisitor.generateMappingFile()
            ipcMethodVisitor.generateHiltModule()
            methodMappings.clear()
        }
        return ret
    }
}