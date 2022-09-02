package com.finallion.graveyard.entities.ai.goals;

import com.finallion.graveyard.entities.LichEntity;
import com.finallion.graveyard.init.TGParticles;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;


/* MOSTLY COPY OF MELEE_ATTACK_GOAL */
public class LichMeleeGoal extends Goal {
    protected final LichEntity mob;
    private final double speed;
    private final boolean pauseWhenMobIdle;
    private Path path;
    private double targetX;
    private double targetY;
    private double targetZ;
    private int updateCountdownTicks;
    private int cooldown;
    private final int attackIntervalTicks = 20;
    private long lastUpdateTime;
    private final int DAMAGE_START_IN_ANIM = 16;
    private int animationTicker = 40;
    private int scareSoundAge = 40;
    boolean canFinishAttack = false;

    public LichMeleeGoal(LichEntity mob, double speed, boolean pauseWhenMobIdle) {
        this.mob = mob;
        this.speed = speed;
        this.pauseWhenMobIdle = pauseWhenMobIdle;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    public boolean canStart() {
        long l = this.mob.world.getTime();
        // for some reason if you time your attack good enough, and position yourself close enough to the mob, the mob will deadlock and do nothing while you're standing still
        // this is because l - this.lastUpdateTime < 20L is true and will only reset if you move away and close in again
        if (l - this.lastUpdateTime < 20L) {
            this.lastUpdateTime -= 20; // ignore this by making sure it wont deadlock. Probably best to remove lastUpdateTime. Should be covered by cooldown.
            return canStart();
        } else {
            this.lastUpdateTime = l;
            LivingEntity livingEntity = this.mob.getTarget();
            if (livingEntity == null) {
                return false;
            } else if (!livingEntity.isAlive()) {
                return false;
            } else {
                this.path = this.mob.getNavigation().findPathTo(livingEntity, 0);
                if (this.path != null) {
                    return true;
                } else {
                    return this.getSquaredMaxAttackDistance(livingEntity) >= this.mob.squaredDistanceTo(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
                }
            }
        }
    }

    public boolean shouldContinue() {
        LivingEntity livingEntity = this.mob.getTarget();
        if (livingEntity == null) {
            return false;
        } else if (!livingEntity.isAlive()) {
            return false;
        } else if (!this.pauseWhenMobIdle) {
            return !this.mob.getNavigation().isIdle();
        } else if (!this.mob.isInWalkTargetRange(livingEntity.getBlockPos())) {
            return false;
        } else {
            return !(livingEntity instanceof PlayerEntity) || !livingEntity.isSpectator() && !((PlayerEntity) livingEntity).isCreative();
        }
    }

    public void start() {
        this.mob.getNavigation().startMovingAlong(this.path, this.speed);
        this.mob.setAttacking(true);
        this.updateCountdownTicks = 0;
        this.cooldown = 0;
    }

    public void stop() {
        LivingEntity livingEntity = this.mob.getTarget();
        if (!EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR.test(livingEntity)) {
            this.mob.setTarget((LivingEntity) null);
        }

        this.mob.setAttacking(false);
        this.mob.getNavigation().stop();
    }

    public boolean shouldRunEveryTick() {
        return true;
    }

    public void tick() {
        LivingEntity livingEntity = this.mob.getTarget();
        if (livingEntity != null) {
            this.mob.getLookControl().lookAt(livingEntity, 30.0F, 30.0F);
            double d = this.mob.squaredDistanceTo(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
            this.updateCountdownTicks = Math.max(this.updateCountdownTicks - 1, 0);
            if ((this.pauseWhenMobIdle || this.mob.getVisibilityCache().canSee(livingEntity)) && this.updateCountdownTicks <= 0 && (this.targetX == 0.0D && this.targetY == 0.0D && this.targetZ == 0.0D || livingEntity.squaredDistanceTo(this.targetX, this.targetY, this.targetZ) >= 1.0D || this.mob.getRandom().nextFloat() < 0.05F)) {
                this.targetX = livingEntity.getX();
                this.targetY = livingEntity.getY();
                this.targetZ = livingEntity.getZ();
                this.updateCountdownTicks = 4 + this.mob.getRandom().nextInt(7);
                if (d > 1024.0D) {
                    this.updateCountdownTicks += 10;
                } else if (d > 256.0D) {
                    this.updateCountdownTicks += 5;
                }

                if (!this.mob.getNavigation().startMovingTo(livingEntity, this.speed)) {
                    this.updateCountdownTicks += 15;
                }

                this.updateCountdownTicks = this.getTickCount(this.updateCountdownTicks);
            }

            this.cooldown = Math.max(this.cooldown - 1, 0);
            this.animationTicker--;
            this.scareSoundAge--;
            this.attack(livingEntity, d);
        }
    }

    protected void attack(LivingEntity target, double squaredDistance) {
        double d = this.getSquaredMaxAttackDistance(target);

        int phase = this.mob.getPhase();

        // SOUL BEAM ATTACK
        if (phase == 1) {
            if (this.mob.canMeeleAttack()) {
                if (squaredDistance <= d && this.cooldown <= 0) {
                    this.resetCooldown(20);
                    // set timer to start animation, after timer runs out, possibility to set again
                    if (this.mob.getAttackAnimTimer() == 0) {
                        this.mob.setAttackAnimTimer(this.mob.ATTACK_ANIMATION_DURATION);
                        animationTicker = this.mob.ATTACK_ANIMATION_DURATION;
                    }

                    // sound on start anim
                    if (this.mob.getAttackAnimTimer() == this.mob.ATTACK_ANIMATION_DURATION) {
                        this.mob.playAttackSound();
                    }

                    canFinishAttack = true;
                }

                if (canFinishAttack && animationTicker == DAMAGE_START_IN_ANIM && this.mob.tryAttack(target)) {
                    // warden sonic boom logic
                    Vec3d vec3d = mob.getPos().add(0.0D, 1.600000023841858D, 0.0D);
                    Vec3d vec3d2 = target.getEyePos().subtract(vec3d);
                    Vec3d vec3d3 = vec3d2.normalize();

                    for (int i = 1; i < MathHelper.floor(vec3d2.length()) + 7; ++i) {
                        Vec3d vec3d4 = vec3d.add(vec3d3.multiply((double) i));
                        ((ServerWorld) mob.getWorld()).spawnParticles(TGParticles.GRAVEYARD_SOUL_BEAM_PARTICLE, vec3d4.x, vec3d4.y + 1.0D, vec3d4.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                    }

                    double e = 2.5D * (1.0D - target.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE));
                    double f = 1.5D * (1.0D - target.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE));
                    target.addVelocity(vec3d3.getX() * e, vec3d3.getY() * f, vec3d3.getZ() * e);
                    canFinishAttack = false;

                }
            }
            // HUNT ATTACK
        } else if ((phase == 3 && this.mob.canHuntStart()) || phase == 5) {
            // if lich enters "light-sphere" while player has blindness, play sound
            if (phase == 3 && squaredDistance <= d + 7.0D && scareSoundAge <= 0) {
                this.mob.playScareSound();
                scareSoundAge = 40;
            }

            if (squaredDistance <= d && this.cooldown <= 0) {
                this.resetCooldown(20);
                this.mob.tryAttack(target);
            }
        }
    }

    protected double getSquaredMaxAttackDistance(LivingEntity entity) {
        if (this.mob.getPhase() == 1) {
            return 16.0D;
        } else if (this.mob.getPhase() == 3) {
            return (double) (this.mob.getWidth() * 3.0F * this.mob.getWidth() * 3.0F + entity.getWidth());
        }
        return (double) (this.mob.getWidth() * 2.0F * this.mob.getWidth() * 2.0F + entity.getWidth());
    }

    protected void resetCooldown(int ticks) {
        this.cooldown = this.getTickCount(ticks);
    }
}