Troubleshooting
----------------
When you face an issue you could try to enable a logger to these two packages on the level specified and try to find errors, this will show in logs the information send from Jenkins (SP) to the SAML service (IdP), this information could be sensitive so take care where you copy/send it.  

    * org.jenkinsci.plugins.saml - FINEST
    * org.pac4j - FINE

**Azure AD**

After leaving the system for some period of time (like overnight) and trying to log in again you get this error

```
org.pac4j.saml.exceptions.SAMLException: No valid subject assertion found in response 
at org.pac4j.saml.sso.impl.SAML2DefaultResponseValidator.validateSamlSSOResponse(SAML2DefaultResponseValidator.java:313) 
at org.pac4j.saml.sso.impl.SAML2DefaultResponseValidator.validate(SAML2DefaultResponseValidator.java:138) 
at org.pac4j.saml.sso.impl.SAML2WebSSOMessageReceiver.receiveMessage(SAML2WebSSOMessageReceiver.java:77) 
at org.pac4j.saml.sso.impl.SAML2WebSSOProfileHandler.receive(SAML2WebSSOProfileHandler.java:35) 
at org.pac4j.saml.client.SAML2Client.retrieveCredentials(SAML2Client.java:225) 
at org.pac4j.saml.client.SAML2Client.retrieveCredentials(SAML2Client.java:60) 
at org.pac4j.core.client.IndirectClient.getCredentials(IndirectClient.java:106) 
at org.jenkinsci.plugins.saml.SamlProfileWrapper.process(SamlProfileWrapper.java:53) 
at org.jenkinsci.plugins.saml.SamlProfileWrapper.process(SamlProfileWrapper.java:33) 
at org.jenkinsci.plugins.saml.OpenSAMLWrapper.get(OpenSAMLWrapper.java:65) 
at org.jenkinsci.plugins.saml.SamlSecurityRealm.doFinishLogin(SamlSecurityRealm.java:265) 
at java.lang.invoke.MethodHandle.invokeWithArguments(MethodHandle.java:627) 
at org.kohsuke.stapler.Function$MethodFunction.invoke(Function.java:343) 
at org.kohsuke.stapler.Function.bindAndInvoke(Function.java:184) 
at org.kohsuke.stapler.Function.bindAndInvokeAndServeResponse(Function.java:117) 
at org.kohsuke.stapler.MetaClass$1.doDispatch(MetaClass.java:129) 
at org.kohsuke.stapler.NameBasedDispatcher.dispatch(NameBasedDispatcher.java:58) 
at org.kohsuke.stapler.Stapler.tryInvoke(Stapler.java:715) 
Caused: javax.servlet.ServletException at org.kohsuke.stapler.Stapler.tryInvoke(Stapler.java:765) 

...
at winstone.BoundedExecutorService$1.run(BoundedExecutorService.java:77) 
at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149) 
at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624) 
at java.lang.Thread.run(Thread.java:748)
```
* Click on logout button, then hit the Jenkins sign-in again.
* Clear the cookies in your browser or Go to Azure and sign out of the user name, then hit the Jenkins sign-in again.
* The max lifetime of the Access Token in Azure AD seems to be 24 hours where the refresh token can live for a maximum of 14 days (if the access token expires the refresh token is used to try to obtain a new access token).  The Jenkins setting in Configure Global Security > SAML Identity Provider Settings > Maximum Authentication Lifetime is 24 hours (86400 in seconds) upping this to 1209600 (which is 14 days in seconds/the max lifetime of the Refresh Token).
* Enable the advanced "force authentication" setting is another workaround.
 

**Identity provider has no single sign on service available for the selected...**

* Check the SP EntryID configured on the IdP
* Check the binding methods supported on your IdP

