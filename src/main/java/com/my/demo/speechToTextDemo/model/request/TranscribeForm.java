package com.my.demo.speechToTextDemo.model.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class TranscribeForm {
  private MultipartFile file;
}
