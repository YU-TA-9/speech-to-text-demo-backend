# Speech-to-Text Demo Backend

音声認識 DEMO のバックエンドソース

## Author

YU-TA-9

## Main Libraries

* Spring Boot 2.5.2

## Require

- Java 11
- gradle 7.1.1
- AWSアカウント及びIAMユーザー
  - ユーザーには、Transcribeへのアクセス許可を設定したIAMポリシーを紐付けてください
- AWS CLI：使用端末にて認証情報の設定まで行うこと（設定したIAMユーザーを通じてAWSにリクエストが送信されます）
  - https://docs.aws.amazon.com/ja_jp/cli/latest/userguide/cli-configure-files.html

## Installation

```zsh
$ gradle bootRun
```

## APIs

- `Swagger.yaml`をご参照ください

## Note

- 音声認識APIには`Amazon Transcribe`を使用しており、変換API実行毎に課金が発生する可能性があります
- IntelliJ IDEAで開発を行う際はLombokの拡張機能をインストールしてください
- フォーマッターには`google-java-format`を使用してください

## References

- https://aws.amazon.com/jp/transcribe/
- https://github.com/awsdocs/aws-doc-sdk-examples/tree/master/javav2/example_code/transcribe