```
org.pac4j.saml.exceptions.SAMLException: Identity provider has no single sign on service available for the selected profileorg.opensaml.saml.saml2.metadata.impl.IDPSSODescriptorImpl@7ef38e46
	at org.pac4j.saml.context.SAML2MessageContext.getIDPSingleSignOnService(SAML2MessageContext.java:93)
	at org.pac4j.saml.sso.impl.SAML2AuthnRequestBuilder.build(SAML2AuthnRequestBuilder.java:70)
	at org.pac4j.saml.sso.impl.SAML2AuthnRequestBuilder.build(SAML2AuthnRequestBuilder.java:34)
	at org.pac4j.saml.client.SAML2Client.retrieveRedirectAction(SAML2Client.java:209)
	at org.pac4j.core.client.IndirectClient.getRedirectAction(IndirectClient.java:79)
	at org.jenkinsci.plugins.saml.SamlRedirectActionWrapper.process(SamlRedirectActionWrapper.java:47)
	at org.jenkinsci.plugins.saml.SamlRedirectActionWrapper.process(SamlRedirectActionWrapper.java:30)
	at org.jenkinsci.plugins.saml.OpenSAMLWrapper.get(OpenSAMLWrapper.java:65)
	at org.jenkinsci.plugins.saml.SamlSecurityRealm.doCommenceLogin(SamlSecurityRealm.java:260)
	at java.lang.invoke.MethodHandle.invokeWithArguments(MethodHandle.java:627)
	at org.kohsuke.stapler.Function$MethodFunction.invoke(Function.java:343)
	at org.kohsuke.stapler.Function.bindAndInvoke(Function.java:184)
	at org.kohsuke.stapler.Function.bindAndInvokeAndServeResponse(Function.java:117)
	at org.kohsuke.stapler.MetaClass$1.doDispatch(MetaClass.java:129)
	at org.kohsuke.stapler.NameBasedDispatcher.dispatch(NameBasedDispatcher.java:58)
	at org.kohsuke.stapler.Stapler.tryInvoke(Stapler.java:715)
```

**Identity provider does not support encryption settings**

