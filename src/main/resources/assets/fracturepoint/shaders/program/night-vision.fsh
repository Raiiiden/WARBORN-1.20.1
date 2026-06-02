#version 150

uniform float NightVisionEnabled;
uniform float VignetteEnabled;
uniform float VignetteRadius;
uniform float Brightness;
uniform float SepiaRatio;
uniform sampler2D DiffuseSampler;
uniform sampler2D NoiseSampler;
uniform float Time;
uniform vec2 InSize;
uniform float NoiseAmplification;
uniform float IntensityAdjust;
uniform float RedValue;
uniform float GreenValue;
uniform float BlueValue;

in vec2 texCoord;
in vec2 oneTexel;
in vec4 outPos;

out vec4 fragColor;

const float SOFTNESS = 0.25;
const float contrast = 0.8;
const vec3 SEPIA = vec3(1.2, 1.0, 0.8);

void main() {
    vec4 texColor = texture(DiffuseSampler, texCoord.xy);

    // Gamma correction to lift shadows
    texColor.rgb = pow(texColor.rgb, vec3(0.5)) * Brightness;

    if (NightVisionEnabled > 0) {
        // Calculate scene brightness
        float sceneBrightness = dot(texColor.rgb, vec3(0.299, 0.587, 0.114));

        // Simulate overexposure in bright areas
        float overexposure = smoothstep(0.6, 0.9, sceneBrightness);
        texColor.rgb = mix(texColor.rgb, vec3(1.0), overexposure * 0.8);

        // More noise in dark areas, less in bright
        float noiseIntensity = pow(1.0 - sceneBrightness, 2.0) * NoiseAmplification;

        vec2 uv;
        uv.x = sin(Time * 50.0) * 0.5;
        uv.y = cos(Time * 73.0) * 0.5;
        vec3 noise = texture(NoiseSampler, texCoord.xy + uv).rgb * noiseIntensity;  // ← Use noiseIntensity here!
        texColor.rgb += noise.rgb * 0.10;  // ← Apply to all RGB channels

        // Add bloom/glow around bright light sources
        if (sceneBrightness > 0.8) {
            texColor.rgb += vec3(0.3) * (sceneBrightness - 0.8) * 5.0;
        }
    }

    if (VignetteEnabled > 0) {
        float dist = distance(texCoord.xy, vec2(0.5, 0.5));
        float vignette = smoothstep(VignetteRadius, VignetteRadius - SOFTNESS, dist);
        texColor.rgb *= vignette;
        texColor.a = 1.0;
    }

    if (NightVisionEnabled > 0) {
        vec2 center = vec2(0.5, 0.5);
        vec2 scaledCoord = (texCoord.xy - center) * vec2(InSize.x / InSize.y, 1.0);
        float dist = length(scaledCoord);

        if (dist <= 2.0) {
            const vec3 lumvec = vec3(0.30, 0.59, 0.11);
            float intensity = dot(lumvec, texColor.rgb);
            intensity = clamp(contrast * (intensity - 0.5) + 0.5, 0.0, 1.0);
            float color = clamp(intensity / 0.59, 0.0, 1.0) * IntensityAdjust;
            vec4 visionColor = vec4(RedValue * color, GreenValue * color, BlueValue * color, 1.0);
            float gray = dot(texColor.rgb, vec3(0.299, 0.587, 0.114));
            vec4 grayColor = vec4(gray, gray, gray, 1.0);
            texColor = grayColor * visionColor;
        }
    }

    if (SepiaRatio > 0) {
        float gray = dot(texColor.rgb, vec3(0.299, 0.587, 0.114));
        vec4 sepiaColor = vec4(vec3(gray) * SEPIA, 1.0);
        texColor = mix(texColor, sepiaColor, SepiaRatio);
    }

    fragColor = vec4(texColor.rgb, 1.0);
}