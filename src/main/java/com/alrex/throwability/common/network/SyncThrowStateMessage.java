package com.alrex.throwability.common.network;

import com.alrex.throwability.Throwability;
import com.alrex.throwability.common.ability.AbstractThrowingAbility;
import com.alrex.throwability.common.ability.IThrowabilityProvider;
import com.alrex.throwability.common.ability.RemoteThrowingAbility;
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
	private UUID senderID = null;
	private RemoteThrowingAbility.SyncedData data;

	@OnlyIn(Dist.CLIENT)
	public static void send(PlayerEntity player, RemoteThrowingAbility.SyncedData syncedData) {
		SyncThrowStateMessage message = new SyncThrowStateMessage();
		message.senderID = player.getUUID();
		message.data = syncedData;
		Throwability.CHANNEL.send(PacketDistributor.SERVER.noArg(), message);
	}

	public static SyncThrowStateMessage decode(PacketBuffer packet) {
		SyncThrowStateMessage message = new SyncThrowStateMessage();
		message.senderID = new UUID(packet.readLong(), packet.readLong());
		message.data = RemoteThrowingAbility.SyncedData.read(packet);
		return message;
	}

	@OnlyIn(Dist.CLIENT)
	public static void handleClient(SyncThrowStateMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
		contextSupplier.get().enqueueWork(() -> {
			if (contextSupplier.get().getDirection().getReceptionSide() == LogicalSide.CLIENT) {
				World world = Minecraft.getInstance().level;
				if (world == null) return;
				PlayerEntity player = world.getPlayerByUUID(message.senderID);
				if (!(player instanceof IThrowabilityProvider)) return;
				AbstractThrowingAbility throwingAbility = ((IThrowabilityProvider) player).getThrowAbility();
				if (!(throwingAbility instanceof RemoteThrowingAbility)) return;
				RemoteThrowingAbility remoteThrowingAbility = (RemoteThrowingAbility) throwingAbility;
				message.data.apply(remoteThrowingAbility);
			} else {
				Throwability.CHANNEL.send(PacketDistributor.ALL.noArg(), message);
			}
		});
		contextSupplier.get().setPacketHandled(true);
	}

	public void encode(PacketBuffer packet) {
		packet.writeLong(senderID.getLeastSignificantBits());
		packet.writeLong(senderID.getMostSignificantBits());
		data.write(packet);
	}

	@OnlyIn(Dist.DEDICATED_SERVER)
	public static void handleServer(SyncThrowStateMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
		contextSupplier.get().enqueueWork(() -> {
			PlayerEntity player = contextSupplier.get().getSender();
			if (player == null) return;
			Throwability.CHANNEL.send(PacketDistributor.ALL.noArg(), message);
		});
		contextSupplier.get().setPacketHandled(true);
	}
}
