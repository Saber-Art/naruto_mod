
package net.narutomod.keybind;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.narutomod.NarutomodMod;
import net.narutomod.procedure.ProcedureChakraControlToggle;
import net.narutomod.procedure.ProcedureSpecialJutsu1OnKeyPressed;
import net.narutomod.procedure.ProcedureSync;
import org.lwjgl.input.Keyboard;

import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.client.settings.KeyBinding;

@ElementsNarutomodMod.ModElement.Tag
public class KeyBindingChakraControl extends ElementsNarutomodMod.ModElement {
	private KeyBinding keys;
	private boolean wasKeyDown;
	public KeyBindingChakraControl(ElementsNarutomodMod instance) {
		super(instance, 861);
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		elements.addNetworkMessage(KeyBindingChakraControl.KeyBindingPressedMessage.Handler.class, KeyBindingChakraControl.KeyBindingPressedMessage.class, Side.SERVER);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void init(FMLInitializationEvent event) {
		this.keys = new KeyBinding("key.mcreator.chakracontrol", Keyboard.KEY_V, "key.mcreator.category");
		ClientRegistry.registerKeyBinding(this.keys);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onClientPostTick(TickEvent.ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			Minecraft mc = Minecraft.getMinecraft();
			if (mc.currentScreen == null) {
				this.processKeyBind();
			}
			if (mc.player != null) {
				boolean flag = mc.currentScreen != null;
				if (flag != mc.player.getEntityData().getBoolean("hasAnyGuiOpen")) {
					mc.player.getEntityData().setBoolean("hasAnyGuiOpen", flag);
					ProcedureSync.EntityNBTTag.sendToServer(mc.player, "hasAnyGuiOpen", flag);
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private void processKeyBind() {
		boolean isKeyDown = this.keys.isKeyDown();
		if (isKeyDown || this.wasKeyDown) {
			NarutomodMod.PACKET_HANDLER.sendToServer(new KeyBindingChakraControl.KeyBindingPressedMessage(isKeyDown));
			EntityPlayer player = Minecraft.getMinecraft().player;
			if (player != null) {
				pressAction(player, isKeyDown);
			}
		}
		this.wasKeyDown = isKeyDown;
	}


	public static class KeyBindingPressedMessage implements IMessage {
		boolean is_pressed;
		public KeyBindingPressedMessage() {
		}

		public KeyBindingPressedMessage(boolean is_pressed) {
			this.is_pressed = is_pressed;
		}

		public static class Handler implements IMessageHandler<KeyBindingChakraControl.KeyBindingPressedMessage, IMessage> {
			@Override
			public IMessage onMessage(KeyBindingChakraControl.KeyBindingPressedMessage message, MessageContext context) {
				EntityPlayerMP entity = context.getServerHandler().player;
				entity.getServerWorld().addScheduledTask(() -> {
					pressAction(entity, message.is_pressed);
				});
				return null;
			}
		}

		public void toBytes(ByteBuf buf) {
			buf.writeBoolean(this.is_pressed);
		}

		public void fromBytes(ByteBuf buf) {
			this.is_pressed = buf.readBoolean();
		}
	}

	private static void pressAction(EntityPlayer entity, boolean is_pressed) {
		World world = entity.world;
		int x = (int) entity.posX;
		int y = (int) entity.posY;
		int z = (int) entity.posZ;
		// security measure to prevent arbitrary chunk generation
		if (!world.isBlockLoaded(new BlockPos(x, y, z)))
			return;
		{
			java.util.HashMap<String, Object> $_dependencies = new java.util.HashMap<>();
			$_dependencies.put("is_pressed", is_pressed);
			$_dependencies.put("entity", entity);
			$_dependencies.put("x", x);
			$_dependencies.put("y", y);
			$_dependencies.put("z", z);
			$_dependencies.put("world", world);
			ProcedureChakraControlToggle.executeProcedure($_dependencies);
		}
	}
}
