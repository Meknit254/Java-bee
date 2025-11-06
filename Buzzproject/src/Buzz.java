/* I declare that this code is my own work
 * Author: <Your Name> <your@email>
 * Main class required by the assignment spec.
 */
import java.awt.*;
import javax.swing.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

public class Buzz {
  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      GLProfile profile = GLProfile.get(GLProfile.GL3);
      GLCapabilities caps = new GLCapabilities(profile);
      GLCanvas canvas = new GLCanvas(caps);

      Scene scene = new Scene(canvas);
      canvas.addGLEventListener(scene);

      JFrame frame = new JFrame("Buzz - Scene");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setLayout(new BorderLayout());
      frame.add(canvas, BorderLayout.CENTER);
      frame.add(scene.buildUI(), BorderLayout.EAST);
      frame.setSize(1280, 800);
      frame.setLocationRelativeTo(null);
      frame.setVisible(true);

      FPSAnimator animator = new FPSAnimator(canvas, 60);
      animator.start();
      canvas.requestFocusInWindow();
    });
  }
}
