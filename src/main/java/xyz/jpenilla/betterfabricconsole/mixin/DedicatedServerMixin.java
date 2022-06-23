/*
 * This file is part of Better Fabric Console, licensed under the MIT License.
 *
 * Copyright (c) 2021-2022 Jason Penilla
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package xyz.jpenilla.betterfabricconsole.mixin;

import com.mojang.datafixers.DataFixer;
import java.net.Proxy;
import java.util.function.Function;
import net.kyori.adventure.platform.fabric.FabricServerAudiences;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Services;
import net.minecraft.server.WorldStem;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.jpenilla.betterfabricconsole.adventure.LoggingComponentSerializerHolder;

@Mixin(DedicatedServer.class)
abstract class DedicatedServerMixin extends MinecraftServer implements LoggingComponentSerializerHolder {
  @Final @Shadow static Logger LOGGER;

  private final FabricServerAudiences audiences = FabricServerAudiences.of(this);
  private final LegacyComponentSerializer legacySerializer = LegacyComponentSerializer.builder()
    .flattener(this.audiences.flattener())
    .hexColors()
    .character(LegacyComponentSerializer.SECTION_CHAR)
    .hexCharacter(LegacyComponentSerializer.HEX_CHAR)
    .build();

  DedicatedServerMixin(final Thread thread, final LevelStorageSource.LevelStorageAccess levelStorageAccess, final PackRepository packRepository, final WorldStem worldStem, final Proxy proxy, final DataFixer dataFixer, final Services services, final ChunkProgressListenerFactory chunkProgressListenerFactory) {
    super(thread, levelStorageAccess, packRepository, worldStem, proxy, dataFixer, services, chunkProgressListenerFactory);
  }

  @Override
  public Function<net.kyori.adventure.text.Component, String> loggingComponentSerializer() {
    return this.legacySerializer::serialize;
  }

  @Override
  public void sendSystemMessage(final @NonNull Component component) {
    LOGGER.info(this.legacySerializer.serialize(component.asComponent()));
  }
}
