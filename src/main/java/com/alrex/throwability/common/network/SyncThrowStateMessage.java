package com.alrex.throwability.common.network;

import com.alrex.throwability.Throwability;
import com.alrex.throwability.common.capability.IThrow;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.UUID;
import java.util.function.Supplier;

public class SyncThrowStateMessage {
	public UUID senderID = null;
	public float throwStrength = 0;
	public boolean isCharging = false;

	@OnlyIn(Dist.CLIENT)
	public static void send(PlayerEntity player, IThrow iThrow) {
		SyncThrowStateMessage message = new SyncThrowStateMessage();
		message.throwStrength = iThrow.getChargingPower();
		message.senderID = player.getUUID();
		message.isCharging = iThrow.isCharging();
		Throwability.CHANNEL.send(PacketDistributor.SERVER.noArg(), message);
	}

	public static SyncThrowStateMessage decode(PacketBuffer packet) {
		SyncThrowStateMessage message = new SyncThrowStateMessage();
		message.throwStrength = packet.readFloat();
		message.senderID = new UUID(packet.readLong(), packet.readLong());
		message.isCharging = packet.readBoolean();
		return message;
	}

	public static void handleClient(SyncThrowStateMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
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
			IThrow iThrow = IThrow.get(player);
			if (iThrow == null) return;
			iThrow.setPower(message.throwStrength);
			iThrow.setCharging(message.isCharging);
		});
		contextSupplier.get().setPacketHandled(true);
	}

	@OnlyIn(Dist.DEDICATED_SERVER)
	public static void handleServer(SyncThrowStateMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
		contextSupplier.get().enqueueWork(() -> {
			PlayerEntity player = contextSupplier.get().getSender();
			if (player == null) return;

			IThrow iThrow = IThrow.get(player);
			if (iThrow == null) return;
			iThrow.setPower(message.throwStrength);
			Throwability.CHANNEL.send(PacketDistributor.ALL.noArg(), message);
		});
		contextSupplier.get().setPacketHandled(true);
	}

	public void encode(PacketBuffer packet) {
		packet.writeFloat(throwStrength);
		packet.writeLong(senderID.getLeastSignificantBits());
		packet.writeLong(senderID.getMostSignificantBits());
		packet.writeBoolean(isCharging);
	}
}
