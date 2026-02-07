package club.xiaojiawei.hsscriptcardsdk.cardparser

import club.xiaojiawei.hsscriptcardsdk.CardAction
import club.xiaojiawei.hsscriptcardsdk.bean.PlayAction
import club.xiaojiawei.hsscriptcardsdk.bean.Player
import club.xiaojiawei.hsscriptcardsdk.bean.War
import club.xiaojiawei.hsscriptcardsdk.util.CardUtil
import net.bytebuddy.implementation.bind.annotation.Argument
import net.bytebuddy.implementation.bind.annotation.This

/**
 * @author 肖嘉威
 * @date 2026/2/5 14:28
 */
interface PlayActionInterceptor {
    fun generatePlayActions(
        @Argument(0) war: War,
        @Argument(1) player: Player,
        @This cardAction: CardAction
    ): List<PlayAction>
}

/*无需指向*/

object NoPointPlayActionInterceptor : PlayActionInterceptor {
    override fun generatePlayActions(
        @Argument(0) war: War,
        @Argument(1) player: Player,
        @This cardAction: CardAction
    ): List<PlayAction> {
        return listOf(
            PlayAction({ newWar ->
                cardAction.findSelf(newWar)?.action?.power()
            }, { newWar ->
                cardAction.apply {
                    spendSelfCost(newWar)
                    removeSelf(newWar)
                }
            }, cardAction.belongCard)
        )
    }
}

/*法术单体友方*/

class SpellPointMyMinionPlayActionInterceptor(private val damage: Int) : PlayActionInterceptor {
    override fun generatePlayActions(
        @Argument(0) war: War,
        @Argument(1) player: Player,
        @This cardAction: CardAction
    ): List<PlayAction> {
        val result = mutableListOf<PlayAction>()
        val myPlayCards = war.me.playArea.cards
        cardAction.apply {
            for (myCard in myPlayCards) {
                if (myCard.canBeTargetedByMySpells()) {
                    result.add(
                        PlayAction({ newWar ->
                            findSelf(newWar)?.action?.power(myCard.action.findSelf(newWar))
                        }, { newWar ->
                            spendSelfCost(newWar)
                            removeSelf(newWar)?.let {
//                                myCard.action.findSelf(newWar)?.heal(damage + newWar.me.getSpellPower())
                            }
                        }, belongCard)
                    )
                }
            }
        }
        return result
    }
}

class SpellPointMyPlayActionInterceptor(private val damage: Int) : PlayActionInterceptor {
    override fun generatePlayActions(
        @Argument(0) war: War,
        @Argument(1) player: Player,
        @This cardAction: CardAction
    ): List<PlayAction> {
        val result = mutableListOf<PlayAction>()
        val myPlayCards = war.me.playArea.hero?.let {
            buildList {
                addAll(war.me.playArea.cards)
                add(it)
            }
        } ?: war.me.playArea.cards

        cardAction.apply {
            for (myCard in myPlayCards) {
                if (myCard.canBeTargetedByMySpells()) {
                    result.add(
                        PlayAction({ newWar ->
                            findSelf(newWar)?.action?.power(myCard.action.findSelf(newWar))
                        }, { newWar ->
                            spendSelfCost(newWar)
                            removeSelf(newWar)?.let {
//                                myCard.action.findSelf(newWar)?.heal(damage + newWar.me.getSpellPower())
                            }
                        }, belongCard)
                    )
                }
            }
        }
        return result
    }
}

/*法术单体敌方*/

class SpellPointRivalMinionPlayActionInterceptor(private val damage: Int) : PlayActionInterceptor {
    override fun generatePlayActions(
        @Argument(0) war: War,
        @Argument(1) player: Player,
        @This cardAction: CardAction
    ): List<PlayAction> {
        val result = mutableListOf<PlayAction>()
        val rivalPlayCards = war.rival.playArea.cards
        cardAction.apply {
            for (rivalCard in rivalPlayCards) {
                if (rivalCard.canHurt() && rivalCard.canBeTargetedByRivalSpells()) {
                    result.add(
                        PlayAction({ newWar ->
                            findSelf(newWar)?.action?.power(rivalCard.action.findSelf(newWar))
                        }, { newWar ->
                            spendSelfCost(newWar)
                            removeSelf(newWar)?.let {
                                rivalCard.action.findSelf(newWar)?.injured(damage + newWar.me.getSpellPower())
                            }
                        }, belongCard)
                    )
                }
            }
        }
        return result
    }
}

