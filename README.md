simplechat
==========
いまのところただのTCP通信のサンプル  
並列処理・TCP通信はAkkaを使用

使い方：  
* Mavenでビルド  
* chat-serverのServerMain.java、chat-clientのClientMain.javaからそれぞれ起動  
  clientの入力内容はserverを通して全clientに配信  
  clientは"exit"を入力するとクライアントを終了
