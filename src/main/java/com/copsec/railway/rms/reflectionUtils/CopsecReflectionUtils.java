package com.copsec.railway.rms.reflectionUtils;

import com.esotericsoftware.reflectasm.MethodAccess;

public class CopsecReflectionUtils {

	public static Object getInvoke(Object object,String method,Object...args){

		MethodAccess methodAccess = MethodAccess.get(object.getClass());

		return methodAccess.invoke(object,method,args);
	}

	/**
	 * 设置属性值
	 * @param targert 目标对象
	 * @param methodName 方法
	 * @param args 参数
	 */
	public static void setInvoke(Object targert,String methodName,Object...args){

		MethodAccess access = MethodAccess.get(targert.getClass());

		access.invoke(targert, methodName, args);
	}
}
