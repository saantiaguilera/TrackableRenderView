package com.santiago;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.view.View;

import com.android.dx.Code;
import com.android.dx.DexMaker;
import com.android.dx.Local;
import com.android.dx.MethodId;
import com.android.dx.TypeId;

import java.io.IOException;
import java.lang.reflect.Modifier;

public class RenderTrackerInjector {

    public static void track(Context context, Class<?> clazz) {
        //Create a dexMaker
        DexMaker maker = new DexMaker();

        //Initialize classes we will use
        TypeId<?> viewClass = TypeId.get(clazz);
        TypeId<TrackerUtils> trackerClass = TypeId.get(TrackerUtils.class);
        TypeId<Canvas> canvasClass = TypeId.get(Canvas.class);
        TypeId<View> androidViewClass = TypeId.get(View.class);

        //Add the class to the dex
        maker.declare(viewClass, viewClass.getName() + ".generated", Modifier.PUBLIC, TypeId.OBJECT);

        //Create onMeasure method (or dont if its already)
        MethodId onMeasure = viewClass.getMethod(TypeId.VOID, "onMeasure", TypeId.INT, TypeId.INT);
        Code onMeasureCode = maker.declare(onMeasure, Modifier.PROTECTED);

        Local onMeasureMyLocal = onMeasureCode.getThis(viewClass);

        //Call the tracker in onmeasure
        MethodId<TrackerUtils, Void> onTrackMeasure = trackerClass.getMethod(TypeId.VOID, "onMeasure", androidViewClass);
        onMeasureCode.invokeStatic(onTrackMeasure, null, onMeasureMyLocal);

        //Call the super
        MethodId<?, Void> onMeasureSuper = viewClass.getMethod(TypeId.VOID, "onMeasure", TypeId.INT, TypeId.INT);
        onMeasureCode.invokeSuper(onMeasureSuper, null, onMeasureMyLocal, onMeasureCode.getParameter(0, TypeId.INT), onMeasureCode.getParameter(1, TypeId.INT));

        onMeasureCode.returnVoid();

        //Create dispatchDraw method (or dont if its already)
        MethodId dispatchDrawMethod = viewClass.getMethod(TypeId.VOID, "dispatchDraw", canvasClass);
        Code dispatchDrawMethodCode = maker.declare(dispatchDrawMethod, Modifier.PROTECTED);

        Local dispatchMethodMyLocal = dispatchDrawMethodCode.getThis(viewClass);

        //Call the tracker
        MethodId<TrackerUtils, Void> onTrackDraw = trackerClass.getMethod(TypeId.VOID, "onDraw", androidViewClass);
        dispatchDrawMethodCode.invokeStatic(onTrackDraw, null, dispatchMethodMyLocal);

        //Call the super
        MethodId<?, Void> dispatchDrawMethodSuper = viewClass.getMethod(TypeId.VOID, "dispatchDraw", canvasClass);
        dispatchDrawMethodCode.invokeSuper(dispatchDrawMethodSuper, null, dispatchMethodMyLocal, dispatchDrawMethodCode.getParameter(0, canvasClass));

        dispatchDrawMethodCode.returnVoid();

        //Create draw method (or dont if its already)
        MethodId drawMethod = viewClass.getMethod(TypeId.VOID, "draw", canvasClass);
        Code drawMethodCode = maker.declare(drawMethod, Modifier.PUBLIC);

        Local drawMethodMyLocal = drawMethodCode.getThis(viewClass);

        //Call the tracker
        MethodId<TrackerUtils, Void> onTrackPresent = trackerClass.getMethod(TypeId.VOID, "printRenderingTimes", androidViewClass);
        drawMethodCode.invokeStatic(onTrackPresent, null, drawMethodMyLocal);

        //Call the super
        MethodId<?, Void> drawMethodSuper = viewClass.getMethod(TypeId.VOID, "draw", canvasClass);
        drawMethodCode.invokeSuper(drawMethodSuper, null, drawMethodMyLocal, drawMethodCode.getParameter(0, canvasClass));

        drawMethodCode.returnVoid();

        // Create the dex file.
        try {
            maker.generateAndLoad(clazz.getClassLoader(), context.getDir("dx", Context.MODE_PRIVATE));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.w("", "Finsihed");
    }

}
