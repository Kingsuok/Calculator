package com.a655.su.calculator;
/*Some optimization:
a lot of repeated code, can create a function to realize, like get a a number of the key

 */
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Arrays;

import bsh.EvalError;
import bsh.Interpreter;

public class MainActivity extends AppCompatActivity {

    // set variables of views
    private TextView display;
    private Button num0;
    private Button num1;
    private Button num2;
    private Button num3;
    private Button num4;
    private Button num5;
    private Button num6;
    private Button num7;
    private Button num8;
    private Button num9;
    private Button percent;
    private Button multiplication;
    private Button division;
    private Button root;
    private Button substraction;
    private Button mrc;
    private Button clear;
    private Button plus;
    private Button MPlus;
    private Button MSub;
    private Button dot;
    private Button equal;
    private TextView memoryDisplay;

    private boolean isClear = true; // display is clear or not
    private StringBuilder expression = new StringBuilder(); // math expression
    private boolean repetition = false; // continuous zeros = one zero, or + - * ...
    private Button button;
    private boolean rootFlag = false; //是不是连续两次输入开根号
    private boolean percentFlag = false; //是不是连续两次输入%
    private double memoryValue = 0;
    private boolean memoryPlusOrSubAhead = false; // this flage is used to check whether M+ or M- was pressed just moment, if yes, the number that M+ or M- used should be replaced by the new input number.
    private boolean memoryFlage = false; //连续两次mrc，将会清楚mrc，此flag确定是不是连续输入两次mrc
    private boolean memoryInputFirst = false;// 在clear后，如果第一次输入的是mrc，紧接着输入【0-9】，要用【0-9】的数值代替前面输入的mrc，此flag为了确定是不是mrc是在要输入【0-9】之前第一次输入的
    private boolean memoryAhead = false;// 当输入一个mrc后，紧接着在输入【0-9】数值时，要用【0-9】的数值代替前面输入的mrc，此flag为了确定是不是mrc是在要输入【0-9】之前输入的

    DecimalFormat df = new DecimalFormat("0.######"); // set format of calculation, 6位有效数字
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        // find ids of views
        display = (TextView)findViewById(R.id.display);
        num0 = (Button)findViewById(R.id.button_0);
        num1 = (Button)findViewById(R.id.button_1);
        num2 = (Button)findViewById(R.id.button_2);
        num3 = (Button)findViewById(R.id.button_3);
        num4 = (Button)findViewById(R.id.button_4);
        num5 = (Button)findViewById(R.id.button_5);
        num6 = (Button)findViewById(R.id.button_6);
        num7 = (Button)findViewById(R.id.button_7);
        num8 = (Button)findViewById(R.id.button_8);
        num9 = (Button)findViewById(R.id.button_9);
        percent = (Button)findViewById(R.id.button_percent);
        multiplication = (Button)findViewById(R.id.button_multiplication);
        division = (Button)findViewById(R.id.button_division);
        root = (Button)findViewById(R.id.button_root);
        substraction = (Button)findViewById(R.id.button_subtraction);
        mrc = (Button)findViewById(R.id.button_MRC);
        clear = (Button)findViewById(R.id.button_C);
        plus = (Button)findViewById(R.id.button_plus);
        MPlus = (Button)findViewById(R.id.button_M_Plus);
        MSub = (Button)findViewById(R.id.button_M_Sub);
        dot = (Button)findViewById(R.id.button_dot);
        equal = (Button)findViewById(R.id.button_equal);
        memoryDisplay = (TextView)findViewById(R.id.displayMemory);

