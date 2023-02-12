package com.veriff.sdk.reactnative.mapper;

import android.graphics.Typeface;
import android.util.JsonWriter;
import android.util.Log;
import androidx.annotation.NonNull;
import com.facebook.infer.annotation.Assertions;
import com.facebook.react.bridge.Dynamic;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableNativeMap;
import com.veriff.sdk.reactnative.ImageLoader;
import com.veriff.sdk.reactnative.ReactNativeExternalResources;
import com.veriff.Branding;
import com.veriff.Branding.ExternalResources.Image;
import com.veriff.Branding.ExternalResources.Lottie;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.veriff.sdk.reactnative.VeriffSdkModule.KEY_ADVANCED_CONFIGURATIONS;
import static com.veriff.sdk.reactnative.VeriffSdkModule.TAG;

public class ExternalResourcesMapper {
    public ReactNativeExternalResources map(ReadableMap brandConfig) {
        Map<Image, String> images = mapImages(brandConfig);
        Map<Lottie, String> lottie = mapLottie(brandConfig);

        if (!images.isEmpty() || !lottie.isEmpty()) {
            return new ReactNativeExternalResources(images, lottie);
        } else {
            return null;
        }
    }

    private Map<Image, String> mapImages(ReadableMap brandConfig) {
        Map<Image, String> images = new HashMap();
        if (brandConfig.hasKey(KEY_ADVANCED_CONFIGURATIONS)) {
            ReadableMap config = brandConfig.getMap(KEY_ADVANCED_CONFIGURATIONS);

            appendImage(config, KEY_DOCUMENT_FRONT, images, Image.DOCUMENT_FRONT);
            appendImage(config, KEY_DOCUMENT_BACK, images, Image.DOCUMENT_BACK_END);
            appendImage(config, KEY_BANNER_ERROR_ICON, images, Image.BANNER_ICON_ERROR);
            appendImage(config, KEY_CAMERA_ERROR, images, Image.ERROR_CAMERA);
            appendImage(config, KEY_MICROPHONE_ERROR, images, Image.ERROR_MICROPHONE);
            appendImage(config, KEY_SERVER_ERROR, images, Image.ERROR_SYSTEM);
            appendImage(config, KEY_LOCAL_ERROR, images, Image.ERROR_SYSTEM);
            appendImage(config, KEY_NETWORK_ERROR, images, Image.ERROR_NETWORK);
            appendImage(config, KEY_VERSION_ERROR, images, Image.ERROR_VERSION);
            appendImage(config, KEY_UNKNOWN_ERROR, images, Image.ERROR_UNKNOWN);
            appendImage(config, KEY_CONSENT_ICON, images, Image.CONSENT_IMAGE);
        }

        return images;
    }

    private void appendImage(
            ReadableMap advancedConfig,
            String configKey,
            Map<Branding.ExternalResources.Image, String> map,
            Branding.ExternalResources.Image mapKey
    ) {
        if (advancedConfig.hasKey(configKey)) {
            ReadableMap item = advancedConfig.getMap(configKey);
            String url = item.getString("uri");
            if (url != null) {
                map.put(mapKey, url);
            }
        }
    }

    private Map<Lottie, String> mapLottie(ReadableMap brandConfig) {
        Map<Lottie, String> lottie = new HashMap();

        if (brandConfig.hasKey(KEY_ADVANCED_CONFIGURATIONS)) {
            ReadableMap config = brandConfig.getMap(KEY_ADVANCED_CONFIGURATIONS);

            appendLottie(config, KEY_LOTTIE_PROGRESS, lottie, Lottie.PROGRESS);

        }

        return lottie;
    }

    private void appendLottie(
            ReadableMap advancedConfig,
            String configKey,
            Map<Lottie, String> map,
            Lottie mapKey
    ) {
        if (advancedConfig.hasKey(configKey)) {
            String lottie = advancedConfig.getString(configKey);

            map.put(mapKey, lottie);
        }
    }

    private final String KEY_DOCUMENT_FRONT = "documentFrontIcon";
    private final String KEY_DOCUMENT_BACK = "documentBackIcon";
    private final String KEY_BANNER_ERROR_ICON = "bannerErrorIcon";
    private final String KEY_CONSENT_ICON = "consentIcon";

    private final String KEY_CAMERA_ERROR = "cameraUnavailableErrorIcon";
    private final String KEY_MICROPHONE_ERROR = "microphoneUnavailableErrorIcon";
    private final String KEY_SERVER_ERROR = "serverErrorIcon";
    private final String KEY_LOCAL_ERROR = "localErrorIcon";
    private final String KEY_NETWORK_ERROR = "networkErrorIcon";
    private final String KEY_VERSION_ERROR = "deprecatedSDKVersionErrorIcon";
    private final String KEY_UNKNOWN_ERROR = "unknownErrorIcon";

    private final String KEY_LOTTIE_PROGRESS = "loadingAnimation";
}
