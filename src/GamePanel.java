import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import javax.swing.Timer;
import java.io.File;

/**
 * Creates the panel for the game where everything is drawn
 * Peter Benum
 * ICS3U
 * @author Ken Chen
 */
public class GamePanel extends JPanel implements KeyListener,ActionListener {
    //Leaderboard components
    static File leaderboard = new File("leaderboard.txt");
    int[] leaderBoardScores = new int[5];
    String[] names = new String[5];
    boolean greaterScoreFound = false;
    String displayLeaderboard;
    final int LEADERBOARD_SPOTS = 5;

    //Text box variables
    Image textBox;
    String text;
    String textLine2 = "";
    boolean textOn = false;

    //Timer variables
    boolean createdTimer = false;
    Timer gameTimer;
    long initialTime;
    boolean resetTimeOn = false;

    //Character variables
    JLabel character;
    Icon charIcon;
    final int CHARACTER_START_X = 590;
    final int CHARACTER_START_Y = 620;
    final int CHARACTER_WIDTH = 25;
    final int CHARACTER_HEIGHT = 25;
    int hpLostWidth = 0;
    final int CHARACTER_SPEED = 8;

    //Hitbox variables
    HitBox characterHitBox;
    final int HITBOX_ADJUSTMENT = 10;

    //Boss variables
    Image bossSweating;
    JLabel bossAnimation;
    JLabel bossDeath;
    JLabel bossShrug;
    int bossHP = 10000;
    final int BOSS_ORIGIN_HP = 10000;
    double bossBarWidth = 300;
    int bossHpLost = 0;

    //Frame values
    final int SCREEN_WIDTH = 1200;
    final int SCREEN_HEIGHT = 800;
    final int GAME_TICKS = 20;
    final int BORDER_UP = 400;
    final int BORDER_DOWN = 625;
    final int BORDER_LEFT = 350;
    final int BORDER_RIGHT = 825;

    //Keyboard input trackers
    boolean wPress = false;
    boolean sPress = false;
    boolean aPress = false;
    boolean dPress = false;

    //Selection phase variables 
    int selector = 0;
    boolean pressed = false;
    boolean selection = false;

    //Fight option variables
    Image fightButton;
    Image fightSelected;
    Image attackBarGraph;
    Image attackBar;
    int attackBarX = 330;
    int attackBarY = 475;
    boolean attackOptionFinish = false;
    boolean showDamageDealt = false;
    double maxDamageDealt = 1600;
    int bossDamageDisplayNumber;
    long damageDisplayTime;
    final int FIGHT_BUTTON_X = 390;

    //Mercy option variables
    Image mercyButton;
    Image mercySelected;
    int mercyCount = 0;
    final int MERCY_BUTTON_X = 550;

    //Item option variables
    Image itemButton;
    Image itemSelected;
    int[][] items = {{1, 3},
            {2, 4}};
    final int BUTTERSCOTCH_HP = 99;
    final int LEGENDARY_HP = 40;
    final int STEAK_HP = 60;
    final int APPLE_HP = 20;
    int itemRow = 0;
    int itemColumn = 0;
    int itemSelector = 1;
    final int ITEM_BUTTON_X = 710;

    //General choice button variables
    final int CHOICE_BUTTON_Y = 700;

    //General attack variables
    int attackCount = 0;
    boolean attackOn = false;
    Random rand;

    //Invisibility variables
    static final int INVISIBILITY_EXPIRE_TIME = 1000;
    boolean isInvincible = false;

    //Attack 1 variables
    Image warningBox1;
    Image warningBox2;
    Image lightning;
    Image lightning2;
    int attack1X = 350;
    boolean doStrike = false;
    long animationTimer;
    boolean attacking = false;
    int specificAttackCount = 0;
    boolean strike1 = false;
    final int STRIKES = 5;

    //Attack 2 variables
    final int MAX_JUMP_DISTANCE = 150;
    int initialY = 620;
    boolean floorTouch = true;
    Image bone;
    Image boneGap;
    int[] sweepingBoneX = {275, -25, 875, 1175};
    int boneGapY1 = 500;
    int boneGapY2 = 500;
    boolean jumpBuffer = false;
    int jumpBufferCount = 0;
    final int LEFT_BONE_LOCATION = 275;
    final int RIGHT_BONE_LOCATION = 875;
    final int BOTTOM_Y = 650;
    final int BONE_SPEED = 12;

    //Attack 3 variables
    Image portal;
    double yOfLine = 0;
    double xOfLine = 0;
    double lineY = 0;
    double slope = 0;
    double perpendicularSlope = 0;
    double lineBComponent = 0;
    double lineBComponent2 = 0;
    double distanceLineXInt = 0;
    double distanceLineYInt = 0;
    double distanceOfPlayerToBeam = -1;
    boolean drawBeam = false;
    final int BEAM_RIGHT_LOCATION = 1210;
    final int BEAM_LEFT_LOCATION = -10;
    int colorPicked = 0;

    //attack 4 variables
    Image platform;
    Image revolvingBone;
    Image boneRow;
    boolean moveRight = true;
    int platformX = BORDER_LEFT;
    boolean platformTouch = false;
    final int PLATFORM_Y = 575;
    int[] revolvingBonesY = {600, 550, 500, 700, 650, 600, 800, 750, 700};
    final int[] REVOLVING_BONES_X = {450, 600, 750, 450, 600, 750, 450, 600, 750};

    //attack 5 variables
    Image crossingBone;
    int[] crossingBonesX = {1100, 1240, 1380, 1520, 1660, 1800, 1940};
    int[] crossingBonesX2 = {-740, -600, -460, -320, -180, -40, 100};


    //Game variables
    Image charDead;
    boolean gameOver = false;
    boolean deathAnimation = false;
    boolean genocideWin = false;
    boolean pacifistWin = false;
    boolean gameOn = false;

    //Music
    Clip clip;

    //Gravity variables
    boolean gravityMoveOn = false;
    boolean gravityOn = false;
    final int GRAVITY_SPEED = 12;
    final int JUMP_SPEED = 12;

    //Endless mode variables
    boolean endlessOn = false;
    boolean endlessGame = false;
    int endlessCounter = 0;

