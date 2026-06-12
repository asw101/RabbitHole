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
import static org.lgna.project.migration.ProjectMigrationTextMigrationFactory.createVersion3_2_110TextMigration;
import static org.lgna.project.migration.ProjectMigrationTextSnippets.createJointIdRule;
import static org.lgna.project.migration.ProjectMigrationTextSnippets.createMoreSpecificFieldReplacement;
import static org.lgna.project.migration.ProjectMigrationTextSnippets.createMoreSpecificFieldPattern;
import static org.lgna.project.migration.ProjectMigrationTextSnippets.createMoreSpecificFieldRule;

// @formatter:off
@Deprecated // Text migrations now load from migrations/text-migrations.json; retained for JSON regeneration and documentation.
final class TextMigrationRegistryLateVersions {

  static TextMigration[] create() {
    return new TextMigration[] {
      new TextMigration(
          new Version("3.1.68.0.0"),

          createMoreSpecificFieldRule("STRAIGHT1_RIVERBANK2", "STRAIGHT1", "org.lgna.story.resources.prop.RiverPieceResource"),

          createMoreSpecificFieldRule("STRAIGHT1_BROWN", "STRAIGHT1", "org.lgna.story.resources.prop.RiverPieceResource"),

          createMoreSpecificFieldRule("STRAIGHT2_BLUE", "STRAIGHT2", "org.lgna.story.resources.prop.RiverPieceResource"),

          createMoreSpecificFieldRule("STRAIGHT2_BROWN", "STRAIGHT2", "org.lgna.story.resources.prop.RiverPieceResource"),

          createMoreSpecificFieldRule("STRAIGHT3_BLUE", "STRAIGHT3", "org.lgna.story.resources.prop.RiverPieceResource"),

          createMoreSpecificFieldRule("STRAIGHT3_BROWN", "STRAIGHT3", "org.lgna.story.resources.prop.RiverPieceResource"),

          createMoreSpecificFieldRule("STRAIGHT4_BLUE", "STRAIGHT4", "org.lgna.story.resources.prop.RiverPieceResource"),

          createMoreSpecificFieldRule("STRAIGHT4_BROWN", "STRAIGHT4", "org.lgna.story.resources.prop.RiverPieceResource"),

          createMoreSpecificFieldRule("CURVE1_BLUE", "CURVE1", "org.lgna.story.resources.prop.RiverPieceResource"),

          createMoreSpecificFieldRule("CURVE1_BROWN", "CURVE1", "org.lgna.story.resources.prop.RiverPieceResource"),

          createMoreSpecificFieldRule("CURVE2_BLUE", "CURVE2", "org.lgna.story.resources.prop.RiverPieceResource"),

          createMoreSpecificFieldRule("CURVE2_BROWN", "CURVE2", "org.lgna.story.resources.prop.RiverPieceResource"),

          createMoreSpecificFieldRule("CURVE3_BLUE", "CURVE3", "org.lgna.story.resources.prop.RiverPieceResource"),

          createMoreSpecificFieldRule("CURVE3_BROWN", "CURVE3", "org.lgna.story.resources.prop.RiverPieceResource"),

          createMoreSpecificFieldRule("CURVE4_BLUE", "CURVE4", "org.lgna.story.resources.prop.RiverPieceResource"),

          createMoreSpecificFieldRule("CURVE4_BROWN", "CURVE4", "org.lgna.story.resources.prop.RiverPieceResource"),

          createMoreSpecificFieldRule("BOW1_RIVERBANK3", "BOW1", "org.lgna.story.resources.prop.RiverPieceResource"),

          createMoreSpecificFieldRule("BOW1_BROWN", "BOW1", "org.lgna.story.resources.prop.RiverPieceResource"),

          createMoreSpecificFieldRule("BOW2_BLUE", "BOW2", "org.lgna.story.resources.prop.RiverPieceResource"),

          createMoreSpecificFieldRule("BOW2_BROWN", "BOW2", "org.lgna.story.resources.prop.RiverPieceResource"),

          createMoreSpecificFieldRule("BOW3_BLUE", "BOW3", "org.lgna.story.resources.prop.RiverPieceResource"),

          createMoreSpecificFieldRule("BOW3_BROWN", "BOW3", "org.lgna.story.resources.prop.RiverPieceResource"),

          replace(createMoreSpecificFieldPattern("PINK", "org.lgna.story.resources.quadruped.BabyDragonResource"),
              createMoreSpecificFieldReplacement("DEFAULT_PINK", "org.lgna.story.resources.quadruped.DragonBabyResource")),

          replace(createMoreSpecificFieldPattern("AQUA", "org.lgna.story.resources.quadruped.BabyDragonResource"),
              createMoreSpecificFieldReplacement("DEFAULT_AQUA", "org.lgna.story.resources.quadruped.DragonBabyResource")),

          replace(createMoreSpecificFieldPattern("BLUE", "org.lgna.story.resources.quadruped.BabyDragonResource"),
              createMoreSpecificFieldReplacement("DEFAULT_BLUE", "org.lgna.story.resources.quadruped.DragonBabyResource")),

          replace(createMoreSpecificFieldPattern("GREEN", "org.lgna.story.resources.quadruped.BabyDragonResource"),
              createMoreSpecificFieldReplacement("DEFAULT_GREEN", "org.lgna.story.resources.quadruped.DragonBabyResource")),

          replace(createMoreSpecificFieldPattern("RED", "org.lgna.story.resources.quadruped.BabyDragonResource"),
              createMoreSpecificFieldReplacement("DEFAULT_RED", "org.lgna.story.resources.quadruped.DragonBabyResource")),

          replace("name=\"org.lgna.story.resources.quadruped.BabyDragonResource",
              "name=\"org.lgna.story.resources.quadruped.DragonBabyResource"),

          replace(createMoreSpecificFieldPattern("WITH_SCARF", "org.lgna.story.resources.biped.BabyYetiResource"),
              createMoreSpecificFieldReplacement("WITH_SCARF", "org.lgna.story.resources.biped.YetiBabyResource")),

          replace(createMoreSpecificFieldPattern("DEFAULT", "org.lgna.story.resources.biped.BabyYetiResource"),
              createMoreSpecificFieldReplacement("DEFAULT", "org.lgna.story.resources.biped.YetiBabyResource")),

          replace("name=\"org.lgna.story.resources.biped.BabyYetiResource",
              "name=\"org.lgna.story.resources.biped.YetiBabyResource"),

          replace(createMoreSpecificFieldPattern("DEFAULT", "org.lgna.story.resources.marinemammal.BabyWalrusResource"),
              createMoreSpecificFieldReplacement("DEFAULT", "org.lgna.story.resources.marinemammal.WalrusBabyResource")),

          replace("name=\"org.lgna.story.resources.marinemammal.BabyWalrusResource",
              "name=\"org.lgna.story.resources.marinemammal.WalrusBabyResource"),

          createMoreSpecificFieldRule("ROSE_RED", "RED", "org.lgna.story.resources.prop.RoseResource"),

          createMoreSpecificFieldRule("ROSE_WHITE", "WHITE", "org.lgna.story.resources.prop.RoseResource"),

          createMoreSpecificFieldRule("DEFAULT", "ICE_FLOE1", "org.lgna.story.resources.prop.IceFloeResource"),

          createMoreSpecificFieldRule("DEFAULT", "DESERT", "org.lgna.story.resources.prop.CanyonSpiresResource"),

          createMoreSpecificFieldRule("CANYON_SPIRES", "DESERT", "org.lgna.story.resources.prop.CanyonSpiresResource"),

          createMoreSpecificFieldRule("DEFAULT", "DEFAULT_DESERT", "org.lgna.story.resources.prop.CliffWallResource"),

          createMoreSpecificFieldRule("CLIFF_WALL", "DEFAULT_DESERT", "org.lgna.story.resources.prop.CliffWallResource"),

          createMoreSpecificFieldRule("PURPLE", "DEFAULT_PURPLE", "org.lgna.story.resources.quadruped.DragonResource"),

          createMoreSpecificFieldRule("GREEN", "DEFAULT_GREEN", "org.lgna.story.resources.quadruped.DragonResource"),

          createMoreSpecificFieldRule("BLUE", "DEFAULT_BLUE", "org.lgna.story.resources.quadruped.DragonResource"),

          createMoreSpecificFieldRule("ORANGE", "DEFAULT_ORANGE", "org.lgna.story.resources.quadruped.DragonResource"),

          createMoreSpecificFieldRule("RED", "DEFAULT_RED", "org.lgna.story.resources.quadruped.DragonResource"),

          createMoreSpecificFieldRule("ART_NOUVEAU_DARK_WOOD", "ART_NOUVEAU_DARK_WOOD_GREEN", "org.lgna.story.resources.prop.LoveseatResource"),

          createMoreSpecificFieldRule("ART_NOUVEAU_MAHOGANY", "ART_NOUVEAU_OAK_GREEN", "org.lgna.story.resources.prop.LoveseatResource"),

          createMoreSpecificFieldRule("ART_NOUVEAU_OAK", "ART_NOUVEAU_OAK_GREEN", "org.lgna.story.resources.prop.LoveseatResource"),

          createMoreSpecificFieldRule("CAMEL_BACK_WHITE", "CAMEL_BACK_WHITE_MAHOGANY", "org.lgna.story.resources.prop.LoveseatResource"),

          createMoreSpecificFieldRule("CAMEL_BACK_BLACK", "CAMEL_BACK_BLACK_MAHOGANY", "org.lgna.story.resources.prop.LoveseatResource"),

          createMoreSpecificFieldRule("CAMEL_BACK_BLUE", "CAMEL_BACK_BLUE_MAHOGANY", "org.lgna.story.resources.prop.LoveseatResource"),

          createMoreSpecificFieldRule("CAMEL_BACK_PINK", "CAMEL_BACK_PINK_MAHOGANY", "org.lgna.story.resources.prop.LoveseatResource"),

          createMoreSpecificFieldRule("CAMEL_BACK_RED", "CAMEL_BACK_RED_MAHOGANY", "org.lgna.story.resources.prop.LoveseatResource"),

          createMoreSpecificFieldRule("CAMEL_BACK_LIGHT_WOOD", "CAMEL_BACK_WHITE_MAHOGANY", "org.lgna.story.resources.prop.LoveseatResource"),

          createMoreSpecificFieldRule("CAMEL_BACK_MAHOGANY", "CAMEL_BACK_BLACK_MAHOGANY", "org.lgna.story.resources.prop.LoveseatResource"),

          createMoreSpecificFieldRule("CAMEL_BACK_REDWOOD", "CAMEL_BACK_RED_MAHOGANY", "org.lgna.story.resources.prop.LoveseatResource"),

          createMoreSpecificFieldRule("CAMEL_BACK_WHITE_WOOD", "CAMEL_BACK_WHITE_MAHOGANY", "org.lgna.story.resources.prop.LoveseatResource"),

          createMoreSpecificFieldRule("MODERN_LOFT_BLUE", "MODERN_LOFT_BLUE_BLUE_CUSHIONS", "org.lgna.story.resources.prop.LoveseatResource"),

          createMoreSpecificFieldRule("MODERN_LOFT_GREEN", "MODERN_LOFT_GREEN_GREEN_CUSHIONS", "org.lgna.story.resources.prop.LoveseatResource"),

          createMoreSpecificFieldRule("MODERN_LOFT_ORANGE", "MODERN_LOFT_ORANGE_GREEN_CUSHIONS", "org.lgna.story.resources.prop.LoveseatResource"),

          createMoreSpecificFieldRule("MODERN_LOFT_RED", "MODERN_LOFT_ORANGE_GREEN_CUSHIONS", "org.lgna.story.resources.prop.LoveseatResource"),

          createMoreSpecificFieldRule("MODERN_LOFT_BROWN", "MODERN_LOFT_ORANGE_GREEN_CUSHIONS", "org.lgna.story.resources.prop.LoveseatResource"),

          createMoreSpecificFieldRule("MODERN_LOFT_BROWN_CUSHIONS", "MODERN_LOFT_BLUE_GREEN_CUSHIONS", "org.lgna.story.resources.prop.LoveseatResource"),

          createMoreSpecificFieldRule("MODERN_LOFT_BLUE_CUSHIONS", "MODERN_LOFT_BLUE_BLUE_CUSHIONS", "org.lgna.story.resources.prop.LoveseatResource"),

          createMoreSpecificFieldRule("MODERN_LOFT_GREEN_CUSHIONS", "MODERN_LOFT_GREEN_GREEN_CUSHIONS", "org.lgna.story.resources.prop.LoveseatResource"),

          createMoreSpecificFieldRule("MODERN_LOFT_ORANGE_CUSHIONS", "MODERN_LOFT_ORANGE_ORANGE_CUSHIONS", "org.lgna.story.resources.prop.LoveseatResource"),

          createMoreSpecificFieldRule("MODERN_LOFT_WHITE_CUSHIONS", "MODERN_LOFT_BLUE_BLUE_CUSHIONS", "org.lgna.story.resources.prop.LoveseatResource"),

          createMoreSpecificFieldRule("QUAINT_WHITE", "QUAINT_TAN", "org.lgna.story.resources.prop.SofaResource"),

          createMoreSpecificFieldRule("CENTRAL_ASIAN_FANCY_BLONDE", "CENTRAL_ASIAN_FANCY_BLONDE_BLONDE", "org.lgna.story.resources.prop.CoffeeTableResource"),

          createMoreSpecificFieldRule("CENTRAL_ASIAN_FANCY_CHERRY", "CENTRAL_ASIAN_FANCY_CHERRY_CHERRY", "org.lgna.story.resources.prop.CoffeeTableResource"),

          createMoreSpecificFieldRule("CENTRAL_ASIAN_FANCY_DARK_WOOD", "CENTRAL_ASIAN_FANCY_DARK_WOOD_DARK_WOOD", "org.lgna.story.resources.prop.CoffeeTableResource"),

          createMoreSpecificFieldRule("CENTRAL_ASIAN_FANCY_REDWOOD", "CENTRAL_ASIAN_FANCY_REDWOOD_REDWOOD", "org.lgna.story.resources.prop.CoffeeTableResource"),

          createMoreSpecificFieldRule("CENTRAL_ASIAN_BLONDE", "CENTRAL_ASIAN_FANCY_BLONDE_BLONDE", "org.lgna.story.resources.prop.CoffeeTableResource"),

          createMoreSpecificFieldRule("CENTRAL_ASIAN_CHERRY", "CENTRAL_ASIAN_FANCY_CHERRY_CHERRY", "org.lgna.story.resources.prop.CoffeeTableResource"),

          createMoreSpecificFieldRule("CENTRAL_ASIAN_DARK_WOOD", "CENTRAL_ASIAN_FANCY_DARK_WOOD_DARK_WOOD", "org.lgna.story.resources.prop.CoffeeTableResource"),

          createMoreSpecificFieldRule("CENTRAL_ASIAN_REDWOOD", "CENTRAL_ASIAN_FANCY_REDWOOD_REDWOOD", "org.lgna.story.resources.prop.CoffeeTableResource"),

          createMoreSpecificFieldRule("MOROCCAN_YELLOW_INLAY", "MOROCCAN_YELLOW_INLAY_BLACK", "org.lgna.story.resources.prop.CoffeeTableResource"),

          createMoreSpecificFieldRule("MOROCCAN_FANCY_INLAY", "MOROCCAN_FANCY_INLAY_BLACK", "org.lgna.story.resources.prop.CoffeeTableResource"),

          createMoreSpecificFieldRule("MOROCCAN_STARS_INLAY", "MOROCCAN_STARS_INLAY_BLACK", "org.lgna.story.resources.prop.CoffeeTableResource"),

          createMoreSpecificFieldRule("MOROCCAN_TILE_INLAY", "MOROCCAN_TILE_INLAY_BLACK", "org.lgna.story.resources.prop.CoffeeTableResource"),

          createMoreSpecificFieldRule("MOROCCAN_BLACK", "MOROCCAN_TILE_INLAY_BLACK", "org.lgna.story.resources.prop.CoffeeTableResource"),

          createMoreSpecificFieldRule("MOROCCAN_REDWOOD", "MOROCCAN_TILE_INLAY_REDWOOD", "org.lgna.story.resources.prop.CoffeeTableResource"),

          createMoreSpecificFieldRule("MOROCCAN_MAHOGANY", "MOROCCAN_TILE_INLAY_MAHOGANY", "org.lgna.story.resources.prop.CoffeeTableResource"),

          createMoreSpecificFieldRule("MOROCCAN_YELLOW", "MOROCCAN_FANCY_INLAY_YELLOW", "org.lgna.story.resources.prop.CoffeeTableResource"),

          replace(createMoreSpecificFieldPattern("ORIENTAL_DRAGON_BROWN", "org.lgna.story.resources.prop.DiningTableResource"),
              createMoreSpecificFieldReplacement("ORIENTAL_DRAGON_BROWN", "org.lgna.story.resources.prop.EndTableResource")),

          replace(createMoreSpecificFieldPattern("ORIENTAL_DRAGON_RED", "org.lgna.story.resources.prop.DiningTableResource"),
              createMoreSpecificFieldReplacement("ORIENTAL_DRAGON_RED", "org.lgna.story.resources.prop.EndTableResource")),

          replace(createMoreSpecificFieldPattern("ORIENTAL_FISH_BROWN", "org.lgna.story.resources.prop.DiningTableResource"),
              createMoreSpecificFieldReplacement("ORIENTAL_FISH_BROWN", "org.lgna.story.resources.prop.EndTableResource")),

          replace(createMoreSpecificFieldPattern("ORIENTAL_FISH_RED", "org.lgna.story.resources.prop.DiningTableResource"),
              createMoreSpecificFieldReplacement("ORIENTAL_FISH_RED", "org.lgna.story.resources.prop.EndTableResource")),

          replace(createMoreSpecificFieldPattern("ORIENTAL_LOTUS_BLACK", "org.lgna.story.resources.prop.DiningTableResource"),
              createMoreSpecificFieldReplacement("ORIENTAL_LOTUS_BLACK", "org.lgna.story.resources.prop.EndTableResource")),

          replace(createMoreSpecificFieldPattern("ORIENTAL_LOTUS_ORANGE", "org.lgna.story.resources.prop.DiningTableResource"),
              createMoreSpecificFieldReplacement("ORIENTAL_LOTUS_ORANGE", "org.lgna.story.resources.prop.EndTableResource")),

          createMoreSpecificFieldRule("LOFT_BLACK_TRIM", "LOFT_OAK_BLACK_TRIM", "org.lgna.story.resources.prop.ArmoireResource"),

          createMoreSpecificFieldRule("LOFT_DARK_HONEY_TRIM", "LOFT_OAK_DARK_HONEY_TRIM", "org.lgna.story.resources.prop.ArmoireResource"),

          createMoreSpecificFieldRule("LOFT_SWIRLY_BROWN_TRIM", "LOFT_DARK_WOOD_DARK_HONEY_TRIM", "org.lgna.story.resources.prop.ArmoireResource"),

          createMoreSpecificFieldRule("LOFT_DARK_RED_TRIM", "LOFT_RED_FINISH_DARK_HONEY_TRIM", "org.lgna.story.resources.prop.ArmoireResource"),

          createMoreSpecificFieldRule("LOFT_MEDIUM_BROWN_TRIM", "LOFT_DARK_WOOD_DARK_HONEY_TRIM", "org.lgna.story.resources.prop.ArmoireResource"),

          createMoreSpecificFieldRule("LOFT_RED_FINISH", "LOFT_RED_FINISH_BLACK_TRIM", "org.lgna.story.resources.prop.ArmoireResource"),

          createMoreSpecificFieldRule("LOFT_DARK_WOOD", "LOFT_DARK_WOOD_BLACK_TRIM", "org.lgna.story.resources.prop.ArmoireResource"),

          createMoreSpecificFieldRule("LOFT_OAK", "LOFT_OAK_DARK_HONEY_TRIM", "org.lgna.story.resources.prop.ArmoireResource"),

          createMoreSpecificFieldRule("LOFT_HONEY", "LOFT_OAK_DARK_HONEY_TRIM", "org.lgna.story.resources.prop.ArmoireResource"),

          createMoreSpecificFieldRule("LOFT_MAPLE", "LOFT_DARK_WOOD_BLACK_TRIM", "org.lgna.story.resources.prop.ArmoireResource"),

          createMoreSpecificFieldRule("GREEN", "GREEN_LIT", "org.lgna.story.resources.prop.GrillResource"),

          createMoreSpecificFieldRule("BLACK", "BLACK_LIT", "org.lgna.story.resources.prop.GrillResource"),

          createMoreSpecificFieldRule("YELLOW", "YELLOW_LIT", "org.lgna.story.resources.prop.GrillResource"),

          createMoreSpecificFieldRule("RED", "RED_LIT", "org.lgna.story.resources.prop.GrillResource"),

          createMoreSpecificFieldRule("BLUE", "BLUE_LIT", "org.lgna.story.resources.prop.GrillResource"),

          createMoreSpecificFieldRule("HELICOPTER", "MILITARY", "org.lgna.story.resources.prop.HelicopterResource"),

          createMoreSpecificFieldRule("CANDY_FACTORY", "LIGHT_OFF", "org.lgna.story.resources.prop.CandyFactoryResource"),

          createMoreSpecificFieldRule("LOFT_FORK_BLACK", "LOFT_OFFICE_WHITE_BLACK", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("LOFT_FORK_LIGHT_WOOD", "LOFT_FORK_RED_LIGHT_WOOD", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("LOFT_FORK_ORANGE_WOOD", "LOFT_FORK_RED_LIGHT_WOOD", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("LOFT_FORK_RED_WOOD", "LOFT_FORK_RED_LIGHT_WOOD", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("LOFT_FORK_BLUE", "LOFT_FORK_BLUE_BLACK", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("LOFT_FORK_GREEN", "LOFT_FORK_GREEN_BLACK", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("LOFT_FORK_YELLOW", "LOFT_FORK_YELLOW_BLACK", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("LOFT_FORK_RED", "LOFT_FORK_RED_BLACK", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("LOFT_FORK_WHITE", "LOFT_FORK_WHITE_BLACK", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("LOFT_OFFICE_BLACK", "LOFT_OFFICE_WHITE_BLACK", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("LOFT_OFFICE_LIGHT_WOOD", "LOFT_OFFICE_WHITE_LIGHT_WOOD", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("LOFT_OFFICE_ORANGE_WOOD", "LOFT_OFFICE_YELLOW_LIGHT_WOOD", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("LOFT_OFFICE_RED_WOOD", "LOFT_FORK_RED_LIGHT_WOOD", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("LOFT_OFFICE_BLUE", "LOFT_OFFICE_BLUE_BLACK", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("LOFT_OFFICE_GREEN", "LOFT_OFFICE_GREEN_BLACK", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("LOFT_OFFICE_YELLOW", "LOFT_OFFICE_YELLOW_BLACK", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("LOFT_OFFICE_RED", "LOFT_OFFICE_RED_BLACK", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("LOFT_OFFICE_WHITE", "LOFT_OFFICE_WHITE_BLACK", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("MODERATE_BLACK", "MODERATE_BLUE_BLACK_BODY", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("MODERATE_BLUE_BODY", "MODERATE_BLUE_WOOD_BODY", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("MODERATE_RED_BODY", "MODERATE_RED_WOOD_BODY", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("MODERATE_TEAL_BODY", "MODERATE_TEAL_WOOD_BODY", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("MODERATE_WOOD", "MODERATE_BLUE_WOOD_BODY", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("MODERATE_BLUE", "MODERATE_BLUE_WOOD_BODY", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("MODERATE_YELLOW_STRIPES", "MODERATE_YELLOW_WOOD_BODY", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("MODERATE_GRAY", "MODERATE_GRAY_WOOD_BODY", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("MODERATE_RED", "MODERATE_RED_WOOD_BODY", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("MODERATE_TEAL", "MODERATE_TEAL_WOOD_BODY", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("MODERATE_YELLOW", "MODERATE_YELLOW_WOOD_BODY", "org.lgna.story.resources.prop.ChairResource"),

          createMoreSpecificFieldRule("CENTRAL_ASIAN_TOP_BLONDE", "CENTRAL_ASIAN_WOOD_TOP_BLONDE", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("CENTRAL_ASIAN_TOP_CHERRY", "CENTRAL_ASIAN_WOOD_TOP_CHERRY", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("CENTRAL_ASIAN_TOP_RED_LAQUER", "CENTRAL_ASIAN_DARK_WOOD_TOP_RED_LAQUER", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("CENTRAL_ASIAN_TOP_ROUGH", "CENTRAL_ASIAN_ROUGH_TOP_ROUGH", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("CENTRAL_ASIAN_BLONDE", "CENTRAL_ASIAN_WOOD_TOP_BLONDE", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("CENTRAL_ASIAN_WOOD", "CENTRAL_ASIAN_DARK_WOOD_TOP_BLONDE", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("CENTRAL_ASIAN_CHERRY", "CENTRAL_ASIAN_WOOD_TOP_CHERRY", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("CENTRAL_ASIAN_DARK_WOOD", "CENTRAL_ASIAN_DARK_WOOD_TOP_BLONDE", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("CENTRAL_ASIAN_RED_LAQUER", "CENTRAL_ASIAN_DARK_WOOD_TOP_RED_LAQUER", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("CENTRAL_ASIAN_ROUGH", "CENTRAL_ASIAN_ROUGH_TOP_ROUGH", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("QUAINT_YELLOW", "QUAINT_YELLOW_WHITE_WOOD", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("QUAINT_BLUE", "QUAINT_BLUE_BLUE_WOOD", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("QUAINT_GREEN", "QUAINT_GREEN_GREEN_WOOD", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("QUAINT_PINK", "QUAINT_PINK_RED_WOOD", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("QUAINT_WHITE", "QUAINT_WHITE_WHITE_WOOD", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("QUAINT_FLOWERS", "QUAINT_FLOWERS_WHITE_WOOD", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("TRAINGULAR_BLACK_MARBLE", "TRIANGULAR_BLACK_MARBLE_OAK", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("TRAINGULAR_CREAM_MARBLE", "TRIANGULAR_GREEN_MARBLE_OAK", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("TRAINGULAR_GREEN_MARBLE", "TRIANGULAR_GREEN_MARBLE_OAK", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("TRAINGULAR_RED_MARBLE", "TRIANGULAR_GREEN_MARBLE_OAK", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("TRAINGULAR_WHITE_MARBLE", "TRIANGULAR_WHITE_MARBLE_OAK", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("TRAINGULAR_BLACK_WOOD", "TRIANGULAR_BLACK_MARBLE_BLACK_WOOD", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("TRAINGULAR_CHERRY", "TRIANGULAR_GREEN_MARBLE_WHITE_WOOD", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("TRAINGULAR_OAK", "TRIANGULAR_GREEN_MARBLE_OAK", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("TRAINGULAR_DARK_WOOD", "TRIANGULAR_BLACK_MARBLE_BLACK_WOOD", "org.lgna.story.resources.prop.EndTableResource"),

          createMoreSpecificFieldRule("TRAINGULAR_WHITE_WOOD", "TRIANGULAR_WHITE_MARBLE_WHITE_WOOD", "org.lgna.story.resources.prop.EndTableResource"),

          //sims person changes
          createMoreSpecificFieldRule("BLACK", "BLACK_BLACK_HAT", "org.lgna.story.resources.sims2.MaleAdultHairTopHat"),
          createMoreSpecificFieldRule("BLOND", "BLOND_BLACK_HAT", "org.lgna.story.resources.sims2.MaleAdultHairTopHat"),
          createMoreSpecificFieldRule("BROWN", "BROWN_BLACK_HAT", "org.lgna.story.resources.sims2.MaleAdultHairTopHat"),
          createMoreSpecificFieldRule("GREY", "GREY_BLACK_HAT", "org.lgna.story.resources.sims2.MaleAdultHairTopHat"),
          createMoreSpecificFieldRule("RED", "RED_BLACK_HAT", "org.lgna.story.resources.sims2.MaleAdultHairTopHat"),

          createMoreSpecificFieldRule("BLACK", "BLACK_BLACK_HAT", "org.lgna.story.resources.sims2.MaleAdultHairHatFedoraCasual"),
          createMoreSpecificFieldRule("BLOND", "BLOND_BLACK_HAT", "org.lgna.story.resources.sims2.MaleAdultHairHatFedoraCasual"),
          createMoreSpecificFieldRule("BROWN", "BROWN_BLACK_HAT", "org.lgna.story.resources.sims2.MaleAdultHairHatFedoraCasual"),
          createMoreSpecificFieldRule("GREY", "GREY_BLACK_HAT", "org.lgna.story.resources.sims2.MaleAdultHairHatFedoraCasual"),
          createMoreSpecificFieldRule("RED", "RED_BLACK_HAT", "org.lgna.story.resources.sims2.MaleAdultHairHatFedoraCasual"),

          //map both KINKY and STRAIGHT to replacement
          createMoreSpecificFieldRule("KINKY_BLACK", "BLACK_BLACK_HAT", "org.lgna.story.resources.sims2.MaleAdultHairHatFedora"),
          createMoreSpecificFieldRule("KINKY_BLOND", "BLOND_BLACK_HAT", "org.lgna.story.resources.sims2.MaleAdultHairHatFedora"),
          createMoreSpecificFieldRule("KINKY_BROWN", "BROWN_BLACK_HAT", "org.lgna.story.resources.sims2.MaleAdultHairHatFedora"),
          createMoreSpecificFieldRule("KINKY_GREY", "GREY_BLACK_HAT", "org.lgna.story.resources.sims2.MaleAdultHairHatFedora"),
          createMoreSpecificFieldRule("KINKY_RED", "RED_BLACK_HAT", "org.lgna.story.resources.sims2.MaleAdultHairHatFedora"),

          createMoreSpecificFieldRule("STRAIGHT_BLACK", "BLACK_BLACK_HAT", "org.lgna.story.resources.sims2.MaleAdultHairHatFedora"),
          createMoreSpecificFieldRule("STRAIGHT_BLOND", "BLOND_BLACK_HAT", "org.lgna.story.resources.sims2.MaleAdultHairHatFedora"),
          createMoreSpecificFieldRule("STRAIGHT_BROWN", "BROWN_BLACK_HAT", "org.lgna.story.resources.sims2.MaleAdultHairHatFedora"),
          createMoreSpecificFieldRule("STRAIGHT_GREY", "GREY_BLACK_HAT", "org.lgna.story.resources.sims2.MaleAdultHairHatFedora"),
          createMoreSpecificFieldRule("STRAIGHT_RED", "RED_BLACK_HAT", "org.lgna.story.resources.sims2.MaleAdultHairHatFedora"),

          createMoreSpecificFieldRule("STRAIGHT_BLACK", "BLACK_BLACK_HAT", "org.lgna.story.resources.sims2.MaleAdultHairHatFedora"),
          createMoreSpecificFieldRule("STRAIGHT_BLOND", "BLOND_BLACK_HAT", "org.lgna.story.resources.sims2.MaleAdultHairHatFedora"),
          createMoreSpecificFieldRule("STRAIGHT_BROWN", "BROWN_BLACK_HAT", "org.lgna.story.resources.sims2.MaleAdultHairHatFedora"),
          createMoreSpecificFieldRule("STRAIGHT_GREY", "GREY_BLACK_HAT", "org.lgna.story.resources.sims2.MaleAdultHairHatFedora"),
          createMoreSpecificFieldRule("STRAIGHT_RED", "RED_BLACK_HAT", "org.lgna.story.resources.sims2.MaleAdultHairHatFedora"),

          createMoreSpecificFieldRule("STUBBLE_BLACK", "BLACK", "org.lgna.story.resources.sims2.MaleAdultHairBald"),
          createMoreSpecificFieldRule("STUBBLE_BLOND", "BLOND", "org.lgna.story.resources.sims2.MaleAdultHairBald"),
          createMoreSpecificFieldRule("STUBBLE_BROWN", "BROWN", "org.lgna.story.resources.sims2.MaleAdultHairBald"),
          createMoreSpecificFieldRule("STUBBLE_GREY", "GREY", "org.lgna.story.resources.sims2.MaleAdultHairBald"),
          createMoreSpecificFieldRule("STUBBLE_RED", "RED", "org.lgna.story.resources.sims2.MaleAdultHairBald"),

          createMoreSpecificFieldRule("BLUEPINSTRIPE", "FORMAL_BLUE_PINSTRIPE", "org.lgna.story.resources.sims2.MaleAdultFullBodyOutfitOpenCoatLongPants"),
          createMoreSpecificFieldRule("BROWNTWEED", "FORMAL_BROWN_TWEED", "org.lgna.story.resources.sims2.MaleAdultFullBodyOutfitOpenCoatLongPants"),
          createMoreSpecificFieldRule("GREYPINSTRIPE", "FORMAL_GREY_PINSTRIPE", "org.lgna.story.resources.sims2.MaleAdultFullBodyOutfitOpenCoatLongPants"),
          createMoreSpecificFieldRule("GREYTWEED", "FORMAL_GREY_TWEED", "org.lgna.story.resources.sims2.MaleAdultFullBodyOutfitOpenCoatLongPants"),

          //map removed swimwear to existing swimwear
          createMoreSpecificFieldRule("BLUEBIKINI", "BLUE_WAVE_TANKINI", "org.lgna.story.resources.sims2.FemaleAdultFullBodyOutfitSwimwear"),
          createMoreSpecificFieldRule("BLUESINGLE", "BLUE_DIVING", "org.lgna.story.resources.sims2.FemaleAdultFullBodyOutfitSwimwear"),
          createMoreSpecificFieldRule("GRAYSINGLE", "BLUE_DIVING", "org.lgna.story.resources.sims2.FemaleAdultFullBodyOutfitSwimwear"),
          createMoreSpecificFieldRule("GREENSINGLE", "BLUE_DIVING", "org.lgna.story.resources.sims2.FemaleAdultFullBodyOutfitSwimwear"),
          createMoreSpecificFieldRule("LIMEBIKINI", "LIME_WAVE_TANKINI", "org.lgna.story.resources.sims2.FemaleAdultFullBodyOutfitSwimwear"),
          createMoreSpecificFieldRule("PINKBIKINI", "PINK_WAVE_TANKINI", "org.lgna.story.resources.sims2.FemaleAdultFullBodyOutfitSwimwear"),
          createMoreSpecificFieldRule("PINKSINGLE", "PINK_DIVING", "org.lgna.story.resources.sims2.FemaleAdultFullBodyOutfitSwimwear"),
          createMoreSpecificFieldRule("REDBIKINI", "RED_WAVE_TANKINI", "org.lgna.story.resources.sims2.FemaleAdultFullBodyOutfitSwimwear"),
          createMoreSpecificFieldRule("REDSINGLE", "RED_DIVING", "org.lgna.story.resources.sims2.FemaleAdultFullBodyOutfitSwimwear"),
          createMoreSpecificFieldRule("YELLOWBIKINI", "YELLOW_WAVE_TANKINI", "org.lgna.story.resources.sims2.FemaleAdultFullBodyOutfitSwimwear"),
          createMoreSpecificFieldRule("BLUEPAISLEY", "BLUE_FLOWER", "org.lgna.story.resources.sims2.FemaleAdultFullBodyOutfitSwimwear"),
          createMoreSpecificFieldRule("CLASSICBIKINIBLACK", "BLACK_RAINBOW_TANKINI", "org.lgna.story.resources.sims2.FemaleAdultFullBodyOutfitSwimwear"),
          createMoreSpecificFieldRule("SPORTBIKINIBLUE", "BLUE_WAVE_TANKINI", "org.lgna.story.resources.sims2.FemaleAdultFullBodyOutfitSwimwear"),
          createMoreSpecificFieldRule("SPORTBIKINIVIOLET", "BLUE_WAVE_TANKINI", "org.lgna.story.resources.sims2.FemaleAdultFullBodyOutfitSwimwear"),
          createMoreSpecificFieldRule("STRINGBIKINIBROWN", "BLACK_RAINBOW_TANKINI", "org.lgna.story.resources.sims2.FemaleAdultFullBodyOutfitSwimwear"),
          createMoreSpecificFieldRule("STRINGBIKINIBURGANDY", "BLACK_RAINBOW_TANKINI", "org.lgna.story.resources.sims2.FemaleAdultFullBodyOutfitSwimwear"),

          //map removed dresses to existing dresses
          createMoreSpecificFieldRule("REDHOLE", "RED", "org.lgna.story.resources.sims2.FemaleAdultFullBodyOutfitDressLongTwo"),
          createMoreSpecificFieldRule("BLACKHOLE", "BLACK", "org.lgna.story.resources.sims2.FemaleAdultFullBodyOutfitDressLongTwo"),
          createMoreSpecificFieldRule("LEOPARD", "CREAM", "org.lgna.story.resources.sims2.FemaleAdultFullBodyOutfitDressLongTwo"),

          //Name change
          createMoreSpecificFieldRule("BLUE", "BLUE_PINSTRIPE", "org.lgna.story.resources.sims2.FemaleAdultFullBodyOutfitPowerSuit"),

          //Name change...Where did SOCIALWORKER come from?
          createMoreSpecificFieldRule("SOCIALWORKER", "BLACK", "org.lgna.story.resources.sims2.FemaleAdultFullBodyOutfitSuit"),

          //map removed swimwear to existing swimwear
          createMoreSpecificFieldRule("TANKINIGREEN", "TROPIC_SEA_SWIM", "org.lgna.story.resources.sims2.ChildFullBodyOutfitNaked"),
          createMoreSpecificFieldRule("TANKINIPINK", "TROPIC_BERRY_SWIM", "org.lgna.story.resources.sims2.ChildFullBodyOutfitNaked"),
          createMoreSpecificFieldRule("TANKINISTRIPES", "TROPIC_FIRE_SWIM", "org.lgna.story.resources.sims2.ChildFullBodyOutfitNaked"),
          createMoreSpecificFieldRule("WHITEUNDER", "WHITE_CAMISOLE", "org.lgna.story.resources.sims2.ChildFullBodyOutfitNaked"),

          //Name change
          createMoreSpecificFieldRule("FRIED", "BLACK", "org.lgna.story.resources.sims2.ChildHairShocked"),

          //other fields handled by underscore migration
          createMoreSpecificFieldRule("GREENPANTSFLOWERS", "GREEN_PANTS_SUNFLOWER", "org.lgna.story.resources.sims2.FemaleChildFullBodyOutfitTShirtPants"),

          //mail delivery changes class name as well as constant
          replace(createMoreSpecificFieldPattern("STANDARDBLUE", "org.lgna.story.resources.sims2.FemaleAdultFullBodyOutfitMailDelivery"),
              createMoreSpecificFieldReplacement("BLUE", "org.lgna.story.resources.sims2.FemaleAdultFullBodyOutfitDeliveryPerson")),
          replace(createMoreSpecificFieldPattern("STANDARDBLUE", "org.lgna.story.resources.sims2.MaleAdultFullBodyOutfitMailDelivery"),
              createMoreSpecificFieldReplacement("BLUE", "org.lgna.story.resources.sims2.MaleAdultFullBodyOutfitDeliveryPerson"))

      ),

      new TextMigration(
          new Version("3.1.69.0.0"),

          createMoreSpecificFieldRule("DEFAULT", "DEFAULT_UNDERWATER", "org.lgna.story.resources.prop.CaveResource"),

          createMoreSpecificFieldRule("DESERT", "DEFAULT_DESERT", "org.lgna.story.resources.prop.CliffWallResource"),

          createMoreSpecificFieldRule("MARS", "DEFAULT_MARS", "org.lgna.story.resources.prop.CliffWallResource")
      ),

      new TextMigration(
          new Version("3.1.70.0.0"),

          createMoreSpecificFieldRule("STRAIGHT1", "STRAIGHT1_BLUE", "org.lgna.story.resources.prop.RiverPieceResource"),

          createMoreSpecificFieldRule("STRAIGHT2", "STRAIGHT2_BLUE", "org.lgna.story.resources.prop.RiverPieceResource"),

          createMoreSpecificFieldRule("STRAIGHT3", "STRAIGHT3_BLUE", "org.lgna.story.resources.prop.RiverPieceResource"),

          createMoreSpecificFieldRule("STRAIGHT4", "STRAIGHT4_BLUE", "org.lgna.story.resources.prop.RiverPieceResource"),

          createMoreSpecificFieldRule("CURVE1", "CURVE1_BLUE", "org.lgna.story.resources.prop.RiverPieceResource"),

          createMoreSpecificFieldRule("CURVE2", "CURVE2_BLUE", "org.lgna.story.resources.prop.RiverPieceResource"),

          createMoreSpecificFieldRule("CURVE3", "CURVE3_BLUE", "org.lgna.story.resources.prop.RiverPieceResource"),

          createMoreSpecificFieldRule("CURVE4", "CURVE4_BLUE", "org.lgna.story.resources.prop.RiverPieceResource"),

          createMoreSpecificFieldRule("BOW1", "BOW1_BLUE", "org.lgna.story.resources.prop.RiverPieceResource"),

          createMoreSpecificFieldRule("BOW2", "BOW2_BLUE", "org.lgna.story.resources.prop.RiverPieceResource"),

          createMoreSpecificFieldRule("BOW3", "BOW3_BLUE", "org.lgna.story.resources.prop.RiverPieceResource"),

          createMoreSpecificFieldRule("SHARP_BEND", "SHARP_BEND_BLUE", "org.lgna.story.resources.prop.RiverPieceResource"),

          //note: possible duplicate
          createMoreSpecificFieldRule("BOW1_RIVERBANK3", "BOW1_BLUE", "org.lgna.story.resources.prop.RiverPieceResource"),

          createMoreSpecificFieldRule("STRAIGHT1_RIVERBANK2", "STRAIGHT1_BLUE", "org.lgna.story.resources.prop.RiverPieceResource"),
          //

          replace(createMoreSpecificFieldPattern("WOODEN_BOAT", "org.lgna.story.resources.aircraft.WoodenBoatResource"),
              createMoreSpecificFieldReplacement("WOODEN_BOAT", "org.lgna.story.resources.watercraft.WoodenBoatResource")),

          replace("name=\"org.lgna.story.resources.aircraft.WoodenBoatResource",
              "name=\"org.lgna.story.resources.watercraft.WoodenBoatResource"),

          replace(createMoreSpecificFieldPattern("SAILBOAT", "org.lgna.story.resources.aircraft.SailboatResource"),
              createMoreSpecificFieldReplacement("SAILBOAT", "org.lgna.story.resources.watercraft.SailboatResource")),

          replace("name=\"org.lgna.story.resources.aircraft.SailboatResource",
              "name=\"org.lgna.story.resources.watercraft.SailboatResource")
      ),

      new TextMigration(
          new Version("3.1.85.0.0"),

          //<note: moved from 68 - 69 migration when a world with the UFO discovered with old resource>
          replace("name=\"org.lgna.story.resources.prop.HelicopterResource",
              "name=\"org.lgna.story.resources.prop.HelicopterPropResource"),

          replace(createMoreSpecificFieldPattern("UFO", "org.lgna.story.resources.prop.UFOResource"),
              createMoreSpecificFieldReplacement("U_F_O_PROP", "org.lgna.story.resources.prop.UFOPropResource")),

          //added for older projects
          replace("name=\"org.lgna.story.resources.prop.UFOResource",
              "name=\"org.lgna.story.resources.prop.UFOPropResource"),
          //

          replace("name=\"org.lgna.story.resources.prop.PirateShipResource",
              "name=\"org.lgna.story.resources.prop.PirateShipPropResource"),

          replace("name=\"org.lgna.story.resources.prop.FishingBoatResource",
              "name=\"org.lgna.story.resources.prop.FishingBoatPropResource"),

          replace("name=\"org.lgna.story.resources.prop.SubmarineResource",
              "name=\"org.lgna.story.resources.prop.SubmarinePropResource"),

          //</note>

          replace("org.lgna.story.event.ComesIntoViewEvent",
              "org.lgna.story.event.EnterViewEvent"),

          replace("org.lgna.story.event.LeavesViewEvent",
              "org.lgna.story.event.ExitViewEvent"),

          replace("getForegroundMovable",
              "getForegroundModel"),

          replace("getBackgroundMovable",
              "getBackgroundModel"),

          replace("edu.cmu.cs.dennisc.matt.EndOcclusionEvent",
              "org.lgna.story.event.EndOcclusionEvent")

      ),

      new TextMigration(
          new Version("3.1.92.0.0")
      ),

      new TextMigration(
          new Version("3.1.93.0.0"),

          createMoreSpecificFieldRule("WALNUT_DOOR_WALNUT_WALNUT", "BIOTECH_STATION", "org.lgna.story.resources.prop.BiotechStationResource"),

          createMoreSpecificFieldRule("WALNUT_DOOR_WALNUT_LIGHT_WOOD", "BIOTECH_STATION", "org.lgna.story.resources.prop.BiotechStationResource"),

          createMoreSpecificFieldRule("WALNUT_DOOR_WALNUT_ORANGE", "BIOTECH_STATION", "org.lgna.story.resources.prop.BiotechStationResource"),

          createMoreSpecificFieldRule("WALNUT_DOOR_WALNUT_BLUE", "BIOTECH_STATION", "org.lgna.story.resources.prop.BiotechStationResource"),

          createMoreSpecificFieldRule("WALNUT_DOOR_WALNUT_PINK", "BIOTECH_STATION", "org.lgna.story.resources.prop.BiotechStationResource"),

          createMoreSpecificFieldRule("BASIC", "BIOTECH_STATION", "org.lgna.story.resources.prop.BiotechStationResource"),

          createMoreSpecificFieldRule("FANCY", "BIOTECH_STATION", "org.lgna.story.resources.prop.BiotechStationResource"),

          //The textbook version needs to not remove these models, so for this branch leave this commented out.
          //          createMoreSpecificFieldPattern("DEFAULT", "org.lgna.story.resources.biped.BlackCatResource"),
          //          createMoreSpecificFieldReplacement("DEFAULT", "org.lgna.story.resources.biped.PandaResource"),
          //
          //          "name=\"org.lgna.story.resources.biped.BlackCatResource",
          //          "name=\"org.lgna.story.resources.biped.AliceResource",
          //
          //          createMoreSpecificFieldPattern("DEFAULT", "org.lgna.story.resources.biped.PumpkinHeadResource"),
          //          createMoreSpecificFieldReplacement("DEFAULT", "org.lgna.story.resources.biped.PandaResource"),
          //
          //          createMoreSpecificFieldPattern("HEADLESS", "org.lgna.story.resources.biped.PumpkinHeadResource"),
          //          createMoreSpecificFieldReplacement("DEFAULT", "org.lgna.story.resources.biped.PandaResource"),
          //
          //          "name=\"org.lgna.story.resources.biped.PumpkinHeadResource",
          //          "name=\"org.lgna.story.resources.biped.PandaResource",
          //

          //note: this is a doomed migration
          ////"name=\"org.lgna.story.resources.prop.TrainEngineResource",
          ////"name=\"org.lgna.story.resources.train.TrainEngineResource",
          //
          //          createMoreSpecificFieldPattern("DEFAULT", "org.lgna.story.resources.biped.GhostResource"),
          //          createMoreSpecificFieldReplacement("DEFAULT", "org.lgna.story.resources.biped.ThorResource"),
          //
          //          createMoreSpecificFieldPattern("SHEET_GHOST", "org.lgna.story.resources.biped.GhostResource"),
          //          createMoreSpecificFieldReplacement("DEFAULT", "org.lgna.story.resources.biped.ThorResource"),
          //
          //          createMoreSpecificFieldPattern("SHEET_GHOST_SHEET_TRANSPARENT", "org.lgna.story.resources.biped.GhostResource"),
          //          createMoreSpecificFieldReplacement("DEFAULT", "org.lgna.story.resources.biped.ThorResource"),
          //
          //          "name=\"org.lgna.story.resources.biped.GhostResource",
          //          "name=\"org.lgna.story.resources.biped.ThorResource",
          //

          //note: this is a doomed migration
          ////"name=\"org.lgna.story.resources.prop.TrainCarResource",
          ////"name=\"org.lgna.story.resources.train.TrainCarResource",

          //
          //          createMoreSpecificFieldPattern("DEFAULT", "org.lgna.story.resources.prop.TunnelResource"),
          //          createMoreSpecificFieldReplacement("DEFAULT", "org.lgna.story.resources.prop.TentResource"),
          //
          //          "name=\"org.lgna.story.resources.prop.TunnelResource",
          //          "name=\"org.lgna.story.resources.prop.TentResource",
          //
          //          createMoreSpecificFieldPattern("WITH_HAT", "org.lgna.story.resources.biped.SkeletonResource"),
          //          createMoreSpecificFieldReplacement("DEFAULT", "org.lgna.story.resources.biped.YetiResource"),
          //
          //          createMoreSpecificFieldPattern("DEFAULT_SKELETON", "org.lgna.story.resources.biped.SkeletonResource"),
          //          createMoreSpecificFieldReplacement("DEFAULT", "org.lgna.story.resources.biped.YetiResource"),
          //
          //          createMoreSpecificFieldPattern("DEFAULT_TOP_HAT", "org.lgna.story.resources.biped.SkeletonResource"),
          //          createMoreSpecificFieldReplacement("DEFAULT", "org.lgna.story.resources.biped.YetiResource"),
          //
          //          "name=\"org.lgna.story.resources.biped.SkeletonResource",
          //          "name=\"org.lgna.story.resources.biped.YetiResource",
          //
          //          createMoreSpecificFieldPattern("DIFFUSE", "org.lgna.story.resources.prop.FirTreeTrunkResource"),
          //          createMoreSpecificFieldReplacement("DEFAULT", "org.lgna.story.resources.prop.TreeTrunkResource"),
          //
          //          createMoreSpecificFieldPattern("SKELETON", "org.lgna.story.resources.prop.FirTreeTrunkResource"),
          //          createMoreSpecificFieldReplacement("DEFAULT", "org.lgna.story.resources.prop.TreeTrunkResource"),
          //
          //          createMoreSpecificFieldPattern("TOP_HAT", "org.lgna.story.resources.prop.FirTreeTrunkResource"),
          //          createMoreSpecificFieldReplacement("DEFAULT", "org.lgna.story.resources.prop.TreeTrunkResource"),
          //
          //          "name=\"org.lgna.story.resources.prop.FirTreeTrunkResource",
          //          "name=\"org.lgna.story.resources.prop.TreeTrunkResource",
          //
          //          createMoreSpecificFieldPattern("DEFAULT", "org.lgna.story.resources.biped.BatResource"),
          //          createMoreSpecificFieldReplacement("DEFAULT", "org.lgna.story.resources.biped.YetiResource"),
          //
          //          "name=\"org.lgna.story.resources.biped.BatResource",
          //          "name=\"org.lgna.story.resources.biped.YetiResource",

          replace(createMoreSpecificFieldPattern("DEFAULT", "org.lgna.story.resources.biped.AsuraResource"),
              createMoreSpecificFieldReplacement("DEFAULT", "org.lgna.story.resources.biped.AlienResource")),

          replace("name=\"org.lgna.story.resources.biped.AsuraResource",
              "name=\"org.lgna.story.resources.biped.AlienResource"),

          createMoreSpecificFieldRule("CHEAP", "TELEVISION_REMOTE", "org.lgna.story.resources.prop.TelevisionRemoteResource"),

          createJointIdRule("TAIL", "TAIL_0", "biped.BigBadWolfResource"),
          createJointIdRule("TAIL_2", "TAIL_1", "biped.BigBadWolfResource"),
          createJointIdRule("TAIL_3", "TAIL_2", "biped.BigBadWolfResource"),
          createJointIdRule("TAIL_4", "TAIL_3", "biped.BigBadWolfResource"),

          createJointIdRule("TAIL", "TAIL_0", "biped.BunnyResource"),
          createJointIdRule("TAIL_2", "TAIL_1", "biped.BunnyResource"),
          createJointIdRule("TAIL_3", "TAIL_2", "biped.BunnyResource"),

          createJointIdRule("TAIL", "TAIL_0", "biped.CheshireCatResource"),
          createJointIdRule("TAIL_2", "TAIL_1", "biped.CheshireCatResource"),
          createJointIdRule("TAIL_3", "TAIL_2", "biped.CheshireCatResource"),
          createJointIdRule("TAIL_4", "TAIL_3", "biped.CheshireCatResource"),
          createJointIdRule("TAIL_5", "TAIL_4", "biped.CheshireCatResource"),

          createJointIdRule("TAIL", "TAIL_0", "biped.GoldenMonkeyResource"),
          createJointIdRule("TAIL_2", "TAIL_1", "biped.GoldenMonkeyResource"),
          createJointIdRule("TAIL_3", "TAIL_2", "biped.GoldenMonkeyResource"),
          createJointIdRule("JOINT_4", "TAIL_3", "biped.GoldenMonkeyResource"),

          createJointIdRule("TAIL", "TAIL_0", "biped.HareResource"),
          createJointIdRule("TAIL_2", "TAIL_1", "biped.HareResource"),

          createJointIdRule("TAIL", "TAIL_0", "biped.MandrilResource"),
          createJointIdRule("TAIL_2", "TAIL_1", "biped.MandrilResource"),

          createJointIdRule("TAIL", "TAIL_0", "biped.MonkeyKingResource"),
          createJointIdRule("TAIL_2", "TAIL_1", "biped.MonkeyKingResource"),
          createJointIdRule("TAIL_3", "TAIL_2", "biped.MonkeyKingResource"),
          createJointIdRule("TAIL_4", "TAIL_3", "biped.MonkeyKingResource"),

          createJointIdRule("JAW_1", "MOUTH_TIP", "biped.PandaResource"),

          createJointIdRule("TAIL", "TAIL_0", "biped.StuffedTigerResource"),
          createJointIdRule("TAIL_2", "TAIL_1", "biped.StuffedTigerResource"),
          createJointIdRule("TAIL_3", "TAIL_2", "biped.StuffedTigerResource"),
          createJointIdRule("TAIL_4", "TAIL_3", "biped.StuffedTigerResource"),
          createJointIdRule("TAIL_5", "TAIL_4", "biped.StuffedTigerResource"),

          createJointIdRule("NECK", "NECK_0", "FlyerResource"),
          createJointIdRule("NECK_2", "NECK_1", "FlyerResource"),

          createJointIdRule("TAIL", "TAIL_0", "FlyerResource"),
          createJointIdRule("TAIL_2", "TAIL_1", "FlyerResource"),
          createJointIdRule("TAIL_3", "TAIL_2", "FlyerResource"),

          createJointIdRule("LEFT_PLUMAGE_1", "PLUMAGE_LEFT_TIP", "flyer.PeacockResource"),

          createJointIdRule("RIGHT_PLUMAGE_1", "PLUMAGE_RIGHT_TIP", "flyer.PeacockResource"),

          createJointIdRule("TAIL", "TAIL_0", "QuadrupedResource"),
          createJointIdRule("TAIL_2", "TAIL_1", "QuadrupedResource"),
          createJointIdRule("TAIL_3", "TAIL_2", "QuadrupedResource"),
          createJointIdRule("TAIL_4", "TAIL_3", "QuadrupedResource"),

          createJointIdRule("JAW_1", "LOWER_LIP", "quadruped.AbyssinianCatResource"),
          createJointIdRule("JAW_1", "MOUTH_TIP", "quadruped.AlienRobotResource"),
          createJointIdRule("JAW_1", "MOUTH_TIP", "quadruped.CaimanResource"),
          createJointIdRule("JAW_1", "MOUTH_TIP", "quadruped.CowResource"),
          createJointIdRule("JAW_1", "MOUTH_TIP", "quadruped.DragonResource"),
          createJointIdRule("JAW_1", "MOUTH_TIP", "quadruped.DragonBabyResource"),
          createJointIdRule("JAW_1", "MOUTH_TIP", "quadruped.PeccaryResource"),
          createJointIdRule("JAW_1", "MOUTH_TIP", "quadruped.YaliResource"),

          createJointIdRule("TONGUE", "TONGUE_0", "quadruped.CoyoteResource"),
          createJointIdRule("TONGUE_2", "TONGUE_1", "quadruped.CoyoteResource"),
          createJointIdRule("TONGUE_3", "TONGUE_2", "quadruped.CoyoteResource"),
          createJointIdRule("TONGUE_4", "TONGUE_3", "quadruped.CoyoteResource"),

          createJointIdRule("TRUNK_1", "TRUNK_0", "quadruped.ElephantResource"),
          createJointIdRule("TRUNK_2", "TRUNK_1", "quadruped.ElephantResource"),
          createJointIdRule("TRUNK_3", "TRUNK_2", "quadruped.ElephantResource"),
          createJointIdRule("TRUNK_4", "TRUNK_3", "quadruped.ElephantResource"),
          createJointIdRule("TRUNK_5", "TRUNK_4", "quadruped.ElephantResource"),
          createJointIdRule("TRUNK_6", "TRUNK_5", "quadruped.ElephantResource"),

          createJointIdRule("TONGUE_1", "TONGUE_0", "quadruped.HornedLizardResource"),
          createJointIdRule("TONGUE_2", "TONGUE_1", "quadruped.HornedLizardResource"),
          createJointIdRule("TONGUE_3", "TONGUE_2", "quadruped.HornedLizardResource"),
          createJointIdRule("TONGUE_4", "TONGUE_3", "quadruped.HornedLizardResource"),

          createJointIdRule("TONGUE", "TONGUE_0", "quadruped.YakResource"),
          createJointIdRule("TONGUE_2", "TONGUE_1", "quadruped.YakResource"),
          createJointIdRule("TONGUE_3", "TONGUE_2", "quadruped.YakResource"),
          createJointIdRule("TONGUE_4", "TONGUE_3", "quadruped.YakResource"),

          createJointIdRule("TRUNK", "TRUNK_0", "quadruped.YaliResource"),
          createJointIdRule("TRUNK_2", "TRUNK_1", "quadruped.YaliResource"),
          createJointIdRule("TRUNK_3", "TRUNK_2", "quadruped.YaliResource"),
          createJointIdRule("TRUNK_4", "TRUNK_3", "quadruped.YaliResource"),
          createJointIdRule("TRUNK_5", "TRUNK_4", "quadruped.YaliResource"),
          createJointIdRule("TRUNK_6", "TRUNK_5", "quadruped.YaliResource"),

          createJointIdRule("LEFT_1", "LEFT_0", "prop.NavajoBlanketResource"),
          createJointIdRule("LEFT_2", "LEFT_1", "prop.NavajoBlanketResource"),
          createJointIdRule("LEFT_3", "LEFT_2", "prop.NavajoBlanketResource"),
          createJointIdRule("RIGHT_1", "RIGHT_0", "prop.NavajoBlanketResource"),
          createJointIdRule("RIGHT_2", "RIGHT_1", "prop.NavajoBlanketResource"),
          createJointIdRule("RIGHT_3", "RIGHT_2", "prop.NavajoBlanketResource"),

          createJointIdRule("FLAG_5", "FLAG_05", "prop.PrayerFlagsResource"),
          createJointIdRule("FLAG_6", "FLAG_06", "prop.PrayerFlagsResource"),
          createJointIdRule("FLAG_7", "FLAG_07", "prop.PrayerFlagsResource"),
          createJointIdRule("FLAG_8", "FLAG_08", "prop.PrayerFlagsResource"),
          createJointIdRule("FLAG_9", "FLAG_09", "prop.PrayerFlagsResource"),

          createJointIdRule("STRING_2", "STRING_0", "prop.PrayerFlagsResource"),
          createJointIdRule("FLAG", "FLAG_00", "prop.PrayerFlagsResource"),
          createJointIdRule("FLAG_1", "FLAG_01", "prop.PrayerFlagsResource"),
          createJointIdRule("FLAG_2", "FLAG_02", "prop.PrayerFlagsResource"),
          createJointIdRule("FLAG_3", "FLAG_03", "prop.PrayerFlagsResource"),
          createJointIdRule("FLAG_4", "FLAG_04", "prop.PrayerFlagsResource"),

          createJointIdRule("STRING_3", "STRING_2", "prop.PrayerFlagsResource"),
          createJointIdRule("FLAG_10", "FLAG_10", "prop.PrayerFlagsResource"),
          createJointIdRule("FLAG_11", "FLAG_11", "prop.PrayerFlagsResource"),
          createJointIdRule("FLAG_12", "FLAG_12", "prop.PrayerFlagsResource"),
          createJointIdRule("FLAG_13", "FLAG_13", "prop.PrayerFlagsResource"),
          createJointIdRule("FLAG_14", "FLAG_14", "prop.PrayerFlagsResource"),

          createJointIdRule("TAIL_5", "TAIL_4", "quadruped.YaliResource"),

          createJointIdRule("TAIL_5", "TAIL_4", "quadruped.DalmatianResource")
      ),
      new TextMigration(
          new Version("3.2.108.0.0")

          ),
      createVersion3_2_110TextMigration(),
      new TextMigration(
          new Version("3.2.111.0.0"),

          createMoreSpecificFieldRule("BLEACHERS", "DEFAULT_BLEACHERS", "org.lgna.story.resources.prop.CircusBleachersResource"),

          replace(createMoreSpecificFieldPattern("BONE_PILE", "org.lgna.story.resources.prop.BonesResource"),
              createMoreSpecificFieldReplacement("DEFAULT", "org.lgna.story.resources.prop.BonePileResource")),

          replace("org.lgna.story.resources.prop.BonesResource",
              "org.lgna.story.resources.prop.BonePileResource"),

          createMoreSpecificFieldRule("SHORT", "DEFAULT", "org.lgna.story.resources.prop.WychElmResource")
          ),
      new TextMigration(
          new Version("3.2.112.0.0")
          ),
      new TextMigration(
          new Version("3.2.113.0.0"),

          replace(createMoreSpecificFieldPattern("DEFAULT", "org.lgna.story.resources.prop.FirTreeTrunkMirrorResource"),
              createMoreSpecificFieldReplacement("MIRROR", "org.lgna.story.resources.prop.FirTreeTrunkResource")),

          replace(createMoreSpecificFieldPattern("DEFAULT", "org.lgna.story.resources.prop.FirTreeTrunkTallResource"),
              createMoreSpecificFieldReplacement("TALL", "org.lgna.story.resources.prop.FirTreeTrunkResource")),

          replace(createMoreSpecificFieldPattern("DEFAULT", "org.lgna.story.resources.prop.FirTreeTrunkTallMirrorResource"),
              createMoreSpecificFieldReplacement("TALL_MIRROR", "org.lgna.story.resources.prop.FirTreeTrunkResource")),

          replace(createMoreSpecificFieldPattern("DEFAULT", "org.lgna.story.resources.prop.FirTreeTrunkSnowResource"),
              createMoreSpecificFieldReplacement("SNOW", "org.lgna.story.resources.prop.FirTreeTrunkResource")),

          replace(createMoreSpecificFieldPattern("DEFAULT", "org.lgna.story.resources.prop.FirTreeTrunkSnowMirrorResource"),
              createMoreSpecificFieldReplacement("SNOW_MIRROR", "org.lgna.story.resources.prop.FirTreeTrunkResource")),

          replace(createMoreSpecificFieldPattern("DEFAULT", "org.lgna.story.resources.prop.FirTreeTrunkSnowTallResource"),
              createMoreSpecificFieldReplacement("SNOW_TALL", "org.lgna.story.resources.prop.FirTreeTrunkResource")),

          replace(createMoreSpecificFieldPattern("DEFAULT", "org.lgna.story.resources.prop.FirTreeTrunkSnowTallMirrorResource"),
              createMoreSpecificFieldReplacement("SNOW_TALL_MIRROR", "org.lgna.story.resources.prop.FirTreeTrunkResource")),


          replace("org.lgna.story.resources.prop.FirTreeTrunkMirrorResource",
              "org.lgna.story.resources.prop.FirTreeTrunkResource"),

          replace("org.lgna.story.resources.prop.FirTreeTrunkTallResource",
              "org.lgna.story.resources.prop.FirTreeTrunkResource"),

          replace("org.lgna.story.resources.prop.FirTreeTrunkTallMirrorResource",
              "org.lgna.story.resources.prop.FirTreeTrunkResource"),

          replace("org.lgna.story.resources.prop.FirTreeTrunkSnowResource",
              "org.lgna.story.resources.prop.FirTreeTrunkResource"),

          replace("org.lgna.story.resources.prop.FirTreeTrunkSnowMirrorResource",
              "org.lgna.story.resources.prop.FirTreeTrunkResource"),

          replace("org.lgna.story.resources.prop.FirTreeTrunkSnowTallResource",
              "org.lgna.story.resources.prop.FirTreeTrunkResource"),

          replace("org.lgna.story.resources.prop.FirTreeTrunkSnowTallMirrorResource",
              "org.lgna.story.resources.prop.FirTreeTrunkResource"),

          replace("FirTreeTrunkSnowTallMirror",
              "FirTreeTrunk"),

          replace("FirTreeTrunkSnowTall",
              "FirTreeTrunk"),

          replace("FirTreeTrunkTallMirror",
              "FirTreeTrunk"),

          replace("FirTreeTrunkSnowMirror",
              "FirTreeTrunk"),

          replace("FirTreeTrunkSnow",
              "FirTreeTrunk"),

          replace("FirTreeTrunkMirror",
              "FirTreeTrunk"),

          replace("FirTreeTrunkTall",
              "FirTreeTrunk"),



          createMoreSpecificFieldRule("SQUARE", "SQUARE_DESERT", "org.lgna.story.resources.prop.SandDunesResource"),

          createMoreSpecificFieldRule("CRESCENT", "CRESCENT_DESERT", "org.lgna.story.resources.prop.SandDunesResource"),

          createMoreSpecificFieldRule("OVAL", "OVAL_DESERT", "org.lgna.story.resources.prop.SandDunesResource"),

          createMoreSpecificFieldRule("BLOB", "BLOB_DESERT", "org.lgna.story.resources.prop.SandDunesResource"),

          createMoreSpecificFieldRule("SQUARE_DRYGRASS", "SQUARE_DRY_GRASS", "org.lgna.story.resources.prop.SandDunesResource"),

          createMoreSpecificFieldRule("SQUARE_FORESTFLOOR", "SQUARE_FOREST_FLOOR", "org.lgna.story.resources.prop.SandDunesResource"),

          createMoreSpecificFieldRule("SQUARE_FORESTFLOORBROWN", "SQUARE_FOREST_FLOOR_BROWN", "org.lgna.story.resources.prop.SandDunesResource"),

          createMoreSpecificFieldRule("SQUARE_FORESTFLOORRED", "SQUARE_FOREST_FLOOR_RED", "org.lgna.story.resources.prop.SandDunesResource"),

          createMoreSpecificFieldRule("CRESCENT_DRYGRASS", "CRESCENT_DRY_GRASS", "org.lgna.story.resources.prop.SandDunesResource"),

          createMoreSpecificFieldRule("CRESCENT_FORESTFLOOR", "CRESCENT_FOREST_FLOOR", "org.lgna.story.resources.prop.SandDunesResource"),

          createMoreSpecificFieldRule("CRESCENT_FORESTFLOORBROWN", "CRESCENT_FOREST_FLOOR_BROWN", "org.lgna.story.resources.prop.SandDunesResource"),

          createMoreSpecificFieldRule("CRESCENT_FORESTFLOORRED", "CRESCENT_FOREST_FLOOR_RED", "org.lgna.story.resources.prop.SandDunesResource"),

          createMoreSpecificFieldRule("BLOB_DRYGRASS", "BLOB_DRY_GRASS", "org.lgna.story.resources.prop.SandDunesResource"),

          createMoreSpecificFieldRule("BLOB_FORESTFLOOR", "BLOB_FOREST_FLOOR", "org.lgna.story.resources.prop.SandDunesResource"),

          createMoreSpecificFieldRule("BLOB_FORESTFLOORBROWN", "BLOB_FOREST_FLOOR_BROWN", "org.lgna.story.resources.prop.SandDunesResource"),

          createMoreSpecificFieldRule("BLOB_FORESTFLOORRED", "BLOB_FOREST_FLOOR_RED", "org.lgna.story.resources.prop.SandDunesResource"),

          createMoreSpecificFieldRule("OVAL_DRYGRASS", "OVAL_DRY_GRASS", "org.lgna.story.resources.prop.SandDunesResource"),

          createMoreSpecificFieldRule("OVAL_FORESTFLOOR", "OVAL_FOREST_FLOOR", "org.lgna.story.resources.prop.SandDunesResource"),

          createMoreSpecificFieldRule("OVAL_FORESTFLOORBROWN", "OVAL_FOREST_FLOOR_BROWN", "org.lgna.story.resources.prop.SandDunesResource"),

          createMoreSpecificFieldRule("OVAL_FORESTFLOORRED", "OVAL_FOREST_FLOOR_RED", "org.lgna.story.resources.prop.SandDunesResource"),

          createMoreSpecificFieldRule("FLAT_DRYGRASS", "FLAT_DRY_GRASS", "org.lgna.story.resources.prop.SandDunesResource"),

          createMoreSpecificFieldRule("FLAT_FORESTFLOOR", "FLAT_FOREST_FLOOR", "org.lgna.story.resources.prop.SandDunesResource"),

          createMoreSpecificFieldRule("FLAT_FORESTFLOORBROWN", "FLAT_FOREST_FLOOR_BROWN", "org.lgna.story.resources.prop.SandDunesResource"),

          createMoreSpecificFieldRule("FLAT_FORESTFLOORRED", "FLAT_FOREST_FLOOR_RED", "org.lgna.story.resources.prop.SandDunesResource"),

          createMoreSpecificFieldRule("FLAT_OCEANNIGHT", "FLAT_OCEAN_NIGHT", "org.lgna.story.resources.prop.SandDunesResource"),

          createMoreSpecificFieldRule("FLAT_OVAL_DRYGRASS", "FLAT_OVAL_DRY_GRASS", "org.lgna.story.resources.prop.SandDunesResource"),

          createMoreSpecificFieldRule("FLAT_OVAL_FORESTFLOOR", "FLAT_OVAL_FOREST_FLOOR", "org.lgna.story.resources.prop.SandDunesResource"),

          createMoreSpecificFieldRule("FLAT_OVAL_FORESTFLOORBROWN", "FLAT_OVAL_FOREST_FLOOR_BROWN", "org.lgna.story.resources.prop.SandDunesResource"),

          createMoreSpecificFieldRule("FLAT_OVAL_FORESTFLOORRED", "FLAT_OVAL_FOREST_FLOOR_RED", "org.lgna.story.resources.prop.SandDunesResource"),

          createMoreSpecificFieldRule("FLAT_OVAL_OCEANNIGHT", "FLAT_OVAL_OCEAN_NIGHT", "org.lgna.story.resources.prop.SandDunesResource"),

          replace("org.lgna.story.resources.prop.SandDunesResource",
              "org.lgna.story.resources.prop.TerrainResource")

          ),

      new TextMigration(
          new Version("3.3.0.0.0"),

          replace("INDIA_BRICK_D",
              "GRAY"),

          replace("INDIA_LIGHT_BRICK_D",
              "GOLD"),

          replace("INDIA_LIGHTEST_BRICK_D",
              "SAND"),

          replace("INDIA_MED_BRICK_D",
              "RED"),


          replace("INDIA_WATER_TANK_LIGHTEST",
              "SAND"),

          replace("INDIA_WATER_TANK_LIGHT",
              "GOLD"),

          replace("INDIA_WATER_TANK_MED",
              "RED"),

          replace("INDIA_WATER_TANK",
              "GRAY")
          ),

      new TextMigration(new Version("3.4.0.0"),
          replace("<method isVarArgs=\"false\" name=\"getModelAtMouseLocation\"><declaringClass name=\"org.lgna.story.event.MouseClickEvent\"/><parameters/></method>",
              "<method isVarArgs=\"false\" name=\"getModelAtMouseLocation\"><declaringClass name=\"org.lgna.story.event.MouseClickOnObjectEvent\"/><parameters/></method>")
          ),

      new TextMigration(new Version("3.9.0.0"),
          replace("<method isVarArgs=\"true\" name=\"getDistanceTo\"><declaringClass name=\"org.lgna.story.STurnable\"/><parameters><type name=\"org.lgna.story.STurnable\"/>",
              "<method isVarArgs=\"true\" name=\"getDistanceTo\"><declaringClass name=\"org.lgna.story.STurnable\"/><parameters><type name=\"org.lgna.story.SThing\"/>")
      )

    };
  }

  // @formatter:on

  private TextMigrationRegistryLateVersions() {
  }
}
