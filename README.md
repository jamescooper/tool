# Overview
This is a set of tools and processes to test, how JVM based tools can handle TLS certificates. This should be used to 
test/debug and support carriers that employ Java based technologies. It should help people understand and validate, support
changes ahead of time.
 

#Tested Runtime
 - OSX 10.10.2 
 - Java 1.8.0_31-b13
 - Apache Maven 3.3.1

## Set the username to be used in code.
vi src/main/java/com/example/DefaultKeystoreApp.java 
vi src/main/java/com/example/CustomKeystoreApp.java

Look for the following line:

```java
request.initializeClient("charter-api-coopja", "password", true);
```

Change to the appropriate username and password.

# Compile
mvn clean package

## Run Default 
Uses the default trusted certificate chain baked into the JVM.

Example, OSX JVM
/Library/Java/JavaVirtualMachines/jdk1.8.0_31.jdk/Contents/Home/jre/lib/security/cacerts

java -jar target/tls-1.0-SNAPSHOT.jar

## Run, Custom Keystore
Uses the toolTrustStore.jks

java -cp target/tls-1.0-SNAPSHOT.jar com.example.CustomKeystoreApp

## Debug TLS connections
java -Djavax.net.debug=all -jar target/tls-1.0-SNAPSHOT.jar

## Debug TLS connections with overriding the default truststore
In this case we'll simulate a user attempting to request a RESTful TLS endpoint without the ability to trust the root ca.

java -Djavax.net.debug=all -Djavax.net.ssl.trustStore=toolBadTrustStore.jks -jar target/tls-1.0-SNAPSHOT.jar

# Solving JSSE Security Woes

## List the certificates stored in the VALID keystore
keytool -list -v -keystore toolTrustStore.jks -storepass password

## List the certificates stored in the BAD keystore
keytool -list -v -keystore toolBadTrustStore.jks -storepass password

## Import a certificate into a NEW keystore
First get your certifcate's in binary DER format.

keytool -import -file ~/Downloads/safeavenue-naadmin-2/Baltimore\ CyberTrust\ Root.DER -alias baltimore -keystore new_store.jks -storepass password

### Run tool with the new keystore, debug off
java -Djavax.net.ssl.trustStore=new_store.jks -jar target/tls-1.0-SNAPSHOT.jar

## How to check if your environment supports AES 128
java -cp target/tls-1.0-SNAPSHOT.jar com.example.AesApp

## How to check if your environment supports AES 256
java -cp target/tls-1.0-SNAPSHOT.jar com.example.AesApp -b 256

#### Example Error Message
> java.lang.RuntimeException: java.security.InvalidKeyException: Illegal key size or default parameters
  	at com.example.AesApp.encrypt(AesApp.java:71)
  	at com.example.AesApp.main(AesApp.java:16)
  Caused by: java.security.InvalidKeyException: Illegal key size or default parameters
  	at javax.crypto.Cipher.checkCryptoPerm(Cipher.java:1021)
  	at javax.crypto.Cipher.implInit(Cipher.java:796)
  	at javax.crypto.Cipher.chooseProvider(Cipher.java:859)
  	at javax.crypto.Cipher.init(Cipher.java:1229)
  	at javax.crypto.Cipher.init(Cipher.java:1166)
  	at com.example.AesApp.encrypt(AesApp.java:68)
 

### Enable stronger cryptography in the JVM
You will need the download the Java Cryptography Extension for your JVM version. For JRE 8

[Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files 8 Download](http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html)

> mkdir ~/old_jdk_crypto_policies

> cp /Library/Java/JavaVirtualMachines/jdk1.8.0_31.jdk/Contents/Home/jre/lib/security/US_export_policy.jar /Library/Java/JavaVirtualMachines/jdk1.8.0_31.jdk/Contents/Home/jre/lib/security/local_policy.jar ~/old_jdk_crypto_policies/

> sudo cp ~/Downloads/UnlimitedJCEPolicyJDK8/US_export_policy.jar ~/Downloads/UnlimitedJCEPolicyJDK8/local_policy.jar /Library/Java/JavaVirtualMachines/jdk1.8.0_31.jdk/Contents/Home/jre/lib/security


### Retest the AES 256 limitation
java -cp target/tls-1.0-SNAPSHOT.jar com.example.AesApp -b 256

> com.example.AesApp: Start

> Encrypted Data: �C#%<`,���>i��C

> Decrypted Data: Hello World

> com.example.AesApp: Finnish

### Revert to original crypto setup
> sudo cp ~/old_jdk_crypto_policies/US_export_policy.jar ~/old_jdk_crypto_policies/local_policy.jar /Library/Java/JavaVirtualMachines/jdk1.8.0_31.jdk/Contents/Home/jre/lib/security

### Simulate Limitations > version Java 7
> sudo vi /Library/Java/JavaVirtualMachines/jdk1.8.0_31.jdk/Contents/Home/jre/lib/security/java.security

Disable SHA256
> jdk.certpath.disabledAlgorithms=MD2, SHA256, RSA keySize < 1024

> Exception in thread "main" javax.ws.rs.ProcessingException: javax.net.ssl.SSLHandshakeException: sun.security.validator.ValidatorException: PKIX path validation failed: java.security.cert.CertPathValidatorException: Algorithm constraints check failed: SHA256
  	at org.glassfish.jersey.client.HttpUrlConnector.apply(HttpUrlConnector.java:244)
  	at org.glassfish.jersey.client.ClientRuntime.invoke(ClientRuntime.java:245)
  	at org.glassfish.jersey.client.JerseyInvocation$1.call(JerseyInvocation.java:671)
  	at org.glassfish.jersey.client.JerseyInvocation$1.call(JerseyInvocation.java:668)
  	at org.glassfish.jersey.internal.Errors.process(Errors.java:315)
  	at org.glassfish.jersey.internal.Errors.process(Errors.java:297)
  	at org.glassfish.jersey.internal.Errors.process(Errors.java:228)
  	at org.glassfish.jersey.process.internal.RequestScope.runInScope(RequestScope.java:444)
  	at org.glassfish.jersey.client.JerseyInvocation.invoke(JerseyInvocation.java:668)
  	at org.glassfish.jersey.client.JerseyInvocation$Builder.method(JerseyInvocation.java:402)
  	at org.glassfish.jersey.client.JerseyInvocation$Builder.get(JerseyInvocation.java:302)
  	at com.example.EchoRequest.send(EchoRequest.java:36)
  	at com.example.DefaultKeystoreApp.main(DefaultKeystoreApp.java:13)
  	
### Disable SHA256 Ciphers in TLS  	

> jdk.tls.disabledAlgorithms=SSLv3, TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256, TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256,TLS_RSA_WITH_AES_128_CBC_SHA256, TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA256, TLS_ECDH_RSA_WITH_AES_128_CBC_SHA256, TLS_DHE_RSA_WITH_AES_128_CBC_SHA256, TLS_DHE_DSS_WITH_AES_128_CBC_SHA256