    GamePanel() {
        //Initial panel creation
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(this);

        //Boss idle animation
        bossAnimation = new JLabel("", new ImageIcon("./resources/bossAnimation.gif"), JLabel.CENTER);
        bossAnimation.setLocation(600, 200);
        bossAnimation.setBounds(350, 25, 500, 500);
        this.add(bossAnimation);
        bossAnimation.setVisible(true);

        bossSweating = new ImageIcon("./resources/bossSweating.png").getImage();

        //Boss death animation
        bossDeath = new JLabel("", new ImageIcon("./resources/bossDeath.gif"), JLabel.CENTER);
        bossDeath.setLocation(600, 200);
        bossDeath.setBounds(350, 25, 500, 500);
        this.add(bossDeath);
        bossDeath.setVisible(false);

        bossShrug = new JLabel("", new ImageIcon("./resources/bossShrug.gif"), JLabel.CENTER);
        bossShrug.setLocation(600, 200);
        bossShrug.setBounds(350, 25, 500, 500);
        this.add(bossShrug);
        bossShrug.setVisible(false);


        //Choice button images
        fightButton = new ImageIcon("./resources/fight.png").getImage();
        mercyButton = new ImageIcon("./resources/mercy.png").getImage();
        itemButton = new ImageIcon("./resources/item.png").getImage();
        fightSelected = new ImageIcon("./resources/fightSelected.png").getImage();
        mercySelected = new ImageIcon("./resources/mercySelected.png").getImage();
        itemSelected = new ImageIcon("./resources/itemSelected.png").getImage();

        //Attack choice images
        attackBarGraph = new ImageIcon("./resources/attackBar.png").getImage();
        attackBar = new ImageIcon("./resources/bar.png").getImage();

        //Text bubble
        textBox = new ImageIcon("./resources/textBubble.png").getImage();

        //Character image
        charIcon = new ImageIcon("./resources/heart.png");

        //Character dead image
        charDead = new ImageIcon("./resources/BrokenHeart.png").getImage();

        //Creates character
        character = new JLabel();
        character.setIcon(charIcon);
        character.setBounds(CHARACTER_START_X, CHARACTER_START_Y, CHARACTER_WIDTH, CHARACTER_HEIGHT);
        this.add(character);
        this.setLayout(null);
        this.setVisible(true);

        //Creates character hitbox
        characterHitBox = new HitBox(CHARACTER_START_X + HITBOX_ADJUSTMENT, CHARACTER_START_Y + HITBOX_ADJUSTMENT, CHARACTER_WIDTH - 20, CHARACTER_HEIGHT - 20);


        //Creates attack1 sprites
        warningBox1 = new ImageIcon("./resources/!Box1.png").getImage();
        warningBox2 = new ImageIcon("./resources/!Box2.png").getImage();
        lightning = new ImageIcon("./resources/lightning.png").getImage();
        lightning2 = new ImageIcon("./resources/lightning2.png").getImage();

        //Creates attack2 sprites
        bone = new ImageIcon("./resources/bone.png").getImage();
        boneGap = new ImageIcon("./resources/gap.jpg").getImage();
        rand = new Random();

        //Creates attack3 sprites
        portal = new ImageIcon("./resources/portal.png").getImage();

        //Creates attack4 sprites
        platform = new ImageIcon("./resources/platform.png").getImage();
        revolvingBone = new ImageIcon("./resources/revolvingBone.png").getImage();
        boneRow = new ImageIcon("./resources/boneRow.png").getImage();

        //Creates attack5 sprites
        crossingBone = new ImageIcon("./resources/crossingBone.png").getImage();

        //Creates leaderboard if not already created
        if (!leaderboard.exists()) {
            try {
                leaderboard.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            getLeaderBoard();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Obtains leaderboard data
     * Pre: leaderboard text file exists
     * Post: leaderboard names and scores are stoRED
     *
     * @throws IOException
     */
    public void getLeaderBoard() throws IOException {
        Scanner in = new Scanner(leaderboard);
        for (int i = 0; i < LEADERBOARD_SPOTS; i++) { //Reads the lines of the leaderboard and stores name and score values
            if (in.hasNext()) {
                names[i] = in.next();
                leaderBoardScores[i] = in.nextInt();

            } else {
                leaderBoardScores[i] = 0;
                names[i] = "empty";
            }
        }
    }

    /**
     * Updates leaderboard when a high score is achieved
     * Pre: Score is greater than the bottom score
     * Post: leaderboard is updated
     *
     * @throws IOException
     */
    public void writeToLeaderBoard(String name, int score) throws IOException {
        getLeaderBoard(); //Brings leaderboard up to date
        greaterScoreFound = false;
        FileWriter fWriter = new FileWriter("leaderboard.txt");
        for (int i = 0; i < LEADERBOARD_SPOTS; i++) { //Writes scores to the leaderboard file
            if (score > leaderBoardScores[i] && !greaterScoreFound) { //If a position for the new score has not been found, keep rewriting scores
                fWriter.write(name + " " + score + "\n");
                if (i != LEADERBOARD_SPOTS - 1) {
                    fWriter.write(names[i] + " " + leaderBoardScores[i] + "\n"); //Print new score
                }
                greaterScoreFound = true;
            } else {
                if (greaterScoreFound) { //Once position for score has been found, discard the bottom score to make room for new score
                    if (i != LEADERBOARD_SPOTS - 1) {
                        fWriter.write(names[i] + " " + leaderBoardScores[i] + "\n");
                    }
                } else {
                    fWriter.write(names[i] + " " + leaderBoardScores[i] + "\n");
                }
            }
        }
        fWriter.close(); //Closes text file to update it

    }

    /**
     * Starts game
     * Pre: game is not already started
     * Post: Timer is created and game begins
     */
    public void startGame() {
        gameOn = true;
        deathAnimation = false;
        String music = "./resources/megalovania.wav";
        playMusic(music);
        if (!createdTimer) { //Creates timer that sets pace for game
            gameTimer = new Timer(GAME_TICKS, this);
            gameTimer.start();
            createdTimer = true;
        }
    }

    /**
     * Passes graphics g to draw method
     * Pre: None
     * Post: graphics are passed
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g); //Passes graphics g
    }


    /**
     * Draws everything for the game
     * Pre: graphics g is passed
     * Components are drawn
     */
    public void draw(Graphics g) {
        Graphics2D g2D = (Graphics2D) g;
        if (!gameOver && !genocideWin && !pacifistWin) { //Draws game if animations are not playing
            g2D.setPaint(Color.WHITE);
            g2D.setStroke(new BasicStroke(5));

            if (pressed && selector == 0) { //Draw attack graph border
                g2D.drawRect(330, 470, 550, 120);
            } else if (pressed && selector == 2) { //Draw item border
                g2D.drawRect(350, 450, 500, 200);
            } else {  //Draws general border
                g2D.drawRect(350, 400, 500, 250);
            }

            if (endlessGame) { //Display counter if endless option selected
                g.setFont(new Font("ComicSans", Font.BOLD, 30));
                g.drawString("Score: " + endlessCounter, 20, 750);
            }

            //Draw item border
            drawItemBoard(g, g2D);
            //Draw choice buttons
            drawChoiceButtons(g2D);
            //Draw controls at beginning of game
            drawControlsAndLeaderBoard(g);
            //Draw hp bar
            drawHpBar(g, g2D);


            //Draw attacks
            if (attackOn) {
                switch (attackCount) {
                    case 0:
                        drawAttack1(g2D);
                        break;
                    case 1:
                        drawAttack2(g2D);
                        g2D.drawRect(350, 400, 500, 250); //REDraws border over attacks
                        break;
                    case 2:
                        drawAttack3(g2D);
                        break;
                    case 3:
                        drawAttack4(g2D);
                        break;
                    case 4:
                        drawAttack5(g2D);
                        break;
                }
            }


        } else if (gameOver) { //If character HP reaches 0, start death animation
            g2D.setPaint(Color.BLACK);
            if (System.currentTimeMillis() - initialTime > 3000) {
                reset();
            } else if (System.currentTimeMillis() - initialTime > 1000) {
                character.setVisible(false);
                g2D.drawImage(charDead, character.getX(), character.getY(), null);
            }

        } else if (genocideWin) {  //If character defeats the boss, begin victory sequence
            bossAnimation.setVisible(false);
            if (System.currentTimeMillis() - initialTime > 16000) {
                reset();
            } else if (System.currentTimeMillis() - initialTime > 13500) {
                bossDeath.setVisible(false);
                resetTimeOn = true;
            } else if (System.currentTimeMillis() - initialTime > 10000) {
                textOn = false;
                bossDeath.setVisible(true);
            } else if (System.currentTimeMillis() - initialTime > 8000) {
                text = "you smell bad";
            } else if (System.currentTimeMillis() - initialTime > 6000) {
                text = "Damn it...";
            } else if (System.currentTimeMillis() - initialTime > 4000) {
                text = "To think I would lose to you...";
            } else if (System.currentTimeMillis() - initialTime > 2000) {
                text = "...";
                textOn = true;
            }
            if (!resetTimeOn) {
                g2D.drawImage(bossSweating, 480, 180, null);
            }

        } else { //If character beats the boss through mercy, mercy sequence plays
            if (System.currentTimeMillis() - initialTime > 13000) {
                reset();
                bossShrug.setVisible(false);
            } else if (System.currentTimeMillis() - initialTime > 10000) {
                text = "but next time, you won't be";
                textLine2 = "so lucky pal";
            } else if (System.currentTimeMillis() - initialTime > 8000) {
                text = "I'll let you off the hook just";
                textLine2 = "this once since it was fun";

            } else if (System.currentTimeMillis() - initialTime > 6000) {
                text = "Whatever";
                bossAnimation.setVisible(false);
                bossShrug.setVisible(true);
            } else if (System.currentTimeMillis() - initialTime > 4000) {
                text = "Your a special cookie ya know?";
            } else if (System.currentTimeMillis() - initialTime > 2000) {
                text = "...";
                textOn = true;
            }
        }

        //Draw text boxes and dialogue
        drawTextBoxes(g, g2D);
        //Draw boss hp bar
        drawBossHpBar(g2D);
        //Draw attack option components
        drawAttackGraph(g2D);
    }


    /**
     * Sets all values back to default after game finishes
     * Pre: game is no longer on
     * Post: All values are set to default
     */
    public void reset() { //Resets all variables to default after a game is played
        int[] originalCrossingBonesX = {1100, 1240, 1380, 1520, 1660, 1800, 1940};
        int[] originalCrossingBonesX2 = {-740, -600, -460, -320, -180, -40, 100};
        int[] originalSweepingBoneX = {275, -25, 875, 1175};
        textLine2 = "";
        textOn = false;
        gameOver = false;
        attacking = false;
        attackOn = false;
        character.setVisible(true);
        bossAnimation.setVisible(true);
        hpLostWidth = 0;
        specificAttackCount = 0;
        attackCount = 0;
        bossHpLost = 0;
        animationTimer = 0;
        doStrike = false;
        sweepingBoneX = originalSweepingBoneX;
        crossingBonesX = originalCrossingBonesX;
        crossingBonesX2 = originalCrossingBonesX2;
        bossHP = 10000;
        mercyCount = 0;
        character.setLocation(CHARACTER_START_X, CHARACTER_START_Y);
        characterHitBox.setLocation(character.getX() + HITBOX_ADJUSTMENT, character.getY() + HITBOX_ADJUSTMENT);
        pacifistWin = false;
        resetTimeOn = false;
        genocideWin = false;
        endlessOn = false;
        items[0][0] = 1;
        items[0][1] = 3;
        items[1][0] = 2;
        items[1][1] = 4;
        endlessGame = false;
        wPress = false;
        sPress = false;
        aPress = false;
        dPress = false;
        jumpBufferCount = 0;
        jumpBuffer = false;
        initialY = 620;
    }


    /**
     * Draws item board
     * Pre: Graphics g and g2D are  passed
     * Post: Item board is drawn
     */
    public void drawItemBoard(Graphics g, Graphics2D g2D) { //If item choice was selected, draw item menu
        if (pressed && selector == 2) {
            g.setFont(new Font("ComicSans", Font.BOLD, 20));
            if (itemSelector == 1) {
                g2D.setPaint(Color.YELLOW);
                g.drawString("*Butterscotch Pie", 380, 490);
            } else {
                g2D.setPaint(Color.WHITE);
                g.drawString("*Butterscotch Pie", 380, 490);
            }
            if (itemSelector == 2) {
                g2D.setPaint(Color.YELLOW);
                g.drawString("*Steak", 380, 540);
            } else {
                g2D.setPaint(Color.WHITE);
                g.drawString("*Steak", 380, 540);
            }
            if (itemSelector == 3) {
                g2D.setPaint(Color.YELLOW);
                g.drawString("*Legendary Hero", 630, 490);
            } else {
                g2D.setPaint(Color.WHITE);
                g.drawString("*Legendary Hero", 630, 490);
            }
            if (itemSelector == 4) {
                g2D.setPaint(Color.YELLOW);
                g.drawString("*Apple", 630, 540);
            } else {
                g2D.setPaint(Color.WHITE);
                g.drawString("*Apple", 630, 540);
            }
            g2D.setPaint(Color.RED);
            if (items[0][0] == -1) {
                g.drawString("*Butterscotch Pie", 380, 490);
            }
            if (items[0][1] == -1) {
                g.drawString("*Legendary Hero", 630, 490);
            }
            if (items[1][0] == -1) {
                g.drawString("*Steak", 380, 540);
            }
            if (items[1][1] == -1) {
                g.drawString("*Apple", 630, 540);
            }
            //draw item selector border
            g2D.setPaint(Color.WHITE);
        }
    }

    /**
     * Draws choice button images
     * Pre: Graphics g2D is passed
     * Post: Choice button images are drawn
     */
    public void drawChoiceButtons(Graphics2D g2D) {
        if (!selection || pressed) {
            g2D.drawImage(fightButton, FIGHT_BUTTON_X, CHOICE_BUTTON_Y, null);
            g2D.drawImage(mercyButton, MERCY_BUTTON_X, CHOICE_BUTTON_Y, null);
            g2D.drawImage(itemButton, ITEM_BUTTON_X, CHOICE_BUTTON_Y, null);
        } else {
            if (selector == 0) {
                g2D.drawImage(fightSelected, FIGHT_BUTTON_X, CHOICE_BUTTON_Y, null);
                g2D.drawImage(mercyButton, MERCY_BUTTON_X, CHOICE_BUTTON_Y, null);
                g2D.drawImage(itemButton, ITEM_BUTTON_X, CHOICE_BUTTON_Y, null);
            } else if (selector == 1) {
                g2D.drawImage(fightButton, FIGHT_BUTTON_X, CHOICE_BUTTON_Y, null);
                g2D.drawImage(mercySelected, MERCY_BUTTON_X, CHOICE_BUTTON_Y, null);
                g2D.drawImage(itemButton, ITEM_BUTTON_X, CHOICE_BUTTON_Y, null);
            } else {
                g2D.drawImage(fightButton, FIGHT_BUTTON_X, CHOICE_BUTTON_Y, null);
                g2D.drawImage(mercyButton, MERCY_BUTTON_X, CHOICE_BUTTON_Y, null);
                g2D.drawImage(itemSelected, ITEM_BUTTON_X, CHOICE_BUTTON_Y, null);
            }
        }
    }

    /**
     * Draws player HP bar
     * Pre: Graphics g and g2D are passed
     * Post: Hp bar is drawn
     */
    public void drawHpBar(Graphics g, Graphics2D g2D) { //Updates HP bar to account for damage taken
        g2D.setPaint(Color.YELLOW);
        g2D.fillRect(555, 665, 100, 20);
        g2D.setPaint(Color.RED);
        g2D.fillRect(655 + hpLostWidth, 665, -hpLostWidth, 20);
        g2D.setPaint(Color.WHITE);
        g.setFont(new Font("ComicSans", Font.BOLD, 20));
        g.drawString("HP", 520, 685);
        g.drawString(100 + hpLostWidth + "/100", 660, 680);
    }

    /**
     * Draws boss HP bar
     * Pre: Graphics g2D is passed
     * Post: Boss HP bar is drawn
     */
    public void drawBossHpBar(Graphics2D g2D) {
        if (showDamageDealt) { //Displays boss HP temporarily
            g2D.setPaint(Color.GREEN);
            g2D.fillRect(450, 150, (int) bossBarWidth, 15);
            g2D.setPaint(Color.RED);
            g2D.fillRect(750 - bossHpLost, 150, +bossHpLost, 15);
        }
    }

    /**
     * Draws attack graphic and damage dealt for attack choice
     * Pre: Graphics g2D is passed
     * Post: Attack graph and its components are drawn
     */
    public void drawAttackGraph(Graphics2D g2D) {
        if (pressed && selector == 0) { //When attack choice is selected, draw attack graph and sequence
            g2D.setPaint(Color.WHITE);
            g2D.drawImage(attackBarGraph, 330, 470, null);
            g2D.drawImage(attackBar, attackBarX, attackBarY, null);
        }
        if (showDamageDealt) {
            g2D.setFont(new Font("ComicSans", Font.BOLD, 70));
            g2D.setPaint(Color.RED);
            g2D.drawString("-" + bossDamageDisplayNumber, 250, 200);
            if (System.currentTimeMillis() - damageDisplayTime > 1000) {
                showDamageDealt = false;
            }
        }
    }

    /**
     * Draws leaderboard and control list
     * Pre: leaderboard file exists and Graphics g is passed
     * Post: leaderboard and controls are drawn
     */
    public void drawControlsAndLeaderBoard(Graphics g) {
        //Controls
        if (!gameOn) { //Draws leaderboard and controls when game is not on
            g.setFont(new Font("ComicSans", Font.BOLD, 30));
            g.setColor(Color.WHITE);
            g.drawString("Movement: W, A, S, D", 80, 100);
            g.drawString("Start Game: Z", 80, 140);
            g.drawString("Start Endless Game: G", 80, 180);
            g.drawString("Browse Choices: A, D", 80, 220);
            g.drawString("Select Choice: Enter", 80, 260);
            g.drawString("Close dialogue: Enter", 80, 300);

            g.drawString("Endless Mode Leaderboard", 710, 60);
            g.drawString("Rank     Name     Score", 750, 100);
            g.setColor(Color.YELLOW);

            //Formats leaderboard data
            displayLeaderboard = String.format("%5s%13s%10s", "1.", names[0], leaderBoardScores[0]);
            g.drawString(displayLeaderboard, 750, 140);

            g.setColor(Color.LIGHT_GRAY);
            displayLeaderboard = String.format("%5s%13s%10s", "2.", names[1], leaderBoardScores[1]);
            g.drawString(displayLeaderboard, 750, 180);

            g.setColor(new Color(153, 102, 51));
            displayLeaderboard = String.format("%5s%13s%10s", "3.", names[2], leaderBoardScores[2]);
            g.drawString(displayLeaderboard, 750, 220);

            g.setColor(Color.WHITE);
            displayLeaderboard = String.format("%5s%13s%10s", "4.", names[3], leaderBoardScores[3]);
            g.drawString(displayLeaderboard, 750, 260);

            displayLeaderboard = String.format("%5s%13s%10s", "5.", names[4], leaderBoardScores[4]);
            g.drawString(displayLeaderboard, 750, 300);


        }
    }

    /**
     * Draws text boxes and dialogue
     * Pre: Graphics g and g2D are passed
     * Post: text boxes and dialogue are drawn
     */
    public void drawTextBoxes(Graphics g, Graphics2D g2D) {
        if (textOn) {
            g2D.drawImage(textBox, 700, 185, null);
            g2D.setPaint(Color.BLACK);
            g.setFont(new Font("ComicSans", Font.BOLD, 15));
            g.drawString(text, 750, 220);
            g.drawString(textLine2, 750, 240);
        }
    }

    /**
     * Draws sprites and components of attack 1
     * Pre: Graphics g2D is passed
     * Post: Attack 1 is drawn
     */
    public void drawAttack1(Graphics2D g2D) {
        if (strike1) { //Randomly chooses between two strike positions
            if (!doStrike) { //First warns player of where strike appears, then sends down the lightning
                for (int i = 0; i < STRIKES; i++) {
                    g2D.drawImage(warningBox1, attack1X, 600, null);
                    attack1X += 100;
                }

            } else {
                for (int i = 0; i < STRIKES; i++) {
                    g2D.drawImage(lightning, attack1X, BORDER_UP, null);
                    if (characterHitBox.intersects(attack1X, BORDER_UP, 50, 245)) { //Checks if player hitbox rectangle intersects with each strike zone
                        takeDamage(12);
                    }
                    attack1X += 100;
                }
            }
            attack1X = 350;
        } else {
            if (!doStrike) {
                for (int i = 0; i < STRIKES; i++) {
                    g2D.drawImage(warningBox2, attack1X, 600, null);
                    attack1X += 100;
                }

            } else {
                for (int i = 0; i < STRIKES; i++) {
                    g2D.drawImage(lightning2, attack1X, BORDER_UP, null);
                    if (characterHitBox.intersects(attack1X, BORDER_UP, 50, 245)) {
                        takeDamage(12);
                    }
                    attack1X += 100;
                }
            }
            attack1X = 400;
        }
    }

    /**
     * Controls the strikes and animations of attack 1
     * Pre: None
     * Post: Strike position is chosen and timing is kept
     */
    public void callAttack1() {
        if (specificAttackCount != 20) { //Checks if attack has been preformed a certain amount of times
            if (!attacking) {
                animationTimer = System.currentTimeMillis();
                attacking = true;
            }
            if (specificAttackCount % 2 == 0) { //initiates strike
                if (System.currentTimeMillis() - animationTimer > 400) {
                    doStrike = true;
                    attacking = false;
                    specificAttackCount++;
                }
            } else { //start warning phase
                if (System.currentTimeMillis() - animationTimer > 450) {
                    doStrike = false;
                    attacking = false;
                    specificAttackCount++;
                    strike1 = rand.nextBoolean();
                }
            }

        } else {
            attackOver();
        }
    }

    /**
     * Draws attack 2 sprites and animates the attack
     * Pre: Graphics g2D is passed
     * Post: attack 2 is drawn
     */
    public void drawAttack2(Graphics2D g2D) {
        for (int i = 0; i < sweepingBoneX.length / 2; i++) { //Draws first pair of bones
            g2D.drawImage(bone, sweepingBoneX[i], BORDER_UP, null);
            if (i % 2 == 0) {
                g2D.drawImage(boneGap, sweepingBoneX[i], boneGapY1, null); //Makes sure each pair has the same gap for the player to jump through
            } else {
                g2D.drawImage(boneGap, sweepingBoneX[i], boneGapY2, null);
            }
        }

        for (int i = sweepingBoneX.length / 2; i < sweepingBoneX.length; i++) { //Draws second pair
            g2D.drawImage(bone, sweepingBoneX[i], BORDER_UP, null);
            if (i % 2 == 0) {
                g2D.drawImage(boneGap, sweepingBoneX[i], boneGapY1, null);
            } else {
                g2D.drawImage(boneGap, sweepingBoneX[i], boneGapY2, null);
            }
        }
    }

    /**
     * Moves bones and detects for collision
     * Pre: None
     * Post: X position of bones change and collision is checked
     */
    public void callAttack2() {
        if (specificAttackCount != 20) { //Checks if attack has been preformed a certain amount of times
            for (int i = 0; i < sweepingBoneX.length / 2; i++) {
                if (sweepingBoneX[i] > RIGHT_BONE_LOCATION) { //Once bone reaches endpoint, send it back to original location
                    if (i % 2 == 0) {
                        boneGapY1 = rand.nextInt(100) + 480; //Randomly sets the location of the gap the player must jump through
                    }
                    sweepingBoneX[i] = LEFT_BONE_LOCATION;
                }
            }
            for (int i = sweepingBoneX.length / 2; i < sweepingBoneX.length; i++) {
                if (sweepingBoneX[i] < LEFT_BONE_LOCATION) {
                    if (i % 2 == 1) {
                        boneGapY2 = rand.nextInt(100) + 480;
                    }

                    sweepingBoneX[i] = RIGHT_BONE_LOCATION;
                    specificAttackCount++;

                }
            }
            for (int i = 0; i < sweepingBoneX.length / 2; i++) {
                sweepingBoneX[i] += BONE_SPEED; //Moves the bones
            }
            for (int i = sweepingBoneX.length / 2; i < sweepingBoneX.length; i++) {
                sweepingBoneX[i] -= BONE_SPEED;
            }


            for (int i = 0; i < sweepingBoneX.length; i++) { //Constantly checks whether player hitbox rectangle intercepts with bone and not gap
                if (i % 2 == 0) {
                    if (characterHitBox.intersects(sweepingBoneX[i], (BORDER_UP + 5), 40, boneGapY1 - (BORDER_UP + 5) - 10)) { //Checks for the top half of the bone (bone to gap)
                        takeDamage(8);
                    }
                    if (characterHitBox.intersects(sweepingBoneX[i], boneGapY1 + 60, 40, BOTTOM_Y)) { //Checks for the bottom half of the bone (gap to bone)
                        takeDamage(8);
                    }
                } else {
                    if (characterHitBox.intersects(sweepingBoneX[i], (BORDER_UP + 5), 40, boneGapY2 - (BORDER_UP + 5) - 10)) {
                        takeDamage(8);
                    }
                    if (characterHitBox.intersects(sweepingBoneX[i], boneGapY2 + 60, 40, BOTTOM_Y)) {
                        takeDamage(8);
                    }
                }

            }
        } else {
            attackOver();
        }
    }

    /**
     * Calculates beam line that tracks player and controls time intervals it is displayed
     * Pre: None
     * Post: Beam equation is calculated and display variables are passed
     */
    public void callAttack3() {
        if (specificAttackCount != 20) { //Checks if attack has been preformed a certain amount of times
            if (initialTime < 20) { //Gives a delay before the attack
                initialTime++;
            } else {
                if (!attacking) { //Makes sure another line is not generated prematurely
                    do {
                        xOfLine = rand.nextInt(1100) + 50; //Chooses an x position for the first point of the line so that it is not in the border or boss
                    }
                    while (xOfLine > BORDER_LEFT - 80 && xOfLine < BORDER_RIGHT + 80);
                    yOfLine = rand.nextInt(500) + 50; //Chooses the y coordinate for the point
                    slope = (yOfLine - (character.getY() + HITBOX_ADJUSTMENT)) / (xOfLine - (character.getX() + HITBOX_ADJUSTMENT)); //Calculates the slope of the generated point and the (x,y) of the character
                    lineBComponent = Math.round(yOfLine - slope * xOfLine); //Calculates the y intercept for the line to complete the equation y=mx+b
                    if (xOfLine < 600) { //Depending on the side of the line, generate a point off-screen to make line seem continuous
                        lineY = (BEAM_RIGHT_LOCATION * slope + lineBComponent);
                    } else {
                        lineY = (BEAM_LEFT_LOCATION * slope + lineBComponent);
                    }
                    attacking = true;
                    animationTimer = System.currentTimeMillis();
                }
                if (System.currentTimeMillis() - animationTimer > 600) { //Times when to draw portal
                    drawBeam = false;
                    attacking = false;
                    specificAttackCount++;
                } else if (System.currentTimeMillis() - animationTimer > 200 && !drawBeam) { //Times when to draw line
                    drawBeam = true;
                    colorPicked = rand.nextInt(4) + 1; //Chooses the color
                }
            }
        } else {
            attackOver();
        }
    }

    /**
     * Draws attack 3
     * Pre: None
     * Post: Attack 3 is drawn
     */
    public void drawAttack3(Graphics2D g2D) {
        if (colorPicked == 1) { //Randomly chooses a color for the line
            g2D.setPaint(Color.RED);
        } else if (colorPicked == 2) {
            g2D.setPaint(Color.BLUE);
        } else if (colorPicked == 3) {
            g2D.setPaint(Color.YELLOW);
        } else {
            g2D.setPaint(Color.GREEN);
        }

        g2D.setStroke(new BasicStroke(25));
        if (yOfLine != 0 && !drawBeam) { //If line is not being drawn, draw portal to indicate location of the lines first position
            g2D.drawImage(portal, (int) xOfLine, (int) yOfLine, null);
        }
        if (drawBeam) {
            if (xOfLine > 600) {
                g2D.drawLine((int) xOfLine, (int) yOfLine, BEAM_LEFT_LOCATION, (int) lineY);
            } else {
                g2D.drawLine((int) xOfLine, (int) yOfLine, BEAM_RIGHT_LOCATION, (int) lineY);
            }
        }
        attack3Collision();
    }

    /**
     * Calculates the shortest distance of the player to the line for collision detection
     * Pre: None
     * Post: Distance is calculated and collision is checked
     */
    public void attack3Collision() {
        perpendicularSlope = -1 / slope; //Calculates the perpendicular slope of the line
        lineBComponent2 = (character.getY() + HITBOX_ADJUSTMENT) - ((character.getX() + HITBOX_ADJUSTMENT) * perpendicularSlope); //Finds the y intercept for the line that is perpendicular to the beam and cross through the (x,y) of the player
        distanceLineXInt = (lineBComponent2 - lineBComponent) / (slope - perpendicularSlope); //Finds the x point of intersection for the perpendicular line and the beam line
        distanceLineYInt = (slope * distanceLineXInt + lineBComponent); //Finds the y point by subbing in the x into the equation
        distanceOfPlayerToBeam = Math.abs(Math.sqrt(Math.pow(distanceLineXInt - (character.getX() + HITBOX_ADJUSTMENT), 2) + Math.pow(distanceLineYInt - (character.getY() + 10), 2))); //Calculates distance using the players coordinate and the coordinate of intersection
        if (distanceOfPlayerToBeam > -1 && distanceOfPlayerToBeam < 20 && drawBeam) { //if player is 20 pixels from the beam line, take damage
            takeDamage(2);
        }
    }

    /**
     * Rotates bones and detects collision for attack 4
     * Pre: None
     * Post: Bones have y changed and collision is detected
     */
    public void callAttack4() {
        if (specificAttackCount < 50) {
            if (!attacking) { //Sets the position of the player onto the platform when the attack is called for the first time
                character.setLocation(platformX + 15, PLATFORM_Y - 25);
                characterHitBox.setLocation(character.getX() + HITBOX_ADJUSTMENT, character.getY() + HITBOX_ADJUSTMENT);
                attacking = true;
            }
            if (moveRight && platformX < BORDER_RIGHT - 20) { //Move the platform back and forth the border
                platformX += 5;
            } else {
                moveRight = false;
            }
            if (!moveRight && platformX > BORDER_LEFT) {
                platformX -= 5;
            } else {
                moveRight = true;
            }

            if (characterHitBox.intersects(BORDER_LEFT, PLATFORM_Y + 30, 500, BORDER_DOWN - PLATFORM_Y)) { //Damages the player if they fall off the platform
                takeDamage(1);
            }

            for (int i = 0; i < revolvingBonesY.length; i++) { //Sends the revolving bones back to the bottom of the screen when they reach the top of the border
                revolvingBonesY[i] -= 5;
                if (revolvingBonesY[i] <= BORDER_UP - 40) {
                    revolvingBonesY[i] = BORDER_DOWN + 40;
                    specificAttackCount++;

                }
                if (characterHitBox.intersects(REVOLVING_BONES_X[i] - 10, revolvingBonesY[i] + 10, 30, 50)) { //Checks if the player gets hit by the revolving bones
                    takeDamage(2);
                }
            }
        } else {
            System.out.print(specificAttackCount);
            attackOver();
        }
    }

    /**
     * Draws attack 4
     * Pre: Graphics g2D are passed
     * Post: Attack 4 is drawn
     */
    public void drawAttack4(Graphics2D g2D) {
        g2D.drawImage(platform, platformX, PLATFORM_Y, null);
        g2D.drawImage(boneRow, BORDER_LEFT, PLATFORM_Y + 30, null);
        for (int i = 0; i < revolvingBonesY.length; i++) {
            g2D.drawImage(revolvingBone, REVOLVING_BONES_X[i], revolvingBonesY[i], null);
        }
    }

    /**
     * Detects whether the player is on the platform sets it as the "floor"
     * Pre: None
     * Post: Players floor position is set
     */
    public void attack4PlatformMove() {
        if (Math.abs(character.getY() - 620) < 10) { //If player is on the bottom of the border
            floorTouch = true; //Allows the player to jump again
            gravityOn = false; //Turns off the force pulling the player down
            platformTouch = false;
            initialY = 620; //Decreases the maximum jump height
        } else if (PLATFORM_Y - character.getY() > 20 && PLATFORM_Y - character.getY() < 30 && character.getX() - platformX > -30 && character.getX() - platformX < 50) { //Checks if player in on the platform
            if (moveRight && character.getX() + 5 < BORDER_RIGHT) { //Moves the character along with the platform
                character.setLocation(character.getX() + 5, character.getY());
                characterHitBox.setLocation(character.getX() + HITBOX_ADJUSTMENT, character.getY() + HITBOX_ADJUSTMENT);
            } else if (!moveRight && character.getX() - 5 > BORDER_LEFT) {
                character.setLocation(character.getX() - 5, character.getY());
                characterHitBox.setLocation(character.getX() + HITBOX_ADJUSTMENT, character.getY() + HITBOX_ADJUSTMENT);
            }
            floorTouch = true;
            gravityOn = false;
            platformTouch = true;
            initialY = 590; //Increases maximum jump height
        } else if (PLATFORM_Y - character.getY() > 20 && PLATFORM_Y - character.getY() < 30 && (!(character.getX() - platformX > -30) || !(character.getX() - platformX < 50)) && platformTouch) { //Checks if player has jumped off the platform
            floorTouch = false;
            gravityOn = true;
            platformTouch = false;
        } else { //If player in anywhere in between the two "floors"
            platformTouch = false;
            floorTouch = false;
        }
    }

    /**
     * Calls assets of attack 5 and detects collision
     * Pre: None
     * Post: Assets of attack 5 are called and collision is enabled
     */
    public void callAttack5() {
        if (specificAttackCount != 30) { //Checks if attack has been preformed a certain amount of times
            for (int i = 0; i < crossingBonesX.length; i++) { //Moves the bones
                crossingBonesX[i] -= 6;
                crossingBonesX2[i] += 6;
                if (crossingBonesX[i] < 200) {
                    crossingBonesX[i] = 1200;
                    specificAttackCount++;
                }
                if (crossingBonesX2[i] > 1000) {
                    crossingBonesX2[i] = 0;
                }
                if (characterHitBox.intersects(crossingBonesX[i], 400, 30, 200) || characterHitBox.intersects(crossingBonesX2[i], 600, 20, 50)) { //Checks if player intersects with the bones
                    takeDamage(2);
                }
            }
        } else {
            attackOver();
        }

    }

    /**
     * Draws attack 5
     * Pre: None
     * Post: Attack 5 is drawn
     */
    public void drawAttack5(Graphics g2D) {
        for (int i = 0; i < crossingBonesX.length; i++) {
            g2D.drawImage(crossingBone, crossingBonesX[i], 400, null);
            g2D.drawImage(revolvingBone, crossingBonesX2[i], 600, null);
        }
    }

    /**
     * Turns all attack values to default
     * Pre: None
     * Post: Attack values are set back to default
     */
    public void attackOver() {
        int[] originalCrossingBonesX = {1100, 1240, 1380, 1520, 1660, 1800, 1940};
        int[] originalCrossingBonesX2 = {-740, -600, -460, -320, -180, -40, 100};
        int[] originalSweepingBoneX = {275, -25, 875, 1175};
        initialY = 620;
        jumpBuffer = false;
        character.setLocation(CHARACTER_START_X, CHARACTER_START_Y);
        characterHitBox.setLocation(character.getX() + HITBOX_ADJUSTMENT, character.getY() + HITBOX_ADJUSTMENT);
        doStrike = false;
        specificAttackCount = 0;
        floorTouch = true;
        gravityOn = false;
        sweepingBoneX = originalSweepingBoneX;
        crossingBonesX = originalCrossingBonesX;
        crossingBonesX2 = originalCrossingBonesX2;
        drawBeam = false;
        yOfLine = 0;
        xOfLine = 0;
        initialTime = 0;
        platformX = BORDER_LEFT;
        attackOn = false;
        attacking = false;
        wPress = false;
        sPress = false;
        aPress = false;
        dPress = false;
        platformTouch = false;
        selection = true;
        endlessCounter++;
        jumpBufferCount = 0;
        if (!endlessOn) {
            attackCount++;
        } else {
            attackCount = rand.nextInt(5);
        }
    }

    /**
     * Subtracts player HP based on damageTaken
     * Pre: DamageTaken is positive
     * Post: HP is decreased
     */
    public void takeDamage(int damageTaken) {
        if (hpLostWidth - damageTaken <= -100) { //If the player takes lethal damage, set HP to the maximum and call gameOver
            hpLostWidth = -100;
            gameOver();
        } else if (!isInvincible) { //If the attack has invincibility frames, allow the player to be immune from damage
            hpLostWidth -= damageTaken;
            isInvincible = true;
            initialTime = System.currentTimeMillis();
        } else if (attackCount == 2 || attackCount == 3 || attackCount == 4) { //If attack does not have invincibility frames, allow the player to take multiple instances of damage
            hpLostWidth -= damageTaken;
        }
    }

    /**
     * If player HP reaches 0, initiate death sequence and set game to default settings. If game is in endless mode, checks if score is good enough for leaderboard
     * Pre: None
     * Post: gameOver sequence is initiated and game is reset
     */
    public void gameOver() {
        gameOn = false;
        gameOver = true;
        bossAnimation.setVisible(false);
        if (!deathAnimation) {
            initialTime = System.currentTimeMillis();
            deathAnimation = true;
        }
        clip.stop();
        clip.close();
        if (endlessGame && endlessCounter > leaderBoardScores[4]) { //If score is greater than bottom score on leaderboard, ask player to input a username
            try {
                writeToLeaderBoard(JOptionPane.showInputDialog("CONGRATS! NEW HIGHSCORE! Enter name with no spaces: "), endlessCounter);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            getLeaderBoard(); //Updates leaderboard
        } catch (Exception e) {
            e.printStackTrace();
        }
        endlessCounter = 0;
    }

    /**
     * If player defeats boss, initiates the genocide win sequence and set game back to default
     * Pre: None
     * Post: Genocide win is initiated and game is set to default
     */
    public void genocideWin() {
        gameOn = false;
        genocideWin = true;
        initialTime = System.currentTimeMillis();
        clip.stop();
        clip.close();
        endlessCounter = 0;
    }

    /**
     * If player wins through mercy, initiates the mercy win sequence and set game back to default
     * Pre: None
     * Post: Mercy win is initiated and game is set to default
     */
    public void pacifistWin() {
        pacifistWin = true;
        gameOn = false;
        initialTime = System.currentTimeMillis();
        clip.stop();
        clip.close();
        endlessCounter = 0;
    }

    /**
     * Change the movement settings when gravity is on
     * Pre: None
     * Post: Movement settings are change
     */
    public void gravMove(KeyEvent e) {
        if (floorTouch) {
            if (e.getKeyChar() == 'w') {
                wPress = true;
                floorTouch = false; //Makes sure player cannot jump midair
            }
        }
        if (e.getKeyChar() == 's') {
            if (attackCount != 3) { //Makes sure player cannot phase through platform
                sPress = true;
            }
        }
        if (e.getKeyChar() == 'a') {
            aPress = true;
        }
        if (e.getKeyChar() == 'd') {
            dPress = true;
        }
    }

    /**
     * Changes visuals and display for selection options
     * Pre: None
     * Post: visuals and display for selection options are changed
     */
    public void SelectionControl(KeyEvent e) {
        if (selector == 2 && pressed) {
            if (e.getKeyChar() == 'w') {
                if (itemRow != 0) {
                    itemRow--;
                    itemSelector = items[itemRow][itemColumn];
                }
            }
            if (e.getKeyChar() == 's') {
                if (itemRow != 1) {
                    itemRow++;
                    itemSelector = items[itemRow][itemColumn];
                }
            }
            if (e.getKeyChar() == 'a') {
                if (itemColumn != 0) {
                    itemColumn--;
                    itemSelector = items[itemRow][itemColumn];
                }
            }
            if (e.getKeyChar() == 'd') {
                if (itemColumn != 1) {
                    itemColumn++;
                    itemSelector = items[itemRow][itemColumn];
                }
            }
        } else {
            if (e.getKeyChar() == 'a') {
                if (selector == 0) {
                    selector = 2;
                } else {
                    selector--;
                }
            }
            if (e.getKeyChar() == 'd') {
                if (selector == 2) {
                    selector = 0;
                } else {
                    selector++;
                }
            }
        }
    }

    /**
     * Plays music
     * Pre: File path is found
     * Post: Music is played
     */
    public void playMusic(String filepath) {
        try {
            File musicPath = new File(filepath);
            if (musicPath.exists()) {
                AudioInputStream audio = AudioSystem.getAudioInputStream(musicPath);
                clip = AudioSystem.getClip();
                clip.open(audio);
                clip.start();
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    /**
     * Detects key inputs for game
     * Pre: None
     * Post: Key inputs are detected
     */
    public void keyPressed(KeyEvent e) {
        if (!gameOn) { //Detects start key to begin game
            if (e.getKeyChar() == 'z') {
                startGame();
                text = "Alright kid, lets get this over with";
                textOn = true;
            } else if (e.getKeyChar() == 'g') { //Starts endless mode game
                startGame();
                text = "Your in for a real bad time";
                textOn = true;
                endlessOn = true;
                endlessGame = true;
            }
        } else {
            if (attackOn) {
                if (gravityMoveOn) { //If gravity is on, detect a different setting of the following keys
                    gravMove(e);
                } else {
                    if (e.getKeyChar() == 'w') { //Sets up movement true
                        wPress = true;
                    }
                    if (e.getKeyChar() == 's') { //Sets down movement true
                        sPress = true;
                    }
                    if (e.getKeyChar() == 'a') { //Sets left movement true
                        aPress = true;
                    }
                    if (e.getKeyChar() == 'd') { //Sets right movement true
                        dPress = true;
                    }
                }
            } else {
                SelectionControl(e); //If selection phase is on, keys change display of choice options
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (textOn && !(pacifistWin || genocideWin)) { //Closes dialogue
                textOn = false;
                attackOn = true;

            }
            if (selection) { //Selects a choice in selection phase
                if (!pressed) {
                    pressed = true;
                } else if (selector == 0 && !endlessGame) {
                    attackOptionFinish = true; //Ends attack option
                } else if (selector == 2) {
                    itemPressed(); //Consumes an item
                }
            }
        }
    }

    /**
     * Counts mercy selections and initiates pacifistWin
     * Pre: None
     * Post: Mercy counter is increased and the call of pacifistWin is checked
     */
    public void mercyPressed() {
        mercyCount++;
        if (mercyCount == 5) { //If player selects mercy choice 5 times
            pacifistWin();
            text = "huh";
        } else {
            text = "weird";
        }
        textOn = true;
        pressed = false;
        selection = false;
        selector = 0;
    }

    /**
     * if food items are available, consumes food item and removes if from future consumption
     * Pre: None
     * Post: Food item is consumed and item is blocked
     */
    public void itemPressed() {
        if (items[0][0] == -1 && items[0][1] == -1 && items[1][0] == -1 && items[1][1] == -1) { //If all food has been consumed, kickplayer out of the option
            text = "Ur kinda out of food pal";
            pressed = false;
            selection = false;
        } else if (items[itemRow][itemColumn] != -1) { //If player consumes butterscotch pie
            if (itemSelector == 1) {
                if (hpLostWidth + BUTTERSCOTCH_HP >= 0) {
                    hpLostWidth = 0;
                } else {
                    hpLostWidth += BUTTERSCOTCH_HP;
                }
            } else if (itemSelector == 2) { //If player consumes steak
                if (hpLostWidth + STEAK_HP >= 0) {
                    hpLostWidth = 0;
                } else {
                    hpLostWidth += STEAK_HP;
                }
            } else if (itemSelector == 3) { //If player consumes legendary hero
                if (hpLostWidth + LEGENDARY_HP >= 0) {
                    hpLostWidth = 0;
                } else {
                    hpLostWidth += LEGENDARY_HP;
                }
            } else {
                if (hpLostWidth + APPLE_HP >= 0) { //If player consumes apple
                    hpLostWidth = 0;
                } else {
                    hpLostWidth += APPLE_HP;
                }
            }
            pressed = false;
            selection = false;
            text = "looks yummy";
            items[itemRow][itemColumn] = -1;
        }
        //Blocks consumed food item
        if (items[0][0] != -1) {
            itemSelector = 1;
            itemRow = 0;
            itemColumn = 0;
        } else if (items[0][1] != -1) {
            itemSelector = 3;
            itemRow = 0;
            itemColumn = 1;
        } else if (items[1][0] != -1) {
            itemSelector = 2;
            itemRow = 1;
            itemColumn = 0;
        } else if (items[1][1] != -1) {
            itemSelector = 4;
            itemRow = 1;
            itemColumn = 1;
        }
        textOn = true;
    }

    /**
     * Releases key and its settings
     * Pre: None
     * Post: key and its settings are set back to default
     */
    public void keyReleased(KeyEvent e) {
        if (e.getKeyChar() == 'w') {
            wPress = false;
            if (gravityMoveOn) {
                jumpBuffer = true; //When player jumps while gravity is on, calls method to smooth out the motion
            }
        }

        if (e.getKeyChar() == 's') {
            sPress = false;
        }

        if (e.getKeyChar() == 'a') {
            aPress = false;
        }

        if (e.getKeyChar() == 'd') {
            dPress = false;
        }
    }

    /**
     * Moves character upwards to a max distance before bringing them back down
     * Pre: None
     * Post: Player jumps with gravity
     */
    public void gravityJump() {
        if (wPress) {
            if (Math.abs(character.getY() - initialY) < MAX_JUMP_DISTANCE) {
                character.setLocation(character.getX(), character.getY() - JUMP_SPEED);
                characterHitBox.setLocation(character.getX() + HITBOX_ADJUSTMENT, character.getY() - JUMP_SPEED + HITBOX_ADJUSTMENT);
            } else {
                jumpBuffer = true;
            }
        }
    }

    /**
     * Moves player and hitbox around
     * Pre: None
     * Post: Player and hitbox are moved around
     */
    public void move() {
        if (!gravityMoveOn) {
            if (wPress && character.getY() - CHARACTER_SPEED > BORDER_UP) {
                character.setLocation(character.getX(), character.getY() - CHARACTER_SPEED);
                characterHitBox.setLocation(character.getX() + HITBOX_ADJUSTMENT, character.getY() - CHARACTER_SPEED + HITBOX_ADJUSTMENT);
            }
        } else {
            gravityJump();
        }
        if (sPress && character.getY() + CHARACTER_SPEED < BORDER_DOWN) {
            character.setLocation(character.getX(), character.getY() + HITBOX_ADJUSTMENT);
            characterHitBox.setLocation(character.getX() + HITBOX_ADJUSTMENT, character.getY() + CHARACTER_SPEED + HITBOX_ADJUSTMENT);
        }
        if (aPress && character.getX() - CHARACTER_SPEED > BORDER_LEFT) {
            character.setLocation(character.getX() - CHARACTER_SPEED, character.getY());
            characterHitBox.setLocation(character.getX() + HITBOX_ADJUSTMENT - CHARACTER_SPEED, character.getY() + HITBOX_ADJUSTMENT);
        }
        if (dPress && character.getX() + CHARACTER_SPEED < BORDER_RIGHT) {
            character.setLocation(character.getX() + HITBOX_ADJUSTMENT, character.getY());
            characterHitBox.setLocation(character.getX() + CHARACTER_SPEED + HITBOX_ADJUSTMENT, character.getY() + HITBOX_ADJUSTMENT);
        }
        if (gravityOn && character.getY() + GRAVITY_SPEED < BORDER_DOWN) { //Brings player back down to a floor position
            character.setLocation(character.getX(), character.getY() + GRAVITY_SPEED);
            characterHitBox.setLocation(character.getX() + HITBOX_ADJUSTMENT, character.getY() + CHARACTER_SPEED + HITBOX_ADJUSTMENT);
        } else {
            gravityOn = false;

        }
    }

    /**
     * Initiates attack sequence when attack choice is selected
     * Pre: None
     * Post: attack sequence is initialized
     */
    public void attackPressed() {
        if (attackBarX < BORDER_RIGHT + 30) { //If bar is not at the end of the graph
            if (!attackOptionFinish) {
                attackBarX += 15; //Moves attack bar across the graph
            } else {
                if (attackBarX - 330 < 265) {
                    maxDamageDealt *= (attackBarX - 330) / (double) 265; //Maximizes damage in the middle of the graph and minimizes it on the ends

                } else {
                    maxDamageDealt *= 1 - (attackBarX - 595) / (double) 265;
                }
                bossHP -= maxDamageDealt;
                if (bossHP < 0) { //If boss is defeated after attack, call genocideWin
                    bossHP = 0;
                    genocideWin();
                }
                bossBarWidth *= (bossHP / (double) BOSS_ORIGIN_HP); //Updates visual of bossHP
                bossDamageDisplayNumber = (int) maxDamageDealt;
                damageDisplayTime = System.currentTimeMillis();
                bossHpLost = 300 - (int) bossBarWidth;
                //Sets attack option variables back to default
                selection = false;
                pressed = false;
                maxDamageDealt = 1600;
                bossBarWidth = 300;
                attackBarX = 330;
                attackOptionFinish = false;
                showDamageDealt = true;
                text = "yeesh";
                textOn = true;
            }
        } else { //If player lets graph travel all the way to the end
            maxDamageDealt = 1;
            bossHP -= maxDamageDealt;
            bossDamageDisplayNumber = (int) maxDamageDealt;
            damageDisplayTime = System.currentTimeMillis();
            maxDamageDealt = 1600;
            selection = false;
            pressed = false;
            bossBarWidth = 300;
            attackBarX = 330;
            showDamageDealt = true;
            text = "TICKELS KID";
            textOn = true;
        }
    }

    /**
     * Smooths out gravity movement after player stops ascending
     * Pre: None
     * Post: Motion is smoothed out
     */
    public void gravityBuffer() {
        if (jumpBufferCount < 3) { //For 3 ticks, player continues moving up slower then normal
            character.setLocation(character.getX(), character.getY() - 4);
            characterHitBox.setLocation(character.getX() + HITBOX_ADJUSTMENT, character.getY() - 5 + HITBOX_ADJUSTMENT);
            jumpBufferCount++;
        } else if (jumpBufferCount < 6) { //For 3 more ticks, player moves down slower then normal
            character.setLocation(character.getX(), character.getY() + 4);
            characterHitBox.setLocation(character.getX() + HITBOX_ADJUSTMENT, character.getY() + 5 + HITBOX_ADJUSTMENT);
            jumpBufferCount++;
        } else {
            //Calls normal gravity
            gravityOn = true;
            wPress = false;
            jumpBuffer = false;
            jumpBufferCount = 0;
        }
    }

    /**
     * Timer that sets the pace of game and detects game functions
     * Pre: None
     * Post: Timer detects and initiates game functions
     */
    public void actionPerformed(ActionEvent e) {
        if (gameOn) {
            move(); //Allows for player movement
            if (selection && pressed) { //Detects if a choice was made
                if (endlessGame && selector != 2) {
                    selection = false;
                    pressed = false;
                    text = "You poor thing~";
                    textOn = true;
                } else if (selector == 0) {
                    attackPressed();
                } else if (selector == 1) {
                    mercyPressed();
                }
            }
            if (attackOn) { //Calls the various attacks of the game
                if (attackCount == 0) {
                    callAttack1();
                    gravityMoveOn = false;
                } else if (attackCount == 1) {
                    callAttack2();
                    gravityMoveOn = true;
                } else if (attackCount == 2) {
                    callAttack3();
                    gravityMoveOn = false;
                } else if (attackCount == 3) {
                    callAttack4();
                    gravityMoveOn = true;
                } else if (attackCount == 4) {
                    callAttack5();
                    gravityMoveOn = true;
                } else {
                    endlessOn = true;
                    attackCount = rand.nextInt(5); //Randomly chooses an attack after attack cycle is finished or game is endless
                }

                if (isInvincible) { //Disables invincibility after expire time has passed
                    if (System.currentTimeMillis() - initialTime > INVISIBILITY_EXPIRE_TIME) {
                        isInvincible = false;
                    }
                }
                if (gravityMoveOn) {
                    if (jumpBuffer) { //Calls buffer when player reaches max jump height or w key is released
                        gravityBuffer();
                    }
                    if (attackCount == 1 || attackCount == 4) { //Determines the floor settings for the attacks with gravity
                        if (Math.abs(character.getY() - 620) < 10) {
                            floorTouch = true;
                            gravityOn = false;
                        } else {
                            floorTouch = false;
                        }
                    } else if (attackCount == 3) {
                        attack4PlatformMove();
                    }
                }
            }
        }
        repaint(); //Updates the graphics for the game every 20 milliseconds
    }
}
