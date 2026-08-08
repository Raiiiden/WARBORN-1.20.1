#version 150
// Digital (white phosphor) night vision.
//
// The previous version stacked a 3%-of-screen blur, 10x10 pixel blocking and three
// unclamped noise terms on top of a halved luminance vector, which left the image a
// dark sparkling mush. This one models the device instead - fixed sensor grid, auto
// gain, tube response, phosphor tint - and keeps every effect bounded.

in vec2 texCoord;
in vec2 oneTexel;

out vec4 fragColor;

uniform sampler2D DiffuseSampler;
uniform sampler2D NoiseSampler;
// 1x1 target written by the scene-brightness pass; b holds the average this shader meters against.
uniform sampler2D BrightnessSampler;
uniform float Time;
uniform vec2 InSize;

uniform float VignetteEnabled;       // 0.0 = off, 1.0 = on
uniform float VignetteRadius;        // Default: 0.65
uniform float Brightness;            // Overall brightness multiplier
uniform float NoiseAmplification;    // Strength of noise effect

const vec3 LUM = vec3(0.2126, 0.7152, 0.0722);

// slightly cool white, the way a real white phosphor tube reads
const vec3 PHOSPHOR = vec3(0.87, 0.94, 1.0);

// the sensor is a fixed grid, so the look doesn't change with window size
const float SENSOR_LINES = 540.0;

// How far the tube is allowed to amplify a dark scene, and what average it aims for.
//
// These used to be 20x onto a target of 0.30, which blew the image out: 0.30 is the mean
// the gain drives the scene to *before* the tube curve, and the curve then lifted it again
// to around 0.46. A moonlit surface sitting at 0.10 luma came out at 0.97 - effectively
// white - so night looked like an overexposed day. The target is now set so the finished
// image averages roughly a quarter brightness, which leaves headroom for torches and fire
// to actually read as bright against it.
const float MIN_GAIN = 1.0;
const float MAX_GAIN = 14.0;
const float TARGET_LUMA = 0.22;
// Only a divide-by-zero guard now; MAX_GAIN is what actually limits a pitch black scene.
const float DARKEST_METERED = 0.004;

// a real tube never reads pure black
const float BLACK_LEVEL = 0.03;

float hash(vec2 p) {
    return fract(sin(dot(p, vec2(12.9898, 78.233))) * 43758.5453123);
}

// Coarse average of the frame, used to drive the gain. The 5x5 grid this used to walk per pixel is now
// metered once per frame by the scene-brightness pass, which leaves a single fetch of a 1x1 texture.
// The channel is gamma encoded on the way in - see scene-brightness.fsh for why - so undo that here.
float sceneLuma() {
    float encoded = texture(BrightnessSampler, vec2(0.5, 0.5)).b;
    return encoded * encoded * encoded * encoded;
}

void main() {
    vec2 uv = texCoord;
    float aspect = InSize.x / max(InSize.y, 1.0);

    vec2 sensor = vec2(floor(SENSOR_LINES * aspect), SENSOR_LINES);
    vec2 sensorUv = (floor(uv * sensor) + 0.5) / sensor;
    vec2 texel = 1.0 / sensor;

    // one sensor pixel plus its four neighbours: enough softness to read as a
    // sensor rather than a raw framebuffer, nowhere near enough to blur the scene
    vec3 c = texture(DiffuseSampler, sensorUv).rgb * 0.5;
    c += texture(DiffuseSampler, sensorUv + vec2(texel.x, 0.0)).rgb * 0.125;
    c += texture(DiffuseSampler, sensorUv + vec2(-texel.x, 0.0)).rgb * 0.125;
    c += texture(DiffuseSampler, sensorUv + vec2(0.0, texel.y)).rgb * 0.125;
    c += texture(DiffuseSampler, sensorUv + vec2(0.0, -texel.y)).rgb * 0.125;

    float luma = dot(c, LUM);

    // auto gain, clamped both ways so a dark room doesn't blow out and a lit one
    // doesn't wash flat
    float gain = clamp(TARGET_LUMA / max(sceneLuma(), DARKEST_METERED), MIN_GAIN, MAX_GAIN);
    luma *= gain * max(Brightness, 0.05);

    // Tube response - soft shoulder, so highlights roll off instead of clipping. The
    // shoulder is gentler than it was (1.35 rather than 1.7) so mid tones climb toward
    // white more slowly, and the gamma now sits above 1.0 instead of below it. At 0.85 it
    // was pulling mid tones *up*, compounding the auto gain rather than shaping it.
    float signal = 1.0 - exp(-luma * 1.35);
    signal = pow(signal, 1.05);

    // bloom only on genuinely bright sources (torches, fire, the sky)
    float bloom = smoothstep(0.7, 1.0, signal) * 0.3;

    float vignette = 1.0;
    if (VignetteEnabled > 0.5) {
        float r = length(uv - vec2(0.5, 0.5)) * 1.4;
        vignette = mix(0.5, 1.0, 1.0 - smoothstep(VignetteRadius - 0.3, VignetteRadius + 0.3, r));
    }

    // sensor grain: strongest in the shadows, gone in the highlights, and scaled by
    // NoiseAmplification so it can be dialled out entirely
    float frame = floor(Time * 20.0);
    float grain = hash(floor(uv * sensor) + frame) - 0.5;
    float grainAmount = mix(0.09, 0.015, smoothstep(0.0, 0.55, signal)) * NoiseAmplification;

    float value = (signal + bloom) * vignette + grain * grainAmount;

    // faint readout lines, locked to the sensor grid so they never alias into moire
    float scan = 1.0 - 0.05 * (0.5 + 0.5 * sin((uv.y * sensor.y * 0.5 - Time * 12.0) * 3.14159265));
    value *= scan;

    value = max(value, BLACK_LEVEL * vignette);

    fragColor = vec4(PHOSPHOR * clamp(value, 0.0, 1.15), 1.0);
}
