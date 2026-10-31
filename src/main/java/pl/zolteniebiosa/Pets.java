package pl.zolteniebiosa;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class Pets {
    private static Entity pet;
    private static int kind = -1;
    private static World petWorld;
    private static boolean broken = false;

    private static final DyeColor[] DYE = {DyeColor.WHITE, DyeColor.WHITE, DyeColor.RED, DyeColor.ORANGE, DyeColor.YELLOW,
            DyeColor.LIME, DyeColor.CYAN, DyeColor.BLUE, DyeColor.PURPLE, DyeColor.PINK};

    private static Entity make(int k, World w) {
        return switch (k) {
            case 0 -> new ParrotEntity(EntityType.PARROT, w);
            case 1 -> new CatEntity(EntityType.CAT, w);
            case 2 -> new WolfEntity(EntityType.WOLF, w);
            default -> new PandaEntity(EntityType.PANDA, w);
        };
    }

    public static void render(WorldRenderContext ctx) {
        if (broken || !Mods.PET.enabled) return;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null || mc.player == null) return;
        try {
            int k = Mods.PET.mode("pet");
            if (pet == null || kind != k || petWorld != mc.world) {
                pet = make(k, mc.world);
                kind = k;
                petWorld = mc.world;
            }
            int ci = Mods.PET.get("color").idx;
            DyeColor dc = ci <= 0 ? DyeColor.values()[(int) ((System.currentTimeMillis() / 700) % 16)] : DYE[Math.min(ci, DYE.length - 1)];
            if (pet instanceof CatEntity c) {
                if (!c.isTamed()) c.setTamed(true, false);
                c.setCollarColor(dc);
                c.setInSittingPose(true);
            } else if (pet instanceof WolfEntity w) {
                if (!w.isTamed()) w.setTamed(true, false);
                w.setCollarColor(dc);
                w.setInSittingPose(true);
            } else if (pet instanceof ParrotEntity p) {
                ParrotEntity.Variant[] vs = ParrotEntity.Variant.values();
                p.setVariant(vs[Math.max(0, ci) % vs.length]);
            }
            pet.setOnGround(true);
            pet.age = mc.player.age;

            float td = ctx.tickCounter().getTickDelta(true);
            ClientPlayerEntity pl = mc.player;
            double px = MathHelper.lerp(td, pl.prevX, pl.getX());
            double py = MathHelper.lerp(td, pl.prevY, pl.getY());
            double pz = MathHelper.lerp(td, pl.prevZ, pl.getZ());
            float yaw = MathHelper.lerp(td, pl.prevBodyYaw, pl.bodyYaw);
            double rad = Math.toRadians(yaw);
            double rx = -Math.cos(rad), rz = -Math.sin(rad);
            int pos = Mods.PET.mode("pos");
            double ox, oy, oz;
            if (pos == 0) {
                ox = rx * 0.42; oy = pl.isSneaking() ? 1.25 : 1.5; oz = rz * 0.42;
            } else if (pos == 1) {
                ox = rx * 0.8; oy = 0.0; oz = rz * 0.8;
            } else {
                ox = 0; oy = 2.35; oz = 0;
            }

            if (pet instanceof LivingEntity le) {
                le.bodyYaw = yaw; le.prevBodyYaw = yaw;
                le.headYaw = yaw; le.prevHeadYaw = yaw;
            }
            pet.setPosition(px + ox, py + oy, pz + oz);

            VertexConsumerProvider consumers = ctx.consumers();
            MatrixStack ms = ctx.matrixStack();
            if (consumers == null || ms == null) return;
            Vec3d cam = ctx.camera().getPos();
            float s = Mods.PET.n("size") / 100f;
            ms.push();
            ms.translate(px + ox - cam.x, py + oy - cam.y, pz + oz - cam.z);
            ms.scale(s, s, s);
            mc.getEntityRenderDispatcher().render(pet, 0.0, 0.0, 0.0, td, ms, consumers, 0xF000F0);
            ms.pop();
        } catch (Throwable t) {
            broken = true;
            mc.player.sendMessage(Text.literal("§e[ZolteNiebiosa] §cPety wyłączone (błąd renderu): " + t.getClass().getSimpleName()), false);
        }
    }
}
