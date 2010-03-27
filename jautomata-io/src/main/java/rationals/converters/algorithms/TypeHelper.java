package rationals.converters.algorithms;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * A Helper class for converting string parameters to primitive
 * types
 *
 * @author Arnaud Bailly
 */
public class TypeHelper {

    /** hash table to store conversion constructors */
    private static java.util.Map constructormap = new java.util.HashMap();
    /** hash table to store conversion methods */
    private static java.util.Map conversionmap = new java.util.HashMap();
    
    /** static initilizer 
	put constructors for base types
    */
    static {
	Class cls = Integer.class;
	try {
	    Class[] ctorparam = new Class[] {java.lang.String.class};
	    // type int
	    cls = Integer.class;
	    Constructor ctor = cls.getConstructor(ctorparam);
	    constructormap.put(int.class,ctor);
	    // type float
	    cls = Float.class;
	    ctor =  cls.getConstructor(ctorparam);
	    constructormap.put(float.class,ctor);
	    // type boolean
	    cls = Boolean.class;
	    ctor =  cls.getConstructor(ctorparam);
	    constructormap.put(boolean.class,ctor);
	    // type double
	    cls = Double.class;
	    ctor =  cls.getConstructor(ctorparam);
	    constructormap.put(double.class,ctor);
	    // type short
	    cls = Short.class;
	    ctor =  cls.getConstructor(ctorparam);
	    constructormap.put(short.class,ctor);
	    // type byte
	    cls = Byte.class;
	    ctor =  cls.getConstructor(ctorparam);
	    constructormap.put(byte.class,ctor);
	} catch(Exception ex) {
	    System.err.println("Unable to get constructor for class "+cls.getName() +" : "+ex.getMessage());
	}
    }
    
    /**
     * A method to register a factory for a type
     *
     * @param cls a class object for which we gives a factory
     * @param method a method object to invoke for constructing objects. This method must
     * be static, takes one String parameter and returns objects of class cls
     * @exception IllegalArgumentException if method or cls are invalid (null, not static...)
     */
    public static void registerFactory(Class cls,Method method)
    {
	Class[] clsparms = new Class[] {java.lang.String.class};
	Class retcls = method.getReturnType();
	int mod = method.getModifiers();
	Class[] parms = method.getParameterTypes();
	if(!retcls.equals(cls) ||
	   !Modifier.isStatic(mod) ||
	   !Modifier.isPublic(mod) ||
	   Modifier.isAbstract(mod) ||
	   !java.util.Arrays.equals(parms,clsparms))
	    throw new IllegalArgumentException ("Invalid argument to method TypeHelper.registerFactory");
	conversionmap.put(cls,method);
    }

    /**
     * Main method to convert from a string given a class object
     *
     * @param cls a Class object
     * @param str a String to parse into an object of given class
     */
    public static Object convert(Class cls,String str)
    {
	Class[] clsparms = new Class[] {java.lang.String.class};
	// first look into hashtables
	Constructor ctor = (Constructor)constructormap.get(cls);
	if(ctor != null)
	    return invokeCtor(ctor,str);
	Method meth = (Method)conversionmap.get(cls);
	if(meth != null)
	    return invokeMethod(meth,str);
	// try to find a suitable constructor
	try {
	    ctor = cls.getConstructor(clsparms);
	    // store in hashtable
	    constructormap.put(cls,ctor);
	    return invokeCtor(ctor,str);
	}catch(Exception ex) {
	    System.err.println("No constructor with String argument for class "+cls.getName() +" : " +ex.getMessage());
	}
	// try to find a suitable method
	try {
	    Method[] methods = cls.getMethods();
	    // try to find a static method taking one string parameter and returning an object of class cls
	    for(int i = 0;i<methods.length;i++) {
		Class retcls = methods[i].getReturnType();
		int mod = methods[i].getModifiers();
		Class[] parms = methods[i].getParameterTypes();
		if(!retcls.equals(cls) ||
		   !Modifier.isStatic(mod) ||
		   !java.util.Arrays.equals(parms,clsparms))
		    continue;
		// found a method - hope it is OK !!!
		conversionmap.put(cls,methods[i]);
		return invokeMethod(methods[i],str);
	    }
	}catch(Throwable t) {
	    System.err.println("Error in enumerating methods for class "+cls.getName() +" : "+t.getMessage());
	} 		    
	// default to null
	System.err.println("No conversion method found for class "+cls.getName());
	return null;
    }
    
    private static Object invokeCtor(Constructor ctor,String str)
    {
	try {
	    return ctor.newInstance(new Object[]{str});
	}catch(Throwable t) {
	    System.err.println("Error in constructor invocation with argument "+str+" : "+t.getMessage());
	    return null;
	}
    }

    private static Object invokeMethod(Method meth,String str)
    {
	try {
	    // assume method is static
	    return meth.invoke(null,new Object[]{str});
	}catch(Throwable t) {
	    System.err.println("Error in method invocation with argument "+str+" : "+t.getMessage());
	    return null;
	}
    }
}
	


	
