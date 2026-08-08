#version 150
// Frame-average scene brightness, reduced to a single pixel.
//
// The vision shaders used to run this grid sample inside their own fullscreen pass, so the same ~50 fixed
// screen positions were fetched again for every pixel on screen - about 100 million redundant texture reads
// per frame at 1080p. The value is a per-frame constant, so it belongs in a pass that renders one fragment.
//
// Output channels, so one pass can feed every consumer:
//   r - weighted average luma over a 7x7 grid, thermal weighting
//   g - brightest single sample of that grid
//   b - weighted average luma over a 5x5 grid, digital weighting, gamma encoded
//
// This target is RGBA8, so every channel is quantised to 1/255. That is fine for the thermal channels,
// whose consumer runs them through tanh, but digital divides by its value to get an auto-gain - and in a
// dark scene the metered luma sits around 4-6/255, where one code step swings the gain by 20%. The digital
// channel is therefore stored as luma^(1/4), which puts roughly six times more resolution down at the dark
// end and holds the gain step under 5%.

in vec2 texCoord;

out vec4 fragColor;

uniform sampler2D DiffuseSampler;

const vec3 LUM_THERMAL = vec3(0.2125, 0.4154, 0.0721);
const vec3 LUM_DIGITAL = vec3(0.2126, 0.7152, 0.0722);

void main() {
    // The samples used to be taken with textureLod(.., 4.0). Post-chain targets are created without
    // mipmaps, so that always clamped to level 0 - this is the same fetch, just stated honestly.
    float thermalTotal = 0.0;
    float thermalWeight = 0.0;
    float maxSample = 0.0;

    for (int y = 0; y < 7; y++) {
        float yPos = 0.1 + 0.8 * (float(y) / 6.0);
        for (int x = 0; x < 7; x++) {
            float xPos = 0.1 + 0.8 * (float(x) / 6.0);

            float w = 1.0 - (distance(vec2(xPos, yPos), vec2(0.5, 0.5)) * 1.2);
            w = max(0.2, w * w);

            float luma = dot(texture(DiffuseSampler, vec2(xPos, yPos)).rgb, LUM_THERMAL);
            maxSample = max(maxSample, luma);
            thermalTotal += luma * w;
            thermalWeight += w;
        }
    }

    float digitalTotal = 0.0;
    float digitalWeight = 0.0;

    for (int y = 0; y < 5; y++) {
        for (int x = 0; x < 5; x++) {
            vec2 p = vec2(0.1 + 0.2 * float(x), 0.1 + 0.2 * float(y));
            float w = max(0.15, 1.0 - distance(p, vec2(0.5, 0.5)));

            digitalTotal += dot(texture(DiffuseSampler, p).rgb, LUM_DIGITAL) * w;
            digitalWeight += w;
        }
    }

    fragColor = vec4(
        thermalTotal / max(thermalWeight, 0.001),
        maxSample,
        pow(digitalTotal / max(digitalWeight, 0.0001), 0.25),
        1.0);
}
