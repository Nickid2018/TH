package io.github.nickid2018.th.system.dyn;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.nickid2018.th.util.ResourceLocation;
import lombok.Getter;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.objectweb.asm.Opcodes.*;

public class UserDefinedBulletMaker {

    public static final Codec<UserDefinedBulletMaker> CODEC = RecordCodecBuilder.create(app -> app.group(
            ResourceLocation.CODEC.fieldOf("location").forGetter(UserDefinedBulletMaker::getLocation),
            Codec.compoundList(Codec.STRING, Codec.STRING).fieldOf("args").forGetter(UserDefinedBulletMaker::getFields)
    ).apply(app, UserDefinedBulletMaker::new));

    public static final String CTOR_DESC = "(Lio/github/nickid2018/th/system/compute/Playground;" +
            "Lio/github/nickid2018/th/system/bullet/BulletBasicData;Ljava/lang/String;Lorg/joml/Vector2f;)V";
    public static final String SUPER_CLASS = "io/github/nickid2018/th/system/bullet/Bullet";
    public static final String PREFIX = "io/github/nickid2018/th/system/dyn/UserDefinedBullet_";
    private static final AtomicInteger COUNTER = new AtomicInteger(0);

    @Getter
    private final ResourceLocation location;
    @Getter
    private final List<Pair<String, String>> fields;

    @Getter
    private final String className;
    @Getter
    private Class<?> clazz;
    private MethodHandle constructor;

    public UserDefinedBulletMaker(ResourceLocation location, List<Pair<String, String>> fields) {
        this.location = location;
        this.fields = fields;
        className = PREFIX + COUNTER.getAndIncrement();
    }

    public MethodHandle getConstructor() {
        if (constructor == null) {
            constructor = makeClass();
            MethodHandleRepo.putHandle(location, constructor, MethodHandleRepo.BULLET_HANDLE);
        }
        return constructor;
    }

    private MethodHandle makeClass() {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        cw.visit(V17, ACC_PUBLIC + ACC_SUPER + ACC_FINAL,
                className, null, SUPER_CLASS, null);
        cw.visitSource("UserDefinedBullet[" + location.toString() + "]", "Dynamic Generated");
        makeFields(cw);
        makeConstructor(cw);
        makeTick(cw);
        makeCLInit(cw);
        cw.visitEnd();

        byte[] bytes = cw.toByteArray();

        //#if !NO_DEBUG
        File file = new File("debug/" + className + ".class");
        file.getParentFile().mkdirs();
        try (FileOutputStream fos = new FileOutputStream(file)){
            fos.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //#endif

        clazz = CodeUploader.INSTANCE.defineClass(className.replace('/', '.'), bytes);
        Constructor<?> ctor = clazz.getConstructors()[0];
        try {
            return MethodHandles.lookup().unreflectConstructor(ctor);
        } catch (IllegalAccessException e) {
            // Impossible!
            throw new RuntimeException(e);
        }
    }

    private void makeFields(ClassWriter cw) {
        FieldVisitor visitor = cw.visitField(ACC_PUBLIC + ACC_STATIC + ACC_FINAL, CodeUploader.FIELD_SCRIPT_FUNCTION,
                CodeUploader.FIELD_TYPE_SCRIPT_FUNCTION, null, null);
        visitor.visitEnd();
        visitor = cw.visitField(ACC_PUBLIC + ACC_STATIC + ACC_FINAL, CodeUploader.FIELD_SCRIPT_OBJECT,
                CodeUploader.FIELD_TYPE_SCRIPT_OBJECT, null, null);
        visitor.visitEnd();
        for (Pair<String, String> field : fields) {
            visitor = cw.visitField(ACC_PUBLIC, field.getFirst(), field.getSecond(), null, null);
            visitor.visitEnd();
        }
    }

    private void makeConstructor(ClassWriter cw) {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, CodeUploader.CTOR, CTOR_DESC, null, null);
        mv.visitCode();
        // super(Playground, BulletBasicData, String, Vector2f)
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitVarInsn(ALOAD, 3);
        mv.visitVarInsn(ALOAD, 4);
        mv.visitMethodInsn(INVOKESPECIAL, SUPER_CLASS, CodeUploader.CTOR, CTOR_DESC, false);
        // return
        mv.visitInsn(RETURN);
        mv.visitMaxs(5, 5);
        mv.visitEnd();
    }