class SpellPointRivalPlayActionInterceptor(private val damage: Int) : PlayActionInterceptor {
    override fun generatePlayActions(
        @Argument(0) war: War,
        @Argument(1) player: Player,
        @This cardAction: CardAction
    ): List<PlayAction> {
        val result = mutableListOf<PlayAction>()
        val rivalPlayCards = war.rival.playArea.hero?.let {
            buildList {
                addAll(war.rival.playArea.cards)
                add(it)
            }
        } ?: war.rival.playArea.cards

        cardAction.apply {
            for (rivalCard in rivalPlayCards) {
                if (rivalCard.canHurt() && rivalCard.canBeTargetedByRivalSpells()) {
                    result.add(
                        PlayAction({ newWar ->
                            findSelf(newWar)?.action?.power(rivalCard.action.findSelf(newWar))
                        }, { newWar ->
                            spendSelfCost(newWar)
                            removeSelf(newWar)?.let {
                                rivalCard.action.findSelf(newWar)?.injured(damage + newWar.me.getSpellPower())
                            }
                        }, belongCard)
                    )
                }
            }
        }
        return result
    }
}

class SpellPointRivalHeroPlayActionInterceptor(private val damage: Int) : PlayActionInterceptor {
    override fun generatePlayActions(
        @Argument(0) war: War,
        @Argument(1) player: Player,
        @This cardAction: CardAction
    ): List<PlayAction> {
        cardAction.apply {
            war.rival.playArea.hero?.let { rivalCard ->
                if (rivalCard.canHurt() && rivalCard.canBeTargetedByRivalSpells()) {
                    return listOf(PlayAction({ newWar ->
                        findSelf(newWar)?.action?.power(rivalCard.action.findSelf(newWar))
                    }, { newWar ->
                        spendSelfCost(newWar)
                        removeSelf(newWar)?.let {
                            rivalCard.action.findSelf(newWar)?.injured(damage + newWar.me.getSpellPower())
                        }
                    }, belongCard))
                }
            }
        }
        return emptyList()
    }
}

/*法术群体敌方*/

class SpellAllRivalMinionPlayActionInterceptor(private val damage: Int) : PlayActionInterceptor {
    override fun generatePlayActions(
        @Argument(0) war: War,
        @Argument(1) player: Player,
        @This cardAction: CardAction
    ): List<PlayAction> {
        val result = mutableListOf<PlayAction>()
        cardAction.apply {
            PlayAction({ newWar ->
                findSelf(newWar)?.action?.power()
            }, { newWar ->
                spendSelfCost(newWar)
                removeSelf(newWar)?.let {
                    val cards = newWar.rival.playArea.cards.toList()
                    for (card in cards) {
                        if (card.canHurt()) {
                            card.injured(damage + newWar.me.getSpellPower())
                        }
                    }
                }
            }, belongCard)
        }
        return result
    }
}

class SpellAllRivalPlayActionInterceptor(private val damage: Int) : PlayActionInterceptor {
    override fun generatePlayActions(
        @Argument(0) war: War,
        @Argument(1) player: Player,
        @This cardAction: CardAction
    ): List<PlayAction> {
        val result = mutableListOf<PlayAction>()
        cardAction.apply {
            PlayAction({ newWar ->
                findSelf(newWar)?.action?.power()
            }, { newWar ->
                spendSelfCost(newWar)
                removeSelf(newWar)?.let {
                    val cards = buildList {
                        addAll(newWar.rival.playArea.cards)
                        newWar.rival.playArea.hero?.let {
                            add(it)
                        }
                    }
                    for (card in cards) {
                        if (card.canHurt()) {
                            card.injured(damage + newWar.me.getSpellPower())
                        }
                    }
                }
            }, belongCard)
        }
        return result
    }
}

/*随从单体敌方*/

class MinionPointRivalMinionPlayActionInterceptor(private val damage: Int) : PlayActionInterceptor {
    override fun generatePlayActions(
        @Argument(0) war: War,
        @Argument(1) player: Player,
        @This cardAction: CardAction
    ): List<PlayAction> {
        val result = mutableListOf<PlayAction>()
        val rivalPlayCards = war.rival.playArea.cards
        cardAction.apply {
            for (rivalCard in rivalPlayCards) {
                if (rivalCard.canHurt() && rivalCard.canBeTargetedByRivalSpells()) {
                    result.add(
                        PlayAction({ newWar ->
                            findSelf(newWar)?.action?.power(false)?.pointTo(rivalCard.action.findSelf(newWar))
                        }, { newWar ->
                            spendSelfCost(newWar)
                            val me = newWar.me
                            removeSelf(newWar)?.let { card ->
                                if (me.playArea.safeAdd(card)) {
                                    CardUtil.handleCardExhaustedWhenIntoPlayArea(card)
                                    rivalCard.action.findSelf(newWar)?.injured(damage)
                                }
                            }
                        }, belongCard)
                    )
                }
            }
        }
        return result
    }
}

