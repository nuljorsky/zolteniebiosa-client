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
import java.util.List;
import java.util.Set;

// Wbudowany "Baritone": automatyczny kopacz ze schodkami w dol/gore i szukaniem rud przez zaladowane chunki.
public class Miner {
    public static boolean active = false;
    private static BlockPos target = null;
    private static int tick = 0, still = 0;
    private static Vec3d lastPos = Vec3d.ZERO;
    private static Direction heading = Direction.NORTH;
    private static final Set<BlockPos> ignored = new HashSet<>();
    public static String status = "";

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
        status = "Start";
        msg("§aKopacz włączony. Trzymaj kilof w ręce. Wyłączasz go przełącznikiem w menu (N).");
    }

    public static void stop() {
        active = false;
        status = "Wyłączony";
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

    // na jakiej wysokosci najlepiej szukac danej rudy
    private static int preferredY(Set<Block> want) {
        for (Block b : want) {
            String p = Registries.BLOCK.getId(b).getPath();
            if (p.contains("diamond") || p.contains("redstone")) return -58;
            if (p.contains("gold")) return -16;
            if (p.contains("lapis")) return 0;
            if (p.contains("iron")) return 16;
            if (p.contains("copper")) return 48;
            if (p.contains("coal")) return 45;
            if (p.contains("emerald")) return 100;
            if (p.contains("ancient_debris")) return 15;
        }
        return Integer.MIN_VALUE;
    }

    private static boolean solid(MinecraftClient mc, BlockPos b) {
        BlockState s = mc.world.getBlockState(b);
        return !s.getCollisionShape(mc.world, b).isEmpty();
    }

    private static boolean lava(MinecraftClient mc, BlockPos b) {
        return mc.world.getBlockState(b).getFluidState().isIn(FluidTags.LAVA);
    }

    private static void findTarget(MinecraftClient mc, BlockPos pos, Set<Block> want) {
        target = null;
        if (want.isEmpty()) return;
        int r = Mods.BARITONE.b("unlimited") ? 400 : Mods.BARITONE.n("radius");
        List<BlockPos> list = Scan.find(mc.world, pos, r, want, 3000);
        double best = Double.MAX_VALUE;
        for (BlockPos im : list) {
            if (ignored.contains(im)) continue;
            double d = im.getSquaredDistance(pos);
            if (d < best) {
                best = d;
                target = im;
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
        Set<Block> want = resolve(Mods.BARITONE.txt("block"));
        if (target == null || mc.world.getBlockState(target).isAir() || tick % 40 == 1) findTarget(mc, pos, want);

        mc.options.attackKey.setPressed(false);

        // cel w zasiegu ramienia: kop go bezposrednio
        if (target != null && p.getEyePos().distanceTo(Vec3d.ofCenter(target)) <= 4.2) {
            status = "Kopię cel";
            mine(mc, p, target);
            return;
        }

        // cel docelowy: znaleziona ruda albo wysokosc, na ktorej zwykle wystepuje
        BlockPos goal;
        if (target != null) {
            goal = target;
            status = "Idę do rudy";
        } else {
            int py = preferredY(want);
            if (py != Integer.MIN_VALUE && Math.abs(pos.getY() - py) > 2) {
                goal = pos.offset(heading, 4).withY(py);
                status = "Schodzę/wchodzę na wysokość rudy (Y " + py + ")";
            } else {
                goal = pos.offset(heading, 6);
                status = "Kopię tunel i szukam rudy";
            }
        }

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
            dir = heading;
        } else if (Math.abs(dx) >= Math.abs(dz)) {
            dir = dx > 0 ? Direction.EAST : Direction.WEST;
        } else {
            dir = dz > 0 ? Direction.SOUTH : Direction.NORTH;
        }
        heading = dir;

        int vy = dy < -1 ? -1 : dy > 1 ? 1 : 0;
        BlockPos col = pos.offset(dir);          // kolumna przed nami
        BlockPos newFeet = col.up(vy);
        BlockPos newHead = newFeet.up();

        if (safe) {
            for (int k = -2; k <= 3; k++) {
                if (lava(mc, col.up(k + vy)) || lava(mc, col.offset(dir).up(k + vy))) {
                    off("Lawa na drodze - kopacz zatrzymany.");
                    return;
                }
            }
        }

        // kolejnosc kopania: gora -> dol
        if (vy > 0 && solid(mc, pos.up(2))) {
            mine(mc, p, pos.up(2));
            return;
        }
        if (vy < 0 && solid(mc, col.up())) {
            mine(mc, p, col.up());
            return;
        }
        if (solid(mc, newHead)) {
            mine(mc, p, newHead);
            return;
        }
        if (solid(mc, newFeet)) {
            mine(mc, p, newFeet);
            return;
        }
        if (safe && vy >= 0 && !solid(mc, newFeet.down()) && !solid(mc, newFeet.down(2)) && !solid(mc, newFeet.down(3))) {
            off("Przepaść przed tobą - kopacz zatrzymany.");
            return;
        }

        aim(p, Vec3d.ofCenter(newFeet).add(0, 0.3, 0));
        mc.options.forwardKey.setPressed(true);
        mc.options.jumpKey.setPressed(vy > 0);
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
