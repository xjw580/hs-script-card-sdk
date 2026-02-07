package club.xiaojiawei.hsscriptcardsdk.cardparser

import club.xiaojiawei.hsscriptcardsdk.bean.DBCard
import club.xiaojiawei.hsscriptcardsdk.bean.PlayAction
import club.xiaojiawei.hsscriptcardsdk.enums.CardActionEnum
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.let

/**
 * @author 肖嘉威
 * @date 2026/2/4 15:50
 */
object CardDescriptionParser {

    private val htmlRegex = Regex("<.*?>")
    private val numberRegex = Regex("""(\d+)""")
    private val dictionary: Map<String, () -> Token> = mapOf(
        "一个" to { One },
        "一只" to { One },
        "所有" to { All },
        "敌方" to { Enemy },
        "友方" to { Friendly },
        "随从" to { Minion },
        "角色" to { Role },
        "英雄" to { Hero },
        "，" to { Comma },
        "。" to { Period },
        "抽" to { Deck },
        "牌库" to { Deck },
        "手牌" to { Hand },
        "发现" to { Hand },
        "弃" to { Hand },
        "获得" to { Obtain },
        "造成" to { Damage },
        "恢复" to { Heal },
        "冻结" to { Value(ValueTagEnum.FROZEN) },
        "突袭" to { Value(ValueTagEnum.RUSH) },
        "$" to { Value(ValueTagEnum.HEALTH, -1) },
        "#" to { Value(ValueTagEnum.HEALTH) },
        "+" to { Value(ValueTagEnum.ATK) },
        "/+" to { Value(ValueTagEnum.HEALTH) },
    )

    private val json = Json {
//            classDiscriminator = "type" // 可选，默认是 "type"
//            prettyPrint = true
    }

    private fun preprocess(text: String): String {
        return text
            .replace(htmlRegex, "")
            .trim()
    }

    fun tokenize(text: String): MutableList<Token> {
        val clean = preprocess(text)
        val tokens = mutableListOf<Token>()

        var i = 0
        while (i < clean.length) {
            dictionary.entries
                .firstOrNull { clean.startsWith(it.key, i) }
                ?.let { matched ->
                    val token = matched.value()
                    tokens += token
                    i += matched.key.length
                    if (token is Value) {
                        val num = numberRegex.find(clean, i)
                        if (num != null && num.range.first == i) {
                            token.number = (num.groupValues[1].toIntOrNull() ?: 0) * token.number
                            i += num.value.length
                        }
                    }
                    continue
                }
            i++
        }
        if (tokens.firstOrNull() is Value) {
            tokens.addFirst(One)
        }
        if (tokens.getOrNull(1) is Value) {
            tokens.add(1, Role)
        }
        return tokens
    }

    fun printInfo(text: String) {
        val tokenize = tokenize(text)
        print("$text : ")
//        println(tokenize)
        val text = json.encodeToString<List<Token>>(tokenize)
        val tokens: List<Token> =
            json.decodeFromString<List<Token>>(text)
        for (token in tokens) {
            println(token)
        }
        println()
    }
//
//    fun parseAsPlayActionInterceptor(dbCard: DBCard): PlayActionInterceptor? {
//        val tokens = tokenize(dbCard.text)
//        if (tokens.isEmpty()) {
//            return null
//        } else {
//            var camp: Camp? = null
//            var target: Target = Role
//            var value: Value? = null
//            var segment: Segment? = null
//            var area: Area = Play
//            var action: Action? = null
//            var quantity: Quantity = One
//            for (token in tokens) {
//                if (token is Quantity) {
//                    quantity = token
//                } else if (token is Camp) {
//                    camp = token
//                } else if (token is Target) {
//                    target = token
//                } else if (token is Value) {
//                    value = token
//                } else if (token is Segment) {
//                    segment = token
//                } else if (token is Area) {
//                    area = token
//                } else if (token is Action) {
//                    action = token
//                }
//                if (token == tokens.last() || token is Segment) {
//                    val damage = value?.number ?: 0
//                    val interceptor: PlayActionInterceptor? = if ((value == null)) {
//                        when (action) {
//                            is Damage -> {
//                                when (camp) {
//                                    is Enemy -> when (target) {
//                                        is Minion -> SpellPointRivalMinionPlayActionInterceptor(damage)
//
//                                        is Hero -> NoPointPlayActionInterceptor
//
//                                        is Role -> SpellPointRivalPlayActionInterceptor(damage)
//                                    }
//
//                                    is Friendly -> when (target) {
//                                        is Minion -> SpellPointMyMinionPlayActionInterceptor(damage)
//
//                                        is Hero -> NoPointPlayActionInterceptor
//
//                                        is Role -> SpellPointMyPlayActionInterceptor(damage)
//                                    }
//
//                                    else -> when (target) {
//                                        is Minion -> SpellPointRivalMinionPlayActionInterceptor(damage)
//
//                                        is Hero -> NoPointPlayActionInterceptor
//
//                                        is Role -> SpellPointRivalPlayActionInterceptor(damage)
//                                    }
//                                }
//                            }
//
//                            is Obtain, Heal -> {
//                                when (camp) {
//                                    is Enemy -> when (target) {
//                                        is Minion -> SpellPointRivalMinionPlayActionInterceptor(damage)
//
//                                        is Hero -> NoPointPlayActionInterceptor
//
//                                        is Role -> SpellPointRivalPlayActionInterceptor(damage)
//                                    }
//
//                                    is Friendly -> when (target) {
//                                        is Minion -> SpellPointMyMinionPlayActionInterceptor(damage)
//
//                                        is Hero -> NoPointPlayActionInterceptor
//
//                                        is Role -> SpellPointMyPlayActionInterceptor(damage)
//                                    }
//
//                                    else -> when (target) {
//                                        is Minion -> SpellPointMyMinionPlayActionInterceptor(damage)
//
//                                        is Hero -> NoPointPlayActionInterceptor
//
//                                        is Role -> SpellPointMyPlayActionInterceptor(damage)
//                                    }
//                                }
//                            }
//
//                            else -> null
//                        }
//                    } else if (area !is Play) {
//                        null
//                    } else {
//                        when (camp) {
//                            is Enemy -> when (target) {
//                                is Minion -> SpellPointRivalMinionPlayActionInterceptor(damage)
//
//                                is Hero -> NoPointPlayActionInterceptor
//
//                                is Role -> SpellPointRivalPlayActionInterceptor(damage)
//                            }
//
//                            is Friendly -> when (target) {
//                                is Minion -> SpellPointMyMinionPlayActionInterceptor(damage)
//
//                                is Hero -> NoPointPlayActionInterceptor
//
//                                is Role -> SpellPointMyPlayActionInterceptor(damage)
//                            }
//
//                            else -> when (target) {
//                                is Minion -> if (damage > 0) SpellPointMyMinionPlayActionInterceptor(damage) else SpellPointRivalMinionPlayActionInterceptor(damage)
//
//                                is Hero -> NoPointPlayActionInterceptor
//
//                                is Role -> if (damage > 0) SpellPointMyPlayActionInterceptor(damage) else SpellPointRivalPlayActionInterceptor(damage)
//                            }
//                        }
//                    }
//                    return interceptor
//                }
//            }
//        }
//        return null
//    }

