#version 150
// Simple (analogue tube) night vision.
//
// Night vision from 4rknova, adjusted for my purposes.
//
// Was a single circle pushed to the right of the screen with the raw, un-intensified
// scene still visible around it. Now the intensified image covers the whole frame and
// the mask is four overlapping tubes in a row, centred - a GPNVG-18 style quad array.
//
// Also moved from #version 120 to 150: the old file declared `varying`/gl_FragColor
// against vanilla's #version 150 blit.vsh, which only links on lenient drivers.

in vec2 texCoord;
in vec2 oneTexel;

out vec4 fragColor;

uniform sampler2D DiffuseSampler;
uniform sampler2D NoiseSampler;
uniform float Time;
uniform vec2 InSize;
uniform float CenterOffsetX;   // nudges the whole tube cluster sideways; 0 = centred

const vec3 LUM = vec3(0.2126, 0.7152, 0.0722);
const vec3 TUBE_GREEN = vec3(0.2, 1.0, 0.2);

// Spacing is well under 2*radius, so neighbouring tubes overlap and the union reads
// as one wide panoramic field with the scalloped edge the real optic has.
const float TUBE_RADIUS  = 0.82;
const float TUBE_SPACING = 0.70;
const float TUBE_FEATHER = 0.08;

float hash(in float n) { return fract(sin(n) * 43758.5453123); }

void main() {
    vec2 p = texCoord;

    vec3 scene = texture(DiffuseSampler, p).rgb;

    vec3 nv = scene;
    nv += sin(hash(Time)) * 0.01;
    nv += hash((hash(p.x) + p.y) * Time) * 0.1;

    float gray = dot(nv, LUM);
    vec3 nightVision = TUBE_GREEN * gray * 2.5;

    // aspect-corrected screen space: y in [-1,1], x scaled by the aspect ratio
    vec2 n = (p * 2.0 - 1.0) * vec2(InSize.x / max(InSize.y, 1.0), 1.0);

    // one pass over the four tubes, collecting both the union mask and the distance
    // to the nearest tube centre (used for the per-tube falloff)
    float mask = 0.0;
    float nearest = 1e9;
    for (int i = 0; i < 4; i++) {
        float offset = (float(i) - 1.5) * TUBE_SPACING + CenterOffsetX;
        float d = length(n - vec2(offset, 0.0));
        nearest = min(nearest, d);
        mask = max(mask, 1.0 - smoothstep(TUBE_RADIUS - TUBE_FEATHER, TUBE_RADIUS, d));
    }

    // real tubes dim toward their own rim rather than staying flat to the edge
    float falloff = 1.0 - 0.3 * smoothstep(0.25, TUBE_RADIUS, nearest);

    fragColor = vec4(nightVision * mask * falloff, 1.0);
}
