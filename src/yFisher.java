

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.imageio.ImageIO;
import org.powerbot.core.event.events.MessageEvent;
import org.powerbot.core.event.listeners.MessageListener;
import org.powerbot.core.event.listeners.PaintListener;
import org.powerbot.core.script.ActiveScript;
import org.powerbot.core.script.job.state.Node;
import org.powerbot.core.script.job.state.Tree;
import org.powerbot.game.api.Manifest;
import org.powerbot.game.api.methods.Game;
import org.powerbot.game.api.methods.input.Mouse;
import org.powerbot.game.api.methods.tab.Skills;
import org.powerbot.game.api.util.Random;
import org.powerbot.game.api.util.Timer;

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
/**
 *
 * @author PimGame
 */
@Manifest(authors = {"yPad Scripting"}, name = "yFisher", description = "Karamja Fishing")
public class yFisher extends ActiveScript implements PaintListener, MouseListener, MessageListener {

    //Primary variables
    private String currentStatus = "";
    private Tree container = null;
    private List<Node> jobs = new ArrayList<>();
    //GUI & Paint
    private final LinkedList<MousePathPoint> mousePath = new LinkedList<>();
    boolean hide = false;
    public static boolean guiWait = true;
    Image background = getImage("http://i49.tinypic.com/1zl3iar.jpg");
    Image hidebuttonenabled = getImage("http://i50.tinypic.com/a08p4x.jpg");
    Image hidebuttondisabled = getImage("http://i50.tinypic.com/16i6wz4.jpg");
    Rectangle hidebutton = new Rectangle(497, 395, 20, 20);
    Timer runTime = new Timer(0);
    Font font1 = new Font("Verdana", 0, 20);
    yFishGUI gui = new yFishGUI();
    //Secondary Variables
    public static String msg;
    public static String location = "";
    public static String fish = "";
    public static int amount;
    private double startingxp;
    private double xpgained;

    @Override
    public void onStart() {
        System.out.println("Welcome to yFisher!");
        startingxp = Skills.getExperience(Skills.FISHING);
        gui.setVisible(true);
    }

    public void onStop() {
        gui.dispose();
    }

    @Override
    public int loop() {
        while (guiWait) {
            sleep(500);
        }
        if (container != null) {
            final Node job = container.state();

            if (job != null) {
                currentStatus = job.toString();
                container.set(job);
                getContainer().submit(job);
                job.join();
            }
        } else {
            jobs.add(new KaramjaFisher());
            jobs.add(new KaramjaWalking());
            jobs.add(new KaramjaExchanging());
            container = new Tree(jobs.toArray(new Node[jobs.size()]));
        }


        return Random.nextInt(150, 250);
    }

    @Override
    public void onRepaint(Graphics g1) {

        Graphics2D g = (Graphics2D) g1;

        if (Game.getClientState() == 11) {
            if (!hide) {

                xpgained = Skills.getExperience(Skills.FISHING) - startingxp;
                double time = (double) runTime.getElapsed();
                DecimalFormat df = new DecimalFormat("#,###,###");
                g.drawImage(background, 7, 395, null);
                g.setColor(Color.BLACK);
                g.setFont(font1);
                g.drawString("Status: " + "\n" + currentStatus, 7, 365); // Status
                g.drawString(runTime.toElapsedString(), 330, 425); //Runtime
                g.drawImage(hidebuttondisabled, 497, 395, null);
                g.drawString(df.format(amount), 330, 451); // laps/h
                g.drawString(df.format((3600000 / time) * xpgained), 330, 477); //xp/h
                g.drawString(df.format(xpgained), 330, 506); //xp gained

                //Progress Bar
                //int xpreq = Skills.getExperienceRequired(Skills.getLevel(Skills.AGILITY)+1) - Skills.getExperienceRequired(Skills.getLevel(Skills.AGILITY));
                // int xptill = Skills.getExperienceToLevel(Skills.AGILITY, Skills.getLevel((Skills.AGILITY) + 1));
                int expReq = Skills.getExperienceRequired(Skills.getLevel(Skills.FISHING) + 1) - Skills.getExperienceRequired(Skills.getLevel(Skills.FISHING));
                int expTL = Skills.getExperienceToLevel(Skills.FISHING, Skills.getLevel(Skills.FISHING) + 1);
                int percentage = (int) (100 - (expTL * 100D) / expReq);
                if (percentage > 100) {
                    percentage = 100;
                }
                int rectWidth = 515;
                int rectHeight = 12;
                int rectX = 1;
                int rectY = 376;



                g.setColor(Color.red);
                g.draw3DRect(rectX, rectY, rectWidth, rectHeight, true);

                g.setColor(Color.red);
                g.fill3DRect(rectX + 1, rectY + 1, rectWidth, rectHeight - 1, true);

                g.setColor(Color.green);
                g.fill3DRect(rectX + 1, rectY + 1, (percentage * rectWidth) / 100, rectHeight - 1, true);

                g.setColor(Color.white);
                g.setFont(new Font("Verdana", 0, 12));
                g.drawString(percentage + "% till level " + (Skills.getLevel(Skills.FISHING) + 1), 220, 386);

            }
            if (hide) {
                g.drawImage(hidebuttonenabled, 497, 395, null);
            }
        }

        //Mouse cursor
        g.setColor(Color.YELLOW);
        g.drawLine(Mouse.getX() - 5, Mouse.getY() - 5, Mouse.getX() + 5, Mouse.getY() + 5);
        g.drawLine(Mouse.getX() - 5, Mouse.getY() + 5, Mouse.getX() + 5, Mouse.getY() - 5);

        //Mouse trail
        while (!mousePath.isEmpty() && mousePath.peek().isUp()) {
            mousePath.remove();
        }
        Point clientCursor = Mouse.getLocation();
        MousePathPoint mpp = new MousePathPoint(clientCursor.x, clientCursor.y, 200); //Lasting time/MS
        if (mousePath.isEmpty() || !mousePath.getLast().equals(mpp)) {
            mousePath.add(mpp);
        }
        MousePathPoint lastPoint = null;
        for (MousePathPoint a : mousePath) {
            if (lastPoint != null) {
                g.setColor(Color.YELLOW);//Trail color
                g.drawLine(a.x, a.y, lastPoint.x, lastPoint.y);
            }
            lastPoint = a;
        }




    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Point m = e.getPoint();
        if (hidebutton.contains(m)) {
            hide = !hide;
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void messageReceived(MessageEvent me) {
        msg = me.getMessage();
        if (msg.startsWith("You catch a")) {
            amount++;
        }

    }

    @SuppressWarnings("serial")
    private class MousePathPoint extends Point { // All credits to Enfilade

        private long finishTime;
        private double lastingTime;

        public MousePathPoint(int x, int y, int lastingTime) {
            super(x, y);
            this.lastingTime = lastingTime;
            finishTime = System.currentTimeMillis() + lastingTime;
        }

        public boolean isUp() {
            return System.currentTimeMillis() > finishTime;
        }
    }

    private Image getImage(String url) {
        try {
            return ImageIO.read(new URL(url));
        } catch (IOException e) {
            return null;
        }
    }
}
