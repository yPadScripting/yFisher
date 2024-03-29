
import org.powerbot.core.script.job.state.Node;
import org.powerbot.game.api.methods.interactive.NPCs;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.node.Menu;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.methods.widget.Camera;
import org.powerbot.game.api.util.Random;
import org.powerbot.game.api.wrappers.Area;
import org.powerbot.game.api.wrappers.Tile;
import org.powerbot.game.api.wrappers.interactive.NPC;
import org.powerbot.game.api.wrappers.node.Item;

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
/**
 *
 * @author PimGame
 */
public class KaramjaFisher extends Node {

    public final static Area docks = new Area(new Tile[]{
                new Tile(2926, 3173, 0), new Tile(2912, 3176, 0),
                new Tile(2912, 3183, 0), new Tile(2930, 3186, 0)
            });

    @Override
    public boolean activate() {
        return yFisher.location.equals("Karamja") && atDocks() && !Inventory.isFull();
    }

    @Override
    public void execute() {
        final NPC fishingspot = NPCs.getNearest(324);
        if (Players.getLocal().getAnimation() != 619 && !Players.getLocal().isMoving()) {
            fishingspot.hover();
            if (Menu.contains("Cage") && Menu.contains("Harpoon")) {
                if (Menu.getItems().length <= 20) {
                    if (yFisher.fish.equals("Cage")) {
                        Menu.select("Cage");
                        sleep(1200);
                        while (Players.getLocal().getAnimation() == 619) {
                            sleep(1000);
                        }
                    }
                    if (yFisher.fish.equals("Harpoon")) {
                        Menu.select("Harpoon");
                        sleep(1200);
                        while (Players.getLocal().getAnimation() == 618) {
                            sleep(100);
                        }
                    }
                } else {
                    Camera.setAngle(Camera.getYaw() + Random.nextInt(90, 95));
                }
            }
        }
        
        if (yFisher.dropTuna == true) {
            if (Inventory.isFull() && Inventory.getCount(359) > 0) {                
                while (Inventory.getCount(359) > 0) {
                    Inventory.getItem(359).getWidgetChild().interact("Drop");
                    sleep(650);
                }
            }
        }
    }

    public static boolean atDocks() {
        return docks.contains(Players.getLocal().getLocation());
    }
}
