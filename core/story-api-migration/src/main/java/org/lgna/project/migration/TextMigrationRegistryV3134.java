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
import static org.lgna.project.migration.ProjectMigrationTextSnippets.*;

// @formatter:off
@Deprecated // Text migrations now load from migrations/text-migrations.json; retained for JSON regeneration and documentation.
final class TextMigrationRegistryV3134 {

  static TextMigration[] create() {
    return new TextMigration[] {
      new TextMigration(
          new Version("3.1.34.0.0"),
          replace("org.lgna.story.Program",
              "org.lgna.story.SProgram"),
          replace("org.lgna.story.Entity",
              "org.lgna.story.SThing"),
          replace("org.lgna.story.Ground",
              "org.lgna.story.SGround"),
          replace("org.lgna.story.Room",
              "org.lgna.story.SRoom"),
          replace("org.lgna.story.Scene",
              "org.lgna.story.SScene"),
          replace("org.lgna.story.Turnable",
              "org.lgna.story.STurnable"),
          replace("org.lgna.story.Joint",
              "org.lgna.story.SJoint"),
          replace("org.lgna.story.MovableTurnable",
              "org.lgna.story.SMovableTurnable"),
          replace("org.lgna.story.Axes",
              "org.lgna.story.SAxes"),
          replace("org.lgna.story.Camera",
              "org.lgna.story.SCamera"),
          replace("org.lgna.story.Marker",
              "org.lgna.story.SMarker"),
          replace("org.lgna.story.BookmarkCameraMarker",
              "org.lgna.story.SCameraMarker"),
          replace("org.lgna.story.ObjectMarker",
              "org.lgna.story.SThingMarker"),

          replace("org.lgna.story.Model",
              "org.lgna.story.SModel"),

          replace("org.lgna.story.Billboard",
              "org.lgna.story.SBillboard"),

          replace("org.lgna.story.JointedModel",
              "org.lgna.story.SJointedModel"),

          replace("org.lgna.story.Biped",
              "org.lgna.story.SBiped"),

          replace("org.lgna.story.Flyer",
              "org.lgna.story.SFlyer"),

          replace("org.lgna.story.Prop",
              "org.lgna.story.SProp"),

          replace("org.lgna.story.Quadruped",
              "org.lgna.story.SQuadruped"),

          replace("org.lgna.story.Swimmer",
              "org.lgna.story.SSwimmer"),

          replace("org.lgna.story.Vehicle",
              "org.lgna.story.SVehicle"),

          replace("org.lgna.story.Shape",
              "org.lgna.story.SShape"),

          replace("org.lgna.story.Box",
              "org.lgna.story.SBox"),

          replace("org.lgna.story.Cone",
              "org.lgna.story.SCone"),

          replace("org.lgna.story.Cylinder",
              "org.lgna.story.SCylinder"),

          replace("org.lgna.story.Disc",
              "org.lgna.story.SDisc"),

          replace("org.lgna.story.Sphere",
              "org.lgna.story.SSphere"),

          replace("org.lgna.story.Torus",
              "org.lgna.story.STorus"),

          replace("org.lgna.story.TextModel",
              "org.lgna.story.STextModel"),

          replace("org.lgna.story.Target",
              "org.lgna.story.STarget"),

          replace("org.lgna.story.Sun",
              "org.lgna.story.SSun"),

          replace("ABYSSINIAN",
              "ABYSSINIAN_CAT"),

          replace("org.lgna.story.resources.flyer.MeanChicken",
              "org.lgna.story.resources.flyer.Chicken"),

          replace("org.lgna.story.resources.quadruped.Dalmation",
              "org.lgna.story.resources.quadruped.Dalmatian"),

          replace("BROWN_OGRE",
              "BROWN"),

          replace("PAJAMA_CARDINAL",
              "PAJAMA_FISH"),

          replace("PINK_POODLE",
              "POODLE"),

          replace("SHORT_HAIR",
              "SHORT_HAIR_CAT"),

          replace("ROBOT",
              "ALIEN_ROBOT"),

          replace("ASTEROID1_GRAY",
              "BOULDER1_MOON"),

          replace("ASTEROID1_RED",
              "BOULDER1_MARS"),

          replace("ASTEROID1_BROWN",
              "BOULDER1_DESERT"),

          replace("org.lgna.story.resources.prop.Boulder1",
              "org.lgna.story.resources.prop.Boulder"),

          replace("ASTEROID2_BROWN",
              "BOULDER2_DESERT"),

          replace("ASTEROID2_GRAY",
              "BOULDER2_MOON"),

          replace("ASTEROID2_RED",
              "BOULDER2_MARS"),

          replace("org.lgna.story.resources.prop.Boulder2",
              "org.lgna.story.resources.prop.Boulder"),

          replace("ASTEROID4_BROWN",
              "BOULDER3_DESERT"),

          replace("ASTEROID4_GRAY",
              "BOULDER3_MOON"),

          replace("ASTEROID4_RED",
              "BOULDER3_MARS"),

          replace("org.lgna.story.resources.prop.Boulder3",
              "org.lgna.story.resources.prop.Boulder"),

          replace("ASTEROID5_RED",
              "BOULDER4_MARS"),

          replace("ASTEROID5_GRAY",
              "BOULDER4_MOON"),

          replace("ASTEROID5_BROWN",
              "BOULDER4_DESERT"),

          replace("org.lgna.story.resources.prop.Boulder4",
              "org.lgna.story.resources.prop.Boulder"),

          replace("ASTEROID6_BROWN",
              "BOULDER5_DESERT"),

          replace("ASTEROID6_RED",
              "BOULDER5_MARS"),

          replace("ASTEROID6_GRAY",
              "BOULDER5_MOON"),

          replace("org.lgna.story.resources.prop.Boulder5",
              "org.lgna.story.resources.prop.Boulder"),

          replace("OUTSIDE_SHIP",
              "PIRATE_SHIP"),

          replace("CORAL_SHELF1",
              "SHELF1"),

          replace("org.lgna.story.resources.prop.CoralShelf1",
              "org.lgna.story.resources.prop.CoralShelf"),

          replace("CORAL_SHELF2",
              "SHELF2"),

          replace("org.lgna.story.resources.prop.CoralShelf2",
              "org.lgna.story.resources.prop.CoralShelf"),

          replace("SEA_PLANT1",
              "PLANT1"),

          replace("org.lgna.story.resources.prop.SeaPlant1",
              "org.lgna.story.resources.prop.SeaPlant"),

          replace("SEAPLANT2",
              "PLANT2"),

          replace("org.lgna.story.resources.prop.SeaPlant2",
              "org.lgna.story.resources.prop.SeaPlant"),

          replace("SEA_PLANT3",
              "PLANT3"),

          replace("org.lgna.story.resources.prop.SeaPlant3",
              "org.lgna.story.resources.prop.SeaPlant"),

          replace("org.lgna.story.resources.prop.SeaWeed1",
              "org.lgna.story.resources.prop.Seaweed"),

          replace("org.lgna.story.resources.prop.SeaWeed2",
              "org.lgna.story.resources.prop.Seaweed"),

          replace("org.lgna.story.resources.prop.SeaWeed3",
              "org.lgna.story.resources.prop.Seaweed"),

          replace("MUSHROOM_RED",
              "RED"),

          replace("org.lgna.story.resources.prop.ShortRedMushroom",
              "org.lgna.story.resources.prop.ShortMushroom"),

          replace("org.lgna.story.resources.prop.TallRedMushroom",
              "org.lgna.story.resources.prop.TallMushroom"),

          replace("MUSHROOM_WHITE",
              "WHITE"),

          replace("org.lgna.story.resources.prop.ShortWhiteMushroom",
              "org.lgna.story.resources.prop.ShortMushroom"),

          replace("org.lgna.story.resources.prop.TallWhiteMushroom",
              "org.lgna.story.resources.prop.TallMushroom"),

          replace("TREE_WONDERLAND",
              "WONDERLAND_TREE"),

          replace("ARMOIRE_LOFT_REDFINISH",
              "LOFT_RED_FINISH"),

          replace("ARMOIRE_LOFT_DARK_WOOD",
              "LOFT_DARK_WOOD"),

          replace("ARMOIRE_LOFT_HONEY",
              "LOFT_HONEY"),

          replace("ARMOIRE_LOFT_MAPLE",
              "LOFT_MAPLE"),

          replace("org.lgna.story.resources.ArmoireResource",
              "org.lgna.story.resources.prop.Armoire"),

          replace("org.lgna.story.resources.armoire.ArmoireLoft",
              "org.lgna.story.resources.prop.Armoire"),

          replace("org.lgna.story.resources.armoire.ArmoireQuaint",
              "org.lgna.story.resources.prop.Armoire"),

          replace("ARMOIRE_CENTRAL_ASIAN_GREENFLORAL",
              "CENTRAL_ASIAN_GREEN_FLORAL"),

          replace("ARMOIRE_CENTRAL_ASIAN_LATTICE",
              "CENTRAL_ASIAN_LATTICE"),

          replace("ARMOIRE_CENTRAL_ASIAN_LION",
              "CENTRAL_ASIAN_LION"),

          replace("ARMOIRE_CENTRAL_ASIAN_SIMPLEFLORAL",
              "CENTRAL_ASIAN_SIMPLE_FLORAL"),

          replace("ARMOIRE_CENTRAL_ASIAN_STORY",
              "CENTRAL_ASIAN_STORY"),

          replace("org.lgna.story.resources.armoire.ArmoireColonial",
              "org.lgna.story.resources.prop.Armoire"),

          replace("_ARMOIRE_MOROCCAN_GREEN",
              "MOROCCAN_GREEN"),

          replace("_ARMOIRE_MOROCCAN_BLUE",
              "MOROCCAN_BLUE"),

          replace("_ARMOIRE_MOROCCAN_RED",
              "MOROCCAN_RED"),

          replace("org.lgna.story.resources.armoire.ArmoireMoroccan",
              "org.lgna.story.resources.prop.Armoire"),

          replace("UFO__GLOW",
              "UFO"),

          replace("UFO__ZAP2",
              "UFO"),

          replace("CANDY_FACTORY_SURFACE",
              "CANDY_FACTORY"),

          replace("org.lgna.story.resources.prop.ArtNoveauCoffeeTable",
              "org.lgna.story.resources.prop.CoffeeTable"),

          replace("org.lgna.story.resources.prop.SmallClubCoffeeTable",
              "org.lgna.story.resources.prop.CoffeeTable"),

          replace("org.lgna.story.resources.prop.LargeClubCoffeeTable",
              "org.lgna.story.resources.prop.CoffeeTable"),

          createMoreSpecificFieldRule("TABLE_COFFEE_CLUB1_X1_MATERIALS_WOOD", "SMALL_CLUB_TABLE_COFFEE_CLUB1_X1_MATERIALS_WOOD", "org.lgna.story.resources.prop.CoffeeTable"),

          createMoreSpecificFieldRule("TABLE_COFFEE_CLUB1_X1_MATERIALS_WHITEOAK", "SMALL_CLUB_TABLE_COFFEE_CLUB1_X1_MATERIALS_WHITEOAK", "org.lgna.story.resources.prop.CoffeeTable"),

          createMoreSpecificFieldRule("TABLE_COFFEE_CLUB1_X1_MATERIAL_BIRDSRED", "SMALL_CLUB_TABLE_COFFEE_CLUB1_X1_MATERIAL_BIRDSRED", "org.lgna.story.resources.prop.CoffeeTable"),

          createMoreSpecificFieldRule("TABLE_COFFEE_CLUB1_X1_MATERIALS_MAHOG", "SMALL_CLUB_TABLE_COFFEE_CLUB1_X1_MATERIALS_MAHOG", "org.lgna.story.resources.prop.CoffeeTable"),

          createMoreSpecificFieldRule("TABLE_COFFEE_CLUB1_X1_MATERIALS_GUMWOOD", "SMALL_CLUB_TABLE_COFFEE_CLUB1_X1_MATERIALS_GUMWOOD", "org.lgna.story.resources.prop.CoffeeTable"),

          createMoreSpecificFieldRule("TABLE_COFFEE_CLUB1_X1_MATERIALS_REDASH", "SMALL_CLUB_TABLE_COFFEE_CLUB1_X1_MATERIALS_REDASH", "org.lgna.story.resources.prop.CoffeeTable"),

          createMoreSpecificFieldRule("TABLE_COFFEE_CLUB1_X1_MATERIALS_BLEACHEDOAK", "SMALL_CLUB_TABLE_COFFEE_CLUB1_X1_MATERIALS_BLEACHEDOAK", "org.lgna.story.resources.prop.CoffeeTable"),

          createMoreSpecificFieldRule("TABLE_COFFEE_CLUB1_X1_MATERIALS_LTBLUE", "SMALL_CLUB_TABLE_COFFEE_CLUB1_X1_MATERIALS_LTBLUE", "org.lgna.story.resources.prop.CoffeeTable"),

          replace("TABLE_COFFEE_COLONIAL_GOLDFLORAL",
              "COLONIAL_TABLE_COFFEE_COLONIAL_GOLDFLORAL"),

          replace("TABLE_COFFEE_COLONIAL_PAONAZZETTO",
              "COLONIAL_TABLE_COFFEE_COLONIAL_PAONAZZETTO"),

          replace("TABLE_COFFEE_COLONIAL_PERLINO",
              "COLONIAL_TABLE_COFFEE_COLONIAL_PERLINO"),

          replace("TABLE_COFFEE_COLONIAL_WHITEMARBLE",
              "COLONIAL_TABLE_COFFEE_COLONIAL_WHITEMARBLE"),

          replace("org.lgna.story.resources.prop.ColonialCoffeeTable",
              "org.lgna.story.resources.prop.CoffeeTable"),

          replace("TABLE_COFFEE_END_DESIGNER_WALNUT",
              "DESIGNER_TABLE_COFFEE_END_DESIGNER_WALNUT"),

          replace("org.lgna.story.resources.prop.DesignerCoffeeTable",
              "org.lgna.story.resources.prop.CoffeeTable"),

          replace("_TABLE_COFFEE_LOFT_SHEEN",
              "LOFT_TABLE_COFFEE_LOFT_SHEEN"),

          replace("_TABLE_COFFEE_LOFT_CONCRETE",
              "LOFT_TABLE_COFFEE_LOFT_CONCRETE"),

          replace("_TABLE_COFFEE_LOFT_RED_METAL",
              "LOFT_TABLE_COFFEE_LOFT_RED_METAL"),

          replace("_TABLE_COFFEE_LOFT_PATINA",
              "LOFT_TABLE_COFFEE_LOFT_PATINA"),

          replace("org.lgna.story.resources.prop.LoftCoffeeTable",
              "org.lgna.story.resources.prop.CoffeeTable"),

          replace("TABLE_COFFEE_MOROCCAN_TOP_TABLE_STAR",
              "MOROCCAN_TABLE_COFFEE_MOROCCAN_TOP_TABLE_STAR"),

          replace("TABLE_COFFEE_MOROCCAN_TOP_TABLE_ALADDIN",
              "MOROCCAN_TABLE_COFFEE_MOROCCAN_TOP_TABLE_ALADDIN"),

          replace("TABLE_COFFEE_MOROCCAN_TOP_TABLE_DETAIL",
              "MOROCCAN_TABLE_COFFEE_MOROCCAN_TOP_TABLE_DETAIL"),

          replace("TABLE_COFFEE_MOROCCAN_TOP_TABLE_TILE",
              "MOROCCAN_TABLE_COFFEE_MOROCCAN_TOP_TABLE_TILE"),

          replace("TABLE_COFFEE_MOROCCAN_WOODS_CHERRY",
              "MOROCCAN_TABLE_COFFEE_MOROCCAN_WOODS_CHERRY"),

          replace("TABLE_COFFEE_MOROCCAN_WOODS_MAHOGNY",
              "MOROCCAN_TABLE_COFFEE_MOROCCAN_WOODS_MAHOGNY"),

          replace("TABLE_COFFEE_MOROCCAN_WOODS_YELLOWASPEN",
              "MOROCCAN_TABLE_COFFEE_MOROCCAN_WOODS_YELLOWASPEN"),

          replace("TABLE_COFFEE_MOROCCAN_WOODS_BLACK_LAQUER",
              "MOROCCAN_TABLE_COFFEE_MOROCCAN_WOODS_BLACK_LAQUER"),

          replace("org.lgna.story.resources.prop.MoroccanCoffeeTable",
              "org.lgna.story.resources.prop.CoffeeTable"),

          replace("_TABLE_COFFEE_PINE_CEDAR_WOOD",
              "PINE_TABLE_COFFEE_PINE_CEDAR_WOOD"),

          replace("_TABLE_COFFEE_PINE_BLONDE_WOOD",
              "PINE_TABLE_COFFEE_PINE_BLONDE_WOOD"),

          replace("_TABLE_COFFEE_PINE_HONEY_PINE",
              "PINE_TABLE_COFFEE_PINE_HONEY_PINE"),

          replace("_TABLE_COFFEE_PINE_WALNUT_WOOD",
              "PINE_TABLE_COFFEE_PINE_WALNUT_WOOD"),

          replace("_TABLE_COFFEE_PINE_BIRCH_WOOD",
              "PINE_TABLE_COFFEE_PINE_BIRCH_WOOD"),

          replace("org.lgna.story.resources.prop.PineCoffeeTable",
              "org.lgna.story.resources.prop.CoffeeTable"),

          replace("TABLE_COFFEE_QUAINT_BLUE",
              "QUAINT_TABLE_COFFEE_QUAINT_BLUE"),

          replace("TABLE_COFFEE_QUAINT_GREEN",
              "QUAINT_TABLE_COFFEE_QUAINT_GREEN"),

          replace("TABLE_COFFEE_QUAINT_WHITE",
              "QUAINT_TABLE_COFFEE_QUAINT_WHITE"),

          replace("TABLE_COFFEE_QUAINT_RED",
              "QUAINT_TABLE_COFFEE_QUAINT_RED"),

          replace("org.lgna.story.resources.prop.QuaintCoffeeTable",
              "org.lgna.story.resources.prop.CoffeeTable"),

          replace("TABLE_COFFEE_SPINDLE_WOOD_OLDWOOD",
              "SPINDLE_TABLE_COFFEE_SPINDLE_WOOD_OLDWOOD"),

          replace("TABLE_COFFEE_SPINDLE_WOOD_PAINTED",
              "SPINDLE_TABLE_COFFEE_SPINDLE_WOOD_PAINTED"),

          replace("TABLE_COFFEE_SPINDLE_WOOD_RED",
              "SPINDLE_TABLE_COFFEE_SPINDLE_WOOD_RED"),

          replace("org.lgna.story.resources.prop.SpindleCoffeeTable",
              "org.lgna.story.resources.prop.CoffeeTable"),

          replace("name=\"TABLE_DINING_CLUB_ROOT",
              "name=\"CLUB_TABLE_DINING_CLUB_ROOT"),

          replace("name=\"TABLE_DINING_CLUB_NEDAR",
              "name=\"CLUB_TABLE_DINING_CLUB_NEDAR"),

          replace("name=\"TABLE_DINING_CLUB_OAK",
              "name=\"CLUB_TABLE_DINING_CLUB_OAK"),

          replace("name=\"TABLE_DINING_CLUB_RED",
              "name=\"CLUB_TABLE_DINING_CLUB_RED"),

          replace("name=\"TABLE_DINING_CLUB_REDDARK",
              "name=\"CLUB_TABLE_DINING_CLUB_REDDARK"),

          replace("name=\"TABLE_DINING_CLUB_WOOD",
              "name=\"CLUB_TABLE_DINING_CLUB_WOOD"),

          replace("name=\"TABLE_DINING_CLUB_PINE",
              "name=\"CLUB_TABLE_DINING_CLUB_PINE"),

          replace("name=\"TABLE_DINING_CLUB_SEDAR",
              "name=\"CLUB_TABLE_DINING_CLUB_SEDAR"),

          replace("org.lgna.story.resources.prop.ClubDiningTable",
              "org.lgna.story.resources.prop.DiningTable"),

          replace("name=\"TABLE_DINING_MOROCCAN_TURQ",
              "name=\"MOROCCAN_TABLE_DINING_MOROCCAN_TURQ"),

          replace("name=\"TABLE_DINING_MOROCCAN_BLUE",
              "name=\"MOROCCAN_TABLE_DINING_MOROCCAN_BLUE"),

          replace("name=\"TABLE_DINING_MOROCCAN_BLUE_LIGHT",
              "name=\"MOROCCAN_TABLE_DINING_MOROCCAN_BLUE_LIGHT"),

          replace("name=\"TABLE_DINING_MOROCCAN_GREEN",
              "name=\"MOROCCAN_TABLE_DINING_MOROCCAN_GREEN"),

          replace("org.lgna.story.resources.prop.MoroccanDiningTable",
              "org.lgna.story.resources.prop.DiningTable"),

          replace("TABLE_DINING_ORIENTAL_DRAGON_BROWN",
              "ORIENTAL_TABLE_DINING_ORIENTAL_DRAGON_BROWN"),

          replace("TABLE_DINING_ORIENTAL_FISH_BROWN",
              "ORIENTAL_TABLE_DINING_ORIENTAL_FISH_BROWN"),

          replace("TABLE_DINING_ORIENTAL_DRAGON_RED",
              "ORIENTAL_TABLE_DINING_ORIENTAL_DRAGON_RED"),

          replace("TABLE_DINING_ORIENTAL_FISH_RED",
              "ORIENTAL_TABLE_DINING_ORIENTAL_FISH_RED"),

          replace("TABLE_DINING_ORIENTAL_LOTUS_BLACK",
              "ORIENTAL_TABLE_DINING_ORIENTAL_LOTUS_BLACK"),

          replace("TABLE_DINING_ORIENTAL_LOTUS_ORANGE",
              "ORIENTAL_TABLE_DINING_ORIENTAL_LOTUS_ORANGE"),

          replace("org.lgna.story.resources.prop.OrientalDiningTable",
              "org.lgna.story.resources.prop.DiningTable"),

          replace("TABLE_DINING_OUTDOOR_WOOD_ASH",
              "OUTDOOR_TABLE_DINING_OUTDOOR_WOOD_ASH"),

          replace("TABLE_DINING_OUTDOOR_WOOD_REDOAK",
              "OUTDOOR_TABLE_DINING_OUTDOOR_WOOD_REDOAK"),

          replace("TABLE_DINING_OUTDOOR_WOOD_REDWOOD",
              "OUTDOOR_TABLE_DINING_OUTDOOR_WOOD_REDWOOD"),

          replace("TABLE_DINING_OUTDOOR_WOOD_WHITE",
              "OUTDOOR_TABLE_DINING_OUTDOOR_WOOD_WHITE"),

          replace("TABLE_DINING_OUTDOOR_WOOD_CROSSPINE",
              "OUTDOOR_TABLE_DINING_OUTDOOR_WOOD_CROSSPINE"),

          replace("org.lgna.story.resources.prop.OutdoorDiningTable",
              "org.lgna.story.resources.prop.DiningTable"),

          replace("TABLE_DINING_QUAINT_RED",
              "QUAINT_TABLE_DINING_QUAINT_RED"),

          replace("TABLE_DINING_QUAINT_GREEN",
              "QUAINT_TABLE_DINING_QUAINT_GREEN"),

          replace("TABLE_DINING_QUAINT_WHITE",
              "QUAINT_TABLE_DINING_QUAINT_WHITE"),

          replace("TABLE_DINING_QUAINT_BLUE",
              "QUAINT_TABLE_DINING_QUAINT_BLUE"),

          replace("org.lgna.story.resources.prop.QuaintDiningTable",
              "org.lgna.story.resources.prop.DiningTable"),

          replace("org.lgna.story.resources.prop.ClubEndTable",
              "org.lgna.story.resources.prop.EndTable"),

          createMoreSpecificFieldRule("TABLE_COFFEE_CLUB1_X1_MATERIALS_WOOD", "CLUB_TABLE_COFFEE_CLUB1_X1_MATERIALS_WOOD", "org.lgna.story.resources.prop.EndTable"),

          createMoreSpecificFieldRule("TABLE_COFFEE_CLUB1_X1_MATERIALS_WHITEOAK", "CLUB_TABLE_COFFEE_CLUB1_X1_MATERIALS_WHITEOAK", "org.lgna.story.resources.prop.EndTable"),

          createMoreSpecificFieldRule("TABLE_COFFEE_CLUB1_X1_MATERIAL_BIRDSRED", "CLUB_TABLE_COFFEE_CLUB1_X1_MATERIAL_BIRDSRED", "org.lgna.story.resources.prop.EndTable"),

          createMoreSpecificFieldRule("TABLE_COFFEE_CLUB1_X1_MATERIALS_MAHOG", "CLUB_TABLE_COFFEE_CLUB1_X1_MATERIALS_MAHOG", "org.lgna.story.resources.prop.EndTable"),

          createMoreSpecificFieldRule("TABLE_COFFEE_CLUB1_X1_MATERIALS_GUMWOOD", "CLUB_TABLE_COFFEE_CLUB1_X1_MATERIALS_GUMWOOD", "org.lgna.story.resources.prop.EndTable"),

          createMoreSpecificFieldRule("TABLE_COFFEE_CLUB1_X1_MATERIALS_REDASH", "CLUB_TABLE_COFFEE_CLUB1_X1_MATERIALS_REDASH", "org.lgna.story.resources.prop.EndTable"),

          createMoreSpecificFieldRule("TABLE_COFFEE_CLUB1_X1_MATERIALS_BLEACHEDOAK", "CLUB_TABLE_COFFEE_CLUB1_X1_MATERIALS_BLEACHEDOAK", "org.lgna.story.resources.prop.EndTable"),

          createMoreSpecificFieldRule("TABLE_COFFEE_CLUB1_X1_MATERIALS_LTBLUE", "CLUB_TABLE_COFFEE_CLUB1_X1_MATERIALS_LTBLUE", "org.lgna.story.resources.prop.EndTable"),

          replace("TABLE_END_COLONIAL2_TABLE_LIGHTWOOD",
              "COLONIAL_TABLE_END_COLONIAL2_TABLE_LIGHTWOOD"),

          replace("TABLE_END_COLONIAL2_TABLE_REDWOOD",
              "COLONIAL_TABLE_END_COLONIAL2_TABLE_REDWOOD"),

          replace("TABLE_END_COLONIAL2_TABLE_WOOD",
              "COLONIAL_TABLE_END_COLONIAL2_TABLE_WOOD"),

          replace("TABLE_END_COLONIAL2_TABLE_DARKWOOD",
              "COLONIAL_TABLE_END_COLONIAL2_TABLE_DARKWOOD"),

          replace("TABLE_END_MOROCCAN_END_TABLE_ALADDIN",
              "MOROCCAN_TABLE_END_MOROCCAN_END_TABLE_ALADDIN"),

          replace("TABLE_END_MOROCCAN_END_TABLE_STAR",
              "MOROCCAN_TABLE_END_MOROCCAN_END_TABLE_STAR"),

          replace("TABLE_END_MOROCCAN_END_TABLE_TILE",
              "MOROCCAN_TABLE_END_MOROCCAN_END_TABLE_TILE"),

          replace("TABLE_END_MOROCCAN_END_TABLE_DETAIL",
              "MOROCCAN_TABLE_END_MOROCCAN_END_TABLE_DETAIL"),

          replace("org.lgna.story.resources.prop.MoroccanEndTable",
              "org.lgna.story.resources.prop.EndTable"),

          replace("org.lgna.story.resources.prop.OctagonalEndTable",
              "org.lgna.story.resources.prop.EndTable"),

          replace("TABLE_END_QUAINT__FABRIC_BLUE",
              "QUAINT_TABLE_END_QUAINT_FABRIC_BLUE"),

          replace("TABLE_END_QUAINT__FABRIC_PINK",
              "QUAINT_TABLE_END_QUAINT_FABRIC_PINK"),

          replace("TABLE_END_QUAINT__FABRIC_GREEN",
              "QUAINT_TABLE_END_QUAINT_FABRIC_GREEN"),

          replace("TABLE_END_QUAINT__FABRIC_BEIGE",
              "QUAINT_TABLE_END_QUAINT_FABRIC_BEIGE"),

          replace("TABLE_END_QUAINT__FABRIC_WHITE",
              "QUAINT_TABLE_END_QUAINT_FABRIC_WHITE"),

          replace("TABLE_END_QUAINT__FABRIC_WHITE_FLOWERS",
              "QUAINT_TABLE_END_QUAINT_FABRIC_WHITE_FLOWERS"),

          replace("TABLE_END_UM_PURPLE",
              "UM_TABLE_END_UM_PURPLE"),

          replace("TABLE_END_UM_GREEN",
              "UM_TABLE_END_UM_GREEN"),

          replace("TABLE_END_UM_BLACK",
              "UM_TABLE_END_UM_BLACK"),

          replace("TABLE_END_UM_BLUE",
              "UM_TABLE_END_UM_BLUE"),

          replace("TABLE_END_UM_YELLOW",
              "UM_TABLE_END_UM_YELLOW"),

          replace("TABLE_END_UM_WHITE",
              "UM_TABLE_END_UM_WHITE"),

          replace("TABLE_END_UM_ORANGE",
              "UM_TABLE_END_UM_ORANGE"),

          replace("org.lgna.story.resources.prop.UmEndTable",
              "org.lgna.story.resources.prop.EndTable"),

          replace("LOFT_BOOKCASE_WOOD_DARK",
              "LOFT_LOFT_BOOKCASE_WOOD_DARK"),

          replace("LOFT_BOOKCASE_WOOD_LIGHT",
              "LOFT_LOFT_BOOKCASE_WOOD_LIGHT"),

          replace("LOFT_BOOKCASE_WOOD_MEDIUM",
              "LOFT_LOFT_BOOKCASE_WOOD_MEDIUM"),

          replace("LOFT_BOOKCASE_BRUSHED",
              "LOFT_LOFT_BOOKCASE_BRUSHED"),

          replace("org.lgna.story.resources.prop.BookcaseLoft",
              "org.lgna.story.resources.prop.Bookcase"),

          replace("BOOKCASE_CHEAP_OAK",
              "CHEAP_BOOKCASE_CHEAP_OAK"),

          replace("BOOKCASE_CHEAP_MAHOGANY",
              "CHEAP_BOOKCASE_CHEAP_MAHOGANY"),

          replace("BOOKCASE_CHEAP_PINE",
              "CHEAP_BOOKCASE_CHEAP_PINE"),

          replace("BOOKCASE_CHEAP_BLACK_WASH",
              "CHEAP_BOOKCASE_CHEAP_BLACK_WASH"),

          replace("BOOKCASE_CHEAP_WOOD_PLANK",
              "CHEAP_BOOKCASE_CHEAP_WOOD_PLANK"),

          replace("org.lgna.story.resources.prop.BookcaseCheap",
              "org.lgna.story.resources.prop.Bookcase"),

          replace("BOOKCASE_COLONIAL_REDWOODCURLY",
              "COLONIAL_BOOKCASE_COLONIAL_REDWOODCURLY"),

          replace("BOOKCASE_COLONIAL_BROWNWOODCURLY",
              "COLONIAL_BOOKCASE_COLONIAL_BROWNWOODCURLY"),

          replace("BOOKCASE_COLONIAL_DARK_BROWN_WOODCURLY",
              "COLONIAL_BOOKCASE_COLONIAL_DARK_BROWN_WOODCURLY"),

          replace("org.lgna.story.resources.prop.BookcaseColonial",
              "org.lgna.story.resources.prop.Bookcase"),

          replace("BOOKCASE_VALUE_PRESSEDPINE",
              "VALUE_BOOKCASE_VALUE_PRESSEDPINE"),

          replace("BOOKCASE_VALUE_PINE",
              "VALUE_BOOKCASE_VALUE_PINE"),

          replace("org.lgna.story.resources.prop.BookcaseValue",
              "org.lgna.story.resources.prop.Bookcase"),

          replace("org.lgna.story.resources.prop.LoveseatCamelBack",
              "org.lgna.story.resources.prop.Loveseat"),

          replace("org.lgna.story.resources.prop.LoveseatMoroccan",
              "org.lgna.story.resources.prop.Loveseat"),

          replace("org.lgna.story.resources.prop.LoveseatParkBench",
              "org.lgna.story.resources.prop.Loveseat"),

          replace("org.lgna.story.resources.prop.LoveseatQuaint",
              "org.lgna.story.resources.prop.Loveseat"),

          createMoreSpecificFieldRule("SOFA_MOROCCAN_BEIGE", "MOROCCAN_SOFA_MOROCCAN_BEIGE", "org.lgna.story.resources.prop.Loveseat"),

          createMoreSpecificFieldRule("SOFA_MOROCCAN_GREEN", "MOROCCAN_SOFA_MOROCCAN_GREEN", "org.lgna.story.resources.prop.Loveseat"),

          createMoreSpecificFieldRule("SOFA_MOROCCAN_RED", "MOROCCAN_SOFA_MOROCCAN_RED", "org.lgna.story.resources.prop.Loveseat"),

          createMoreSpecificFieldRule("LOVESEAT_PARK_BENCH_OAK", "PARK_BENCH_LOVESEAT_PARK_BENCH_OAK", "org.lgna.story.resources.prop.Loveseat"),

          createMoreSpecificFieldRule("LOVESEAT_PARK_BENCH_RED", "PARK_BENCH_LOVESEAT_PARK_BENCH_RED", "org.lgna.story.resources.prop.Loveseat"),

          createMoreSpecificFieldRule("LOVESEAT_PARK_BENCH_OAKGREEN", "PARK_BENCH_LOVESEAT_PARK_BENCH_OAKGREEN", "org.lgna.story.resources.prop.Loveseat"),

          createMoreSpecificFieldRule("LOVESEAT_PARK_BENCH_OAKBLUE", "PARK_BENCH_LOVESEAT_PARK_BENCH_OAKBLUE", "org.lgna.story.resources.prop.Loveseat"),

          createMoreSpecificFieldRule("LOVESEAT_PARK_BENCH_IVORY", "PARK_BENCH_LOVESEAT_PARK_BENCH_IVORY", "org.lgna.story.resources.prop.Loveseat"),

          replace("LOVESEAT_VALUE_RED_CHECKER",
              "VALUE_LOVESEAT_VALUE_RED_CHECKER"),

          replace("LOVESEAT_VALUE_BLUE_CHECKER",
              "VALUE_LOVESEAT_VALUE_BLUE_CHECKER"),

          replace("LOVESEAT_VALUE_BLUE_STRIPE",
              "VALUE_LOVESEAT_VALUE_BLUE_STRIPE"),

          replace("LOVESEAT_VALUE_FLOWER",
              "VALUE_LOVESEAT_VALUE_FLOWER"),

          replace("org.lgna.story.resources.prop.LoveseatValue",
              "org.lgna.story.resources.prop.Loveseat"),

          replace("DESK_CENTRAL_ASIAN_RED",
              "CENTRAL_ASIAN_DESK_CENTRAL_ASIAN_RED"),

          replace("DESK_CENTRAL_ASIAN_BLACK",
              "CENTRAL_ASIAN_DESK_CENTRAL_ASIAN_BLACK"),

          replace("DESK_CLUB__DARKWOOD",
              "CLUB_DESK_CLUB_DARKWOOD"),

          replace("DESK_CLUB__ASH",
              "CLUB_DESK_CLUB_ASH"),

          replace("DESK_CLUB__REDWOOD",
              "CLUB_DESK_CLUB_REDWOOD"),

          replace("org.lgna.story.resources.prop.DeskClub",
              "org.lgna.story.resources.prop.Desk"),

          replace("DESK_QUAINT_GREEN",
              "QUAINT_DESK_QUAINT_GREEN"),

          replace("DESK_QUAINT_WHITE",
              "QUAINT_DESK_QUAINT_WHITE"),

          replace("DESK_QUAINT_RED",
              "QUAINT_DESK_QUAINT_RED"),

          replace("DESK_QUAINT_BLUE",
              "QUAINT_DESK_QUAINT_BLUE"),

          replace("DESK_VALUE_WOODWHITE",
              "VALUE_DESK_VALUE_WOODWHITE"),

          replace("DESK_VALUE_WOODRED",
              "VALUE_DESK_VALUE_WOODRED"),

          replace("DESK_VALUE_WOOD_METAL",
              "VALUE_DESK_VALUE_WOOD_METAL"),

          replace("DESK_VALUE_WOODMAPPLE",
              "VALUE_DESK_VALUE_WOODMAPPLE"),

          replace("org.lgna.story.resources.prop.DeskValue",
              "org.lgna.story.resources.prop.Desk"),

          replace("SOFA_VALUE1_REDCHECKER",
              "VALUE1_SOFA_VALUE1_REDCHECKER"),

          replace("SOFA_VALUE1_BLUE_CHECKER",
              "VALUE1_SOFA_VALUE1_BLUE_CHECKER"),

          replace("SOFA_VALUE1_BLUE_STRIPE",
              "VALUE1_SOFA_VALUE1_BLUE_STRIPE"),

          replace("SOFA_VALUE1_FLOWER",
              "VALUE1_SOFA_VALUE1_FLOWER"),

          replace("org.lgna.story.resources.prop.SofaValue1",
              "org.lgna.story.resources.prop.Sofa"),

          replace("org.lgna.story.resources.prop.SofaQuaint",
              "org.lgna.story.resources.prop.Sofa"),

          replace("org.lgna.story.resources.prop.SofaValue2",
              "org.lgna.story.resources.prop.Sofa"),

          replace("org.lgna.story.resources.prop.SofaSteelFrame",
              "org.lgna.story.resources.prop.Sofa"),

          replace("org.lgna.story.resources.prop.SofaColonial1",
              "org.lgna.story.resources.prop.Sofa"),

          replace("org.lgna.story.resources.prop.SofaColonial2",
              "org.lgna.story.resources.prop.Sofa"),

          replace("org.lgna.story.resources.prop.SofaMoroccan",
              "org.lgna.story.resources.prop.Sofa"),

          replace("org.lgna.story.resources.prop.SofaModernCutout",
              "org.lgna.story.resources.prop.Sofa"),

          replace("org.lgna.story.resources.prop.SofaModernDiamond",
              "org.lgna.story.resources.prop.Sofa"),

          createMoreSpecificFieldRule("SOFA_MOROCCAN_BEIGE", "MOROCCAN_SOFA_MOROCCAN_BEIGE", "org.lgna.story.resources.prop.Sofa"),

          createMoreSpecificFieldRule("SOFA_MOROCCAN_GREEN", "MOROCCAN_SOFA_MOROCCAN_GREEN", "org.lgna.story.resources.prop.Sofa"),

          createMoreSpecificFieldRule("SOFA_MOROCCAN_RED", "MOROCCAN_SOFA_MOROCCAN_RED", "org.lgna.story.resources.prop.Sofa"),

          replace("SOFA_VALUE2_LIGHT_BROWN_FLOWER",
              "VALUE2_SOFA_VALUE2_LIGHT_BROWN_FLOWER"),

          replace("SOFA_VALUE2_RED_CHECKER",
              "VALUE2_SOFA_VALUE2_RED_CHECKER"),

          replace("SOFA_VALUE2_GREEN_FLOWER",
              "VALUE2_SOFA_VALUE2_GREEN_FLOWER"),

          replace("SOFA_VALUE2_BLUE_FLOWER_BORDER",
              "VALUE2_SOFA_VALUE2_BLUE_FLOWER_BORDER"),

          replace("name=\"LIGHTING_FLOOR_DESIGNER_SHADE_BLUESHADE",
              "name=\"DESIGNER_LIGHTING_FLOOR_DESIGNER_SHADE_BLUESHADE"),

          replace("name=\"LIGHTING_FLOOR_DESIGNER_SHADE_BLUESHADEON",
              "name=\"DESIGNER_LIGHTING_FLOOR_DESIGNER_SHADE_BLUESHADEON"),

          replace("name=\"LIGHTING_FLOOR_DESIGNER_SHADE_PLAINSHADE",
              "name=\"DESIGNER_LIGHTING_FLOOR_DESIGNER_SHADE_PLAINSHADE"),

          replace("name=\"LIGHTING_FLOOR_DESIGNER_SHADE_PLAINSHADEON",
              "name=\"DESIGNER_LIGHTING_FLOOR_DESIGNER_SHADE_PLAINSHADEON"),

          replace("name=\"LIGHTING_FLOOR_DESIGNER_SHADE_OLIVESHADE",
              "name=\"DESIGNER_LIGHTING_FLOOR_DESIGNER_SHADE_OLIVESHADE"),

          replace("name=\"LIGHTING_FLOOR_DESIGNER_SHADE_OLIVESHADEON",
              "name=\"DESIGNER_LIGHTING_FLOOR_DESIGNER_SHADE_OLIVESHADEON"),

          replace("name=\"LIGHTING_FLOOR_DESIGNER_SHADE_ORANGESHADEON",
              "name=\"DESIGNER_LIGHTING_FLOOR_DESIGNER_SHADE_ORANGESHADEON"),

          replace("name=\"LIGHTING_FLOOR_DESIGNER_SHADE_ORANGESHADE",
              "name=\"DESIGNER_LIGHTING_FLOOR_DESIGNER_SHADE_ORANGESHADE"),

          replace("name=\"LIGHTING_FLOOR_DESIGNER_SHADE_REDSHADE",
              "name=\"DESIGNER_LIGHTING_FLOOR_DESIGNER_SHADE_REDSHADE"),

          replace("name=\"LIGHTING_FLOOR_DESIGNER_SHADE_REDSHADEON",
              "name=\"DESIGNER_LIGHTING_FLOOR_DESIGNER_SHADE_REDSHADEON"),

          replace("LIGHTING_FLOOR_LOFT_LAMP_SHADE_YELLOW_UNLIT",
              "LOFT_LIGHTING_FLOOR_LOFT_LAMP_SHADE_YELLOW_UNLIT"),

          replace("LIGHTING_FLOOR_LOFT_LAMP_SHADE_BLUE_UNLIT",
              "LOFT_LIGHTING_FLOOR_LOFT_LAMP_SHADE_BLUE_UNLIT"),

          replace("LIGHTING_FLOOR_LOFT_LAMP_SHADE_GREEN_UNLIT",
              "LOFT_LIGHTING_FLOOR_LOFT_LAMP_SHADE_GREEN_UNLIT"),

          replace("LIGHTING_FLOOR_LOFT_LAMP_SHADE_ORANGE_UNLIT",
              "LOFT_LIGHTING_FLOOR_LOFT_LAMP_SHADE_ORANGE_UNLIT"),

          replace("LIGHTING_FLOOR_LOFT_LAMP_SHADE_RED_UNLIT",
              "LOFT_LIGHTING_FLOOR_LOFT_LAMP_SHADE_RED_UNLIT"),

          replace("LIGHTING_FLOOR_LOFT_LAMP_SHADE_YELLOW_LIT",
              "LOFT_LIGHTING_FLOOR_LOFT_LAMP_SHADE_YELLOW_LIT"),

          replace("LIGHTING_FLOOR_LOFT_LAMP_SHADE_BLUE_LIT",
              "LOFT_LIGHTING_FLOOR_LOFT_LAMP_SHADE_BLUE_LIT"),

          replace("LIGHTING_FLOOR_LOFT_LAMP_SHADE_RED_LIT",
              "LOFT_LIGHTING_FLOOR_LOFT_LAMP_SHADE_RED_LIT"),

          replace("LIGHTING_FLOOR_LOFT_LAMP_SHADE_GREEN_LIT",
              "LOFT_LIGHTING_FLOOR_LOFT_LAMP_SHADE_GREEN_LIT"),

          replace("LIGHTING_FLOOR_MOROCCAN_SHADE_BLUE_UNLIT",
              "MOROCCAN_LIGHTING_FLOOR_MOROCCAN_SHADE_BLUE_UNLIT"),

          replace("LIGHTING_FLOOR_MOROCCAN_SHADE_GOLD_BLUE_UNLIT",
              "MOROCCAN_LIGHTING_FLOOR_MOROCCAN_SHADE_GOLD_BLUE_UNLIT"),

          replace("LIGHTING_FLOOR_MOROCCAN_SHADE_ORANGE_UNLIT",
              "MOROCCAN_LIGHTING_FLOOR_MOROCCAN_SHADE_ORANGE_UNLIT"),

          replace("LIGHTING_FLOOR_MOROCCAN_SHADE_RED_UNLIT",
              "MOROCCAN_LIGHTING_FLOOR_MOROCCAN_SHADE_RED_UNLIT"),

          replace("LIGHTING_FLOOR_MOROCCAN_SHADE_YELLOW_UNLIT",
              "MOROCCAN_LIGHTING_FLOOR_MOROCCAN_SHADE_YELLOW_UNLIT"),

          replace("LIGHTING_FLOOR_MOROCCAN_SHADE_BLUES_LIT",
              "MOROCCAN_LIGHTING_FLOOR_MOROCCAN_SHADE_BLUES_LIT"),

          replace("LIGHTING_FLOOR_MOROCCAN_SHADE_GOLD_BLUE_LIT",
              "MOROCCAN_LIGHTING_FLOOR_MOROCCAN_SHADE_GOLD_BLUE_LIT"),

          replace("LIGHTING_FLOOR_MOROCCAN_SHADE_ORANGE_LIT",
              "MOROCCAN_LIGHTING_FLOOR_MOROCCAN_SHADE_ORANGE_LIT"),

          replace("LIGHTING_FLOOR_MOROCCAN_SHADE_RED_LIT",
              "MOROCCAN_LIGHTING_FLOOR_MOROCCAN_SHADE_RED_LIT"),

          replace("LIGHTING_FLOOR_MOROCCAN_SHADE_YELLOW_LIT",
              "MOROCCAN_LIGHTING_FLOOR_MOROCCAN_SHADE_YELLOW_LIT"),

          replace("name=\"LIGHTING_FLOOR_QUAINT_SHADE_PINK_LIT",
              "name=\"QUAINT_LIGHTING_FLOOR_QUAINT_SHADE_PINK_LIT"),

          replace("name=\"LIGHTING_FLOOR_QUAINT_SHADE_BEIGE_LIT",
              "name=\"QUAINT_LIGHTING_FLOOR_QUAINT_SHADE_BEIGE_LIT"),

          replace("name=\"LIGHTING_FLOOR_QUAINT_SHADE_BEIGE",
              "name=\"QUAINT_LIGHTING_FLOOR_QUAINT_SHADE_BEIGE"),

          replace("name=\"LIGHTING_FLOOR_QUAINT_SHADE_WHITE",
              "name=\"QUAINT_LIGHTING_FLOOR_QUAINT_SHADE_WHITE"),

          replace("name=\"LIGHTING_FLOOR_QUAINT_SHADE_PINK",
              "name=\"QUAINT_LIGHTING_FLOOR_QUAINT_SHADE_PINK"),

          replace("name=\"LIGHTING_FLOOR_QUAINT_SHADE_BLUE",
              "name=\"QUAINT_LIGHTING_FLOOR_QUAINT_SHADE_BLUE"),

          replace("name=\"LIGHTING_FLOOR_QUAINT_SHADE_YELLOW",
              "name=\"QUAINT_LIGHTING_FLOOR_QUAINT_SHADE_YELLOW"),

          replace("name=\"LIGHTING_FLOOR_QUAINT_SHADE_GREEN",
              "name=\"QUAINT_LIGHTING_FLOOR_QUAINT_SHADE_GREEN"),

          replace("name=\"LIGHTING_FLOOR_QUAINT_SHADE_BLUE_LIT",
              "name=\"QUAINT_LIGHTING_FLOOR_QUAINT_SHADE_BLUE_LIT"),

          replace("name=\"LIGHTING_FLOOR_QUAINT_SHADE_GREEN_LIT",
              "name=\"QUAINT_LIGHTING_FLOOR_QUAINT_SHADE_GREEN_LIT"),

          replace("name=\"LIGHTING_FLOOR_QUAINT_SHADE_WHITE_LIT",
              "name=\"QUAINT_LIGHTING_FLOOR_QUAINT_SHADE_WHITE_LIT"),

          replace("name=\"LIGHTING_FLOOR_QUAINT_SHADE_YELLOW_LIT",
              "name=\"QUAINT_LIGHTING_FLOOR_QUAINT_SHADE_YELLOW_LIT"),

          replace("LIGHTING_FLOOR_STUDIO_LIGHTS_LIGHTS_LIT",
              "STUDIO_LIGHTING_FLOOR_STUDIO_LIGHTS_LIGHTS_LIT"),

          replace("LIGHTING_FLOOR_STUDIO_LIGHTS_LIGHTS_UNLIT",
              "STUDIO_LIGHTING_FLOOR_STUDIO_LIGHTS_LIGHTS_UNLIT"),

          replace("LIGHTING_FLOOR_VALUE_PAINTED_METAL_BLACKPAINT",
              "VALUE_LIGHTING_FLOOR_VALUE_PAINTED_METAL_BLACKPAINT"),

          replace("LIGHTING_FLOOR_VALUE_PAINTED_METAL_REDPAINT",
              "VALUE_LIGHTING_FLOOR_VALUE_PAINTED_METAL_REDPAINT"),

          replace("LIGHTING_FLOOR_VALUE_PAINTED_METAL_TANPAINT",
              "VALUE_LIGHTING_FLOOR_VALUE_PAINTED_METAL_TANPAINT"),

          replace("LIGHTING_FLOOR_VALUE_PAINTED_METAL_GREEN_PAINT",
              "VALUE_LIGHTING_FLOOR_VALUE_PAINTED_METAL_GREEN_PAINT"),

          replace("LIGHTING_FLOOR_VALUE_PAINTED_METAL_WHITEPAINT",
              "VALUE_LIGHTING_FLOOR_VALUE_PAINTED_METAL_WHITEPAINT"),

          replace("CHAIR_DINING_CLUB_RED_WOOD",
              "CLUB_CHAIR_DINING_CLUB_RED_WOOD"),

          replace("CHAIR_DINING_CLUB_GREENLEATH",
              "CLUB_CHAIR_DINING_CLUB_GREENLEATH"),

          replace("CHAIR_DINING_CLUB_OAKCANE",
              "CLUB_CHAIR_DINING_CLUB_OAKCANE"),

          replace("CHAIR_DINING_CLUB_LTGREENLEATH",
              "CLUB_CHAIR_DINING_CLUB_LTGREENLEATH"),

          replace("org.lgna.story.resources.prop.ClubDiningChair",
              "org.lgna.story.resources.prop.Chair"),

          replace("CHAIR_DINING_COLONIAL1_PURPLE",
              "COLONIAL_CHAIR_DINING_COLONIAL1_PURPLE"),

          replace("CHAIR_DINING_COLONIAL1_GOLD_PATTERN",
              "COLONIAL_CHAIR_DINING_COLONIAL1_GOLD_PATTERN"),

          replace("CHAIR_DINING_COLONIAL1_GOLDEN2",
              "COLONIAL_CHAIR_DINING_COLONIAL1_GOLDEN2"),

          replace("CHAIR_DINING_COLONIAL1_STRIPES",
              "COLONIAL_CHAIR_DINING_COLONIAL1_STRIPES"),

          replace("CHAIR_DINING_COLONIAL1_BLUEPATTERN",
              "COLONIAL_CHAIR_DINING_COLONIAL1_BLUEPATTERN"),

          replace("CHAIR_DINING_COLONIAL1_DIAMONDS",
              "COLONIAL_CHAIR_DINING_COLONIAL1_DIAMONDS"),

          replace("org.lgna.story.resources.prop.ParkChair",
              "org.lgna.story.resources.prop.Chair"),

          createMoreSpecificFieldRule("LOVESEAT_PARK_BENCH_OAK", "PARK_LOVESEAT_PARK_BENCH_OAK", "org.lgna.story.resources.prop.Chair"),

          createMoreSpecificFieldRule("LOVESEAT_PARK_BENCH_RED", "PARK_LOVESEAT_PARK_BENCH_RED", "org.lgna.story.resources.prop.Chair"),

          createMoreSpecificFieldRule("LOVESEAT_PARK_BENCH_OAKGREEN", "PARK_LOVESEAT_PARK_BENCH_OAKGREEN", "org.lgna.story.resources.prop.Chair"),

          createMoreSpecificFieldRule("LOVESEAT_PARK_BENCH_OAKBLUE", "PARK_LOVESEAT_PARK_BENCH_OAKBLUE", "org.lgna.story.resources.prop.Chair"),

          createMoreSpecificFieldRule("LOVESEAT_PARK_BENCH_IVORY", "PARK_LOVESEAT_PARK_BENCH_IVORY", "org.lgna.story.resources.prop.Chair"),

          replace("LOVESEAT_PARK_BENCH_WALNUT",
              "PARK_LOVESEAT_PARK_BENCH_WALNUT"),

          replace("LOVESEAT_PARK_BENCH_CHESTNUT",
              "PARK_LOVESEAT_PARK_BENCH_CHESTNUT"),

          replace("CHAIR_DINING_MODERATE_BODY_BLACK",
              "MODERATE_CHAIR_DINING_MODERATE_BODY_BLACK"),

          replace("CHAIR_DINING_MODERATE_BODY_WOOD",
              "MODERATE_CHAIR_DINING_MODERATE_BODY_WOOD"),

          replace("CHAIR_DINING_MODERATE_BODY_TEAL",
              "MODERATE_CHAIR_DINING_MODERATE_BODY_TEAL"),

          replace("CHAIR_DINING_MODERATE_BODY_RED",
              "MODERATE_CHAIR_DINING_MODERATE_BODY_RED"),

          replace("CHAIR_DINING_MODERATE_BODY_BLUE",
              "MODERATE_CHAIR_DINING_MODERATE_BODY_BLUE"),

          replace("CHAIR_DINING_MODERATE_SEAT_GRAY",
              "MODERATE_CHAIR_DINING_MODERATE_SEAT_GRAY"),

          replace("CHAIR_DINING_MODERATE_SEAT_BUMBLE",
              "MODERATE_CHAIR_DINING_MODERATE_SEAT_BUMBLE"),

          replace("CHAIR_DINING_MODERATE_SEAT_TEAL",
              "MODERATE_CHAIR_DINING_MODERATE_SEAT_TEAL"),

          replace("CHAIR_DINING_MODERATE_SEAT_STRAWBERRY",
              "MODERATE_CHAIR_DINING_MODERATE_SEAT_STRAWBERRY"),

          replace("CHAIR_DINING_MODERATE_SEAT_YELLOW",
              "MODERATE_CHAIR_DINING_MODERATE_SEAT_YELLOW"),

          replace("CHAIR_DINING_MODERATE_SEAT_BLUE",
              "MODERATE_CHAIR_DINING_MODERATE_SEAT_BLUE"),

          replace("CHAIR_DINING_MOROCCAN_SURFACES_BLUE_ORANGE",
              "MOROCCAN_CHAIR_DINING_MOROCCAN_SURFACES_BLUE_ORANGE"),

          replace("CHAIR_DINING_MOROCCAN_SURFACES_RED_CIRCLES",
              "MOROCCAN_CHAIR_DINING_MOROCCAN_SURFACES_RED_CIRCLES"),

          replace("CHAIR_DINING_MOROCCAN_SURFACES_RED_TAN",
              "MOROCCAN_CHAIR_DINING_MOROCCAN_SURFACES_RED_TAN"),

          replace("CHAIR_DINING_MOROCCAN_SURFACES_BLUE_STRIPES",
              "MOROCCAN_CHAIR_DINING_MOROCCAN_SURFACES_BLUE_STRIPES"),

          replace("CHICKENEYE_CARTOON",
              "CHICKEN"),

          replace("org.lgna.story.resources.whale.Dolphin",
              "org.lgna.story.resources.marinemammal.Dolphin"),

          replace("MANX",
              "MANX_CAT"),

          replace("GREEN_OGRE",
              "GREEN"),

          replace("org.lgna.story.resources.whale.Orca",
              "org.lgna.story.resources.marinemammal.Orca"),

          replace("ADULT_PENGUIN",
              "ADULT"),

          replace("BABY_PENGUIN",
              "BABY"),

          replace("org.lgna.story.resources.biped.FarmerPig",
              "org.lgna.story.resources.biped.Pig"),

          replace("PIG_OVERALLS",
              "PIG"),

          replace("SCOTTY",
              "SCOTTY_DOG"),

          replace("SHOE",
              "TORTOISE"),

          replace("org.lgna.story.resources.quadruped.Robot",
              "org.lgna.story.resources.quadruped.AlienRobot"),

          replace("_SUB",
              "SUBMARINE"),

          replace("SEAWEED_BACK",
              "SEAWEED3"),

          replace("SEAWEED_FRONT",
              "SEAWEED1"),

          replace("SEAWEED_MID",
              "SEAWEED2"),

          replace("QUEEN",
              "QUEEN_OF_HEARTS"),

          replace("PLAYING_CARD_TWO",
              "CARD02"),

          replace("PLAYING_CARD_TEN",
              "CARD10"),

          replace("PLAYING_CARD_THREE",
              "CARD03"),

          replace("PLAYING_CARD_ONE",
              "CARD01"),

          replace("PLAYING_CARD_SEVEN",
              "CARD07"),

          replace("PLAYING_CARD_NINE",
              "CARD09"),

          replace("PLAYING_CARD_SIX",
              "CARD06"),

          replace("PLAYING_CARD_EIGHT",
              "CARD08"),

          replace("PLAYING_CARD_FIVE",
              "CARD05"),

          replace("PLAYING_CARD_FOUR",
              "CARD04"),

          replace("name=\"PLAYING_CARD",
              "name=\"BLANK"),

          replace("ARMOIRE_LOFT_TRIM_BLACK",
              "LOFT_BLACK_TRIM"),

          replace("ARMOIRE_LOFT_TRIM_HONEY_DARK",
              "LOFT_DARK_HONEY_TRIM"),

          replace("ARMOIRE_LOFT_TRIM_SWIRLY_BROWN",
              "LOFT_SWIRLY_BROWN_TRIM"),

          replace("ARMOIRE_LOFT_TRIM_DARK_RED",
              "LOFT_DARK_RED_TRIM"),

          replace("ARMOIRE_LOFT_TRIM_MEDIUM_BROWN",
              "LOFT_MEDIUM_BROWN_TRIM"),

          replace("ARMOIRE_LOFT_SURFACES",
              "LOFT_OAK"),

          replace("ARMOIRE_QUAINT_ARMOIRE_BLUE",
              "QUAINT_BLUE"),

          replace("ARMOIRE_QUAINT_ARMOIRE_GREEN",
              "QUAINT_GREEN"),

          replace("ARMOIRE_QUAINT_ARMOIR_LEAVES",
              "QUAINT_LEAVES"),

          replace("ARMOIRE_QUAINT_ARMOIRE_RED",
              "QUAINT_RED"),

          replace("ARMOIRE_QUAINT_ARMOIRE_ROSES",
              "QUAINT_ROSES"),

          replace("ARMOIRE_CENTRAL_ASIAN_DRAGONDOOR",
              "CENTRAL_ASIAN_DRAGON"),

          replace("org.lgna.story.resources.armoire.ArmoireCentralAsian",
              "org.lgna.story.resources.prop.Armoire"),

          replace("ARMOIRE_COLONIAL_WOOD_REDWOODCURLY",
              "COLONIAL_CURLY_REDWOOD"),

          replace("ARMOIRE_COLONIAL_WOOD_DARKWOODQUILTED",
              "COLONIAL_QUILTED_DARK_WOOD"),

          replace("ARMOIRE_COLONIAL_WOOD_LIGHTWOODCURLY",
              "COLONIAL_CURLY_LIGHT_WOOD"),

          replace("name=\"DRESSER_CENTRAL_ASIAN_GREEN_FLOWERS",
              "name=\"CENTRAL_ASIAN_DRESSER_CENTRAL_ASIAN_GREEN_FLOWERS"),

          replace("name=\"DRESSER_CENTRAL_ASIAN_RED_FLOWERS",
              "name=\"CENTRAL_ASIAN_DRESSER_CENTRAL_ASIAN_RED_FLOWERS"),

          replace("name=\"DRESSER_CENTRAL_ASIAN_GREEN",
              "name=\"CENTRAL_ASIAN_DRESSER_CENTRAL_ASIAN_GREEN"),

          replace("name=\"DRESSER_CENTRAL_ASIAN_RED",
              "name=\"CENTRAL_ASIAN_DRESSER_CENTRAL_ASIAN_RED"),

          replace("org.lgna.story.resources.prop.DresserCentralAsian",
              "org.lgna.story.resources.prop.Dresser"),

          replace("name=\"DRESSER_COLONIAL_WOOD",
              "name=\"COLONIAL_DRESSER_COLONIAL_WOOD"),

          replace("name=\"DRESSER_COLONIAL_LIGHT_WOOD_CURLY",
              "name=\"COLONIAL_DRESSER_COLONIAL_LIGHT_WOOD_CURLY"),

          replace("name=\"DRESSER_COLONIAL_RED_WOOD",
              "name=\"COLONIAL_DRESSER_COLONIAL_RED_WOOD"),

          replace("name=\"DRESSER_COLONIAL_WOOD_STRAIGHT_DARK",
              "name=\"COLONIAL_DRESSER_COLONIAL_WOOD_STRAIGHT_DARK"),

          replace("org.lgna.story.resources.prop.DresserColonial",
              "org.lgna.story.resources.prop.Dresser"),

          replace("DRESSER_DESIGNER_BROWN",
              "DESIGNER_DRESSER_DESIGNER_BROWN"),

          replace("DRESSER_DESIGNER_LIGHT_WOOD",
              "DESIGNER_DRESSER_DESIGNER_LIGHT_WOOD"),

          replace("DRESSER_DESIGNER_RED",
              "DESIGNER_DRESSER_DESIGNER_RED"),

          replace("DRESSER_DESIGNER_BLACK",
              "DESIGNER_DRESSER_DESIGNER_BLACK"),

          replace("DRESSER_DESIGNER_BLUE",
              "DESIGNER_DRESSER_DESIGNER_BLUE"),

          replace("org.lgna.story.resources.prop.DresserDesigner",
              "org.lgna.story.resources.prop.Dresser"),

          replace("DRESSER_JAPANESE_TANSU_NORMAL",
              "JAPANESE_DRESSER_JAPANESE_TANSU_NORMAL"),

          replace("DRESSER_JAPANESE_TANSU_BLACK",
              "JAPANESE_DRESSER_JAPANESE_TANSU_BLACK"),

          replace("DRESSER_JAPANESE_TANSU_LIGHT",
              "JAPANESE_DRESSER_JAPANESE_TANSU_LIGHT"),

          replace("DRESSER_JAPANESE_TANSU_RED",
              "JAPANESE_DRESSER_JAPANESE_TANSU_LIGHT"),

          replace("org.lgna.story.resources.prop.DresserJapaneseTansu",
              "org.lgna.story.resources.prop.Dresser"),

          replace("BARBEQUE_VALUE_METAL_GREEN",
              "GREEN"),

          replace("BARBEQUE_VALUE_METAL_BLACK",
              "BLACK"),

          replace("BARBEQUE_VALUE_METAL_YELLO",
              "YELLOW"),

          replace("BARBEQUE_VALUE_METAL_RED",
              "RED"),

          replace("BARBEQUE_VALUE_METAL_BLUE",
              "BLUE"),

          replace("UFO_MAIN",
              "UFO"),

          replace("UFO_FRAME",
              "UFO"),

          replace("CANDY_FACTORY_ANIMATED_SURFACE",
              "CANDY_FACTORY"),

          replace("CANDY_FACTORY_LIGHT_GREEN",
              "CANDY_FACTORY"),

          replace("CANDY_FACTORY_LIGHT_OFF",
              "CANDY_FACTORY"),

          replace("CANDY_FACTORY_LIGHT_RED",
              "CANDY_FACTORY"),

          replace("TABLE_COFFEE_ART_NOUVEAU_TABLE1",
              "ART_NOVEAU_TABLE_COFFEE_ART_NOUVEAU_TABLE1"),

          replace("TABLE_COFFEE_ART_NOUVEAU_TABLE2",
              "ART_NOVEAU_TABLE_COFFEE_ART_NOUVEAU_TABLE2"),

          replace("TABLE_COFFEE_ART_NOUVEAU_TABLE3",
              "ART_NOVEAU_TABLE_COFFEE_ART_NOUVEAU_TABLE3"),

          replace("TABLE_COFFEE_CENTRAL_ASIAN_REFLECT_CHINESE_RED",
              "CENTRAL_ASIAN_TABLE_COFFEE_CENTRAL_ASIAN_REFLECT_CHINESE_RED"),

          replace("TABLE_COFFEE_CENTRAL_ASIAN_REFLECT_CHINESE_CHERRY",
              "CENTRAL_ASIAN_TABLE_COFFEE_CENTRAL_ASIAN_REFLECT_CHINESE_CHERRY"),

          replace("TABLE_COFFEE_CENTRAL_ASIAN_REFLECT_CHINESE_BLONDE",
              "CENTRAL_ASIAN_TABLE_COFFEE_CENTRAL_ASIAN_REFLECT_CHINESE_BLONDE"),

          replace("TABLE_COFFEE_CENTRAL_ASIAN_REFLECT_CHINESE_DARK",
              "CENTRAL_ASIAN_TABLE_COFFEE_CENTRAL_ASIAN_REFLECT_CHINESE_DARK"),

          replace("TABLE_COFFEE_CENTRAL_ASIAN_ASIAN_WOOD_CHINESE_CHERRY",
              "CENTRAL_ASIAN_TABLE_COFFEE_CENTRAL_ASIAN_ASIAN_WOOD_CHINESE_CHERRY"),

          replace("TABLE_COFFEE_CENTRAL_ASIAN_ASIAN_WOOD_CHINESE_BLONDE",
              "CENTRAL_ASIAN_TABLE_COFFEE_CENTRAL_ASIAN_ASIAN_WOOD_CHINESE_BLONDE"),

          replace("TABLE_COFFEE_CENTRAL_ASIAN_ASIAN_WOOD_CHINESE_RED",
              "CENTRAL_ASIAN_TABLE_COFFEE_CENTRAL_ASIAN_ASIAN_WOOD_CHINESE_RED"),

          replace("TABLE_COFFEE_CENTRAL_ASIAN_ASIAN_WOOD_CHINESE_DARK",
              "CENTRAL_ASIAN_TABLE_COFFEE_CENTRAL_ASIAN_ASIAN_WOOD_CHINESE_DARK"),

          replace("org.lgna.story.resources.prop.CentralAsianCoffeeTable",
              "org.lgna.story.resources.prop.CoffeeTable"),

          replace("TABLE_COFFEE_CLUB_RECTANGLE_WOOD",
              "LARGE_CLUB_TABLE_COFFEE_CLUB_RECTANGLE_WOOD"),

          replace("TABLE_COFFEE_CLUB_RECTANGLE_BRIDS_RED",
              "LARGE_CLUB_TABLE_COFFEE_CLUB_RECTANGLE_BRIDS_RED"),

          replace("TABLE_COFFEE_CLUB_RECTANGLE_BLEACHED_OAK",
              "LARGE_CLUB_TABLE_COFFEE_CLUB_RECTANGLE_BLEACHED_OAK"),

          replace("TABLE_COFFEE_CLUB_RECTANGLE_MAHOG",
              "LARGE_CLUB_TABLE_COFFEE_CLUB_RECTANGLE_MAHOG"),

          replace("TABLE_COFFEE_CLUB_RECTANGLE_RED_ASH",
              "LARGE_CLUB_TABLE_COFFEE_CLUB_RECTANGLE_RED_ASH"),

          replace("TABLE_COFFEE_CLUB_RECTANGLE_WHITE_OAK",
              "LARGE_CLUB_TABLE_COFFEE_CLUB_RECTANGLE_WHITE_OAK"),

          replace("TABLE_COFFEE_CLUB_RECTANGLE_LTBLUE",
              "LARGE_CLUB_TABLE_COFFEE_CLUB_RECTANGLE_LTBLUE"),

          replace("TABLE_COFFEE_END_DESIGNER__WHITE",
              "DESIGNER_TABLE_COFFEE_END_DESIGNER_WHITE"),

          replace("TABLE_COFFEE_END_DESIGNER__ASH",
              "DESIGNER_TABLE_COFFEE_END_DESIGNER_ASH"),

          replace("TABLE_END_CENTRAL_ASIAN_WOOD_BLOND_WOOD",
              "CENTRAL_ASIAN_TABLE_END_CENTRAL_ASIAN_WOOD_BLOND_WOOD"),

          replace("TABLE_END_CENTRAL_ASIAN_WOOD_ROUGH",
              "CENTRAL_ASIAN_TABLE_END_CENTRAL_ASIAN_WOOD_ROUGH"),

          replace("TABLE_END_CENTRAL_ASIAN_WOOD_BROWN",
              "CENTRAL_ASIAN_TABLE_END_CENTRAL_ASIAN_WOOD_BROWN"),

          replace("TABLE_END_CENTRAL_ASIAN_WOOD_CHERRY",
              "CENTRAL_ASIAN_TABLE_END_CENTRAL_ASIAN_WOOD_CHERRY"),

          replace("TABLE_END_CENTRAL_ASIAN_WOOD_DARK",
              "CENTRAL_ASIAN_TABLE_END_CENTRAL_ASIAN_WOOD_DARK"),

          replace("TABLE_END_CENTRAL_ASIAN_WOOD_RED_LACQUER",
              "CENTRAL_ASIAN_TABLE_END_CENTRAL_ASIAN_WOOD_RED_LACQUER"),

          replace("TABLE_END_CENTRAL_ASIAN_TABLE_TOP_ROUGH",
              "CENTRAL_ASIAN_TABLE_END_CENTRAL_ASIAN_TABLE_TOP_ROUGH"),

          replace("TABLE_END_CENTRAL_ASIAN_TABLE_TOP_BLOND_WOOD",
              "CENTRAL_ASIAN_TABLE_END_CENTRAL_ASIAN_TABLE_TOP_BLOND_WOOD"),

          replace("TABLE_END_CENTRAL_ASIAN_TABLE_TOP_CHERRY",
              "CENTRAL_ASIAN_TABLE_END_CENTRAL_ASIAN_TABLE_TOP_CHERRY"),

          replace("TABLE_END_CENTRAL_ASIAN_TABLE_TOP_RED_LACQUER",
              "CENTRAL_ASIAN_TABLE_END_CENTRAL_ASIAN_TABLE_TOP_RED_LACQUER"),

          replace("org.lgna.story.resources.prop.CentralAsianEndTable",
              "org.lgna.story.resources.prop.EndTable"),

          replace("org.lgna.story.resources.prop.ColonialEndTable",
              "org.lgna.story.resources.prop.EndTable"),

          replace("TABLE_END_OCTAGONAL_CHERRY",
              "OCTAGONAL_TABLE_END_OCTAGONAL_CHERRY"),

          replace("TABLE_END_OCTAGONAL_WHITE",
              "OCTAGONAL_TABLE_END_OCTAGONAL_WHITE"),

          replace("TABLE_END_OCTAGONAL_DARK",
              "OCTAGONAL_TABLE_END_OCTAGONAL_DARK"),

          replace("TABLE_END_OCTAGONAL_YELLOW",
              "OCTAGONAL_TABLE_END_OCTAGONAL_YELLOW"),

          replace("TABLE_END_OCTAGONAL_GREEN",
              "OCTAGONAL_TABLE_END_OCTAGONAL_GREEN"),

          replace("org.lgna.story.resources.prop.QuaintEndTable",
              "org.lgna.story.resources.prop.EndTable"),

          replace("TABLE_END_TRIANGULAR_TILE_MARBLE_GREEN",
              "TRAINGULAR_TABLE_END_TRIANGULAR_TILE_MARBLE_GREEN"),

          replace("TABLE_END_TRIANGULAR_TILE_MARBLE_CREAM",
              "TRAINGULAR_TABLE_END_TRIANGULAR_TILE_MARBLE_CREAM"),

          replace("TABLE_END_TRIANGULAR_TILE_MARBLE_RED",
              "TRAINGULAR_TABLE_END_TRIANGULAR_TILE_MARBLE_RED"),

          replace("TABLE_END_TRIANGULAR_TILE_MARBLE_WHITE",
              "TRAINGULAR_TABLE_END_TRIANGULAR_TILE_MARBLE_WHITE"),

          replace("TABLE_END_TRIANGULAR_TILE_MARBLE_BLACK",
              "TRAINGULAR_TABLE_END_TRIANGULAR_TILE_MARBLE_BLACK"),

          replace("TABLE_END_TRIANGULAR_TILE_WOOD_SANTA_MARIA",
              "TRAINGULAR_TABLE_END_TRIANGULAR_TILE_WOOD_SANTA_MARIA"),

          replace("TABLE_END_TRIANGULAR_TILE_WOOD_BLACKWOOD",
              "TRAINGULAR_TABLE_END_TRIANGULAR_TILE_WOOD_BLACKWOOD"),

          replace("TABLE_END_TRIANGULAR_TILE_WOOD_CHERRY",
              "TRAINGULAR_TABLE_END_TRIANGULAR_TILE_WOOD_CHERRY"),

          replace("TABLE_END_TRIANGULAR_TILE_WOOD_WHITE",
              "TRAINGULAR_TABLE_END_TRIANGULAR_TILE_WOOD_WHITE"),

          replace("TABLE_END_TRIANGULAR_TILE_WOOD_RED_OAK",
              "TRAINGULAR_TABLE_END_TRIANGULAR_TILE_WOOD_RED_OAK"),

          replace("org.lgna.story.resources.prop.TriangularEndTable",
              "org.lgna.story.resources.prop.EndTable"),

          replace("BOOKCASE_ART_NOUVEAU_SURFACE",
              "ART_NOUVEAU_BOOKCASE_ART_NOUVEAU_SURFACE"),

          replace("org.lgna.story.resources.prop.BookcaseArtNouveau",
              "org.lgna.story.resources.prop.Bookcase"),

          replace("BOOKCASE_CINDERBLOCK_SHELVES_BLACKWASH",
              "CINDER_BLOCK_BOOKCASE_CINDERBLOCK_SHELVES_BLACKWASH"),

          replace("BOOKCASE_CINDERBLOCK_SHELVES_KNOTTYPINE",
              "CINDER_BLOCK_BOOKCASE_CINDERBLOCK_SHELVES_KNOTTYPINE"),

          replace("BOOKCASE_CINDERBLOCK_SHELVES_OLDWOOD",
              "CINDER_BLOCK_BOOKCASE_CINDERBLOCK_SHELVES_OLDWOOD"),

          replace("org.lgna.story.resources.prop.BookcaseCinderblock",
              "org.lgna.story.resources.prop.Bookcase"),

          replace("CHAIR_LIVING_ADIRONDACK_CUSHION_GRAY",
              "ADIRONDACK_CHAIR_LIVING_ADIRONDACK_CUSHION_GRAY"),

          replace("CHAIR_LIVING_ADIRONDACK_CUSHION_CAMO",
              "ADIRONDACK_CHAIR_LIVING_ADIRONDACK_CUSHION_CAMO"),

          replace("CHAIR_LIVING_ADIRONDACK_CUSHION_STRIPES",
              "ADIRONDACK_CHAIR_LIVING_ADIRONDACK_CUSHION_STRIPES"),

          replace("CHAIR_LIVING_ADIRONDACK_CUSHION_POLKA",
              "ADIRONDACK_CHAIR_LIVING_ADIRONDACK_CUSHION_POLKA"),

          replace("CHAIR_LIVING_ADIRONDACK_CUSHION_PALM",
              "ADIRONDACK_CHAIR_LIVING_ADIRONDACK_CUSHION_PALM"),

          replace("org.lgna.story.resources.prop.LoveseatAdirondack",
              "org.lgna.story.resources.prop.Loveseat"),

          replace("LOVESEAT_ART_NOUVEAU_FRAME_ANTIQUE",
              "ART_NOUVEAU_LOVESEAT_ART_NOUVEAU_FRAME_ANTIQUE"),

          replace("LOVESEAT_ART_NOUVEAU_FRAME_MOHOGANY",
              "ART_NOUVEAU_LOVESEAT_ART_NOUVEAU_FRAME_MOHOGANY"),

          replace("LOVESEAT_ART_NOUVEAU_FRAME_OAK",
              "ART_NOUVEAU_LOVESEAT_ART_NOUVEAU_FRAME_OAK"),

          replace("org.lgna.story.resources.prop.LoveseatArtNouveau",
              "org.lgna.story.resources.prop.Loveseat"),

          replace("LOVESEAT_EXPENSIVE_CAMEL_BACK_WOOD_MAHOGONY",
              "CAMEL_BACK_LOVESEAT_EXPENSIVE_CAMEL_BACK_WOOD_MAHOGONY"),

          replace("LOVESEAT_EXPENSIVE_CAMEL_BACK_WOOD_RED",
              "CAMEL_BACK_LOVESEAT_EXPENSIVE_CAMEL_BACK_WOOD_RED"),

          replace("LOVESEAT_EXPENSIVE_CAMEL_BACK_WOOD_WHITE",
              "CAMEL_BACK_LOVESEAT_EXPENSIVE_CAMEL_BACK_WOOD_WHITE"),

          replace("LOVESEAT_EXPENSIVE_CAMEL_BACK_WOOD_LIGHT_WOOD",
              "CAMEL_BACK_LOVESEAT_EXPENSIVE_CAMEL_BACK_WOOD_LIGHT_WOOD"),

          replace("LOVESEAT_EXPENSIVE_CAMEL_BACK_CUSHION_PINK_VELOUR",
              "CAMEL_BACK_LOVESEAT_EXPENSIVE_CAMEL_BACK_CUSHION_PINK_VELOUR"),

          replace("LOVESEAT_EXPENSIVE_CAMEL_BACK_CUSHION_BLUE_VELOUR",
              "CAMEL_BACK_LOVESEAT_EXPENSIVE_CAMEL_BACK_CUSHION_BLUE_VELOUR"),

          replace("LOVESEAT_EXPENSIVE_CAMEL_BACK_CUSHION_RED_VELOUR",
              "CAMEL_BACK_LOVESEAT_EXPENSIVE_CAMEL_BACK_CUSHION_RED_VELOUR"),

          replace("LOVESEAT_EXPENSIVE_CAMEL_BACK_CUSHION_BLACK_VELOUR",
              "CAMEL_BACK_LOVESEAT_EXPENSIVE_CAMEL_BACK_CUSHION_BLACK_VELOUR"),

          replace("LOVESEAT_EXPENSIVE_CAMEL_BACK_CUSHION_BEIGE_FABRIC",
              "CAMEL_BACK_LOVESEAT_EXPENSIVE_CAMEL_BACK_CUSHION_BEIGE_FABRIC"),

          replace("LOVSEATLOFT_MODERN_FABRIC_BEIGE",
              "MODERN_LOFT_LOVSEATLOFT_MODERN_FABRIC_BEIGE"),

          replace("LOVSEATLOFT_MODERN_FABRIC_ORANGE",
              "MODERN_LOFT_LOVSEATLOFT_MODERN_FABRIC_ORANGE"),

          replace("LOVSEATLOFT_MODERN_FABRIC_WHITE",
              "MODERN_LOFT_LOVSEATLOFT_MODERN_FABRIC_WHITE"),

          replace("LOVSEATLOFT_MODERN_FABRIC_BLUE",
              "MODERN_LOFT_LOVSEATLOFT_MODERN_FABRIC_BLUE"),

          replace("LOVSEATLOFT_MODERN_FABRIC_GREEN",
              "MODERN_LOFT_LOVSEATLOFT_MODERN_FABRIC_GREEN"),

          replace("LOVSEATLOFT_MODERN_CUSHIONS_TAN",
              "MODERN_LOFT_LOVSEATLOFT_MODERN_CUSHIONS_TAN"),

          replace("LOVSEATLOFT_MODERN_CUSHIONS_GREEN",
              "MODERN_LOFT_LOVSEATLOFT_MODERN_CUSHIONS_GREEN"),

          replace("LOVSEATLOFT_MODERN_CUSHIONS_ORANGE",
              "MODERN_LOFT_LOVSEATLOFT_MODERN_CUSHIONS_ORANGE"),

          replace("LOVSEATLOFT_MODERN_CUSHIONS_RED",
              "MODERN_LOFT_LOVSEATLOFT_MODERN_CUSHIONS_RED"),

          replace("LOVSEATLOFT_MODERN_CUSHIONS_BLUE",
              "MODERN_LOFT_LOVSEATLOFT_MODERN_CUSHIONS_BLUE"),

          replace("org.lgna.story.resources.prop.LoveseatLoftModern",
              "org.lgna.story.resources.prop.Loveseat"),

          replace("SOFA_MOROCCAN_BEIGECROSS",
              "MOROCCAN_SOFA_MOROCCAN_BEIGECROSS"),

          replace("LOVESEAT_PARK_BENCH_WOOD",
              "PARK_BENCH_LOVESEAT_PARK_BENCH_WOOD"),

          replace("SOFA_QUAINT_FABRIC_WHITE_FLOWERS",
              "QUAINT_SOFA_QUAINT_FABRIC_WHITE_FLOWERS"),

          replace("SOFA_QUAINT_FABRIC_GREEN_FLOWERS",
              "QUAINT_SOFA_QUAINT_FABRIC_GREEN_FLOWERS"),

          replace("SOFA_QUAINT_FABRIC_BEIGE_FLOWERS",
              "QUAINT_SOFA_QUAINT_FABRIC_BEIGE_FLOWERS"),

          replace("SOFA_QUAINT_FABRIC_BLUE_FLOWERS",
              "QUAINT_SOFA_QUAINT_FABRIC_BLUE_FLOWERS"),

          replace("SOFA_QUAINT_FABRIC_PINK_FLOWERS",
              "QUAINT_SOFA_QUAINT_FABRIC_PINK_FLOWERS"),

          replace("DESK_CENTRAL_ASIAN_WALNUT",
              "CENTRAL_ASIAN_DESK_CENTRAL_ASIAN_WALNUT"),

          replace("DESK_CENTRAL_ASIAN_AVODIRE",
              "CENTRAL_ASIAN_DESK_CENTRAL_ASIAN_AVODIRE"),

          replace("org.lgna.story.resources.prop.DeskCentralAsian",
              "org.lgna.story.resources.prop.Desk"),

          replace("org.lgna.story.resources.prop.DeskQuaint",
              "org.lgna.story.resources.prop.Desk"),

          replace("SOFA_COLONIAL1_FRUITS",
              "COLONIAL1_SOFA_COLONIAL1_FRUITS"),

          replace("SOFA_COLONIAL1_DIAMOND",
              "COLONIAL1_SOFA_COLONIAL1_DIAMOND"),

          replace("SOFA_COLONIAL1_REDPATTERN",
              "COLONIAL1_SOFA_COLONIAL1_REDPATTERN"),

          replace("SOFA_COLONIAL1_BEIGE",
              "COLONIAL1_SOFA_COLONIAL1_BEIGE"),

          replace("SOFA_COLONIAL1_WHITE_DIAMOND",
              "COLONIAL1_SOFA_COLONIAL1_WHITE_DIAMOND"),

          replace("SOFA_COLONIAL1_LINE_CURVES",
              "COLONIAL1_SOFA_COLONIAL1_LINE_CURVES"),

          replace("SOFA_COLONIAL2_NEONBLUE",
              "COLONIAL2_SOFA_COLONIAL2_NEONBLUE"),

          replace("SOFA_COLONIAL2_GOLDDIAMOND",
              "COLONIAL2_SOFA_COLONIAL2_GOLDDIAMOND"),

          replace("SOFA_COLONIAL2_GREEN_FLORAL",
              "COLONIAL2_SOFA_COLONIAL2_GREEN_FLORAL"),

          replace("SOFA_COLONIAL2_ORANGE",
              "COLONIAL2_SOFA_COLONIAL2_ORANGE"),

          replace("SOFA_COLONIAL2_RED_STRIPES",
              "COLONIAL2_SOFA_COLONIAL2_RED_STRIPES"),

          replace("SOFA_COLONIAL2_WHITEDIAMOND",
              "COLONIAL2_SOFA_COLONIAL2_WHITEDIAMOND"),

          replace("SOFA_COLONIAL2_BLUE_PATTERN",
              "COLONIAL2_SOFA_COLONIAL2_BLUE_PATTERN"),

          replace("SOFA_MODERN_STEEL_FRAME_FABRIC_LEATHER",
              "STEEL_FRAME_SOFA_MODERN_STEEL_FRAME_FABRIC_LEATHER"),

          replace("SOFA_MODERN_STEEL_FRAME_FABRIC_BLACKLEATHER",
              "STEEL_FRAME_SOFA_MODERN_STEEL_FRAME_FABRIC_BLACKLEATHER"),

          replace("SOFA_MODERN_STEEL_FRAME_FABRIC_CORDOROY",
              "STEEL_FRAME_SOFA_MODERN_STEEL_FRAME_FABRIC_CORDOROY"),

          replace("SOFA_MODERN_STEEL_FRAME_FABRIC_STRIPE",
              "STEEL_FRAME_SOFA_MODERN_STEEL_FRAME_FABRIC_STRIPE"),

          replace("SOFA_MODERN_STEEL_FRAME_FABRIC_GATOR",
              "STEEL_FRAME_SOFA_MODERN_STEEL_FRAME_FABRIC_GATOR"),

          //duplicates
          //          "SOFA_MOROCCAN_BEIGECROSS",
          //          "MOROCCAN_SOFA_MOROCCAN_BEIGECROSS",

          //          "SOFA_QUAINT_FABRIC_WHITE_FLOWERS",
          //          "QUAINT_SOFA_QUAINT_FABRIC_WHITE_FLOWERS",
          //
          //          "SOFA_QUAINT_FABRIC_GREEN_FLOWERS",
          //          "QUAINT_SOFA_QUAINT_FABRIC_GREEN_FLOWERS",
          //
          //          "SOFA_QUAINT_FABRIC_BEIGE_FLOWERS",
          //          "QUAINT_SOFA_QUAINT_FABRIC_BEIGE_FLOWERS",
          //
          //          "SOFA_QUAINT_FABRIC_BLUE_FLOWERS",
          //          "QUAINT_SOFA_QUAINT_FABRIC_BLUE_FLOWERS",
          //
          //          "SOFA_QUAINT_FABRIC_PINK_FLOWERS",
          //          "QUAINT_SOFA_QUAINT_FABRIC_PINK_FLOWERS",

          replace("SOFA_UM_CUTOUT_BLACK_CREAM",
              "MODERN_CUTOUT_SOFA_UM_CUTOUT_BLACK_CREAM"),

          replace("SOFA_UM_CUTOUT_BLUE",
              "MODERN_CUTOUT_SOFA_UM_CUTOUT_BLUE"),

          replace("SOFA_UM_CUTOUT_LEOPARD",
              "MODERN_CUTOUT_SOFA_UM_CUTOUT_LEOPARD"),

          replace("SOFA_UM_CUTOUT_PURPLE",
              "MODERN_CUTOUT_SOFA_UM_CUTOUT_PURPLE"),

          replace("SOFA_UM_CUTOUT_RED",
              "MODERN_CUTOUT_SOFA_UM_CUTOUT_RED"),

          replace("SOFA_UM_CUTOUT_ZEBRA",
              "MODERN_CUTOUT_SOFA_UM_CUTOUT_ZEBRA"),

          replace("SOFA_UM_CUTOUT_GREEN",
              "MODERN_CUTOUT_SOFA_UM_CUTOUT_GREEN"),

          replace("SOFA_U_M_DIAMOND_CHECK",
              "MODERN_DIAMOND_SOFA_U_M_DIAMOND_CHECK"),

          replace("SOFA_U_M_DIAMOND_BABYBLUE",
              "MODERN_DIAMOND_SOFA_U_M_DIAMOND_BABYBLUE"),

          replace("SOFA_U_M_DIAMOND_BLACK",
              "MODERN_DIAMOND_SOFA_U_M_DIAMOND_BLACK"),

          replace("SOFA_U_M_DIAMOND_YELLOW",
              "MODERN_DIAMOND_SOFA_U_M_DIAMOND_YELLOW"),

          replace("SOFA_U_M_DIAMOND_PURPLE",
              "MODERN_DIAMOND_SOFA_U_M_DIAMOND_PURPLE"),

          replace("SOFA_U_M_DIAMOND_RED",
              "MODERN_DIAMOND_SOFA_U_M_DIAMOND_RED"),

          replace("LIGHTING_FLOOR_CLUB_LAMP_LAMP",
              "SWING_ARM_LIGHTING_FLOOR_CLUB_LAMP_LAMP"),

          replace("org.lgna.story.resources.prop.FloorLampSwingArm",
              "org.lgna.story.resources.prop.Lamp"),

          replace("org.lgna.story.resources.prop.FloorLampDesigner",
              "org.lgna.story.resources.prop.Lamp"),

          replace("name=\"LIGHTING_FLOOR_GARDEN_TIER_LIT",
              "name=\"GARDEN_TIER_LIGHTING_FLOOR_GARDEN_TIER_LIT"),

          replace("name=\"LIGHTING_FLOOR_GARDEN_TIER_GREEN_LIT",
              "name=\"GARDEN_TIER_LIGHTING_FLOOR_GARDEN_TIER_GREEN_LIT"),

          replace("name=\"LIGHTING_FLOOR_GARDEN_TIER_GREEN",
              "name=\"GARDEN_TIER_LIGHTING_FLOOR_GARDEN_TIER_GREEN"),

          replace("name=\"LIGHTING_FLOOR_GARDEN_TIER",
              "name=\"GARDEN_TIER_LIGHTING_FLOOR_GARDEN_TIER"),

          replace("org.lgna.story.resources.prop.FloorLampGardenBollard",
              "org.lgna.story.resources.prop.Lamp"),

          replace("org.lgna.story.resources.prop.FloorLampGardenTier",
              "org.lgna.story.resources.prop.Lamp"),

          replace("org.lgna.story.resources.prop.FloorLampLoft",
              "org.lgna.story.resources.prop.Lamp"),

          replace("org.lgna.story.resources.prop.FloorLampMoroccan",
              "org.lgna.story.resources.prop.Lamp"),

          replace("org.lgna.story.resources.prop.FloorLampQuaint",
              "org.lgna.story.resources.prop.Lamp"),

          replace("org.lgna.story.resources.prop.FloorLampStudio",
              "org.lgna.story.resources.prop.Lamp"),

          replace("org.lgna.story.resources.prop.FloorLampValue",
              "org.lgna.story.resources.prop.Lamp"),

          replace("CHAIR_DINING_ART_NOUVEAU_LIGHT_CLEAN",
              "ART_NOUVEAU_CHAIR_DINING_ART_NOUVEAU_LIGHT_CLEAN"),

          replace("CHAIR_DINING_ART_NOUVEAU_MID_CLEAN",
              "ART_NOUVEAU_CHAIR_DINING_ART_NOUVEAU_MID_CLEAN"),

          replace("CHAIR_DINING_ART_NOUVEAU_DARK_CLEAN",
              "ART_NOUVEAU_CHAIR_DINING_ART_NOUVEAU_DARK_CLEAN"),

          replace("org.lgna.story.resources.prop.ArtNouveauDiningChair",
              "org.lgna.story.resources.prop.Chair"),

          replace("org.lgna.story.resources.prop.ColonialDiningChair",
              "org.lgna.story.resources.prop.Chair"),

          replace("CHAIR_DINING_COLONIAL2_GOLDFLOWER",
              "FANCY_COLONIAL_CHAIR_DINING_COLONIAL2_GOLDFLOWER"),

          replace("CHAIR_DINING_COLONIAL2_SRIPE",
              "FANCY_COLONIAL_CHAIR_DINING_COLONIAL2_SRIPE"),

          replace("CHAIR_DINING_COLONIAL2_GOLDPATTERN",
              "FANCY_COLONIAL_CHAIR_DINING_COLONIAL2_GOLDPATTERN"),

          replace("CHAIR_DINING_COLONIAL2_BEIGE",
              "FANCY_COLONIAL_CHAIR_DINING_COLONIAL2_BEIGE"),

          replace("CHAIR_DINING_COLONIAL2_BLUEPATTERN",
              "FANCY_COLONIAL_CHAIR_DINING_COLONIAL2_BLUEPATTERN"),

          replace("CHAIR_DINING_COLONIAL2_BLUESILK",
              "FANCY_COLONIAL_CHAIR_DINING_COLONIAL2_BLUESILK"),

          replace("CHAIR_DINING_COLONIAL2_DIAMONDPATTERN",
              "FANCY_COLONIAL_CHAIR_DINING_COLONIAL2_DIAMONDPATTERN"),

          replace("org.lgna.story.resources.prop.FancyColonialDiningChair",
              "org.lgna.story.resources.prop.Chair"),

          replace("CHAIR_DINING_DANISH_MODERN_CUSHIONS_GREEN",
              "DANISH_MODERN_CHAIR_DINING_DANISH_MODERN_CUSHIONS_GREEN"),

          replace("CHAIR_DINING_DANISH_MODERN_CUSHIONS_BABY_BLUE",
              "DANISH_MODERN_CHAIR_DINING_DANISH_MODERN_CUSHIONS_BABY_BLUE"),

          replace("CHAIR_DINING_DANISH_MODERN_CUSHIONS_POLKA",
              "DANISH_MODERN_CHAIR_DINING_DANISH_MODERN_CUSHIONS_POLKA"),

          replace("CHAIR_DINING_DANISH_MODERN_CUSHIONS_WHITE",
              "DANISH_MODERN_CHAIR_DINING_DANISH_MODERN_CUSHIONS_WHITE"),

          replace("CHAIR_DINING_DANISH_MODERN_CUSHIONS_RED",
              "DANISH_MODERN_CHAIR_DINING_DANISH_MODERN_CUSHIONS_RED"),

          replace("org.lgna.story.resources.prop.DanishModernDiningChair",
              "org.lgna.story.resources.prop.Chair"),

          replace("CHAIR_DINING_LOFT_SEAT_BLUE",
              "LOFT_FORK_CHAIR_DINING_LOFT_SEAT_BLUE"),

          replace("CHAIR_DINING_LOFT_SEAT_GREEN",
              "LOFT_FORK_CHAIR_DINING_LOFT_SEAT_GREEN"),

          replace("CHAIR_DINING_LOFT_SEAT_RED",
              "LOFT_FORK_CHAIR_DINING_LOFT_SEAT_RED"),

          replace("CHAIR_DINING_LOFT_SEAT_TAN",
              "LOFT_FORK_CHAIR_DINING_LOFT_SEAT_TAN"),

          replace("CHAIR_DINING_LOFT_SEAT_ORANGE",
              "LOFT_FORK_CHAIR_DINING_LOFT_SEAT_ORANGE"),

          replace("CHAIR_DINING_LOFT_FORK_BASE_WOOD_LIGHT",
              "LOFT_FORK_CHAIR_DINING_LOFT_FORK_BASE_WOOD_LIGHT"),

          replace("CHAIR_DINING_LOFT_FORK_BASE_WOOD_ORANGE",
              "LOFT_FORK_CHAIR_DINING_LOFT_FORK_BASE_WOOD_ORANGE"),

          replace("CHAIR_DINING_LOFT_FORK_BASE_IRON",
              "LOFT_FORK_CHAIR_DINING_LOFT_FORK_BASE_IRON"),

          replace("CHAIR_DINING_LOFT_FORK_BASE_WOOD_RED",
              "LOFT_FORK_CHAIR_DINING_LOFT_FORK_BASE_WOOD_RED"),

          replace("org.lgna.story.resources.prop.LoftForkDiningChair",
              "org.lgna.story.resources.prop.Chair"),

          replace("org.lgna.story.resources.prop.LoftOfficeDiningChair",
              "org.lgna.story.resources.prop.Chair"),

          replace("org.lgna.story.resources.prop.ModerateDiningChair",
              "org.lgna.story.resources.prop.Chair"),

          replace("org.lgna.story.resources.prop.MoroccanDiningChair",
              "org.lgna.story.resources.prop.Chair"),

          replace("SAUCER_QUEEN_OF_HEARTS",
              "SAUCER_QUEEN"),

          replace("TEACUP_QUEEN_OF_HEARTS",
              "TEACUP_QUEEN"),

          replace(createPrevBipedJointString("LEFT_THUMB_1"),
              createNextBipedJointString("LEFT_THUMB")),

          replace(createPrevBipedJointString("LEFT_THUMB_2"),
              createNextBipedJointString("LEFT_THUMB_KNUCKLE")),

          replace(createPrevBipedJointString("LEFT_INDEX_1"),
              createNextBipedJointString("LEFT_INDEX_FINGER")),

          replace(createPrevBipedJointString("LEFT_INDEX_2"),
              createNextBipedJointString("LEFT_INDEX_FINGER_KNUCKLE")),

          replace(createPrevBipedJointString("LEFT_MIDDLE_1"),
              createNextBipedJointString("LEFT_MIDDLE_FINGER")),

          replace(createPrevBipedJointString("LEFT_MIDDLE_2"),
              createNextBipedJointString("LEFT_MIDDLE_FINGER_KNUCKLE")),

          replace(createPrevBipedJointString("LEFT_PINKY_1"),
              createNextBipedJointString("LEFT_PINKY_FINGER")),

          replace(createPrevBipedJointString("LEFT_PINKY_2"),
              createNextBipedJointString("LEFT_PINKY_FINGER_KNUCKLE")),

          replace(createPrevBipedJointString("RIGHT_THUMB_1"),
              createNextBipedJointString("RIGHT_THUMB")),

          replace(createPrevBipedJointString("RIGHT_THUMB_2"),
              createNextBipedJointString("RIGHT_THUMB_KNUCKLE")),

          replace(createPrevBipedJointString("RIGHT_INDEX_1"),
              createNextBipedJointString("RIGHT_INDEX_FINGER")),

          replace(createPrevBipedJointString("RIGHT_INDEX_2"),
              createNextBipedJointString("RIGHT_INDEX_FINGER_KNUCKLE")),

          replace(createPrevBipedJointString("RIGHT_MIDDLE_1"),
              createNextBipedJointString("RIGHT_MIDDLE_FINGER")),

          replace(createPrevBipedJointString("RIGHT_MIDDLE_2"),
              createNextBipedJointString("RIGHT_MIDDLE_FINGER_KNUCKLE")),

          replace(createPrevBipedJointString("RIGHT_PINKY_1"),
              createNextBipedJointString("RIGHT_PINKY_FINGER")),

          replace(createPrevBipedJointString("RIGHT_PINKY_2"),
              createNextBipedJointString("RIGHT_PINKY_FINGER_KNUCKLE")),

          replace(createPrevQuadrupedJointString("TAIL_1"),
              createNextQuadrupedJointString("TAIL")),

          replace(createPrevFlyerJointString("TAIL_1"),
              createNextFlyerJointString("TAIL")),

          replace(createJointAccessorPattern("getRightClavicle", "SFlyer"),
              createJointAccessorReplacement("getRightWingShoulder", "SFlyer")),

          replace(createJointAccessorPattern("getLeftClavicle", "SFlyer"),
              createJointAccessorReplacement("getLeftWingShoulder", "SFlyer")),

          replace(createJointAccessorPattern("getLeftShoulder", "SFlyer"),
              createJointAccessorReplacement("getLeftWingElbow", "SFlyer")),

          replace(createJointAccessorPattern("getRightShoulder", "SFlyer"),
              createJointAccessorReplacement("getRightWingElbow", "SFlyer")),

          replace(createJointAccessorPattern("getRightElbow", "SFlyer"),
              createJointAccessorReplacement("getRightWingWrist", "SFlyer")),

          replace(createJointAccessorPattern("getLeftElbow", "SFlyer"),
              createJointAccessorReplacement("getLeftWingWrist", "SFlyer")),

          replace(createJointAccessorPattern("getLeftPectoralFin", "SSwimmer"),
              createJointAccessorReplacement("getFrontRightFin", "SSwimmer")),

          replace(createJointAccessorPattern("getRightPectoralFin", "SSwimmer"),
              createJointAccessorReplacement("getFrontRightFin", "SSwimmer"))
      ),
    };
  }

  // @formatter:on

  private TextMigrationRegistryV3134() {
  }
}