class MinionPointRivalPlayActionInterceptor(private val damage: Int) : PlayActionInterceptor {
    override fun generatePlayActions(
        @Argument(0) war: War,
        @Argument(1) player: Player,
        @This cardAction: CardAction
    ): List<PlayAction> {
        val result = mutableListOf<PlayAction>()
        val rivalPlayCards = war.rival.playArea.hero?.let {
            buildList {
                addAll(war.rival.playArea.cards)
                add(it)
            }
        } ?: war.rival.playArea.cards
        cardAction.apply {
            for (rivalCard in rivalPlayCards) {
                if (rivalCard.canHurt() && rivalCard.canBeTargetedByRivalSpells()) {
                    result.add(
                        PlayAction({ newWar ->
                            findSelf(newWar)?.action?.power(false)?.pointTo(rivalCard.action.findSelf(newWar))
                        }, { newWar ->
                            spendSelfCost(newWar)
                            val me = newWar.me
                            removeSelf(newWar)?.let { card ->
                                if (me.playArea.safeAdd(card)) {
                                    CardUtil.handleCardExhaustedWhenIntoPlayArea(card)
                                    rivalCard.action.findSelf(newWar)?.injured(damage)
                                }
                            }
                        }, belongCard)
                    )
                }
            }
        }
        return result
    }
}

class MinionPointRivalHeroPlayActionInterceptor(private val damage: Int) : PlayActionInterceptor {
    override fun generatePlayActions(
        @Argument(0) war: War,
        @Argument(1) player: Player,
        @This cardAction: CardAction
    ): List<PlayAction> {
        cardAction.apply {
            war.rival.playArea.hero?.let { rivalCard ->
                if (rivalCard.canHurt() && rivalCard.canBeTargetedByRivalSpells()) {
                    listOf(PlayAction({ newWar ->
                        findSelf(newWar)?.action?.power(false)?.pointTo(rivalCard.action.findSelf(newWar))
                    }, { newWar ->
                        spendSelfCost(newWar)
                        val me = newWar.me
                        removeSelf(newWar)?.let { card ->
                            if (me.playArea.safeAdd(card)) {
                                CardUtil.handleCardExhaustedWhenIntoPlayArea(card)
                                rivalCard.action.findSelf(newWar)?.injured(damage)
                            }
                        }
                    }, belongCard))
                }
            }
        }
        return emptyList()
    }
}

/*随从群体敌方*/

class MinionAllRivalMinionPlayActionInterceptor(private val damage: Int) : PlayActionInterceptor {
    override fun generatePlayActions(
        @Argument(0) war: War,
        @Argument(1) player: Player,
        @This cardAction: CardAction
    ): List<PlayAction> {
        cardAction.apply {
            return listOf(PlayAction({ newWar ->
                findSelf(newWar)?.action?.power()
            }, { newWar ->
                spendSelfCost(newWar)
                removeSelf(newWar)?.let { card ->
                    CardUtil.handleCardExhaustedWhenIntoPlayArea(card)
                    val cards = newWar.rival.playArea.cards.toList()
                    for (card in cards) {
                        if (card.canHurt()) {
                            card.injured(damage)
                        }
                    }
                }
            }, belongCard))
        }
    }
}

class MinionAllRivalPlayActionInterceptor(private val damage: Int) : PlayActionInterceptor {
    override fun generatePlayActions(
        @Argument(0) war: War,
        @Argument(1) player: Player,
        @This cardAction: CardAction
    ): List<PlayAction> {
        cardAction.apply {
            return listOf(PlayAction({ newWar ->
                findSelf(newWar)?.action?.power()
            }, { newWar ->
                spendSelfCost(newWar)
                removeSelf(newWar)?.let { card ->
                    CardUtil.handleCardExhaustedWhenIntoPlayArea(card)
                    val cards = newWar.rival.playArea.hero?.let {
                        buildList {
                            add(it)
                            addAll(newWar.rival.playArea.cards)
                        }
                    } ?: newWar.rival.playArea.cards.toList()
                    for (card in cards) {
                        if (card.canHurt()) {
                            card.injured(damage)
                        }
                    }
                }
            }, belongCard))
        }
    }
}