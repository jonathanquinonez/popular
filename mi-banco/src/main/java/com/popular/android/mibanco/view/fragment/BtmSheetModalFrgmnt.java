/**
 *  Project: CIBP MBFIS
 *  Company: Evertec
 */
package com.popular.android.mibanco.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.R;

/**
 * BtmSheetModalFrgmnt is a reusable asset meant
 * to create simple but customizable bottom modals with text
 *
 * @author ismael ahumada <ismael.ahumada@evertecinc.com>
 * @see BottomSheetDialogFragment
 * @since Java 1.8
 * @version 1.0
 */
public class BtmSheetModalFrgmnt extends BottomSheetDialogFragment {

    /**
     * Override onCreateView from {@link BottomSheetDialogFragment } so as to being able to customize
     * @param inflater used to inflate resource
     * @param container used for inflation purposes
     * @param savedInstance use for redrawing of modal
     * @return view for this modal fragment
     */
    @Override
    public View onCreateView (LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstance){
        final View v = inflater.inflate(R.layout.bottom_sheet_modal_layout, container, false);//Represents the actual fragment view
        View crossImage = v.findViewById(R.id.cross_image);//Represents the closing cross image
        TextView text;//It is the TextView that contains the main text of the modal

        text = v.findViewById(R.id.main_modal_text);
        text.setText(getArguments().getCharSequence(MiBancoConstants.FOOTER_FULL_TEXT_KEY));
        crossImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick (View v) {
                BtmSheetModalFrgmnt.this.dismiss();
            }
        });
        return v;
    }
}
