/**
 * @author SERKAN OZAL
 *         
 *         E-Mail: <a href="mailto:serkanozal86@hotmail.com">serkanozal86@hotmail.com</a>
 *         GitHub: <a>https://github.com/serkan-ozal</a>
 */

package tr.com.serkanozal.jillegal.demo.classredefine;

import tr.com.serkanozal.jillegal.core.Jillegal;
import tr.com.serkanozal.jillegal.core.memory.DirectMemoryService;
import tr.com.serkanozal.jillegal.core.memory.DirectMemoryServiceFactory;
import tr.com.serkanozal.jillegal.core.util.JvmUtil;

public class ClassRedefineDemo {

	private static final int SUPER_CLASS_POINTER_OFFSET_FOR_32_BIT_JAVA_6 = 32;
	private static final int SUPER_CLASS_POINTER_OFFSET_FOR_64_BIT_JAVA_6 = 56;
	private static final int SUPER_CLASS_POINTER_OFFSET_FOR_32_BIT_JAVA_7 = 36;
	private static final int SUPER_CLASS_POINTER_OFFSET_FOR_64_BIT_JAVA_7 = 64;
	
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
			// Throws "ClassCastException", because "SampleClass" class doesn't extends from "SampleBaseClass" class
			baseObj = (SampleBaseClass)(Object)obj; 
			System.out.println(baseObj);
		}
		catch (ClassCastException e) {
			e.printStackTrace();
		}
		
		///////////////////////////////////////////////////////////////////////////////////////////////

		long addressOfClass = JvmUtil.addressOfClass(SampleClass.class);
		long addressOfBaseClass = JvmUtil.addressOfClass(SampleBaseClass.class);
		
		
		System.out.println("Memory layout of SampleClass at 0x"  + Long.toHexString(addressOfClass) + ":");
		JvmUtil.dump(addressOfClass, 128);
		
		System.out.println();
		
		System.out.println("Memory layout of SampleBaseClass at 0x"  + Long.toHexString(addressOfBaseClass) + ":");
		JvmUtil.dump(addressOfBaseClass, 128);

		int superClassPointerOffset = 0;
		if (JvmUtil.isJavaVersionSupported()) {
			if (JvmUtil.isAddressSizeSupported()) {
				switch (JvmUtil.JAVA_VERSION_INFO) {
					case JAVA_VERSION_1_6:
						switch (JvmUtil.getAddressSize()) {
							case JvmUtil.SIZE_32_BIT:
								superClassPointerOffset = SUPER_CLASS_POINTER_OFFSET_FOR_32_BIT_JAVA_6;
								break;
							case JvmUtil.SIZE_64_BIT:
								superClassPointerOffset = SUPER_CLASS_POINTER_OFFSET_FOR_64_BIT_JAVA_6;
								break;	
						}
						break;
					case JAVA_VERSION_1_7:
						switch (JvmUtil.getAddressSize()) {
							case JvmUtil.SIZE_32_BIT:
								superClassPointerOffset = SUPER_CLASS_POINTER_OFFSET_FOR_32_BIT_JAVA_7;
								break;
							case JvmUtil.SIZE_64_BIT:
								superClassPointerOffset = SUPER_CLASS_POINTER_OFFSET_FOR_64_BIT_JAVA_7;
								break;	
						}
						break;
				}		
			}
			else {
				new AssertionError("Unsupported address size: " + JvmUtil.getAddressSize());
			}
		}
		else {
			new AssertionError("Unsupported java version: " + JvmUtil.JAVA_SPEC_VERSION);
		}
		
		// Set super class pointer of "SampleClass" to "SampleBaseClass" class definition address
		directMemoryService.putLong(addressOfClass + superClassPointerOffset, addressOfBaseClass); 

		///////////////////////////////////////////////////////////////////////////////////////////////
		
		try {
			obj = new SampleClass();
			// Doesn't throw "ClassCastException" as before, 
			// Because now super class pointer of "SampleClass" shows "SampleBaseClass" class definition address
			baseObj = (SampleBaseClass)(Object)obj;
			System.out.println(baseObj);
		}
		catch (ClassCastException e) {
			e.printStackTrace();
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////

	public static class SampleClass {
		
		int i;
		String str;

	}
	
	public static class SampleBaseClass {
		
		char c;
		
	}
	
}
