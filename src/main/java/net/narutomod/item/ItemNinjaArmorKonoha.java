
package net.narutomod.item;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.item.Item;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;

import net.narutomod.creativetab.TabModTab;
import net.narutomod.ElementsNarutomodMod;

@ElementsNarutomodMod.ModElement.Tag
public class ItemNinjaArmorKonoha extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:ninja_armor_konohahelmet")
	public static final Item helmet = null;
	@GameRegistry.ObjectHolder("narutomod:ninja_armor_konohabody")
	public static final Item body = null;
	@GameRegistry.ObjectHolder("narutomod:ninja_armor_konohalegs")
	public static final Item legs = null;

	public ItemNinjaArmorKonoha(ElementsNarutomodMod instance) {
		super(instance, 741);
	}

	public static ItemArmor.ArmorMaterial ENUMA = EnumHelper.addArmorMaterial("KONOHA_ARMOR", "narutomod:sasuke_",
			100, new int[]{1, 2, 3, 1}, 9, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0f);

	@Override
	public void initElements() {
		elements.items.add(() -> new Base(ENUMA, 0, EntityEquipmentSlot.HEAD) {
		}.setUnlocalizedName("ninja_armor_konohahelmet").setRegistryName("ninja_armor_konohahelmet").setCreativeTab(TabModTab.tab));
		elements.items.add(() -> new ItemNinjaArmor.Base(ItemNinjaArmor.Type.KONOHA, EntityEquipmentSlot.CHEST) {
			@Override
			protected ItemNinjaArmor.ArmorData setArmorData(ItemNinjaArmor.Type type, EntityEquipmentSlot slotIn) {
				return new Armor4Slot();
			}

			class Armor4Slot extends ItemNinjaArmor.ArmorData {
				@SideOnly(Side.CLIENT)
				@Override
				protected void init() {
					ItemNinjaArmor.ModelNinjaArmor model1 = new ItemNinjaArmor.ModelNinjaArmor(ItemNinjaArmor.Type.KONOHA);
					model1.shirt.showModel = false;
					model1.shirtRightArm.showModel = false;
					model1.shirtLeftArm.showModel = false;
					this.model = model1;
					this.texture = "narutomod:textures/konohaarmor.png";
				}
				@SideOnly(Side.CLIENT)
				@Override
				public void setSlotVisible() {
					this.model.bipedHeadwear.showModel = true;
				}
			}
		}.setUnlocalizedName("ninja_armor_konohabody").setRegistryName("ninja_armor_konohabody").setCreativeTab(TabModTab.tab));
		elements.items.add(() -> new ItemNinjaArmor.Base(ItemNinjaArmor.Type.KONOHA, EntityEquipmentSlot.LEGS) {
			@Override
			protected ItemNinjaArmor.ArmorData setArmorData(ItemNinjaArmor.Type type, EntityEquipmentSlot slotIn) {
				return new Armor4Slot();
			}

			class Armor4Slot extends ItemNinjaArmor.ArmorData {
				@SideOnly(Side.CLIENT)
				@Override
				protected void init() {
					ItemNinjaArmor.ModelNinjaArmor model1 = new ItemNinjaArmor.ModelNinjaArmor(ItemNinjaArmor.Type.KONOHA);
					model1.vest.showModel = false;
					model1.rightArmVestLayer.showModel = false;
					model1.leftArmVestLayer.showModel = false;
					this.model = model1;
					this.texture = "narutomod:textures/konohaarmor.png";
				}
				@SideOnly(Side.CLIENT)
				@Override
				public void setSlotVisible() {
					this.model.bipedRightArm.showModel = true;
					this.model.bipedLeftArm.showModel = true;
				}
			}
		}.setUnlocalizedName("ninja_armor_konohalegs").setRegistryName("ninja_armor_konohalegs").setCreativeTab(TabModTab.tab));
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(helmet, 0, new ModelResourceLocation("narutomod:ninja_armor_konohahelmet", "inventory"));
		ModelLoader.setCustomModelResourceLocation(body, 0, new ModelResourceLocation("narutomod:ninja_armor_konohabody", "inventory"));
		ModelLoader.setCustomModelResourceLocation(legs, 0, new ModelResourceLocation("narutomod:ninja_armor_konohalegs", "inventory"));
	}

	private class Base extends ItemArmor implements ItemOnBody.Interface {

		public Base(ArmorMaterial materialIn, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn) {
			super(materialIn, renderIndexIn, equipmentSlotIn);
		}

		@SideOnly(Side.CLIENT)
		private ModelBiped armorModel;
		private boolean equipped = false;

		@Override
		@SideOnly(Side.CLIENT)
		public ModelBiped getArmorModel(EntityLivingBase living, ItemStack stack, EntityEquipmentSlot slot, ModelBiped defaultModel) {
			if (this.armorModel == null) {
				this.armorModel = new ItemNinjaArmor.ModelNinjaArmor(ItemNinjaArmor.Type.KONOHA);
			}

			if (!equipped) {
				this.armorModel.bipedHeadwear.showModel = false;
				this.armorModel.bipedBody.showModel = false;
				this.armorModel.bipedLeftArm.showModel = false;
				this.armorModel.bipedRightArm.showModel = false;
				this.armorModel.bipedLeftLeg.showModel = false;
				this.armorModel.bipedRightLeg.showModel = false;
			}

			this.armorModel.isSneak = living.isSneaking();
			this.armorModel.isRiding = living.isRiding();
			this.armorModel.isChild = living.isChild();
			return this.armorModel;
		}

		@Override
		public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
			this.equipped = true;
		}

		@Override
		public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
			super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);

			this.equipped = false;
		}

		@Override
		public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {

			if (stack.hasTagCompound()) {
				NBTTagCompound tag = stack.getTagCompound();
				if (tag.hasKey("Rogue") && tag.getBoolean("Rogue")) {
					return "narutomod:textures/konohaarmor_rogue.png";
				} else if (tag.hasKey("Rogue_Itachi") && tag.getBoolean("Rogue_Itachi")) {
					return "narutomod:textures/konohaarmor_rogue_black.png";
				}
			}

			return "narutomod:textures/konohaarmor.png";
		}


		@Override
		public boolean showSkinLayer() { return true; }
		@Override
		public ItemOnBody.BodyPart showOnBody() {
			return ItemOnBody.BodyPart.HEAD;
		}

		@Override
		public ItemStack getRenderStack() { return null; }

		@Override
		public boolean canRender() { return false; }

	}
}
