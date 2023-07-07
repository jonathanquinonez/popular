package com.popular.android.mibanco.util;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.popular.android.mibanco.R;

public class AmountEditor extends BottomSheetDialog {

    TextView amount;
    TextView valorUno, valorDos, valorTres, valorCuatro, valorCinco;
    TextView valorSeis, valorSiete, valorOcho, valorNueve, valorCero;
    TextView valorPuntoCero;
    LinearLayout borrar;

    public AmountEditor(@NonNull Context context) {
        super(context);

        super.setContentView(R.layout.botton_sheet_amount_editor);

        initView();
        dismissView();
    }

    public void initView(){
        amount             = this.findViewById(R.id.amount);
        valorUno        = this.findViewById(R.id.valorUno);
        valorDos        = this.findViewById(R.id.valorDos);
        valorTres       = this.findViewById(R.id.valorTres);
        valorCuatro     = this.findViewById(R.id.valorCuatro);
        valorCinco      = this.findViewById(R.id.valorCinco);
        valorSeis       = this.findViewById(R.id.valorSeis);
        valorSiete      = this.findViewById(R.id.valorSiete);
        valorOcho       = this.findViewById(R.id.valorOcho);
        valorNueve      = this.findViewById(R.id.valorNueve);
        valorCero       = this.findViewById(R.id.valorCero);
        valorPuntoCero  = this.findViewById(R.id.valorPuntoCero);
        borrar          = this.findViewById(R.id.borrar);

        setValue();
        delteValue(borrar);
    }

    public void dismissView(){
        this.findViewById(R.id.closeBottonSheet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                dismiss();
            }
        });

        this.findViewById(R.id.btnAmountEditor).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                dismiss();
            }
        });
    }

    public void setValue(){
        setIntanceValue(valorUno, "1");
        setIntanceValue(valorDos, "2");
        setIntanceValue(valorTres, "3");
        setIntanceValue(valorCuatro, "4");
        setIntanceValue(valorCinco, "5");
        setIntanceValue(valorSeis, "6");
        setIntanceValue(valorSiete, "7");
        setIntanceValue(valorOcho, "8");
        setIntanceValue(valorNueve, "9");
        setIntanceValue(valorCero, "0");
        setIntanceValue(valorPuntoCero, ".00");
    }

    public void setIntanceValue(TextView valor, String strValor){
        valor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                addValue(strValor);
            }
        });
    }

    public void delteValue(LinearLayout delete){
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                delValue();
            }
        });
    }

    public void addValue(String strValor){
        String amountStr = amount.getText().toString() + strValor;
        amountStr = amountStr.replaceAll("\\.","");
        amountStr = amountStr.replaceAll(",","");
        amountStr = amountStr.replaceAll("\\$","");

        if (amountStr.length() <= 0 || Integer.parseInt(amountStr) == 0) {
            amountStr = "0";
            amount.setText("");
        }

        if (amountStr == null || amountStr.length() == 0  && amountStr.length() < 2) {
            amountStr = "0";
        }

        int inAmount = Integer.parseInt(amountStr);
        String valorFormateado = Utils.formatAmount(inAmount);
        amount.setText(valorFormateado.replaceAll("\\$",""));
    }


    public void delValue(){
        String amountStr = amount.getText().toString();
        amountStr = amountStr.replaceAll("\\.","");
        amountStr = amountStr.replaceAll(",","");
        amountStr = amountStr.replaceAll("\\$","");

        if (amountStr.length() <= 0 || Integer.parseInt(amountStr) == 0) {
            amountStr = "0";
            amount.setText("");
        }

        if (amountStr == null || amountStr.length() == 0  && amountStr.length() < 2) {
            amountStr = "0";
        }

        amountStr = amountStr.substring(0, amountStr.length() - 1);

        int inAmount = Integer.parseInt(amountStr);
        String valorFormateado = Utils.formatAmount(inAmount);
        amount.setText(valorFormateado.replaceAll("\\$",""));
    }

    public AmountEditor(@NonNull Context context, int theme) {
        super(context, theme);
    }

    protected AmountEditor(@NonNull Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }
}