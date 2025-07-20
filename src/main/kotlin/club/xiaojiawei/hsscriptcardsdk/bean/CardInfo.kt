package club.xiaojiawei.hsscriptcardsdk.bean

import club.xiaojiawei.hsscriptcardsdk.enums.CardActionEnum
import club.xiaojiawei.hsscriptcardsdk.enums.CardEffectTypeEnum

/**
 * @author 肖嘉威
 * @date 2025/6/9 15:16
 */
class CardInfo {

    /**
     * 效果类型
     */
    var effectType: CardEffectTypeEnum

    /**
     * 打出行为
     */
    var playActions: List<CardActionEnum>

    /**
     * 使用行为
     */
    var powerActions: List<CardActionEnum>

    constructor(
        effectType: CardEffectTypeEnum = CardEffectTypeEnum.UNKNOWN,
        playActions: List<CardActionEnum>,
        powerActions: List<CardActionEnum>,
    ) {
        this.effectType = effectType
        this.playActions = playActions
        this.powerActions = powerActions
    }

}