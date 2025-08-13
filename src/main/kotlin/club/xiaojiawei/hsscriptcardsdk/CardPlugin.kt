package club.xiaojiawei.hsscriptcardsdk

import club.xiaojiawei.hsscriptpluginsdk.Plugin
import club.xiaojiawei.hsscriptpluginsdk.config.PluginScope

/**
 * @author 肖嘉威
 * @date 2024/9/22 19:18
 */
interface CardPlugin : Plugin {
    /**
     * 对哪些插件可见
     * @return 插件id数组
     */
    fun pluginScope(): Array<String> {
//        可自定义范围，如下
//        return arrayOf("id1", "id2", "id3")
        return PluginScope.PROTECTED
    }

    companion object {
        /**
         * 最低兼容版本
         */
        const val MINIMUM_COMPATIBLE_VERSION = "1.0.1"
    }
}