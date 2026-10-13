package com.example.data

import com.example.model.*

object GameContent {

    fun initialCoreUpgrades(): List<CoreUpgrade> = listOf(
        CoreUpgrade(
            id = "pulse_touch",
            name = "Прикосновение Пустоты",
            description = "Усиливает базовое касание Сердца Бездны, вытягивая больше первородных эманаций.",
            baseCost = 15.0,
            costMultiplier = 1.15,
            clickPowerAdd = 1.0
        ),
        CoreUpgrade(
            id = "abyssal_resonance",
            name = "Резонанс Бездны",
            description = "Каждый клик вызывает вибрирующее эхо, увеличивающее общую силу удара.",
            baseCost = 100.0,
            costMultiplier = 1.18,
            clickPowerAdd = 5.0,
            clickMultiplierAdd = 0.05,
            requiredEchoesSeen = 50.0
        ),
        CoreUpgrade(
            id = "fissure_sight",
            name = "Взор в Трещину",
            description = "Позволяет видеть уязвимости реальности, открывая шанс на критический импульс.",
            baseCost = 500.0,
            costMultiplier = 1.25,
            critChanceAdd = 0.02,
            critMultiplierAdd = 0.3,
            requiredEchoesSeen = 300.0
        ),
        CoreUpgrade(
            id = "eldritch_momentum",
            name = "Тёмный Импульс",
            description = "Умножает отдачу Сердца при каждом соприкосновении с тканью мироздания.",
            baseCost = 2500.0,
            costMultiplier = 1.20,
            clickPowerAdd = 25.0,
            clickMultiplierAdd = 0.1,
            requiredEchoesSeen = 1500.0
        ),
        CoreUpgrade(
            id = "shard_infusion",
            name = "Осколочное Насыщение",
            description = "Вплавляет осколки в ядро, резко повышая критический урон.",
            baseCost = 10000.0,
            costMultiplier = 1.22,
            critChanceAdd = 0.03,
            critMultiplierAdd = 0.5,
            requiredEchoesSeen = 6000.0
        ),
        CoreUpgrade(
            id = "void_vortex",
            name = "Вихрь Забвения",
            description = "Касание раскручивает воронку пространства, втягивая блуждающую энергию.",
            baseCost = 50000.0,
            costMultiplier = 1.25,
            clickPowerAdd = 150.0,
            clickMultiplierAdd = 0.15,
            requiredEchoesSeen = 30000.0
        ),
        CoreUpgrade(
            id = "astral_shatter",
            name = "Астральный Раскол",
            description = "Глубокая трещина в материи, дарующая колоссальные критические всплески.",
            baseCost = 250000.0,
            costMultiplier = 1.28,
            critChanceAdd = 0.05,
            critMultiplierAdd = 1.0,
            requiredEchoesSeen = 150000.0
        ),
        CoreUpgrade(
            id = "chronal_tear",
            name = "Разрыв Времени",
            description = "Клики вытягивают секунды из погибших эпох, удесятеряя силу.",
            baseCost = 1500000.0,
            costMultiplier = 1.30,
            clickPowerAdd = 1000.0,
            clickMultiplierAdd = 0.25,
            requiredEchoesSeen = 1000000.0
        ),
        CoreUpgrade(
            id = "oblivion_strike",
            name = "Удар Небытия",
            description = "Превращает каждое нажатие в сейсмический катаклизм космического ужаса.",
            baseCost = 10000000.0,
            costMultiplier = 1.35,
            clickPowerAdd = 8000.0,
            critChanceAdd = 0.05,
            critMultiplierAdd = 2.0,
            requiredEchoesSeen = 6000000.0
        ),
        CoreUpgrade(
            id = "singularity_heart",
            name = "Сердце Сингулярности",
            description = "Сжимает пространство в бесконечно плотную точку абсолютной мощи.",
            baseCost = 100000000.0,
            costMultiplier = 1.40,
            clickPowerAdd = 50000.0,
            clickMultiplierAdd = 0.5,
            requiredEchoesSeen = 50000000.0
        )
    )

