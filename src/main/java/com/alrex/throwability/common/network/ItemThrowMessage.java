package com.alrex.throwability.common.network;

import com.alrex.throwability.Throwability;
import com.alrex.throwability.common.ability.ThrowType;
import com.alrex.throwability.common.capability.Capabilities;
import com.alrex.throwability.common.capability.IThrowable;
import com.alrex.throwability.common.capability.throwable.StandardThrowable;
import com.alrex.throwability.utils.ThrowUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.UUID;
import java.util.function.Supplier;

public class ItemThrowMessage {
	public UUID senderID = null;
	public int chargingTick = 0;
	public int itemIndex = 0;
	public ThrowType type;

	@OnlyIn(Dist.CLIENT)
	public static void send(Player player, int itemIndex, int chargingTick, ThrowType type) {
		ItemThrowMessage message = new ItemThrowMessage();
		message.senderID = player.getUUID();
		message.chargingTick = chargingTick;
		message.type = type;
		message.itemIndex = itemIndex;
		Throwability.CHANNEL.send(PacketDistributor.SERVER.noArg(), message);
	}

	public static ItemThrowMessage decode(FriendlyByteBuf packet) {
		ItemThrowMessage message = new ItemThrowMessage();
		message.senderID = new UUID(packet.readLong(), packet.readLong());
		message.chargingTick = packet.readInt();
		message.type = ThrowType.values()[packet.readByte() % ThrowType.values().length];
		message.itemIndex = packet.readInt();
		return message;
	}

	@OnlyIn(Dist.CLIENT)
	public static void handleClient(ItemThrowMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
		contextSupplier.get().enqueueWork(() -> {
			Player player;
			if (contextSupplier.get().getDirection().getReceptionSide() == LogicalSide.CLIENT) {
				var world = Minecraft.getInstance().level;
				if (world == null) return;
				player = world.getPlayerByUUID(message.senderID);
				if (player == Minecraft.getInstance().player) return;
			} else {
				player = contextSupplier.get().getSender();
			}
			if (player == null) return;
			ItemStack stack = player.getInventory().getItem(message.itemIndex);
			if (stack.isEmpty()) return;
			IThrowable throwable = stack.getCapability(Capabilities.THROWABLE_CAPABILITY).orElseGet(StandardThrowable::getInstance);
			ThrowUtil.throwItem(player, message.itemIndex, stack, throwable, message.type, message.chargingTick);
		});
		contextSupplier.get().setPacketHandled(true);
	}

	@OnlyIn(Dist.DEDICATED_SERVER)
	public static void handleServer(ItemThrowMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
		contextSupplier.get().enqueueWork(() -> {
			var player = contextSupplier.get().getSender();
			if (player == null) return;
			ItemStack stack = player.getInventory().getItem(message.itemIndex);
			if (stack.isEmpty()) return;
			IThrowable throwable = stack.getCapability(Capabilities.THROWABLE_CAPABILITY).orElseGet(StandardThrowable::getInstance);
			ThrowUtil.throwItem(player, message.itemIndex, stack, throwable, message.type, message.chargingTick);
		});
		contextSupplier.get().setPacketHandled(true);
	}

	public void encode(FriendlyByteBuf packet) {
		packet.writeLong(senderID.getLeastSignificantBits());
		packet.writeLong(senderID.getMostSignificantBits());
		packet.writeInt(chargingTick);
		packet.writeByte(type.ordinal());
		packet.writeInt(itemIndex);
	}
}