        //click listener
        ButtonListener buttonListener = new ButtonListener();
        num0.setOnClickListener(buttonListener);
        num1.setOnClickListener(buttonListener);
        num2.setOnClickListener(buttonListener);
        num3.setOnClickListener(buttonListener);
        num4.setOnClickListener(buttonListener);
        num5.setOnClickListener(buttonListener);
        num6.setOnClickListener(buttonListener);
        num7.setOnClickListener(buttonListener);
        num8.setOnClickListener(buttonListener);
        num9.setOnClickListener(buttonListener);
        percent.setOnClickListener(buttonListener);
        multiplication.setOnClickListener(buttonListener);
        division.setOnClickListener(buttonListener);
        root.setOnClickListener(buttonListener);
        substraction.setOnClickListener(buttonListener);
        mrc.setOnClickListener(buttonListener);
        clear.setOnClickListener(buttonListener);
        plus.setOnClickListener(buttonListener);
        MPlus.setOnClickListener(buttonListener);
        MSub.setOnClickListener(buttonListener);
        dot.setOnClickListener(buttonListener);
        equal.setOnClickListener(buttonListener);


    }

    class ButtonListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            // if (memoryValue == 0){

            //}
            if (isClear == true) {
                if (v.getId() == R.id.button_1
                        || v.getId() == R.id.button_2
                        || v.getId() == R.id.button_3
                        || v.getId() == R.id.button_4
                        || v.getId() == R.id.button_5
                        || v.getId() == R.id.button_6
                        || v.getId() == R.id.button_7
                        || v.getId() == R.id.button_8
                        || v.getId() == R.id.button_9
                        || v.getId() == R.id.button_0
                        || v.getId() == R.id.button_subtraction
                        ) {
                    isClear = false;
                    memoryFlage = false;
                    memoryInputFirst = false;
                    memoryAhead = false;
                    rootFlag = false;//&&&&&&&&&&&&&&&&&
                    percentFlag = false;//&&&&&&&&&&&&&
                    button = (Button) v;
                    expression.append(button.getText().toString());
                    display.setText(expression.toString());
                }  else if (v.getId() == R.id.button_dot) {
                    memoryFlage = false;
                    isClear = false;
                    memoryInputFirst = false;
                    memoryAhead = false;
                    rootFlag = false;//&&&&&&&&&&&&&&&&&
                    percentFlag = false;//&&&&&&&&&&&&&
                    button = (Button) v;
                    expression.append("0.");
                    display.setText(expression.toString());
                }else if (v.getId() == R.id.button_MRC){
                    isClear = false;
                    if (memoryFlage == true ){
                        memoryValue = 0;
                        memoryDisplay.setText("");
                        memoryFlage = false;
                    }else{

                        display.setText(df.format(memoryValue));

                        expression.append(df.format(memoryValue));
                        memoryInputFirst = true;
                        memoryFlage = true;
                        rootFlag = false;//&&&&&&&&&&&&&&&&&
                        percentFlag = false;//&&&&&&&&&&&&&
                    }
                }
            } else {
                if (v.getId() == R.id.button_C) {
                    memoryFlage = false;
                    memoryInputFirst = false;
                    memoryAhead = false;
                    rootFlag = false;//&&&&&&&&&&&&&&&&&
                    percentFlag = false;//&&&&&&&&&&&&&
                    expression.setLength(0);
                    display.setText("");
                    isClear = true;
                } else {
                    button = (Button) v;
                    if (expression.length() == 0 ) {
                        if (!display.getText().toString().equals("")){

                            if (display.getText().toString().equals("Illegal") || display.getText().toString().equals("∞")) {


                                if (button.getText().toString().matches("[0-9]")) {
                                    memoryInputFirst = false;
                                    memoryAhead = false;
                                    memoryFlage = false;
                                    rootFlag = false;//&&&&&&&&&&&&&&&&&
                                    percentFlag = false;//&&&&&&&&&&&&&
                                    expression.append(button.getText().toString());
                                    display.setText(expression.toString());
                                }else {
                                    if (v.getId() == R.id.button_MRC){
                                        if (memoryFlage == true ){
                                            memoryValue = 0;
                                            memoryDisplay.setText("");
                                            memoryFlage = false;
                                        }else{
                                            memoryInputFirst = false;
                                            memoryAhead = true;
                                            display.setText(df.format(memoryValue));

                                            expression.append(df.format(memoryValue));

                                            memoryFlage = true;
                                            rootFlag = false;//&&&&&&&&&&&&&&&&&
                                            percentFlag = false;//&&&&&&&&&&&&&
                                        }


                                    }else{
                                        memoryFlage = false;
                                        memoryInputFirst = false;
                                        memoryAhead = false;
                                        rootFlag = false;//&&&&&&&&&&&&&&&&&
                                        percentFlag = false;//&&&&&&&&&&&&&
                                    }
                                }
                            } else {
                                if (Double.parseDouble(display.getText().toString()) < 0 && (v.getId() == R.id.button_root)) {
                                    memoryFlage = false;
                                    memoryInputFirst = false;
                                    memoryAhead = false;
                                    rootFlag = false;//&&&&&&&&&&&&&&&&&
                                    percentFlag = false;//&&&&&&&&&&&&&
                                    expression.setLength(0);
                                    display.setText("Illegal");
                                } else {
                                    if (button.getText().toString().equals("+")) {
                                        memoryInputFirst = false;
                                        memoryAhead = false;
                                        memoryFlage = false;
                                        rootFlag = false;//&&&&&&&&&&&&&&&&&
                                        percentFlag = false;//&&&&&&&&&&&&&
                                        expression.append(display.getText().toString() + "+");
                                        display.setText(expression.toString());
                                    }
                                    if (button.getText().toString().equals("-")) {
                                        memoryInputFirst = false;
                                        memoryAhead = false;
                                        memoryFlage = false;
                                        rootFlag = false;//&&&&&&&&&&&&&&&&&
                                        percentFlag = false;//&&&&&&&&&&&&&
                                        expression.append(display.getText().toString() + "-");
                                        display.setText(expression.toString());
                                    }
                                    if (button.getText().toString().equals("X")) {
                                        memoryInputFirst = false;
                                        memoryAhead = false;
                                        memoryFlage = false;
                                        rootFlag = false;//&&&&&&&&&&&&&&&&&
                                        percentFlag = false;//&&&&&&&&&&&&&
                                        expression.append(display.getText().toString() + "*");
                                        display.setText(expression.toString());
                                    }
                                    if (button.getText().toString().equals("÷")) {
                                        memoryInputFirst = false;
                                        memoryAhead = false;
                                        memoryFlage = false;
                                        rootFlag = false;//&&&&&&&&&&&&&&&&&
                                        percentFlag = false;//&&&&&&&&&&&&&
                                        expression.append(display.getText().toString() + "/");
                                        display.setText(expression.toString());
                                    }
                                    if (button.getText().toString().matches("[0-9]")) {
                                        memoryInputFirst = false;
                                        memoryAhead = false;
                                        memoryFlage = false;
                                        rootFlag = false;//&&&&&&&&&&&&&&&&&
                                        percentFlag = false;//&&&&&&&&&&&&&
                                        expression.append(button.getText().toString());
                                        display.setText(button.getText().toString());
                                    }
                                    if (button.getText().toString().equals(".")) {
                                        memoryInputFirst = false;
                                        memoryAhead = false;
                                        memoryFlage = false;
                                        rootFlag = false;//&&&&&&&&&&&&&&&&&
                                        percentFlag = false;//&&&&&&&&&&&&&
                                        expression.append("0.");
                                        display.setText(expression.toString());
                                    }
                                    if (button.getText().toString().equals("%")) {
                                        memoryInputFirst = false;
                                        memoryAhead = false;
                                        memoryFlage = false;
                                        rootFlag = false;//&&&&&&&&&&&&&&&&&
                                        percentFlag = true;//&&&&&&&&&&&&&
                                        double temp = Double.parseDouble(display.getText().toString()) / 100.0;
                                        display.setText(df.format(temp));

                                        expression.append(display.getText().toString());
                                    }
                                    if (button.getText().toString().equals("√")) {
                                        memoryInputFirst = false;
                                        memoryAhead = false;
                                        rootFlag = true;
                                        memoryFlage = false;
                                        percentFlag = false;//&&&&&&&&&&&&&
                                        double temp = Double.parseDouble(display.getText().toString());

                                        display.setText(df.format(Math.sqrt(temp)));

                                        expression.append(display.getText().toString());
                                    }
                                    if (v.getId() == R.id.button_MRC){

                                        if (memoryFlage == true ){
                                            memoryValue = 0;
                                            memoryDisplay.setText("");
                                            memoryFlage = false;
                                        }else{
                                            memoryInputFirst = false;
                                            memoryAhead = true;
                                            display.setText(df.format(memoryValue));

                                            expression.append(df.format(memoryValue));
                                            memoryFlage = true;
                                            rootFlag = false;//&&&&&&&&&&&&&&&&&
                                            percentFlag = false;//&&&&&&&&&&&&&
                                        }


                                    }
                                    if (v.getId() == R.id.button_M_Plus){
                                        memoryFlage = false;
                                        double temp = Double.parseDouble(display.getText().toString());
                                        if (temp != 0){
                                            memoryValue = memoryValue + temp;
                                            if (memoryValue == 0){
                                                memoryDisplay.setText("");
                                            }else {
                                                memoryDisplay.setText("M");
                                            }
                                        }

                                    }
                                    if (v.getId() == R.id.button_M_Sub){
                                        memoryFlage = false;
                                        double temp = Double.parseDouble(display.getText().toString());
                                        if (temp != 0){
                                            memoryValue = memoryValue - temp;
                                            if (memoryValue == 0){
                                                memoryDisplay.setText("");
                                            }else {
                                                memoryDisplay.setText("M");
                                            }
                                        }

                                    }

                                }
                            }
                        }else{
                            if (button.getText().toString().equals("-")) {
                                memoryInputFirst = false;
                                memoryAhead = false;
                                memoryFlage = false;
                                rootFlag = false;//&&&&&&&&&&&&&&&&&
                                percentFlag = false;//&&&&&&&&&&&&&
                                expression.append(display.getText().toString() + "-");
                                display.setText(expression.toString());
                            }
                            if (button.getText().toString().matches("[0-9]")) {
                                memoryInputFirst = false;
                                memoryAhead = false;
                                memoryFlage = false;
                                rootFlag = false;//&&&&&&&&&&&&&&&&&
                                percentFlag = false;//&&&&&&&&&&&&&
                                expression.append(button.getText().toString());
                                display.setText(button.getText().toString());
                            }
                            if (button.getText().toString().equals(".")) {
                                memoryInputFirst = false;
                                memoryAhead = false;
                                memoryFlage = false;
                                rootFlag = false;//&&&&&&&&&&&&&&&&&
                                percentFlag = false;//&&&&&&&&&&&&&
                                expression.append("0.");
                                display.setText(expression.toString());
                            }
                            if (v.getId() == R.id.button_MRC){
                                if (memoryFlage == true ){
                                    memoryValue = 0;
                                    memoryDisplay.setText("");
                                    memoryFlage = false;
                                    memoryFlage = false;

                                }else{
                                    memoryInputFirst = false;
                                    memoryAhead = true;
                                    display.setText(df.format(memoryValue));

                                    expression.append(df.format(memoryValue));

                                    memoryFlage = true;
                                    rootFlag = false;//&&&&&&&&&&&&&&&&&
                                    percentFlag = false;//&&&&&&&&&&&&&
                                }


                            }
                        }


                    } else {

                        String dis = display.getText().toString();
                        int len = dis.length();

                        String re = dis.replaceAll("[-+/*]", "*");
                        String[] split = re.split("[*]");
                        int size = split.length;
                        if (v.getId() == R.id.button_root) {
                            memoryInputFirst = false;
                            memoryAhead = false;
                            memoryFlage = false;
                            memoryPlusOrSubAhead = false;
                            if (!re.endsWith("*")) {
                                rootFlag = true;
                                if (dis.endsWith(".")) {
                                    split[size - 1] = split[size - 1] + "0";
                                }
                                if (!re.contains("*") ||re.lastIndexOf("*")==0){

                                    double result = Math.sqrt(Double.parseDouble(split[size - 1]));
                                    if(re.lastIndexOf("*")==0){
                                        result = -result;
                                    }
                                    expression.setLength(0);
                                    expression.append(df.format(result));
                                    display.setText(expression.toString());
                                }else if (dis.endsWith(")")){
                                    expression.setLength(0);
                                    String subExp = dis.substring(0,dis.lastIndexOf("("));
                                    String MValue = dis.substring(dis.lastIndexOf("(")+1, dis.length()-1);
                                    Double MValueDou = Double.parseDouble(MValue);
                                    if (MValueDou < 0){
                                        expression.setLength(0);
                                        display.setText("Illegal");

                                    }else{
                                        double result = Math.sqrt(MValueDou);
                                        String newExp = subExp + df.format(result);
                                        expression.append(newExp);
                                        display.setText(expression.toString());
                                    }



                                }else {
                                    double result = Math.sqrt(Double.parseDouble(split[size - 1]));
                                    int lastOperation = re.lastIndexOf("*");

                                    expression.setLength(0);
                                    expression.append(dis.substring(0, lastOperation+1) + df.format(result));
                                    display.setText(expression.toString());
                                }
                            }
                        } else if (v.getId() == R.id.button_percent) {
                            memoryInputFirst = false;
                            memoryAhead = false;
                            memoryFlage = false;
                            memoryPlusOrSubAhead = false;
                            if (!re.endsWith("*")) {
                                percentFlag = true;
                                if (dis.endsWith(".")) {
                                    split[size - 1] = split[size - 1] + "0";
                                }
                                if (!re.contains("*")|| re.lastIndexOf("*")==0){

                                    double result = Double.parseDouble(dis) / 100.0;


                                    expression.setLength(0);
                                    expression.append(df.format(result));
                                    display.setText(expression.toString());
                                }else {
                                    double result = 0;
                                    int lastOperation = 0;
                                    String subExp = null;


                                    if (dis.endsWith(")")){
                                        subExp = dis.substring(0,dis.lastIndexOf("("));
                                        String MValue = dis.substring(dis.lastIndexOf("(")+1, dis.length()-1);

                                        Double MValueDou = Double.parseDouble(MValue);
                                        result= MValueDou / 100.0;
                                        lastOperation = dis.lastIndexOf("(") - 1;
                                    }else{
                                        subExp = dis.substring(0,re.lastIndexOf("*") + 1);
                                        result = Double.parseDouble(split[size - 1]) / 100.0;
                                        lastOperation = re.lastIndexOf("*");
                                    }


                                    if (dis.charAt(lastOperation) == '+' || dis.charAt(lastOperation) == '-'){
                                        if (dis.charAt(lastOperation) == '+'){
                                            result = 1.0 + result;
                                        }else {
                                            result = 1.0 - result;
                                        }

                                        if (subExp.lastIndexOf(")") == subExp.length() - 2){
                                            String MValue = subExp.substring(subExp.lastIndexOf("(")+1, subExp.length()-2);
                                            Double MValueDou = Double.parseDouble(MValue);
                                            result = MValueDou * result;
                                        }else{
                                            if (split[size-2].equals("(") ){
                                                result = Double.parseDouble(split[size-3]) * result;
                                            }else {
                                                result = Double.parseDouble(split[size-2]) * result;
                                            }


                                        }
                                        if (size == 2){
                                            expression.setLength(0);
                                            expression.append(df.format(result));
                                            display.setText(expression.toString());

                                        }else{
                                            expression.setLength(0);
                                            String newExp = null;
                                            if (subExp.lastIndexOf(")") == subExp.length() - 2){
                                                newExp = subExp.substring(0,subExp.lastIndexOf("("))+ "(" + df.format(result) + ")";
                                            }else{
                                                if (split[size-2].equals("(") ){
                                                    if (lastOperation-split[size-3].length() == 0){
                                                        newExp =  df.format(result) ;
                                                    }else {
                                                        newExp = subExp.substring(0,lastOperation-split[size-3].length()) + "("+ df.format(result) + ")";
                                                    }


                                                }else {

                                                    if (lastOperation-split[size-2].length() == 0){
                                                        newExp =  df.format(result) ;
                                                    }else {
                                                        newExp = subExp.substring(0,lastOperation-split[size-2].length()) + "("+ df.format(result) + ")";
                                                    }

                                                }
                                            }
                                            expression.append(newExp);
                                            display.setText(newExp);
                                        }



                                    }else{


                                        if (subExp.lastIndexOf(")") == subExp.length() - 2){
                                            String MValue = subExp.substring(subExp.lastIndexOf("(")+1, subExp.length()-2);
                                            Double MValueDou = Double.parseDouble(MValue);
                                            result = MValueDou * MValueDou * result;
                                        }else{
                                            if (split[size-2].equals("(")){
                                                result = Double.parseDouble(split[size-3]) *Double.parseDouble(split[size-3]) * result;
                                            }else {
                                                result = Double.parseDouble(split[size-2]) *Double.parseDouble(split[size-2]) * result;
                                            }
                                        }

                                        expression.setLength(0);
                                        String newExp = null;
                                        if (subExp.lastIndexOf(")") == subExp.length() - 2){
                                            newExp = subExp.substring(0,subExp.lastIndexOf("("))+ "(" + df.format(result) + ")";
                                        }else{
                                            if (split[size-2].equals("(") ){
                                                if (lastOperation-split[size-3].length() == 0){
                                                    newExp = df.format(result) ;

                                                }else {
                                                    newExp = subExp.substring(0,lastOperation-split[size-3].length()) + "("+ df.format(result) + ")";

                                                }

                                            }else {
                                                if (lastOperation-split[size-2].length() == 0){
                                                    newExp =  df.format(result) ;

                                                }else {
                                                    newExp = subExp.substring(0,lastOperation-split[size-2].length()) + "(" + df.format(result) + ")";

                                                }
                                            }
                                        }
                                        expression.append(newExp);
                                        display.setText(newExp);
                                    }
                                }

                            }
                        } else if (v.getId() == R.id.button_plus) {
                            memoryInputFirst = false;
                            memoryAhead = false;
                            memoryFlage = false;
                            rootFlag = false;
                            percentFlag = false;
                            memoryPlusOrSubAhead = false;
                            if (!re.endsWith("*") && !dis.endsWith(".")) {
                                expression.append("+");
                                display.setText(expression.toString());
                            } else if (dis.endsWith(".")) {
                                String newExp = dis + "0+";
                                display.setText(newExp);
                                expression.setLength(0);
                                expression.append(newExp);
                            } else {
                                if (!dis.endsWith("+")){
                                    String newExp;
                                    if (dis.length() == 1){
                                        newExp = "";
                                    }else {
                                        newExp = dis.substring(0, len - 1) + "+";
                                    }
                                    display.setText(newExp);
                                    expression.setLength(0);
                                    expression.append(newExp);
                                }
                            }
                        } else if (v.getId() == R.id.button_subtraction) {
                            memoryInputFirst = false;
                            memoryAhead = false;
                            memoryFlage = false;
                            rootFlag = false;
                            percentFlag = false;
                            memoryPlusOrSubAhead = false;
                            if (!re.endsWith("*") && !dis.endsWith(".")) {
                                expression.append("-");
                                display.setText(expression.toString());
                            } else if (dis.endsWith(".")) {
                                String newExp = dis + "0-";
                                display.setText(newExp);
                                expression.setLength(0);
                                expression.append(newExp);
                            } else {
                                if (!dis.endsWith("-")){
                                    String newExp = dis.substring(0, len - 1) + "-";
                                    display.setText(newExp);
                                    expression.setLength(0);
                                    expression.append(newExp);
                                }
                            }
                        } else if (v.getId() == R.id.button_multiplication) {
                            memoryInputFirst = false;
                            memoryAhead = false;
                            memoryFlage = false;
                            rootFlag = false;
                            percentFlag = false;
                            memoryPlusOrSubAhead = false;
                            if (!re.endsWith("*") && !dis.endsWith(".")) {
                                expression.append("*");
                                display.setText(expression.toString());
                            } else if (dis.endsWith(".")) {
                                String newExp = dis + "0*";
                                display.setText(newExp);
                                expression.setLength(0);
                                expression.append(newExp);
                            } else {
                                if (!dis.endsWith("*") && dis.length() != 1){
                                    String newExp = dis.substring(0, len - 1) + "*";
                                    display.setText(newExp);
                                    expression.setLength(0);
                                    expression.append(newExp);
                                }
                            }
                        } else if (v.getId() == R.id.button_division ) {
                            memoryInputFirst = false;
                            memoryAhead = false;
                            memoryFlage = false;
                            rootFlag = false;
                            percentFlag = false;
                            memoryPlusOrSubAhead = false;
                            if (!re.endsWith("*") && !dis.endsWith(".")) {
                                expression.append("/");
                                display.setText(expression.toString());
                            } else if (dis.endsWith(".")) {
                                String newExp = dis + "0/";
                                display.setText(newExp);
                                expression.setLength(0);
                                expression.append(newExp);
                            } else {
                                if (!dis.endsWith("/") && dis.length() != 1) {
                                    String newExp = dis.substring(0, len - 1) + "/";
                                    display.setText(newExp);
                                    expression.setLength(0);
                                    expression.append(newExp);
                                }
                            }
                        } else if (v.getId() == R.id.button_equal) {
                            memoryInputFirst = false;
                            memoryAhead = false;
                            memoryFlage = false;
                            memoryPlusOrSubAhead = false;
                            if (!re.endsWith("*")) {
                                if (dis.endsWith(".")) {
                                    expression.append("0");
                                }
                                display.setText(getResult(expression.toString()));
                                expression.setLength(0);
                                rootFlag = false;
                                percentFlag = false;
                            }

                        } else if (v.getId() == R.id.button_dot) {
                            memoryInputFirst = false;
                            memoryAhead = false;
                            memoryFlage = false;
                            if (rootFlag == true || percentFlag == true) {
                                expression.setLength(0);
                                expression.append(button.getText().toString());
                                display.setText(expression.toString());
                            } else if (re.endsWith("*")) {
                                expression.append("0.");
                                display.setText(expression.toString());
                            } else if (!split[size - 1].contains(".")) {
                                expression.append(".");
                                display.setText(expression.toString());
                            }
                            rootFlag = false;
                            percentFlag = false;
                            memoryPlusOrSubAhead = false;
                        } else if(v.getId() == R.id.button_M_Plus){
                            memoryFlage = false;
                            //int location = re.lastIndexOf("*");
                            if (!re.endsWith("*")){
                                if (dis.endsWith(".")) {
                                    split[size - 1] = split[size - 1] + "0";
                                }
                                double result = 0;
                                if (dis.endsWith(")")){
                                    String value = dis.substring(dis.lastIndexOf("(")+1, dis.lastIndexOf(")"));
                                    result = Double.parseDouble(value);
                                } else {
                                    result = Double.parseDouble(split[size - 1]);
                                    if (re.charAt(0) == '*' && size == 2 ){
                                        result = - result;
                                    }
                                }
                                memoryValue = memoryValue + result;
                                memoryPlusOrSubAhead = true;

                                if (memoryValue == 0){
                                    memoryDisplay.setText("");
                                }else {
                                    memoryDisplay.setText("M");
                                }
                            }

                        } else if(v.getId() == R.id.button_M_Sub){
                            memoryFlage = false;
                            if (!re.endsWith("*")){
                                if (dis.endsWith(".")) {
                                    split[size - 1] = split[size - 1] + "0";
                                }
                                double result = 0;
                                if (dis.endsWith(")")){
                                    String value = dis.substring(dis.lastIndexOf("(")+1, dis.lastIndexOf(")"));
                                    result = Double.parseDouble(value);
                                } else {
                                    result = Double.parseDouble(split[size - 1]);
                                    if (re.charAt(0) == '*' && size == 2 ){
                                        result = - result;
                                    }
                                }
                                memoryValue = memoryValue - result;
                                memoryPlusOrSubAhead = true;
                                if (memoryValue == 0){
                                    memoryDisplay.setText("");
                                }else {
                                    memoryDisplay.setText("M");
                                }
                            }
                        } else if(v.getId() == R.id.button_MRC){
                            if (memoryFlage == true){

                                memoryValue = 0;
                                memoryDisplay.setText("");
                                memoryFlage = false;
                            }else {
                                if (re.endsWith("*")){

                                    expression.append("("+ df.format(memoryValue)+")");
                                    display.setText(expression.toString());
                                }else{
                                    if (!re.contains("*")){
                                        expression.setLength(0);
                                        expression.append(df.format(memoryValue));
                                        display.setText(expression.toString());
                                    }else{
                                        String newExpression = null;
                                        if (dis.endsWith(")")) {
                                            newExpression = dis.substring(0,dis.lastIndexOf("(")) +  "("+df.format(memoryValue) + ")";
                                        }else{
                                            newExpression = dis.substring(0,re.lastIndexOf("*")+1)  + "("+df.format(memoryValue) + ")";
                                        }

                                        expression.setLength(0);
                                        expression.append(newExpression);
                                        display.setText(expression.toString());
                                    }
                                }
                                memoryPlusOrSubAhead = false;
                                memoryFlage = true;
                                memoryInputFirst = false;
                                memoryAhead = true;
                                rootFlag = false;
                                percentFlag = false;
                            }
                        }else {
                            memoryFlage = false;
                            if (memoryInputFirst == true ) {
                                expression.setLength(0);
                                expression.append(button.getText().toString());
                                display.setText(expression.toString());
                                memoryInputFirst = false;
                                memoryAhead = false;
                            }else if(memoryAhead == true) {
                                if (!dis.endsWith(")")){
                                    expression.setLength(0);
                                    expression.append(button.getText().toString());
                                    display.setText(expression.toString());
                                }else {
                                    String subExp = dis.substring(0, dis.lastIndexOf("("));
                                    String exp = subExp + button.getText().toString();
                                    expression.setLength(0);
                                    expression.append(exp);
                                    display.setText(expression.toString());
                                }
                                memoryAhead = false;
                                memoryInputFirst = false;

                            }else if (memoryPlusOrSubAhead == true){
                                memoryPlusOrSubAhead = false;
                                if (!re.contains("*")){
                                    expression.setLength(0);
                                    expression.append(button.getText().toString());
                                    display.setText(expression.toString());
                                }else{
                                    String newExpression = null;
                                    if (dis.endsWith(")")) {
                                        newExpression = dis.substring(0,dis.lastIndexOf("(")) + button.getText().toString();
                                    }else{
                                        newExpression = dis.substring(0,re.lastIndexOf("*")+1) + button.getText().toString();
                                    }

                                    expression.setLength(0);
                                    expression.append(newExpression);
                                    display.setText(expression.toString());
                                }
                            }else {
                                memoryInputFirst = false;
                                memoryAhead = false;
                                if (rootFlag == true || percentFlag == true) {
                                    expression.setLength(0);
                                    expression.append(button.getText().toString());
                                    display.setText(expression.toString());
                                } else {
                                    if (dis.endsWith("0") && (dis.length() == 1 || re.charAt(len - 2) == '*')) {
                                        if (v.getId() != R.id.button_0) {
                                            String subExp = dis.substring(0, dis.length()-1);
                                            String exp = subExp + button.getText().toString();
                                            expression.setLength(0);
                                            expression.append(exp);
                                            display.setText(expression.toString());
                                        }
                                    } else {
                                        expression.append(button.getText().toString());
                                        display.setText(expression.toString());
                                    }
                                }
                            }
                            rootFlag = false;
                            percentFlag = false;
                        }
                    }


                }
            }

        }
    }