    fun initialStructures(): List<Structure> = listOf(
        Structure(
            id = "void_dredger",
            name = "Землечерпалка Пустоты",
            description = "Механический бур из ржавого железа, непрерывно скребущий пласты умирающего мира.",
            baseCost = 20.0,
            costMultiplier = 1.15,
            baseDps = 1.0
        ),
        Structure(
            id = "cultist_coven",
            name = "Шабаш Культистов",
            description = "Группа фанатиков в капюшонах, бормочущих молитвы Сердцу во тьме.",
            baseCost = 120.0,
            costMultiplier = 1.16,
            baseDps = 6.0,
            requiredEchoesSeen = 80.0
        ),
        Structure(
            id = "abyssal_obelisk",
            name = "Обелиск Бездны",
            description = "Монолит из чёрного обсидиана, пассивно притягивающий рассеянное эхо и эссенцию.",
            baseCost = 800.0,
            costMultiplier = 1.18,
            baseDps = 35.0,
            essencePerSec = 0.05,
            requiredEchoesSeen = 500.0
        ),
        Structure(
            id = "soul_furnace",
            name = "Горнило Душ",
            description = "Древняя алхимическая печь, сжигающая остатки материи для выработки эссенции.",
            baseCost = 4500.0,
            costMultiplier = 1.20,
            baseDps = 180.0,
            essencePerSec = 0.2,
            requiredEchoesSeen = 3000.0
        ),
        Structure(
            id = "astral_harvester",
            name = "Астральный Жнец",
            description = "Левитирующий механизм с серповидными лезвиями, срезающий эфирные нити космоса.",
            baseCost = 25000.0,
            costMultiplier = 1.22,
            baseDps = 950.0,
            essencePerSec = 0.8,
            requiredEchoesSeen = 18000.0
        ),
        Structure(
            id = "chrono_forge",
            name = "Кузница Времени",
            description = "Аномальный комплекс, перековывающий застывшие секунды в чистейшую энергию.",
            baseCost = 150000.0,
            costMultiplier = 1.24,
            baseDps = 5200.0,
            essencePerSec = 2.5,
            requiredEchoesSeen = 100000.0
        ),
        Structure(
            id = "leviathan_spire",
            name = "Шпиль Левиафана",
            description = "Колоссальная башня из плоти и звёздной стали, направляющая мощь космических глубин.",
            baseCost = 900000.0,
            costMultiplier = 1.26,
            baseDps = 28000.0,
            essencePerSec = 8.0,
            requiredEchoesSeen = 600000.0
        ),
        Structure(
            id = "cosmic_rift",
            name = "Космический Разлом",
            description = "Стабильная дыра в ткани пространства, через которую хлещет необузданный хаос.",
            baseCost = 6000000.0,
            costMultiplier = 1.28,
            baseDps = 160000.0,
            essencePerSec = 25.0,
            requiredEchoesSeen = 4000000.0
        ),
        Structure(
            id = "eye_of_azathoth",
            name = "Око Первородного",
            description = "Гигантское пульсирующее око спящего божества, созерцающее конец мироздания.",
            baseCost = 45000000.0,
            costMultiplier = 1.30,
            baseDps = 1000000.0,
            essencePerSec = 100.0,
            requiredEchoesSeen = 30000000.0
        ),
        Structure(
            id = "abyssal_citadel",
            name = "Цитадель Забвения",
            description = "Величественный город-крепость, парящий над пустотой и выкачивающий суть бытия.",
            baseCost = 350000000.0,
            costMultiplier = 1.33,
            baseDps = 7500000.0,
            essencePerSec = 400.0,
            requiredEchoesSeen = 200000000.0
        )
    )

