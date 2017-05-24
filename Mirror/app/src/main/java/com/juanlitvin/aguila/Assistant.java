package com.juanlitvin.aguila;

import android.content.Context;
import android.widget.Toast;

import ai.api.AIListener;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIResponse;

public class Assistant {

    private static AIService aiService;

    public interface AssistantResult {
        void onResult(AIResponse result);
        void onError(AIError error);
    }

    public static void init(final Context context, final AssistantResult handler) {
        AIConfiguration config = new AIConfiguration("88e5cc4f864e42e0885aa0c71bb90e53", AIConfiguration.SupportedLanguages.English, AIConfiguration.RecognitionEngine.System);
        aiService = AIService.getService(context, config);
        aiService.setListener(new AIListener() {
            @Override
            public void onResult(AIResponse result) {
                handler.onResult(result);
            }

            @Override
            public void onError(AIError error) {
                handler.onError(error);
            }

            @Override
            public void onAudioLevel(float level) {

            }

            @Override
            public void onListeningStarted() {
                Toast.makeText(context, "Listening...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onListeningCanceled() {

            }

            @Override
            public void onListeningFinished() {

            }
        });
    }

    public static void listen() {
        aiService.startListening();
    }

    public static void stopListening() {
        aiService.stopListening();
    }

    public static void cancel() {
        aiService.cancel();
    }

}
