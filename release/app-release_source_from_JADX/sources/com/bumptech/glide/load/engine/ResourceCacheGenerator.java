package com.bumptech.glide.load.engine;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.data.DataFetcher.DataCallback;
import com.bumptech.glide.load.engine.DataFetcherGenerator.FetcherReadyCallback;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoader.LoadData;
import java.io.File;
import java.util.List;

class ResourceCacheGenerator implements DataFetcherGenerator, DataCallback<Object> {
    private File cacheFile;

    /* renamed from: cb */
    private final FetcherReadyCallback f55cb;
    private ResourceCacheKey currentKey;
    private final DecodeHelper<?> helper;
    private volatile LoadData<?> loadData;
    private int modelLoaderIndex;
    private List<ModelLoader<File, ?>> modelLoaders;
    private int resourceClassIndex = -1;
    private int sourceIdIndex;
    private Key sourceKey;

    ResourceCacheGenerator(DecodeHelper<?> decodeHelper, FetcherReadyCallback fetcherReadyCallback) {
        this.helper = decodeHelper;
        this.f55cb = fetcherReadyCallback;
    }

    public boolean startNext() {
        List cacheKeys = this.helper.getCacheKeys();
        boolean z = false;
        if (cacheKeys.isEmpty()) {
            return false;
        }
        List registeredResourceClasses = this.helper.getRegisteredResourceClasses();
        if (registeredResourceClasses.isEmpty() && File.class.equals(this.helper.getTranscodeClass())) {
            return false;
        }
        while (true) {
            if (this.modelLoaders == null || !hasNextModelLoader()) {
                int i = this.resourceClassIndex + 1;
                this.resourceClassIndex = i;
                if (i >= registeredResourceClasses.size()) {
                    int i2 = this.sourceIdIndex + 1;
                    this.sourceIdIndex = i2;
                    if (i2 >= cacheKeys.size()) {
                        return false;
                    }
                    this.resourceClassIndex = 0;
                }
                Key key = (Key) cacheKeys.get(this.sourceIdIndex);
                Class cls = (Class) registeredResourceClasses.get(this.resourceClassIndex);
                Key key2 = key;
                ResourceCacheKey resourceCacheKey = new ResourceCacheKey(this.helper.getArrayPool(), key2, this.helper.getSignature(), this.helper.getWidth(), this.helper.getHeight(), this.helper.getTransformation(cls), cls, this.helper.getOptions());
                this.currentKey = resourceCacheKey;
                File file = this.helper.getDiskCache().get(this.currentKey);
                this.cacheFile = file;
                if (file != null) {
                    this.sourceKey = key;
                    this.modelLoaders = this.helper.getModelLoaders(file);
                    this.modelLoaderIndex = 0;
                }
            } else {
                this.loadData = null;
                while (!z && hasNextModelLoader()) {
                    List<ModelLoader<File, ?>> list = this.modelLoaders;
                    int i3 = this.modelLoaderIndex;
                    this.modelLoaderIndex = i3 + 1;
                    this.loadData = ((ModelLoader) list.get(i3)).buildLoadData(this.cacheFile, this.helper.getWidth(), this.helper.getHeight(), this.helper.getOptions());
                    if (this.loadData != null && this.helper.hasLoadPath(this.loadData.fetcher.getDataClass())) {
                        this.loadData.fetcher.loadData(this.helper.getPriority(), this);
                        z = true;
                    }
                }
                return z;
            }
        }
    }

    private boolean hasNextModelLoader() {
        return this.modelLoaderIndex < this.modelLoaders.size();
    }

    public void cancel() {
        LoadData<?> loadData2 = this.loadData;
        if (loadData2 != null) {
            loadData2.fetcher.cancel();
        }
    }

    public void onDataReady(Object obj) {
        this.f55cb.onDataFetcherReady(this.sourceKey, obj, this.loadData.fetcher, DataSource.RESOURCE_DISK_CACHE, this.currentKey);
    }

    public void onLoadFailed(Exception exc) {
        this.f55cb.onDataFetcherFailed(this.currentKey, exc, this.loadData.fetcher, DataSource.RESOURCE_DISK_CACHE);
    }
}
