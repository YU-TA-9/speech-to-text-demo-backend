package com.my.demo.speechToTextDemo.service;

import com.my.demo.speechToTextDemo.model.request.TranscribeForm;
import com.my.demo.speechToTextDemo.model.response.SearchResponse;
import com.my.demo.speechToTextDemo.model.response.SearchResult;
import com.my.demo.speechToTextDemo.model.response.TranscribeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DemoService {

  private final TranscribeService transcribeService;

  /**
   * TranscribeResponseを取得
   *
   * @param transcribeForm
   * @return
   */
  public TranscribeResponse getTranscribeResponse(TranscribeForm transcribeForm) {
    String transcribedResult = transcribeService.getTranscribedResult(transcribeForm.getFile());
    return new TranscribeResponse(transcribedResult);
  }

  /**
   * SearchResponseのリストを取得
   *
   * @param original
   * @param targets
   * @return
   */
  public List<SearchResponse> getSearchResponse(String original, String[] targets) {
    return searchTargets(original, targets);
  }

  /**
   * 文字列を検索
   *
   * @param original
   * @param targets
   * @return
   */
  private List<SearchResponse> searchTargets(String original, String[] targets) {
    List<SearchResponse> searchResponseList = new ArrayList<SearchResponse>();
    for (String target : targets) {
      List<SearchResult> searchResultList = new ArrayList<SearchResult>();
      int position = original.indexOf(target, 0);
      if (position < 0) {
        // 一致がなければ
        searchResponseList.add(new SearchResponse(target, null));
        continue;
      }

      // 一致あり
      searchResultList.add(
          new SearchResult(position + 1, getSubString(5, original, target, position)));

      // ２つ目以降の一致を探す
      while (position < original.length()) {
        position = original.indexOf(target, position + target.length());
        if (position < 0) {
          // 一致がなければ
          break;
        }
        searchResultList.add(
            new SearchResult(position + 1, getSubString(5, original, target, position)));
      }
      searchResponseList.add(new SearchResponse(target, searchResultList));
    }
    return searchResponseList;
  }

  /**
   * 検索一致部分から前後指定された文字数分切り出し
   *
   * @param number
   * @param original
   * @param target
   * @param position
   * @return
   */
  private String getSubString(int number, String original, String target, int position) {
    if (position < number) {
      // positionが指定文字数より前
      return original.substring(0, position + target.length() + number);
    } else if ((position + target.length()) > (original.length() - number)) {
      // （position+検索文字）が検索元の末尾指定文字数以内
      return original.substring(position - number, original.length());
    } else {
      return original.substring(position - number, position + target.length() + number);
    }
  }
}
