package com.hyosakura.lexicalanalysis.log

import com.hyosakura.lexicalanalysis.applicationState
import org.apache.logging.log4j.core.Filter
import org.apache.logging.log4j.core.Layout
import org.apache.logging.log4j.core.LogEvent
import org.apache.logging.log4j.core.appender.AbstractAppender
import org.apache.logging.log4j.core.config.Property
import org.apache.logging.log4j.core.config.plugins.Plugin
import org.apache.logging.log4j.core.config.plugins.PluginAttribute
import org.apache.logging.log4j.core.config.plugins.PluginElement
import org.apache.logging.log4j.core.config.plugins.PluginFactory
import org.apache.logging.log4j.core.layout.PatternLayout
import java.io.Serializable


/**
 * @author LovesAsuna
 **/
@Plugin(name = "Gui", category = "Core", elementType = "appender", printObject = false)
class GuiAppender(
    name: String,
    filter: Filter?,
    layout: Layout<out Serializable>?,
    ignoreException: Boolean,
    properties: Array<Property>
) : AbstractAppender(name, filter, layout, ignoreException, properties) {
    override fun append(event: LogEvent) {
        applicationState.analyzeWindowState.errorText = String(layout.toByteArray(event))
        applicationState.analyzeWindowState.openErrorDialog = true
    }

    companion object {
        @PluginFactory
        @JvmStatic
        fun createAppender(
            @PluginAttribute("name") name: String?,
            @PluginAttribute("ignoreExceptions") ignoreExceptions: Boolean,
            @PluginElement("Layout") layout: Layout<out Serializable>? = PatternLayout.createDefaultLayout(),
            @PluginElement("Filters") filter: Filter?
        ): GuiAppender? {
            if (name == null) {
                LOGGER.error("No name provided for GuiAppender")
                return null
            }
            return GuiAppender(name, filter, layout, ignoreExceptions, Property.EMPTY_ARRAY)
        }
    }
}