    fun initialAlchemyRecipes(): List<AlchemyRecipe> = listOf(
        AlchemyRecipe(
            id = "transmute_shards",
            name = "Кристаллизация Эха",
            description = "Сжимает рассеянное эхо в твердый Осколок Пустоты.",
            inputCurrency = CurrencyType.ECHOES,
            inputCost = 500.0,
            outputCurrency = CurrencyType.SHARDS,
            outputAmount = 1.0,
            bonusOutputChance = 0.2
        ),
        AlchemyRecipe(
            id = "distill_essence",
            name = "Дистилляция Эссенции",
            description = "Переплавляет осколки в мерцающую Эссенцию Хаоса.",
            inputCurrency = CurrencyType.SHARDS,
            inputCost = 10.0,
            outputCurrency = CurrencyType.ESSENCE,
            outputAmount = 5.0,
            bonusOutputChance = 0.25
        ),
        AlchemyRecipe(
            id = "forge_rune",
            name = "Гравировка Древней Руны",
            description = "Насыщает эссенцию памятью предков, создавая Древнюю Руну.",
            inputCurrency = CurrencyType.ESSENCE,
            inputCost = 50.0,
            outputCurrency = CurrencyType.ANCIENT_RUNES,
            outputAmount = 1.0,
            bonusOutputChance = 0.15
        ),
        AlchemyRecipe(
            id = "echo_burst",
            name = "Всплеск Растворения",
            description = "Растворяет 1 Осколок в массивную волну Эха (мгновенный приток).",
            inputCurrency = CurrencyType.SHARDS,
            inputCost = 1.0,
            outputCurrency = CurrencyType.ECHOES,
            outputAmount = 650.0,
            bonusOutputChance = 0.3
        ),
        AlchemyRecipe(
            id = "chrono_catalyst",
            name = "Хроно-Катализ",
            description = "Смешивает руны и эссенцию для мгновенного получения Пепла Времени.",
            inputCurrency = CurrencyType.ANCIENT_RUNES,
            inputCost = 5.0,
            outputCurrency = CurrencyType.TIME_ASHES,
            outputAmount = 2.0,
            bonusOutputChance = 0.1
        )
    )

    fun initialRunes(): List<Rune> = listOf(
        Rune(
            id = "rune_devour_1",
            name = "Руна Алчности",
            type = RuneType.DEVOUR,
            tier = 1,
            bonusPercent = 25.0,
            costRunes = 2.0
        ),
        Rune(
            id = "rune_eternity_1",
            name = "Руна Бессмертия",
            type = RuneType.ETERNITY,
            tier = 1,
            bonusPercent = 20.0,
            costRunes = 2.0
        ),
        Rune(
            id = "rune_ruin_1",
            name = "Руна Разгрома",
            type = RuneType.RUIN,
            tier = 1,
            bonusPercent = 35.0,
            costRunes = 3.0
        ),
        Rune(
            id = "rune_alchemy_1",
            name = "Руна Трансмутации",
            type = RuneType.ALCHEMY,
            tier = 1,
            bonusPercent = 40.0,
            costRunes = 4.0
        ),
        Rune(
            id = "rune_devour_2",
            name = "Руна Пожирателя Миров",
            type = RuneType.DEVOUR,
            tier = 2,
            bonusPercent = 75.0,
            costRunes = 8.0
        ),
        Rune(
            id = "rune_cosmos_1",
            name = "Руна Первозданного Космоса",
            type = RuneType.COSMOS,
            tier = 2,
            bonusPercent = 50.0,
            costRunes = 12.0
        )
    )

