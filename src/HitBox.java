import java.awt.*;

/**
 * Ken Chen
 * 2023/06/06
 * Creates rectangle under character imageIcon to act as hitbox
 */
public class HitBox extends Rectangle {
    HitBox(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
}
