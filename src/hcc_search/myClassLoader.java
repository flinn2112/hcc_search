/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hcc_search;

/**
 *
 * @author frankkempf
 */
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.InvocationTargetException;
import java.util.jar.Attributes;



import java.util.Hashtable;
public class myClassLoader extends ClassLoader {
    int m_iTraceLevel = 0 ;
    public myClassLoader(int iTraceLevel){
        super(myClassLoader.class.getClassLoader());
        m_iTraceLevel = iTraceLevel ;
    }
  
    public  Class loadClass(String className) throws ClassNotFoundException {
         return findClass(className);
    }
 
    public Class findClass(String className){
        byte classByte[];
        Class result = null;
       if(  m_iTraceLevel > 2)
        System.out.println("findClass - try to get " + className) ;
/*
        result = (Class)classes.get(className);
        if(result != null){
           System.out.println("findClass - classes.get succeeded. return") ;
            return result;
        }
        */
       if(  m_iTraceLevel > 2)
            System.out.println("findClass - searching on") ;        
        try{
            result = findSystemClass(className);
            if(  m_iTraceLevel > 2)
                System.out.println("findClass - findSystemClass OK return") ;
            return result ;
        }catch(Exception e){
            if(  m_iTraceLevel > 2)
            System.out.println("findClass exception [" + e.toString()  + "]") ;
        }
       if(  m_iTraceLevel > 2)
        System.out.println("findClass - try to get classPath") ;

        try{
           String classPath =    
                 ((String)ClassLoader.getSystemResource(
                     className.replace('.',
                     File.separatorChar)+".class").getFile()).substring(1);
           System.out.println("CLASSPATH [" + classPath  + "]") ;
           classByte = loadClassData(classPath);
           result = defineClass(className,classByte,0,classByte.length,null);
  //          classes.put(className,result);
            return result;
        }catch(Exception e){
            System.out.println(" findClass: no classdef for  [" + className +   "]") ;
            return null;
        } 
    }
     
   public Object invokeClassMethod(String strName, String strMethod, Object args)
        throws ClassNotFoundException,
               NoSuchMethodException,
               InvocationTargetException
    {   
        Object oRet = null ;
        Class c = findClass(strName);
        Class partypes[] = new Class[1];
        Method cbMethod = null ;
        /*
        Method methlist[] 
              = c.getDeclaredMethods();
            for (int i = 0; i < methlist.length;
               i++) {  
               Method _m = methlist[i];
               System.out.println("name  = " + _m.getName());
               if( _m.getName().equals(strMethod) ){
                   cbMethod = _m ;
                break ;
               }
               
        }
            
            
        if( null == cbMethod){
            return null ;
        }

*/
        if(  m_iTraceLevel > 2)
         System.out.println("found class try getMethod... " + strMethod ) ;        
         cbMethod = c.getMethod(strMethod, 
                               //(Class[])null) ; 
                               new Class[] { args.getClass() }); 
        if(  m_iTraceLevel > 2)
            System.out.println(" setAccessible... " ) ;
        cbMethod.setAccessible(true);
        int mods = cbMethod.getModifiers();
        if ( !Modifier.isStatic(mods) ||   //m.getReturnType() != void.class ||
            !Modifier.isPublic(mods)) {
            throw new NoSuchMethodException(strMethod);
        }  
         
        try {
            if(  m_iTraceLevel > 1)
                System.out.println(" invoking [ " + strName + "]" ) ;
            if( null != args)
               oRet = cbMethod.invoke(null, new Object[] { args }); 
            else
               oRet = cbMethod.invoke(null, null); 
        } catch (IllegalAccessException e) {
            // This should not happen, as we have disabled access checks
            Throwable t = e.getCause() ;
            System.out.println(" invokeClassMethod exception [" + e.toString() +   "]") ;
        }   
        return oRet ;
    } 
 
    private byte[] loadClassData(String className) throws IOException{
 
        File f ;
        f = new File(className);
        int size = (int)f.length();
        byte buf[] = new byte[size];
        FileInputStream fis = new FileInputStream(f);
        DataInputStream dis = new DataInputStream(fis);
        dis.readFully(buf);
        dis.close();
        return buf;
    }
 
    
}