    fun initialArtifacts(): List<Artifact> = listOf(
        Artifact(
            id = "azathoth_eye",
            name = "Око Незрячего Бога",
            lore = "Окаменевший зрачок древней сущности, следящий за биением Сердца.",
            rarity = ArtifactRarity.ELDRITCH,
            baseUpgradeCostCores = 1.0,
            effectDescription = "+50% к урону клика и +5% шанс критического удара за уровень."
        ),
        Artifact(
            id = "void_crown",
            name = "Венец Пустоты",
            lore = "Корона из черного звездного металла, венчавшая первого владыку разлома.",
            rarity = ArtifactRarity.MYTHIC,
            baseUpgradeCostCores = 2.0,
            effectDescription = "+30% ко всей пассивной генерации Эха за уровень."
        ),
        Artifact(
            id = "oblivion_hourglass",
            name = "Песочные Часы Забвения",
            lore = "Вместо песка внутри струится прах погибших цивилизаций.",
            rarity = ArtifactRarity.EPIC,
            baseUpgradeCostCores = 1.0,
            effectDescription = "+25% к оффлайн-доходу и +1 час к лимиту оффлайна."
        ),
        Artifact(
            id = "grimoire_of_madness",
            name = "Гримуар Безумия",
            lore = "Страницы книги сотканы из шепотов, сводящих с ума смертных мудрецов.",
            rarity = ArtifactRarity.RARE,
            baseUpgradeCostCores = 1.0,
            effectDescription = "+20% к выходу Эссенции и эффективности алхимии."
        ),
        Artifact(
            id = "astral_blade",
            name = "Клинок Затмения",
            lore = "Оружие, разрубившее границу между светом и изначальной тьмой.",
            rarity = ArtifactRarity.EPIC,
            baseUpgradeCostCores = 2.0,
            effectDescription = "+100% к урону по боссам во время испытаний."
        ),
        Artifact(
            id = "heart_of_dying_star",
            name = "Сердце Угасшей Звезды",
            lore = "Сверхплотное ядро белого карлика, излучающее холодную гравитацию.",
            rarity = ArtifactRarity.MYTHIC,
            baseUpgradeCostCores = 3.0,
            effectDescription = "Увеличивает получение Пепла Времени при Престиже на +20%."
        ),
        Artifact(
            id = "cloak_of_the_void",
            name = "Мантия Теней",
            lore = "Ткань, поглощающая любой луч света и отражающая чистую тишину.",
            rarity = ArtifactRarity.COMMON,
            baseUpgradeCostCores = 1.0,
            effectDescription = "Снижает базовую стоимость всех сооружений на 5%."
        ),
        Artifact(
            id = "mirror_of_ether",
            name = "Зеркало Эфира",
            lore = "Отражает не того, кто смотрит, а то, чем он станет в конце времен.",
            rarity = ArtifactRarity.RARE,
            baseUpgradeCostCores = 1.0,
            effectDescription = "Увеличивает силу всех активных фамильяров на +30%."
        )
    )

    fun initialFamiliars(): List<Familiar> = listOf(
        Familiar(
            id = "shadow_wisp",
            name = "Теневой Огонёк",
            subtitle = "Блуждающий дух Бездны",
            lore = "Маленький сгусток темного пламени, ластящийся к Сердцу и автоматически кликающий по нему.",
            autoClicksPerSec = 2.0,
            dpsBonusPercent = 5.0,
            baseCostEssence = 25.0
        ),
        Familiar(
            id = "void_devourer",
            name = "Пожиратель Эха",
            subtitle = "Хищник астральных течений",
            lore = "Многоглазое существо с пастью из щупалец, усиливающее пассивную добычу ресурсов.",
            autoClicksPerSec = 0.0,
            dpsBonusPercent = 25.0,
            baseCostEssence = 100.0
        ),
        Familiar(
            id = "chrono_wraith",
            name = "Хроно-Призрак",
            subtitle = "Хранитель разорванных эпох",
            lore = "Бесплотный призрак, ускоряющий ход времени вокруг Сердца.",
            autoClicksPerSec = 4.0,
            dpsBonusPercent = 40.0,
            baseCostEssence = 400.0
        ),
        Familiar(
            id = "astral_leviathan",
            name = "Астральный Змей",
            subtitle = "Исполин предвечного океана",
            lore = "Гигантский змей, свернувшийся в кольцо вокруг мира. Наносит сокрушительный урон.",
            autoClicksPerSec = 10.0,
            dpsBonusPercent = 100.0,
            baseCostEssence = 1500.0
        )
    )

