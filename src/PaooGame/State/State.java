package PaooGame.State;
import java.awt.*;

/**
 * Clasa abstracta care face legatura intre joc si starea curenta.
 */
public interface State {
    void onKeyTyped(char c);
    void Update();
    void Draw(Graphics g);
}
