import javax.swing.*;
/**
 * Ken Chen
 * 2023/06/06
 * Sets frame values and creates panel to add to frame
 */
public class GameFrame extends JFrame{
    GameFrame()  {
        this.add(new GamePanel());
        this.setTitle("Lowertale");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);
    }
}