    fun initialBiomes(): List<Biome> = listOf(
        Biome(
            id = 1,
            name = "Разрушенные Врата",
            subtitle = "Руины забытого порога",
            description = "Место, где когда-то стояли барьеры, защищавшие мир от космического небытия.",
            unlockEchoesRequired = 0.0,
            dpsMultiplier = 1.0,
            bossName = "Хранитель Врат",
            bossTitle = "Страж Треснувшего Порога",
            bossMaxHp = 1500.0,
            bossRewardCores = 1.0,
            bossRewardAshes = 5.0,
            isUnlocked = true
        ),
        Biome(
            id = 2,
            name = "Обсидиановые Глубины",
            subtitle = "Море застывшего камня",
            description = "Бескрайние скалы из вулканического стекла, пропитанные древней кровью богов.",
            unlockEchoesRequired = 25000.0,
            dpsMultiplier = 1.5,
            bossName = "Обсидиановый Голем",
            bossTitle = "Монолит Вечной Ярости",
            bossMaxHp = 60000.0,
            bossRewardCores = 3.0,
            bossRewardAshes = 25.0
        ),
        Biome(
            id = 3,
            name = "Астральный Разлом",
            subtitle = "Вихрь мерцающих звезд",
            description = "Где законы физики теряют смысл, а пространство расслаивается на сотни измерений.",
            unlockEchoesRequired = 1000000.0,
            dpsMultiplier = 2.5,
            bossName = "Астральный Пожиратель",
            bossTitle = "Ужас Межзвездной Пустоты",
            bossMaxHp = 2500000.0,
            bossRewardCores = 8.0,
            bossRewardAshes = 120.0
        ),
        Biome(
            id = 4,
            name = "Престол Древних",
            subtitle = "Обитель спящих владык",
            description = "Зал из циклопических колонн, где эхо первобытного сна звучит как оглушительный гром.",
            unlockEchoesRequired = 50000000.0,
            dpsMultiplier = 5.0,
            bossName = "Аватар Хаоса",
            bossTitle = "Тень Неназванного",
            bossMaxHp = 120000000.0,
            bossRewardCores = 20.0,
            bossRewardAshes = 600.0
        ),
        Biome(
            id = 5,
            name = "Сердце Сингулярности",
            subtitle = "Эпицентр Коллапса",
            description = "Финальная бездна, где все сущее возвращается к истоку черного пламени.",
            unlockEchoesRequired = 2000000000.0,
            dpsMultiplier = 12.0,
            bossName = "Абсолютное Ничто",
            bossTitle = "Венец Вечного Забвения",
            bossMaxHp = 5000000000.0,
            bossRewardCores = 50.0,
            bossRewardAshes = 3000.0
        )
    )

    fun initialConstellations(): List<ConstellationPerk> = listOf(
        ConstellationPerk(
            id = "perk_eternity",
            name = "Звезда Вечности",
            description = "+15% ко всей глобальной добыче Эха за каждый ранг.",
            costAshes = 5.0,
            bonusPerRank = 0.15
        ),
        ConstellationPerk(
            id = "perk_primal_strike",
            name = "Первородная Сила",
            description = "+25% к силе активных кликов по Сердцу за ранг.",
            costAshes = 8.0,
            bonusPerRank = 0.25
        ),
        ConstellationPerk(
            id = "perk_clairvoyance",
            name = "Око Прозрения",
            description = "+2% к шансу крита и +30% к критическому множителю за ранг.",
            costAshes = 15.0,
            bonusPerRank = 0.02
        ),
        ConstellationPerk(
            id = "perk_chrono_anomaly",
            name = "Временная Аномалия",
            description = "+25% к эффективности оффлайн-дохода и +2 часа к максимальному лимиту.",
            costAshes = 20.0,
            bonusPerRank = 0.25
        ),
        ConstellationPerk(
            id = "perk_chaos_mastery",
            name = "Власть над Хаосом",
            description = "Снижает базовую стоимость всех сооружений и улучшений на 3% за ранг.",
            costAshes = 35.0,
            bonusPerRank = 0.03
        ),
        ConstellationPerk(
            id = "perk_supernova",
            name = "Рождение Сверхновой",
            description = "+40% к получаемым Ядрам Пустоты и Пеплу при победе над боссами.",
            costAshes = 50.0,
            bonusPerRank = 0.40
        ),
        ConstellationPerk(
            id = "perk_alchemical_genesis",
            name = "Алхимический Генезис",
            description = "+35% к вероятности критического удвоения при трансмутации.",
            costAshes = 40.0,
            bonusPerRank = 0.05
        ),
        ConstellationPerk(
            id = "perk_abyssal_resonance",
            name = "Абсолютный Резонанс",
            description = "Каждое открытое сооружение увеличивает силу клика на 1% за ранг.",
            costAshes = 75.0,
            bonusPerRank = 0.01
        )
    )

