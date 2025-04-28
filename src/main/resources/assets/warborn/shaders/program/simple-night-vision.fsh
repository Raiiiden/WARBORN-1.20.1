#version 120

// Night vision from 4rknova, modified for split-screen effect
// with vignette centered in the active half and smooth transition seam.

uniform sampler2D DiffuseSampler;
uniform float Time;
varying vec2 texCoord;
varying vec2 oneTexel;
varying vec4 outPos;
uniform vec2 InSize;

float hash(in float n) { return fract(sin(n) * 43758.5453123); }

void main() {
    vec2 p = texCoord;

    vec3 originalColor = texture2D(DiffuseSampler, p).xyz;

    vec3 nightVisionColor = originalColor;

    nightVisionColor += sin(hash(Time)) * 0.01;
    nightVisionColor += hash((hash(p.x) + p.y) * Time) * 0.1;

    float vignette = 1.0;
    float transitionWidth = 0.1;
    float blendEdgeRight = 0.5 + transitionWidth / 2.0;

    if (p.x < blendEdgeRight) {
                                vec2 vignetteCoords = vec2(
                                (p.x / 0.5) * 2.0 - 1.0,
                                p.y * 2.0 - 1.0
                                );
                                float screenAspectRatio = InSize.x / InSize.y;
                                float halfAspectRatio = screenAspectRatio / 2.0;
                                vec2 n_vignette = vignetteCoords * vec2(halfAspectRatio, 1.0);

                                float inner_radius = 0.7;
                                float outer_radius = 0.9;
                                vignette = 1.0 - smoothstep(inner_radius, outer_radius, length(n_vignette));
    }

    nightVisionColor *= vignette * 1.5;

    float gray = dot(nightVisionColor, vec3(0.2126, 0.7152, 0.0722));
    nightVisionColor = vec3(gray * 0.2, gray * 1.0, gray * 0.2);
    nightVisionColor *= 2.5;


    float blendEdgeLeft = 0.5 - transitionWidth / 2.0;
    float blendFactor = smoothstep(blendEdgeRight, blendEdgeLeft, p.x);
    vec3 finalColor = mix(originalColor, nightVisionColor, blendFactor);

    gl_FragColor = vec4(finalColor, 1.0);
}