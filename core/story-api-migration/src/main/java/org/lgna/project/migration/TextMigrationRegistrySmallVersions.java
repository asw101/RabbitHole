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

// @formatter:off
@Deprecated // Text migrations now load from migrations/text-migrations.json; retained for JSON regeneration and documentation.
final class TextMigrationRegistrySmallVersions {

  static TextMigration[] createEarly() {
    return new TextMigration[] {
      new TextMigration(
          new Version("3.1.8.0.0")),

      new TextMigration(
          new Version("3.1.9.0.0"),

          replace("ARMOIRE_CLOTHING",
              NO_REPLACEMENT),

          replace("org.lgna.story.resources.armoire.ArmoireArtNouveau",
              NO_REPLACEMENT),

          replace("PINK_POODLE",
              NO_REPLACEMENT),

          replace("org.lgna.story.resources.quadruped.Poodle",
              NO_REPLACEMENT)),

      new TextMigration(
          new Version("3.1.11.0.0")),

      new TextMigration(
          new Version("3.1.14.0.0"),

          replace("CAMEL",
              NO_REPLACEMENT),

          replace("org.lgna.story.resources.quadruped.Camel",
              NO_REPLACEMENT),

          replace("FALCON",
              NO_REPLACEMENT),

          replace("org.lgna.story.resources.flyer.Falcon",
              NO_REPLACEMENT),

          replace("LION",
              NO_REPLACEMENT),

          replace("org.lgna.story.resources.quadruped.Lion",
              NO_REPLACEMENT),

          replace("WOLF",
              NO_REPLACEMENT),

          replace("org.lgna.story.resources.quadruped.Wolf",
              NO_REPLACEMENT)),

      new TextMigration(
          new Version("3.1.15.1.0")),

      new TextMigration(
          new Version("3.1.20.0.0"),

          replace("org.lgna.story.resources.dresser.DresserCentralAsian",
              "org.lgna.story.resources.prop.DresserCentralAsian"),

          replace("org.lgna.story.resources.dresser.DresserColonial",
              "org.lgna.story.resources.prop.DresserColonial"),

          replace("org.lgna.story.resources.dresser.DresserDesigner",
              "org.lgna.story.resources.prop.DresserDesigner")),

      new TextMigration(
          new Version("3.1.33.0.0")),
    };
  }

