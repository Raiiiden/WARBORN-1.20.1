#version 150
// White Phosphor Vision Shader

// All most all of it is from https://www.shadertoy.com/view/4XtSR4
// Created by curiouspers in 2024-06-18
// Thanks so much for this amazing shader.

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

const vec3 lum = vec3(0.2125, 0.4154, 0.0721) * 0.5;

const vec3 phosphor_color = vec3(0.95, 1.0, 0.98);

const float minBlur = 0.0;
const float blurIterations = 4.0;
const float blurDistance = 0.03;
const float pixels = 0.1;

const float MAX_BRIGHTNESS_THRESHOLD = 10.0;
const float MIN_BRIGHTNESS_THRESHOLD = 0.05;

//TODO actually make this good still wtf its ugly as shit
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

float getAverageSceneBrightness(sampler2D tex) {
    const int SAMPLE_COUNT_X = 7;
    const int SAMPLE_COUNT_Y = 7;

    vec3 totalBrightness = vec3(0.0);
    float weightSum = 0.0;
    float maxBrightnessSample = 0.0;

    for (int y = 0; y < SAMPLE_COUNT_Y; y++) {
        float yPos = 0.1 + 0.8 * (float(y) / float(SAMPLE_COUNT_Y - 1));

        for (int x = 0; x < SAMPLE_COUNT_X; x++) {
            float xPos = 0.1 + 0.8 * (float(x) / float(SAMPLE_COUNT_X - 1));

            float weight = 1.0 - (distance(vec2(xPos, yPos), vec2(0.5, 0.5)) * 1.2);
            weight = max(0.2, weight * weight);

            vec3 sampleColor = textureLod(tex, vec2(xPos, yPos), 4.0).rgb;

            float sampleBrightness = dot(sampleColor, lum);

            maxBrightnessSample = max(maxBrightnessSample, sampleBrightness);

            if (sampleBrightness > 1.0) {
                sampleBrightness = 1.0 + log(sampleBrightness);
            }

            totalBrightness += sampleColor * weight;
            weightSum += weight;
        }
    }

    vec3 averageBrightness = totalBrightness / max(0.001, weightSum);
    float brightness = dot(averageBrightness, lum);

    if (maxBrightnessSample > MAX_BRIGHTNESS_THRESHOLD) {
        float dampFactor = 1.0 / (1.0 + log(maxBrightnessSample / MAX_BRIGHTNESS_THRESHOLD));
        brightness *= dampFactor;
    }

    float remappedBrightness = 0.5 + 0.45 * tanh((brightness - 0.5) * 2.0);

    remappedBrightness = max(MIN_BRIGHTNESS_THRESHOLD, remappedBrightness);

    return remappedBrightness;
}

vec4 whitePhosphorVision(sampler2D tex, vec2 uv, vec2 fragCoord) {
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

    float sceneBrightness = getAverageSceneBrightness(tex);

    if (uv.y > 0.98 && uv.x > 0.49 && uv.x < 0.51)
    return vec4(sceneBrightness, sceneBrightness, sceneBrightness, 1.0);

    float mul = 1.2;
    float brightnessFactor = smoothstep(0.0, 0.5, sceneBrightness);

    float adjustedSceneBrightness = max(0.2, sceneBrightness);
    mul = mix(pow(adjustedSceneBrightness, -0.8), mul, brightnessFactor);

    mul = max(0.5, mul);

    fragResult *= mul * Brightness;

    float grey = dot(fragResult.rgb, lum);

    grey = pow(grey, 1.2);
    grey = smoothstep(0.12, 0.8, grey);
    grey = max(MIN_BRIGHTNESS_THRESHOLD, grey);

    float bloom = pow(grey, 3.0) * 0.8;

    bloom = max(0.02, bloom);

    float pulseFactor = 0.03 * sin(Time * 2.5);

    vec3 color = phosphor_color * (grey + bloom + pulseFactor);

    float scanLine = sin(fragCoord.y * 2.0 + Time * 10.0) * 0.02 + 0.98;
    color *= scanLine;

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

    if (edgeContrast > 0.12) {
        color = mix(color, vec3(1.0), 0.35 * grey * grey);
    }

    float noiseTime = mod(Time * 0.1, 10.0);
    float noise = random(uv / resScale + noiseTime);

    vec2 noiseUv = floor(fragCoord * noisePixels) / InSize / noisePixels;
    float grainNoise = random(noiseUv + noiseTime) * 0.1;

    float brightnessDampener = 1.0 - min(1.0, pow(sceneBrightness, 2.0) * 2.0);

    float highNoise = pow(random(noiseUv + noiseTime * 0.5), 30.0) * 0.5;
    float staticNoise = (noise * 0.07 + grainNoise) * (1.0 - grey * 0.7) * brightnessDampener + highNoise * vignette;

    float hotspotIntensity = pow(random(uv + Time * 0.05), 40.0) * grey * 0.7 * brightnessDampener;

    vec3 finalColor = color * vignette + staticNoise + hotspotIntensity;
    float finalBrightness = dot(finalColor, lum);

    if (finalBrightness < MIN_BRIGHTNESS_THRESHOLD) {
        finalColor = mix(vec3(MIN_BRIGHTNESS_THRESHOLD), finalColor, finalBrightness / MIN_BRIGHTNESS_THRESHOLD);
    }

    return vec4(finalColor, 1.0);
}

void main() {
    fragColor = whitePhosphorVision(DiffuseSampler, texCoord, texCoord * InSize);
}