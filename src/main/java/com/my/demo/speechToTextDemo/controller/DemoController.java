package com.my.demo.speechToTextDemo.controller;

import com.my.demo.speechToTextDemo.model.request.TranscribeForm;
import com.my.demo.speechToTextDemo.model.response.SearchResponse;
import com.my.demo.speechToTextDemo.model.response.TranscribeResponse;
import com.my.demo.speechToTextDemo.service.DemoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/demo")
@RequiredArgsConstructor
public class DemoController {

  private final DemoService demoService;

  /**
   * 音声変換API Todo: リクエストのバリデーションを設定する（ファイルの拡張子やサイズなど）
   *
   * @param transcribeForm
   * @return
   */
  @PostMapping("/transcribe")
  public TranscribeResponse getSpeechToText(TranscribeForm transcribeForm) {
    return demoService.getTranscribeResponse(transcribeForm);
  }

  /**
   * 文字検索API　Todo: リクエストのバリデーションを設定する
   *
   * @param targets
   * @return
   */
  @GetMapping("/search")
  public List<SearchResponse> getSpeechToText(
      @RequestParam String original, @RequestParam String[] targets) {
    return demoService.getSearchResponse(original, targets);
  }
}
