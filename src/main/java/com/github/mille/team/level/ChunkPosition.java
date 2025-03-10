package com.github.mille.team.level;

import com.github.mille.team.math.MathHelper;
import com.github.mille.team.math.Vector3;

/**
 * Author: Adam Matthew
 * <p>
 * Nukkit Project
 */
public class ChunkPosition {

    public final int x;

    public final int y;

    public final int z;

    public ChunkPosition(
        int i,
        int j,
        int k
    ) {
        this.x = i;
        this.y = j;
        this.z = k;
    }

    public ChunkPosition(Vector3 vec3d) {
        this(MathHelper.floor(vec3d.x), MathHelper.floor(vec3d.y), MathHelper.floor(vec3d.z));
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ChunkPosition chunkposition)) {
            return false;
        } else {

            return chunkposition.x == this.x && chunkposition.y == this.y && chunkposition.z == this.z;
        }
    }

    @Override
    public int hashCode() {
        return this.x * 8976890 + this.y * 981131 + this.z;
    }

}
