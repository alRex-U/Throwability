package com.alrexu.throwability.common.network.handler;

import com.alrexu.throwability.common.capability.IThrow;
import com.alrexu.throwability.common.capability.capabilities.ThrowProvider;
import com.alrexu.throwability.common.network.ItemThrowMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ItemThrowMessageHandler {
	@OnlyIn(Dist.CLIENT)
	public static void handleClient(ItemThrowMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
		contextSupplier.get().enqueueWork(() -> {
			PlayerEntity player;
			if (contextSupplier.get().getDirection().getReceptionSide() == LogicalSide.CLIENT) {
				World world = Minecraft.getInstance().world;
				if (world == null) return;
				player = world.getPlayerByUuid(message.senderID);
			} else {
				player = contextSupplier.get().getSender();
			}
			if (player == null) return;
			IThrow iThrow = ThrowProvider.get(player);
			if (iThrow == null) return;

			iThrow.throwItem(player, message.isAll, message.throwStrength);
		});
		contextSupplier.get().setPacketHandled(true);
	}

	@OnlyIn(Dist.DEDICATED_SERVER)
	public static void handleServer(ItemThrowMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
		contextSupplier.get().enqueueWork(() -> {
			PlayerEntity player = contextSupplier.get().getSender();
			if (player == null) return;

			IThrow iThrow = ThrowProvider.get(player);
			if (iThrow == null) return;
			iThrow.throwItem(player, message.isAll, message.throwStrength);
		});
		contextSupplier.get().setPacketHandled(true);
	}
}
