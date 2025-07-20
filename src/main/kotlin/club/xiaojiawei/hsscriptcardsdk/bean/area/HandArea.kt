package club.xiaojiawei.hsscriptcardsdk.bean.area

import club.xiaojiawei.hsscriptcardsdk.bean.Card
import club.xiaojiawei.hsscriptcardsdk.bean.Player
import club.xiaojiawei.hsscriptcardsdk.enums.CardTypeEnum
import kotlin.random.Random

/**
 * 手牌区
 *
 * @author 肖嘉威
 * @date 2022/11/27 15:02
 */
class HandArea(allowLog: Boolean = false, player: Player) : Area(allowLog = allowLog, maxSize = 10, player = player) {

    fun drawCard(): Card? {
        val deckArea = player.deckArea
        if (deckArea.isEmpty) {
            player.playArea.hero?.let { hero ->
                hero.damage += player.incrementFatigue()
            }
        } else {
            deckArea.remove(deckArea.cardSize() - 1)?.let { removeCard ->
                removeCard.apply {
//                     todo 应该从实际套牌中随机获取
                    cardType = CardTypeEnum.SPELL
                    cost = Random.nextInt(11)
                }
                if (player.handArea.safeAdd(removeCard)) {
                    return removeCard
                }
            }
        }
        return null
    }

}
