
import org.powerbot.core.script.job.state.Node;
import org.powerbot.game.api.methods.Walking;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.wrappers.Tile;

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
/**
 *
 * @author PimGame
 */
public class KaramjaWalking extends Node {

    public static Tile[] myTiles = new Tile[]{new Tile(2924, 3177, 0), new Tile(2924, 3172, 0), new Tile(2919, 3171, 0),
        new Tile(2914, 3172, 0), new Tile(2909, 3171, 0), new Tile(2904, 3172, 0),
        new Tile(2899, 3171, 0), new Tile(2894, 3169, 0), new Tile(2890, 3166, 0),
        new Tile(2885, 3164, 0), new Tile(2880, 3162, 0), new Tile(2875, 3160, 0),
        new Tile(2871, 3157, 0), new Tile(2867, 3154, 0), new Tile(2862, 3152, 0),
        new Tile(2858, 3149, 0), new Tile(2854, 3146, 0), new Tile(2850, 3143, 0),
        new Tile(2851, 3142, 0)};

    @Override
    public boolean activate() {
        return (KaramjaFisher.atDocks() && Inventory.isFull()) || (KaramjaExchanging.atStiles() && !Inventory.isFull());
    }

    @Override
    public void execute() {
        if (Walking.getEnergy() > 15 && !Walking.isRunEnabled()) {
            Walking.setRun(true);
        }
        if (KaramjaFisher.atDocks() && Inventory.isFull()) {
            while (!KaramjaExchanging.atStiles()) {
                Walking.newTilePath(myTiles).traverse();
                sleep(600);
            }
        }

        if ((KaramjaExchanging.atStiles() && !Inventory.isFull())) {
            while (!KaramjaFisher.atDocks()) {
                Walking.newTilePath(myTiles).reverse().traverse();
                sleep(600);
            }
        }
    }
}
