
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
public class ItemWitchHatRed extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:witch_hat_redhelmet")
	public static final Item helmet = null;
	public ItemWitchHatRed(ElementsNarutomodMod instance) {
		super(instance, 878);
	}

	@Override
	public void initElements() {
		ItemArmor.ArmorMaterial enuma = EnumHelper.addArmorMaterial("WITCH_HAT_RED", "narutomod:sasuke_", 25, new int[]{2, 5, 6, 2}, 9,
				(net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("")), 0f);
		elements.items.add(() -> {
			return ((Item)(new ItemWitchHat.Base(enuma) {
			}).setUnlocalizedName("witch_hat_redhelmet").setRegistryName("witch_hat_redhelmet")).setCreativeTab(TabModTab.tab);
		});
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(helmet, 0, new ModelResourceLocation("narutomod:witch_hat_redhelmet", "inventory"));
	}
}
