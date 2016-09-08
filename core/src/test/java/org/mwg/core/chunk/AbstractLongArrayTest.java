package org.mwg.core.chunk;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.Type;
import org.mwg.chunk.ChunkSpace;
import org.mwg.chunk.ChunkType;
import org.mwg.chunk.StateChunk;
import org.mwg.plugin.MemoryFactory;
import org.mwg.struct.Relationship;

public abstract class AbstractLongArrayTest {

    private MemoryFactory factory;

    public AbstractLongArrayTest(MemoryFactory factory) {
        this.factory = factory;
    }

    @Test
    public void genericTest() {

        ChunkSpace space = factory.newSpace(100, null);
        StateChunk chunk = (StateChunk) space.createAndMark(ChunkType.STATE_CHUNK, 0, 0, 0);
        Relationship relationship = (Relationship) chunk.getOrCreate(0, Type.RELATION);

        Assert.assertEquals(relationship.size(), 0);
        relationship.add(-1);
        relationship.add(10);
        relationship.add(100);
        Assert.assertEquals(relationship.size(), 3);

        Assert.assertEquals(relationship.get(0), -1);
        Assert.assertEquals(relationship.get(1), 10);
        Assert.assertEquals(relationship.get(2), 100);

        for (int i = 200; i < 1000; i++) {
            relationship.add(i);
        }

        Assert.assertEquals(relationship.size(), 803);
        Assert.assertEquals(relationship.get(802), 999);

        relationship.remove(3000);
        Assert.assertEquals(relationship.size(), 803);

        relationship.remove(950);
        Assert.assertEquals(relationship.size(), 802);

        space.free(chunk);
        space.freeAll();

    }

}
