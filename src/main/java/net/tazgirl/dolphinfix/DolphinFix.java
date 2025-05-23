package net.tazgirl.dolphinfix;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Dolphin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;


// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(DolphinFix.MODID)
public class DolphinFix
{

    public static final String MODID = "dolphin_fix";

    public DolphinFix(IEventBus modEventBus, ModContainer modContainer)
    {
        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onLivingHurt(LivingIncomingDamageEvent event)
    {
        if (event.getEntity() instanceof Dolphin && event.getSource().is(DamageTypes.DROWN))
        {
            if (!isPlayerNearby(event.getEntity().level(), event.getEntity()))
            {
                if (event instanceof ICancellableEvent cancellableEvent)
                {
                    cancellableEvent.setCanceled(true);
                }
            }
        }
    }

    public static boolean isPlayerNearby(Level level, Entity entity)
    {
        MinecraftServer server = level.getServer();

        //I don't know what happens if we run the check and level.players is empty so this prevents any unexpected behaviour
        //Also uses slightly less resources on the spawn chunks when the servers empty I suppose
        if (server.getPlayerCount() == 0)
        {
            return false;
        }

        //The -16 is just to be safe and shouldn't affect gameplay
        int simulationDistance = server.getPlayerList().getSimulationDistance() - 16;

        double distanceSqr = simulationDistance * simulationDistance * 16;


        for (Player player : level.players())
        {
            if (player.distanceToSqr(entity) <= distanceSqr)
            {
                return true;
            }
        }

        return false;
    }

}
