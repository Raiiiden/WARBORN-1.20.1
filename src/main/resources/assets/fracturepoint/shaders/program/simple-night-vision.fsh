#version 120

// Night vision from 4rknova adjusted for my purposes.

uniform sampler2D DiffuseSampler;
uniform float Time;
uniform vec2 InSize;
uniform float CenterOffsetX;

varying vec2 texCoord;

float hash(in float n) { return fract(sin(n) * 43758.5453123); }

void main() {
    vec2 p = texCoord;

    vec3 originalColor = texture2D(DiffuseSampler, p).xyz;
    vec3 nvBase = originalColor;
    nvBase += sin(hash(Time)) * 0.01;
    nvBase += hash((hash(p.x) + p.y) * Time) * 0.1;
    float gray = dot(nvBase, vec3(0.2126, 0.7152, 0.0722));
    vec3 nightVisionColor = vec3(gray * 0.2, gray * 1.0, gray * 0.2); // Green tint
    nightVisionColor *= 2.5;

    vec3 blackColor = vec3(0.0);

    vec2 u = p * 2.0 - 1.0;
    float aspectRatio = InSize.x / InSize.y;
    vec2 n = u * vec2(aspectRatio, 1.0);
    vec2 effectCenter = vec2(CenterOffsetX, 0.0);

    float r = length(n - effectCenter);
    float r1 = 0.45; // end of full NV / Start fade NV->Black
    float r2 = 0.75; // end fade NV->Black / Start fade Black->Normal
    float r3 = 1.2; // end fade Black->Normal (fully Normal beyond this)

    float factorNV = 1.0 - smoothstep(r1, r2, r);
    float factorNormal = smoothstep(r2, r3, r);

    vec3 finalColor = mix(blackColor, nightVisionColor, factorNV);
    finalColor = mix(finalColor, originalColor, factorNormal);

    gl_FragColor = vec4(finalColor, 1.0);
}