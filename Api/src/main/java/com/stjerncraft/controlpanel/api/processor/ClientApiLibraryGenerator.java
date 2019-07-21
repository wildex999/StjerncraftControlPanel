package com.stjerncraft.controlpanel.api.processor;

import java.io.IOException;
import java.util.Collection;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;

import org.json.JSONArray;
import org.json.JSONObject;

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.stjerncraft.controlpanel.api.client.ICallMethodReturnHandler;
import com.stjerncraft.controlpanel.api.client.IClientApiLibrary;
import com.stjerncraft.controlpanel.api.client.IClientCore;
import com.stjerncraft.controlpanel.api.client.IClientSubscriptionHandler;
import com.stjerncraft.controlpanel.api.client.ISession;

/**
 * Generates the Client API Library for use by GWT Clients.
 * Provides Async Methods for the API calls, returning a Future instead of the return value.
 */
public class ClientApiLibraryGenerator {
DataObjectProcessor dataObjects;
	
	public ClientApiLibraryGenerator(DataObjectProcessor dataObjects) {
		this.dataObjects = dataObjects;
	}

	/**
	 * Generate a Client API proxy class for each Service API
	 * @param filer
	 * @throws IOException
	 */
	public void generateClientLibrary(Filer filer, ServiceApiInfo api) throws IOException {
		String packageName = "";
		String className = api.getName();
		int nameIndex = className.lastIndexOf(".");
		if(nameIndex != -1) {
			packageName = className.substring(0, nameIndex);
			className = className.substring(nameIndex+1);
		}
		className += ApiStrings.APICLIENTLIBRARYSUFFIX;
		
		TypeSpec.Builder builder = TypeSpec.classBuilder(className)
				.addSuperinterface(IClientApiLibrary.class)
				.addField(IClientCore.class, "clientCore", Modifier.PRIVATE)
				.addField(ISession.class, "session", Modifier.PRIVATE)
				.addModifiers(Modifier.PUBLIC)
				.addMethod(generateConstructor());
		
		builder.addMethod(generateStaticGetApiName(api));
		builder.addMethod(generateGetApiName(api));
		builder.addMethod(generateStaticGetApiVersion(api));
		builder.addMethod(generateGetApiVersion(api));
		builder.addMethod(generateSetSession(api));
		builder.addMethod(generateStaticGet(api, packageName, className));
		
		//Methods & Event Subscriptions
		Collection<Method> methods = api.getMethods();
		for(Method method : methods) {
			MethodSpec generatedMethod;
			if(method.isEventHandler())
			{
				generatedMethod = generateCallSubscribe(method);
			} else {
				generatedMethod = generateCallMethod(method);
			}
			
			builder.addMethod(generatedMethod);
		}
		
		 
		TypeSpec generatedClass = builder.build();
		JavaFile javaFile = JavaFile.builder(packageName, generatedClass).build();
		javaFile.writeTo(filer);
	}
	
	private MethodSpec generateConstructor() {
		return MethodSpec.constructorBuilder()
			.addModifiers(Modifier.PUBLIC)
			.addParameter(IClientCore.class, "clientCore")
			.addParameter(ISession.class, "session")
			.beginControlFlow("if(clientCore == null)")
			.addStatement("throw new $T($S)", RuntimeException.class, "Missing Client Core")
			.endControlFlow()
			.addStatement("this.clientCore = clientCore")
			.addStatement("this.session = session")
			.build();
	}
	
	private MethodSpec generateStaticGetApiName(ServiceApiInfo api) {
		MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("getName")
				.addModifiers(Modifier.PUBLIC, Modifier.STATIC)
				.returns(String.class);
		
		methodBuilder.addStatement("return $S", api.getName());
		
		return methodBuilder.build();
	}
	
	private MethodSpec generateStaticGetApiVersion(ServiceApiInfo api) {
		MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("getVersion")
				.addModifiers(Modifier.PUBLIC, Modifier.STATIC)
				.returns(int.class);
		
		methodBuilder.addStatement("return $L", api.getVersion());
		
		return methodBuilder.build();
	}
	
	private MethodSpec generateGetApiName(ServiceApiInfo api) {
		MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("getApiName")
				.addAnnotation(Override.class)
				.addModifiers(Modifier.PUBLIC)
				.returns(String.class);
		
		methodBuilder.addStatement("return $S", api.getName());
		
		return methodBuilder.build();
	}
	
	private MethodSpec generateGetApiVersion(ServiceApiInfo api) {
		MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("getApiVersion")
				.addAnnotation(Override.class)
				.addModifiers(Modifier.PUBLIC)
				.returns(int.class);
		
		methodBuilder.addStatement("return $L", api.getVersion());
		
		return methodBuilder.build();
	}
	
