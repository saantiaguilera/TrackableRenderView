package com.santiago;

import android.content.Context;

import com.santiago.dexer.dex.DexOptions;
import com.santiago.dexer.dex.file.ClassDefItem;
import com.santiago.javassist.CannotCompileException;
import com.santiago.javassist.ClassPool;
import com.santiago.javassist.CtClass;
import com.santiago.javassist.CtField;
import com.santiago.javassist.CtMethod;
import com.santiago.javassist.NotFoundException;
import com.santiago.javassist.android.DexFile;
import com.santiago.javassist.util.HotSwapper;

import java.io.File;
import java.io.IOException;

public class RenderTrackerInjector {

    public static void track(Context context, Class<?> clazz) {
        try {
            ClassPool pool = ClassPool.getDefault(context);
            pool.appendClassPath("com.santiago.rendertracker");
            CtClass viewClass = pool.get(clazz.getName());
            final CtMethod measureMethod = CtMethod.make(
                    "protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) { android.util.Log.w(\"injected\", \"injected\"); }",
                    viewClass);
            viewClass.addMethod(measureMethod);
            final CtMethod onDrawMethod = CtMethod.make(
                    "public void draw(android.graphics.Canvas canvas) { android.util.Log.w(\"injected\", \"injected\"); }",
                    viewClass);
            viewClass.addMethod(onDrawMethod);

//            viewClass.rebuildClassFile();
            viewClass.writeFile(context.getFilesDir().getAbsolutePath());
//
//            HotSwapper hotSwapper = new HotSwapper(8000);
//            hotSwapper.reload(clazz.getName(), viewClass.toBytecode());
//
            DexFile dexFile = new DexFile();

            String path = "/" + clazz.getName().replace('.', '/');
            dexFile.addClass(new File(context.getFilesDir().getAbsolutePath() + path));
            dexFile.writeFile(new File(context.getFilesDir(), "runtimeclasses.dex").getAbsolutePath());
        } catch (NotFoundException | IOException | CannotCompileException e) {
            e.printStackTrace();
        }
    }

}
