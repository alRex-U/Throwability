package com.alrexu.throwability.common.network;

import com.alrexu.throwability.ThrowabilityMod;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.UUID;

public class ItemThrowMessage {
	public UUID senderID = null;
	public float throwStrength = 0;
	public boolean isAll = false;

	public static ItemThrowMessage decode(PacketBuffer packet) {
		ItemThrowMessage message = new ItemThrowMessage();
		message.throwStrength = packet.readFloat();
		message.senderID = new UUID(packet.readLong(), packet.readLong());
		message.isAll = packet.readBoolean();
		return message;
	}

	@OnlyIn(Dist.CLIENT)
	public static void send(PlayerEntity player, float throwStrength, boolean isAll) {
		ItemThrowMessage message = new ItemThrowMessage();
		message.throwStrength = throwStrength;
		message.senderID = player.getUniqueID();
		message.isAll = isAll;
		ThrowabilityMod.CHANNEL.send(PacketDistributor.SERVER.noArg(), message);
	}

	public void encode(PacketBuffer packet) {
		packet.writeFloat(throwStrength);
		packet.writeLong(senderID.getLeastSignificantBits());
		packet.writeLong(senderID.getMostSignificantBits());
		packet.writeBoolean(isAll);
	}
}
