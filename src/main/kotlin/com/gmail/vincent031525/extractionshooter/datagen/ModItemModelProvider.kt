package com.gmail.vincent031525.extractionshooter.datagen

import com.gmail.vincent031525.extractionshooter.Extractionshooter
import com.gmail.vincent031525.extractionshooter.registry.ModItems
import net.minecraft.client.data.models.BlockModelGenerators
import net.minecraft.client.data.models.ItemModelGenerators
import net.minecraft.client.data.models.ModelProvider
import net.minecraft.client.data.models.model.ItemModelUtils
import net.minecraft.client.data.models.model.ModelLocationUtils
import net.minecraft.client.data.models.model.ModelTemplates
import net.minecraft.client.data.models.model.TextureMapping
import net.minecraft.data.PackOutput
import net.minecraft.resources.Identifier
import software.bernie.geckolib.renderer.internal.GeckolibItemSpecialRenderer.Unbaked

@Suppress("UnstableApiUsage")
class ModItemModelProvider(output: PackOutput) : ModelProvider(output, Extractionshooter.ID) {

    override fun registerModels(blockModels: BlockModelGenerators, itemModels: ItemModelGenerators) {
        val m4a1Model =
            ItemModelUtils.specialModel(ModelLocationUtils.getModelLocation(ModItems.M4A1_ITEM.get()), Unbaked())
        itemModels.itemModelOutput.accept(ModItems.M4A1_ITEM.get(), m4a1Model)
        itemModels.itemModelOutput.accept(
            ModItems.MAG_30_ITEM.get(), ItemModelUtils.plainModel(
                ModelTemplates.FLAT_ITEM.create(
                    ModItems.MAG_30_ITEM.get(),
                    TextureMapping.layer0(Identifier.fromNamespaceAndPath(Extractionshooter.ID, "item/mag/30")),
                    itemModels.modelOutput
                )
            )
        )
        itemModels.itemModelOutput.accept(
            ModItems.MAG_45_ITEM.get(), ItemModelUtils.plainModel(
                ModelTemplates.FLAT_ITEM.create(
                    ModItems.MAG_45_ITEM.get(),
                    TextureMapping.layer0(Identifier.fromNamespaceAndPath(Extractionshooter.ID, "item/mag/45")),
                    itemModels.modelOutput
                )
            )
        )
        itemModels.itemModelOutput.accept(
            ModItems.MAG_60_ITEM.get(), ItemModelUtils.plainModel(
                ModelTemplates.FLAT_ITEM.create(
                    ModItems.MAG_60_ITEM.get(),
                    TextureMapping.layer0(Identifier.fromNamespaceAndPath(Extractionshooter.ID, "item/mag/60")),
                    itemModels.modelOutput
                )
            )
        )
        itemModels.itemModelOutput.accept(
            ModItems.AMMO_556_ITEM.get(), ItemModelUtils.plainModel(
                ModelTemplates.FLAT_ITEM.create(
                    ModItems.AMMO_556_ITEM.get(),
                    TextureMapping.layer0(Identifier.fromNamespaceAndPath(Extractionshooter.ID, "item/ammo/556")),
                    itemModels.modelOutput
                )
            )
        )
        itemModels.itemModelOutput.accept(
            ModItems.BANDAGE_ITEM.get(), ItemModelUtils.plainModel(
                ModelTemplates.FLAT_ITEM.create(
                    ModItems.BANDAGE_ITEM.get(),
                    TextureMapping.layer0(
                        Identifier.fromNamespaceAndPath(
                            Extractionshooter.ID,
                            "item/medical/bandage"
                        )
                    ),
                    itemModels.modelOutput
                )
            )
        )
        itemModels.itemModelOutput.accept(
            ModItems.SPLINT_ITEM.get(), ItemModelUtils.plainModel(
                ModelTemplates.FLAT_ITEM.create(
                    ModItems.SPLINT_ITEM.get(),
                    TextureMapping.layer0(Identifier.fromNamespaceAndPath(Extractionshooter.ID, "item/medical/splint")),
                    itemModels.modelOutput
                )
            )
        )
        itemModels.itemModelOutput.accept(
            ModItems.PAINKILLERS_ITEM.get(), ItemModelUtils.plainModel(
                ModelTemplates.FLAT_ITEM.create(
                    ModItems.PAINKILLERS_ITEM.get(),
                    TextureMapping.layer0(
                        Identifier.fromNamespaceAndPath(
                            Extractionshooter.ID,
                            "item/medical/painkillers"
                        )
                    ),
                    itemModels.modelOutput
                )
            )
        )
        itemModels.itemModelOutput.accept(
            ModItems.SURGERY_KIT_ITEM.get(), ItemModelUtils.plainModel(
                ModelTemplates.FLAT_ITEM.create(
                    ModItems.SURGERY_KIT_ITEM.get(),
                    TextureMapping.layer0(
                        Identifier.fromNamespaceAndPath(
                            Extractionshooter.ID,
                            "item/medical/surger_kit"
                        )
                    ),
                    itemModels.modelOutput
                )
            )
        )
        itemModels.itemModelOutput.accept(
            ModItems.MEDKIT_SMALL_ITEM.get(), ItemModelUtils.plainModel(
                ModelTemplates.FLAT_ITEM.create(
                    ModItems.MEDKIT_SMALL_ITEM.get(),
                    TextureMapping.layer0(
                        Identifier.fromNamespaceAndPath(
                            Extractionshooter.ID,
                            "item/medical/medkit_small"
                        )
                    ),
                    itemModels.modelOutput
                )
            )
        )
        itemModels.itemModelOutput.accept(
            ModItems.MEDKIT_LARGE_ITEM.get(), ItemModelUtils.plainModel(
                ModelTemplates.FLAT_ITEM.create(
                    ModItems.MEDKIT_LARGE_ITEM.get(),
                    TextureMapping.layer0(
                        Identifier.fromNamespaceAndPath(
                            Extractionshooter.ID,
                            "item/medical/medkit_large"
                        )
                    ),
                    itemModels.modelOutput
                )
            )
        )
        itemModels.itemModelOutput.accept(
            ModItems.BACKPACK_ITEM.get(), ItemModelUtils.plainModel(
                ModelTemplates.FLAT_ITEM.create(
                    ModItems.BACKPACK_ITEM.get(),
                    TextureMapping.layer0(
                        Identifier.fromNamespaceAndPath(
                            Extractionshooter.ID,
                            "item/equipment/backpack"
                        )
                    ),
                    itemModels.modelOutput
                )
            )
        )
        itemModels.itemModelOutput.accept(
            ModItems.RIG_ITEM.get(), ItemModelUtils.plainModel(
                ModelTemplates.FLAT_ITEM.create(
                    ModItems.RIG_ITEM.get(),
                    TextureMapping.layer0(
                        Identifier.fromNamespaceAndPath(
                            Extractionshooter.ID,
                            "item/equipment/rig"
                        )
                    ),
                    itemModels.modelOutput
                )
            )
        )
    }
}