// 使用bsh进行算数表达式的计算，用到了第三方的类库bsh.Interpreter，
    private String getResult(String expression){
        Interpreter bsh = new Interpreter();
        Number result = null;
        try{
            expression = filterExpression(expression);
            result = (Number)bsh.eval(expression);

        }catch (EvalError e){
            e.printStackTrace();
            isClear = true;
            return "Illegal";

        }

        expression = df.format(result.doubleValue());
        if(expression.endsWith(".0")){
            expression = expression.substring(0,expression.indexOf(".0"));
        }
        return expression;
    }
    //算数表达式的处理，因为是小数的运算，所以把所有的数字，变成实数
    private String filterExpression(String expression){
        String[] target = expression.split("");
        boolean flag = false;
        for (int i = 0; i < target.length; i++ ){
            if(target[i].equals(".")){
                flag = true;
            }
            if (target[i].matches("[-+/*]")&& !target[i-1].equals("(")){
                if (flag == true){
                    flag = false;
                    continue;
                }else{
                    if(target[i-1].equals(")")){
                        target[i - 2] = target[i - 2] + ".0";
                    }else{
                        target[i - 1] = target[i - 1] + ".0";
                    }

                }

            }
            if (i == target.length - 1){
                if (flag == true){
                    flag = false;
                    continue;
                }else{
                    if (target[i].equals(")")){
                        target[i-1] = target[i-1] + ".0";
                    }else {
                        target[i] = target[i] + ".0";
                    }

                }
            }
        }

        return Arrays.toString(target).replaceAll("[\\[\\], ]","");//去除表达式中的[ ]
    }

}
