package com.bumptech.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.manager.ConnectivityMonitor;
import com.bumptech.glide.manager.ConnectivityMonitor.ConnectivityListener;
import com.bumptech.glide.manager.ConnectivityMonitorFactory;
import com.bumptech.glide.manager.Lifecycle;
import com.bumptech.glide.manager.LifecycleListener;
import com.bumptech.glide.manager.RequestManagerTreeNode;
import com.bumptech.glide.manager.RequestTracker;
import com.bumptech.glide.manager.TargetTracker;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.target.ViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.util.Util;
import java.io.File;
import java.net.URL;

public class RequestManager implements LifecycleListener, ModelTypes<RequestBuilder<Drawable>> {
    private static final RequestOptions DECODE_TYPE_BITMAP = RequestOptions.decodeTypeOf(Bitmap.class).lock();
    private static final RequestOptions DECODE_TYPE_GIF = RequestOptions.decodeTypeOf(GifDrawable.class).lock();
    private static final RequestOptions DOWNLOAD_ONLY_OPTIONS = RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.DATA).priority(Priority.LOW).skipMemoryCache(true);
    private final Runnable addSelfToLifecycle;
    private final ConnectivityMonitor connectivityMonitor;
    protected final Context context;
    protected final Glide glide;
    final Lifecycle lifecycle;
    private final Handler mainHandler;
    private RequestOptions requestOptions;
    private final RequestTracker requestTracker;
    private final TargetTracker targetTracker;
    private final RequestManagerTreeNode treeNode;

    private static class ClearTarget extends ViewTarget<View, Object> {
        public void onResourceReady(Object obj, Transition<? super Object> transition) {
        }

        ClearTarget(View view) {
            super(view);
        }
    }

    private static class RequestManagerConnectivityListener implements ConnectivityListener {
        private final RequestTracker requestTracker;

        RequestManagerConnectivityListener(RequestTracker requestTracker2) {
            this.requestTracker = requestTracker2;
        }

        public void onConnectivityChanged(boolean z) {
            if (z) {
                this.requestTracker.restartRequests();
            }
        }
    }

    public RequestManager(Glide glide2, Lifecycle lifecycle2, RequestManagerTreeNode requestManagerTreeNode, Context context2) {
        this(glide2, lifecycle2, requestManagerTreeNode, new RequestTracker(), glide2.getConnectivityMonitorFactory(), context2);
    }

    RequestManager(Glide glide2, Lifecycle lifecycle2, RequestManagerTreeNode requestManagerTreeNode, RequestTracker requestTracker2, ConnectivityMonitorFactory connectivityMonitorFactory, Context context2) {
        this.targetTracker = new TargetTracker();
        this.addSelfToLifecycle = new Runnable() {
            public void run() {
                RequestManager.this.lifecycle.addListener(RequestManager.this);
            }
        };
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.glide = glide2;
        this.lifecycle = lifecycle2;
        this.treeNode = requestManagerTreeNode;
        this.requestTracker = requestTracker2;
        this.context = context2;
        this.connectivityMonitor = connectivityMonitorFactory.build(context2.getApplicationContext(), new RequestManagerConnectivityListener(requestTracker2));
        if (Util.isOnBackgroundThread()) {
            this.mainHandler.post(this.addSelfToLifecycle);
        } else {
            lifecycle2.addListener(this);
        }
        lifecycle2.addListener(this.connectivityMonitor);
        setRequestOptions(glide2.getGlideContext().getDefaultRequestOptions());
        glide2.registerRequestManager(this);
    }

    /* access modifiers changed from: protected */
    public void setRequestOptions(RequestOptions requestOptions2) {
        this.requestOptions = requestOptions2.clone().autoClone();
    }

    private void updateRequestOptions(RequestOptions requestOptions2) {
        this.requestOptions = this.requestOptions.apply(requestOptions2);
    }

    public RequestManager applyDefaultRequestOptions(RequestOptions requestOptions2) {
        updateRequestOptions(requestOptions2);
        return this;
    }

    public RequestManager setDefaultRequestOptions(RequestOptions requestOptions2) {
        setRequestOptions(requestOptions2);
        return this;
    }

    public boolean isPaused() {
        Util.assertMainThread();
        return this.requestTracker.isPaused();
    }

    public void pauseRequests() {
        Util.assertMainThread();
        this.requestTracker.pauseRequests();
    }

    public void pauseAllRequests() {
        Util.assertMainThread();
        this.requestTracker.pauseAllRequests();
    }

    public void pauseRequestsRecursive() {
        Util.assertMainThread();
        pauseRequests();
        for (RequestManager pauseRequests : this.treeNode.getDescendants()) {
            pauseRequests.pauseRequests();
        }
    }

    public void resumeRequests() {
        Util.assertMainThread();
        this.requestTracker.resumeRequests();
    }

    public void resumeRequestsRecursive() {
        Util.assertMainThread();
        resumeRequests();
        for (RequestManager resumeRequests : this.treeNode.getDescendants()) {
            resumeRequests.resumeRequests();
        }
    }

    public void onStart() {
        resumeRequests();
        this.targetTracker.onStart();
    }

    public void onStop() {
        pauseRequests();
        this.targetTracker.onStop();
    }

    public void onDestroy() {
        this.targetTracker.onDestroy();
        for (Target clear : this.targetTracker.getAll()) {
            clear(clear);
        }
        this.targetTracker.clear();
        this.requestTracker.clearRequests();
        this.lifecycle.removeListener(this);
        this.lifecycle.removeListener(this.connectivityMonitor);
        this.mainHandler.removeCallbacks(this.addSelfToLifecycle);
        this.glide.unregisterRequestManager(this);
    }

    public RequestBuilder<Bitmap> asBitmap() {
        return mo9160as(Bitmap.class).apply(DECODE_TYPE_BITMAP);
    }

    public RequestBuilder<GifDrawable> asGif() {
        return mo9160as(GifDrawable.class).apply(DECODE_TYPE_GIF);
    }

    public RequestBuilder<Drawable> asDrawable() {
        return mo9160as(Drawable.class);
    }

    public RequestBuilder<Drawable> load(Bitmap bitmap) {
        return asDrawable().load(bitmap);
    }

    public RequestBuilder<Drawable> load(Drawable drawable) {
        return asDrawable().load(drawable);
    }

    public RequestBuilder<Drawable> load(String str) {
        return asDrawable().load(str);
    }

    public RequestBuilder<Drawable> load(Uri uri) {
        return asDrawable().load(uri);
    }

    public RequestBuilder<Drawable> load(File file) {
        return asDrawable().load(file);
    }

    public RequestBuilder<Drawable> load(Integer num) {
        return asDrawable().load(num);
    }

    @Deprecated
    public RequestBuilder<Drawable> load(URL url) {
        return asDrawable().load(url);
    }

    public RequestBuilder<Drawable> load(byte[] bArr) {
        return asDrawable().load(bArr);
    }

    public RequestBuilder<Drawable> load(Object obj) {
        return asDrawable().load(obj);
    }

    public RequestBuilder<File> downloadOnly() {
        return mo9160as(File.class).apply(DOWNLOAD_ONLY_OPTIONS);
    }

    public RequestBuilder<File> download(Object obj) {
        return downloadOnly().load(obj);
    }

    public RequestBuilder<File> asFile() {
        return mo9160as(File.class).apply(RequestOptions.skipMemoryCacheOf(true));
    }

    /* renamed from: as */
    public <ResourceType> RequestBuilder<ResourceType> mo9160as(Class<ResourceType> cls) {
        return new RequestBuilder<>(this.glide, this, cls, this.context);
    }

    public void clear(View view) {
        clear((Target<?>) new ClearTarget<Object>(view));
    }

    public void clear(final Target<?> target) {
        if (target != null) {
            if (Util.isOnMainThread()) {
                untrackOrDelegate(target);
            } else {
                this.mainHandler.post(new Runnable() {
                    public void run() {
                        RequestManager.this.clear(target);
                    }
                });
            }
        }
    }

    private void untrackOrDelegate(Target<?> target) {
        if (!untrack(target) && !this.glide.removeFromManagers(target) && target.getRequest() != null) {
            Request request = target.getRequest();
            target.setRequest(null);
            request.clear();
        }
    }

    /* access modifiers changed from: 0000 */
    public boolean untrack(Target<?> target) {
        Request request = target.getRequest();
        if (request == null) {
            return true;
        }
        if (!this.requestTracker.clearRemoveAndRecycle(request)) {
            return false;
        }
        this.targetTracker.untrack(target);
        target.setRequest(null);
        return true;
    }

    /* access modifiers changed from: 0000 */
    public void track(Target<?> target, Request request) {
        this.targetTracker.track(target);
        this.requestTracker.runRequest(request);
    }

    /* access modifiers changed from: 0000 */
    public RequestOptions getDefaultRequestOptions() {
        return this.requestOptions;
    }

    /* access modifiers changed from: 0000 */
    public <T> TransitionOptions<?, T> getDefaultTransitionOptions(Class<T> cls) {
        return this.glide.getGlideContext().getDefaultTransitionOptions(cls);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append("{tracker=");
        sb.append(this.requestTracker);
        sb.append(", treeNode=");
        sb.append(this.treeNode);
        sb.append("}");
        return sb.toString();
    }
}