	private MethodSpec generateSetSession(ServiceApiInfo api) {
		MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("setSession")
				.addAnnotation(Override.class)
				.addModifiers(Modifier.PUBLIC)
				.addParameter(ISession.class, "session")
				.returns(void.class);
		
		methodBuilder.addStatement("this.session = session");
		
		return methodBuilder.build();
	}
	
	private MethodSpec generateStaticGet(ServiceApiInfo api, String packageName, String className) {
		MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("get")
				.addParameter(IClientCore.class, "clientCore")
				.addModifiers(Modifier.PUBLIC, Modifier.STATIC)
				.returns(ClassName.get(packageName, className));
		
		methodBuilder.addStatement("return new $T(clientCore, null)", ClassName.get(packageName, className));
		
		return methodBuilder.build();
	}
	
	/**
	 * Generate the API method calls to proxy for the Client Core
	 * @return
	 */
	private MethodSpec generateCallMethod(Method method) {
		MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(method.getMethodName())
				.addModifiers(Modifier.PUBLIC)
				.returns(boolean.class);
		
		String comments = method.getComments();
		if(comments != null)
			methodBuilder.addJavadoc(comments);
		
		//Parameters
		for(Field par : method.getParameters()) {
			TypeName type = ClassName.bestGuess(par.fieldType.getCanonicalName());
			if(par.isArray)
				type = ArrayTypeName.of(type);
			
			methodBuilder.addParameter(type, par.name);
		}
		
		//Callback if applicable
		String callbackVar;
		if(method.getReturnType() != BaseType.Void.type)
		{
			TypeName returnType = ClassName.bestGuess(method.getReturnType().getCanonicalName());
			if(method.isReturnArray())
				returnType = ArrayTypeName.of(returnType);
			ParameterizedTypeName callbackHandlerType = ParameterizedTypeName.get(ClassName.get(ICallMethodReturnHandler.class), returnType);
			methodBuilder.addParameter(callbackHandlerType, "callback");
			
			//Create Callback proxy, deserializing the result
			callbackVar = "callbackProxy";
			
			CodeBlock.Builder callbackBuilder = CodeBlock.builder();
			callbackBuilder.addStatement("$T returnJSON = new $T(value)", JSONArray.class, JSONArray.class);
			Field returnField = new Field("", method.isReturnArray(), method.getReturnType());
			Parse.parseVariable(returnField, "returnJSON", 0, callbackBuilder, dataObjects, "returnValue");
			callbackBuilder.addStatement("callback.onReturnValue(returnValue)");
			
			ParameterizedTypeName callbackHandlerProxyType = ParameterizedTypeName.get(ICallMethodReturnHandler.class, String.class);
			methodBuilder.addStatement("$T $L = (String value) -> { $L }", callbackHandlerProxyType, callbackVar, callbackBuilder.build().toString());
			
		} else
			callbackVar = "null";
		
		//Check if session is in correct state
		methodBuilder.beginControlFlow("if(session == null || !session.isValid())");
		methodBuilder.addStatement("return false");
		methodBuilder.endControlFlow();
		
		//Serialize parameters into JSON method object
		String parArray = "methodParameters";
		methodBuilder.addStatement("$T $L = new $T()", JSONArray.class, parArray, JSONArray.class);
		for(Field par : method.getParameters())
		{
			CodeBlock.Builder serializeCode = CodeBlock.builder();
			Serialize.serializeVariable(par.fieldType, par.name, par.isArray, serializeCode, dataObjects, parArray);
			methodBuilder.addCode(serializeCode.build());
		}
		String methodObj = "methodObject";
		methodBuilder.addStatement("$T $L = new $T()", JSONObject.class, methodObj, JSONObject.class);
		methodBuilder.addStatement("$L.put($S, $L)", methodObj, method.getFullName(), parArray);
		
		//Send to Core
		methodBuilder.addStatement("clientCore.callMethod(session.getSessionId(), $L.toString(), $L)", methodObj, callbackVar);
		methodBuilder.addStatement("return true");

		return methodBuilder.build();
	}
	
