package tocraft.walkers.screen.widget;

import tocraft.walkers.screen.WalkersHelpScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.Collections;

public class HelpWidget extends ButtonWidget {

    public HelpWidget(int x, int y, int width, int height) {
        super(x, y, width, height, Text.of("?"), (widget) -> {
            MinecraftClient.getInstance().setScreen(new WalkersHelpScreen());
        });
    }

    @Override
    public void renderTooltip(MatrixStack matrices, int mouseX, int mouseY) {
        Screen currentScreen = MinecraftClient.getInstance().currentScreen;

        if(currentScreen != null) {
            currentScreen.renderTooltip(matrices, Collections.singletonList(Text.translatable("walkers.help")), mouseX, mouseY + 15);
        }
    }
}