package com.stjerncraft.controlpanel.agent.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.Consumer;

import org.json.JSONArray;
import org.junit.Test;

import com.stjerncraft.controlpanel.agent.IAgentListener;
import com.stjerncraft.controlpanel.agent.IRemoteClient;
import com.stjerncraft.controlpanel.agent.ISession;
import com.stjerncraft.controlpanel.agent.ServiceApi;
import com.stjerncraft.controlpanel.agent.ServiceProvider;
import com.stjerncraft.controlpanel.agent.local.LocalAgent;
import com.stjerncraft.controlpanel.agent.local.LocalServiceApi;
import com.stjerncraft.controlpanel.agent.local.LocalServiceProvider;
import com.stjerncraft.controlpanel.api.IServiceManager;
import com.stjerncraft.controlpanel.api.IServiceProvider;

@com.stjerncraft.controlpanel.api.annotation.ServiceApi(version = 1)
interface TestApi extends IServiceProvider {
	int test(String val);
	String[] testStr();
}

public class TestAgent {
	
	class Service implements TestApi {
		public boolean registered;
		public String testVal;
		
		@Override
		public int test(String val) {
			testVal = val;
			return 8;
		}

		@Override
		public void onRegister(IServiceManager manager) {
			registered = true;
		}

		@Override
		public void onUnregister() {
			registered = false;
		}

		@Override
		public String[] testStr() {
			String[] arr = {"str1", "str2"};
			return arr;
		}
		
	}
	
	@Test
	public void testLocalAgent() {
		LocalAgent agent = new LocalAgent("test");
		
		AtomicBoolean apiAdded = new AtomicBoolean(false);
		AtomicBoolean providerAdded = new AtomicBoolean(false);
		IAgentListener listener = new IAgentListener() {
			
			@Override
			public void onProviderRemoved(ServiceProvider<? extends ServiceApi> provider) {
				providerAdded.set(false);
			}
			
			@Override
			public void onProviderAdded(ServiceProvider<? extends ServiceApi> provider) throws Exception {
				providerAdded.set(true);
			}
			
			@Override
			public void onApiRemoved(ServiceApi api) {
				apiAdded.set(false);
			}
			
			@Override
			public void onApiAdded(ServiceApi api) throws Exception {
				if(api.getName().equals(TestApi.class.getName()))
					apiAdded.set(true);
			}
		};
		
		agent.addListener(listener);
		
		Service s = new Service();
		agent.addServiceProvider(s);
		assertTrue("API was not added", apiAdded.get());
		assertTrue("Service Provider was not added", providerAdded.get());
		
		List<LocalServiceApi> api = agent.getServiceApiList(TestApi.class.getName());
		assertTrue("API not found", api.size() > 0);
		
		List<LocalServiceProvider> service = agent.getServiceProviders(api.get(0));
		assertTrue("Service not found", service.size() > 0);
		
		assertTrue("Service Provider does not match", service.get(0).getServiceProvider() == s);
		
		assertTrue("OnRegister was not run", s.registered);
		
		AtomicBoolean sessionEnded = new AtomicBoolean(false);
		ISession session = agent.startSession(api.get(0), service.get(0), new IRemoteClient() {
			
			@Override
			public void onSessionEnd(ISession session, String reason) {
				sessionEnded.set(true);
			}
		}, 0);
		
		
		//Test method call and return
		AtomicInteger returnInt = new AtomicInteger(0);
		session.callMethod("{ \"test\": [\"strArg\"] }", new Consumer<String>() {

			@Override
			public void accept(String t) {
				JSONArray arr = new JSONArray(t);
				returnInt.set(arr.getInt(0));
			}
		});
		assertEquals("strArg", s.testVal);
		assertEquals(8, returnInt.get());
		
		//Test method call and return with array
		AtomicReferenceArray<String> returnStr = new AtomicReferenceArray<String>(2);
		session.callMethod("{ \"testStr\": [] }", new Consumer<String>() {

			@Override
			public void accept(String t) {
				JSONArray arr = new JSONArray(t);
				arr = arr.getJSONArray(0); //Arrays are packed into the JSONArray
				returnStr.set(0, arr.getString(0));
				returnStr.set(1, arr.getString(1));
			}
		});
		assertEquals("str1", returnStr.get(0));
		assertEquals("str2", returnStr.get(1));
		
		
		agent.removeServiceProvider(service.get(0));
		
		assertFalse("API was not removed", apiAdded.get());
		assertFalse("Service PRovider was not removed", providerAdded.get());
		assertFalse("OnUnregister was not run", s.registered);
		
		assertTrue("Agent Listener was not removed", agent.removeListener(listener));
		assertTrue("Session was not ended", sessionEnded.get());
	}
}
