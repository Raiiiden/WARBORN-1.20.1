#version 150
// White-Hot Thermal Vision Shader
// Monochrome sibling of thermal-vision.fsh - identical gain/blur/noise pipeline so
// both palettes behave like the same device, only the output mapping differs:
// cold = black, hot = white.

in vec2 texCoord;
in vec2 oneTexel;

out vec4 fragColor;

uniform sampler2D DiffuseSampler;
uniform sampler2D NoiseSampler;
// 1x1 target written by the scene-brightness pass: r = weighted frame average, g = brightest sample.
uniform sampler2D BrightnessSampler;
uniform float Time;
uniform vec2 InSize;

uniform float VignetteEnabled;       // 0.0 = off, 1.0 = on
uniform float VignetteRadius;        // Default: 0.65
uniform float Brightness;            // Overall brightness multiplier
uniform float NoiseAmplification;    // Strength of noise effect

const vec3 lum = vec3(0.2125, 0.4154, 0.0721);

// white-hot ramp: the coldest thing on screen never goes fully black, so the
// scene still reads as an image rather than a void. Kept low - a high floor is
// what washes the contrast out and makes hot targets stop standing apart.
const float COLD_FLOOR = 0.03;

const float minBlur = 0.0;
const float blurIterations = 4.0;
const float blurDistance = 0.03;
const float pixels = 0.2;

const float MAX_BRIGHTNESS_THRESHOLD = 10.0;
const float MIN_BRIGHTNESS_THRESHOLD = 0.25;
const float BASE_BRIGHTNESS = 0.15;

float random(in vec2 st) {
    return fract(sin(dot(st.xy, vec2(12.9898, 78.233))) * 43758.5453123);
}

vec4 blurTex(in sampler2D tex, in vec2 uv, float off, float it) {
    float subpx = 8.0 * it;

    // The old loop started at i = 0, where the offset is zero - so eight of its taps re-sampled the exact
    // centre that was already fetched. Folding that weight into the centre fetch gives the same result for
    // eight fewer lookups per pixel.
    vec4 fullRes = texture(tex, uv) * (1.0 / max(1.0, subpx + 1.0) + 8.0 / subpx);

    for (float i = 1.0; i < it; i++) {
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

// The 7x7 grid sample this used to do per pixel now happens once per frame in the scene-brightness pass;
// only the remap is left, which is pure arithmetic on two numbers.
float getAverageSceneBrightness() {
    vec2 metered = texture(BrightnessSampler, vec2(0.5, 0.5)).rg;
    float brightness = metered.r;
    float maxBrightnessSample = metered.g;

    if (maxBrightnessSample > MAX_BRIGHTNESS_THRESHOLD) {
        float dampFactor = 1.0 / (1.0 + log(maxBrightnessSample / MAX_BRIGHTNESS_THRESHOLD));
        brightness *= dampFactor;
    }

    if (maxBrightnessSample < 0.1) {
        brightness = mix(brightness + BASE_BRIGHTNESS, brightness, maxBrightnessSample * 10.0);
    }

    float remappedBrightness = 0.5 + 0.45 * tanh((brightness - 0.5) * 2.0);
    return max(MIN_BRIGHTNESS_THRESHOLD, remappedBrightness);
}

vec4 whiteHotVision(sampler2D tex, vec2 uv, vec2 fragCoord) {
    float resScale = 1440.0 / InSize.y;
    float noisePixels = (pixels < 1.0 ? pixels : pixels * 0.2) * resScale;

    float vignette = (VignetteEnabled > 0.5) ?
    pow(1.0 - dot(uv - 0.5, uv - 0.5), 2.2) * 1.2 : 1.0;

    float blurVignette = clamp(1.0 - pow(vignette, 1.4), 0.0, 1.0) + minBlur;
    vignette = pow(vignette, 3.0);

    vec4 fullRes = blurTex(tex, uv, blurDistance / blurIterations * blurVignette, blurIterations);

    vec2 pixelCoord = floor(fragCoord * pixels) / pixels;
    vec4 pixelRes = textureLod(tex, pixelCoord / InSize, 0.0);

    float pixelBrightness = dot(pixelRes.rgb, lum);
    if (pixelBrightness < 0.1) {
        pixelRes.rgb += vec3(BASE_BRIGHTNESS * (1.0 - pixelBrightness * 5.0));
    }

    vec4 fragResult = mix(pixelRes, fullRes, 0.5);

    float sceneBrightness = getAverageSceneBrightness();

    float mul = 1.2;
    float brightnessFactor = smoothstep(0.0, 0.5, sceneBrightness);
    float adjustedSceneBrightness = max(0.2, sceneBrightness);
    mul = mix(pow(adjustedSceneBrightness, -0.8), mul, brightnessFactor);
    mul = max(0.6, mul);

    fragResult *= mul * Brightness * 1.2;

    // Heat channel. The colour version can lean on hue to separate a target from its
    // background; white-hot has only brightness, so it needs a much harder curve than
    // the 0.65 gamma lift + wide smoothstep the palette version uses - that left
    // everything sitting in a flat mid grey where nothing read as "glowing".
    float grey = dot(fragResult.rgb, lum);
    grey = pow(grey, 0.70);

    // Low edge near zero so dark parts of the scene still carry signal - pulling it
    // up crushed everything cold to black. Top edge wide enough that nothing clips
    // until the input is well past halfway.
    grey = smoothstep(0.12, 0.85, grey);

    // edge highlight: hot/cold boundaries get a thin bright rim, which is what
    // keeps a monochrome image readable without a colour ramp to lean on
    vec4 neighbors[4];
    neighbors[0] = texture(tex, uv + vec2(0.01, 0.0));
    neighbors[1] = texture(tex, uv + vec2(-0.01, 0.0));
    neighbors[2] = texture(tex, uv + vec2(0.0, 0.01));
    neighbors[3] = texture(tex, uv + vec2(0.0, -0.01));

    float edgeContrast = 0.0;
    for (int i = 0; i < 4; i++) {
        edgeContrast += abs(grey - dot(neighbors[i].rgb, lum));
    }

    float heat = mix(COLD_FLOOR, 1.0, grey);

    // rim scaled by heat, so edges on warm targets get outlined but the cold
    // background doesn't get lifted out of black by its own edge detail
    if (edgeContrast > 0.12) {
        heat = mix(heat, 1.0, 0.35 * grey);
    }

    // bloom on the hottest bodies so they read as sources rather than flat white
    heat += smoothstep(0.80, 1.0, grey) * 0.25;

    float noiseTime = mod(Time * 0.15, 10.0);

    // same restrained grain/sparkle pair as the colour version, so the two palettes
    // keep matching - centred grain, and a hot pixel that is genuinely rare
    float grain = (random(uv / resScale + noiseTime) - 0.5) * 0.05;

    vec2 noiseUv = floor(fragCoord * noisePixels) / InSize / noisePixels;
    float sparkle = pow(random(noiseUv + noiseTime), 3000.0) * 0.25;

    // sensor noise sits in the cold end of the image, same as the colour version
    float noiseDarknessFactor = 1.0 - smoothstep(0.0, 0.6, grey);
    float thermalNoise = (grain + sparkle) * noiseDarknessFactor * NoiseAmplification;

    float value = clamp((heat + thermalNoise) * vignette, 0.0, 1.0);
    value = max(value, COLD_FLOOR * vignette);

    return vec4(vec3(value), 1.0);
}

void main() {
    fragColor = whiteHotVision(DiffuseSampler, texCoord, texCoord * InSize);
}
