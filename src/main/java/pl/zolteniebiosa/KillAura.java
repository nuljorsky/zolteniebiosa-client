package pl.zolteniebiosa;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

public class KillAura {
    public static void tick(MinecraftClient mc) {
        if (!Mods.KILLAURA.enabled || mc.player == null || mc.world == null || mc.interactionManager == null) return;
        if (mc.currentScreen != null) return;
        ClientPlayerEntity p = mc.player;
        if (p.getAttackCooldownProgress(0.5f) < 0.92f) return;
        double range = Mods.KILLAURA.n("range") / 10.0;
        int tm = Mods.KILLAURA.mode("target");
        Entity best = null;
        double bd = Double.MAX_VALUE;
        for (Entity e : mc.world.getEntities()) {
            if (e == p || !(e instanceof LivingEntity le) || !le.isAlive()) continue;
            boolean isP = e instanceof PlayerEntity;
            boolean isM = e instanceof MobEntity;
            if (!((tm == 0 && isP) || (tm == 1 && isM) || (tm == 2 && (isP || isM)))) continue;
            double d = p.distanceTo(e);
            if (d > range) continue;
            if (d < bd) {
                bd = d;
                best = e;
            }
        }
        if (best == null) return;
        if (Mods.KILLAURA.b("rotate")) {
            Vec3d e = p.getEyePos();
            Vec3d t = best.getPos().add(0, best.getHeight() * 0.6, 0);
            double dx = t.x - e.x, dy = t.y - e.y, dz = t.z - e.z;
            double h = Math.sqrt(dx * dx + dz * dz);
            p.setYaw((float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0));
            p.setPitch((float) -Math.toDegrees(Math.atan2(dy, h)));
        }
        mc.interactionManager.attackEntity(p, best);
        p.swingHand(Hand.MAIN_HAND);
    }
}
