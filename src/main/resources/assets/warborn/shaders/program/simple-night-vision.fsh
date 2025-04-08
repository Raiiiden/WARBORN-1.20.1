#version 120

// Night vision from 4rknova
// https://www.shadertoy.com/view/Xsl3zf
// by Nikos Papadopoulos, 4rknova / 2013
// WTFPL

// Adjust for minecraft



uniform sampler2D DiffuseSampler;
uniform float Time;
varying vec2 texCoord;
varying vec2 oneTexel;
varying vec4 outPos;
uniform vec2 InSize;

float hash(in float n) { return fract(sin(n) * 43758.5453123); }

void main() {
    vec2 p = texCoord;
    vec3 c = texture2D(DiffuseSampler, p).xyz;

    c += sin(hash(Time)) * 0.01;
    c += hash((hash(p.x) + p.y) * Time) * 0.1;

    vec2 u = p * 2.0 - 1.0;
    float aspectRatio = InSize.x / InSize.y;
    vec2 n = u * vec2(aspectRatio, 1.0);

    float vignette = 1.0 - smoothstep(0.7, 1.5, length(n));
    c *= vignette * 1.5;

    float gray = dot(c, vec3(0.2126, 0.7152, 0.0722));
    c = vec3(gray * 0.2, gray * 1.0, gray * 0.2); // Green tint

    // Somebrightening 
    c *= 2.5; // Increased brightness

    gl_FragColor = vec4(c, 1.0);
}