    private void makeCLInit(ClassWriter cw) {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC,
                CodeUploader.CLINIT, CodeUploader.CLINIT_DESC, null, null);
        mv.visitCode();
        // ResourceLocation location = ResourceLocation.fromString(...)
        mv.visitLdcInsn(location.toString());
        mv.visitMethodInsn(INVOKESTATIC, CodeUploader.RESOURCE_LOCATION,
                CodeUploader.RESOURCE_LOCATION_FROM_STRING, CodeUploader.RESOURCE_LOCATION_FROM_STRING_DESC, false);
        mv.visitVarInsn(ASTORE, 0);
        // scriptObject = ScriptRunner.loadAndCreateObjectMirror(location, "tick")
        mv.visitVarInsn(ALOAD, 0);
        mv.visitLdcInsn(CodeUploader.METHOD_TICK);
        mv.visitMethodInsn(INVOKESTATIC, CodeUploader.SCRIPT_RUNNER,
                CodeUploader.SCRIPT_RUNNER_CREATE_OBJECT, CodeUploader.SCRIPT_RUNNER_CREATE_OBJECT_DESC, false);
        mv.visitFieldInsn(PUTSTATIC, className, CodeUploader.FIELD_SCRIPT_OBJECT, CodeUploader.FIELD_TYPE_SCRIPT_OBJECT);
        // scriptFunction = ScriptRunner.getScriptFunction(location, scriptObject)
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETSTATIC, className, CodeUploader.FIELD_SCRIPT_OBJECT, CodeUploader.FIELD_TYPE_SCRIPT_OBJECT);
        mv.visitMethodInsn(INVOKESTATIC, CodeUploader.SCRIPT_RUNNER,
                CodeUploader.SCRIPT_RUNNER_CREATE_FUNCTION, CodeUploader.SCRIPT_RUNNER_CREATE_FUNCTION_DESC, false);
        mv.visitFieldInsn(PUTSTATIC, className, CodeUploader.FIELD_SCRIPT_FUNCTION, CodeUploader.FIELD_TYPE_SCRIPT_FUNCTION);
        // return
        mv.visitInsn(RETURN);
        mv.visitMaxs(2, 1);
        mv.visitEnd();
    }

    private void makeTick(ClassWriter cw) {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, CodeUploader.METHOD_TICK,
                CodeUploader.METHOD_TICK_DESC, null, null);
        mv.visitCode();

        Label returnLabel = new Label();
        Label tryLabel = new Label();
        Label catchLabel = new Label();

        mv.visitTryCatchBlock(tryLabel, catchLabel, catchLabel, "java/lang/Exception");

        // if (scriptObject == null || scriptFunction == null) return;
        mv.visitFieldInsn(GETSTATIC, className, CodeUploader.FIELD_SCRIPT_OBJECT, CodeUploader.FIELD_TYPE_SCRIPT_OBJECT);
        mv.visitJumpInsn(IFNULL, returnLabel);
        mv.visitFieldInsn(GETSTATIC, className, CodeUploader.FIELD_SCRIPT_FUNCTION, CodeUploader.FIELD_TYPE_SCRIPT_FUNCTION);
        mv.visitJumpInsn(IFNULL, returnLabel);

        // ScriptRunner.runScriptNoInvalidate(object, function, this, tickTime);
        mv.visitLabel(tryLabel);
        mv.visitFieldInsn(GETSTATIC, className, CodeUploader.FIELD_SCRIPT_OBJECT, CodeUploader.FIELD_TYPE_SCRIPT_OBJECT);
        mv.visitFieldInsn(GETSTATIC, className, CodeUploader.FIELD_SCRIPT_FUNCTION, CodeUploader.FIELD_TYPE_SCRIPT_FUNCTION);
        mv.visitInsn(ICONST_2);
        mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
        mv.visitInsn(DUP);
        mv.visitInsn(ICONST_0);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitInsn(AASTORE);
        mv.visitInsn(DUP);
        mv.visitInsn(ICONST_1);
        mv.visitVarInsn(LLOAD, 1);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
        mv.visitInsn(AASTORE);
        mv.visitMethodInsn(INVOKESTATIC, CodeUploader.SCRIPT_RUNNER,
                CodeUploader.SCRIPT_RUNNER_RUN_SCRIPT, CodeUploader.SCRIPT_RUNNER_RUN_SCRIPT_DESC, false);
        mv.visitInsn(POP);
        mv.visitJumpInsn(GOTO, returnLabel);

        // catch (Exception e) ScriptRunner.JS_LOGGER.error("Failed to run Bullet script " + location, e);
        mv.visitLabel(catchLabel);
        mv.visitFrame(F_SAME1, 0, null, 1, new Object[]{"java/lang/Exception"});
        mv.visitVarInsn(ASTORE, 3);
        mv.visitFieldInsn(GETSTATIC, CodeUploader.SCRIPT_RUNNER, CodeUploader.JS_LOGGER, CodeUploader.JS_LOGGER_DESC);
        mv.visitLdcInsn("Failed to run Bullet script " + location);
        mv.visitVarInsn(ALOAD, 3);
        mv.visitMethodInsn(INVOKEINTERFACE, "org/slf4j/Logger", "error",
                "(Ljava/lang/String;Ljava/lang/Throwable;)V", true);

        // super.tick(tickTime);
        mv.visitLabel(returnLabel);
        mv.visitFrame(F_SAME, 0, null, 0, null);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(LLOAD, 1);
        mv.visitMethodInsn(INVOKESPECIAL, SUPER_CLASS, CodeUploader.METHOD_TICK, CodeUploader.METHOD_TICK_DESC, false);
        mv.visitInsn(RETURN);
        mv.visitMaxs(6, 3);
        mv.visitEnd();
    }
}
