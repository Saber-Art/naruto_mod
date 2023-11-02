
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
public class ItemWitchHat extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:witch_hathelmet")
	public static final Item helmet = null;
	public ItemWitchHat(ElementsNarutomodMod instance) {
		super(instance, 877);
	}

	@Override
	public void initElements() {
		ItemArmor.ArmorMaterial enuma = EnumHelper.addArmorMaterial("WITCH_HAT", "narutomod:sasuke_", 25, new int[]{2, 5, 6, 2}, 9,
				(net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("item.armor.equip_leather")),
				0f);
		elements.items.add(() -> {
			return ((Item)(new Base(enuma) {
			}).setUnlocalizedName("witch_hathelmet").setRegistryName("witch_hathelmet")).setCreativeTab(TabModTab.tab);
		});
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(helmet, 0, new ModelResourceLocation("narutomod:witch_hathelmet", "inventory"));
	}

	public static class Base extends ItemArmor implements ItemOnBody.Interface {
		public Base (ItemArmor.ArmorMaterial material) { super(material, 0, EntityEquipmentSlot.HEAD);}
		@Override
		public ItemOnBody.BodyPart showOnBody() { return ItemOnBody.BodyPart.HEAD; }
	}
}
