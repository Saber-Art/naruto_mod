package net.narutomod.procedure;

import net.narutomod.item.ItemJutsu;
import net.narutomod.ElementsNarutomodMod;

import net.minecraft.world.World;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;

import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedurePowerDecreasedOnKeyPressed extends ElementsNarutomodMod.ModElement {
	public ProcedurePowerDecreasedOnKeyPressed(ElementsNarutomodMod instance) {
		super(instance, 860);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("is_pressed") == null) {
			System.err.println("Failed to load dependency is_pressed for procedure PowerDecreasedOnKeyPressed!");
			return;
		}
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure PowerDecreasedOnKeyPressed!");
			return;
		}
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure PowerDecreasedOnKeyPressed!");
			return;
		}
		boolean is_pressed = (boolean) dependencies.get("is_pressed");
		Entity entity = (Entity) dependencies.get("entity");
		World world = (World) dependencies.get("world");
		double i = 0;
		ItemStack helmet = ItemStack.EMPTY;
		ItemStack itemmainhand = ItemStack.EMPTY;
		ItemStack itemoffhand = ItemStack.EMPTY;
		if ((!(world.isRemote))) {
			helmet = ((entity instanceof EntityPlayer) ? ((EntityPlayer) entity).inventory.armorInventory.get(3) : ItemStack.EMPTY);
			itemmainhand = ((entity instanceof EntityLivingBase) ? ((EntityLivingBase) entity).getHeldItemMainhand() : ItemStack.EMPTY);
			itemoffhand = ((entity instanceof EntityLivingBase) ? ((EntityLivingBase) entity).getHeldItemOffhand() : ItemStack.EMPTY);
			if (itemmainhand.getItem() instanceof ItemJutsu.Base) {
				if ((!(is_pressed))) {
					ItemJutsu.Base.switchPrevJutsu(itemmainhand, (EntityLivingBase) entity);
				}
			} else if (itemoffhand.getItem() instanceof ItemJutsu.Base) {
				if ((!(is_pressed))) {
					ItemJutsu.Base.switchPrevJutsu(itemoffhand, (EntityLivingBase) entity);
				}
			}
		}
	}
}
