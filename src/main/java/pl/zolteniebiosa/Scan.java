package pl.zolteniebiosa;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

// Szybkie szukanie blokow po zaladowanych chunkach (pomija sekcje bez szukanych blokow).
public class Scan {
    public static List<BlockPos> find(ClientWorld w, BlockPos center, int radius, Set<Block> wanted, int max) {
        List<BlockPos> out = new ArrayList<>();
        if (w == null || wanted.isEmpty()) return out;
        Predicate<BlockState> pred = s -> wanted.contains(s.getBlock());
        int cr = (radius >> 4) + 1;
        int ccx = center.getX() >> 4, ccz = center.getZ() >> 4;
        for (int dx = -cr; dx <= cr; dx++) {
            for (int dz = -cr; dz <= cr; dz++) {
                WorldChunk ch = w.getChunkManager().getWorldChunk(ccx + dx, ccz + dz);
                if (ch == null) continue;
                ChunkSection[] secs = ch.getSectionArray();
                int bottom = ch.getBottomSectionCoord();
                for (int i = 0; i < secs.length; i++) {
                    ChunkSection sec = secs[i];
                    if (sec == null || sec.isEmpty() || !sec.hasAny(pred)) continue;
                    int baseY = (bottom + i) << 4;
                    int baseX = (ccx + dx) << 4, baseZ = (ccz + dz) << 4;
                    for (int lx = 0; lx < 16; lx++) {
                        int x = baseX + lx;
                        if (Math.abs(x - center.getX()) > radius) continue;
                        for (int lz = 0; lz < 16; lz++) {
                            int z = baseZ + lz;
                            if (Math.abs(z - center.getZ()) > radius) continue;
                            for (int ly = 0; ly < 16; ly++) {
                                if (wanted.contains(sec.getBlockState(lx, ly, lz).getBlock())) {
                                    out.add(new BlockPos(x, baseY + ly, z));
                                    if (out.size() >= max) return out;
                                }
                            }
                        }
                    }
                }
            }
        }
        return out;
    }
}
