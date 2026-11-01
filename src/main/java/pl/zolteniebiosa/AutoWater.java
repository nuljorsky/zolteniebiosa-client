package pl.zolteniebiosa;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

// Auto Water (MLG): przy spadaniu sam wylewa wode pod siebie, potem zbiera ja z powrotem.
public class AutoWater {
    private static int stage = 0, prevSlot = -1;
    private static long t = 0;

    public static void tick(MinecraftClient mc) {
        if (!Mods.AUTOWATER.enabled || mc.player == null || mc.world == null || mc.interactionManager == null) {
            stage = 0;
            return;
        }
        ClientPlayerEntity p = mc.player;
        if (stage == 0) {
            if (p.isOnGround() || p.isTouchingWater() || p.getAbilities().flying || p.isFallFlying()) return;
            if (p.fallDistance < 2.5 || p.getVelocity().y > -0.4) return;
            int slot = -1;
            for (int i = 0; i < 9; i++) {
                if (p.getInventory().getStack(i).isOf(Items.WATER_BUCKET)) {
                    slot = i;
                    break;
                }
            }
            if (slot < 0) return;
            Vec3d eye = p.getEyePos();
            BlockHitResult hr = mc.world.raycast(new RaycastContext(eye, eye.add(0, -6, 0),
                    RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, p));
            if (hr.getType() != HitResult.Type.BLOCK) return;
            if (eye.y - hr.getPos().y > 4.4) return;
            prevSlot = p.getInventory().selectedSlot;
            p.getInventory().selectedSlot = slot;
            p.setPitch(90f);
            mc.interactionManager.interactItem(p, Hand.MAIN_HAND);
            p.swingHand(Hand.MAIN_HAND);
            stage = 1;
            t = System.currentTimeMillis();
        } else {
            long el = System.currentTimeMillis() - t;
            if (el > 4000) {
                p.getInventory().selectedSlot = Math.max(0, prevSlot);
                stage = 0;
                return;
            }
            if (el > 150 && (p.isOnGround() || p.isTouchingWater())) {
                if (p.getMainHandStack().isOf(Items.BUCKET)) {
                    p.setPitch(90f);
                    mc.interactionManager.interactItem(p, Hand.MAIN_HAND);
                    p.swingHand(Hand.MAIN_HAND);
                }
                p.getInventory().selectedSlot = Math.max(0, prevSlot);
                stage = 0;
            }
        }
    }
}
