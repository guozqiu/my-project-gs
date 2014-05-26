package com.gs.biz.classCreator;

import java.io.File;
import java.io.IOException;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.gs.biz.sina.bean.SinaRealTimeData;
import com.gs.common.exception.ClassCreateFailException;
import com.gs.common.util.FileUtils;

public class SinaRealTimeDataModelCreator implements Opcodes {
	private static final String GP_CLASS_PACKAGE = "gpclass";

	@SuppressWarnings("unchecked")
	public static Class<? extends SinaRealTimeData> createClass(String code) {
		String classSimpleName = "RealTimeData"+code;
		String className = new StringBuilder(GP_CLASS_PACKAGE).append(".").append(classSimpleName).toString();
		if(existClass(classSimpleName)){
			try {
				return (Class<? extends SinaRealTimeData>) Class.forName(className);
			} catch (ClassNotFoundException e) {
				throw new ClassCreateFailException(e);
			}
		}
		
		String superName = SinaRealTimeData.class.getName().replace(".", "/");

		ClassWriter cw = new ClassWriter(0);
		MethodVisitor mv;

		
		cw.visit(V1_6, ACC_PUBLIC + ACC_SUPER, GP_CLASS_PACKAGE+"/"+classSimpleName, null, superName, null);

		
		cw.visitSource(classSimpleName+".java", null);

		{
			mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(6, l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKESPECIAL,
					superName, "<init>", "()V");
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLineNumber(7, l1);
			mv.visitInsn(RETURN);
			Label l2 = new Label();
			mv.visitLabel(l2);
			mv.visitLocalVariable("this", "L"+GP_CLASS_PACKAGE+"/"+classSimpleName+";", null,
					l0, l2, 0);
			mv.visitMaxs(1, 1);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(Ljava/lang/String;)V",
					null, null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(10, l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKESPECIAL,
					superName, "<init>",
					"(Ljava/lang/String;)V");
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLineNumber(11, l1);
			mv.visitInsn(RETURN);
			Label l2 = new Label();
			mv.visitLabel(l2);
			mv.visitLocalVariable("this", "L"+GP_CLASS_PACKAGE+"/"+classSimpleName+";", null,
					l0, l2, 0);
			mv.visitLocalVariable("data", "Ljava/lang/String;", null, l0, l2, 1);
			mv.visitMaxs(2, 2);
			mv.visitEnd();
		}
		cw.visitEnd();

		byte[] byteArray = cw.toByteArray();
		
		createClassFile(byteArray, classSimpleName);
		
		try {
			return (Class<? extends SinaRealTimeData>) Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new ClassCreateFailException(e);
		}
		
	}
	
	private static boolean existClass(String className) {
		return !(SinaRealTimeDataModelCreator.class.getResourceAsStream("/"+GP_CLASS_PACKAGE+"/"+className)==null);
	}

	private static void createClassFile(byte[] classContent, String className){
		File parent = new File(SinaRealTimeDataModelCreator.class.getResource("/").getFile()+"/"+GP_CLASS_PACKAGE);
		File clazzFile = new File(parent,className+".class");
		try {
			FileUtils.writeByteArrayToFile(clazzFile, classContent);
		} catch (IOException e) {
			throw new ClassCreateFailException(e);
		}
	}
}