package pl.zolteniebiosa;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.HashSet;
import java.util.Set;

// Wbudowany "Baritone" - uproszczony automatyczny kopacz (bez prawdziwego pathfindingu)
public class Miner {
    public static boolean active = false;
    private static BlockPos target = null;
    private static int tick = 0, still = 0;
    private static Vec3d lastPos = Vec3d.ZERO;
    private static Direction heading = Direction.NORTH;
    private static final Set<BlockPos> ignored = new HashSet<>();

    private static void msg(String s) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player != null) mc.player.sendMessage(Text.literal("§e[ZolteNiebiosa] " + s), false);
    }

    public static void start() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;
        active = true;
        target = null;
        tick = 0;
        still = 0;
        ignored.clear();
        heading = mc.player.getHorizontalFacing();
        msg("§aKopacz włączony. Trzymaj kilof w ręce. Wyłączasz go przełącznikiem w menu (N).");
    }

    public static void stop() {
        active = false;
        release();
    }

    private static void off(String why) {
        stop();
        Mods.BARITONE.enabled = false;
        msg("§c" + why);
    }

    private static void release() {
        MinecraftClient mc = MinecraftClient.getInstance();
        mc.options.forwardKey.setPressed(false);
        mc.options.attackKey.setPressed(false);
        mc.options.jumpKey.setPressed(false);
    }

    private static Set<Block> resolve(String txt) {
        Set<Block> r = new HashSet<>();
        for (String id : txt.split(",")) {
            id = id.trim().toLowerCase();
            if (id.isEmpty()) continue;
            Identifier i = Identifier.tryParse(id.contains(":") ? id : "minecraft:" + id);
            if (i != null && Registries.BLOCK.containsId(i)) r.add(Registries.BLOCK.get(i));
        }
        return r;
    }

    private static boolean solid(MinecraftClient mc, BlockPos b) {
        BlockState s = mc.world.getBlockState(b);
        return !s.getCollisionShape(mc.world, b).isEmpty();
    }

    private static boolean lava(MinecraftClient mc, BlockPos b) {
        return mc.world.getBlockState(b).getFluidState().isIn(FluidTags.LAVA);
    }

    private static void findTarget(MinecraftClient mc, BlockPos pos) {
        target = null;
        Set<Block> want = resolve(Mods.BARITONE.txt("block"));
        if (want.isEmpty()) return;
        int r = Mods.BARITONE.n("radius");
        int minY = Math.max(mc.world.getBottomY(), pos.getY() - r);
        int maxY = Math.min(mc.world.getTopYInclusive(), pos.getY() + 3);
        double best = Double.MAX_VALUE;
        BlockPos.Mutable mp = new BlockPos.Mutable();
        for (int x = pos.getX() - r; x <= pos.getX() + r; x++) {
            for (int z = pos.getZ() - r; z <= pos.getZ() + r; z++) {
                for (int y = minY; y <= maxY; y++) {
                    mp.set(x, y, z);
                    if (!want.contains(mc.world.getBlockState(mp).getBlock())) continue;
                    BlockPos im = mp.toImmutable();
                    if (ignored.contains(im)) continue;
                    double d = im.getSquaredDistance(pos);
                    if (d < best) {
                        best = d;
                        target = im;
                    }
                }
            }
        }
    }

    private static void aim(ClientPlayerEntity p, Vec3d to) {
        Vec3d e = p.getEyePos();
        double dx = to.x - e.x, dy = to.y - e.y, dz = to.z - e.z;
        double h = Math.sqrt(dx * dx + dz * dz);
        float yaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
        float pitch = (float) -Math.toDegrees(Math.atan2(dy, h));
        p.setYaw(MathHelper.lerpAngleDegrees(0.6f, p.getYaw(), yaw));
        p.setPitch(MathHelper.lerp(0.6f, p.getPitch(), pitch));
    }

    private static void mine(MinecraftClient mc, ClientPlayerEntity p, BlockPos b) {
        BlockState st = mc.world.getBlockState(b);
        if (st.getHardness(mc.world, b) < 0) {
            off("Tego nie da się wykopać: " + st.getBlock().getName().getString());
            return;
        }
        aim(p, Vec3d.ofCenter(b));
        mc.options.forwardKey.setPressed(false);
        mc.options.jumpKey.setPressed(false);
        mc.options.attackKey.setPressed(true);
    }

    public static void tick(MinecraftClient mc) {
        if (!active) return;
        ClientPlayerEntity p = mc.player;
        if (p == null || mc.world == null || mc.interactionManager == null) {
            active = false;
            Mods.BARITONE.enabled = false;
            return;
        }
        if (mc.currentScreen != null && !(mc.currentScreen instanceof MenuScreen)) {
            release();
            return;
        }
        boolean safe = Mods.BARITONE.b("safe");
        if (safe && p.getHealth() < 8f) {
            off("Za mało życia - kopacz zatrzymany.");
            return;
        }
        if (p.getInventory().getEmptySlot() == -1) {
            off("Pełny ekwipunek - kopacz zatrzymany.");
            return;
        }
        tick++;
        BlockPos pos = p.getBlockPos();
        if (target == null || mc.world.getBlockState(target).isAir() || tick % 40 == 1) findTarget(mc, pos);

        mc.options.attackKey.setPressed(false);

        // cel w zasiegu ramienia: celuj i kop (po drodze zniknie to, co zasłania)
        if (target != null) {
            Vec3d center = Vec3d.ofCenter(target);
            if (p.getEyePos().distanceTo(center) <= 4.2) {
                mine(mc, p, target);
                return;
            }
        }

        BlockPos goal = target != null ? target : pos.offset(heading, 6);
        int dx = goal.getX() - pos.getX(), dz = goal.getZ() - pos.getZ(), dy = goal.getY() - pos.getY();
        Direction dir;
        if (dx == 0 && dz == 0) {
            if (dy < 0) {
                BlockPos below = pos.down();
                if (safe && (lava(mc, below) || lava(mc, below.down()))) {
                    off("Lawa pod tobą - kopacz zatrzymany.");
                    return;
                }
                if (solid(mc, below)) mine(mc, p, below);
                else release();
                return;
            }
            if (target != null) ignored.add(target);
            target = null;
            return;
        } else if (Math.abs(dx) >= Math.abs(dz)) {
            dir = dx > 0 ? Direction.EAST : Direction.WEST;
        } else {
            dir = dz > 0 ? Direction.SOUTH : Direction.NORTH;
        }

        BlockPos feet = pos.offset(dir), head = feet.up();
        if (safe && (lava(mc, feet) || lava(mc, head) || lava(mc, feet.down())
                || lava(mc, feet.offset(dir)) || lava(mc, feet.offset(dir).up()))) {
            off("Lawa na drodze - kopacz zatrzymany.");
            return;
        }
        if (solid(mc, head)) {
            mine(mc, p, head);
            return;
        }
        if (solid(mc, feet)) {
            mine(mc, p, feet);
            return;
        }
        if (safe && !solid(mc, feet.down()) && !solid(mc, feet.down(2)) && !solid(mc, feet.down(3))) {
            off("Przepaść przed tobą - kopacz zatrzymany.");
            return;
        }
        aim(p, Vec3d.ofCenter(feet).add(0, 0.3, 0));
        mc.options.forwardKey.setPressed(true);
        if (p.getPos().squaredDistanceTo(lastPos) < 0.0004) still++;
        else still = 0;
        lastPos = p.getPos();
        if (still > 80) {
            if (target != null) ignored.add(target);
            target = null;
            still = 0;
            heading = heading.rotateYClockwise();
        }
    }
}
