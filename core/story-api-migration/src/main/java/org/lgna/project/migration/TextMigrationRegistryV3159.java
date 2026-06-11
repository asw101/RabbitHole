/*******************************************************************************
 * Copyright (c) 2006, 2015, Carnegie Mellon University. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Products derived from the software may not be called "Alice", nor may
 *    "Alice" appear in their name, without prior written permission of
 *    Carnegie Mellon University.
 *
 * 4. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgement: "This product includes software
 *    developed by Carnegie Mellon University"
 *
 * 5. The gallery of art assets and animations provided with this software is
 *    contributed by Electronic Arts Inc. and may be used for personal,
 *    non-commercial, and academic use only. Redistributions of any program
 *    source code that utilizes The Sims 2 Assets must also retain the copyright
 *    notice, list of conditions and the disclaimer contained in
 *    The Alice 3.0 Art Gallery License.
 *
 * DISCLAIMER:
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND.
 * ANY AND ALL EXPRESS, STATUTORY OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY,  FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE, AND NON-INFRINGEMENT ARE DISCLAIMED. IN NO EVENT
 * SHALL THE AUTHORS, COPYRIGHT OWNERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, PUNITIVE OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING FROM OR OTHERWISE RELATING TO
 * THE USE OF OR OTHER DEALINGS WITH THE SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************/
package org.lgna.project.migration;

import org.lgna.project.Version;

import static org.lgna.project.migration.TextMigrationRule.replace;
import static org.lgna.project.migration.MigrationManager.NO_REPLACEMENT;
import static org.lgna.project.migration.ProjectMigrationTextSnippets.createMoreSpecificFieldReplacement;
import static org.lgna.project.migration.ProjectMigrationTextSnippets.createMoreSpecificFieldPattern;
import static org.lgna.project.migration.ProjectMigrationTextSnippets.createMoreSpecificFieldRule;

// @formatter:off
@Deprecated // Text migrations now load from migrations/text-migrations.json; retained for JSON regeneration and documentation.
final class TextMigrationRegistryV3159 {

