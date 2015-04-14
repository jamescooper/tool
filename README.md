mkdir ~/old_jdk_crypto_policies; cp US_export_policy.jar local_policy.jar ~/old_jdk_crypto_policies/


sudo cp ~/Downloads/UnlimitedJCEPolicyJDK8/US_export_policy.jar ~/Downloads/UnlimitedJCEPolicyJDK8/local_policy.jar 
/Library/Java/JavaVirtualMachines/jdk1.8.0_31.jdk/Contents/Home/jre/lib/security

+Revert
sudo cp ~/old_jdk_crypto_policies/US_export_policy.jar ~/old_jdk_crypto_policies/local_policy.jar /Library/Java/JavaVirtualMachines/jdk1.8.0_31.jdk/Contents/Home/jre/lib/security

+View the Trust Store
keytool -list -v -keystore toolTrustStore.jks
