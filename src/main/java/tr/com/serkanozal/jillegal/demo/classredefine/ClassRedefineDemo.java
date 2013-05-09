package tr.com.serkanozal.jillegal.demo.classredefine;

import tr.com.serkanozal.jillegal.core.Jillegal;
import tr.com.serkanozal.jillegal.core.memory.DirectMemoryService;
import tr.com.serkanozal.jillegal.core.memory.DirectMemoryServiceFactory;
import tr.com.serkanozal.jillegal.core.util.JvmUtil;

public class ClassRedefineDemo {

	static {
		Jillegal.init();
	}
	
	public static void main(String[] args) throws Exception {
		DirectMemoryService directMemoryService = DirectMemoryServiceFactory.getDirectMemoryService();
		
		SampleClass obj;
		SampleBaseClass baseObj;
		
		///////////////////////////////////////////////////////////////////////////////////////////////
		
		try {
			obj = new SampleClass();
			baseObj = (SampleBaseClass)(Object)obj;
			System.out.println(baseObj);
		}
		catch (ClassCastException e) {
			e.printStackTrace();
		}
		
		///////////////////////////////////////////////////////////////////////////////////////////////

		long addressOfClass = JvmUtil.addressOfClass(SampleClass.class);
		long addressOfBaseClass = JvmUtil.addressOfClass(SampleBaseClass.class);
		
		System.out.println(Long.toHexString(addressOfClass));
		System.out.println(Long.toHexString(addressOfClass >> 3));
		System.out.println(Long.toHexString(addressOfBaseClass));
		System.out.println(Long.toHexString(addressOfBaseClass >> 3));
		System.out.println(JvmUtil.getOptions().getCompressRefShift());
		JvmUtil.dump(addressOfClass, 256);
		JvmUtil.dump(new SampleClass(), 128);
		JvmUtil.dump(new SampleBaseClass(), 128);
		JvmUtil.dump(directMemoryService.getLong(new SampleClass(), 8) << 3, 512);
		
		//directMemoryService.putLong(addressOfClass + 32, addressOfBaseClass); 32 bit Java 6
		//directMemoryService.putLong(addressOfClass + 56, addressOfBaseClass); 64 bit Java 6
		///////////////////////////////////////////////////////////////////////////////////////////////
		
		try {
			obj = new SampleClass();
			baseObj = (SampleBaseClass)(Object)obj;
			System.out.println(baseObj);
		}
		catch (ClassCastException e) {
			e.printStackTrace();
		}
		
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////

	public static class SampleClass extends SampleBaseClass {
		static int i = 100;
	}
	
	public static class SampleBaseClass {
		static int i = 100;
	}
	
}
