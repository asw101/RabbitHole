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
 *    this list of conditions and the disclaimer in the documentation
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

import static org.lgna.project.migration.ProjectMigrationTextSnippets.createMoreSpecificFieldPattern;
import static org.lgna.project.migration.ProjectMigrationTextSnippets.createMoreSpecificFieldReplacement;

final class ProjectMigrationTextMigrationFactory {
  static TextMigration createVersion3_2_110TextMigration() {
    return new TextMigration(
        new Version("3.2.110.0.0"),

        createMoreSpecificFieldPattern("OVAL", "org.lgna.story.resources.prop.SandDunesResource"),
        createMoreSpecificFieldReplacement("OVAL_DESERT", "org.lgna.story.resources.prop.SandDunesResource"),

        createMoreSpecificFieldPattern("CRESCENT", "org.lgna.story.resources.prop.SandDunesResource"),
        createMoreSpecificFieldReplacement("CRESCENT_DESERT", "org.lgna.story.resources.prop.SandDunesResource"),

        createMoreSpecificFieldPattern("BLOB", "org.lgna.story.resources.prop.SandDunesResource"),
        createMoreSpecificFieldReplacement("BLOB_DESERT", "org.lgna.story.resources.prop.SandDunesResource"),


        createMoreSpecificFieldPattern("ARCHES", "org.lgna.story.resources.prop.AncientTempleBlockResource"),
        createMoreSpecificFieldReplacement("ARCHES_INDIA_BRICK_D", "org.lgna.story.resources.prop.AncientTempleBlockResource"),

        createMoreSpecificFieldPattern("PASSAGE", "org.lgna.story.resources.prop.AncientTempleBlockResource"),
        createMoreSpecificFieldReplacement("PASSAGE_INDIA_BRICK_D", "org.lgna.story.resources.prop.AncientTempleBlockResource"),

        createMoreSpecificFieldPattern("SHELF", "org.lgna.story.resources.prop.AncientTempleBlockResource"),
        createMoreSpecificFieldReplacement("SHELF_INDIA_BRICK_D", "org.lgna.story.resources.prop.AncientTempleBlockResource"),

        createMoreSpecificFieldPattern("SOLID", "org.lgna.story.resources.prop.AncientTempleBlockResource"),
        createMoreSpecificFieldReplacement("SOLID_INDIA_BRICK_D", "org.lgna.story.resources.prop.AncientTempleBlockResource"),


        createMoreSpecificFieldPattern("END", "org.lgna.story.resources.prop.AncientTemplePieceResource"),
        createMoreSpecificFieldReplacement("END_INDIA_BRICK_D", "org.lgna.story.resources.prop.AncientTemplePieceResource"),

        createMoreSpecificFieldPattern("LEDGE", "org.lgna.story.resources.prop.AncientTemplePieceResource"),
        createMoreSpecificFieldReplacement("LEDGE_INDIA_BRICK_D", "org.lgna.story.resources.prop.AncientTemplePieceResource"),

        createMoreSpecificFieldPattern("LEDGE_AND_STAIRS", "org.lgna.story.resources.prop.AncientTemplePieceResource"),
        createMoreSpecificFieldReplacement("LEDGE_AND_STAIRS_INDIA_BRICK_D", "org.lgna.story.resources.prop.AncientTemplePieceResource"),

        createMoreSpecificFieldPattern("PLAZA", "org.lgna.story.resources.prop.AncientTemplePieceResource"),
        createMoreSpecificFieldReplacement("PLAZA_INDIA_BRICK_D", "org.lgna.story.resources.prop.AncientTemplePieceResource"),

        createMoreSpecificFieldPattern("ROOM", "org.lgna.story.resources.prop.AncientTemplePieceResource"),
        createMoreSpecificFieldReplacement("ROOM_INDIA_BRICK_D", "org.lgna.story.resources.prop.AncientTemplePieceResource"),

        createMoreSpecificFieldPattern("STACK", "org.lgna.story.resources.prop.AncientTemplePieceResource"),
        createMoreSpecificFieldReplacement("STACK_INDIA_BRICK_D", "org.lgna.story.resources.prop.AncientTemplePieceResource"),

        createMoreSpecificFieldPattern("STAIRS", "org.lgna.story.resources.prop.AncientTemplePieceResource"),
        createMoreSpecificFieldReplacement("STAIRS_INDIA_BRICK_D", "org.lgna.story.resources.prop.AncientTemplePieceResource"),


        createMoreSpecificFieldPattern("DEFAULT", "org.lgna.story.resources.prop.AncientTempleArchResource"),
        createMoreSpecificFieldReplacement("INDIA_BRICK_D", "org.lgna.story.resources.prop.AncientTempleArchResource"),


        createMoreSpecificFieldPattern("DEFAULT", "org.lgna.story.resources.prop.AncientTemplePillarResource"),
        createMoreSpecificFieldReplacement("INDIA_BRICK_D", "org.lgna.story.resources.prop.AncientTemplePillarResource"),


        createMoreSpecificFieldPattern("DEFAULT", "org.lgna.story.resources.prop.AncientTempleWallResource"),
        createMoreSpecificFieldReplacement("INDIA_BRICK_D", "org.lgna.story.resources.prop.AncientTempleWallResource"),


        createMoreSpecificFieldPattern("DEFAULT", "org.lgna.story.resources.prop.AncientTempleWellResource"),
        createMoreSpecificFieldReplacement("INDIA_BRICK_D", "org.lgna.story.resources.prop.AncientTempleWellResource"),


        createMoreSpecificFieldPattern("NO_WATER", "org.lgna.story.resources.prop.WaterTankResource"),
        createMoreSpecificFieldReplacement("NO_WATER_INDIA_WATER_TANK", "org.lgna.story.resources.prop.WaterTankResource"),

        createMoreSpecificFieldPattern("WATER", "org.lgna.story.resources.prop.WaterTankResource"),
        createMoreSpecificFieldReplacement("WATER_INDIA_WATER_TANK", "org.lgna.story.resources.prop.WaterTankResource"),


        createMoreSpecificFieldPattern("DEFAULT", "org.lgna.story.resources.prop.WaterTankPillarResource"),
        createMoreSpecificFieldReplacement("INDIA_WATER_TANK", "org.lgna.story.resources.prop.WaterTankPillarResource"),


        createMoreSpecificFieldPattern("DEFAULT", "org.lgna.story.resources.prop.WaterTankShrineResource"),
        createMoreSpecificFieldReplacement("INDIA_WATER_TANK", "org.lgna.story.resources.prop.WaterTankShrineResource"),


        createMoreSpecificFieldPattern("DEFAULT", "org.lgna.story.resources.prop.WaterTankTowerResource"),
        createMoreSpecificFieldReplacement("INDIA_WATER_TANK", "org.lgna.story.resources.prop.WaterTankTowerResource"),


        createMoreSpecificFieldPattern("ARCH", "org.lgna.story.resources.prop.WaterTankWallResource"),
        createMoreSpecificFieldReplacement("ARCH_INDIA_WATER_TANK", "org.lgna.story.resources.prop.WaterTankWallResource"),

        createMoreSpecificFieldPattern("CIRCLE", "org.lgna.story.resources.prop.WaterTankWallResource"),
        createMoreSpecificFieldReplacement("CIRCLE_INDIA_WATER_TANK", "org.lgna.story.resources.prop.WaterTankWallResource"),


        createMoreSpecificFieldPattern("NO_WATER", "org.lgna.story.resources.prop.WaterTankWellResource"),
        createMoreSpecificFieldReplacement("NO_WATER_INDIA_WATER_TANK", "org.lgna.story.resources.prop.WaterTankWellResource"),

        createMoreSpecificFieldPattern("WATER", "org.lgna.story.resources.prop.WaterTankWellResource"),
        createMoreSpecificFieldReplacement("WATER_INDIA_WATER_TANK", "org.lgna.story.resources.prop.WaterTankWellResource")
        );
  }

  private ProjectMigrationTextMigrationFactory() {
  }
}
