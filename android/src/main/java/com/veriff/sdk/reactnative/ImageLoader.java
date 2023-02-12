package com.veriff.sdk.reactnative;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

public class ImageLoader {
    private static final String TAG = "ImageLoader";

    private final Handler main = new Handler(Looper.getMainLooper());

    public Drawable load(Context context, String url) throws IOException {
        if (isAsyncLogoUrl(url)) {
            Log.d(TAG, "Async image" + url);
            return loadAsyncImage(context, url);
        } else {
            Log.d(TAG, "Image from resource" + url);
            int drawableId = getDrawableId(context, url);
            return context.getDrawable(drawableId);
        }
    }

    private Drawable loadAsyncImage(Context context, String url) throws IOException {
        ImageRequest request = ImageRequestBuilder.fromRequest(ImageRequest.fromUri(url)).build();

        if (!Fresco.hasBeenInitialized()) {
            Fresco.initialize(context);
        }

        DataSource<CloseableReference<CloseableImage>> source =
                Fresco.getImagePipeline().fetchDecodedImage(request, null);

        CountDownLatch imageLoadLatch = new CountDownLatch(1);
        AtomicReference<Result> atomicResult = new AtomicReference<>(null);
        source.subscribe(new BaseBitmapDataSubscriber() {
            @Override
            protected void onNewResultImpl(Bitmap bitmap) {
                if (bitmap == null) {
                    Log.w(TAG, "Null bitmap: " + url);
                    atomicResult.set(new Result(new IOException("Loaded bitmap was null")));
                } else {
                    Log.w(TAG, "Non-Null bitmap: " + url);
                    atomicResult.set(new Result(bitmap.copy(bitmap.getConfig(), false)));
                }
                imageLoadLatch.countDown();
            }

            @Override
            protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
                Throwable failure = dataSource.getFailureCause();
                if (failure == null) {
                    atomicResult.set(new Result(new IOException("Provided failure cause was null")));
                } else if (failure instanceof IOException) {
                    atomicResult.set(new Result((IOException) failure));
                } else {
                    atomicResult.set(new Result(new IOException("Failed loading image", failure)));
                }
                imageLoadLatch.countDown();
            }
        }, main::post);


        try {
            imageLoadLatch.await();
            Result result = atomicResult.get();

            if (result.error != null) {
                Log.w(TAG, "[ImageLoader] Loading image from [" + url + "] failed", result.error);
                throw result.error;
            } else {
                Log.w(TAG, "[ImageLoader] Loading image [" + url + "] got a bitmap with size w=" + result.bitmap.getWidth() + " h=" + result.bitmap.getHeight());
                return new BitmapDrawable(context.getResources(), result.bitmap);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // preserve interrupt status
            throw new IOException("Interrupted while loading image");
        }
    }

    private static int getDrawableId(Context context, String name) {
        return context.getResources().getIdentifier(name, "drawable", context.getPackageName());
    }

    private boolean isAsyncLogoUrl(String url) {
        return url.startsWith("https://") || url.startsWith("http://") || url.startsWith("file://");
    }

    private static class Result {
        private final IOException error;

        private final Bitmap bitmap;

        private Result(IOException error) {
            this.error = error;
            this.bitmap = null;
        }

        private Result(Bitmap bitmap) {
            this.error = null;
            this.bitmap = bitmap;
        }
    }

}
