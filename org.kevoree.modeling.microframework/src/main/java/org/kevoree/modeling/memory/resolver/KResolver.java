package org.kevoree.modeling.memory.resolver;

import org.kevoree.modeling.KCallback;
import org.kevoree.modeling.KObject;
import org.kevoree.modeling.memory.chunk.KMemoryChunk;
import org.kevoree.modeling.meta.KMetaClass;

public interface KResolver {

    Runnable lookup(long universe, long time, long uuid, KCallback<KObject> callback);

    Runnable lookupAllObjects(long universe, long time, long[] uuids, KCallback<KObject[]> callback);

    Runnable lookupAllTimes(long universe, long[] times, long uuid, KCallback<KObject[]> callback);

    Runnable lookupAllObjectsTimes(long universe, long[] times, long[] uuids, KCallback<KObject[]> callback);

    KMemoryChunk preciseChunk(long universe, long time, long uuid, KMetaClass metaClass, long[] previousResolution);

    KMemoryChunk closestChunk(long universe, long time, long uuid, KMetaClass metaClass, long[] previousResolution);

    void indexObject(KObject obj);


}
