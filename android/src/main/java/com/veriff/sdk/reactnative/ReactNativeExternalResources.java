package com.veriff.sdk.reactnative;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.veriff.Branding;
import com.veriff.Branding.ExternalResources.Image;
import com.veriff.Branding.ExternalResources.Lottie;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.veriff.sdk.reactnative.VeriffSdkModule.TAG;

public class ReactNativeExternalResources implements Branding.ExternalResources, Parcelable {
    private final ImageLoader imageLoader = new ImageLoader();

    private final Map<Image, String> imageUrls;
    private final Map<Lottie, String> lottieJson;

    protected ReactNativeExternalResources(Parcel in) {
        imageUrls = readMapFromParcel(in, Image.values());
        lottieJson = readMapFromParcel(in, Lottie.values());
    }

    public ReactNativeExternalResources(Map<Image, String> imageUrls, Map<Lottie, String> lottieJson) {
        this.imageUrls = imageUrls;
        this.lottieJson = lottieJson;
    }

    @Nullable
    @Override
    public Drawable loadImage(@NonNull Context context, @NonNull Image target) throws IOException {
        String imageUrl = imageUrls.get(target);
        if (imageUrl != null) {
            return imageLoader.load(context, imageUrl);
        } else {
            return null;
        }
    }

    @Nullable
    @Override
    public String getLottieJson(Context context, @NonNull Lottie target) {
        return lottieJson.get(target);
    }

    public static final Creator<ReactNativeExternalResources> CREATOR = new Creator<ReactNativeExternalResources>() {
        @Override
        public ReactNativeExternalResources createFromParcel(Parcel in) {
            return new ReactNativeExternalResources(in);
        }

        @Override
        public ReactNativeExternalResources[] newArray(int size) {
            return new ReactNativeExternalResources[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        writeMapToParcel(dest, imageUrls);
        writeMapToParcel(dest, lottieJson);
    }

    private <E extends Enum<E>> void writeMapToParcel(Parcel dest, Map<E, String> map) {
        dest.writeInt(map.size());
        for (Map.Entry<E, String> entry : map.entrySet()) {
            dest.writeInt(entry.getKey().ordinal());
            dest.writeString(entry.getValue());
            Log.d(TAG, String.format("Write to parcel [%s] -> [%s]", entry.getKey().ordinal(), entry.getValue()));
        }
    }

    public <E extends Enum<E>> Map<E, String> readMapFromParcel(Parcel in, E[] values) {
        int count = in.readInt();

        Map<E, String> map = new HashMap<>(count);

        int i = 0;
        while (i < count) {
            i++;

            int ordinal = in.readInt();
            String value = in.readString();
            Log.d(TAG, String.format("Read from parcel [%s] -> [%s]", ordinal, value));

            E key = values[ordinal];
            map.put(key, value);
        }

        return map;
    }
}
