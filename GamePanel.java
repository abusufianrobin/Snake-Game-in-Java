import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.JPanel;
import java.util.Random;
import javax.swing.*;  // For GUI components
import java.awt.*;  // For graphics
import java.awt.event.*;  // For event handling
import java.util.Random;  // For random apple placement
import java.io.File;  // For handling audio files
import javax.sound.sampled.*;  // For playing sounds


public class GamePanel extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT)/ UNIT_SIZE;
    static final int  DELAY = 150;
    final int x[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];
    int bodyParts = 6;
    int applesEaten;
    int appleX;
    int appleY;
    char direction = 'R';
    boolean running = false;
    Timer timer;
    Random random;

    GamePanel(){
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH,SCREEN_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();

    }
    public void startGame(){
        playSound("/end.wav");
        newApple();
        running = true;
        timer = new Timer(DELAY,this);
        timer.start();

    }

    public void paintComponent(Graphics graphics){
        super.paintComponent(graphics);
        draw(graphics);
    }


    public void draw(Graphics graphics) {
        if (running) {
            // Draw grid lines (optional)
            for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
                graphics.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
            }

            // Draw apple
            graphics.setColor(Color.RED);
            graphics.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            // Draw snake
            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    graphics.setColor(Color.GREEN);
                } else {
                    graphics.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
                }
                graphics.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }

            // Draw score
            graphics.setColor(Color.RED);
            graphics.setFont(new Font("Ink Free", Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(graphics.getFont());
            graphics.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten)) / 2, graphics.getFont().getSize());
        } else {
            gameOver(graphics);
        }
    }


    public void newApple(){
        appleX = random.nextInt((int)(SCREEN_WIDTH/UNIT_SIZE))*UNIT_SIZE;
        appleY = random.nextInt((int)(SCREEN_HEIGHT/UNIT_SIZE))*UNIT_SIZE;

    }
    public void move(){
        for(int i = bodyParts; i>0; i--){
            x[i] = x[i-1];
            y[i] = y[i-1];
        }

        switch (direction){
            case 'U':
                y[0] = y[0]-UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0]+UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0]-UNIT_SIZE;
                break;

            case 'R':
                x[0] = x[0]+UNIT_SIZE;
                break;
        }

    }

    public void checkApple(){
        if((x[0]==appleX) && (y[0]==appleY)){
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }

    public void checkCollisions(){
        //checks if head collides with body
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
            }
        }

        // Check if head touches borders
        if (x[0] < 0 || x[0] >= SCREEN_WIDTH || y[0] < 0 || y[0] >= SCREEN_HEIGHT) {
            running = false;
        }

        if (!running) {
            timer.stop();
            playSound("/start.wav");  // Play game over music
        }

    }

    public void gameOver(Graphics graphics){

        //Score
        graphics.setColor(Color.RED);
        graphics.setFont(new Font("Ink Free",Font.BOLD,40));
        FontMetrics metrics1 = getFontMetrics(graphics.getFont());
        graphics.drawString("Score: " +applesEaten, (SCREEN_WIDTH - metrics1.stringWidth("Score: " +applesEaten ))/2,graphics.getFont().getSize());

        //Game Over Text
        graphics.setColor(Color.RED);
        graphics.setFont(new Font("Ink Free",Font.BOLD,75));
        FontMetrics metrics2 = getFontMetrics(graphics.getFont());
        graphics.drawString("Game Over", (SCREEN_WIDTH - metrics2.stringWidth("Game Over"))/2,SCREEN_HEIGHT/2);
    }

    @Override
    public void actionPerformed(ActionEvent e){
        if(running){
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter{
        @Override
        public void keyPressed(KeyEvent e){
            switch(e.getKeyCode()){
                case KeyEvent.VK_LEFT:
                    if(direction != 'R'){
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if(direction != 'L'){
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if(direction != 'D'){
                        direction = 'U';
                    }
                    break;

                case KeyEvent.VK_DOWN:
                    if(direction != 'U'){
                        direction = 'D';
                    }
                    break;
            }
        }
    }

    public void playSound(String soundFile) {
        try {
            File file = new File(soundFile);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (Exception e) {
            System.out.println("Error playing sound: " + e.getMessage());
        }
    }
}
