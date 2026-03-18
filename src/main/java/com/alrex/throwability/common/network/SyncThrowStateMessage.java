package com.alrex.throwability.common.network;

import com.alrex.throwability.Throwability;
import com.alrex.throwability.common.ability.IThrowabilityProvider;
import com.alrex.throwability.common.ability.RemoteThrowingAbility;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.UUID;
import java.util.function.Supplier;

public class SyncThrowStateMessage {
	private UUID senderID = null;
	private RemoteThrowingAbility.SyncedData data;

	@OnlyIn(Dist.CLIENT)
	public static void send(Player player, RemoteThrowingAbility.SyncedData syncedData) {
		SyncThrowStateMessage message = new SyncThrowStateMessage();
		message.senderID = player.getUUID();
		message.data = syncedData;
		Throwability.CHANNEL.send(PacketDistributor.SERVER.noArg(), message);
	}

	public static SyncThrowStateMessage decode(FriendlyByteBuf packet) {
		SyncThrowStateMessage message = new SyncThrowStateMessage();
		message.senderID = new UUID(packet.readLong(), packet.readLong());
		message.data = RemoteThrowingAbility.SyncedData.read(packet);
		return message;
	}

	@OnlyIn(Dist.CLIENT)
	public static void handleClient(SyncThrowStateMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
		contextSupplier.get().enqueueWork(() -> {
			if (contextSupplier.get().getDirection().getReceptionSide() == LogicalSide.CLIENT) {
				var world = Minecraft.getInstance().level;
				if (world == null) return;
				var player = world.getPlayerByUUID(message.senderID);
				if (!(player instanceof IThrowabilityProvider)) return;
				var throwingAbility = ((IThrowabilityProvider) player).getThrowAbility();
				if (!(throwingAbility instanceof RemoteThrowingAbility remoteThrowingAbility)) return;
				message.data.apply(remoteThrowingAbility);
			} else {
				Throwability.CHANNEL.send(PacketDistributor.ALL.noArg(), message);
			}
		});
		contextSupplier.get().setPacketHandled(true);
	}

	@OnlyIn(Dist.DEDICATED_SERVER)
	public static void handleServer(SyncThrowStateMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
		contextSupplier.get().enqueueWork(() -> {
			var player = contextSupplier.get().getSender();
			if (player == null) return;
			Throwability.CHANNEL.send(PacketDistributor.ALL.noArg(), message);
		});
		contextSupplier.get().setPacketHandled(true);
	}

	public void encode(FriendlyByteBuf packet) {
		packet.writeLong(senderID.getLeastSignificantBits());
		packet.writeLong(senderID.getMostSignificantBits());
		data.write(packet);
	}
}
