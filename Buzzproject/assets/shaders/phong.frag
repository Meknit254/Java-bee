#version 330 core
out vec4 FragColor;

in vec3 FragPos;
in vec3 Normal;
in vec2 TexCoord;

struct Material {
  sampler2D diffuse;
  sampler2D specular;
  sampler2D emission;
  float shininess;
};
uniform Material material;

struct WorldLight {
  int   enabled;
  vec3  position;
  vec3  colour;
  float intensity;
};
uniform WorldLight worldLight;

uniform vec3 viewPos;

vec3 sampleOrWhite(sampler2D tex, vec2 uv) {
  return texture(tex, uv).rgb;
}

void main() {
  vec3 norm = normalize(Normal);
  vec3 viewDir = normalize(viewPos - FragPos);

  vec3 ambient = sampleOrWhite(material.diffuse, TexCoord) * 0.1;

  vec3 result = ambient;

  if (worldLight.enabled == 1) {
    vec3 lightDir = normalize(worldLight.position - FragPos);
    float diff = max(dot(norm, lightDir), 0.0);
    vec3 diffuse = diff * sampleOrWhite(material.diffuse, TexCoord);

    vec3 reflectDir = reflect(-lightDir, norm);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
    vec3 specular = spec * sampleOrWhite(material.specular, TexCoord);

    result += (diffuse + specular) * worldLight.colour * worldLight.intensity;
  }

  vec3 emission = sampleOrWhite(material.emission, TexCoord);
  FragColor = vec4(result + emission, 1.0);
}
