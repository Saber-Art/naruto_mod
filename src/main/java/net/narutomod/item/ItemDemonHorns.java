
package net.narutomod.item;

import net.narutomod.creativetab.TabModTab;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.util.ResourceLocation;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.Item;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;

@ElementsNarutomodMod.ModElement.Tag
public class ItemDemonHorns extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:demon_hornshelmet")
	public static final Item helmet = null;
	public ItemDemonHorns(ElementsNarutomodMod instance) {
		super(instance, 880);
	}

	@Override
	public void initElements() {
		ItemArmor.ArmorMaterial enuma = EnumHelper.addArmorMaterial("DEMON_HORNS", "narutomod:sasuke_", 25, new int[]{2, 5, 6, 2}, 9,
				(net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("")), 0f);
		elements.items.add(() -> {
			return ((Item)(new ItemWitchHat.Base(enuma) {
			}).setUnlocalizedName("demon_hornshelmet").setRegistryName("demon_hornshelmet")).setCreativeTab(TabModTab.tab);
		});
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(helmet, 0, new ModelResourceLocation("narutomod:demon_hornshelmet", "inventory"));
	}
}
