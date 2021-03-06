// 
// Decompiled by Procyon v0.5.36
// 

package bananaplusdevelopment.addons.modules;

import minegame159.meteorclient.events.entity.player.PlayerMoveEvent;
import meteordevelopment.orbit.EventHandler;
import bananaplusdevelopment.utils.ares.ReflectionHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderTickCounter;
import minegame159.meteorclient.events.world.TickEvent;
import java.util.Objects;
import minegame159.meteorclient.settings.IntSetting;
import minegame159.meteorclient.settings.KeybindSetting;
import minegame159.meteorclient.settings.DoubleSetting;
import minegame159.meteorclient.settings.BoolSetting;
import bananaplusdevelopment.addons.AddModule;
import minegame159.meteorclient.utils.misc.Keybind;
import minegame159.meteorclient.settings.Setting;
import minegame159.meteorclient.settings.SettingGroup;
import minegame159.meteorclient.systems.modules.Module;

public class StrafePlus extends Module
{
    private final SettingGroup sgGeneral;
    private final Setting<Boolean> lowHop;
    private final Setting<Double> height;
    private final Setting<Boolean> speedBool;
    private final Setting<Double> speedVal;
    private final Setting<Boolean> sprintBool;
    private final Setting<Boolean> useTimer;
    private final Setting<Keybind> timerKeybind;
    private final Setting<Integer> tps;
    
    public StrafePlus() {
        super(AddModule.BANANAPLUS, "Strafe+", "Increase speed and control in air");
        this.sgGeneral = this.settings.getDefaultGroup();
        this.lowHop = (Setting<Boolean>)this.sgGeneral.add((Setting)new BoolSetting.Builder().name("Low Hop").defaultValue(false).build());
        this.height = (Setting<Double>)this.sgGeneral.add((Setting)new DoubleSetting.Builder().name("Height").defaultValue(0.3).sliderMin(0.1).sliderMax(0.5).build());
        this.speedBool = (Setting<Boolean>)this.sgGeneral.add((Setting)new BoolSetting.Builder().name("Modify Speed").defaultValue(true).build());
        this.speedVal = (Setting<Double>)this.sgGeneral.add((Setting)new DoubleSetting.Builder().name("Speed").defaultValue(0.3199999928474426).min(0.20000000298023224).sliderMax(0.6000000238418579).build());
        this.sprintBool = (Setting<Boolean>)this.sgGeneral.add((Setting)new BoolSetting.Builder().name("Auto Sprint").defaultValue(true).build());
        this.useTimer = (Setting<Boolean>)this.sgGeneral.add((Setting)new BoolSetting.Builder().name("Use Timer+").description("if the timer should be used").defaultValue(false).build());
        this.timerKeybind = (Setting<Keybind>)this.sgGeneral.add((Setting)new KeybindSetting.Builder().name("force-timer-keybind").description("turns on timer when held").defaultValue(Keybind.fromKey(-1)).build());
        final SettingGroup sgGeneral = this.sgGeneral;
        final IntSetting.Builder sliderMax = new IntSetting.Builder().name("TPS").defaultValue(20).min(1).sliderMax(160);
        final Setting<Boolean> useTimer = this.useTimer;
        Objects.requireNonNull(useTimer);
        this.tps = (Setting<Integer>)sgGeneral.add((Setting)sliderMax.visible(useTimer::get).build());
    }
    
    @EventHandler
    private void onTick(final TickEvent.Post event) {
        if ((boolean)this.useTimer.get() || ((Keybind)this.timerKeybind.get()).isPressed()) {
            ReflectionHelper.setPrivateValue(RenderTickCounter.class, ReflectionHelper.getPrivateValue(MinecraftClient.class, this.mc, "renderTickCounter", "renderTickCounter"), 1000.0f / (int)this.tps.get(), "tickTime", "tickTime");
        }
        else {
            ReflectionHelper.setPrivateValue(RenderTickCounter.class, ReflectionHelper.getPrivateValue(MinecraftClient.class, this.mc, "renderTickCounter", "renderTickCounter"), 50.0f, "tickTime", "tickTime");
        }
    }
    
    @EventHandler
    private void onPlayerMove(final PlayerMoveEvent event) {
        if (this.mc.player.input.movementForward != 0.0f || this.mc.player.input.movementSideways != 0.0f) {
            if (this.sprintBool.get()) {
                this.mc.player.setSprinting(true);
            }
            if (this.mc.player.isOnGround() && (boolean)this.lowHop.get()) {
                this.mc.player.addVelocity(0.0, (double)this.height.get(), 0.0);
            }
            if (this.mc.player.isOnGround()) {
                return;
            }
            Double speed;
            if (!(boolean)this.speedBool.get()) {
                speed = Math.sqrt(this.mc.player.getVelocity().x * this.mc.player.getVelocity().x + this.mc.player.getVelocity().z * this.mc.player.getVelocity().z);
            }
            else {
                speed = (Double)this.speedVal.get();
            }
            float yaw = this.mc.player.yaw;
            float forward = 1.0f;
            if (this.mc.player.forwardSpeed < 0.0f) {
                yaw += 180.0f;
                forward = -0.5f;
            }
            else if (this.mc.player.forwardSpeed > 0.0f) {
                forward = 0.5f;
            }
            if (this.mc.player.sidewaysSpeed > 0.0f) {
                yaw -= 90.0f * forward;
            }
            if (this.mc.player.sidewaysSpeed < 0.0f) {
                yaw += 90.0f * forward;
            }
            yaw = (float)Math.toRadians(yaw);
            this.mc.player.setVelocity(-Math.sin(yaw) * speed, this.mc.player.getVelocity().y, Math.cos(yaw) * speed);
        }
    }
    
    public void onDeactivate() {
        ReflectionHelper.setPrivateValue(RenderTickCounter.class, ReflectionHelper.getPrivateValue(MinecraftClient.class, this.mc, "renderTickCounter", "renderTickCounter"), 50.0f, "tickTime", "tickTime");
    }
}
