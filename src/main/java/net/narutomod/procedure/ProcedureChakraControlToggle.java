package net.narutomod.procedure;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.narutomod.ElementsNarutomodMod;

import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureChakraControlToggle extends ElementsNarutomodMod.ModElement {
	public ProcedureChakraControlToggle(ElementsNarutomodMod instance) {
		super(instance, 862);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("is_pressed") == null) {
			System.err.println("Failed to load dependency is_pressed for procedure PowerIncreaseOnKeyPressed!");
			return;
		}
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure PowerIncreaseOnKeyPressed!");
			return;
		}
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure PowerIncreaseOnKeyPressed!");
			return;
		}
		boolean is_pressed = (boolean) dependencies.get("is_pressed");
		Entity entity = (Entity) dependencies.get("entity");
		World world = (World) dependencies.get("world");

		if (!world.isRemote) {
			NBTTagCompound entityData = entity.getEntityData();
			if (!entityData.hasKey("ChakraControl")) {
				entityData.setBoolean("ChakraControl", true);
			}

			if (is_pressed) {
				entityData.setBoolean("ChakraControl", entityData.getBoolean("ChakrControl"));
			}
		}
	}
}
