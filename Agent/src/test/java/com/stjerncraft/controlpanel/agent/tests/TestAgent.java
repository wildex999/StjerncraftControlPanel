package com.stjerncraft.controlpanel.agent.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.Consumer;

import org.json.JSONArray;
import org.junit.Test;

import com.stjerncraft.controlpanel.agent.IAgentListener;
import com.stjerncraft.controlpanel.agent.ISession;
import com.stjerncraft.controlpanel.agent.ISessionListener;
import com.stjerncraft.controlpanel.agent.ServiceProvider;
import com.stjerncraft.controlpanel.agent.local.LocalAgent;
import com.stjerncraft.controlpanel.agent.local.LocalServiceApi;
import com.stjerncraft.controlpanel.agent.local.LocalServiceProvider;
import com.stjerncraft.controlpanel.api.IServiceManager;
import com.stjerncraft.controlpanel.api.IServiceProvider;
import com.stjerncraft.controlpanel.common.ServiceApi;
import com.stjerncraft.controlpanel.common.exceptions.InvalidUUIDException;

@com.stjerncraft.controlpanel.api.annotation.ServiceApi(version = 1)
interface TestApi {
	int test(String val1);
	int test(int val2);
	String[] testStr();
}

interface TestInherit extends TestApi {
	//It should detect the TestApi even if not inherited directly
}

public class TestAgent {
	
	class Service implements TestInherit, IServiceProvider {
		public boolean registered;
		public String testVal;
		public int testVal2;
		
		@Override
		public int test(String val1) {
			testVal = val1;
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

		@Override
		public int test(int val2) {
			testVal2 = val2;
			return val2;
		}
		
	}
	
	@Test
	public void testLocalAgent() {
		LocalAgent agent = new LocalAgent("test", null);
		
		AtomicBoolean apiAdded = new AtomicBoolean(false);
		AtomicBoolean providerAdded = new AtomicBoolean(false);
		IAgentListener listener = new IAgentListener() {
			
			@Override
			public void onProviderRemoved(ServiceProvider<? extends ServiceApi> provider) {
				providerAdded.set(false);
			}
			
			@Override
			public void onProviderAdded(ServiceProvider<? extends ServiceApi> provider) throws InvalidUUIDException {
				providerAdded.set(true);
			}
			
			@Override
			public void onApiRemoved(ServiceApi api) {
				apiAdded.set(false);
			}
			
			@Override
			public void onApiAdded(ServiceApi api) throws InvalidUUIDException {
				if(api.getName().equals(TestApi.class.getName()))
					apiAdded.set(true);
			}
		};
		
		agent.addListener(listener);
		
		Service s = new Service();
		agent.addServiceProvider(s, null);
		assertTrue("API was not added", apiAdded.get());
		assertTrue("Service Provider was not added", providerAdded.get());
		
		List<LocalServiceApi> api = agent.getServiceApiList(TestApi.class.getName());
		assertTrue("API not found", api.size() > 0);
		
		List<LocalServiceProvider> service = agent.getServiceProviders(api.get(0));
		assertTrue("Service not found", service.size() > 0);
		
		assertTrue("Service Provider does not match", service.get(0).getServiceProvider() == s);
		
		assertTrue("OnRegister was not run", s.registered);
		
		//Session
		AtomicBoolean sessionStarted = new AtomicBoolean(false);
		AtomicBoolean sessionEnded = new AtomicBoolean(false);
		ISession session = agent.startSession(api.get(0), service.get(0), null, 0);
		session.addListener(new ISessionListener() {
			
			@Override
			public void onSessionStarted() {
				sessionStarted.set(true);
			}
			
			@Override
			public void onSessionEnded(String reason) {
				sessionEnded.set(true);
			}
		});
		sessionStarted.set(session.hasStarted());
		assertTrue("Session was not started!", sessionStarted.get());
		
		
		//Test method call and return
		AtomicInteger returnInt = new AtomicInteger(0);
		session.callMethod("{ \"test$Str$\": [\"strArg\"] }", new Consumer<String>() {

			@Override
			public void accept(String t) {
				JSONArray arr = new JSONArray(t);
				returnInt.set(arr.getInt(0));
			}
		});
		assertEquals("strArg", s.testVal);
		assertEquals(8, returnInt.get());
		
		//Test method call same name but different signature
		session.callMethod("{ \"test$I$\": [10] }", new Consumer<String>() {

			@Override
			public void accept(String t) {
				JSONArray arr = new JSONArray(t);
				returnInt.set(arr.getInt(0));
			}
			
		});
		assertEquals(10, s.testVal2);
		assertEquals(10, returnInt.get());
		
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
		assertFalse("Service Provider was not removed", providerAdded.get());
		assertFalse("OnUnregister was not run", s.registered);
		
		assertTrue("Agent Listener was not removed", agent.removeListener(listener));
		assertTrue("Session was not ended", sessionEnded.get());
	}
}
