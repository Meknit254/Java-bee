/* I declare that this code is my own work
 * Author: <Your Name> <your@email>
 */
import gmaths.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.texture.Texture;

public class Scene implements GLEventListener, KeyListener, MouseMotionListener, MouseListener {
  private final GLCanvas canvas;
  private Shader shader;
  private Camera camera;
  private TextureLibrary textures;

  // scene content
  private Mesh sphereMesh;
  private Model statue1;

  // input state
  private int prevX, prevY;
  private boolean dragging = false;

  // timing
  private long lastTimeNs;

  // lights
  private Light worldLight;

  // matrices
  private Mat4 P; // projection

  public Scene(GLCanvas canvas) {
    this.canvas = canvas;
    canvas.addKeyListener(this);
    canvas.addMouseMotionListener(this);
    canvas.addMouseListener(this);
  }

  // ---------- UI you will extend ----------
  public JPanel buildUI() {
    JPanel p = new JPanel();
    p.setLayout(new GridLayout(0,1,6,6));
    JButton worldLightToggle = new JButton("Toggle World Light");
    worldLightToggle.addActionListener(e -> worldLight.enabled = !worldLight.enabled);
    p.add(worldLightToggle);

    // stubs for spec controls (you’ll wire them later)
    p.add(new JLabel("Mode:"));
    JButton poseBtn = new JButton("Pose Mode");
    JButton contBtn = new JButton("Continuous Mode");
    p.add(poseBtn); p.add(contBtn);

    JButton toStatue1 = new JButton("Bee -> Statue 1");
    p.add(toStatue1);

    JButton spotlightToggle = new JButton("Toggle Spotlight Sweep");
    p.add(spotlightToggle);

    return p;
  }

  // ---------------------------------------

  @Override
  public void init(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    gl.glEnable(GL.GL_DEPTH_TEST);
    gl.glEnable(GL.GL_CULL_FACE);
    gl.glCullFace(GL.GL_BACK);

    shader = new Shader(gl, "assets/shaders/phong.vert", "assets/shaders/phong.frag");
    textures = new TextureLibrary();

    // fallback & first material textures (stone)
    Texture white = TextureLibrary.loadTexture(gl, "assets/textures/white.png");
    textures.add(gl, "white", "assets/textures/white.png");
    textures.add(gl, "stone_diff", "assets/textures/stone_diffuse.jpg");
    textures.add(gl, "stone_spec", "assets/textures/stone_spec.jpg");
    textures.add(gl, "stone_emis", "assets/textures/stone_emiss.jpg");

    // camera
    camera = new Camera(new Vec3(0, 3, 12), new Vec3(0, 2, 0), new Vec3(0,1,0));
    float aspect = (float)canvas.getWidth() / (float)canvas.getHeight();
    P = Mat4Transform.perspective(45, aspect);

    // world light (you can add another for extra credit)
    worldLight = new Light();
    worldLight.position = new Vec3(6, 8, 6);
    worldLight.colour = new Vec3(1,1,1);
    worldLight.intensity = 1.0f;
    worldLight.enabled = true;

    // geometry
    sphereMesh = new Mesh(gl, Sphere.vertices, Sphere.indices);

    // first statue model (a scaled sphere)
    Material stone = new Material(
      textures.get("stone_diff"),
      textures.get("stone_spec"),
      textures.get("stone_emis"),
      32.0f // shininess
    );

    Mat4 M = Mat4Transform.translate(0, 2.5f, 0);
    M = Mat4.multiply(M, Mat4Transform.scale(1f, 2.5f, 1f)); // menhir-ish
    statue1 = new Model(sphereMesh, stone, M);
    lastTimeNs = System.nanoTime();
  }

  @Override
  public void dispose(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    sphereMesh.dispose(gl);
    textures.destroy(gl);
  }

  @Override
  public void display(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    gl.glClearColor(0.65f,0.8f,1.0f,1); // sky-ish until you add your backdrop
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

    // time (for animation later)
    long now = System.nanoTime();
    float dt = (now - lastTimeNs) / 1_000_000_000f;
    lastTimeNs = now;

    shader.use(gl);
    // camera matrices
    shader.setFloatArray(gl, "view", camera.getViewMatrix().toFloatArrayForGLSL());
    shader.setFloatArray(gl, "projection", P.toFloatArrayForGLSL());
    shader.setVec3(gl, "viewPos", camera.getPosition());

    // world light uniforms
    shader.setInt(gl, "worldLight.enabled", worldLight.enabled ? 1 : 0);
    shader.setFloat(gl, "worldLight.intensity", worldLight.intensity);
    shader.setVec3(gl, "worldLight.position", worldLight.position);
    shader.setFloat(gl, "worldLight.colour", worldLight.colour.x, worldLight.colour.y, worldLight.colour.z);

    // draw statue 1
    statue1.draw(gl, shader);
  }

  @Override
  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    GL3 gl = drawable.getGL().getGL3();
    gl.glViewport(0,0,width,height);
    float aspect = (float)width/(float)height;
    P = Mat4Transform.perspective(45, aspect);
    camera.setPerspectiveMatrix(P);
  }

  // ------------ Input: WASD + QE, Shift for fast, mouse drag to look ------------
  @Override public void keyPressed(KeyEvent e) {
    boolean fast = e.isShiftDown();
    switch (e.getKeyCode()) {
      case KeyEvent.VK_W: camera.keyboardInput(fast ? Camera.Movement.FAST_FORWARD : Camera.Movement.FORWARD); break;
      case KeyEvent.VK_S: camera.keyboardInput(fast ? Camera.Movement.FAST_BACK : Camera.Movement.BACK); break;
      case KeyEvent.VK_A: camera.keyboardInput(fast ? Camera.Movement.FAST_LEFT : Camera.Movement.LEFT); break;
      case KeyEvent.VK_D: camera.keyboardInput(fast ? Camera.Movement.FAST_RIGHT : Camera.Movement.RIGHT); break;
      case KeyEvent.VK_Q: camera.keyboardInput(fast ? Camera.Movement.FAST_DOWN : Camera.Movement.DOWN); break;
      case KeyEvent.VK_E: camera.keyboardInput(fast ? Camera.Movement.FAST_UP : Camera.Movement.UP); break;
    }
    canvas.display();
  }
  @Override public void keyReleased(KeyEvent e) {}
  @Override public void keyTyped(KeyEvent e) {}

  @Override public void mousePressed(MouseEvent e) { dragging = true; prevX = e.getX(); prevY = e.getY(); }
  @Override public void mouseReleased(MouseEvent e) { dragging = false; }
  @Override public void mouseDragged(MouseEvent e) {
    if (!dragging) return;
    float dx = (e.getX() - prevX) * 0.3f;
    float dy = (e.getY() - prevY) * 0.3f;
    camera.updateYawPitch((float)Math.toRadians(dx), (float)Math.toRadians(-dy));
    prevX = e.getX(); prevY = e.getY();
    canvas.display();
  }
  @Override public void mouseMoved(MouseEvent e) {}
  @Override public void mouseClicked(MouseEvent e) {}
  @Override public void mouseEntered(MouseEvent e) {}
  @Override public void mouseExited(MouseEvent e) {}
}
