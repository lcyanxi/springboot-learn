//package com.lcyanxi.designPatterns.dynamicProxy.handler;
//
//import java.lang.reflect.Field;
//import jdk.internal.org.objectweb.asm.ClassWriter;
//import jdk.internal.org.objectweb.asm.FieldVisitor;
//import jdk.internal.org.objectweb.asm.MethodVisitor;
//import jdk.internal.org.objectweb.asm.Opcodes;
//
///**
// * @author lichang
// * @date 2020/12/5
// */
//public class AsmBytecodeHandler {
//
//    public static <T> T createAsmBytecodeDynamicProxy(T delegate, Class<?> clazz) throws Exception {
//        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
//
//        String className = clazz.getName();
//
//        String proxyClassName = className + "AsmProxy";
//        String classPath = proxyClassName.replace('.', '/');
//        String interfacePath = className.replace('.', '/');
//
//        classWriter.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC, classPath, null, "java/lang/Object",
//                new String[] { interfacePath });
//
//        MethodVisitor initVisitor = classWriter.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
//        initVisitor.visitCode();
//        initVisitor.visitVarInsn(Opcodes.ALOAD, 0);
//        initVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
//        initVisitor.visitInsn(Opcodes.RETURN);
//        initVisitor.visitMaxs(0, 0);
//        initVisitor.visitEnd();
//
//        FieldVisitor fieldVisitor = classWriter.visitField(Opcodes.ACC_PUBLIC, "delegate", "L" + interfacePath + ";",
//                null, null);
//        fieldVisitor.visitEnd();
//
//        MethodVisitor methodVisitor = classWriter.visitMethod(Opcodes.ACC_PUBLIC, "count", "()I", null, null);
//        methodVisitor.visitCode();
//        methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
//        methodVisitor.visitFieldInsn(Opcodes.GETFIELD, classPath, "delegate", "L" + interfacePath + ";");
//        methodVisitor.visitMethodInsn(Opcodes.INVOKEINTERFACE, interfacePath, "count", "()I");
//        methodVisitor.visitInsn(Opcodes.IRETURN);
//        methodVisitor.visitMaxs(0, 0);
//        methodVisitor.visitEnd();
//
//        classWriter.visitEnd();
//        byte[] code = classWriter.toByteArray();
//        T bytecodeProxy = (T) new ByteArrayClassLoader().getClass(proxyClassName, code).newInstance();
//
//        Field filed = bytecodeProxy.getClass().getField("delegate");
//        filed.set(bytecodeProxy, delegate);
//
//        return bytecodeProxy;
//    }
//
//    private AsmBytecodeHandler() {
//    }
//
//    private static class ByteArrayClassLoader extends ClassLoader {
//
//        public ByteArrayClassLoader() {
//            super(AsmBytecodeHandler.ByteArrayClassLoader.class.getClassLoader());
//        }
//
//        public synchronized Class getClass(String name, byte[] code) {
//            if (name == null) {
//                throw new IllegalArgumentException("");
//            }
//            return defineClass(name, code, 0, code.length);
//        }
//    }
//}
