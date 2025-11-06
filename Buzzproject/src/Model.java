/* I declare that this code is my own work
 * Author: <Your Name> <your@email>
 */
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.texture.Texture;

public class Model {
  private final Mesh mesh;
  private final Material material;
  private Mat4 M;

  public Model(Mesh mesh, Material material, Mat4 M) {
    this.mesh = mesh;
    this.material = material;
    this.M = new Mat4(M);
  }

  public void setMatrix(Mat4 M) { this.M = new Mat4(M); }

  public void draw(GL3 gl, Shader shader) {
    shader.setFloatArray(gl, "model", M.toFloatArrayForGLSL());

    // bind textures to fixed units
    int DIFFUSE_UNIT = 0, SPEC_UNIT = 1, EMIS_UNIT = 2;
    if (material.diffuse != null) { material.diffuse.bind(gl); shader.setInt(gl, "material.diffuse", DIFFUSE_UNIT); }
    if (material.specular != null){ material.specular.bind(gl); shader.setInt(gl, "material.specular", SPEC_UNIT); }
    if (material.emission != null){ material.emission.bind(gl); shader.setInt(gl, "material.emission", EMIS_UNIT); }
    shader.setFloat(gl, "material.shininess", material.shininess);

    mesh.render(gl);
  }
}
