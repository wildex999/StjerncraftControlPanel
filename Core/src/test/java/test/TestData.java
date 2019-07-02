package test;

import static org.junit.Assert.*;

import org.json.JSONArray;
import org.junit.Test;

import com.stjerncraft.controlpanel.api.IDataObjectGenerated;
import com.stjerncraft.controlpanel.api.annotation.DataObject;
import com.stjerncraft.controlpanel.api.util.Generated;

@DataObject
class Data {
	public enum TestEnum {
		A, B, C;
	}
	
	public int a;
	public Integer b;
	public String t;
	public Float[] tt;
	public DataOther other;
	int priv;
	public TestEnum en;
	
	public Data() {}
}

@DataObject
class DataOther {
	public int a;
	public String b;
	public Data nullOther;
	
	public DataOther() {}
}

class DataInherit extends Data {
	public int g;
	
	public DataInherit() {}
}

public class TestData {
	
	@Test
	public void testSerializeAndDeserialize() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Data testObj = new Data();
		testObj.a = 10;
		testObj.b = 22;
		testObj.t = "Test";
		testObj.tt = new Float[]{11.2f, 0.5f, 0.1f, 4f};
		testObj.other = new DataOther();
		testObj.other.a = 1;
		testObj.other.b = "lol";
		testObj.priv = 44;
		testObj.en = Data.TestEnum.B;
		
		
		IDataObjectGenerated<Data> dataGenerated = Generated.getGeneratedDataObject(Data.class);
		JSONArray json = dataGenerated.serialize(testObj);
		Data obj = dataGenerated.parse(json);

		//Test that the inherited class also generates
		Generated.getGeneratedDataObject(DataInherit.class).parse(new JSONArray());
		
		assertEquals(10, obj.a);
		assertEquals((Integer)22, obj.b);
		assertNotNull(obj.other);
		assertEquals(1, obj.other.a);
		assertEquals("lol", obj.other.b);
		assertEquals("Test", obj.t);
		assertEquals(11.2, obj.tt[0], 0.1);
		assertEquals(0.5, obj.tt[1], 0.1);
		assertEquals(0.1, obj.tt[2], 0.1);
		assertEquals(4, obj.tt[3], 0.1);
		assertEquals(0, obj.priv);
		assertNull(obj.other.nullOther);
		assertEquals(Data.TestEnum.B, obj.en);
	}

}
