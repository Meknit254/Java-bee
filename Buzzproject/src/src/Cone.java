public final class Cone {
  
  private static final int SEGMENTS = 30;
  
  public static final float[] vertices = createVertices();
  public static final int[] indices = createIndices();

  private static float[] createVertices() {
    int step = 8; // x,y,z, nx,ny,nz, s,t
    float[] vertices = new float[(SEGMENTS + 3) * step]; // apex + base_center + (SEGMENTS+1) circle vertices
    float radius = 0.5f;
    float height = 1.0f;
    
    // Apex vertex (top of cone)
    vertices[0] = 0.0f;
    vertices[1] = height;
    vertices[2] = 0.0f;
    vertices[3] = 0.0f;
    vertices[4] = 1.0f;
    vertices[5] = 0.0f;
    vertices[6] = 0.5f;
    vertices[7] = 1.0f;
    
    // Base center vertex
    int baseIdx = step;
    vertices[baseIdx + 0] = 0.0f;
    vertices[baseIdx + 1] = 0.0f;
    vertices[baseIdx + 2] = 0.0f;
    vertices[baseIdx + 3] = 0.0f;
    vertices[baseIdx + 4] = -1.0f;
    vertices[baseIdx + 5] = 0.0f;
    vertices[baseIdx + 6] = 0.5f;
    vertices[baseIdx + 7] = 0.0f;
    
    // Base circle vertices
    for (int i = 0; i <= SEGMENTS; i++) {
      float angle = (float)(2.0 * Math.PI * i / SEGMENTS);
      float x = radius * (float)Math.cos(angle);
      float z = radius * (float)Math.sin(angle);
      
      int idx = (i + 2) * step;
      vertices[idx + 0] = x;
      vertices[idx + 1] = 0.0f;
      vertices[idx + 2] = z;
      
      // Normal for side (pointing outward and upward)
      float nx = x;
      float ny = radius;
      float nz = z;
      float len = (float)Math.sqrt(nx*nx + ny*ny + nz*nz);
      vertices[idx + 3] = nx / len;
      vertices[idx + 4] = ny / len;
      vertices[idx + 5] = nz / len;
      
      vertices[idx + 6] = (float)i / SEGMENTS;
      vertices[idx + 7] = 0.0f;
    }
    
    return vertices;
  }
  
  private static int[] createIndices() {
    int[] indices = new int[SEGMENTS * 3 + SEGMENTS * 3];
    int idx = 0;
    
    // Side triangles (apex to base edge)
    for (int i = 0; i < SEGMENTS; i++) {
      indices[idx++] = 0; // apex
      indices[idx++] = i + 2;
      indices[idx++] = i + 3;
    }
    
    // Base triangles (base center to base edge)
    for (int i = 0; i < SEGMENTS; i++) {
      indices[idx++] = 1; // base center
      indices[idx++] = i + 3;
      indices[idx++] = i + 2;
    }
    
    return indices;
  }
}
