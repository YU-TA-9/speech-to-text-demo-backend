package com.my.demo.speechToTextDemo.service;

import com.my.demo.speechToTextDemo.module.transcribe.AudioStreamPublisher;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.transcribe.TranscribeClient;
import software.amazon.awssdk.services.transcribestreaming.TranscribeStreamingAsyncClient;
import software.amazon.awssdk.services.transcribestreaming.model.*;

import java.io.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class TranscribeService {

  private static final Region REGION = Region.AP_NORTHEAST_1;
  private static TranscribeStreamingAsyncClient client;
  private static TranscribeClient transcribeClient;

  private static String transcribedResult = "";

  // Todo:実行端末の認証情報を読み取る実装になってしまっているので、もう少し汎用的に使える設計にする
  private static AwsCredentialsProvider getCredentials() {
    return DefaultCredentialsProvider.create();
  }

  /**
   * 音声書き起こし結果取得
   *
   * @return
   */
  public String getTranscribedResult(MultipartFile multipartFile) {
    transcribeClient =
        TranscribeClient.builder().credentialsProvider(getCredentials()).region(REGION).build();
    client =
        TranscribeStreamingAsyncClient.builder()
            .credentialsProvider(getCredentials())
            .region(REGION)
            .build();

    CompletableFuture<Void> result =
        client.startStreamTranscription(
            getRequest(16_000),
            new AudioStreamPublisher(getStreamFromFile(multipartFile)),
            getResponseHandler());
    try {
      result.get();
      client.close();
      System.out.println("文字起こし結果：" + transcribedResult);
      return transcribedResult;

    } catch (Exception e) {
      System.out.println("失敗:" + e);
      return null;
    }
  }

  /**
   * 音声ファイルから文字起こし
   *
   * @param multipartFile
   * @return
   */
  private InputStream getStreamFromFile(MultipartFile multipartFile) {
    File inputFile = null;
    try {
      inputFile = new File("src/main/resources/" + multipartFile.getOriginalFilename());
      OutputStream os = new FileOutputStream(inputFile);
      os.write(multipartFile.getBytes());
      InputStream audioStream = new FileInputStream(inputFile);
      return audioStream;
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      inputFile.delete();
    }
  }

  /**
   * リクエスト設定
   *
   * @param mediaSampleRateHertz
   * @return
   */
  private static StartStreamTranscriptionRequest getRequest(Integer mediaSampleRateHertz) {
    return StartStreamTranscriptionRequest.builder()
        .languageCode(LanguageCode.JA_JP.toString())
        .mediaEncoding(MediaEncoding.PCM)
        .mediaSampleRateHertz(mediaSampleRateHertz)
        .build();
  }

  /**
   * レスポンス設定
   *
   * @return
   */
  private static StartStreamTranscriptionResponseHandler getResponseHandler() {
    return StartStreamTranscriptionResponseHandler.builder()
        .onResponse(
            r -> {
              System.out.println("Received Initial response");
            })
        .onError(
            e -> {
              System.out.println(e.getMessage());
              StringWriter sw = new StringWriter();
              e.printStackTrace(new PrintWriter(sw));
              System.out.println("Error Occurred: " + sw.toString());
            })
        .onComplete(
            () -> {
              System.out.println("=== All records stream successfully ===");
            })
        .subscriber(
            event -> {
              List<Result> results = ((TranscriptEvent) event).transcript().results();
              if (results.size() > 0) {
                if (!results.get(0).isPartial()) {
                  // 翻訳が完了した文字列のみ保持
                  System.out.println(results.get(0).alternatives().get(0).transcript());
                  transcribedResult += results.get(0).alternatives().get(0).transcript();
                }
              }
            })
        .build();
  }
}