    fun initialQuests(): List<Quest> = listOf(
        Quest(
            id = "q_clicks_100",
            title = "Первые Содрогания",
            description = "Нанесите 100 ударов по Сердцу Бездны.",
            target = 100.0,
            type = QuestType.CLICKS,
            rewardCurrency = CurrencyType.ECHOES,
            rewardAmount = 500.0
        ),
        Quest(
            id = "q_clicks_1000",
            title = "Неутомимый Палач",
            description = "Нанесите 1,000 ударов по Сердцу Бездны.",
            target = 1000.0,
            type = QuestType.CLICKS,
            rewardCurrency = CurrencyType.SHARDS,
            rewardAmount = 15.0
        ),
        Quest(
            id = "q_echoes_10k",
            title = "Шепот Пустоты",
            description = "Соберите суммарно 10,000 Эха Бездны.",
            target = 10000.0,
            type = QuestType.TOTAL_ECHOES,
            rewardCurrency = CurrencyType.ESSENCE,
            rewardAmount = 20.0
        ),
        Quest(
            id = "q_echoes_1m",
            title = "Прилив Забвения",
            description = "Соберите суммарно 1,000,000 Эха Бездны.",
            target = 1000000.0,
            type = QuestType.TOTAL_ECHOES,
            rewardCurrency = CurrencyType.TIME_ASHES,
            rewardAmount = 10.0
        ),
        Quest(
            id = "q_structs_10",
            title = "Первая Машина",
            description = "Постройте 10 любых сооружений добычи.",
            target = 10.0,
            type = QuestType.STRUCTURES,
            rewardCurrency = CurrencyType.ECHOES,
            rewardAmount = 2500.0
        ),
        Quest(
            id = "q_structs_50",
            title = "Промышленность Хаоса",
            description = "Постройте суммарно 50 сооружений.",
            target = 50.0,
            type = QuestType.STRUCTURES,
            rewardCurrency = CurrencyType.SHARDS,
            rewardAmount = 50.0
        ),
        Quest(
            id = "q_boss_1",
            title = "Падение Стража",
            description = "Одержите победу над первым боссом биома.",
            target = 1.0,
            type = QuestType.BOSS_KILLS,
            rewardCurrency = CurrencyType.VOID_CORES,
            rewardAmount = 2.0
        ),
        Quest(
            id = "q_boss_3",
            title = "Убийца Исполинов",
            description = "Победите 3 боссов в различных биомах.",
            target = 3.0,
            type = QuestType.BOSS_KILLS,
            rewardCurrency = CurrencyType.VOID_CORES,
            rewardAmount = 6.0
        ),
        Quest(
            id = "q_transmute_5",
            title = "Начинающий Алхимик",
            description = "Проведите 5 успешных трансмутаций ресурсов.",
            target = 5.0,
            type = QuestType.ALCHEMY_TRANSMUTES,
            rewardCurrency = CurrencyType.ANCIENT_RUNES,
            rewardAmount = 2.0
        ),
        Quest(
            id = "q_transmute_25",
            title = "Магистр Преобразования",
            description = "Проведите 25 трансмутаций.",
            target = 25.0,
            type = QuestType.ALCHEMY_TRANSMUTES,
            rewardCurrency = CurrencyType.ANCIENT_RUNES,
            rewardAmount = 10.0
        ),
        Quest(
            id = "q_prestige_1",
            title = "Цикл Возрождения",
            description = "Совершите первый Прорыв Печати (Престиж).",
            target = 1.0,
            type = QuestType.PRESTIGE_COUNT,
            rewardCurrency = CurrencyType.TIME_ASHES,
            rewardAmount = 25.0
        ),
        Quest(
            id = "q_clicks_10000",
            title = "Рука Рока",
            description = "Совершите 10,000 ударов по Сердцу Бездны.",
            target = 10000.0,
            type = QuestType.CLICKS,
            rewardCurrency = CurrencyType.VOID_CORES,
            rewardAmount = 15.0
        )
    )

