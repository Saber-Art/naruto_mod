package net.narutomod.procedure;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.entity.EntityHarvestScytheThrown;

import java.util.Map;
import java.util.Random;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureThrownHarvestScytheRightClickedInAir extends ElementsNarutomodMod.ModElement {
	public ProcedureThrownHarvestScytheRightClickedInAir(ElementsNarutomodMod instance) {
		super(instance, 875);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("entity") == null) {
			return;
		} else if (dependencies.get("world") == null) {
			return;
		} else {
			World world = (World) dependencies.get("world");
			Entity entity = (Entity) dependencies.get("entity");
			if (!world.isRemote) {
				EntityHarvestScytheThrown.EntityArrowCustom projectile = new EntityHarvestScytheThrown.EntityArrowCustom(world, (EntityLivingBase) entity);

				Vec3d target = ProcedureUtils.objectEntityLookingAt(entity, 200d).hitVec;
				if (target != null) {
					Vec3d vec = target.subtract(projectile.getPositionVector());
					projectile.shoot(vec.x, vec.y-1, vec.z, 2f, 0);
					projectile.setDamage(21);
					projectile.setKnockbackStrength(2);
					//world.playSound(SoundEvents.ENTITY_ARROW_SHOOT, 1, 1f / (new Random().nextFloat() * 0.5f + 1f) + 0.25f);
					world.spawnEntity(projectile);
				}
			}
		}
	}
}
