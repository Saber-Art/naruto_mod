
package net.narutomod.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.narutomod.Chakra;
import net.narutomod.Particles;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.block.state.IBlockState;
import net.narutomod.entity.EntityKirin;
import net.narutomod.entity.EntityLightningArc;
import net.narutomod.procedure.ProcedureUtils;

import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class ItemSpeedy extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:speedy")
	public static final Item block = null;

	public static final ItemJutsu.JutsuEnum CHAKRAMODE = new ItemJutsu.JutsuEnum(0, "raitonchakramode", 'B', 200d, new ItemRaiton.EntityChakraMode.Jutsu());

	public ItemSpeedy(ElementsNarutomodMod instance) {
		super(instance, 853);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new ItemSpeedy.RangedItem(CHAKRAMODE));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:raiton", "inventory"));
	}
	public static class RangedItem extends ItemJutsu.Base {

		public RangedItem(ItemJutsu.JutsuEnum... list) {
			super(ItemJutsu.JutsuEnum.Type.RAITON, list);
			this.setUnlocalizedName("speedy");
			this.setRegistryName("speedy");
			this.setCreativeTab(TabModTab.tab);
			this.defaultCooldownMap[CHAKRAMODE.index] = 0;
		}

		@Override
		protected float getPower(ItemStack stack, EntityLivingBase entity, int timeLeft) {
			ItemJutsu.JutsuEnum jutsu = this.getCurrentJutsu(stack);
			return 1f;
		}

		@Override
		protected float getMaxPower(ItemStack stack, EntityLivingBase entity) {
			float f = super.getMaxPower(stack, entity);
			ItemJutsu.JutsuEnum jutsu = this.getCurrentJutsu(stack);
			return f;
		}

		@Override
		public void onUsingTick(ItemStack stack, EntityLivingBase player, int timeLeft) {
			if (!player.world.isRemote) {
				ItemJutsu.JutsuEnum jutsu = this.getCurrentJutsu(stack);
			}
			super.onUsingTick(stack, player, timeLeft);
		}
	}
}
