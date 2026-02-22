package com.alrex.throwability.common.network;

import com.alrex.throwability.Throwability;
import com.alrex.throwability.common.ability.ThrowType;
import com.alrex.throwability.common.capability.Capabilities;
import com.alrex.throwability.common.capability.IThrowable;
import com.alrex.throwability.common.capability.throwable.StandardThrowable;
import com.alrex.throwability.utils.ThrowUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.UUID;
import java.util.function.Supplier;

public class ItemThrowMessage {
	public UUID senderID = null;
	public int chargingTick = 0;
	public int itemIndex = 0;
	public ThrowType type;

	@OnlyIn(Dist.CLIENT)
	public static void send(PlayerEntity player, int itemIndex, int chargingTick, ThrowType type) {
		ItemThrowMessage message = new ItemThrowMessage();
		message.senderID = player.getUUID();
		message.chargingTick = chargingTick;
		message.type = type;
		message.itemIndex = itemIndex;
		Throwability.CHANNEL.send(PacketDistributor.SERVER.noArg(), message);
	}

	public static ItemThrowMessage decode(PacketBuffer packet) {
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
			PlayerEntity player;
			if (contextSupplier.get().getDirection().getReceptionSide() == LogicalSide.CLIENT) {
				World world = Minecraft.getInstance().level;
				if (world == null) return;
				player = world.getPlayerByUUID(message.senderID);
				if (player == Minecraft.getInstance().player) return;
			} else {
				player = contextSupplier.get().getSender();
			}
			if (player == null) return;
			ItemStack stack = player.inventory.getItem(message.itemIndex);
			if (stack.isEmpty()) return;
			IThrowable throwable = stack.getCapability(Capabilities.THROWABLE_CAPABILITY).orElseGet(StandardThrowable::getInstance);
			ThrowUtil.throwItem(player, message.itemIndex, stack, throwable, message.type, message.chargingTick);
		});
		contextSupplier.get().setPacketHandled(true);
	}

	@OnlyIn(Dist.DEDICATED_SERVER)
	public static void handleServer(ItemThrowMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
		contextSupplier.get().enqueueWork(() -> {
			PlayerEntity player = contextSupplier.get().getSender();
			if (player == null) return;
			ItemStack stack = player.inventory.getItem(message.itemIndex);
			if (stack.isEmpty()) return;
			IThrowable throwable = stack.getCapability(Capabilities.THROWABLE_CAPABILITY).orElseGet(StandardThrowable::getInstance);
			ThrowUtil.throwItem(player, message.itemIndex, stack, throwable, message.type, message.chargingTick);
		});
		contextSupplier.get().setPacketHandled(true);
	}

	public void encode(PacketBuffer packet) {
		packet.writeLong(senderID.getLeastSignificantBits());
		packet.writeLong(senderID.getMostSignificantBits());
		packet.writeInt(chargingTick);
		packet.writeByte(type.ordinal());
		packet.writeInt(itemIndex);
	}
}
