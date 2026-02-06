package club.xiaojiawei.hsscriptcardsdk.data

import club.xiaojiawei.hsscriptcardsdk.bean.CardInfo
import club.xiaojiawei.hsscriptbase.bean.LikeTrie

/**
 * 合并的卡牌数据存储，包含卡牌信息和权重
 * key: [club.xiaojiawei.hsscriptcardsdk.bean.Card.cardId], value: [CardInfo]
 * @author 肖嘉威
 * @date 2026/2/2
 */
val CARD_DATA_TRIE = LikeTrie(CardInfo())