	/**
	 * Generate the API subscribe calls to proxy for the Client Core
	 * @return
	 */
	private MethodSpec generateCallSubscribe(Method method) {
		MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(method.getMethodName())
				.addModifiers(Modifier.PUBLIC)
				.returns(boolean.class);
		
		String comments = method.getComments();
		if(comments != null)
			methodBuilder.addJavadoc(comments);
		
		//Parameters
		for(Field par : method.getParameters()) {
			TypeName type = ClassName.bestGuess(par.fieldType.getCanonicalName());
			if(par.isArray)
				type = ArrayTypeName.of(type);
			
			methodBuilder.addParameter(type, par.name);
		}
		
		//Check if session is in correct state
		methodBuilder.beginControlFlow("if(session == null || !session.isValid())");
		methodBuilder.addStatement("return false");
		methodBuilder.endControlFlow();
		
		//Proxy Subscription Handler
		TypeName eventType = ClassName.bestGuess(method.getReturnType().getCanonicalName());
		if(method.isReturnArray())
			eventType = ArrayTypeName.of(eventType);
		String subscriptionHandlerVar = "proxyHandler";
		ParameterizedTypeName handlerType = ParameterizedTypeName.get(ClassName.get(IClientSubscriptionHandler.class), eventType);
		methodBuilder.addParameter(handlerType, "handler");
		TypeSpec proxyHandler = generateSubscriptionHandlerProxy(method);
		ParameterizedTypeName proxyHandlerType = ParameterizedTypeName.get(IClientSubscriptionHandler.class, String.class);
		methodBuilder.addStatement("$T $L = $L", proxyHandlerType, subscriptionHandlerVar, proxyHandler);
		
		
		//Serialize parameters into JSON method object
		String parArray = "methodParameters";
		methodBuilder.addStatement("$T $L = new $T()", JSONArray.class, parArray, JSONArray.class);
		for(Field par : method.getParameters())
		{
			CodeBlock.Builder serializeCode = CodeBlock.builder();
			Serialize.serializeVariable(par.fieldType, par.name, par.isArray, serializeCode, dataObjects, parArray);
			methodBuilder.addCode(serializeCode.build());
		}
		String methodObj = "methodObject";
		methodBuilder.addStatement("$T $L = new $T()", JSONObject.class, methodObj, JSONObject.class);
		methodBuilder.addCode("$L.put($S, $L);", methodObj, method.getFullName(), parArray);
		
		//Send to Core
		methodBuilder.addStatement("clientCore.callSubscribe(session.getSessionId(), $L.toString(), $L)", methodObj, subscriptionHandlerVar);
		methodBuilder.addStatement("return true");
		
		return methodBuilder.build();
	}
	
	/**
	 * Generate anonymous instance of the IClientSubscriptionHandler for the given Subscription.
	 * This will handle the deserialization of the event data, and proxy the call to the user provided Subscription Handler.
	 */
	private TypeSpec generateSubscriptionHandlerProxy(Method method) {
		TypeName eventType = ClassName.bestGuess(method.getReturnType().getCanonicalName());
		if(method.isEventHandler())
			eventType = ArrayTypeName.of(eventType);
		
		String handlerVar = "handler";
		
		MethodSpec.Builder onEventMethodBuilder = MethodSpec.methodBuilder("OnEvent")
				.addAnnotation(Override.class)
				.addModifiers(Modifier.PUBLIC, Modifier.FINAL)
				.addParameter(int.class, "subscriptionId")
				.addParameter(String.class, "value");
		CodeBlock.Builder parser = CodeBlock.builder()
				.addStatement("$T valueJson = new $T(value)", JSONArray.class, JSONArray.class);
		Field valueField = new Field("", method.isReturnArray(), method.getReturnType());
		Parse.parseVariable(valueField, "valueJson", 0, parser, dataObjects, "parsedValue");
		onEventMethodBuilder.addCode(parser.build())
				.addStatement("$L.OnEvent(subscriptionId, parsedValue)", handlerVar);
		
		MethodSpec onSubscribedMethod = MethodSpec.methodBuilder("OnSubscribed")
				.addAnnotation(Override.class)
				.addModifiers(Modifier.PUBLIC, Modifier.FINAL)
				.addParameter(int.class, "subscriptionId")
				.addParameter(int.class, "callId")
				.addParameter(boolean.class, "success")
				.addStatement("$L.OnSubscribed(subscriptionId, callId, success)", handlerVar)
				.build();
		
		
		MethodSpec onUnsubscribedMethod = MethodSpec.methodBuilder("OnUnsubscribed")
				.addAnnotation(Override.class)
				.addModifiers(Modifier.PUBLIC, Modifier.FINAL)
				.addParameter(int.class, "subscriptionId")
				.addStatement("$L.OnUnsubscribed(subscriptionId)", handlerVar)
				.build();
		
		
		TypeSpec proxyClass = TypeSpec.anonymousClassBuilder("")
			.addSuperinterface(ParameterizedTypeName.get(IClientSubscriptionHandler.class, String.class))
			.addMethod(onEventMethodBuilder.build())
			.addMethod(onSubscribedMethod)
			.addMethod(onUnsubscribedMethod)
			.build();
		
		return proxyClass;
	}
}
