package com.santiago;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;

import com.android.dx.Code;
import com.android.dx.Comparison;
import com.android.dx.DexMaker;
import com.android.dx.FieldId;
import com.android.dx.Label;
import com.android.dx.Local;
import com.android.dx.MethodId;
import com.android.dx.TypeId;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;

public class RenderTrackerInjector {

    private static final String VIEW_PATH = "com.santiago.rendertracker.TestView";

    public static void init(Context context, Class<?> clazz) {
       // try {
            /*
            ClassPool pool = ClassPool.getDefault(context);

            CtClass viewClass = pool.get(VIEW_PATH);
            viewClass.addField(CtField.make("private long initialMeasureTime;", viewClass));
            viewClass.addField(CtField.make("private boolean onMeasureModeWasCalled;", viewClass));

            viewClass.addField(CtField.make("private long initialDispatchDrawTime;", viewClass));
            viewClass.addField(CtField.make("private boolean dispatchDrawWasCalled;", viewClass));

            final CtMethod measureMethod = CtMethod.make(
                    "protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) { super.onMeasure(widthMeasureSpec, heightMeasureSpec); }",
                    viewClass);
            viewClass.addMethod(measureMethod);

            final CtMethod dispatchDrawMethod = CtMethod.make(
                    "protected void dispatchDraw(android.graphics.Canvas canvas) { super.dispatchDraw(canvas); }",
                    viewClass);
            viewClass.addMethod(dispatchDrawMethod);

            final CtMethod onDrawMethod = CtMethod.make(
                    "public void draw(android.graphics.Canvas canvas) { super.draw(canvas); }",
                    viewClass);
            viewClass.addMethod(onDrawMethod);

            measureMethod.insertBefore("initialMeasureTime = System.currentTimeMillis();");
            measureMethod.insertBefore("onMeasureModeWasCalled = true;");

            dispatchDrawMethod.insertBefore("initialDispatchDrawTime = System.currentTimeMillis();");
            dispatchDrawMethod.insertBefore("dispatchDrawWasCalled = true;");

           // onDrawMethod.insertAfter("if (onMeasureModeWasCalled) { android.util.Log.w(getClass().getSimpleName(), \"Time since onMeasure is: \" java.lang.System.currentTimeMillis() - initialMeasureTime); }");
           // onDrawMethod.insertAfter("if (dispatchDrawWasCalled) { android.util.Log.w(getClass().getSimpleName(), \"Time since dispatchDraw is: \" java.lang.System.currentTimeMillis() - initialDispatchDrawWasCalled); }");
            onDrawMethod.insertAfter("onMeasureModeWasCalled = false;");
            onDrawMethod.insertAfter("dispatchDrawWasCalled = false;");

            //viewClass.writeFile();
        } catch (NotFoundException | CannotCompileException e) {
            e.printStackTrace();
        }
        */

        //Create a dexMaker
        DexMaker maker = new DexMaker();

        //Initialize classes we will use
        TypeId<?> viewClass = TypeId.get(clazz);
        TypeId<System> systemClass = TypeId.get(System.class);
        TypeId<Canvas> canvasClass = TypeId.get(Canvas.class);

        maker.declare(viewClass, viewClass.getName(), Modifier.PUBLIC, TypeId.OBJECT);

        //Create class fields
        FieldId<?, Long> dispatchDrawTimeVariable = viewClass.getField(TypeId.LONG, "initialDispatchDrawTime");
        FieldId<?, Long> initialMeasureTimeVariable = viewClass.getField(TypeId.LONG, "initialMeasureTime");
        FieldId<?, Boolean> onMeasureModeWasCalledVariable = viewClass.getField(TypeId.BOOLEAN, "onMeasureModeWasCalled");
        FieldId<?, Boolean> dispatchDrawWasCalledVariable = viewClass.getField(TypeId.BOOLEAN, "dispatchDrawWasCalled");

        //Declare in the dex the class fields
        maker.declare(dispatchDrawTimeVariable, Modifier.PRIVATE | Modifier.TRANSIENT, null);
        maker.declare(onMeasureModeWasCalledVariable, Modifier.PRIVATE | Modifier.TRANSIENT, null);
        maker.declare(initialMeasureTimeVariable, Modifier.PRIVATE | Modifier.TRANSIENT, null);
        maker.declare(dispatchDrawWasCalledVariable, Modifier.PRIVATE | Modifier.TRANSIENT, null);





        //Create onMeasure method (or dont if its already)
        MethodId onMeasure = viewClass.getMethod(TypeId.VOID, "onMeasure", TypeId.INT, TypeId.INT);
        Code onMeasureCode = maker.declare(onMeasure, Modifier.PROTECTED);

        //Create variables onMeasure will be using
        Local<Long> onMeasureLocal = onMeasureCode.newLocal(TypeId.LONG);
        Local<Boolean> onMeasureWasCalledLocal = onMeasureCode.newLocal(TypeId.BOOLEAN);

        //Set the current time and the boolean in on measure
        MethodId<System, Long> onMeasureCurrentTimeMillisMethod = systemClass.getMethod(TypeId.LONG, "currentTimeMillis");
        onMeasureCode.invokeStatic(onMeasureCurrentTimeMillisMethod, onMeasureLocal);
        onMeasureCode.sput(initialMeasureTimeVariable, onMeasureLocal);

        onMeasureCode.loadConstant(onMeasureWasCalledLocal, true);
        onMeasureCode.sput(onMeasureModeWasCalledVariable, onMeasureWasCalledLocal);

        //Call the super
        MethodId<?, Void> onMeasureSuper = TypeId.get(clazz).getMethod(TypeId.VOID, "onMeasure", TypeId.INT, TypeId.INT);
        Local onMeasureMyLocal = onMeasureCode.getThis(TypeId.get(clazz));
        onMeasureCode.invokeSuper(onMeasureSuper, null, onMeasureMyLocal, onMeasureCode.getParameter(0, TypeId.INT), onMeasureCode.getParameter(1, TypeId.INT));

        onMeasureCode.returnVoid();







        //Create dispatchDraw method (or dont if its already)
        MethodId dispatchDrawMethod = viewClass.getMethod(TypeId.VOID, "dispatchDraw", TypeId.get(Canvas.class));
        Code dispatchDrawMethodCode = maker.declare(dispatchDrawMethod, Modifier.PROTECTED);

        //Create variables dispatchDraw will be using
        Local<Long> dispatchDrawTimeLocal = dispatchDrawMethodCode.newLocal(TypeId.LONG);
        Local<Boolean> dispatchDrawWasCalledLocal = dispatchDrawMethodCode.newLocal(TypeId.BOOLEAN);

        //Set the current time and the boolean in dispatchDraw
        MethodId<System, Long> dispatchDrawCurrentTimeMillisMethod = systemClass.getMethod(TypeId.LONG, "currentTimeMillis");
        dispatchDrawMethodCode.invokeStatic(dispatchDrawCurrentTimeMillisMethod, dispatchDrawTimeLocal);
        dispatchDrawMethodCode.sput(dispatchDrawTimeVariable, dispatchDrawTimeLocal);

        dispatchDrawMethodCode.loadConstant(dispatchDrawWasCalledLocal, true);
        dispatchDrawMethodCode.sput(dispatchDrawWasCalledVariable, dispatchDrawWasCalledLocal);

        //Call the super
        MethodId<?, Void> dispatchDrawMethodSuper = TypeId.get(clazz).getMethod(TypeId.VOID, "dispatchDraw", TypeId.get(Canvas.class));
        Local dispatchMethodMyLocal = dispatchDrawMethodCode.getThis(TypeId.get(clazz));
        dispatchDrawMethodCode.invokeSuper(dispatchDrawMethodSuper, null, dispatchMethodMyLocal, dispatchDrawMethodCode.getParameter(0, TypeId.get(Canvas.class)));

        dispatchDrawMethodCode.returnVoid();






/*

        //Create draw method (or dont if its already)
        MethodId drawMethod = viewClass.getMethod(TypeId.VOID, "draw", TypeId.get(Canvas.class));
        Code drawMethodCode = maker.declare(drawMethod, Modifier.PUBLIC);

        Label returnLabel = new Label();
        Label printMeasureLabel = new Label();
        Label printDrawLabel = new Label();

        //Create variables dispatchDraw will be using
        Local<Boolean> trueLocal = drawMethodCode.newLocal(TypeId.BOOLEAN);
        Local<Long> drawOnMeasureLocal = drawMethodCode.newLocal(TypeId.LONG);
        Local<Boolean> drawOnMeasureWasCalledLocal = drawMethodCode.newLocal(TypeId.BOOLEAN);
        Local<Long> drawDispatchDrawTimeLocal = drawMethodCode.newLocal(TypeId.LONG);
        Local<Boolean> drawDispatchDrawWasCalledLocal = drawMethodCode.newLocal(TypeId.BOOLEAN);
        drawMethodCode.loadConstant(trueLocal, true);

        //Call the super
        drawMethodCode.mark(returnLabel);
        MethodId<?, Void> drawMethodSuper = TypeId.get(clazz).getMethod(TypeId.VOID, "draw", TypeId.get(Canvas.class));
        Local drawMethodMyLocal = drawMethodCode.getThis(TypeId.get(clazz));
        drawMethodCode.invokeSuper(drawMethodSuper, null, drawMethodMyLocal, drawMethodCode.getParameter(0, TypeId.get(Canvas.class)));
        drawMethodCode.returnVoid();

        //DO PRINT MEASURE LABEL
        drawMethodCode.mark(printMeasureLabel);

        drawMethodCode.sget(onMeasureModeWasCalledVariable, drawOnMeasureWasCalledLocal);
        drawMethodCode.sget(initialMeasureTimeVariable, drawOnMeasureLocal);
        drawMethodCode.sget(dispatchDrawTimeVariable, drawDispatchDrawTimeLocal);
        drawMethodCode.sget(dispatchDrawWasCalledVariable, drawDispatchDrawWasCalledLocal);

        drawMethodCode.compare(Comparison.EQ, printMeasureLabel, drawOnMeasureWasCalledLocal, trueLocal);
*/

        // Create the dex file and load it.
        try {
            maker.generateAndLoad(RenderTrackerInjector.class.getClassLoader(), context.getDir("dx", Context.MODE_PRIVATE));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.w("", "Finsihed");
    }

}
