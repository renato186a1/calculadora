package com.calculadora.somas;

import android.app.Activity;
import android.os.Bundle;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends Activity {
    private final int BG=Color.rgb(11,16,25), PANEL=Color.rgb(23,31,45),
        PANEL2=Color.rgb(31,43,60), BLUE=Color.rgb(58,177,235),
        WHITE=Color.rgb(241,246,252), MUTED=Color.rgb(154,170,190);
    private final ArrayList<Double> values=new ArrayList<>();
    private LinearLayout listContainer, operationButtons;
    private EditText input;
    private TextView totalText,countText,emptyText,operationHint;
    private double total=0.0;
    private String operation="+";

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        buildScreen();
        updateList();
    }
    private int dp(float v){return (int)(v*getResources().getDisplayMetrics().density+0.5f);}
    private GradientDrawable bg(int color,int radius){
        GradientDrawable d=new GradientDrawable(); d.setColor(color); d.setCornerRadius(dp(radius)); return d;
    }
    private TextView label(String s,int size,int color,boolean bold){
        TextView t=new TextView(this); t.setText(s); t.setTextSize(size); t.setTextColor(color);
        t.setGravity(Gravity.CENTER_VERTICAL);
        t.setTypeface(Typeface.create(bold?"sans-serif-medium":"sans-serif",Typeface.NORMAL)); return t;
    }
    private Button button(String s,int color,int fg){
        Button b=new Button(this); b.setText(s); b.setTextSize(13); b.setAllCaps(false);
        b.setTextColor(fg); b.setTypeface(Typeface.create("sans-serif-medium",Typeface.NORMAL));
        b.setPadding(dp(4),0,dp(4),0); b.setBackground(bg(color,12)); return b;
    }
    private void buildScreen(){
        ScrollView scroll=new ScrollView(this); scroll.setFillViewport(true); scroll.setBackgroundColor(BG); setContentView(scroll);
        LinearLayout page=new LinearLayout(this); page.setOrientation(1); page.setPadding(dp(18),dp(16),dp(18),dp(22)); scroll.addView(page);
        page.addView(label("CALCULADORA • OFFLINE",11,BLUE,true));
        TextView title=label("Calculadora de Operações",24,WHITE,true);
        LinearLayout.LayoutParams tp=new LinearLayout.LayoutParams(-1,dp(42)); tp.topMargin=dp(4); page.addView(title,tp);
        page.addView(label("Escolha a operação e adicione quantos valores quiser.",14,MUTED,false));

        LinearLayout card=new LinearLayout(this); card.setOrientation(1); card.setPadding(dp(18),dp(15),dp(18),dp(17)); card.setBackground(bg(PANEL,20));
        LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(-1,-2); cp.topMargin=dp(20); page.addView(card,cp);
        card.addView(label("RESULTADO",11,MUTED,true));
        totalText=label("0",38,WHITE,true); LinearLayout.LayoutParams rp=new LinearLayout.LayoutParams(-1,dp(58)); rp.topMargin=dp(3); card.addView(totalText,rp);
        countText=label("0 valores adicionados",12,MUTED,false); card.addView(countText);

        TextView opTitle=label("Tipo de operação",16,WHITE,true);
        LinearLayout.LayoutParams otp=new LinearLayout.LayoutParams(-1,dp(38)); otp.topMargin=dp(20); page.addView(opTitle,otp);
        operationButtons=new LinearLayout(this); operationButtons.setOrientation(0);
        page.addView(operationButtons,new LinearLayout.LayoutParams(-1,dp(48)));
        addOperationButton("+","Somar");
        addOperationButton("−","Subtrair");
        addOperationButton("×","Multiplicar");
        addOperationButton("÷","Dividir");
        operationHint=label("Operação selecionada: Soma",12,BLUE,false);
        LinearLayout.LayoutParams ohp=new LinearLayout.LayoutParams(-1,dp(28)); ohp.topMargin=dp(3); page.addView(operationHint,ohp);

        TextView addLabel=label("Adicionar valor",16,WHITE,true);
        LinearLayout.LayoutParams alp=new LinearLayout.LayoutParams(-1,dp(34)); alp.topMargin=dp(12); page.addView(addLabel,alp);
        LinearLayout entry=new LinearLayout(this); entry.setGravity(Gravity.CENTER_VERTICAL);
        page.addView(entry,new LinearLayout.LayoutParams(-1,dp(54)));
        input=new EditText(this); input.setSingleLine(true); input.setTextSize(17); input.setTextColor(WHITE); input.setHintTextColor(MUTED);
        input.setHint("Digite um número"); input.setInputType(8194|4096); input.setPadding(dp(14),0,dp(10),0); input.setBackground(bg(PANEL2,13));
        LinearLayout.LayoutParams ip=new LinearLayout.LayoutParams(0,-1,1); ip.rightMargin=dp(9); entry.addView(input,ip);
        Button add=button("+ Adicionar",BLUE,Color.rgb(5,22,34)); entry.addView(add,new LinearLayout.LayoutParams(dp(125),-1)); add.setOnClickListener(v->addValue());

        LinearLayout actions=new LinearLayout(this); actions.setOrientation(0);
        LinearLayout.LayoutParams ap=new LinearLayout.LayoutParams(-1,dp(44)); ap.topMargin=dp(10); page.addView(actions,ap);
        Button undo=button("Desfazer último",PANEL2,WHITE), clear=button("Limpar tudo",PANEL2,WHITE);
        LinearLayout.LayoutParams h1=new LinearLayout.LayoutParams(0,-1,1); h1.rightMargin=dp(5); actions.addView(undo,h1);
        LinearLayout.LayoutParams h2=new LinearLayout.LayoutParams(0,-1,1); h2.leftMargin=dp(5); actions.addView(clear,h2);
        undo.setOnClickListener(v->removeLast()); clear.setOnClickListener(v->clearAll());

        LinearLayout header=new LinearLayout(this); header.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams hp=new LinearLayout.LayoutParams(-1,dp(42)); hp.topMargin=dp(16); page.addView(header,hp);
        header.addView(label("Valores utilizados",16,WHITE,true),new LinearLayout.LayoutParams(0,-1,1));
        Button copy=button("Copiar resultado",PANEL2,BLUE); header.addView(copy,new LinearLayout.LayoutParams(dp(132),dp(36))); copy.setOnClickListener(v->copyTotal());

        listContainer=new LinearLayout(this); listContainer.setOrientation(1); page.addView(listContainer,new LinearLayout.LayoutParams(-1,-2));
        emptyText=label("Nenhum valor ainda. Digite acima e toque em Adicionar.",13,MUTED,false);
        emptyText.setGravity(Gravity.CENTER); emptyText.setPadding(dp(12),dp(22),dp(12),dp(22)); page.addView(emptyText);
        TextView footer=label("Seus cálculos ficam somente neste aparelho.",11,MUTED,false); footer.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams fp=new LinearLayout.LayoutParams(-1,dp(40)); fp.topMargin=dp(12); page.addView(footer,fp);
        input.setOnEditorActionListener((v,id,event)->{addValue();return true;});
        refreshOperationButtons();
    }
    private void addOperationButton(String symbol,String name){
        Button b=button(symbol,PANEL2,WHITE);
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,-1,1); p.setMargins(dp(3),0,dp(3),0);
        operationButtons.addView(b,p);
        b.setOnClickListener(v->{
            if(!values.isEmpty()){
                new android.app.AlertDialog.Builder(this)
                    .setTitle("Trocar operação?")
                    .setMessage("Os valores atuais serão mantidos e o resultado será recalculado como "+name.toLowerCase()+".")
                    .setNegativeButton("Cancelar",null)
                    .setPositiveButton("Trocar",(d,w)->{operation=symbol;recalculate();refreshOperationButtons();})
                    .show();
            } else { operation=symbol; recalculate(); refreshOperationButtons(); }
        });
    }
    private void refreshOperationButtons(){
        for(int i=0;i<operationButtons.getChildCount();i++){
            Button b=(Button)operationButtons.getChildAt(i);
            boolean selected=b.getText().toString().equals(operation);
            b.setBackground(bg(selected?BLUE:PANEL2,12));
            b.setTextColor(selected?Color.rgb(5,22,34):WHITE);
        }
        String name=operation.equals("+")?"Soma":operation.equals("−")?"Subtração":operation.equals("×")?"Multiplicação":"Divisão";
        operationHint.setText("Operação selecionada: "+name);
    }
    private void addValue(){
        String raw=input.getText().toString().trim().replace(",",".");
        if(raw.isEmpty()){Toast.makeText(this,"Digite um número primeiro.",Toast.LENGTH_SHORT).show();return;}
        try{
            double n=Double.parseDouble(raw); if(Double.isNaN(n)||Double.isInfinite(n))throw new NumberFormatException();
            if(operation.equals("÷") && !values.isEmpty() && n==0.0){
                Toast.makeText(this,"Não é possível dividir por zero.",Toast.LENGTH_LONG).show();return;
            }
            values.add(n); input.setText(""); recalculate(); updateList(); input.requestFocus();
            InputMethodManager imm=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            if(imm!=null)imm.showSoftInput(input,InputMethodManager.SHOW_IMPLICIT);
        }catch(NumberFormatException e){Toast.makeText(this,"Número inválido.",Toast.LENGTH_SHORT).show();}
    }
    private void recalculate(){
        if(values.isEmpty()){total=0.0;if(totalText!=null)totalText.setText(format(total));return;}
        total=values.get(0);
        for(int i=1;i<values.size();i++){
            double n=values.get(i);
            if(operation.equals("+"))total+=n;
            else if(operation.equals("−"))total-=n;
            else if(operation.equals("×"))total*=n;
            else if(operation.equals("÷")){if(n==0){total=Double.NaN;break;}total/=n;}
        }
        if(totalText!=null)totalText.setText(Double.isNaN(total)?"Indefinido":format(total));
    }
    private void removeLast(){
        if(values.isEmpty()){Toast.makeText(this,"Não há valores para desfazer.",Toast.LENGTH_SHORT).show();return;}
        values.remove(values.size()-1);recalculate();updateList();
    }
    private void clearAll(){values.clear();total=0;input.setText("");recalculate();updateList();Toast.makeText(this,"Cálculo limpo.",Toast.LENGTH_SHORT).show();}
    private void copyTotal(){
        if(Double.isNaN(total)){Toast.makeText(this,"Resultado indefinido.",Toast.LENGTH_SHORT).show();return;}
        ClipboardManager cm=(ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
        if(cm!=null){cm.setPrimaryClip(ClipData.newPlainText("Resultado",format(total)));Toast.makeText(this,"Resultado copiado.",Toast.LENGTH_SHORT).show();}
    }
    private void updateList(){
        if(totalText==null)return;
        countText.setText(values.size()+(values.size()==1?" valor adicionado":" valores adicionados"));
        listContainer.removeAllViews();emptyText.setVisibility(values.isEmpty()?View.VISIBLE:View.GONE);
        for(int i=0;i<values.size();i++){
            final int index=i;LinearLayout row=new LinearLayout(this);row.setGravity(Gravity.CENTER_VERTICAL);
            row.setPadding(dp(13),dp(3),dp(8),dp(3));row.setBackground(bg(PANEL,12));
            LinearLayout.LayoutParams rp=new LinearLayout.LayoutParams(-1,dp(48));rp.bottomMargin=dp(7);listContainer.addView(row,rp);
            TextView number=label(String.valueOf(i+1),12,MUTED,true);number.setGravity(Gravity.CENTER);number.setBackground(bg(PANEL2,9));
            row.addView(number,new LinearLayout.LayoutParams(dp(30),dp(30)));
            TextView val=label(format(values.get(i)),16,WHITE,true);val.setPadding(dp(12),0,0,0);row.addView(val,new LinearLayout.LayoutParams(0,-1,1));
            Button del=button("×",PANEL2,MUTED);row.addView(del,new LinearLayout.LayoutParams(dp(38),dp(36)));
            del.setOnClickListener(v->{values.remove(index);recalculate();updateList();});
        }
    }
    private String format(double value){
        if(Math.abs(value)<0.0000000001)value=0;
        DecimalFormatSymbols s=new DecimalFormatSymbols(new Locale("pt","BR"));s.setDecimalSeparator(',');s.setGroupingSeparator('.');
        return new DecimalFormat("#,##0.##########",s).format(value);
    }
}
