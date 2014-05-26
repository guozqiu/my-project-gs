package com.gs.biz.classCreator;

import java.io.File;
import java.io.IOException;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.gs.biz.common.bean.HistoryTradeData;
import com.gs.common.exception.ClassCreateFailException;
import com.gs.common.util.FileUtils;

public class SinaHistoryTradeDataModelCreator implements Opcodes {
	private static final String GP_CLASS_PACKAGE = "gpclass";

	@SuppressWarnings("unchecked")
	public static Class<? extends HistoryTradeData> createClass(String code) {

		String classSimpleName = "HistoryDatash" + code;

		String className = new StringBuilder(GP_CLASS_PACKAGE).append(".")
				.append(classSimpleName).toString();
		if (existClass(classSimpleName)) {
			try {
				return (Class<? extends HistoryTradeData>) Class
						.forName(className);
			} catch (ClassNotFoundException e) {
				throw new ClassCreateFailException(e);
			}
		}
		String superName = HistoryTradeData.class.getName().replace(".", "/");

		ClassWriter cw = new ClassWriter(0);
		MethodVisitor mv;

		cw.visit(V1_6, ACC_PUBLIC + ACC_SUPER, GP_CLASS_PACKAGE + "/"
				+ classSimpleName, null, superName, null);

		cw.visitSource(classSimpleName + ".java", null);

		{
			mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(5, l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKESPECIAL, superName, "<init>", "()V");
			mv.visitInsn(RETURN);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLocalVariable("this", "L" + GP_CLASS_PACKAGE + "/"
					+ classSimpleName + ";", null, l0, l1, 0);
			mv.visitMaxs(1, 1);
			mv.visitEnd();
		}
		cw.visitEnd();

		byte[] byteArray = cw.toByteArray();

		createClassFile(byteArray, classSimpleName);

		try {
			return (Class<? extends HistoryTradeData>) Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new ClassCreateFailException(e);
		}

	}

	private static boolean existClass(String className) {
		return !(SinaHistoryTradeDataModelCreator.class.getResourceAsStream("/"
				+ GP_CLASS_PACKAGE + "/" + className) == null);
	}

	private static void createClassFile(byte[] classContent, String className) {
		File parent = new File(SinaHistoryTradeDataModelCreator.class
				.getResource("/").getFile() + "/" + GP_CLASS_PACKAGE);
		File clazzFile = new File(parent, className + ".class");
		try {
			FileUtils.writeByteArrayToFile(clazzFile, classContent);
		} catch (IOException e) {
			throw new ClassCreateFailException(e);
		}
	}
}