  static TextMigration[] create() {
    return new TextMigration[] {
      new TextMigration(
          new Version("3.1.59.0.0"),

          replace(createMoreSpecificFieldPattern("PLANT1", "org.lgna.story.resources.prop.SeaPlantResource"),
              createMoreSpecificFieldReplacement("DOUBLE", "org.lgna.story.resources.prop.SeaSpongeResource")),

          createMoreSpecificFieldRule("CAULDRON", "DEFAULT", "org.lgna.story.resources.prop.CauldronResource"),

          createMoreSpecificFieldRule("YETI", "DEFAULT", "org.lgna.story.resources.biped.YetiResource"),

          createMoreSpecificFieldRule("PANDA", "DEFAULT", "org.lgna.story.resources.biped.PandaResource"),

          createMoreSpecificFieldRule("WONDERLAND_TREE", "DEFAULT", "org.lgna.story.resources.prop.WonderlandTreeResource"),

          createMoreSpecificFieldRule("ABYSSINIAN_CAT", "DEFAULT", "org.lgna.story.resources.quadruped.AbyssinianCatResource"),

          createMoreSpecificFieldRule("SMOOTH", "DEFAULT", "org.lgna.story.resources.marinemammal.ManateeResource"),

          createMoreSpecificFieldRule("MARCH_HARE", "DEFAULT", "org.lgna.story.resources.biped.MarchHareResource"),

          createMoreSpecificFieldRule("BANANA_TREE", "DEFAULT", "org.lgna.story.resources.prop.BananaTreeResource"),

          createMoreSpecificFieldRule("BOULDER1_MOON", "BOULDER1_GRAY", "org.lgna.story.resources.prop.BoulderResource"),

          createMoreSpecificFieldRule("BOULDER1_DESERT", "BOULDER1_BROWN", "org.lgna.story.resources.prop.BoulderResource"),

          createMoreSpecificFieldRule("BOULDER2_MOON", "BOULDER2_GRAY", "org.lgna.story.resources.prop.BoulderResource"),

          createMoreSpecificFieldRule("BOULDER2_DESERT", "BOULDER2_BROWN", "org.lgna.story.resources.prop.BoulderResource"),

          createMoreSpecificFieldRule("BOULDER3_MOON", "BOULDER3_GRAY", "org.lgna.story.resources.prop.BoulderResource"),

          createMoreSpecificFieldRule("BOULDER3_DESERT", "BOULDER3_BROWN", "org.lgna.story.resources.prop.BoulderResource"),

          createMoreSpecificFieldRule("BOULDER4_MOON", "BOULDER4_GRAY", "org.lgna.story.resources.prop.BoulderResource"),

          createMoreSpecificFieldRule("BOULDER4_DESERT", "BOULDER4_BROWN", "org.lgna.story.resources.prop.BoulderResource"),

          createMoreSpecificFieldRule("BOULDER5_MOON", "BOULDER5_GRAY", "org.lgna.story.resources.prop.BoulderResource"),

          createMoreSpecificFieldRule("BOULDER5_DESERT", "BOULDER5_BROWN", "org.lgna.story.resources.prop.BoulderResource"),

          replace("name=\"org.lgna.story.resources.prop.JungleShrubResource",
              "name=\"org.lgna.story.resources.prop.JunglePlantResource"),

          createMoreSpecificFieldRule("CASTLE_GATE", "DEFAULT", "org.lgna.story.resources.prop.CastleGateResource"),

          createMoreSpecificFieldRule("BANANA", "DEFAULT", "org.lgna.story.resources.prop.BananaResource"),

          createMoreSpecificFieldRule("WOLF", "DEFAULT", "org.lgna.story.resources.quadruped.WolfResource"),

          createMoreSpecificFieldRule("STAFF", "DEFAULT", "org.lgna.story.resources.prop.StaffResource"),

          replace("org.lgna.story.resources.prop.LogBridgeResource",
              "org.lgna.story.resources.prop.JungleLogResource"),

          createMoreSpecificFieldRule("DRAGON_BABY_GREEN", "GREEN", "org.lgna.story.resources.quadruped.BabyDragonResource"),

          createMoreSpecificFieldRule("DRAGON_BABY_RED", "RED", "org.lgna.story.resources.quadruped.BabyDragonResource"),

          createMoreSpecificFieldRule("DRAGON_BABY_AQUA", "AQUA", "org.lgna.story.resources.quadruped.BabyDragonResource"),

          createMoreSpecificFieldRule("DRAGON_BABY_BLUE", "BLUE", "org.lgna.story.resources.quadruped.BabyDragonResource"),

          createMoreSpecificFieldRule("STONE_BRIDGE", "DEFAULT", "org.lgna.story.resources.prop.StoneBridgeResource"),

          createMoreSpecificFieldRule("WHITE_RABBIT", "DEFAULT", "org.lgna.story.resources.biped.WhiteRabbitResource"),

          createMoreSpecificFieldRule("BOWLING_PIN", "DEFAULT", "org.lgna.story.resources.prop.BowlingPinResource"),

          createMoreSpecificFieldRule("SHRINE", "DEFAULT", "org.lgna.story.resources.prop.ShrineResource"),

          createMoreSpecificFieldRule("SCOTTY_DOG", "DEFAULT", "org.lgna.story.resources.quadruped.ScottyDogResource"),

          createMoreSpecificFieldRule("FISHING_BASKET", "DEFAULT", "org.lgna.story.resources.prop.FishingBasketResource"),

          createMoreSpecificFieldRule("KITE_SPOOL", "DEFAULT", "org.lgna.story.resources.prop.KiteSpoolResource"),

          createMoreSpecificFieldRule("BABY_YETI", "DEFAULT", "org.lgna.story.resources.biped.BabyYetiResource"),

          createMoreSpecificFieldRule("NO_SCARF", "DEFAULT", "org.lgna.story.resources.biped.BabyYetiResource"),

          createMoreSpecificFieldRule("BUNNY", "DEFAULT", "org.lgna.story.resources.biped.BunnyResource"),

          createMoreSpecificFieldRule("MAPINGUARI", "DEFAULT", "org.lgna.story.resources.biped.MapinguariResource"),

          createMoreSpecificFieldRule("HEDGE", "DEFAULT", "org.lgna.story.resources.prop.HedgeResource"),

          createMoreSpecificFieldRule("MANGO", "DEFAULT", "org.lgna.story.resources.prop.MangoResource"),

          createMoreSpecificFieldRule("SUBMARINE", "DEFAULT", "org.lgna.story.resources.prop.SubmarineResource"),

          createMoreSpecificFieldRule("CASTLE_TOWER_MIDDLE", "DEFAULT", "org.lgna.story.resources.prop.CastleTowerMiddleResource"),

          createMoreSpecificFieldRule("TREEHOUSE", "DEFAULT", "org.lgna.story.resources.prop.TreehouseResource"),

          createMoreSpecificFieldRule("CASTLE_TOWER_BASE", "DEFAULT", "org.lgna.story.resources.prop.CastleTowerBaseResource"),

          createMoreSpecificFieldRule("FISHING_BASKET_LID", "DEFAULT", "org.lgna.story.resources.prop.FishingBasketLidResource"),

          createMoreSpecificFieldRule("PLATEAU1_PLATEAU1_BROWN", "TALL_BROWN", "org.lgna.story.resources.prop.PlateauResource"),

          createMoreSpecificFieldRule("PLATEAU1_PLATEAU1_RED", "TALL_RED", "org.lgna.story.resources.prop.PlateauResource"),

          createMoreSpecificFieldRule("PLATEAU1_PLATEAU1_GRAY", "TALL_GRAY", "org.lgna.story.resources.prop.PlateauResource"),

          createMoreSpecificFieldRule("PLATEAU2_PLATEAU2_BROWN", "SHORT_BROWN", "org.lgna.story.resources.prop.PlateauResource"),

          createMoreSpecificFieldRule("PLATEAU2_PLATEAU1_RED", "SHORT_RED", "org.lgna.story.resources.prop.PlateauResource"),

          createMoreSpecificFieldRule("PLATEAU2_PLATEAU2_GRAY", "SHORT_GRAY", "org.lgna.story.resources.prop.PlateauResource"),

          createMoreSpecificFieldRule("RED_ROVER", "DEFAULT", "org.lgna.story.resources.prop.RedRoverResource"),

          createMoreSpecificFieldRule("ALIEN_ROBOT", "DEFAULT", "org.lgna.story.resources.quadruped.AlienRobotResource"),

          createMoreSpecificFieldRule("SOCCER_BALL", "DEFAULT", "org.lgna.story.resources.prop.SoccerBallResource"),

          createMoreSpecificFieldRule("YAK", "DEFAULT", "org.lgna.story.resources.quadruped.YakResource"),

          createMoreSpecificFieldRule("CAULDRON_LID", "DEFAULT", "org.lgna.story.resources.prop.CauldronLidResource"),

          createMoreSpecificFieldRule("TEAPOT", "DEFAULT", "org.lgna.story.resources.prop.TeapotResource"),

          createMoreSpecificFieldRule("CAIMAN", "DEFAULT", "org.lgna.story.resources.quadruped.CaimanResource"),

          createMoreSpecificFieldRule("QUEEN_OF_HEARTS", "DEFAULT", "org.lgna.story.resources.biped.QueenOfHeartsResource"),

          createMoreSpecificFieldRule("CAMEL", "DEFAULT", "org.lgna.story.resources.quadruped.CamelResource"),

          createMoreSpecificFieldRule("FALCON", "DEFAULT", "org.lgna.story.resources.flyer.FalconResource"),

          createMoreSpecificFieldRule("PIRANHA", "DEFAULT", "org.lgna.story.resources.fish.PiranhaResource"),

          createMoreSpecificFieldRule("BAMBOO1", "SHOOT1", "org.lgna.story.resources.prop.BambooResource"),

          createMoreSpecificFieldRule("BAMBOO2", "SHOOT2", "org.lgna.story.resources.prop.BambooResource"),

          createMoreSpecificFieldRule("BAMBOO3", "SHOOT3", "org.lgna.story.resources.prop.BambooResource"),

          createMoreSpecificFieldRule("BAMBOO4", "SHOOT4", "org.lgna.story.resources.prop.BambooResource"),

          createMoreSpecificFieldRule("TORTOISE", "DEFAULT", "org.lgna.story.resources.biped.TortoiseResource"),

          createMoreSpecificFieldRule("GONG", "DEFAULT", "org.lgna.story.resources.prop.GongResource"),

          createMoreSpecificFieldRule("CARD03", "THREE3", "org.lgna.story.resources.biped.PlayingCardResource"),

          createMoreSpecificFieldRule("CARD10", "TEN10", "org.lgna.story.resources.biped.PlayingCardResource"),

          createMoreSpecificFieldRule("DOLPHIN", "DEFAULT", "org.lgna.story.resources.marinemammal.DolphinResource"),

          createMoreSpecificFieldRule("CAVE", "DEFAULT_UNDERWATER", "org.lgna.story.resources.prop.CaveResource"),

          createMoreSpecificFieldRule("MAGIC_WAND", "DEFAULT", "org.lgna.story.resources.prop.MagicWandResource"),

          createMoreSpecificFieldRule("GONG_MALLET", "DEFAULT", "org.lgna.story.resources.prop.GongMalletResource"),

          replace(createMoreSpecificFieldPattern("CLUSTER1", "org.lgna.story.resources.prop.BambooClusterResource"),
              createMoreSpecificFieldReplacement("CLUSTER1", "org.lgna.story.resources.prop.BambooResource")),

          replace(createMoreSpecificFieldPattern("CLUSTER2", "org.lgna.story.resources.prop.BambooClusterResource"),
              createMoreSpecificFieldReplacement("CLUSTER2", "org.lgna.story.resources.prop.BambooResource")),

          replace(createMoreSpecificFieldPattern("CLUSTER3", "org.lgna.story.resources.prop.BambooClusterResource"),
              createMoreSpecificFieldReplacement("CLUSTER3", "org.lgna.story.resources.prop.BambooResource")),

          replace(createMoreSpecificFieldPattern("CLUSTER4", "org.lgna.story.resources.prop.BambooClusterResource"),
              createMoreSpecificFieldReplacement("CLUSTER4", "org.lgna.story.resources.prop.BambooResource")),

          replace("name=\"org.lgna.story.resources.prop.BambooClusterResource",
              "name=\"org.lgna.story.resources.prop.BambooResource"),

          createMoreSpecificFieldRule("OWL", "DEFAULT", "org.lgna.story.resources.flyer.OwlResource"),

          createMoreSpecificFieldRule("PECCARY", "DEFAULT", "org.lgna.story.resources.quadruped.PeccaryResource"),

          createMoreSpecificFieldRule("TEA_TRAY", "DEFAULT", "org.lgna.story.resources.prop.TeaTrayResource"),

          createMoreSpecificFieldRule("POCKET_WATCH", "DEFAULT", "org.lgna.story.resources.prop.PocketWatchResource"),

          createMoreSpecificFieldRule("BABY_WALRUS", "DEFAULT", "org.lgna.story.resources.marinemammal.BabyWalrusResource"),

          createMoreSpecificFieldRule("SHELF1", "YELLOW", "org.lgna.story.resources.prop.CoralShelfResource"),

          createMoreSpecificFieldRule("SHELF2", "YELLOW", "org.lgna.story.resources.prop.CoralShelfResource"),

          createMoreSpecificFieldRule("ORCA", "DEFAULT", "org.lgna.story.resources.marinemammal.OrcaResource"),

          createMoreSpecificFieldRule("FISHING_LANTERN", "DEFAULT", "org.lgna.story.resources.prop.FishingLanternResource"),

          createMoreSpecificFieldRule("TROLL", "DEFAULT", "org.lgna.story.resources.biped.TrollResource"),

          createMoreSpecificFieldRule("MAD_HATTER", "DEFAULT", "org.lgna.story.resources.biped.MadHatterResource"),

          createMoreSpecificFieldRule("CHICKEN", "MEAN_CHICKEN", "org.lgna.story.resources.flyer.ChickenResource"),

          createMoreSpecificFieldRule("ARAPAIMA", "DEFAULT", "org.lgna.story.resources.fish.ArapaimaResource"),

          createMoreSpecificFieldRule("POND", "LIGHT_BLUE", "org.lgna.story.resources.prop.PondResource"),

          createMoreSpecificFieldRule("PHOENIX", "DEFAULT", "org.lgna.story.resources.flyer.PhoenixResource"),

          createMoreSpecificFieldRule("ICE_MOUNTAIN", "DEFAULT", "org.lgna.story.resources.prop.IceMountainResource"),

          createMoreSpecificFieldRule("BLUE_TANG", "DEFAULT", "org.lgna.story.resources.fish.BlueTangResource"),

          createMoreSpecificFieldRule("JAPANESE_CYPRESS", "DEFAULT", "org.lgna.story.resources.prop.JapaneseCypressResource"),

          createMoreSpecificFieldRule("LIONESS", "DEFAULT", "org.lgna.story.resources.quadruped.LionessResource"),

          createMoreSpecificFieldRule("SPELL_BOOK", "DEFAULT", "org.lgna.story.resources.prop.SpellBookResource"),

          createMoreSpecificFieldRule("WALRUS", "DEFAULT", "org.lgna.story.resources.marinemammal.WalrusResource"),

          createMoreSpecificFieldRule("PIG", "DEFAULT", "org.lgna.story.resources.biped.PigResource"),

          createMoreSpecificFieldRule("POODLE", "DEFAULT", "org.lgna.story.resources.quadruped.PoodleResource"),

          createMoreSpecificFieldRule("COCONUT", "DEFAULT", "org.lgna.story.resources.prop.CoconutResource"),

          createMoreSpecificFieldRule("SHORT_HAIR_CAT", "DEFAULT", "org.lgna.story.resources.quadruped.ShortHairCatResource"),

          createMoreSpecificFieldRule("TOUCAN", "DEFAULT", "org.lgna.story.resources.flyer.ToucanResource"),

          createMoreSpecificFieldRule("ICE_FLOE", "ICE_FLOE1", "org.lgna.story.resources.prop.IceFloeResource"),

          createMoreSpecificFieldRule("COLA_BOTTLE", "DEFAULT", "org.lgna.story.resources.prop.ColaBottleResource"),

          createMoreSpecificFieldRule("WITCH", "DEFAULT", "org.lgna.story.resources.biped.WitchResource"),

          createMoreSpecificFieldRule("PRAYER_FLAGS", "DEFAULT", "org.lgna.story.resources.prop.PrayerFlagsResource"),

          createMoreSpecificFieldRule("CHESHIRE_CAT", "DEFAULT", "org.lgna.story.resources.biped.CheshireCatResource"),

          createMoreSpecificFieldRule("BIG_BAD_WOLF", "DEFAULT", "org.lgna.story.resources.biped.BigBadWolfResource"),

          createMoreSpecificFieldRule("SHARK", "DEFAULT", "org.lgna.story.resources.fish.SharkResource"),

          createMoreSpecificFieldRule("TREASURE_CHEST", "DEFAULT", "org.lgna.story.resources.prop.TreasureChestResource"),

          createMoreSpecificFieldRule("CURUPIRA", "DEFAULT", "org.lgna.story.resources.biped.CurupiraResource"),

          createMoreSpecificFieldRule("FLAMINGO", "DEFAULT", "org.lgna.story.resources.flyer.FlamingoResource"),

          createMoreSpecificFieldRule("FISHING_NET", "DEFAULT", "org.lgna.story.resources.prop.FishingNetResource"),

          createMoreSpecificFieldRule("SAUCER_WHITE_RABBIT", "WHITE_RABBIT", "org.lgna.story.resources.prop.SaucerResource"),

          createMoreSpecificFieldRule("SAUCER_QUEEN", "MARCH_HARE", "org.lgna.story.resources.prop.SaucerResource"),

          createMoreSpecificFieldRule("SAUCER_CHESHIRE", "MARCH_HARE", "org.lgna.story.resources.prop.SaucerResource"),

          createMoreSpecificFieldRule("SAUCER_HATTER", "MAD_HATTER", "org.lgna.story.resources.prop.SaucerResource"),

          createMoreSpecificFieldRule("SAUCER_MARCH_HARE", "MARCH_HARE", "org.lgna.story.resources.prop.SaucerResource"),

          createMoreSpecificFieldRule("SAUCER_PLAYING_CARD", "PLAYING_CARD", "org.lgna.story.resources.prop.SaucerResource"),

          createMoreSpecificFieldRule("TEACUP_CHESHIRE", "MARCH_HARE", "org.lgna.story.resources.prop.TeacupResource"),

          createMoreSpecificFieldRule("TEACUP_HATTER", "MAD_HATTER", "org.lgna.story.resources.prop.TeacupResource"),

          createMoreSpecificFieldRule("TEACUP_MARCH_HARE", "MARCH_HARE", "org.lgna.story.resources.prop.TeacupResource"),

          createMoreSpecificFieldRule("TEACUP_PLAYING_CARD", "PLAYING_CARD", "org.lgna.story.resources.prop.TeacupResource"),

          createMoreSpecificFieldRule("TEACUP_WHITE_RABBIT", "WHITE_RABBIT", "org.lgna.story.resources.prop.TeacupResource"),

          createMoreSpecificFieldRule("TEACUP_QUEEN", "MARCH_HARE", "org.lgna.story.resources.prop.TeacupResource"),

          createMoreSpecificFieldRule("CLOWN_FISH", "DEFAULT", "org.lgna.story.resources.fish.ClownFishResource"),

          createMoreSpecificFieldRule("TENT", "DEFAULT", "org.lgna.story.resources.prop.TentResource"),

          createMoreSpecificFieldRule("ICEBERG", "DEFAULT", "org.lgna.story.resources.prop.IcebergResource"),

          createMoreSpecificFieldRule("MONKEY_KING", "DEFAULT", "org.lgna.story.resources.biped.MonkeyKingResource"),

          createMoreSpecificFieldRule("PAJAMA_FISH", "DEFAULT", "org.lgna.story.resources.fish.PajamaFishResource"),

          createMoreSpecificFieldRule("SEAGULL", "DEFAULT", "org.lgna.story.resources.flyer.SeagullResource"),

          createMoreSpecificFieldRule("CASTLE_TOWER_TOP", "DEFAULT", "org.lgna.story.resources.prop.CastleTowerTopResource"),

          createMoreSpecificFieldRule("MANX_CAT", "DEFAULT", "org.lgna.story.resources.quadruped.ManxCatResource"),

          createMoreSpecificFieldRule("FISHING_BOAT", "DEFAULT", "org.lgna.story.resources.prop.FishingBoatResource"),

          createMoreSpecificFieldRule("FOX", "DEFAULT", "org.lgna.story.resources.quadruped.FoxResource"),

          createMoreSpecificFieldRule("MAGIC_STAFF", "DEFAULT", "org.lgna.story.resources.prop.MagicStaffResource"),

          createMoreSpecificFieldRule("FISHING_LANTERN_POLE", "DEFAULT", "org.lgna.story.resources.prop.FishingLanternPoleResource"),

          createMoreSpecificFieldRule("DALMATIAN", "DEFAULT", "org.lgna.story.resources.quadruped.DalmatianResource"),

          createMoreSpecificFieldRule("STUFFED_TIGER", "DEFAULT", "org.lgna.story.resources.biped.StuffedTigerResource"),

          createMoreSpecificFieldRule("MAGIC_SPOON", "DEFAULT", "org.lgna.story.resources.prop.MagicSpoonResource"),

          createMoreSpecificFieldRule("FISHING_BOAT_CANOPY", "DEFAULT", "org.lgna.story.resources.prop.FishingBoatCanopyResource"),

          createMoreSpecificFieldRule("SHRINE_LANTERN", "DEFAULT", "org.lgna.story.resources.prop.ShrineLanternResource"),

          replace(createMoreSpecificFieldPattern("PIRATE_SHIP", "org.lgna.story.resources.prop.PirateShipResource"),
              createMoreSpecificFieldPattern("DEFAULT", "org.lgna.story.resources.prop.PirateShipPropResource")),

          createMoreSpecificFieldRule("TEA_TABLE", "DEFAULT", "org.lgna.story.resources.prop.TeaTableResource"),

          createMoreSpecificFieldRule("WALL", "DEFAULT", "org.lgna.story.resources.prop.CastleWallResource"),

          replace(createMoreSpecificFieldPattern("RED", "org.lgna.story.resources.prop.TallMushroomResource"),
              createMoreSpecificFieldReplacement("TALL_RED", "org.lgna.story.resources.prop.MushroomResource")),

          replace(createMoreSpecificFieldPattern("WHITE", "org.lgna.story.resources.prop.TallMushroomResource"),
              createMoreSpecificFieldReplacement("TALL_WHITE", "org.lgna.story.resources.prop.MushroomResource")),

          replace("name=\"org.lgna.story.resources.prop.TallMushroomResource",
              "name=\"org.lgna.story.resources.prop.MushroomResource"),

          createMoreSpecificFieldRule("TREE_TRUNK", "DEFAULT", "org.lgna.story.resources.prop.TreeTrunkResource"),

          createMoreSpecificFieldRule("DRAGON_ORANGE", "ORANGE", "org.lgna.story.resources.quadruped.DragonResource"),

          createMoreSpecificFieldRule("DRAGON_RED", "RED", "org.lgna.story.resources.quadruped.DragonResource"),

          createMoreSpecificFieldRule("ADIRONDACK_CHAIR_LIVING_ADIRONDACK_CUSHION_GRAY", "ADIRONDACK_GRAY", "org.lgna.story.resources.prop.LoveseatResource"),

          createMoreSpecificFieldRule("ADIRONDACK_CHAIR_LIVING_ADIRONDACK_CUSHION_CAMO", "ADIRONDACK_CAMOUFLAGE", "org.lgna.story.resources.prop.LoveseatResource"),

          createMoreSpecificFieldRule("ADIRONDACK_CHAIR_LIVING_ADIRONDACK_CUSHION_PALM", "ADIRONDACK_GREEN", "org.lgna.story.resources.prop.LoveseatResource"),

          createMoreSpecificFieldRule("ART_NOUVEAU_LOVESEAT_ART_NOUVEAU_FRAME_MOHOGANY", "ART_NOUVEAU_MAHOGANY", "org.lgna.story.resources.prop.LoveseatResource"),

          createMoreSpecificFieldRule("ART_NOUVEAU_LOVESEAT_ART_NOUVEAU_FRAME_OAK", "ART_NOUVEAU_OAK", "org.lgna.story.resources.prop.LoveseatResource"),

          createMoreSpecificFieldRule("CAMEL_BACK_LOVESEAT_EXPENSIVE_CAMEL_BACK_WOOD_LIGHT_WOOD", "CAMEL_BACK_LIGHT_WOOD", "org.lgna.story.resources.prop.LoveseatResource"),

          createMoreSpecificFieldRule("CAMEL_BACK_LOVESEAT_EXPENSIVE_CAMEL_BACK_CUSHION_BEIGE_FABRIC", "CAMEL_BACK_WHITE", "org.lgna.story.resources.prop.LoveseatResource"),

          createMoreSpecificFieldRule("MOROCCAN_SOFA_MOROCCAN_BEIGE", "MOROCCAN_TAN", "org.lgna.story.resources.prop.LoveseatResource"),

          createMoreSpecificFieldRule("MOROCCAN_SOFA_MOROCCAN_GREEN", "MOROCCAN_GREEN", "org.lgna.story.resources.prop.LoveseatResource"),

          createMoreSpecificFieldRule("PARK_BENCH_LOVESEAT_PARK_BENCH_WOOD", "PARK_BENCH_WOOD", "org.lgna.story.resources.prop.LoveseatResource"),

          createMoreSpecificFieldRule("PARK_BENCH_LOVESEAT_PARK_BENCH_OAKGREEN", "PARK_BENCH_GREEN", "org.lgna.story.resources.prop.LoveseatResource"),

          createMoreSpecificFieldRule("PARK_BENCH_LOVESEAT_PARK_BENCH_OAKBLUE", "PARK_BENCH_BLUE", "org.lgna.story.resources.prop.LoveseatResource"),

          createMoreSpecificFieldRule("QUAINT_SOFA_QUAINT_FABRIC_WHITE_FLOWERS", "QUAINT_WHITE", "org.lgna.story.resources.prop.LoveseatResource"),

          createMoreSpecificFieldRule("QUAINT_SOFA_QUAINT_FABRIC_GREEN_FLOWERS", "QUAINT_GREEN", "org.lgna.story.resources.prop.LoveseatResource"),

          createMoreSpecificFieldRule("QUAINT_SOFA_QUAINT_FABRIC_BEIGE_FLOWERS", "QUAINT_BROWN", "org.lgna.story.resources.prop.LoveseatResource"),

          createMoreSpecificFieldRule("VALUE_LOVESEAT_VALUE_BLUE_STRIPE", "VALUE_BLUE_STRIPES", "org.lgna.story.resources.prop.LoveseatResource"),

          createMoreSpecificFieldRule("VALUE1_SOFA_VALUE1_BLUE_STRIPE", "VALUE1_BLUE_STRIPES", "org.lgna.story.resources.prop.SofaResource"),

          createMoreSpecificFieldRule("VALUE1_SOFA_VALUE1_FLOWER", "VALUE1_FLOWERS", "org.lgna.story.resources.prop.SofaResource"),

          createMoreSpecificFieldRule("VALUE2_SOFA_VALUE2_LIGHT_BROWN_FLOWER", "VALUE2_BROWN_FLOWERS", "org.lgna.story.resources.prop.SofaResource"),

          createMoreSpecificFieldRule("VALUE2_SOFA_VALUE2_RED_CHECKER", "VALUE2_RED_SQUARES", "org.lgna.story.resources.prop.SofaResource"),

          createMoreSpecificFieldRule("VALUE2_SOFA_VALUE2_GREEN_FLOWER", "VALUE2_GREEN_FLOWERS", "org.lgna.story.resources.prop.SofaResource"),

          createMoreSpecificFieldRule("VALUE2_SOFA_VALUE2_BLUE_FLOWER_BORDER", "VALUE2_BLUE", "org.lgna.story.resources.prop.SofaResource"),

          createMoreSpecificFieldRule("COLONIAL1_SOFA_COLONIAL1_FRUITS", "COLONIAL1_FRUITS", "org.lgna.story.resources.prop.SofaResource"),

          createMoreSpecificFieldRule("COLONIAL1_SOFA_COLONIAL1_DIAMOND", "COLONIAL1_DIAMONDS", "org.lgna.story.resources.prop.SofaResource"),

          createMoreSpecificFieldRule("COLONIAL1_SOFA_COLONIAL1_REDPATTERN", "COLONIAL1_RED_SQUARES", "org.lgna.story.resources.prop.SofaResource"),

          createMoreSpecificFieldRule("COLONIAL1_SOFA_COLONIAL1_BEIGE", "COLONIAL1_BROWN", "org.lgna.story.resources.prop.SofaResource"),

          createMoreSpecificFieldRule("COLONIAL1_SOFA_COLONIAL1_WHITE_DIAMOND", "COLONIAL1_DIAMONDS", "org.lgna.story.resources.prop.SofaResource"),

          createMoreSpecificFieldRule("COLONIAL1_SOFA_COLONIAL1_LINE_CURVES", "COLONIAL1_CURVES", "org.lgna.story.resources.prop.SofaResource"),

          createMoreSpecificFieldRule("COLONIAL2_SOFA_COLONIAL2_GOLDDIAMOND", "COLONIAL2_GOLD", "org.lgna.story.resources.prop.SofaResource"),

          createMoreSpecificFieldRule("COLONIAL2_SOFA_COLONIAL2_ORANGE", "COLONIAL2_ORANGE", "org.lgna.story.resources.prop.SofaResource"),

          createMoreSpecificFieldRule("COLONIAL2_SOFA_COLONIAL2_RED_STRIPES", "COLONIAL2_RED", "org.lgna.story.resources.prop.SofaResource"),

          createMoreSpecificFieldRule("COLONIAL2_SOFA_COLONIAL2_BLUE_PATTERN", "COLONIAL2_BLUE_PATTERN", "org.lgna.story.resources.prop.SofaResource"),

          createMoreSpecificFieldRule("STEEL_FRAME_SOFA_MODERN_STEEL_FRAME_FABRIC_BLACKLEATHER", "STEEL_FRAME_BLACK_LEATHER", "org.lgna.story.resources.prop.SofaResource"),

          createMoreSpecificFieldRule("STEEL_FRAME_SOFA_MODERN_STEEL_FRAME_FABRIC_CORDOROY", "STEEL_FRAME_BRWON_LEATHER", "org.lgna.story.resources.prop.SofaResource"),

          createMoreSpecificFieldRule("STEEL_FRAME_SOFA_MODERN_STEEL_FRAME_FABRIC_STRIPE", "STEEL_FRAME_BRWON_LEATHER", "org.lgna.story.resources.prop.SofaResource"),

          createMoreSpecificFieldRule("STEEL_FRAME_SOFA_MODERN_STEEL_FRAME_FABRIC_GATOR", "STEEL_FRAME_ALLIGATOR", "org.lgna.story.resources.prop.SofaResource"),

          createMoreSpecificFieldRule("MOROCCAN_SOFA_MOROCCAN_RED", "MOROCCAN_RED", "org.lgna.story.resources.prop.SofaResource"),

          createMoreSpecificFieldRule("MOROCCAN_SOFA_MOROCCAN_GREEN", "MOROCCAN_GREEN", "org.lgna.story.resources.prop.SofaResource"),

          createMoreSpecificFieldRule("MOROCCAN_SOFA_MOROCCAN_BEIGE", "MOROCCAN_LIGHT_BLUE", "org.lgna.story.resources.prop.SofaResource"),

          createMoreSpecificFieldRule("QUAINT_SOFA_QUAINT_FABRIC_WHITE_FLOWERS", "QUAINT_WHITE", "org.lgna.story.resources.prop.SofaResource"),

          createMoreSpecificFieldRule("QUAINT_SOFA_QUAINT_FABRIC_GREEN_FLOWERS", "QUAINT_GREEN", "org.lgna.story.resources.prop.SofaResource"),

          createMoreSpecificFieldRule("QUAINT_SOFA_QUAINT_FABRIC_BLUE_FLOWERS", "QUAINT_BLUE", "org.lgna.story.resources.prop.SofaResource"),

          createMoreSpecificFieldRule("QUAINT_SOFA_QUAINT_FABRIC_PINK_FLOWERS", "QUAINT_PINK", "org.lgna.story.resources.prop.SofaResource"),

          createMoreSpecificFieldRule("MODERN_CUTOUT_SOFA_UM_CUTOUT_BLACK_CREAM", "MODERN_CUTOUT_BLACK", "org.lgna.story.resources.prop.SofaResource"),

          createMoreSpecificFieldRule("MODERN_CUTOUT_SOFA_UM_CUTOUT_BLUE", "MODERN_CUTOUT_BLUE", "org.lgna.story.resources.prop.SofaResource"),

          createMoreSpecificFieldRule("MODERN_CUTOUT_SOFA_UM_CUTOUT_LEOPARD", "MODERN_CUTOUT_LEOPARD", "org.lgna.story.resources.prop.SofaResource"),

          createMoreSpecificFieldRule("MODERN_CUTOUT_SOFA_UM_CUTOUT_PURPLE", "MODERN_CUTOUT_PURPLE", "org.lgna.story.resources.prop.SofaResource"),

          createMoreSpecificFieldRule("MODERN_CUTOUT_SOFA_UM_CUTOUT_ZEBRA", "MODERN_CUTOUT_ZEBRA", "org.lgna.story.resources.prop.SofaResource"),

          createMoreSpecificFieldRule("MODERN_CUTOUT_SOFA_UM_CUTOUT_GREEN", "MODERN_CUTOUT_GREEN", "org.lgna.story.resources.prop.SofaResource"),

          createMoreSpecificFieldRule("MODERN_DIAMOND_SOFA_U_M_DIAMOND_CHECK", "MODERN_DIAMOND_BLUE", "org.lgna.story.resources.prop.SofaResource"),

          createMoreSpecificFieldRule("MODERN_DIAMOND_SOFA_U_M_DIAMOND_BLACK", "MODERN_DIAMOND_BLACK_AND_WHITE", "org.lgna.story.resources.prop.SofaResource"),

          createMoreSpecificFieldRule("MODERN_DIAMOND_SOFA_U_M_DIAMOND_YELLOW", "MODERN_DIAMOND_GREEN", "org.lgna.story.resources.prop.SofaResource"),

          createMoreSpecificFieldRule("MODERN_DIAMOND_SOFA_U_M_DIAMOND_PURPLE", "MODERN_DIAMOND_PURPLE_AND_GREEN", "org.lgna.story.resources.prop.SofaResource"),

          createMoreSpecificFieldRule("MODERN_DIAMOND_SOFA_U_M_DIAMOND_RED", "MODERN_DIAMOND_RED_AND_PURPLE", "org.lgna.story.resources.prop.SofaResource"),

          createMoreSpecificFieldRule("CENTRAL_ASIAN_DRESSER_CENTRAL_ASIAN_GREEN_FLOWERS", "CENTRAL_ASIAN_GREEN_FLOWERS", "org.lgna.story.resources.prop.DresserResource"),

          createMoreSpecificFieldRule("CENTRAL_ASIAN_DRESSER_CENTRAL_ASIAN_RED_FLOWERS", "CENTRAL_ASIAN_RED_FLOWERS", "org.lgna.story.resources.prop.DresserResource"),

          createMoreSpecificFieldRule("CENTRAL_ASIAN_DRESSER_CENTRAL_ASIAN_GREEN", "CENTRAL_ASIAN_GREEN", "org.lgna.story.resources.prop.DresserResource"),

          createMoreSpecificFieldRule("CENTRAL_ASIAN_DRESSER_CENTRAL_ASIAN_RED", "CENTRAL_ASIAN_RED", "org.lgna.story.resources.prop.DresserResource"),

          createMoreSpecificFieldRule("COLONIAL_DRESSER_COLONIAL_WOOD", "COLONIAL_WOOD", "org.lgna.story.resources.prop.DresserResource"),

          createMoreSpecificFieldRule("COLONIAL_DRESSER_COLONIAL_LIGHT_WOOD_CURLY", "COLONIAL_LIGHT_WOOD", "org.lgna.story.resources.prop.DresserResource"),

          createMoreSpecificFieldRule("COLONIAL_DRESSER_COLONIAL_RED_WOOD", "COLONIAL_REDWOOD", "org.lgna.story.resources.prop.DresserResource"),

          createMoreSpecificFieldRule("COLONIAL_DRESSER_COLONIAL_WOOD_STRAIGHT_DARK", "COLONIAL_DARK_WOOD", "org.lgna.story.resources.prop.DresserResource"),

          createMoreSpecificFieldRule("DESIGNER_DRESSER_DESIGNER_BROWN", "DESIGNER_BROWN", "org.lgna.story.resources.prop.DresserResource"),

          createMoreSpecificFieldRule("DESIGNER_DRESSER_DESIGNER_LIGHT_WOOD", "DESIGNER_LIGHT_WOOD", "org.lgna.story.resources.prop.DresserResource"),

          createMoreSpecificFieldRule("DESIGNER_DRESSER_DESIGNER_BLACK", "DESIGNER_BLACK", "org.lgna.story.resources.prop.DresserResource"),

          createMoreSpecificFieldRule("DESIGNER_DRESSER_DESIGNER_BLUE", "DESIGNER_BLUE", "org.lgna.story.resources.prop.DresserResource"),

          createMoreSpecificFieldRule("JAPANESE_DRESSER_JAPANESE_TANSU_NORMAL", "JAPANESE_WOOD", "org.lgna.story.resources.prop.DresserResource"),

          createMoreSpecificFieldRule("JAPANESE_DRESSER_JAPANESE_TANSU_BLACK", "JAPANESE_BLACK", "org.lgna.story.resources.prop.DresserResource"),

          createMoreSpecificFieldRule("JAPANESE_DRESSER_JAPANESE_TANSU_RED", "JAPANESE_RED", "org.lgna.story.resources.prop.DresserResource"),

          createMoreSpecificFieldRule("CENTRAL_ASIAN_TABLE_COFFEE_CENTRAL_ASIAN_REFLECT_CHINESE_RED", "CENTRAL_ASIAN_REDWOOD", "org.lgna.story.resources.prop.CoffeeTableResource"),

          createMoreSpecificFieldRule("CENTRAL_ASIAN_TABLE_COFFEE_CENTRAL_ASIAN_REFLECT_CHINESE_CHERRY", "CENTRAL_ASIAN_CHERRY", "org.lgna.story.resources.prop.CoffeeTableResource"),

          createMoreSpecificFieldRule("CENTRAL_ASIAN_TABLE_COFFEE_CENTRAL_ASIAN_REFLECT_CHINESE_BLONDE", "CENTRAL_ASIAN_BLONDE", "org.lgna.story.resources.prop.CoffeeTableResource"),

          createMoreSpecificFieldRule("CENTRAL_ASIAN_TABLE_COFFEE_CENTRAL_ASIAN_REFLECT_CHINESE_DARK", "CENTRAL_ASIAN_DARK_WOOD", "org.lgna.story.resources.prop.CoffeeTableResource"),

          createMoreSpecificFieldRule("CENTRAL_ASIAN_TABLE_COFFEE_CENTRAL_ASIAN_ASIAN_WOOD_CHINESE_CHERRY", "CENTRAL_ASIAN_FANCY_CHERRY", "org.lgna.story.resources.prop.CoffeeTableResource"),

          createMoreSpecificFieldRule("CENTRAL_ASIAN_TABLE_COFFEE_CENTRAL_ASIAN_ASIAN_WOOD_CHINESE_BLONDE", "CENTRAL_ASIAN_FANCY_BLONDE", "org.lgna.story.resources.prop.CoffeeTableResource"),

          createMoreSpecificFieldRule("CENTRAL_ASIAN_TABLE_COFFEE_CENTRAL_ASIAN_ASIAN_WOOD_CHINESE_RED", "CENTRAL_ASIAN_FANCY_REDWOOD", "org.lgna.story.resources.prop.CoffeeTableResource"),

          createMoreSpecificFieldRule("CENTRAL_ASIAN_TABLE_COFFEE_CENTRAL_ASIAN_ASIAN_WOOD_CHINESE_DARK", "CENTRAL_ASIAN_FANCY_DARK_WOOD", "org.lgna.story.resources.prop.CoffeeTableResource"),

          createMoreSpecificFieldRule("SMALL_CLUB_TABLE_COFFEE_CLUB1_X1_MATERIALS_WOOD", "SMALL_CLUB_WOOD", "org.lgna.story.resources.prop.CoffeeTableResource"),

          createMoreSpecificFieldRule("SMALL_CLUB_TABLE_COFFEE_CLUB1_X1_MATERIALS_WHITEOAK", "SMALL_CLUB_OAK", "org.lgna.story.resources.prop.CoffeeTableResource"),

          createMoreSpecificFieldRule("SMALL_CLUB_TABLE_COFFEE_CLUB1_X1_MATERIAL_BIRDSRED", "SMALL_CLUB_CURLY_REDWOOD", "org.lgna.story.resources.prop.CoffeeTableResource"),

          createMoreSpecificFieldRule("SMALL_CLUB_TABLE_COFFEE_CLUB1_X1_MATERIALS_MAHOG", "SMALL_CLUB_MAHOGANY", "org.lgna.story.resources.prop.CoffeeTableResource"),

          createMoreSpecificFieldRule("SMALL_CLUB_TABLE_COFFEE_CLUB1_X1_MATERIALS_GUMWOOD", "SMALL_CLUB_GREEN", "org.lgna.story.resources.prop.CoffeeTableResource"),

          createMoreSpecificFieldRule("SMALL_CLUB_TABLE_COFFEE_CLUB1_X1_MATERIALS_REDASH", "SMALL_CLUB_REDWOOD", "org.lgna.story.resources.prop.CoffeeTableResource"),

          createMoreSpecificFieldRule("SMALL_CLUB_TABLE_COFFEE_CLUB1_X1_MATERIALS_BLEACHEDOAK", "SMALL_CLUB_WHITE", "org.lgna.story.resources.prop.CoffeeTableResource"),

          createMoreSpecificFieldRule("LARGE_CLUB_TABLE_COFFEE_CLUB_RECTANGLE_WOOD", "LARGE_CLUB_WOOD", "org.lgna.story.resources.prop.CoffeeTableResource"),

          createMoreSpecificFieldRule("LARGE_CLUB_TABLE_COFFEE_CLUB_RECTANGLE_BRIDS_RED", "LARGE_CLUB_CURLY_REDWOOD", "org.lgna.story.resources.prop.CoffeeTableResource"),

          createMoreSpecificFieldRule("LARGE_CLUB_TABLE_COFFEE_CLUB_RECTANGLE_BLEACHED_OAK", "LARGE_CLUB_WHITE", "org.lgna.story.resources.prop.CoffeeTableResource"),

          createMoreSpecificFieldRule("LARGE_CLUB_TABLE_COFFEE_CLUB_RECTANGLE_MAHOG", "LARGE_CLUB_MAHOGANY", "org.lgna.story.resources.prop.CoffeeTableResource"),

          createMoreSpecificFieldRule("LARGE_CLUB_TABLE_COFFEE_CLUB_RECTANGLE_RED_ASH", "LARGE_CLUB_REDWOOD", "org.lgna.story.resources.prop.CoffeeTableResource"),

          createMoreSpecificFieldRule("LARGE_CLUB_TABLE_COFFEE_CLUB_RECTANGLE_WHITE_OAK", "LARGE_CLUB_OAK", "org.lgna.story.resources.prop.CoffeeTableResource"),

          createMoreSpecificFieldRule("LARGE_CLUB_TABLE_COFFEE_CLUB_RECTANGLE_LTBLUE", "LARGE_CLUB_BLUE", "org.lgna.story.resources.prop.CoffeeTableResource"),

          createMoreSpecificFieldRule("COLONIAL_TABLE_COFFEE_COLONIAL_GOLDFLORAL", "COLONIAL_GOLD_FLORAL", "org.lgna.story.resources.prop.CoffeeTableResource"),

          createMoreSpecificFieldRule("COLONIAL_TABLE_COFFEE_COLONIAL_PAONAZZETTO", "COLONIAL_WOOD", "org.lgna.story.resources.prop.CoffeeTableResource"),

          createMoreSpecificFieldRule("COLONIAL_TABLE_COFFEE_COLONIAL_PERLINO", "COLONIAL_PINK", "org.lgna.story.resources.prop.CoffeeTableResource"),

          createMoreSpecificFieldRule("COLONIAL_TABLE_COFFEE_COLONIAL_WHITEMARBLE", "COLONIAL_WHITE", "org.lgna.story.resources.prop.CoffeeTableResource"),

          createMoreSpecificFieldRule("DESIGNER_TABLE_COFFEE_END_DESIGNER_WHITE", "DESIGNER_WHITE", "org.lgna.story.resources.prop.CoffeeTableResource"),

          createMoreSpecificFieldRule("DESIGNER_TABLE_COFFEE_END_DESIGNER_WALNUT", "DESIGNER_WALNUT", "org.lgna.story.resources.prop.CoffeeTableResource"),

          createMoreSpecificFieldRule("LOFT_TABLE_COFFEE_LOFT_SHEEN", "LOFT_CONCRETE", "org.lgna.story.resources.prop.CoffeeTableResource"),

          createMoreSpecificFieldRule("LOFT_TABLE_COFFEE_LOFT_CONCRETE", "LOFT_CONCRETE", "org.lgna.story.resources.prop.CoffeeTableResource"),

          createMoreSpecificFieldRule("LOFT_TABLE_COFFEE_LOFT_PATINA", "LOFT_PATINA", "org.lgna.story.resources.prop.CoffeeTableResource"),

          createMoreSpecificFieldRule("MOROCCAN_TABLE_COFFEE_MOROCCAN_TOP_TABLE_STAR", "MOROCCAN_STARS_INLAY", "org.lgna.story.resources.prop.CoffeeTableResource"),

          createMoreSpecificFieldRule("MOROCCAN_TABLE_COFFEE_MOROCCAN_TOP_TABLE_ALADDIN", "MOROCCAN_YELLOW_INLAY", "org.lgna.story.resources.prop.CoffeeTableResource"),

          createMoreSpecificFieldRule("MOROCCAN_TABLE_COFFEE_MOROCCAN_TOP_TABLE_DETAIL", "MOROCCAN_FANCY_INLAY", "org.lgna.story.resources.prop.CoffeeTableResource"),

          createMoreSpecificFieldRule("MOROCCAN_TABLE_COFFEE_MOROCCAN_TOP_TABLE_TILE", "MOROCCAN_TILE_INLAY", "org.lgna.story.resources.prop.CoffeeTableResource"),

          createMoreSpecificFieldRule("MOROCCAN_TABLE_COFFEE_MOROCCAN_WOODS_MAHOGNY", "MOROCCAN_MAHOGANY", "org.lgna.story.resources.prop.CoffeeTableResource"),

          createMoreSpecificFieldRule("MOROCCAN_TABLE_COFFEE_MOROCCAN_WOODS_YELLOWASPEN", "MOROCCAN_YELLOW", "org.lgna.story.resources.prop.CoffeeTableResource"),

          createMoreSpecificFieldRule("MOROCCAN_TABLE_COFFEE_MOROCCAN_WOODS_BLACK_LAQUER", "MOROCCAN_BLACK", "org.lgna.story.resources.prop.CoffeeTableResource"),

          createMoreSpecificFieldRule("QUAINT_TABLE_COFFEE_QUAINT_BLUE", "QUAINT_BLUE", "org.lgna.story.resources.prop.CoffeeTableResource"),

          createMoreSpecificFieldRule("QUAINT_TABLE_COFFEE_QUAINT_GREEN", "QUAINT_GREEN", "org.lgna.story.resources.prop.CoffeeTableResource"),

          createMoreSpecificFieldRule("QUAINT_TABLE_COFFEE_QUAINT_WHITE", "QUAINT_WHITE", "org.lgna.story.resources.prop.CoffeeTableResource"),

          createMoreSpecificFieldRule("SPINDLE_TABLE_COFFEE_SPINDLE_WOOD_PAINTED", "SPINDLE_PAINTED", "org.lgna.story.resources.prop.CoffeeTableResource"),

          createMoreSpecificFieldRule("SPINDLE_TABLE_COFFEE_SPINDLE_WOOD_RED", "SPINDLE_RED", "org.lgna.story.resources.prop.CoffeeTableResource"),

          createMoreSpecificFieldRule("CENTRAL_ASIAN_DESK_CENTRAL_ASIAN_BLACK", "CENTRAL_ASIAN_BLACK", "org.lgna.story.resources.prop.DeskResource"),

          createMoreSpecificFieldRule("CENTRAL_ASIAN_DESK_CENTRAL_ASIAN_WALNUT", "CENTRAL_ASIAN_WALNUT", "org.lgna.story.resources.prop.DeskResource"),

          createMoreSpecificFieldRule("CLUB_DESK_CLUB_DARKWOOD", "CLUB_DARK_WOOD", "org.lgna.story.resources.prop.DeskResource"),

          createMoreSpecificFieldRule("QUAINT_DESK_QUAINT_GREEN", "QUAINT_GREEN", "org.lgna.story.resources.prop.DeskResource"),

          createMoreSpecificFieldRule("QUAINT_DESK_QUAINT_WHITE", "QUAINT_WHITE", "org.lgna.story.resources.prop.DeskResource"),

          createMoreSpecificFieldRule("QUAINT_DESK_QUAINT_BLUE", "QUAINT_BLUE", "org.lgna.story.resources.prop.DeskResource"),

          createMoreSpecificFieldRule("VALUE_DESK_VALUE_WOODWHITE", "VALUE_LIGHT_WOOD", "org.lgna.story.resources.prop.DeskResource"),

          createMoreSpecificFieldRule("VALUE_DESK_VALUE_WOODRED", "VALUE_RED", "org.lgna.story.resources.prop.DeskResource"),

          createMoreSpecificFieldRule("ACCESSORY_LUGGAGE_SURFACE", "SUITCASE", "org.lgna.story.resources.prop.SuitcaseResource"),

          createMoreSpecificFieldRule("CLUB_TABLE_DINING_CLUB_NEDAR", "CLUB_LIGHT_WOOD", "org.lgna.story.resources.prop.DiningTableResource"),

          createMoreSpecificFieldRule("CLUB_TABLE_DINING_CLUB_OAK", "CLUB_OAK", "org.lgna.story.resources.prop.DiningTableResource"),

          createMoreSpecificFieldRule("CLUB_TABLE_DINING_CLUB_SEDAR", "CLUB_WOOD", "org.lgna.story.resources.prop.DiningTableResource"),

          createMoreSpecificFieldRule("MOROCCAN_TABLE_DINING_MOROCCAN_TURQ", "MOROCCAN_TURQUOISE", "org.lgna.story.resources.prop.DiningTableResource"),

          createMoreSpecificFieldRule("MOROCCAN_TABLE_DINING_MOROCCAN_BLUE", "MOROCCAN_BLUE", "org.lgna.story.resources.prop.DiningTableResource"),

          createMoreSpecificFieldRule("MOROCCAN_TABLE_DINING_MOROCCAN_BLUE_LIGHT", "MOROCCAN_LIGHT_BLUE", "org.lgna.story.resources.prop.DiningTableResource"),

          createMoreSpecificFieldRule("ORIENTAL_TABLE_DINING_ORIENTAL_DRAGON_BROWN", "ORIENTAL_DRAGON_BROWN", "org.lgna.story.resources.prop.DiningTableResource"),

          createMoreSpecificFieldRule("ORIENTAL_TABLE_DINING_ORIENTAL_DRAGON_RED", "ORIENTAL_DRAGON_RED", "org.lgna.story.resources.prop.DiningTableResource"),

          createMoreSpecificFieldRule("ORIENTAL_TABLE_DINING_ORIENTAL_LOTUS_BLACK", "ORIENTAL_LOTUS_BLACK", "org.lgna.story.resources.prop.DiningTableResource"),

          createMoreSpecificFieldRule("ORIENTAL_TABLE_DINING_ORIENTAL_LOTUS_ORANGE", "ORIENTAL_LOTUS_ORANGE", "org.lgna.story.resources.prop.DiningTableResource"),

          createMoreSpecificFieldRule("OUTDOOR_TABLE_DINING_OUTDOOR_WOOD_ASH", "OUTDOOR_ASH", "org.lgna.story.resources.prop.DiningTableResource"),

          createMoreSpecificFieldRule("OUTDOOR_TABLE_DINING_OUTDOOR_WOOD_REDOAK", "OUTDOOR_OAK", "org.lgna.story.resources.prop.DiningTableResource"),

          createMoreSpecificFieldRule("OUTDOOR_TABLE_DINING_OUTDOOR_WOOD_REDWOOD", "OUTDOOR_RED", "org.lgna.story.resources.prop.DiningTableResource"),

          createMoreSpecificFieldRule("OUTDOOR_TABLE_DINING_OUTDOOR_WOOD_WHITE", "OUTDOOR_WHITE", "org.lgna.story.resources.prop.DiningTableResource"),

          createMoreSpecificFieldRule("QUAINT_TABLE_DINING_QUAINT_RED", "QUAINT_RED", "org.lgna.story.resources.prop.DiningTableResource"),

          createMoreSpecificFieldRule("QUAINT_TABLE_DINING_QUAINT_GREEN", "QUAINT_GREEN", "org.lgna.story.resources.prop.DiningTableResource"),

          createMoreSpecificFieldRule("QUAINT_TABLE_DINING_QUAINT_WHITE", "QUAINT_WHITE", "org.lgna.story.resources.prop.DiningTableResource"),

          createMoreSpecificFieldRule("QUAINT_TABLE_DINING_QUAINT_BLUE", "QUAINT_BLUE", "org.lgna.story.resources.prop.DiningTableResource"),

          createMoreSpecificFieldRule("COLONIAL_CURLY_REDWOOD", "COLONIAL_REDWOOD", "org.lgna.story.resources.prop.ArmoireResource"),

          createMoreSpecificFieldRule("COLONIAL_QUILTED_DARK_WOOD", "COLONIAL_DARK_WOOD", "org.lgna.story.resources.prop.ArmoireResource"),

          createMoreSpecificFieldRule("COLONIAL_CURLY_LIGHT_WOOD", "COLONIAL_LIGHT_WOOD", "org.lgna.story.resources.prop.ArmoireResource"),

          createMoreSpecificFieldRule("BIRTHDAY_CAKE_MATERIALS", "BIRTHDAY", "org.lgna.story.resources.prop.CakeResource"),

          createMoreSpecificFieldRule("TRASHCAN_INDOOR_VALUE_CLEAN", "TRASHCAN", "org.lgna.story.resources.prop.TrashcanResource"),

          createMoreSpecificFieldRule("VEHICLE_HELICOPTER", "HELICOPTER", "org.lgna.story.resources.prop.HelicopterResource"),

          createMoreSpecificFieldRule("ART_NOUVEAU_CHAIR_DINING_ART_NOUVEAU_LIGHT_CLEAN", "ART_NOUVEAU_LIGHT_WOOD", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("CLUB_CHAIR_DINING_CLUB_RED_WOOD", "CLUB_DARK_WOOD", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("CLUB_CHAIR_DINING_CLUB_GREENLEATH", "CLUB_DARK_GREEN", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("CLUB_CHAIR_DINING_CLUB_LTGREENLEATH", "CLUB_DARK_GREEN", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("COLONIAL_CHAIR_DINING_COLONIAL1_GOLD_PATTERN", "COLONIAL_GOLD_PATTERN", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("COLONIAL_CHAIR_DINING_COLONIAL1_STRIPES", "COLONIAL_RED_STRIPES", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("COLONIAL_CHAIR_DINING_COLONIAL1_BLUEPATTERN", "COLONIAL_BLUE_PATTERN", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("COLONIAL_CHAIR_DINING_COLONIAL1_DIAMONDS", "COLONIAL_DIAMONDS", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("FANCY_COLONIAL_CHAIR_DINING_COLONIAL2_SRIPE", "FANCY_COLONIAL_RED_STRIPES", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("FANCY_COLONIAL_CHAIR_DINING_COLONIAL2_GOLDPATTERN", "FANCY_COLONIAL_GOLD_PATTERN", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("FANCY_COLONIAL_CHAIR_DINING_COLONIAL2_BLUEPATTERN", "FANCY_COLONIAL_GOLD_PATTERN", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("FANCY_COLONIAL_CHAIR_DINING_COLONIAL2_BLUESILK", "FANCY_COLONIAL_RED_STRIPES", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("FANCY_COLONIAL_CHAIR_DINING_COLONIAL2_DIAMONDPATTERN", "FANCY_COLONIAL_GOLD_PATTERN", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("PARK_LOVESEAT_PARK_BENCH_OAK", "PARK_OAK", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("PARK_LOVESEAT_PARK_BENCH_WALNUT", "PARK_WALNUT", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("PARK_LOVESEAT_PARK_BENCH_RED", "PARK_RED", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("PARK_LOVESEAT_PARK_BENCH_OAKBLUE", "PARK_BLUE", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("PARK_LOVESEAT_PARK_BENCH_IVORY", "PARK_LIGHT_WOOD", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("PARK_LOVESEAT_PARK_BENCH_CHESTNUT", "PARK_CHESTNUT", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("DANISH_MODERN_CHAIR_DINING_DANISH_MODERN_CUSHIONS_GREEN", "DANISH_MODERN_GREEN", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("DANISH_MODERN_CHAIR_DINING_DANISH_MODERN_CUSHIONS_BABY_BLUE", "DANISH_MODERN_BLUE", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("DANISH_MODERN_CHAIR_DINING_DANISH_MODERN_CUSHIONS_WHITE", "DANISH_MODERN_WHITE", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("LOFT_FORK_CHAIR_DINING_LOFT_SEAT_BLUE", "LOFT_FORK_BLUE", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("LOFT_FORK_CHAIR_DINING_LOFT_FORK_BASE_WOOD_LIGHT", "LOFT_FORK_LIGHT_WOOD", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("LOFT_FORK_CHAIR_DINING_LOFT_FORK_BASE_WOOD_ORANGE", "LOFT_FORK_ORANGE_WOOD", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("LOFT_FORK_CHAIR_DINING_LOFT_FORK_BASE_WOOD_RED", "LOFT_FORK_RED_WOOD", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("LOFT_OFFICE_CHAIR_DINING_LOFT_SEAT_BLUE", "LOFT_OFFICE_BLUE", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("LOFT_OFFICE_CHAIR_DINING_LOFT_FORK_BASE_WOOD_LIGHT", "LOFT_OFFICE_LIGHT_WOOD", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("LOFT_OFFICE_CHAIR_DINING_LOFT_FORK_BASE_WOOD_ORANGE", "LOFT_OFFICE_ORANGE_WOOD", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("LOFT_OFFICE_CHAIR_DINING_LOFT_FORK_BASE_WOOD_RED", "LOFT_OFFICE_RED_WOOD", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("MODERATE_CHAIR_DINING_MODERATE_SEAT_GRAY", "MODERATE_GRAY", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("MODERATE_CHAIR_DINING_MODERATE_SEAT_TEAL", "MODERATE_TEAL", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("MODERATE_CHAIR_DINING_MODERATE_SEAT_BLUE", "MODERATE_BLUE", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("MOROCCAN_CHAIR_DINING_MOROCCAN_SURFACES_BLUE_ORANGE", "MOROCCAN_YELLOW", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("MOROCCAN_CHAIR_DINING_MOROCCAN_SURFACES_RED_CIRCLES", "MOROCCAN_RED", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("MOROCCAN_CHAIR_DINING_MOROCCAN_SURFACES_RED_TAN", "MOROCCAN_WHITE", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("MOROCCAN_CHAIR_DINING_MOROCCAN_SURFACES_BLUE_STRIPES", "MOROCCAN_BLUE_STRIPES", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("ORIENTAL_CHAIR_DINING_ORIENTAL_WOOD", "ORIENTAL_WOOD", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("ORIENTAL_CHAIR_DINING_ORIENTAL_LIGHT_WOOD", "ORIENTAL_LIGHT_WOOD", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("ORIENTAL_CHAIR_DINING_ORIENTAL_RED", "ORIENTAL_RED", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("ORIENTAL_CHAIR_DINING_ORIENTAL_ORANGE", "ORIENTAL_ORANGE_WOOD", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("LOFT_LOFT_BOOKCASE_BRUSHED", "LOFT_METAL", "org.lgna.story.resources.prop.BookcaseResource"),

          createMoreSpecificFieldRule("ART_NOUVEAU_BOOKCASE_ART_NOUVEAU_SURFACE", "ART_NOUVEAU", "org.lgna.story.resources.prop.BookcaseResource"),

          createMoreSpecificFieldRule("CHEAP_BOOKCASE_CHEAP_OAK", "CHEAP_OAK", "org.lgna.story.resources.prop.BookcaseResource"),

          createMoreSpecificFieldRule("CHEAP_BOOKCASE_CHEAP_MAHOGANY", "CHEAP_MAHOGANY", "org.lgna.story.resources.prop.BookcaseResource"),

          createMoreSpecificFieldRule("CHEAP_BOOKCASE_CHEAP_PINE", "CHEAP_PINE", "org.lgna.story.resources.prop.BookcaseResource"),

          createMoreSpecificFieldRule("CHEAP_BOOKCASE_CHEAP_BLACK_WASH", "CHEAP_BLACK", "org.lgna.story.resources.prop.BookcaseResource"),

          createMoreSpecificFieldRule("CINDER_BLOCK_BOOKCASE_CINDERBLOCK_SHELVES_BLACKWASH", "CINDER_BLOCK_BLACK", "org.lgna.story.resources.prop.BookcaseResource"),

          createMoreSpecificFieldRule("CINDER_BLOCK_BOOKCASE_CINDERBLOCK_SHELVES_OLDWOOD", "CINDER_BLOCK_PLANK", "org.lgna.story.resources.prop.BookcaseResource"),

          createMoreSpecificFieldRule("COLONIAL_BOOKCASE_COLONIAL_REDWOODCURLY", "COLONIAL_REDWOOD", "org.lgna.story.resources.prop.BookcaseResource"),

          createMoreSpecificFieldRule("COLONIAL_BOOKCASE_COLONIAL_BROWNWOODCURLY", "COLONIAL_DARK_WOOD", "org.lgna.story.resources.prop.BookcaseResource"),

          createMoreSpecificFieldRule("COLONIAL_BOOKCASE_COLONIAL_DARK_BROWN_WOODCURLY", "COLONIAL_WOOD", "org.lgna.story.resources.prop.BookcaseResource"),

          createMoreSpecificFieldRule("VALUE_BOOKCASE_VALUE_PRESSEDPINE", "VALUE_DARK_PINE", "org.lgna.story.resources.prop.BookcaseResource"),

          createMoreSpecificFieldRule("VALUE_BOOKCASE_VALUE_PINE", "VALUE_PINE", "org.lgna.story.resources.prop.BookcaseResource"),

          createMoreSpecificFieldRule("DESIGNER_LIGHTING_FLOOR_DESIGNER_SHADE_ORANGESHADEON", "DESIGNER_ORANGE_ON", "org.lgna.story.resources.prop.LampResource"),

          createMoreSpecificFieldRule("GARDEN_BOLLARD_LIGHTING_FLOOR_GARDEN_TIER_GREEN", "GARDEN_BOLLARD_GREEN_OFF", "org.lgna.story.resources.prop.LampResource"),

          createMoreSpecificFieldRule("GARDEN_TIER_LIGHTING_FLOOR_GARDEN_TIER_GREEN", "GARDEN_TIER_GREEN_OFF", "org.lgna.story.resources.prop.LampResource"),

          createMoreSpecificFieldRule("LOFT_LIGHTING_FLOOR_LOFT_LAMP_SHADE_YELLOW_UNLIT", "LOFT_YELLOW_OFF", "org.lgna.story.resources.prop.LampResource"),

          createMoreSpecificFieldRule("LOFT_LIGHTING_FLOOR_LOFT_LAMP_SHADE_BLUE_UNLIT", "LOFT_BLUE_OFF", "org.lgna.story.resources.prop.LampResource"),

          createMoreSpecificFieldRule("LOFT_LIGHTING_FLOOR_LOFT_LAMP_SHADE_GREEN_UNLIT", "LOFT_GREEN_OFF", "org.lgna.story.resources.prop.LampResource"),

          createMoreSpecificFieldRule("MOROCCAN_LIGHTING_FLOOR_MOROCCAN_SHADE_BLUE_UNLIT", "MOROCCAN_BLUE_OFF", "org.lgna.story.resources.prop.LampResource"),

          createMoreSpecificFieldRule("MOROCCAN_LIGHTING_FLOOR_MOROCCAN_SHADE_GOLD_BLUE_UNLIT", "MOROCCAN_GOLD_BLUE_OFF", "org.lgna.story.resources.prop.LampResource"),

          createMoreSpecificFieldRule("MOROCCAN_LIGHTING_FLOOR_MOROCCAN_SHADE_ORANGE_UNLIT", "MOROCCAN_ORANGE_OFF", "org.lgna.story.resources.prop.LampResource"),

          createMoreSpecificFieldRule("MOROCCAN_LIGHTING_FLOOR_MOROCCAN_SHADE_RED_UNLIT", "MOROCCAN_RED_OFF", "org.lgna.story.resources.prop.LampResource"),

          createMoreSpecificFieldRule("MOROCCAN_LIGHTING_FLOOR_MOROCCAN_SHADE_ORANGE_LIT", "MOROCCAN_ORANGE_ON", "org.lgna.story.resources.prop.LampResource"),

          createMoreSpecificFieldRule("QUAINT_LIGHTING_FLOOR_QUAINT_SHADE_PINK_LIT", "QUAINT_PINK_ON", "org.lgna.story.resources.prop.LampResource"),

          createMoreSpecificFieldRule("QUAINT_LIGHTING_FLOOR_QUAINT_SHADE_PINK", "QUAINT_PINK_OFF", "org.lgna.story.resources.prop.LampResource"),

          createMoreSpecificFieldRule("QUAINT_LIGHTING_FLOOR_QUAINT_SHADE_YELLOW", "QUAINT_YELLOW_OFF", "org.lgna.story.resources.prop.LampResource"),

          createMoreSpecificFieldRule("QUAINT_LIGHTING_FLOOR_QUAINT_SHADE_GREEN", "QUAINT_GREEN_OFF", "org.lgna.story.resources.prop.LampResource"),

          createMoreSpecificFieldRule("STUDIO_LIGHTING_FLOOR_STUDIO_LIGHTS_LIGHTS_UNLIT", "STUDIO_OFF", "org.lgna.story.resources.prop.LampResource"),

          createMoreSpecificFieldRule("VALUE_LIGHTING_FLOOR_VALUE_PAINTED_METAL_BLACKPAINT", "VALUE_BLACK", "org.lgna.story.resources.prop.LampResource"),

          createMoreSpecificFieldRule("VALUE_LIGHTING_FLOOR_VALUE_PAINTED_METAL_REDPAINT", "VALUE_RED", "org.lgna.story.resources.prop.LampResource"),

          createMoreSpecificFieldRule("VALUE_LIGHTING_FLOOR_VALUE_PAINTED_METAL_TANPAINT", "VALUE_TAN", "org.lgna.story.resources.prop.LampResource"),

          createMoreSpecificFieldRule("VALUE_LIGHTING_FLOOR_VALUE_PAINTED_METAL_GREEN_PAINT", "VALUE_GREEN", "org.lgna.story.resources.prop.LampResource"),

          replace(createMoreSpecificFieldPattern("DARK_GREEN", "org.lgna.story.resources.prop.BambooThicketResource"),
              createMoreSpecificFieldReplacement("THICKET_DARK_GREEN", "org.lgna.story.resources.prop.BambooResource")),

          replace(createMoreSpecificFieldPattern("LIGHT_GREEN", "org.lgna.story.resources.prop.BambooThicketResource"),
              createMoreSpecificFieldReplacement("THICKET_LIGHT_GREEN", "org.lgna.story.resources.prop.BambooResource")),

          replace("name=\"org.lgna.story.resources.prop.BambooThicketResource",
              "name=\"org.lgna.story.resources.prop.BambooResource"),

          createMoreSpecificFieldRule("CENTRAL_ASIAN_TABLE_END_CENTRAL_ASIAN_WOOD_BLOND_WOOD", "CENTRAL_ASIAN_BLONDE", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("CENTRAL_ASIAN_TABLE_END_CENTRAL_ASIAN_WOOD_ROUGH", "CENTRAL_ASIAN_ROUGH", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("CENTRAL_ASIAN_TABLE_END_CENTRAL_ASIAN_WOOD_CHERRY", "CENTRAL_ASIAN_CHERRY", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("CENTRAL_ASIAN_TABLE_END_CENTRAL_ASIAN_WOOD_RED_LACQUER", "CENTRAL_ASIAN_RED_LAQUER", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("CENTRAL_ASIAN_TABLE_END_CENTRAL_ASIAN_TABLE_TOP_ROUGH", "CENTRAL_ASIAN_TOP_ROUGH", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("CENTRAL_ASIAN_TABLE_END_CENTRAL_ASIAN_TABLE_TOP_BLOND_WOOD", "CENTRAL_ASIAN_TOP_BLONDE", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("CENTRAL_ASIAN_TABLE_END_CENTRAL_ASIAN_TABLE_TOP_CHERRY", "CENTRAL_ASIAN_TOP_CHERRY", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("CENTRAL_ASIAN_TABLE_END_CENTRAL_ASIAN_TABLE_TOP_RED_LACQUER", "CENTRAL_ASIAN_TOP_RED_LAQUER", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("CLUB_TABLE_COFFEE_CLUB1_X1_MATERIALS_WOOD", "CLUB_WOOD", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("CLUB_TABLE_COFFEE_CLUB1_X1_MATERIAL_BIRDSRED", "CLUB_CURLY_REDWOOD", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("CLUB_TABLE_COFFEE_CLUB1_X1_MATERIALS_MAHOG", "CLUB_MAHOGANY", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("CLUB_TABLE_COFFEE_CLUB1_X1_MATERIALS_GUMWOOD", "CLUB_GREEN", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("CLUB_TABLE_COFFEE_CLUB1_X1_MATERIALS_REDASH", "CLUB_REDWOOD", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("CLUB_TABLE_COFFEE_CLUB1_X1_MATERIALS_BLEACHEDOAK", "CLUB_WHITE", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("COLONIAL_TABLE_END_COLONIAL2_TABLE_LIGHTWOOD", "COLONIAL_LIGHT_WOOD", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("COLONIAL_TABLE_END_COLONIAL2_TABLE_REDWOOD", "COLONIAL_REDWOOD", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("COLONIAL_TABLE_END_COLONIAL2_TABLE_WOOD", "COLONIAL_WOOD", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("COLONIAL_TABLE_END_COLONIAL2_TABLE_DARKWOOD", "COLONIAL_DARK_WOOD", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("OCTAGONAL_TABLE_END_OCTAGONAL_WHITE", "OCTAGONAL_WHITE", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("OCTAGONAL_TABLE_END_OCTAGONAL_DARK", "OCTAGONAL_DARK_WOOD", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("OCTAGONAL_TABLE_END_OCTAGONAL_YELLOW", "OCTAGONAL_YELLOW", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("OCTAGONAL_TABLE_END_OCTAGONAL_GREEN", "OCTAGONAL_GREEN", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("QUAINT_TABLE_END_QUAINT_FABRIC_GREEN", "QUAINT_GREEN", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("UM_TABLE_END_UM_PURPLE", "UM_PURPLE", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("UM_TABLE_END_UM_GREEN", "UM_GREEN", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("UM_TABLE_END_UM_BLACK", "UM_BLACK", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("UM_TABLE_END_UM_YELLOW", "UM_YELLOW", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("UM_TABLE_END_UM_WHITE", "UM_WHITE", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("UM_TABLE_END_UM_ORANGE", "UM_ORANGE", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("VOLLEYBALL_LEATHER", "VOLLEYBALL", "org.lgna.story.resources.prop.VolleyballResource"),

          replace(createMoreSpecificFieldPattern("CAKE_WEDDING_FROSTING", "org.lgna.story.resources.prop.WeddingCakeResource"),
              createMoreSpecificFieldReplacement("WEDDING", "org.lgna.story.resources.prop.CakeResource")),

          replace(createMoreSpecificFieldPattern("PLANT2", "org.lgna.story.resources.prop.SeaPlantResource"),
              createMoreSpecificFieldReplacement("SHORT", "org.lgna.story.resources.prop.SeaSpongeResource")),

          replace(createMoreSpecificFieldPattern("PLANT3", "org.lgna.story.resources.prop.SeaPlantResource"),
              createMoreSpecificFieldReplacement("TALL", "org.lgna.story.resources.prop.SeaSpongeResource")),

          replace("name=\"org.lgna.story.resources.prop.SeaPlantResource",
              "name=\"org.lgna.story.resources.prop.SeaSpongeResource"),

          createMoreSpecificFieldRule("KITE", "DEFAULT", "org.lgna.story.resources.prop.KiteResource"),

          createMoreSpecificFieldRule("BOULDER1_MARS", "BOULDER1_RED", "org.lgna.story.resources.prop.BoulderResource"),

          createMoreSpecificFieldRule("BOULDER2_MARS", "BOULDER2_RED", "org.lgna.story.resources.prop.BoulderResource"),

          createMoreSpecificFieldRule("BOULDER3_MARS", "BOULDER3_RED", "org.lgna.story.resources.prop.BoulderResource"),

          createMoreSpecificFieldRule("BOULDER4_MARS", "BOULDER4_RED", "org.lgna.story.resources.prop.BoulderResource"),

          createMoreSpecificFieldRule("BOULDER5_MARS", "BOULDER5_RED", "org.lgna.story.resources.prop.BoulderResource"),

          replace("name=\"org.lgna.story.resources.prop.JungleShrubResource",
              "name=\"org.lgna.story.resources.prop.JunglePlantResource"),

          createMoreSpecificFieldRule("DRAGON_BABY", "PINK", "org.lgna.story.resources.quadruped.BabyDragonResource"),

          createMoreSpecificFieldRule("HARE", "DEFAULT", "org.lgna.story.resources.biped.HareResource"),

          replace(createMoreSpecificFieldPattern("RED", "org.lgna.story.resources.prop.ShortMushroomResource"),
              createMoreSpecificFieldReplacement("SHORT_RED", "org.lgna.story.resources.prop.MushroomResource")),

          replace(createMoreSpecificFieldPattern("WHITE", "org.lgna.story.resources.prop.ShortMushroomResource"),
              createMoreSpecificFieldReplacement("SHORT_WHITE", "org.lgna.story.resources.prop.MushroomResource")),

          replace("name=\"org.lgna.story.resources.prop.ShortMushroomResource",
              "name=\"org.lgna.story.resources.prop.MushroomResource"),

          createMoreSpecificFieldRule("CARD01", "ONE1", "org.lgna.story.resources.biped.PlayingCardResource"),

          createMoreSpecificFieldRule("CARD02", "TWO2", "org.lgna.story.resources.biped.PlayingCardResource"),

          createMoreSpecificFieldRule("CARD04", "FOUR4", "org.lgna.story.resources.biped.PlayingCardResource"),

          createMoreSpecificFieldRule("CARD05", "FIVE5", "org.lgna.story.resources.biped.PlayingCardResource"),

          createMoreSpecificFieldRule("CARD06", "SIX6", "org.lgna.story.resources.biped.PlayingCardResource"),

          createMoreSpecificFieldRule("CARD07", "SEVEN7", "org.lgna.story.resources.biped.PlayingCardResource"),

          createMoreSpecificFieldRule("CARD08", "EIGHT8", "org.lgna.story.resources.biped.PlayingCardResource"),

          createMoreSpecificFieldRule("CARD09", "NINE9", "org.lgna.story.resources.biped.PlayingCardResource"),

          createMoreSpecificFieldRule("MANGO_TREE", "DEFAULT", "org.lgna.story.resources.prop.MangoTreeResource"),

          createMoreSpecificFieldRule("ALIEN", "DEFAULT", "org.lgna.story.resources.biped.AlienResource"),

          createMoreSpecificFieldRule("SNOWBOARD_YETI", "ADULT_RED", "org.lgna.story.resources.prop.SnowboardResource"),

          createMoreSpecificFieldRule("SNOWBOARD_YETI2", "ADULT_GREEN", "org.lgna.story.resources.prop.SnowboardResource"),

          createMoreSpecificFieldRule("SNOWBOARD_YETI_BABY", "BABY_ORANGE", "org.lgna.story.resources.prop.SnowboardResource"),

          createMoreSpecificFieldRule("SNOWBOARD_YETI_BABY2", "BABY_PINK", "org.lgna.story.resources.prop.SnowboardResource"),

          replace(createMoreSpecificFieldPattern("SPIRE1", "org.lgna.story.resources.prop.RockySpiresResource"),
              createMoreSpecificFieldReplacement("LARGE", "org.lgna.story.resources.prop.RockyOutcropResource")),

          replace(createMoreSpecificFieldPattern("SPIRE2", "org.lgna.story.resources.prop.RockySpiresResource"),
              createMoreSpecificFieldReplacement("MEDIUM", "org.lgna.story.resources.prop.RockyOutcropResource")),

          replace(createMoreSpecificFieldPattern("SPIRE3", "org.lgna.story.resources.prop.RockySpiresResource"),
              createMoreSpecificFieldReplacement("SMALL", "org.lgna.story.resources.prop.RockyOutcropResource")),

          replace("name=\"org.lgna.story.resources.prop.RockySpiresResource",
              "name=\"org.lgna.story.resources.prop.RockyOutcropResource"),

          replace(createMoreSpecificFieldPattern("ROUND", "org.lgna.story.resources.prop.LanternResource"),
              createMoreSpecificFieldReplacement("SHORT_AND_ROUND", "org.lgna.story.resources.prop.PaperLanternResource")),

          replace(createMoreSpecificFieldPattern("OVAL", "org.lgna.story.resources.prop.LanternResource"),
              createMoreSpecificFieldReplacement("TALL_AND_ROUND", "org.lgna.story.resources.prop.PaperLanternResource")),

          replace(createMoreSpecificFieldPattern("BOXY", "org.lgna.story.resources.prop.LanternResource"),
              createMoreSpecificFieldReplacement("SQUARE_HOURGLASS", "org.lgna.story.resources.prop.PaperLanternResource")),

          replace(createMoreSpecificFieldPattern("POINTY", "org.lgna.story.resources.prop.LanternResource"),
              createMoreSpecificFieldReplacement("ROUND_HOURGLASS", "org.lgna.story.resources.prop.PaperLanternResource")),

          replace("name=\"org.lgna.story.resources.prop.LanternResource",
              "name=\"org.lgna.story.resources.prop.PaperLanternResource"),

          createMoreSpecificFieldRule("OAR", "DEFAULT", "org.lgna.story.resources.prop.OarResource"),

          createMoreSpecificFieldRule("SLED", "DEFAULT", "org.lgna.story.resources.prop.SledResource"),

          createMoreSpecificFieldRule("COW", "DEFAULT", "org.lgna.story.resources.quadruped.CowResource"),

          createMoreSpecificFieldRule("DRAGON", "PURPLE", "org.lgna.story.resources.quadruped.DragonResource"),

          createMoreSpecificFieldRule("DRAGON_BLUE", "BLUE", "org.lgna.story.resources.quadruped.DragonResource"),

          createMoreSpecificFieldRule("DRAGON_GREEN", "GREEN", "org.lgna.story.resources.quadruped.DragonResource"),

          createMoreSpecificFieldRule("ADIRONDACK_CHAIR_LIVING_ADIRONDACK_CUSHION_STRIPES", "ADIRONDACK_BLUE", "org.lgna.story.resources.prop.LoveseatResource"),

          createMoreSpecificFieldRule("ADIRONDACK_CHAIR_LIVING_ADIRONDACK_CUSHION_POLKA", "ADIRONDACK_RED", "org.lgna.story.resources.prop.LoveseatResource"),

          createMoreSpecificFieldRule("ART_NOUVEAU_LOVESEAT_ART_NOUVEAU_FRAME_ANTIQUE", "ART_NOUVEAU_DARK_WOOD", "org.lgna.story.resources.prop.LoveseatResource"),

          createMoreSpecificFieldRule("CAMEL_BACK_LOVESEAT_EXPENSIVE_CAMEL_BACK_WOOD_MAHOGONY", "CAMEL_BACK_MAHOGANY", "org.lgna.story.resources.prop.LoveseatResource"),

          createMoreSpecificFieldRule("CAMEL_BACK_LOVESEAT_EXPENSIVE_CAMEL_BACK_WOOD_RED", "CAMEL_BACK_REDWOOD", "org.lgna.story.resources.prop.LoveseatResource"),

          createMoreSpecificFieldRule("CAMEL_BACK_LOVESEAT_EXPENSIVE_CAMEL_BACK_WOOD_WHITE", "CAMEL_BACK_WHITE_WOOD", "org.lgna.story.resources.prop.LoveseatResource"),

          createMoreSpecificFieldRule("CAMEL_BACK_LOVESEAT_EXPENSIVE_CAMEL_BACK_CUSHION_PINK_VELOUR", "CAMEL_BACK_PINK", "org.lgna.story.resources.prop.LoveseatResource"),

          createMoreSpecificFieldRule("CAMEL_BACK_LOVESEAT_EXPENSIVE_CAMEL_BACK_CUSHION_BLUE_VELOUR", "CAMEL_BACK_BLUE", "org.lgna.story.resources.prop.LoveseatResource"),

          createMoreSpecificFieldRule("CAMEL_BACK_LOVESEAT_EXPENSIVE_CAMEL_BACK_CUSHION_RED_VELOUR", "CAMEL_BACK_RED", "org.lgna.story.resources.prop.LoveseatResource"),

          createMoreSpecificFieldRule("CAMEL_BACK_LOVESEAT_EXPENSIVE_CAMEL_BACK_CUSHION_BLACK_VELOUR", "CAMEL_BACK_BLACK", "org.lgna.story.resources.prop.LoveseatResource"),

          createMoreSpecificFieldRule("MODERN_LOFT_LOVSEATLOFT_MODERN_FABRIC_BEIGE", "MODERN_LOFT_BROWN_CUSHIONS", "org.lgna.story.resources.prop.LoveseatResource"),

          createMoreSpecificFieldRule("MODERN_LOFT_LOVSEATLOFT_MODERN_FABRIC_ORANGE", "MODERN_LOFT_ORANGE_CUSHIONS", "org.lgna.story.resources.prop.LoveseatResource"),

          createMoreSpecificFieldRule("MODERN_LOFT_LOVSEATLOFT_MODERN_FABRIC_WHITE", "MODERN_LOFT_WHITE_CUSHIONS", "org.lgna.story.resources.prop.LoveseatResource"),

          createMoreSpecificFieldRule("MODERN_LOFT_LOVSEATLOFT_MODERN_FABRIC_BLUE", "MODERN_LOFT_BLUE_CUSHIONS", "org.lgna.story.resources.prop.LoveseatResource"),

          createMoreSpecificFieldRule("MODERN_LOFT_LOVSEATLOFT_MODERN_FABRIC_GREEN", "MODERN_LOFT_GREEN_CUSHIONS", "org.lgna.story.resources.prop.LoveseatResource"),

          createMoreSpecificFieldRule("MODERN_LOFT_LOVSEATLOFT_MODERN_CUSHIONS_TAN", "MODERN_LOFT_BROWN", "org.lgna.story.resources.prop.LoveseatResource"),

          createMoreSpecificFieldRule("MODERN_LOFT_LOVSEATLOFT_MODERN_CUSHIONS_GREEN", "MODERN_LOFT_GREEN", "org.lgna.story.resources.prop.LoveseatResource"),

          createMoreSpecificFieldRule("MODERN_LOFT_LOVSEATLOFT_MODERN_CUSHIONS_ORANGE", "MODERN_LOFT_ORANGE", "org.lgna.story.resources.prop.LoveseatResource"),

          createMoreSpecificFieldRule("MODERN_LOFT_LOVSEATLOFT_MODERN_CUSHIONS_RED", "MODERN_LOFT_RED", "org.lgna.story.resources.prop.LoveseatResource"),

          createMoreSpecificFieldRule("MODERN_LOFT_LOVSEATLOFT_MODERN_CUSHIONS_BLUE", "MODERN_LOFT_BLUE", "org.lgna.story.resources.prop.LoveseatResource"),

          createMoreSpecificFieldRule("MOROCCAN_SOFA_MOROCCAN_BEIGECROSS", "MOROCCAN_LIGHT_BLUE", "org.lgna.story.resources.prop.LoveseatResource"),

          createMoreSpecificFieldRule("MOROCCAN_SOFA_MOROCCAN_RED", "MOROCCAN_RED", "org.lgna.story.resources.prop.LoveseatResource"),

          createMoreSpecificFieldRule("PARK_BENCH_LOVESEAT_PARK_BENCH_OAK", "PARK_BENCH_OAK", "org.lgna.story.resources.prop.LoveseatResource"),

          createMoreSpecificFieldRule("PARK_BENCH_LOVESEAT_PARK_BENCH_RED", "PARK_BENCH_RED", "org.lgna.story.resources.prop.LoveseatResource"),

          createMoreSpecificFieldRule("PARK_BENCH_LOVESEAT_PARK_BENCH_IVORY", "PARK_BENCH_WHITE", "org.lgna.story.resources.prop.LoveseatResource"),

          createMoreSpecificFieldRule("QUAINT_SOFA_QUAINT_FABRIC_BLUE_FLOWERS", "QUAINT_BLUE", "org.lgna.story.resources.prop.LoveseatResource"),

          createMoreSpecificFieldRule("QUAINT_SOFA_QUAINT_FABRIC_PINK_FLOWERS", "QUAINT_PINK", "org.lgna.story.resources.prop.LoveseatResource"),

          createMoreSpecificFieldRule("VALUE_LOVESEAT_VALUE_RED_CHECKER", "VALUE_RED_SQUARES", "org.lgna.story.resources.prop.LoveseatResource"),

          createMoreSpecificFieldRule("VALUE_LOVESEAT_VALUE_BLUE_CHECKER", "VALUE_BLUE_SQUARES", "org.lgna.story.resources.prop.LoveseatResource"),

          createMoreSpecificFieldRule("VALUE_LOVESEAT_VALUE_FLOWER", "VALUE_WHITE", "org.lgna.story.resources.prop.LoveseatResource"),

          createMoreSpecificFieldRule("VALUE1_SOFA_VALUE1_REDCHECKER", "VALUE1_RED_SQUARES", "org.lgna.story.resources.prop.SofaResource"),

          createMoreSpecificFieldRule("VALUE1_SOFA_VALUE1_BLUE_CHECKER", "VALUE1_BLUE_SQUARES", "org.lgna.story.resources.prop.SofaResource"),

          createMoreSpecificFieldRule("COLONIAL2_SOFA_COLONIAL2_NEONBLUE", "COLONIAL2_BLUE", "org.lgna.story.resources.prop.SofaResource"),

          createMoreSpecificFieldRule("COLONIAL2_SOFA_COLONIAL2_GREEN_FLORAL", "COLONIAL2_GREEN", "org.lgna.story.resources.prop.SofaResource"),

          createMoreSpecificFieldRule("COLONIAL2_SOFA_COLONIAL2_WHITEDIAMOND", "COLONIAL2_GRAY", "org.lgna.story.resources.prop.SofaResource"),

          createMoreSpecificFieldRule("STEEL_FRAME_SOFA_MODERN_STEEL_FRAME_FABRIC_LEATHER", "STEEL_FRAME_BRWON_LEATHER", "org.lgna.story.resources.prop.SofaResource"),

          createMoreSpecificFieldRule("MOROCCAN_SOFA_MOROCCAN_BEIGECROSS", "MOROCCAN_LIGHT_BLUE", "org.lgna.story.resources.prop.SofaResource"),

          createMoreSpecificFieldRule("QUAINT_SOFA_QUAINT_FABRIC_BEIGE_FLOWERS", "QUAINT_TAN", "org.lgna.story.resources.prop.SofaResource"),

          createMoreSpecificFieldRule("MODERN_CUTOUT_SOFA_UM_CUTOUT_RED", "MODERN_CUTOUT_RED", "org.lgna.story.resources.prop.SofaResource"),

          createMoreSpecificFieldRule("MODERN_DIAMOND_SOFA_U_M_DIAMOND_BABYBLUE", "MODERN_DIAMOND_TURQUOISE", "org.lgna.story.resources.prop.SofaResource"),

          createMoreSpecificFieldRule("DESIGNER_DRESSER_DESIGNER_RED", "DESIGNER_RED", "org.lgna.story.resources.prop.DresserResource"),

          createMoreSpecificFieldRule("JAPANESE_DRESSER_JAPANESE_TANSU_LIGHT", "JAPANESE_LIGHT_WOOD", "org.lgna.story.resources.prop.DresserResource"),

          createMoreSpecificFieldRule("ART_NOVEAU_TABLE_COFFEE_ART_NOUVEAU_TABLE1", "ART_NOVEAU_LIGHT_WOOD", "org.lgna.story.resources.prop.CoffeeTableResource"),

          createMoreSpecificFieldRule("ART_NOVEAU_TABLE_COFFEE_ART_NOUVEAU_TABLE2", "ART_NOVEAU_DARK_WOOD", "org.lgna.story.resources.prop.CoffeeTableResource"),

          createMoreSpecificFieldRule("ART_NOVEAU_TABLE_COFFEE_ART_NOUVEAU_TABLE3", "ART_NOVEAU_WOOD", "org.lgna.story.resources.prop.CoffeeTableResource"),

          createMoreSpecificFieldRule("SMALL_CLUB_TABLE_COFFEE_CLUB1_X1_MATERIALS_LTBLUE", "SMALL_CLUB_BLUE", "org.lgna.story.resources.prop.CoffeeTableResource"),

          createMoreSpecificFieldRule("DESIGNER_TABLE_COFFEE_END_DESIGNER_ASH", "DESIGNER_ASH", "org.lgna.story.resources.prop.CoffeeTableResource"),

          createMoreSpecificFieldRule("LOFT_TABLE_COFFEE_LOFT_RED_METAL", "LOFT_RED", "org.lgna.story.resources.prop.CoffeeTableResource"),

          createMoreSpecificFieldRule("MOROCCAN_TABLE_COFFEE_MOROCCAN_WOODS_CHERRY", "MOROCCAN_REDWOOD", "org.lgna.story.resources.prop.CoffeeTableResource"),

          createMoreSpecificFieldRule("PINE_TABLE_COFFEE_PINE_CEDAR_WOOD", "PINE_CEDAR", "org.lgna.story.resources.prop.CoffeeTableResource"),

          createMoreSpecificFieldRule("PINE_TABLE_COFFEE_PINE_BLONDE_WOOD", "PINE_BLONDE", "org.lgna.story.resources.prop.CoffeeTableResource"),

          createMoreSpecificFieldRule("PINE_TABLE_COFFEE_PINE_HONEY_PINE", "PINE_HONEY", "org.lgna.story.resources.prop.CoffeeTableResource"),

          createMoreSpecificFieldRule("PINE_TABLE_COFFEE_PINE_WALNUT_WOOD", "PINE_WALNUT", "org.lgna.story.resources.prop.CoffeeTableResource"),

          createMoreSpecificFieldRule("PINE_TABLE_COFFEE_PINE_BIRCH_WOOD", "PINE_BIRCH", "org.lgna.story.resources.prop.CoffeeTableResource"),

          createMoreSpecificFieldRule("QUAINT_TABLE_COFFEE_QUAINT_RED", "QUAINT_RED", "org.lgna.story.resources.prop.CoffeeTableResource"),

          createMoreSpecificFieldRule("SPINDLE_TABLE_COFFEE_SPINDLE_WOOD_OLDWOOD", "SPINDLE_OLD", "org.lgna.story.resources.prop.CoffeeTableResource"),

          createMoreSpecificFieldRule("CENTRAL_ASIAN_DESK_CENTRAL_ASIAN_RED", "CENTRAL_ASIAN_RED", "org.lgna.story.resources.prop.DeskResource"),

          createMoreSpecificFieldRule("CENTRAL_ASIAN_DESK_CENTRAL_ASIAN_AVODIRE", "CENTRAL_ASIAN_REDWOOD", "org.lgna.story.resources.prop.DeskResource"),

          createMoreSpecificFieldRule("CLUB_DESK_CLUB_ASH", "CLUB_ASH", "org.lgna.story.resources.prop.DeskResource"),

          createMoreSpecificFieldRule("CLUB_DESK_CLUB_REDWOOD", "CLUB_REDWOOD", "org.lgna.story.resources.prop.DeskResource"),

          createMoreSpecificFieldRule("QUAINT_DESK_QUAINT_RED", "QUAINT_RED", "org.lgna.story.resources.prop.DeskResource"),

          createMoreSpecificFieldRule("VALUE_DESK_VALUE_WOOD_METAL", "VALUE_METAL", "org.lgna.story.resources.prop.DeskResource"),

          createMoreSpecificFieldRule("VALUE_DESK_VALUE_WOODMAPPLE", "VALUE_MAPLE", "org.lgna.story.resources.prop.DeskResource"),

          createMoreSpecificFieldRule("CLUB_TABLE_DINING_CLUB_ROOT", "CLUB_CURLY_DARK_WOOD", "org.lgna.story.resources.prop.DiningTableResource"),

          createMoreSpecificFieldRule("CLUB_TABLE_DINING_CLUB_RED", "CLUB_REDWOOD", "org.lgna.story.resources.prop.DiningTableResource"),

          createMoreSpecificFieldRule("CLUB_TABLE_DINING_CLUB_REDDARK", "CLUB_MAHOGANY", "org.lgna.story.resources.prop.DiningTableResource"),

          createMoreSpecificFieldRule("CLUB_TABLE_DINING_CLUB_WOOD", "CLUB_DARK_WOOD", "org.lgna.story.resources.prop.DiningTableResource"),

          createMoreSpecificFieldRule("CLUB_TABLE_DINING_CLUB_PINE", "CLUB_PINE", "org.lgna.story.resources.prop.DiningTableResource"),

          createMoreSpecificFieldRule("MOROCCAN_TABLE_DINING_MOROCCAN_GREEN", "MOROCCAN_GREEN", "org.lgna.story.resources.prop.DiningTableResource"),

          createMoreSpecificFieldRule("ORIENTAL_TABLE_DINING_ORIENTAL_FISH_BROWN", "ORIENTAL_FISH_BROWN", "org.lgna.story.resources.prop.DiningTableResource"),

          createMoreSpecificFieldRule("ORIENTAL_TABLE_DINING_ORIENTAL_FISH_RED", "ORIENTAL_FISH_RED", "org.lgna.story.resources.prop.DiningTableResource"),

          createMoreSpecificFieldRule("OUTDOOR_TABLE_DINING_OUTDOOR_WOOD_CROSSPINE", "OUTDOOR_PINE", "org.lgna.story.resources.prop.DiningTableResource"),

          createMoreSpecificFieldRule("ART_NOUVEAU_CHAIR_DINING_ART_NOUVEAU_MID_CLEAN", "ART_NOUVEAU_WOOD", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("ART_NOUVEAU_CHAIR_DINING_ART_NOUVEAU_DARK_CLEAN", "ART_NOUVEAU_DARK_WOOD", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("CLUB_CHAIR_DINING_CLUB_OAKCANE", "CLUB_LIGHT_WOOD", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("COLONIAL_CHAIR_DINING_COLONIAL1_PURPLE", "COLONIAL_BLUE", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("COLONIAL_CHAIR_DINING_COLONIAL1_GOLDEN2", "COLONIAL_YELLOW", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("FANCY_COLONIAL_CHAIR_DINING_COLONIAL2_GOLDFLOWER", "FANCY_COLONIAL_GOLD_PATTERN", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("FANCY_COLONIAL_CHAIR_DINING_COLONIAL2_BEIGE", "FANCY_COLONIAL_TAN", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("PARK_LOVESEAT_PARK_BENCH_OAKGREEN", "PARK_GREEN", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("DANISH_MODERN_CHAIR_DINING_DANISH_MODERN_CUSHIONS_POLKA", "DANISH_MODERN_PURPLE", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("DANISH_MODERN_CHAIR_DINING_DANISH_MODERN_CUSHIONS_RED", "DANISH_MODERN_RED", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("LOFT_FORK_CHAIR_DINING_LOFT_SEAT_GREEN", "LOFT_FORK_GREEN", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("LOFT_FORK_CHAIR_DINING_LOFT_SEAT_RED", "LOFT_FORK_RED", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("LOFT_FORK_CHAIR_DINING_LOFT_SEAT_TAN", "LOFT_FORK_WHITE", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("LOFT_FORK_CHAIR_DINING_LOFT_SEAT_ORANGE", "LOFT_FORK_YELLOW", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("LOFT_FORK_CHAIR_DINING_LOFT_FORK_BASE_IRON", "LOFT_FORK_BLACK", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("LOFT_OFFICE_CHAIR_DINING_LOFT_SEAT_GREEN", "LOFT_OFFICE_GREEN", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("LOFT_OFFICE_CHAIR_DINING_LOFT_SEAT_ORANGE", "LOFT_OFFICE_YELLOW", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("LOFT_OFFICE_CHAIR_DINING_LOFT_SEAT_RED", "LOFT_OFFICE_RED", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("LOFT_OFFICE_CHAIR_DINING_LOFT_SEAT_TAN", "LOFT_OFFICE_WHITE", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("LOFT_OFFICE_CHAIR_DINING_LOFT_FORK_BASE_IRON", "LOFT_OFFICE_BLACK", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("MODERATE_CHAIR_DINING_MODERATE_BODY_BLACK", "MODERATE_BLACK", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("MODERATE_CHAIR_DINING_MODERATE_BODY_WOOD", "MODERATE_WOOD", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("MODERATE_CHAIR_DINING_MODERATE_BODY_TEAL", "MODERATE_TEAL_BODY", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("MODERATE_CHAIR_DINING_MODERATE_BODY_RED", "MODERATE_RED_BODY", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("MODERATE_CHAIR_DINING_MODERATE_BODY_BLUE", "MODERATE_BLUE_BODY", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("MODERATE_CHAIR_DINING_MODERATE_SEAT_BUMBLE", "MODERATE_YELLOW_STRIPES", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("MODERATE_CHAIR_DINING_MODERATE_SEAT_STRAWBERRY", "MODERATE_RED", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("MODERATE_CHAIR_DINING_MODERATE_SEAT_YELLOW", "MODERATE_YELLOW", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("ORIENTAL_CHAIR_DINING_ORIENTAL_BLACK", "ORIENTAL_BLACK", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("LOFT_LOFT_BOOKCASE_WOOD_DARK", "LOFT_DARK_WOOD", "org.lgna.story.resources.prop.BookcaseResource"),

          createMoreSpecificFieldRule("LOFT_LOFT_BOOKCASE_WOOD_LIGHT", "LOFT_LIGHT_WOOD", "org.lgna.story.resources.prop.BookcaseResource"),

          createMoreSpecificFieldRule("LOFT_LOFT_BOOKCASE_WOOD_MEDIUM", "LOFT_WOOD", "org.lgna.story.resources.prop.BookcaseResource"),

          createMoreSpecificFieldRule("CHEAP_BOOKCASE_CHEAP_WOOD_PLANK", "CHEAP_PLANK", "org.lgna.story.resources.prop.BookcaseResource"),

          createMoreSpecificFieldRule("CINDER_BLOCK_BOOKCASE_CINDERBLOCK_SHELVES_KNOTTYPINE", "CINDER_BLOCK_PINE", "org.lgna.story.resources.prop.BookcaseResource"),

          createMoreSpecificFieldRule("SWING_ARM_LIGHTING_FLOOR_CLUB_LAMP_LAMP", "SWING_ARM", "org.lgna.story.resources.prop.LampResource"),

          createMoreSpecificFieldRule("DESIGNER_LIGHTING_FLOOR_DESIGNER_SHADE_BLUESHADE", "DESIGNER_BLUE_OFF", "org.lgna.story.resources.prop.LampResource"),

          createMoreSpecificFieldRule("DESIGNER_LIGHTING_FLOOR_DESIGNER_SHADE_BLUESHADEON", "DESIGNER_BLUE_ON", "org.lgna.story.resources.prop.LampResource"),

          createMoreSpecificFieldRule("DESIGNER_LIGHTING_FLOOR_DESIGNER_SHADE_PLAINSHADE", "DESIGNER_WHITE_OFF", "org.lgna.story.resources.prop.LampResource"),

          createMoreSpecificFieldRule("DESIGNER_LIGHTING_FLOOR_DESIGNER_SHADE_PLAINSHADEON", "DESIGNER_WHITE_ON", "org.lgna.story.resources.prop.LampResource"),

          createMoreSpecificFieldRule("DESIGNER_LIGHTING_FLOOR_DESIGNER_SHADE_OLIVESHADE", "DESIGNER_GREEN_OFF", "org.lgna.story.resources.prop.LampResource"),

          createMoreSpecificFieldRule("DESIGNER_LIGHTING_FLOOR_DESIGNER_SHADE_OLIVESHADEON", "DESIGNER_GREEN_ON", "org.lgna.story.resources.prop.LampResource"),

          createMoreSpecificFieldRule("DESIGNER_LIGHTING_FLOOR_DESIGNER_SHADE_ORANGESHADE", "DESIGNER_ORANGE_OFF", "org.lgna.story.resources.prop.LampResource"),

          createMoreSpecificFieldRule("DESIGNER_LIGHTING_FLOOR_DESIGNER_SHADE_REDSHADE", "DESIGNER_RED_OFF", "org.lgna.story.resources.prop.LampResource"),

          createMoreSpecificFieldRule("DESIGNER_LIGHTING_FLOOR_DESIGNER_SHADE_REDSHADEON", "DESIGNER_RED_ON", "org.lgna.story.resources.prop.LampResource"),

          createMoreSpecificFieldRule("GARDEN_BOLLARD_LIGHTING_FLOOR_GARDEN_TIER_LIT", "GARDEN_BOLLARD_BLACK_ON", "org.lgna.story.resources.prop.LampResource"),

          createMoreSpecificFieldRule("GARDEN_BOLLARD_LIGHTING_FLOOR_GARDEN_TIER", "GARDEN_BOLLARD_BLACK_OFF", "org.lgna.story.resources.prop.LampResource"),

          createMoreSpecificFieldRule("GARDEN_BOLLARD_LIGHTING_FLOOR_GARDEN_TIER_GREEN_LIT", "GARDEN_BOLLARD_GREEN_ON", "org.lgna.story.resources.prop.LampResource"),

          createMoreSpecificFieldRule("GARDEN_TIER_LIGHTING_FLOOR_GARDEN_TIER_LIT", "GARDEN_TIER_BLACK_ON", "org.lgna.story.resources.prop.LampResource"),

          createMoreSpecificFieldRule("GARDEN_TIER_LIGHTING_FLOOR_GARDEN_TIER", "GARDEN_TIER_BLACK_OFF", "org.lgna.story.resources.prop.LampResource"),

          createMoreSpecificFieldRule("GARDEN_TIER_LIGHTING_FLOOR_GARDEN_TIER_GREEN_LIT", "GARDEN_TIER_GREEN_ON", "org.lgna.story.resources.prop.LampResource"),

          replace(createMoreSpecificFieldPattern("LOFT_LIGHTING_FLOOR_LOFT_LAMP_SHADE_ORANGE_UNLIT", "org.lgna.story.resources.prop.LampResource"),
              NO_REPLACEMENT),

          createMoreSpecificFieldRule("LOFT_LIGHTING_FLOOR_LOFT_LAMP_SHADE_RED_UNLIT", "LOFT_RED_OFF", "org.lgna.story.resources.prop.LampResource"),

          createMoreSpecificFieldRule("LOFT_LIGHTING_FLOOR_LOFT_LAMP_SHADE_YELLOW_LIT", "LOFT_YELLOW_ON", "org.lgna.story.resources.prop.LampResource"),

          createMoreSpecificFieldRule("LOFT_LIGHTING_FLOOR_LOFT_LAMP_SHADE_BLUE_LIT", "LOFT_BLUE_ON", "org.lgna.story.resources.prop.LampResource"),

          createMoreSpecificFieldRule("LOFT_LIGHTING_FLOOR_LOFT_LAMP_SHADE_RED_LIT", "LOFT_RED_ON", "org.lgna.story.resources.prop.LampResource"),

          createMoreSpecificFieldRule("LOFT_LIGHTING_FLOOR_LOFT_LAMP_SHADE_GREEN_LIT", "LOFT_GREEN_ON", "org.lgna.story.resources.prop.LampResource"),

          createMoreSpecificFieldRule("MOROCCAN_LIGHTING_FLOOR_MOROCCAN_SHADE_YELLOW_UNLIT", "MOROCCAN_YELLOW_OFF", "org.lgna.story.resources.prop.LampResource"),

          createMoreSpecificFieldRule("MOROCCAN_LIGHTING_FLOOR_MOROCCAN_SHADE_BLUES_LIT", "MOROCCAN_BLUE_ON", "org.lgna.story.resources.prop.LampResource"),

          createMoreSpecificFieldRule("MOROCCAN_LIGHTING_FLOOR_MOROCCAN_SHADE_GOLD_BLUE_LIT", "MOROCCAN_GOLD_BLUE_ON", "org.lgna.story.resources.prop.LampResource"),

          createMoreSpecificFieldRule("MOROCCAN_LIGHTING_FLOOR_MOROCCAN_SHADE_RED_LIT", "MOROCCAN_RED_ON", "org.lgna.story.resources.prop.LampResource"),

          createMoreSpecificFieldRule("MOROCCAN_LIGHTING_FLOOR_MOROCCAN_SHADE_YELLOW_LIT", "MOROCCAN_YELLOW_ON", "org.lgna.story.resources.prop.LampResource"),

          createMoreSpecificFieldRule("QUAINT_LIGHTING_FLOOR_QUAINT_SHADE_BEIGE_LIT", "QUAINT_ORANGE_ON", "org.lgna.story.resources.prop.LampResource"),

          createMoreSpecificFieldRule("QUAINT_LIGHTING_FLOOR_QUAINT_SHADE_BEIGE", "QUAINT_ORANGE_OFF", "org.lgna.story.resources.prop.LampResource"),

          createMoreSpecificFieldRule("QUAINT_LIGHTING_FLOOR_QUAINT_SHADE_WHITE", "QUAINT_WHITE_OFF", "org.lgna.story.resources.prop.LampResource"),

          createMoreSpecificFieldRule("QUAINT_LIGHTING_FLOOR_QUAINT_SHADE_BLUE", "QUAINT_BLUE_OFF", "org.lgna.story.resources.prop.LampResource"),

          createMoreSpecificFieldRule("QUAINT_LIGHTING_FLOOR_QUAINT_SHADE_BLUE_LIT", "QUAINT_BLUE_ON", "org.lgna.story.resources.prop.LampResource"),

          createMoreSpecificFieldRule("QUAINT_LIGHTING_FLOOR_QUAINT_SHADE_GREEN_LIT", "QUAINT_GREEN_ON", "org.lgna.story.resources.prop.LampResource"),

          createMoreSpecificFieldRule("QUAINT_LIGHTING_FLOOR_QUAINT_SHADE_WHITE_LIT", "QUAINT_WHITE_ON", "org.lgna.story.resources.prop.LampResource"),

          createMoreSpecificFieldRule("QUAINT_LIGHTING_FLOOR_QUAINT_SHADE_YELLOW_LIT", "QUAINT_YELLOW_ON", "org.lgna.story.resources.prop.LampResource"),

          createMoreSpecificFieldRule("STUDIO_LIGHTING_FLOOR_STUDIO_LIGHTS_LIGHTS_LIT", "STUDIO_ON", "org.lgna.story.resources.prop.LampResource"),

          createMoreSpecificFieldRule("VALUE_LIGHTING_FLOOR_VALUE_PAINTED_METAL_WHITEPAINT", "VALUE_WHITE", "org.lgna.story.resources.prop.LampResource"),

          createMoreSpecificFieldRule("CENTRAL_ASIAN_TABLE_END_CENTRAL_ASIAN_WOOD_BROWN", "CENTRAL_ASIAN_WOOD", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("CENTRAL_ASIAN_TABLE_END_CENTRAL_ASIAN_WOOD_DARK", "CENTRAL_ASIAN_DARK_WOOD", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("CLUB_TABLE_COFFEE_CLUB1_X1_MATERIALS_WHITEOAK", "CLUB_OAK", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("CLUB_TABLE_COFFEE_CLUB1_X1_MATERIALS_LTBLUE", "CLUB_BLUE", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("MOROCCAN_TABLE_END_MOROCCAN_END_TABLE_ALADDIN", "MOROCCAN_YELLOW_INLAY", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("MOROCCAN_TABLE_END_MOROCCAN_END_TABLE_STAR", "MOROCCAN_STARS_INLAY", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("MOROCCAN_TABLE_END_MOROCCAN_END_TABLE_TILE", "MOROCCAN_TILE_INLAY", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("MOROCCAN_TABLE_END_MOROCCAN_END_TABLE_DETAIL", "MOROCCAN_FANCY_INLAY", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("OCTAGONAL_TABLE_END_OCTAGONAL_CHERRY", "OCTAGONAL_CHERRY", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("QUAINT_TABLE_END_QUAINT_FABRIC_BLUE", "QUAINT_BLUE", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("QUAINT_TABLE_END_QUAINT_FABRIC_PINK", "QUAINT_PINK", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("QUAINT_TABLE_END_QUAINT_FABRIC_BEIGE", "QUAINT_YELLOW", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("QUAINT_TABLE_END_QUAINT_FABRIC_WHITE", "QUAINT_WHITE", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("QUAINT_TABLE_END_QUAINT_FABRIC_WHITE_FLOWERS", "QUAINT_FLOWERS", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("TRAINGULAR_TABLE_END_TRIANGULAR_TILE_MARBLE_GREEN", "TRAINGULAR_GREEN_MARBLE", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("TRAINGULAR_TABLE_END_TRIANGULAR_TILE_MARBLE_CREAM", "TRAINGULAR_CREAM_MARBLE", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("TRAINGULAR_TABLE_END_TRIANGULAR_TILE_MARBLE_RED", "TRAINGULAR_RED_MARBLE", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("TRAINGULAR_TABLE_END_TRIANGULAR_TILE_MARBLE_WHITE", "TRAINGULAR_WHITE_MARBLE", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("TRAINGULAR_TABLE_END_TRIANGULAR_TILE_MARBLE_BLACK", "TRAINGULAR_BLACK_MARBLE", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("TRAINGULAR_TABLE_END_TRIANGULAR_TILE_WOOD_SANTA_MARIA", "TRAINGULAR_DARK_WOOD", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("TRAINGULAR_TABLE_END_TRIANGULAR_TILE_WOOD_BLACKWOOD", "TRAINGULAR_BLACK_WOOD", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("TRAINGULAR_TABLE_END_TRIANGULAR_TILE_WOOD_CHERRY", "TRAINGULAR_CHERRY", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("TRAINGULAR_TABLE_END_TRIANGULAR_TILE_WOOD_WHITE", "TRAINGULAR_WHITE_WOOD", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("TRAINGULAR_TABLE_END_TRIANGULAR_TILE_WOOD_RED_OAK", "TRAINGULAR_OAK", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("UM_TABLE_END_UM_BLUE", "UM_BLUE", "org.lgna.story.resources.prop.EndTableResource"),

          replace("name=\"org.lgna.story.resources.prop.WeddingCakeResource",
              "name=\"org.lgna.story.resources.prop.CakeResource")
      ),
    };
  }

  // @formatter:on

  private TextMigrationRegistryV3159() {
  }
}
