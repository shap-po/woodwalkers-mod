package tocraft.walkers.neoforge;

import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import tocraft.walkers.Walkers;
import tocraft.walkers.WalkersClient;

@SuppressWarnings("unused")
@Mod(Walkers.MODID)
public class WalkersNeoForge {
    public WalkersNeoForge() {
        new Walkers().initialize();

        NeoForge.EVENT_BUS.register(new WalkersNeoForgeEventHandler());

        if (FMLEnvironment.dist.isClient())
            new WalkersClient().initialize();
    }
}
