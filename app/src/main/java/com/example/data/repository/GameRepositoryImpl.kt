package com.huhx0015.questadventure.data.repository

import com.huhx0015.questadventure.domain.model.BoundaryType
import com.huhx0015.questadventure.domain.model.GameMetadata
import com.huhx0015.questadventure.domain.model.MapScreen
import com.huhx0015.questadventure.domain.model.ScreenFeature
import com.huhx0015.questadventure.domain.repository.GameRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GameRepositoryImpl : GameRepository {

    private val games = listOf(
        GameMetadata(
            id = "kq1",
            title = "Quest I",
            subtitle = "Quest for the Crown",
            releaseYear = "1984",
            story = "Sir Graham of Daventry must find three legendary lost treasures (the Shield of Protection, the Mirror of the Future, and the Chest of Gold) to redeem the dying kingdom and become its rightful King.",
            themeColorHex = "#0000AA", // Royal Retro Blue
            accentColorHex = "#FFFF55", // EGA Light Yellow
            boundaryType = BoundaryType.WRAP, // Repeating torus map!
            width = 8,
            height = 6,
            initialX = 7,
            initialY = 2
        ),
        GameMetadata(
            id = "kq2",
            title = "Quest II",
            subtitle = "Romancing the Throne",
            releaseYear = "1985",
            story = "Now King, Graham gazes into his magic mirror and witnesses the beautiful Princess Valanice imprisoned in a quartz tower. He journeys to the magical realm of Kolyma to find three mystical keys to free her.",
            themeColorHex = "#AA00AA", // EGA Magenta Accent
            accentColorHex = "#55FFFF", // Cyan Highlight
            boundaryType = BoundaryType.WRAP,
            width = 8,
            height = 6,
            initialX = 0,
            initialY = 0
        ),
        GameMetadata(
            id = "kq3",
            title = "Quest III",
            subtitle = "To Heir Is Human",
            releaseYear = "1986",
            story = "Kidnapped as an infant by the cruel wizard Manannan, young Gwydion must craft complex magical spells from ingredients hidden in Llewdor before the wizard inspects his chores, in order to escape and find his true lineage.",
            themeColorHex = "#00AAAA", // Deep Teal
            accentColorHex = "#FF55FF", // Bright Pink
            boundaryType = BoundaryType.WRAP,
            width = 8,
            height = 4,
            initialX = 3,
            initialY = 3
        ),
        GameMetadata(
            id = "kq4",
            title = "Quest IV",
            subtitle = "The Perils of Roselia",
            releaseYear = "1988",
            story = "With King Graham on his deathbed, Princess Rosella volunteers to travel to the distant continent of Roselia to find the Tree of Life. She must evade Lolotte, the dark fairy queen, and restore light to the land.",
            themeColorHex = "#AA0000", // Dark Crimson
            accentColorHex = "#FFAA00", // Gold Accent
            boundaryType = BoundaryType.BLOCK, // Normal boundaries (Mountains/Swamps)
            width = 8,
            height = 5,
            initialX = 4,
            initialY = 2
        ),
        GameMetadata(
            id = "kq5",
            title = "Quest V",
            subtitle = "Absence Makes the Heart Go Yonder!",
            releaseYear = "1990",
            story = "The evil wizard Mordack shrinks Castle Daventry and steals Graham's family. Guided by Cedric, a magical talking owl, Graham journeys across desert sands, snowy peaks, and dark forests to face the tyrant.",
            themeColorHex = "#00AA00", // EGA Green
            accentColorHex = "#55FF55", // Bright Green
            boundaryType = BoundaryType.BLOCK,
            width = 6,
            height = 6,
            initialX = 0,
            initialY = 2
        ),
        GameMetadata(
            id = "kq6",
            title = "Quest VI",
            subtitle = "Heir Today, Gone Tomorrow",
            releaseYear = "1992",
            story = "Prince Alexander follows a memory of Princess Cassima to the mysterious Archipelago of the Green Isles. To reach her, he must voyage between warring islands, solve riddles of the Winged Ones, and outwit Death itself.",
            themeColorHex = "#000080", // Deep Navy
            accentColorHex = "#00FFFF", // Neon Teal
            boundaryType = BoundaryType.BLOCK,
            width = 5,
            height = 5,
            initialX = 2,
            initialY = 2
        ),
        GameMetadata(
            id = "kq7",
            title = "Quest VII",
            subtitle = "The Princeless Bride",
            releaseYear = "1994",
            story = "Using a Disney-style painterly aesthetic, this chapter follows Queen Valanice and Princess Rosella as they are sucked into a whirlpool leading to the underground troll worlds, spooky forests, and high cloud cities.",
            themeColorHex = "#4B0082", // Indigo
            accentColorHex = "#EE82EE", // Violet Accent
            boundaryType = BoundaryType.BLOCK,
            width = 5,
            height = 5,
            initialX = 2,
            initialY = 2
        )
    )

    override fun getGames(): Flow<List<GameMetadata>> = flow {
        emit(games)
    }

    override fun getGame(id: String): Flow<GameMetadata?> = flow {
        emit(games.find { it.id == id })
    }

    override fun getMapScreens(gameId: String): Flow<List<MapScreen>> = flow {
        val game = games.find { it.id == gameId } ?: return@flow emit(emptyList())
        val list = mutableListOf<MapScreen>()
        for (y in 0 until game.height) {
            for (x in 0 until game.width) {
                list.add(getOrCreateScreen(gameId, x, y, game.width, game.height))
            }
        }
        emit(list)
    }

    override fun getMapScreen(gameId: String, x: Int, y: Int): Flow<MapScreen?> = flow {
        val game = games.find { it.id == gameId } ?: return@flow emit(null)
        // Normalize coordinates based on wrap settings
        val coord = normalizeCoordinates(x, y, game.width, game.height, game.boundaryType)
        if (coord == null) {
            emit(null)
        } else {
            emit(getOrCreateScreen(gameId, coord.first, coord.second, game.width, game.height))
        }
    }

    private fun normalizeCoordinates(
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        boundaryType: BoundaryType
    ): Pair<Int, Int>? {
        if (boundaryType == BoundaryType.WRAP) {
            val normX = (x % width + width) % width
            val normY = (y % height + height) % height
            return Pair(normX, normY)
        } else {
            if (x in 0 until width && y in 0 until height) {
                return Pair(x, y)
            }
            return null // Blocked!
        }
    }

    private fun getOrCreateScreen(gameId: String, x: Int, y: Int, width: Int, height: Int): MapScreen {
        // Retrieve specific override if any
        val specificScreen = overlays[gameId]?.find { it.x == x && it.y == y }
        if (specificScreen != null) return specificScreen

        // Otherwise generate a highly structured, procedurally descriptive location
        return generateProceduralScreen(gameId, x, y, width, height)
    }

    private fun generateProceduralScreen(gameId: String, x: Int, y: Int, width: Int, height: Int): MapScreen {
        val seed = (gameId.hashCode() + x * 31 + y * 97).hashCode()
        val r = java.util.Random(seed.toLong())

        return when (gameId) {
            "kq1" -> {
                // Daventry is divided into forests, lake shores, and hills
                val isNearRiver = (x + y) % 3 == 0
                val isNorthernForest = y < 2
                val isSouthernHills = y > 3

                val title: String
                val description: String
                val feature: ScreenFeature

                if (isNearRiver) {
                    feature = ScreenFeature.WATER
                    title = if (r.nextBoolean()) "Babbling River Brook" else "Meandering Stream"
                    description = "A refreshing, crystal-clear stream cuts diagonally through the woodland turf of Daventry. Tiny speckled trout dart below the rippling current. Rocks rise gently along the shore."
                } else if (isNorthernForest) {
                    feature = ScreenFeature.FOREST
                    title = if (r.nextBoolean()) "Shady Oak Grove" else "Thick Woodland Path"
                    description = "A standard clearing surrounded by towering oak, beech, and pine trees. Light filters through the leaves, casting soft shadows. Squirrels scamper among fallen acorns."
                } else if (isSouthernHills) {
                    feature = ScreenFeature.PATH
                    title = if (r.nextBoolean()) "Rolling Green Cliffs" else "Rocky Ridge"
                    description = "The terrain rises steeply into rocky hills in southern Daventry. Large granite rocks line the ridge. Far in the distance, blue mountains outline the horizons."
                } else {
                    feature = ScreenFeature.PATH
                    title = if (r.nextBoolean()) "Lush Meadow" else "Quiet Glade"
                    description = "A quiet, green grassy clearing under a blue sky. Wildflowers of red and sapphire colors sway with the gentle Daventry breeze, scenting the warm air."
                }

                MapScreen(
                    x = x,
                    y = y,
                    title = title,
                    description = "$description You feel Graham's determination as you scan the horizon for lost magical artifacts.",
                    feature = feature,
                    dangerLevel = if (r.nextInt(10) < 2) "Mild" else "Safe",
                    characters = if (r.nextInt(10) == 0) listOf("Roaming Elf") else emptyList()
                )
            }
            "kq2" -> {
                // Kolyma shores, swamp limits
                val isCoast = x == 0 || x == 7
                val isSwamp = y >= 4

                val title: String
                val description: String
                val feature: ScreenFeature

                if (isCoast) {
                    feature = ScreenFeature.WATER
                    title = if (r.nextBoolean()) "Sandy Beachfront" else "Rocky Shoreline"
                    description = "Warm waves crash against the sandy shores of Kolyma. Splashes of seafoam sparkle under the direct blue sun. High sea eagles circle the skies above."
                } else if (isSwamp) {
                    feature = ScreenFeature.SWAMP
                    title = if (r.nextBoolean()) "Murky Bog" else "Foggy Marshland"
                    description = "The soil grows incredibly soggy and thick mist rises from dark marshy waters. gnarled cyprus roots stick up like wooden fingers. Creepy croaking echoes around."
                } else {
                    feature = ScreenFeature.PATH
                    title = if (r.nextBoolean()) "Kolyma Plains" else "Overgrown Bramble Crossroads"
                    description = "An intricate maze of woodland paths, dense bramble-bushes, and colorful flowery meadows. Cobblestone trails lead off to alternative magical gateways."
                }

                MapScreen(
                    x = x,
                    y = y,
                    title = title,
                    description = description,
                    feature = feature,
                    dangerLevel = if (feature == ScreenFeature.SWAMP) "Mild" else "Safe"
                )
            }
            "kq3" -> {
                // Llewdor spans from beach to steep mountain peaks of Manannan
                val isMountain = y == 0
                val isBeach = x == 0

                val title: String
                val description: String
                val feature: ScreenFeature

                if (isMountain) {
                    feature = ScreenFeature.MOUNTAIN
                    title = "Steep Mountain Trail"
                    description = "Dangerous steep stairs carved directly into raw slate lead up to Wizard Manannan's high laboratory. A slip could plunge you straight into the sharp rocks below."
                } else if (isBeach) {
                    feature = ScreenFeature.WATER
                    title = "Sandy Llewdor Shore"
                    description = "Deep coastal sand tires your traveling feet. Waves of the ocean lap against the beach. Seagulls cry as they scan for crabs."
                } else {
                    feature = ScreenFeature.FOREST
                    title = if (r.nextBoolean()) "Llewdor Deep Woods" else "Pine Tree Path"
                    description = "Large, thick forest canopies block out most of the sky. Lizards scurry over rotting logs. You must be careful—Manannan's spies could be tracking Gwydion."
                }

                MapScreen(
                    x = x,
                    y = y,
                    title = title,
                    description = description,
                    feature = feature,
                    dangerLevel = if (isMountain) "Mild" else "Safe"
                )
            }
            "kq4" -> {
                // Roselia coastline is east (x=7), swamp is west (x=0)
                val isBeach = x == 7
                val isSwamp = x == 0
                val isMountain = y == 0

                val title: String
                val description: String
                val feature: ScreenFeature

                if (isSwamp) {
                    feature = ScreenFeature.SWAMP
                    title = "Foggy Swamp Border"
                    description = "Ominous quicksand and heavy willow moss make walking very hazardous here. Strange bubbling gases rise periodically from deep dark mud."
                } else if (isBeach) {
                    feature = ScreenFeature.WATER
                    title = "Sunny Coastline"
                    description = "A long sandy beach stretching north to south. Shells litter the tideline. Beautiful waves crash rhythmic lullabies on the beach."
                } else if (isMountain) {
                    feature = ScreenFeature.MOUNTAIN
                    title = "Roselia Mountain Cliffs"
                    description = "High jagged cliffs block further passage north. Pine and cedar trees cling horizontally on rocky mountain outcroppings."
                } else {
                    feature = ScreenFeature.PATH
                    title = if (r.nextBoolean()) "Grassy Glade" else "Forest Trail"
                    description = "A beautiful and peaceful woodland area of Roselia. Sunlight filters through weeping willow branches. Fragrant wild berries grow near paths."
                }

                MapScreen(
                    x = x,
                    y = y,
                    title = title,
                    description = description,
                    feature = feature
                )
            }
            "kq5" -> {
                // Serenia has endless Desert to the south & west
                val isDesert = y >= 3 || x == 4 || x == 5
                val isMountain = y == 0

                val title: String
                val description: String
                val feature: ScreenFeature

                if (isDesert) {
                    feature = ScreenFeature.DESERT
                    title = if (r.nextBoolean()) "Endless Desert Dunes" else "Blazing Desert Clearing"
                    description = "Great sand mounds stretch as far as the eye can see under a blazing yellow sun. Decaying skeleton bones warn you of dehydration danger. Seek an oasis!"
                } else if (isMountain) {
                    feature = ScreenFeature.MOUNTAIN
                    title = "Snowy Mountain Pass"
                    description = "Bitter cold winds howl through frozen rocky crevasses. Icicles hang from dark grey granite. The mountain air is thin and dangerous."
                } else {
                    feature = ScreenFeature.FOREST
                    title = if (r.nextBoolean()) "Serenia Forest Path" else "Willow Brook Glade"
                    description = "Sunbeams cross beautifully between giant redwood redwood trunks. Birds sing charming melodies. Cedric the Owl hooting echoes advice."
                }

                MapScreen(
                    x = x,
                    y = y,
                    title = title,
                    description = description,
                    feature = feature,
                    dangerLevel = if (isDesert || isMountain) "Deadly" else "Safe"
                )
            }
            "kq6" -> {
                // Archipelago of Green Isles
                val islandName = when {
                    y <= 1 && x <= 1 -> "Island of the Sacred Mountain"
                    y <= 1 && x >= 3 -> "Island of Wonder"
                    y >= 3 && x >= 3 -> "Island of the Beast"
                    y >= 3 && x <= 1 -> "Island of the Mists"
                    else -> "Island of the Crown"
                }

                val feature = when (islandName) {
                    "Island of the Sacred Mountain" -> ScreenFeature.MOUNTAIN
                    "Island of Wonder" -> ScreenFeature.SPECIAL
                    "Island of the Beast" -> ScreenFeature.FOREST
                    "Island of the Mists" -> ScreenFeature.SWAMP
                    else -> ScreenFeature.TOWN
                }

                MapScreen(
                    x = x,
                    y = y,
                    title = "Path on $islandName",
                    description = "You are exploring the outer paths of the famous $islandName. The unique customs of each island govern the local wildlife and atmosphere around Alexander.",
                    feature = feature,
                    landmarkName = islandName
                )
            }
            else -> {
                // KQ7 Cartoon Adventure
                val region = when {
                    y == 0 -> "Cloud Kingdom Etheria"
                    y <= 2 -> "Desert of Eldritch"
                    y == 3 -> "Volcania caverns"
                    else -> "Forest of Ooga Booga"
                }

                val feature = when (region) {
                    "Cloud Kingdom Etheria" -> ScreenFeature.SPECIAL
                    "Desert of Eldritch" -> ScreenFeature.DESERT
                    "Volcania caverns" -> ScreenFeature.MOUNTAIN
                    else -> ScreenFeature.CEMETERY
                }

                MapScreen(
                    x = x,
                    y = y,
                    title = "Clearing in $region",
                    description = "A richly painted screen depicting the vibrant cartoon scenery of $region. Interactive items sparkle gently, prompting Valanice and Rosella to search.",
                    feature = feature
                )
            }
        }
    }

    // Specific famous landmarks corresponding to the true game coordinates!
    private val overlays = mapOf(
        "kq1" to listOf(
            MapScreen(
                x = 7, y = 2,
                title = "Castle Daventry Gates",
                description = "The impressive gray stone walls of Castle Daventry stand proudly in front of you. A crocodile-infested deep moat encircles the fortress to ward off thieves. Heavy gates are guarded by stout knights. This is where King Graham's journey both starts and ends.",
                feature = ScreenFeature.CASTLE,
                landmarkName = "Castle Daventry",
                characters = listOf("Royal Guards"),
                items = listOf("Polished Pebbles"),
                hint = "Bow down to show respect, or find a way around if the gates are shuttered!"
            ),
            MapScreen(
                x = 7, y = 3,
                title = "Daventry Throne Room",
                description = "Inside the grand hall, the dying King Edward sits wearily upon his throne. He pleads with Sir Graham to recover the three stolen treasures. Once they are returned, the crown and the golden future of Daventry will belong to you.",
                feature = ScreenFeature.CASTLE,
                landmarkName = "Throne Room",
                characters = listOf("King Edward"),
                hint = "Retrieve the Mirror, Shield, and Chest of Gold from the far ends of the map."
            ),
            MapScreen(
                x = 4, y = 0,
                title = "The Magic Well",
                description = "A deep circular well built from mossy bricks stands in a quiet clearing. A rusted water bucket hangs by a sturdy hemp rope. Peering into the darkness, you can hear water dripping far below.",
                feature = ScreenFeature.HOUSE,
                landmarkName = "The Well",
                items = listOf("Water Bucket"),
                hint = "Climb down the rope or dive in to discover an underwater dragon's cave!"
            ),
            MapScreen(
                x = 2, y = 3,
                title = "Dahlia's Gingerbread House",
                description = "An alluring cabin constructed completely from warm gingerbread, sweet gumdrops, and frosted glazing is nestled in a thick woods clearing. It belongs to Dahlia, the wicked green witch.",
                feature = ScreenFeature.HOUSE,
                landmarkName = "Gingerbread House",
                characters = listOf("Dahlia the Witch"),
                items = listOf("Magic Flour"),
                dangerLevel = "Mild",
                hint = "Do not let the witch catch you! Eat her house or push her in her oven if she's there."
            ),
            MapScreen(
                x = 0, y = 0,
                title = "Woodcutter's Starving Cabin",
                description = "A humble wooden shack sits among piles of dry logs. Here lives a poor, starving woodcutter and his gentle wife. They have run out of food and hope.",
                feature = ScreenFeature.HOUSE,
                landmarkName = "Woodcutter's Cabin",
                characters = listOf("Starving Woodcutter", "Woodcutter's Wife"),
                items = listOf("Golden Fiddle (Unlocking reward)"),
                hint = "Give them the magical bowl that fills with hot broth to save them from starvation."
            ),
            MapScreen(
                x = 4, y = 4,
                title = "The Gnome's Orchard",
                description = "Gnarled apple trees surround an odd old gnome sitting by a wooden golden spinning wheel. He chuckles warmly, spinning straw into gold thread. He will give Gwydion a magic bean if you can guess his name.",
                feature = ScreenFeature.SPECIAL,
                landmarkName = "Gnome's Clearing",
                characters = listOf("The Gnome"),
                items = listOf("Magic Beans"),
                hint = "His name is IFNIK, solved using a backward-alphabet riddle corresponding to RUMPELSTILTSKIN!"
            ),
            MapScreen(
                x = 1, y = 1,
                title = "The Giant Oak Tree",
                description = "An enormous ancient oak tree rises majestically toward the clouds. Climbing its thick branches reveals a golden bird's nest resting on an upper fork.",
                feature = ScreenFeature.FOREST,
                landmarkName = "Great Oak",
                items = listOf("Golden Egg"),
                hint = "Be careful climbing down—one wrong step can mean a flat adventurer!"
            ),
            MapScreen(
                x = 2, y = 0,
                title = "Eerie Cave Entrance",
                description = "A cave carved in high granite hills. Hot volcanic smoke and low rumbling groans indicate a giant, fire-breathing dragon dwells in the subterranean shadows within.",
                feature = ScreenFeature.MOUNTAIN,
                landmarkName = "Dragon's Cave",
                characters = listOf("Sulfur Dragon"),
                dangerLevel = "Deadly",
                hint = "A bucket of water can extinguish the dragon's fiery breath instantly!"
            )
        ),
        "kq2" to listOf(
            MapScreen(
                x = 0, y = 0,
                title = "Castle Kolyma",
                description = "The beautiful quartz castle of Kolyma rises above the coastal wetlands. The drawbridge is raised and the gates lock tight. King Graham must wander Kolyma to unlock three doors of alternative realms.",
                feature = ScreenFeature.CASTLE,
                landmarkName = "Castle Kolyma",
                hint = "You will need the gold key from the cloud dimension, the ruby key, and the sapphire key!"
            ),
            MapScreen(
                x = 2, y = 4,
                title = "Kolyma Antique Shop",
                description = "A dusty shop crammed with antique books, old lamps, and ticking clocks. A polite, old shopkeeper stands behind the checkout counter.",
                feature = ScreenFeature.TOWN,
                landmarkName = "Antique Shop",
                characters = listOf("Old Shopkeeper"),
                items = listOf("Vase", "Oil Lamp"),
                hint = "He will gladly trade his valuable magic lamp for a beautiful gold cage!"
            ),
            MapScreen(
                x = 7, y = 0,
                title = "Hagatha's Rocky Cave",
                description = "A dark seaside cave belonging to Hagatha, the wicked hag. A delicate nightingale cries in a shiny gold cage near a boiling cauldron.",
                feature = ScreenFeature.HOUSE,
                landmarkName = "Hagatha's Lair",
                characters = listOf("Hagatha"),
                items = listOf("Nightingale in Cage"),
                dangerLevel = "Mild",
                hint = "Wait for Hagatha to turn her back, then grab the nightingale cage and run like the wind!"
            ),
            MapScreen(
                x = 3, y = 2,
                title = "Red Riding Hood's Woods",
                description = "Sunbeams pierce beautiful tree leaves. Red Riding Hood is picking sweet lilies for her ailing grandmother.",
                feature = ScreenFeature.FOREST,
                landmarkName = "Fairy Clearing",
                characters = listOf("Red Riding Hood"),
                hint = "Go to the woodcutter's house to find soup to give to her."
            )
        ),
        "kq3" to listOf(
            MapScreen(
                x = 3, y = 0,
                title = "Manannan's Peak and Manor",
                description = "High on a steep jagged peak, Manannan's stone manor dominates the sky. Here, Gwydion is forced to clean dishes and feed chickens under the cruel watchful eye of the teleporting wizard.",
                feature = ScreenFeature.HOUSE,
                landmarkName = "Wizard's Manor",
                characters = listOf("Wizard Manannan", "Black Cat"),
                items = listOf("Mistletoe", "Cat Hair", "Chicken Feather"),
                dangerLevel = "Deadly",
                hint = "Wait until the wizard takes a nap to brew your turn-him-into-a-cat cookie potion!"
            ),
            MapScreen(
                x = 3, y = 3,
                title = "Llewdor Harbour Town",
                description = "A small harbor town of Llewdor. Seagulls fly around a tavern serving dockworkers. A majestic pirate ship is docked at the end of the wooden pier.",
                feature = ScreenFeature.TOWN,
                landmarkName = "Port of Llewdor",
                characters = listOf("Dock Pirate", "Tavern Barkeep"),
                items = listOf("Fly Sprig"),
                hint = "You can purchase passage on the pirate ship or hide in their holds!"
            ),
            MapScreen(
                x = 5, y = 2,
                title = "Bandit's Tall Treehouse",
                description = "A gigantic pine tree in Llewdor forests has a wooden rope ladder leading up to a platform. It's the secret treehouse cache of the forest bandits.",
                feature = ScreenFeature.HOUSE,
                landmarkName = "Bandits Cache",
                characters = listOf("Sleeping Bandit"),
                items = listOf("Stolen Purse of Coins"),
                hint = "Climb silently when they are asleep, or they will rob Gwydion blind!"
            )
        ),
        "kq4" to listOf(
            MapScreen(
                x = 0, y = 0,
                title = "The Bleak Haunted House",
                description = "An ominous gray victorian mansion shivering against cold winds. The shuttered glass wraps rumors of five crying ghosts trapped within.",
                feature = ScreenFeature.CEMETERY,
                landmarkName = "Haunted Mansion",
                characters = listOf("Restless Ghost Child"),
                items = listOf("Brass Locket", "Silver Coin"),
                dangerLevel = "Mild",
                hint = "Search the graveyard crypts to match the ghosts' stolen items and secure peace."
            ),
            MapScreen(
                x = 4, y = 2,
                title = "Fisherman's Coastal Cabin",
                description = "A simple, weathering shack resting on the shoreline sands. Internally, a friendly fisherman and his tired wife lament their empty nets.",
                feature = ScreenFeature.HOUSE,
                landmarkName = "Fisherman's Shack",
                characters = listOf("Fisherman", "Wife"),
                items = listOf("Fishing Rod"),
                hint = "Give them the golden spinning wheel to trade for their fishing rod."
            ),
            MapScreen(
                x = 7, y = 4,
                title = "Lolotte's Terrifying Castle",
                description = "A gothic basalt fortress resting high on jagged peaks. Lolotte, the dark fairy queen, summons gargoyles to guard her treasures. She demands three tasks of Rosella.",
                feature = ScreenFeature.CASTLE,
                landmarkName = "Lolotte's Castle",
                characters = listOf("Lolotte", "Gargoyles"),
                hint = "You will need the Golden Hen, the Magic Pand flute, and Genesta's stolen Talisman."
            )
        ),
        "kq5" to listOf(
            MapScreen(
                x = 3, y = 3,
                title = "Serenia Marketplace",
                description = "The bustling brick-paved lanes of Serenia town. Fragrant bakery sweets smell sweet, and a tailor measures fabric. Graham and his owl Cedric are looking for supplies.",
                feature = ScreenFeature.TOWN,
                landmarkName = "Market Square",
                characters = listOf("Cedric", "Tailor LLC"),
                items = listOf("Meat Pie"),
                hint = "A shimmering coin can buy delicious pie to soothe a freezing traveler later."
            ),
            MapScreen(
                x = 0, y = 2,
                title = "Crispin's Magical Cottage",
                description = "A colorful cottage with a turning weather-vane and a cozy hearth. Crispin, the old wizard of Serenia, feeds Graham magical berries that enable talking with animals.",
                feature = ScreenFeature.HOUSE,
                landmarkName = "Crispin's Cabin",
                characters = listOf("Crispin", "Cedric"),
                items = listOf("Magic Traveling Wand"),
                hint = "He sets Graham off on his quest. Pay attention to Cedric's warnings!"
            ),
            MapScreen(
                x = 2, y = 5,
                title = "Endless Desert Oasis",
                description = "A pristine blue water pool nestled among green date palms. In the baking dryness, this is the only water source for leagues around.",
                feature = ScreenFeature.DESERT,
                landmarkName = "Desert Oasis",
                items = listOf("Clay Water Jar"),
                hint = "Be sure to drink water every 3 screens in the desert or Graham will experience a dry Game-Over!"
            )
        ),
        "kq6" to listOf(
            MapScreen(
                x = 2, y = 2,
                title = "Island of the Crown Castle",
                description = "The beautiful, white marble castle of the Land of the Green Isles rises majestically. Vizier Alhazred rules with an iron fist, scheming to marry Princess Cassima by force.",
                feature = ScreenFeature.CASTLE,
                landmarkName = "Castle of the Crown",
                characters = listOf("Princess Cassima", "Alhazred's Guards"),
                items = listOf("Letter of Love"),
                hint = "Use Gwydion's ring of truth to prove your lineage to the guards!"
            ),
            MapScreen(
                x = 0, y = 1,
                title = "Sacred Mountain Cliffs",
                description = "A sky-high spire of orange granite. Five steep granite steps carved with riddles block those who want to reach the Winged Ones.",
                feature = ScreenFeature.MOUNTAIN,
                landmarkName = "Sacred Mountain Steps",
                hint = "Decipher the letters of ancient runic texts on the mountain rocks!"
            ),
            MapScreen(
                x = 4, y = 1,
                title = "The Island of Wonder Shore",
                description = "A wild, whimsical beach where books talk and walking oysters sunbathe on the shore. The laws of physics do not apply here.",
                feature = ScreenFeature.SPECIAL,
                landmarkName = "Island of Wonder",
                characters = listOf("Sentence Gnome", "Talking Book"),
                items = listOf("Spelling Book"),
                hint = "The Gnome will test your grammar! Show him the letters."
            )
        ),
        "kq7" to listOf(
            MapScreen(
                x = 2, y = 2,
                title = "Desert of Eldritch Sands",
                description = "A cartoonish, bright desert with cactus columns, jackalopes jumping through brush, and a deep cave on the ridge. Valanice and Rosella are separated.",
                feature = ScreenFeature.DESERT,
                landmarkName = "Eldritch Desert",
                characters = listOf("Desert Jackalope"),
                items = listOf("Salt Water Shaker"),
                hint = "Use the green cactus to drink sap and quench your dry throat!"
            ),
            MapScreen(
                x = 4, y = 4,
                title = "Spooky Forest of Ooga Booga",
                description = "A hauntingly beautiful, pink and violet cartoonish graveyard. Singing pumpkins, spooky doors with bone locks, and laughing skulls fill this realm.",
                feature = ScreenFeature.CEMETERY,
                landmarkName = "Ooga Booga Forest",
                characters = listOf("Boogeyman"),
                items = listOf("Spooky Pumpkin Key"),
                hint = "Find the undertaker to get a shovel!"
            )
        )
    )
}
