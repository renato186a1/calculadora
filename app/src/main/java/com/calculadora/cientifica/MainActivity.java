package com.calculadora.cientifica;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends Activity {
    private final int bg = Color.rgb(16,20,29), panel = Color.rgb(27,34,47);
    private final int blue = Color.rgb(55,166,224), text = Color.rgb(239,244,250);
    private TextView display, expression, modeLabel;
    private String current = "0";
    private boolean degrees = true, fresh = true;
    private final ArrayList<String> history = new ArrayList<>();
    private LinearLayout root, keys;
    private final String[][] buttons = {
        {"2nd","deg","sin","cos","tan"},
        {"("," )","√","x²","÷"},
        {"7","8","9","⌫","×"},
        {"4","5","6","AC","−"},
        {"1","2","3","%","+"},
        {"±","0",".","π","="},
        {"log","ln","e","Hist","Conv"}
    };

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        buildUI();
    }

    private int dp(float v) { return (int)(v * getResources().getDisplayMetrics().density + .5f); }
    private TextView label(String s, int size, int color) {
        TextView t = new TextView(this); t.setText(s); t.setTextSize(size); t.setTextColor(color);
        t.setGravity(Gravity.CENTER); t.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        return t;
    }
    private void buildUI() {
        root = new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(14),dp(12),dp(14),dp(12)); root.setBackgroundColor(bg);
        setContentView(root);

        LinearLayout top = new LinearLayout(this); top.setGravity(Gravity.CENTER_VERTICAL);
        TextView title = label("Calculadora Científica",20,text); title.setGravity(Gravity.CENTER_VERTICAL|Gravity.LEFT);
        top.addView(title,new LinearLayout.LayoutParams(0,dp(48),1));
        modeLabel = label("DEG",13,blue); modeLabel.setPadding(dp(12),0,dp(12),0);
        top.addView(modeLabel,new LinearLayout.LayoutParams(dp(54),dp(38)));
        modeLabel.setOnClickListener(v -> { degrees=!degrees; modeLabel.setText(degrees?"DEG":"RAD"); });
        root.addView(top);

        LinearLayout screen = new LinearLayout(this); screen.setOrientation(LinearLayout.VERTICAL);
        screen.setGravity(Gravity.BOTTOM|Gravity.RIGHT); screen.setPadding(dp(16),dp(14),dp(16),dp(14));
        screen.setBackgroundColor(panel);
        LinearLayout.LayoutParams sp = new LinearLayout.LayoutParams(-1,0,1.15f); sp.setMargins(0,dp(10),0,dp(12));
        root.addView(screen,sp);
        expression = label(" ",14,Color.LTGRAY); expression.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
        screen.addView(expression,new LinearLayout.LayoutParams(-1,dp(30)));
        display = label("0",38,text); display.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
        screen.addView(display,new LinearLayout.LayoutParams(-1,dp(68)));

        keys = new LinearLayout(this); keys.setOrientation(LinearLayout.VERTICAL);
        root.addView(keys,new LinearLayout.LayoutParams(-1,0,3.2f));
        for(String[] row:buttons) {
            LinearLayout line = new LinearLayout(this); line.setOrientation(LinearLayout.HORIZONTAL);
            for(String key:row) {
                Button btn = new Button(this); btn.setText(key); btn.setTextSize(15);
                btn.setAllCaps(false); btn.setTextColor(text);
                btn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                    key.equals("=")?blue: (isFunction(key)?Color.rgb(43,62,82):panel)));
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0,0,1);
                p.setMargins(dp(3),dp(3),dp(3),dp(3)); line.addView(btn,p);
                btn.setOnClickListener(v -> press(key));
            }
            keys.addView(line,new LinearLayout.LayoutParams(-1,0,1));
        }
    }
    private boolean isFunction(String s) {
        return "sin cos tan √ x² ÷ × − + log ln e π ( ) deg 2nd Hist Conv % ±".contains(s);
    }
    private void refresh(){ display.setText(current); }
    private void append(String s) {
        if(fresh && "0123456789.".contains(s)) current="";
        fresh=false;
        if(current.equals("0") && s.length()==1 && "0123456789".contains(s)) current="";
        current += s; if(current.isEmpty()) current="0"; refresh();
    }
    private void press(String k) {
        try {
            switch(k) {
                case "AC": current="0"; expression.setText(" "); fresh=true; break;
                case "⌫": current=current.length()>1?current.substring(0,current.length()-1):"0"; break;
                case "=": calculate(); return;
                case "±": current=current.startsWith("-")?current.substring(1):"-"+current; break;
                case "π": append(String.valueOf(Math.PI)); break;
                case "e": append(String.valueOf(Math.E)); break;
                case "deg": degrees=!degrees; modeLabel.setText(degrees?"DEG":"RAD"); break;
                case "Hist": showHistory(); return;
                case "Conv": showConverter(); return;
                case "√": unary(Math::sqrt,"√"); return;
                case "x²": unary(x->x*x,"sqr"); return;
                case "sin": unary(x->Math.sin(angle(x)),"sin"); return;
                case "cos": unary(x->Math.cos(angle(x)),"cos"); return;
                case "tan": unary(x->Math.tan(angle(x)),"tan"); return;
                case "log": unary(Math::log10,"log"); return;
                case "ln": unary(Math::log,"ln"); return;
                case "%": unary(x->x/100.0,"%"); return;
                case "×": append("*"); break;
                case "÷": append("/"); break;
                case "−": append("-"); break;
                default: append(k.trim());
            }
        } catch(Exception e) { current="Erro"; refresh(); fresh=true; }
        refresh();
    }
    private double angle(double x){ return degrees?Math.toRadians(x):x; }
    private interface Fn { double apply(double x); }
    private void unary(Fn fn,String op) {
        double x=Double.parseDouble(current);
        double y=fn.apply(x);
        if(Double.isNaN(y)||Double.isInfinite(y)) throw new ArithmeticException();
        expression.setText(op+"("+current+")");
        current=format(y); refresh(); fresh=true;
        history.add(expression.getText()+" = "+current);
    }
    private String format(double x) {
        if(Math.abs(x)<1e-12) x=0;
        return new DecimalFormat("0.##########").format(x).replace(",",".");
    }
    private void calculate() {
        double result = new Parser(current).parse();
        String old=current; current=format(result);
        expression.setText(old+" ="); refresh(); fresh=true;
        history.add(old+" = "+current);
    }
    private void showHistory() {
        StringBuilder s=new StringBuilder();
        for(int i=history.size()-1;i>=0;i--) s.append(history.get(i)).append("\n");
        new AlertDialog.Builder(this).setTitle("Histórico")
            .setMessage(s.length()==0?"Ainda não há cálculos.":s.toString())
            .setPositiveButton("Fechar",null).setNeutralButton("Limpar",(d,w)->history.clear()).show();
    }
    private void showConverter() {
        String[] types={"Temperatura (°C / °F)","Distância (km / mi)","Peso (kg / lb)","Volume (L / gal)"};
        new AlertDialog.Builder(this).setTitle("Conversor de unidades").setItems(types,(d,which)->converter(which)).show();
    }
    private void converter(int type) {
        EditText input=new EditText(this); input.setInputType(8194); input.setHint("Digite o valor");
        new AlertDialog.Builder(this).setTitle("Valor para converter").setView(input)
            .setPositiveButton("Converter",(d,w)->{
                try {
                    double x=Double.parseDouble(input.getText().toString().replace(",",".")), y;
                    String result;
                    switch(type) {
                        case 0: y=x*9/5+32; result=format(y)+" °F"; break;
                        case 1: y=x*0.621371; result=format(y)+" mi"; break;
                        case 2: y=x*2.20462; result=format(y)+" lb"; break;
                        default: y=x*0.264172; result=format(y)+" gal";
                    }
                    new AlertDialog.Builder(this).setTitle("Resultado").setMessage(result)
                        .setPositiveButton("Usar resultado",(a,b)->{current=format(y);refresh();fresh=true;}).setNegativeButton("Fechar",null).show();
                } catch(Exception ex) { Toast.makeText(this,"Valor inválido",Toast.LENGTH_SHORT).show(); }
            }).setNegativeButton("Cancelar",null).show();
    }

    // Recursive-descent parser: +, -, *, /, parentheses, unary signs and decimals.
    private static class Parser {
        private final String s; private int pos=0;
        Parser(String s){this.s=s.replace(" ","");}
        double parse(){double v=expr(); if(pos<s.length()) throw new RuntimeException("syntax"); return v;}
        double expr(){double v=term(); while(pos<s.length()){char c=s.charAt(pos); if(c=='+'||c=='-'){pos++; double r=term();v=c=='+'?v+r:v-r;}else break;}return v;}
        double term(){double v=factor();while(pos<s.length()){char c=s.charAt(pos);if(c=='*'||c=='/'){pos++;double r=factor();v=c=='*'?v*r:v/r;}else break;}return v;}
        double factor(){
            if(pos<s.length()&&(s.charAt(pos)=='+'||s.charAt(pos)=='-')){char c=s.charAt(pos++);double v=factor();return c=='-'?-v:v;}
            double v;
            if(pos<s.length()&&s.charAt(pos)=='('){pos++;v=expr();if(pos>=s.length()||s.charAt(pos++)!=')')throw new RuntimeException(")");}
            else {int start=pos;while(pos<s.length()&&(Character.isDigit(s.charAt(pos))||s.charAt(pos)=='.'))pos++;if(start==pos)throw new RuntimeException("number");v=Double.parseDouble(s.substring(start,pos));}
            if(Double.isNaN(v)||Double.isInfinite(v))throw new ArithmeticException();
            return v;
        }
    }
}
