package com.santiago.javassist.android;

import com.santiago.javassist.ClassPath;
import com.santiago.javassist.NotFoundException;
import com.santiago.javassist.bytecode.ClassFile;
import com.santiago.javassist.bytecode.ConstPool;
import com.santiago.javassist.bytecode.FieldInfo;
import com.santiago.javassist.bytecode.MethodInfo;

import java.util.List;

public interface DalvikClassPath extends ClassPath {
	List<FieldInfo> getClassFields(String classname, ConstPool cp);
	List<MethodInfo> getClassMethods(String classname, ConstPool cp);
	ClassFile getClassFile(String classname) throws NotFoundException;
}
