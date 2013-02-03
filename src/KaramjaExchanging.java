
import org.powerbot.core.script.job.state.Node;
import org.powerbot.game.api.methods.interactive.NPCs;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.methods.widget.Camera;
import org.powerbot.game.api.wrappers.Area;
import org.powerbot.game.api.wrappers.Tile;
import org.powerbot.game.api.wrappers.interactive.NPC;

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
/**
 *
 * @author PimGame
 */
public class KaramjaExchanging extends Node {

    public static Area stiles = new Area(new Tile[]{new Tile(2844, 3149, 0), new Tile(2844, 3134, 0), new Tile(2857, 3134, 0),
                new Tile(2857, 3149, 0)});

    @Override
    public boolean activate() {
        return atStiles() && Inventory.isFull();
    }

    @Override
    public void execute() {
        NPC stiles = NPCs.getNearest(11267);
        if (stiles.isOnScreen()) {
            if (Players.getLocal().getInteracting() == null) {
                stiles.interact("Exchange");
                sleep(800);
            }
        } else {
            Camera.turnTo(stiles);
        }
    }

    public static boolean atStiles() {
        return stiles.contains(Players.getLocal().getLocation());

    }
}