  static TextMigration[] createMid() {
    return new TextMigration[] {
      new TextMigration(
          new Version("3.1.35.0.0"),

          replace("org.lgna.story.resources.biped.Alien",
              "org.lgna.story.resources.biped.AlienResource"),

          replace("org.lgna.story.resources.biped.BabyYeti",
              "org.lgna.story.resources.biped.BabyYetiResource"),

          replace("org.lgna.story.resources.biped.BigBadWolf",
              "org.lgna.story.resources.biped.BigBadWolfResource"),

          replace("org.lgna.story.resources.biped.Bunny",
              "org.lgna.story.resources.biped.BunnyResource"),

          replace("org.lgna.story.resources.biped.CheshireCat",
              "org.lgna.story.resources.biped.CheshireCatResource"),

          replace("org.lgna.story.resources.biped.Hare",
              "org.lgna.story.resources.biped.HareResource"),

          replace("org.lgna.story.resources.biped.MadHatter",
              "org.lgna.story.resources.biped.MadHatterResource"),

          replace("org.lgna.story.resources.biped.MarchHare",
              "org.lgna.story.resources.biped.MarchHareResource"),

          replace("org.lgna.story.resources.biped.Ogre",
              "org.lgna.story.resources.biped.OgreResource"),

          replace("org.lgna.story.resources.biped.Pig",
              "org.lgna.story.resources.biped.PigResource"),

          replace("org.lgna.story.resources.biped.Pixie",
              "org.lgna.story.resources.biped.PixieResource"),

          replace("org.lgna.story.resources.biped.PlayingCard",
              "org.lgna.story.resources.biped.PlayingCardResource"),

          replace("org.lgna.story.resources.biped.QueenOfHearts",
              "org.lgna.story.resources.biped.QueenOfHeartsResource"),

          replace("org.lgna.story.resources.biped.StuffedTiger",
              "org.lgna.story.resources.biped.StuffedTigerResource"),

          replace("org.lgna.story.resources.biped.Tortoise",
              "org.lgna.story.resources.biped.TortoiseResource"),

          replace("org.lgna.story.resources.biped.Troll",
              "org.lgna.story.resources.biped.TrollResource"),

          replace("org.lgna.story.resources.biped.WhiteRabbit",
              "org.lgna.story.resources.biped.WhiteRabbitResource"),

          replace("org.lgna.story.resources.biped.Witch",
              "org.lgna.story.resources.biped.WitchResource"),

          replace("org.lgna.story.resources.biped.Yeti",
              "org.lgna.story.resources.biped.YetiResource"),

          replace("org.lgna.story.resources.flyer.Chicken",
              "org.lgna.story.resources.flyer.ChickenResource"),

          replace("org.lgna.story.resources.flyer.Falcon",
              "org.lgna.story.resources.flyer.FalconResource"),

          replace("org.lgna.story.resources.flyer.Flamingo",
              "org.lgna.story.resources.flyer.FlamingoResource"),

          replace("org.lgna.story.resources.flyer.Owl",
              "org.lgna.story.resources.flyer.OwlResource"),

          replace("org.lgna.story.resources.flyer.Penguin",
              "org.lgna.story.resources.flyer.PenguinResource"),

          replace("org.lgna.story.resources.flyer.Seagull",
              "org.lgna.story.resources.flyer.SeagullResource"),

          replace("org.lgna.story.resources.flyer.Toucan",
              "org.lgna.story.resources.flyer.ToucanResource"),

          replace("org.lgna.story.resources.prop.Armoire",
              "org.lgna.story.resources.prop.ArmoireResource"),

          replace("org.lgna.story.resources.prop.Bookcase",
              "org.lgna.story.resources.prop.BookcaseResource"),

          replace("org.lgna.story.resources.prop.Boulder",
              "org.lgna.story.resources.prop.BoulderResource"),

          replace("org.lgna.story.resources.prop.BowlingPin",
              "org.lgna.story.resources.prop.BowlingPinResource"),

          replace("org.lgna.story.resources.prop.Cake",
              "org.lgna.story.resources.prop.CakeResource"),

          replace("org.lgna.story.resources.prop.CandyFactory",
              "org.lgna.story.resources.prop.CandyFactoryResource"),

          replace("org.lgna.story.resources.prop.CastleGate",
              "org.lgna.story.resources.prop.CastleGateResource"),

          replace("org.lgna.story.resources.prop.CastleTowerBase",
              "org.lgna.story.resources.prop.CastleTowerBaseResource"),

          replace("org.lgna.story.resources.prop.CastleTowerMiddle",
              "org.lgna.story.resources.prop.CastleTowerMiddleResource"),

          replace("org.lgna.story.resources.prop.CastleTowerTop",
              "org.lgna.story.resources.prop.CastleTowerTopResource"),

          replace("org.lgna.story.resources.prop.CastleWall",
              "org.lgna.story.resources.prop.CastleWallResource"),

          replace("declaringClass name=\"org.lgna.story.resources.prop.Cauldron\"",
              "declaringClass name=\"org.lgna.story.resources.prop.CauldronResource\""),

          replace("declaringClass name=\"org.lgna.story.resources.prop.CauldronLid\"",
              "declaringClass name=\"org.lgna.story.resources.prop.CauldronLidResource\""),

          replace("org.lgna.story.resources.prop.Cave",
              "org.lgna.story.resources.prop.CaveResource"),

          replace("org.lgna.story.resources.prop.Chair",
              "org.lgna.story.resources.prop.ChairResource"),

          replace("org.lgna.story.resources.prop.CoffeeTable",
              "org.lgna.story.resources.prop.CoffeeTableResource"),

          replace("org.lgna.story.resources.prop.ColaBottle",
              "org.lgna.story.resources.prop.ColaBottleResource"),

          replace("org.lgna.story.resources.prop.CoralShelf",
              "org.lgna.story.resources.prop.CoralShelfResource"),

          replace("org.lgna.story.resources.prop.Desk",
              "org.lgna.story.resources.prop.DeskResource"),

          replace("org.lgna.story.resources.prop.DiningTable",
              "org.lgna.story.resources.prop.DiningTableResource"),

          replace("org.lgna.story.resources.prop.Dresser",
              "org.lgna.story.resources.prop.DresserResource"),

          replace("org.lgna.story.resources.prop.EndTable",
              "org.lgna.story.resources.prop.EndTableResource"),

          replace("org.lgna.story.resources.prop.Grill",
              "org.lgna.story.resources.prop.GrillResource"),

          replace("org.lgna.story.resources.prop.Hedge",
              "org.lgna.story.resources.prop.HedgeResource"),

          replace("org.lgna.story.resources.prop.Helicopter",
              "org.lgna.story.resources.prop.HelicopterResource"),

          replace("org.lgna.story.resources.prop.Iceberg",
              "org.lgna.story.resources.prop.IcebergResource"),

          replace("org.lgna.story.resources.prop.IceFlow",
              "org.lgna.story.resources.prop.IceFlowResource"),

          replace("org.lgna.story.resources.prop.IceMountain",
              "org.lgna.story.resources.prop.IceMountainResource"),

          replace("org.lgna.story.resources.prop.Lamp",
              "org.lgna.story.resources.prop.LampResource"),

          replace("org.lgna.story.resources.prop.Loveseat",
              "org.lgna.story.resources.prop.LoveseatResource"),

          replace("org.lgna.story.resources.prop.MagicSpoon",
              "org.lgna.story.resources.prop.MagicSpoonResource"),

          replace("org.lgna.story.resources.prop.MagicStaff",
              "org.lgna.story.resources.prop.MagicStaffResource"),

          replace("org.lgna.story.resources.prop.MagicStone",
              "org.lgna.story.resources.prop.MagicStoneResource"),

          replace("org.lgna.story.resources.prop.MagicWand",
              "org.lgna.story.resources.prop.MagicWandResource"),

          replace("org.lgna.story.resources.prop.PirateShip",
              "org.lgna.story.resources.prop.PirateShipResource"),

          replace("org.lgna.story.resources.prop.Plateau",
              "org.lgna.story.resources.prop.PlateauResource"),

          replace("org.lgna.story.resources.prop.PocketWatch",
              "org.lgna.story.resources.prop.PocketWatchResource"),

          replace("org.lgna.story.resources.prop.Potion",
              "org.lgna.story.resources.prop.PotionResource"),

          replace("org.lgna.story.resources.prop.PrayerFlags",
              "org.lgna.story.resources.prop.PrayerFlagsResource"),

          replace("org.lgna.story.resources.prop.RedRover",
              "org.lgna.story.resources.prop.RedRoverResource"),

          replace("org.lgna.story.resources.prop.Rose",
              "org.lgna.story.resources.prop.RoseResource"),

          replace("org.lgna.story.resources.prop.Saucer",
              "org.lgna.story.resources.prop.SaucerResource"),

          replace("org.lgna.story.resources.prop.SeaPlant",
              "org.lgna.story.resources.prop.SeaPlantResource"),

          replace("org.lgna.story.resources.prop.Seaweed",
              "org.lgna.story.resources.prop.SeaweedResource"),

          replace("org.lgna.story.resources.prop.ShortMushroom",
              "org.lgna.story.resources.prop.ShortMushroomResource"),

          replace("org.lgna.story.resources.prop.Sled",
              "org.lgna.story.resources.prop.SledResource"),

          replace("org.lgna.story.resources.prop.Snowboard",
              "org.lgna.story.resources.prop.SnowboardResource"),

          replace("org.lgna.story.resources.prop.Sofa",
              "org.lgna.story.resources.prop.SofaResource"),

          replace("org.lgna.story.resources.prop.SpellBook",
              "org.lgna.story.resources.prop.SpellBookResource"),

          replace("org.lgna.story.resources.prop.StoneBridge",
              "org.lgna.story.resources.prop.StoneBridgeResource"),

          replace("org.lgna.story.resources.prop.Stove",
              "org.lgna.story.resources.prop.StoveResource"),

          replace("org.lgna.story.resources.prop.Submarine",
              "org.lgna.story.resources.prop.SubmarineResource"),

          replace("org.lgna.story.resources.prop.Suitcase",
              "org.lgna.story.resources.prop.SuitcaseResource"),

          replace("org.lgna.story.resources.prop.TallMushroom",
              "org.lgna.story.resources.prop.TallMushroomResource"),

          replace("org.lgna.story.resources.prop.Teacup",
              "org.lgna.story.resources.prop.TeacupResource"),

          replace("org.lgna.story.resources.prop.Teapot",
              "org.lgna.story.resources.prop.TeapotResource"),

          replace("org.lgna.story.resources.prop.TeaTable",
              "org.lgna.story.resources.prop.TeaTableResource"),

          replace("org.lgna.story.resources.prop.TeaTray",
              "org.lgna.story.resources.prop.TeaTrayResource"),

          replace("org.lgna.story.resources.prop.Tent",
              "org.lgna.story.resources.prop.TentResource"),

          replace("org.lgna.story.resources.prop.Trashcan",
              "org.lgna.story.resources.prop.TrashcanResource"),

          replace("org.lgna.story.resources.prop.TreasureChest",
              "org.lgna.story.resources.prop.TreasureChestResource"),

          replace("org.lgna.story.resources.prop.UFO",
              "org.lgna.story.resources.prop.UFOResource"),

          replace("org.lgna.story.resources.prop.Volleyball",
              "org.lgna.story.resources.prop.VolleyballResource"),

          replace("org.lgna.story.resources.prop.WeddingCake",
              "org.lgna.story.resources.prop.WeddingCakeResource"),

          replace("org.lgna.story.resources.prop.WonderlandTree",
              "org.lgna.story.resources.prop.WonderlandTreeResource"),

          replace("org.lgna.story.resources.quadruped.AbyssinianCat",
              "org.lgna.story.resources.quadruped.AbyssinianCatResource"),

          replace("org.lgna.story.resources.quadruped.AlienRobot",
              "org.lgna.story.resources.quadruped.AlienRobotResource"),

          replace("org.lgna.story.resources.quadruped.BabyDragon",
              "org.lgna.story.resources.quadruped.BabyDragonResource"),

          replace("org.lgna.story.resources.quadruped.BillyGoat",
              "org.lgna.story.resources.quadruped.BillyGoatResource"),

          replace("org.lgna.story.resources.quadruped.Camel",
              "org.lgna.story.resources.quadruped.CamelResource"),

          replace("org.lgna.story.resources.quadruped.Cow",
              "org.lgna.story.resources.quadruped.CowResource"),

          replace("org.lgna.story.resources.quadruped.Dalmatian",
              "org.lgna.story.resources.quadruped.DalmatianResource"),

          replace("org.lgna.story.resources.quadruped.Dragon",
              "org.lgna.story.resources.quadruped.DragonResource"),

          replace("org.lgna.story.resources.quadruped.Lioness",
              "org.lgna.story.resources.quadruped.LionessResource"),

          replace("org.lgna.story.resources.quadruped.ManxCat",
              "org.lgna.story.resources.quadruped.ManxCatResource"),

          replace("org.lgna.story.resources.quadruped.Poodle",
              "org.lgna.story.resources.quadruped.PoodleResource"),

          replace("org.lgna.story.resources.quadruped.ScottyDog",
              "org.lgna.story.resources.quadruped.ScottyDogResource"),

          replace("org.lgna.story.resources.quadruped.ShortHairCat",
              "org.lgna.story.resources.quadruped.ShortHairCatResource"),

          replace("org.lgna.story.resources.quadruped.Wolf",
              "org.lgna.story.resources.quadruped.WolfResource"),

          replace("org.lgna.story.resources.fish.BlueTang",
              "org.lgna.story.resources.fish.BlueTangResource"),

          replace("org.lgna.story.resources.fish.ClownFish",
              "org.lgna.story.resources.fish.ClownFishResource"),

          replace("org.lgna.story.resources.fish.PajamaFish",
              "org.lgna.story.resources.fish.PajamaFishResource"),

          replace("org.lgna.story.resources.fish.Shark",
              "org.lgna.story.resources.fish.SharkResource"),

          replace("org.lgna.story.resources.marinemammal.BabyWalrus",
              "org.lgna.story.resources.marinemammal.BabyWalrusResource"),

          replace("org.lgna.story.resources.marinemammal.Dolphin",
              "org.lgna.story.resources.marinemammal.DolphinResource"),

          replace("org.lgna.story.resources.marinemammal.Orca",
              "org.lgna.story.resources.marinemammal.OrcaResource"),

          replace("org.lgna.story.resources.marinemammal.Walrus",
              "org.lgna.story.resources.marinemammal.WalrusResource")
      ),
      new TextMigration(
          new Version("3.1.38.0.0")
      ),
      new TextMigration(
          new Version("3.1.39.0.0"),

          replace("org.lgna.story.event.MouseClickOnScreenListener\"/><type name=\"\\[Lorg.lgna.story.AddMouseButtonListener",
              "org.lgna.story.event.MouseClickOnScreenListener\"/><type name=\"\\[Lorg.lgna.story.AddMouseClickOnScreenListener"),

          replace("org.lgna.story.event.MouseClickOnObjectListener\"/><type name=\"\\[Lorg.lgna.story.AddMouseButtonListener",
              "org.lgna.story.event.MouseClickOnObjectListener\"/><type name=\"\\[Lorg.lgna.story.AddMouseClickOnObjectListener")
      ),
      new TextMigration(
          new Version("3.1.48.0.0"),

          replace("BILLY_GOAT",
              "BIG_HORNS")
      ),
      new TextMigration(
          new Version("3.1.58.0.0"),

          replace("name=\"ICE_FLOW",
              "name=\"ICE_FLOE"),

          replace("name=\"org.lgna.story.resources.prop.IceFlowResource",
              "name=\"org.lgna.story.resources.prop.IceFloeResource"),

          replace("name=\"PIXIE_GREEN",
              "name=\"GREEN"),

          replace("name=\"PIXIE_PINK",
              "name=\"PINK"),

          replace("name=\"PIXIE_BLUE",
              "name=\"BLUE")
      ),
    };
  }

  // @formatter:on

  private TextMigrationRegistrySmallVersions() {
  }
}
