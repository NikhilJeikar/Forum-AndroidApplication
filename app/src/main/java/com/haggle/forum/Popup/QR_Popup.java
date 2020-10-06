package com.haggle.forum.Popup;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.haggle.forum.R;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.EnumMap;
import java.util.Map;

public class QR_Popup extends AppCompatDialogFragment {

    private ImageView QR;

    private String UID;
    private String chatID;
    private String ID;
    private String KEY;

    public QR_Popup(String UID, String chatID) {
        this.UID = UID;
        this.chatID = chatID;
    }

    public QR_Popup(String UID, String ID, String KEY) {
        this.UID = UID;
        this.ID = ID;
        this.KEY = KEY;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.popup_qr,null);

        QR = view.findViewById(R.id.QR);
        builder.setView(view);

        StringBuilder text = new StringBuilder();
        text.append("|" + chatID +"||" +UID+"|" );
        QR_Generator(text);

        AlertDialog dialog = builder.show();
        dialog.getWindow().setLayout(350, 350);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        return dialog;
    }


    private void QR_Generator(StringBuilder text){
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            Map<EncodeHintType, Object> hintMap = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
            hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");

            hintMap.put(EncodeHintType.MARGIN, 1); /* default = 4 */

            BitMatrix bitMatrix = multiFormatWriter.encode(text.toString(), BarcodeFormat.QR_CODE, 300, 300,hintMap);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

            QR.setImageBitmap(bitmap);

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
}