* Check the encryption methods, signing methods, and keys types supported by your IdP and set the encryption settings correctly  
* Downgrade to 0.14 version, if it works, then enable encryption on that version to be sure that this is the issue
* Check the JDK version does not have issues like this [JDK-8176043](https://bugs.openjdk.java.net/browse/JDK-8176043)

```
2017-10-18 20:26:49.568+0000 [id=1296]	WARNING	o.j.p.s.SuppressionFilter#reportError: Request processing failed. URI=/securityRealm/finishLogin clientIP=192.168.1.100 ErrorID=b04ec3d5-8fbe-4961-88f2-187f47649000
org.opensaml.messaging.decoder.MessageDecodingException: This message decoder only supports the HTTP POST method
	at org.pac4j.saml.transport.Pac4jHTTPPostDecoder.doDecode(Pac4jHTTPPostDecoder.java:57)
	at org.opensaml.messaging.decoder.AbstractMessageDecoder.decode(AbstractMessageDecoder.java:58)
	at org.pac4j.saml.sso.impl.SAML2WebSSOMessageReceiver.receiveMessage(SAML2WebSSOMessageReceiver.java:40)
Caused: org.pac4j.saml.exceptions.SAMLException: Error decoding saml message
	at org.pac4j.saml.sso.impl.SAML2WebSSOMessageReceiver.receiveMessage(SAML2WebSSOMessageReceiver.java:43)
	at org.pac4j.saml.sso.impl.SAML2WebSSOProfileHandler.receive(SAML2WebSSOProfileHandler.java:35)
	at org.pac4j.saml.client.SAML2Client.retrieveCredentials(SAML2Client.java:225)
	at org.pac4j.saml.client.SAML2Client.retrieveCredentials(SAML2Client.java:60)
	at org.pac4j.core.client.IndirectClient.getCredentials(IndirectClient.java:106)
	at org.jenkinsci.plugins.saml.SamlProfileWrapper.process(SamlProfileWrapper.java:53)
```

```
Caused by: java.lang.IllegalArgumentException: Illegal base64 character d
    at java.util.Base64$Decoder.decode0(Base64.java:714)
    at java.util.Base64$Decoder.decode(Base64.java:526)
    at java.util.Base64$Decoder.decode(Base64.java:549)
    at org.jenkinsci.plugins.saml.SamlSecurityRealm.doFinishLogin(SamlSecurityRealm.java:258)
```

** The SAMLResponse is not correct bsae64 encode **

The SAMLResponse message is not valid because the `SAMLResponse` value is not in Base64 format or it is corrupted.

```
Caused by: java.lang.IllegalStateException: org.pac4j.saml.exceptions.SAMLException: Error decoding saml message
	at org.jenkinsci.plugins.saml.SamlProfileWrapper.process(SamlProfileWrapper.java:68)
	at org.jenkinsci.plugins.saml.SamlProfileWrapper.process(SamlProfileWrapper.java:39)
	at org.jenkinsci.plugins.saml.OpenSAMLWrapper.get(OpenSAMLWrapper.java:65)
	at org.jenkinsci.plugins.saml.SamlSecurityRealm.doFinishLogin(SamlSecurityRealm.java:272)
	at java.lang.invoke.MethodHandle.invokeWithArguments(MethodHandle.java:627)
	at org.kohsuke.stapler.Function$MethodFunction.invoke(Function.java:343)
	... 77 more
```

** There is no SAMLResponse parameter in the POST message **

The response message should have a parameter named `SAMLResponse` that should be the XML of the SAMLResponse in Base64 format.

```
Caused by: org.opensaml.messaging.decoder.MessageDecodingException: Request did not contain either a SAMLRequest or SAMLResponse parameter. Invalid request for SAML 2 HTTP POST binding.
	at org.pac4j.saml.transport.Pac4jHTTPPostDecoder.getBase64DecodedMessage(Pac4jHTTPPostDecoder.java:80)
	at org.pac4j.saml.transport.Pac4jHTTPPostDecoder.doDecode(Pac4jHTTPPostDecoder.java:62)
	at org.opensaml.messaging.decoder.AbstractMessageDecoder.decode(AbstractMessageDecoder.java:58)
	at org.pac4j.saml.sso.impl.SAML2WebSSOMessageReceiver.receiveMessage(SAML2WebSSOMessageReceiver.java:40)
	... 87 more
```

** No valid subject assertion found in response **

Check that the `SP Entry ID` it is the same in the SP (Jenkins) and IdP, by defaulr Jenkins uses `JENKINS_URL/securityRealm/finishLogin` you can change this value if you use the SAML Plugin's Advanced Setting named "SP Entity ID".

```
org.pac4j.saml.exceptions.SAMLException: No valid subject assertion found in response 
at org.pac4j.saml.sso.impl.SAML2DefaultResponseValidator.validateSamlSSOResponse(SAML2DefaultResponseValidator.java:313) 
at org.pac4j.saml.sso.impl.SAML2DefaultResponseValidator.validate(SAML2DefaultResponseValidator.java:138) 
at org.pac4j.saml.sso.impl.SAML2WebSSOMessageReceiver.receiveMessage(SAML2WebSSOMessageReceiver.java:77) 
at org.pac4j.saml.sso.impl.SAML2WebSSOProfileHandler.receive(SAML2WebSSOProfileHandler.java:35) 
at org.pac4j.saml.client.SAML2Client.retrieveCredentials(SAML2Client.java:225) 
at org.pac4j.saml.client.SAML2Client.retrieveCredentials(SAML2Client.java:60) 
at org.pac4j.core.client.IndirectClient.getCredentials(IndirectClient.java:106) 
at org.jenkinsci.plugins.saml.SamlProfileWrapper.process(SamlProfileWrapper.java:53) 
at org.jenkinsci.plugins.saml.SamlProfileWrapper.process(SamlProfileWrapper.java:33) 
at org.jenkinsci.plugins.saml.OpenSAMLWrapper.get(OpenSAMLWrapper.java:65) 
at org.jenkinsci.plugins.saml.SamlSecurityRealm.doFinishLogin(SamlSecurityRealm.java:265) 
at java.lang.invoke.MethodHandle.invokeWithArguments(MethodHandle.java:627) 
at org.kohsuke.stapler.Function$MethodFunction.invoke(Function.java:343) 
at org.kohsuke.stapler.Function.bindAndInvoke(Function.java:184) 
at org.kohsuke.stapler.Function.bindAndInvokeAndServeResponse(Function.java:117) 
at org.kohsuke.stapler.MetaClass$1.doDispatch(MetaClass.java:129) 
at org.kohsuke.stapler.NameBasedDispatcher.dispatch(NameBasedDispatcher.java:58) 
at org.kohsuke.stapler.Stapler.tryInvoke(Stapler.java:715) 
Caused: javax.servlet.ServletException at org.kohsuke.stapler.Stapler.tryInvoke(Stapler.java:765) 

...
at winstone.BoundedExecutorService$1.run(BoundedExecutorService.java:77) 
at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149) 
at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624) 
at java.lang.Thread.run(Thread.java:748)
```