    fun initialLore(): List<LoreChapter> = listOf(
        LoreChapter(
            id = 1,
            title = "Глава I: Пробуждение в Прах",
            unlockCondition = "Доступно с самого начала",
            text = "Мир умирал не в пламени и не в криках. Он умирал в тишине. Солнце померкло столетия назад, обратившись в холодный хрустальный осколок. Из трещин истощенной земли поднялся лишь один монолит — Сердце Бездны. Пульсирующее, древнее, жаждущее прикосновения того, кто осмелится пробудить его спящий голод.",
            isUnlocked = true
        ),
        LoreChapter(
            id = 2,
            title = "Глава II: Землечерпалки и Шум Шестерен",
            unlockCondition = "Постройте 5 Землечерпалок Пустоты",
            text = "Ржавое железо врезается в обсидиановую породу. Древние механизмы не знают усталости, соскребая тонкие пласты застывшей космической пыли. Говорят, каждая песчинка здесь — это память о давно сгоревшей звезде. Собирая эхо, мы собираем само полотно исчезнувшего времени."
        ),
        LoreChapter(
            id = 3,
            title = "Глава III: Лики Скрытых Культов",
            unlockCondition = "Наймите Шабаш Культистов",
            text = "Они пришли сами, босые и укутанные в истлевшие саваны. Их глаза затянуты бельмами, но они видят то, что сокрыто за гранью физического зрения. Их бормотание — не молитва, а математическое уравнение искажения реальности. Чем громче их хор, тем сильнее бьется Сердце."
        ),
        LoreChapter(
            id = 4,
            title = "Глава IV: Таинство Трансмутации",
            unlockCondition = "Проведите первую алхимическую трансмутацию",
            text = "Эхо бестелесно, пока не подвергнется давлению воли. Сожмите его в ладонях — и оно затвердеет в Осколок Пустоты, острый как бритва и холодный как вакуум. Измельчите осколок — и польется вязкая, мерцающая Эссенция Хаоса, кровь древней космологии."
        ),
        LoreChapter(
            id = 5,
            title = "Глава V: Падение Первого Стража",
            unlockCondition = "Победите босса Разрушенных Врат",
            text = "Страж Врат был выкован из титана и метеоритного камня тысячелетия назад теми, кто отчаянно пытался запечатать Бездна снаружи. Но запечатать Бездна — значит запечатать само дыхание Вселенной. Когда колосс рухнул, трещины поползли дальше, открывая путь в Обсидиановые Глубины."
        ),
        LoreChapter(
            id = 6,
            title = "Глава VI: Шепот Первородных",
            unlockCondition = "Откройте Астральный Разлом",
            text = "Там, за горизонтом событий, спят сущности величиной с галактики. Мы не являемся их врагами; для них мы даже не пылинки. Но выкачивая энергию Сердца, мы словно щекочем спящего колосса. Иногда кажется, что когда Сердце вздрагивает — это вздрагивает сам Космос."
        ),
        LoreChapter(
            id = 7,
            title = "Глава VII: Пепел Времени и Вечное Возвращение",
            unlockCondition = "Совершите первый Прорыв Печати",
            text = "Прорыв Печати — это не конец, а сброс хрупкого физического слоя. Сооружения рушатся в прах, эхо рассеивается, но дух закаляется в горниле Пепла Времени. Каждое перерождение приближает смертного к статусу Аватара Бездны."
        ),
        LoreChapter(
            id = 8,
            title = "Глава VIII: Преображение",
            unlockCondition = "Победите Абсолютное Ничто",
            text = "Граница между тем, кто кликает, и Сердцем, по которому бьют, стерта окончательно. Ты больше не наблюдатель разрушения. Ты — архитектор новой вселенной, восстающей из пепла и вечного забвения."
        )
    )
}
