/* I declare that this code is my own work */
import com.jogamp.opengl.util.texture.Texture;

public class Material {
  public final Texture diffuse, specular, emission;
  public final float shininess;
  public Material(Texture diff, Texture spec, Texture emis, float shininess) {
    this.diffuse = diff; this.specular = spec; this.emission = emis; this.shininess = shininess;
  }
}
