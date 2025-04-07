#version 150
// Thermal Vision Shader

// All most all of it is from https://www.shadertoy.com/view/4XtSR4
// Created by curiouspers in 2024-06-18

in vec2 texCoord;
in vec2 oneTexel;

out vec4 fragColor;

uniform sampler2D DiffuseSampler;
uniform sampler2D NoiseSampler;
uniform float Time;
uniform vec2 InSize;

uniform float VignetteEnabled;       // 0.0 = off, 1.0 = on
uniform float VignetteRadius;        // Default: 0.65
uniform float Brightness;            // Overall brightness multiplier
uniform float NoiseAmplification;    // Strength of noise effect

const vec3 lum = vec3(0.2125, 0.4154, 0.0721);

const vec3 thermal_cold = vec3(0.0, 0.0, 1.0);          // Blue (coldest) - more saturated
const vec3 thermal_cool = vec3(0.702, 0.071, 0.980);    // Bright purple
const vec3 thermal_medium = vec3(0.0, 1.0, 0.0);        // Bright green
const vec3 thermal_warm = vec3(1.0, 1.0, 0.0);          // Bright yellow
const vec3 thermal_hot = vec3(1.0, 0.0, 0.0);           // Bright red (hottest)

const float minBlur = 0.0;
const float blurIterations = 4.0;
const float blurDistance = 0.03;
const float pixels = 0.2;

float random(in vec2 st) {
    return fract(sin(dot(st.xy, vec2(12.9898, 78.233))) * 43758.5453123);
}

vec4 blurTex(in sampler2D tex, in vec2 uv, float off, float it) {
    float subpx = 8.0 * it;
    vec4 fullRes = texture(tex, uv + vec2(0, 0)) / max(1.0, subpx + 1.0);

    for (float i = 0.0; i < it; i++) {
        float o = off * i;
        fullRes += texture(tex, uv + vec2(0, o)) / subpx;
        fullRes += texture(tex, uv + vec2(o, o)) / subpx;
        fullRes += texture(tex, uv + vec2(o, 0)) / subpx;
        fullRes += texture(tex, uv + vec2(o, -o)) / subpx;
        fullRes += texture(tex, uv + vec2(0, -o)) / subpx;
        fullRes += texture(tex, uv + vec2(-o, -o)) / subpx;
        fullRes += texture(tex, uv + vec2(-o, 0)) / subpx;
        fullRes += texture(tex, uv + vec2(-o, o)) / subpx;
    }
    return fullRes;
}

vec4 thermalVision(sampler2D tex, vec2 uv, vec2 fragCoord) {
    float resScale = 1440.0 / InSize.y;
    float noisePixels = (pixels < 1.0 ? pixels : pixels * 0.2) * resScale;

    vec4 fullRes = texture(tex, uv);

    float vignette = (VignetteEnabled > 0.5) ?
    pow(1.0 - dot(uv - 0.5, uv - 0.5), 2.2) * 1.2 : 1.0;

    float blurVignette = clamp(1.0 - pow(vignette, 1.4), 0.0, 1.0) + minBlur;
    vignette = pow(vignette, 3.0);

    vec4 fullResBlur = blurTex(tex, uv, blurDistance / blurIterations * blurVignette, blurIterations);
    fullRes = fullResBlur;

    vec2 pixelCoord = floor(fragCoord * pixels) / pixels;
    vec4 pixelRes = textureLod(tex, pixelCoord / InSize, 0.0);

    vec4 fragResult = mix(pixelRes, fullRes, 0.5);

    float mul = 1.2;

    vec3 averageBrightness = vec3(0.0);
    float weightSum = 0.0;

    for (float y = 0.1; y <= 0.9; y += 0.2) {
        for (float x = 0.1; x <= 0.9; x += 0.2) {
            float weight = 1.0 - (distance(vec2(x, y), vec2(0.5, 0.5)) * 1.5);
            weight = max(0.2, weight * weight); // Ensure minimum weight, square for faster falloff

            averageBrightness += textureLod(tex, vec2(x, y), 10.0).rgb * weight;
            weightSum += weight;
        }
    }

    averageBrightness /= weightSum;

    float brightness = dot(averageBrightness.rgb, lum);

    brightness = clamp(brightness, 0.05, 0.95); // Prevent extreme values

    if (uv.y > 0.98 && uv.x > 0.49 && uv.x < 0.51)
    return vec4(brightness, brightness, brightness, 1.0);

    float brightnessFactor = smoothstep(0.0, 0.5, brightness);
    mul = mix(pow(max(0.1, brightness), -1.0), mul, brightnessFactor);
    fragResult *= mul * Brightness;

    float grey = dot(fragResult.rgb, lum);

    grey = pow(grey, 0.7);
    grey = smoothstep(0.1, 0.9, grey);

    float h = grey;

    float pulseFactor = 0.05 * sin(Time * 3.0);

    vec3 color = mix(vec3(0), thermal_cold, smoothstep(0.0, 0.1, h));
    color = mix(color, thermal_cool, smoothstep(0.1, 0.3, h));
    color = mix(color, thermal_medium, smoothstep(0.3, 0.6, h));
    color = mix(color, thermal_warm, smoothstep(0.6, 0.8, h));
    color = mix(color, thermal_hot, smoothstep(0.8, 1.0, h));

    color = color * (1.0 + pulseFactor);

    vec4 center = fragResult;

    vec4 neighbors[4];
    neighbors[0] = texture(tex, uv + vec2(0.01, 0.0));
    neighbors[1] = texture(tex, uv + vec2(-0.01, 0.0));
    neighbors[2] = texture(tex, uv + vec2(0.0, 0.01));
    neighbors[3] = texture(tex, uv + vec2(0.0, -0.01));

    float edgeContrast = 0.0;
    for (int i = 0; i < 4; i++) {
        float neighborGrey = dot(neighbors[i].rgb, lum);
        edgeContrast += abs(grey - neighborGrey);
    }

    if (edgeContrast > 0.15) {
        color = mix(color, vec3(1.0), 0.2);
    }

    float noiseTime = mod(Time * 0.15, 10.0);
    float noise = random(uv / resScale + noiseTime);

    vec2 noiseUv = floor(fragCoord * noisePixels) / InSize / noisePixels;
    float highNoise = pow(random(noiseUv + noiseTime), 1000.0) * 1.0;
    highNoise += highNoise * vignette * 10.0;

    float thermalNoise = noise * 0.3 + highNoise * 1.5;
    return vec4((fragResult.rgb + thermalNoise) * color * vignette, 1.0);
}

void main() {
    fragColor = thermalVision(DiffuseSampler, texCoord, texCoord * InSize);
}