    fun parseAsCardActionEnum(dbCard: DBCard): List<CardActionEnum> {
        val tokens = tokenize(dbCard.text)
        if (tokens.isEmpty()) {
            return emptyList()
        } else {
            var camp: Camp? = null
            var target: Target = Role
            var value: Value? = null
            var segment: Segment? = null
            var area: Area = Play
            var action: Action? = null
            val playCardActions = mutableListOf<CardActionEnum>()
            for (token in tokens) {
                if (token is All) {
                    playCardActions.add(CardActionEnum.NO_POINT)
                    break
                } else if (token is Camp) {
                    camp = token
                } else if (token is Target) {
                    target = token
                } else if (token is Value) {
                    value = token
                } else if (token is Segment) {
                    segment = token
                } else if (token is Area) {
                    area = token
                } else if (token is Action) {
                    action = token
                }
                if (token == tokens.last() || token is Segment) {
                    val playCardAction: CardActionEnum? = if ((value == null)) {
                        when (action) {
                            is Damage -> {
                                when (camp) {
                                    is Enemy -> when (target) {
                                        is Minion -> CardActionEnum.POINT_RIVAL_MINION

                                        is Hero -> CardActionEnum.NO_POINT

                                        is Role -> CardActionEnum.POINT_RIVAL
                                    }

                                    is Friendly -> when (target) {
                                        is Minion -> CardActionEnum.POINT_MY_MINION

                                        is Hero -> CardActionEnum.NO_POINT

                                        is Role -> CardActionEnum.POINT_MY
                                    }

                                    else -> when (target) {
                                        is Minion -> CardActionEnum.POINT_RIVAL_MINION

                                        is Hero -> CardActionEnum.NO_POINT

                                        is Role -> CardActionEnum.POINT_RIVAL
                                    }
                                }
                            }

                            is Obtain, Heal -> {
                                when (camp) {
                                    is Enemy -> when (target) {
                                        is Minion -> CardActionEnum.POINT_RIVAL_MINION

                                        is Hero -> CardActionEnum.NO_POINT

                                        is Role -> CardActionEnum.POINT_RIVAL
                                    }

                                    is Friendly -> when (target) {
                                        is Minion -> CardActionEnum.POINT_MY_MINION

                                        is Hero -> CardActionEnum.NO_POINT

                                        is Role -> CardActionEnum.POINT_MY
                                    }

                                    else -> when (target) {
                                        is Minion -> CardActionEnum.POINT_MY_MINION

                                        is Hero -> CardActionEnum.NO_POINT

                                        is Role -> CardActionEnum.POINT_MY
                                    }
                                }
                            }

                            else -> null
                        }
                    } else if (area !is Play) {
                        null
                    } else {
                        when (camp) {
                            is Enemy -> when (target) {
                                is Minion -> CardActionEnum.POINT_RIVAL_MINION

                                is Hero -> CardActionEnum.NO_POINT

                                is Role -> CardActionEnum.POINT_RIVAL
                            }

                            is Friendly -> when (target) {
                                is Minion -> CardActionEnum.POINT_MY_MINION

                                is Hero -> CardActionEnum.NO_POINT

                                is Role -> CardActionEnum.POINT_MY
                            }

                            else -> when (target) {
                                is Minion -> if (value.number > 0) CardActionEnum.POINT_MY_MINION else CardActionEnum.POINT_RIVAL_MINION

                                is Hero -> CardActionEnum.NO_POINT

                                is Role -> if (value.number > 0) CardActionEnum.POINT_MY else CardActionEnum.POINT_RIVAL
                            }
                        }
                    }
                    playCardAction?.let {
                        playCardActions.add(it)
                    }
                    value = null
                    if (segment == null || segment is Period) {
                        camp = null
                        target = Role
                        area = Play
                    }
                    segment = null
                    action = null
                }
            }
            if (playCardActions.isEmpty()) {
                playCardActions.add(CardActionEnum.NO_POINT)
            }
            return playCardActions
        }
    }
}
