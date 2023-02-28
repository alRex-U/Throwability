package com.alrex.throwability.common.network;

import com.alrex.throwability.Throwability;
import com.alrex.throwability.common.capability.IThrow;
import com.alrex.throwability.common.capability.ThrowType;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.UUID;
import java.util.function.Supplier;

public class ItemThrowMessage {
	public UUID senderID = null;
	public float throwStrength = 0;
	public int itemIndex = 0;
	public ThrowType type;

	@OnlyIn(Dist.CLIENT)
	public static void send(Player player, int itemIndex, float strength, ThrowType type) {
		ItemThrowMessage message = new ItemThrowMessage();
		message.throwStrength = strength;
		message.senderID = player.getUUID();
		message.type = type;
		message.itemIndex = itemIndex;
		Throwability.CHANNEL.send(PacketDistributor.SERVER.noArg(), message);
	}

	public static ItemThrowMessage decode(FriendlyByteBuf packet) {
		ItemThrowMessage message = new ItemThrowMessage();
		message.throwStrength = packet.readFloat();
		message.senderID = new UUID(packet.readLong(), packet.readLong());
		message.type = ThrowType.fromCode(packet.readByte());
		message.itemIndex = packet.readInt();
		return message;
	}

	@OnlyIn(Dist.CLIENT)
	public static void handleClient(ItemThrowMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
		contextSupplier.get().enqueueWork(() -> {
			Player player;
			if (contextSupplier.get().getDirection().getReceptionSide() == LogicalSide.CLIENT) {
				Level world = Minecraft.getInstance().level;
				if (world == null) return;
				player = world.getPlayerByUUID(message.senderID);
				if (player == Minecraft.getInstance().player) return;
			} else {
				player = contextSupplier.get().getSender();
			}
			if (player == null) return;
			IThrow iThrow = IThrow.get(player);
			if (iThrow == null) return;

			iThrow.throwItem(message.itemIndex, message.type, message.throwStrength);
		});
		contextSupplier.get().setPacketHandled(true);
	}

	@OnlyIn(Dist.DEDICATED_SERVER)
	public static void handleServer(ItemThrowMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
		contextSupplier.get().enqueueWork(() -> {
			Player player = contextSupplier.get().getSender();
			if (player == null) return;

			IThrow iThrow = IThrow.get(player);
			if (iThrow == null) return;
			iThrow.throwItem(message.itemIndex, message.type, message.throwStrength);
		});
		contextSupplier.get().setPacketHandled(true);
	}

	public void encode(FriendlyByteBuf packet) {
		packet.writeFloat(throwStrength);
		packet.writeLong(senderID.getLeastSignificantBits());
		packet.writeLong(senderID.getMostSignificantBits());
		packet.writeByte(type.getCode());
		packet.writeInt(itemIndex);
	}
}
