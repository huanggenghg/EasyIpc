package com.hghuangggeng.easyipc_processor

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.validate
import com.hghuangggeng.easyipc_annotations.IpcData

class IpcDataProcessor(private val environment: SymbolProcessorEnvironment) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation(IpcData::class.qualifiedName!!)
        val ret = mutableListOf<KSAnnotated>()
        symbols.toList().forEach {
            if (!it.validate())
                ret.add(it)
            else
                it.accept(IpcDataVisitor(environment), Unit)//处理符号
        }
        return ret
    }
}