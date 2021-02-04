# im

- msg 公共消息服务
- pc-client pc客户端
- server 服务器


# tls
```
生成Netty服务器公钥
keytool -genkey -alias server -keysize 2048 -validity 3650 -keyalg RSA -dname "CN=localhost" -keypass nettydemo -storepass nettydemo -keystore serverStore.jks

导出Netty服务端签名证书
keytool -export -alias server -keystore serverStore.jks -storepass nettydemo -file server.cer

生成Netty客户端的公钥、私钥和证书仓库
keytool -genkey -alias client -keysize 2048 -validity 3650 -keyalg RSA -dname "CN=localhost" -keypass nettydemo -storepass nettydemo -keystore clientStore.jks

将Netty服务端的证书导入到客户端的证书仓库中
keytool -import -trustcacerts -alias server -file server.cer -storepass nettydemo -keystore clientStore.jks
```