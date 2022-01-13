package com.example.textrecognizer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

public class MainActivity extends AppCompatActivity {
    private Button btnSelectImage;
    private Button btnRecognizeText;
    private ImageView img;
    private TextView txtDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img = findViewById(R.id.img);
        btnRecognizeText = findViewById(R.id.btnRecognize);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        txtDisplay = findViewById(R.id.txtdisplay);

        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select image"), 1);
            }
        });

        btnRecognizeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BitmapDrawable bitmap = (BitmapDrawable) img.getDrawable();
                FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap.getBitmap());
                FirebaseVisionTextRecognizer recognize = FirebaseVision.getInstance()
                        .getOnDeviceTextRecognizer();
                txtDisplay.setText(" ");
                recognize.processImage(image).addOnSuccessListener(
                        new OnSuccessListener<FirebaseVisionText>() {
                    @Override
                    public void onSuccess(@NonNull FirebaseVisionText firebaseVisionText) {
                        RecognizeText(firebaseVisionText);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        txtDisplay.setText("Failed to recognize text!!!");
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            if(data != null) {
                img.setImageURI(data.getData());
            }
        }
    }

    private void RecognizeText(FirebaseVisionText resultText){
        if(resultText.getTextBlocks().size() == 0){
            txtDisplay.setText("Data not found!!!");
        }
        for(FirebaseVisionText.TextBlock block: resultText.getTextBlocks()){
            String text = block.getText();
            txtDisplay.setText(txtDisplay.getText() + " " + text);
        }
    }
}