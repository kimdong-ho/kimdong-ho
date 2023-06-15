package com.example.controle;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.MappedByteBuffer;

public class InputActivity extends AppCompatActivity {
    private ImageView imageView;
    private TextView textView;
    private Button backButton;
    private Bitmap photoBitmap;
    private Handler handler;
    private Interpreter tflite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        imageView = findViewById(R.id.image_view_input);
        textView = findViewById(R.id.text_view_input);
        backButton = findViewById(R.id.backButton);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            photoBitmap = extras.getParcelable("photo");
            imageView.setImageBitmap(photoBitmap);
        }

        textView.setText("검사중입니다. 잠시만 기다려주십시오.");

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // PhotoItem 객체 생성
                PhotoItem photoItem = new PhotoItem(photoBitmap, System.currentTimeMillis());

                // ListActivity로 PhotoItem 객체 전달
                Intent intent = new Intent(InputActivity.this, ListActivity.class);
                intent.putExtra("photoItem", photoItem);
                startActivity(intent);

                // MainActivity로 전환
                Intent mainIntent = new Intent(InputActivity.this, MainActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mainIntent);
                finish(); // InputActivity 종료
            }
        });
    }

    private void classifyClothing() {
        try {
            // 모델 파일 읽기
            MappedByteBuffer tfliteModel = FileUtil.loadMappedFile(this, "model.tflite");
            tflite = new Interpreter(tfliteModel);

            // 이미지 전처리
            ImageProcessor imageProcessor = new ImageProcessor.Builder()
                    .add(new ResizeOp(28, 28, ResizeOp.ResizeMethod.BILINEAR))
                    .add(new NormalizeOp(0f, 255f))
                    .build();

            TensorImage inputImageBuffer = new TensorImage(DataType.FLOAT32);
            inputImageBuffer.load(photoBitmap);
            inputImageBuffer = imageProcessor.process(inputImageBuffer);

            // 모델 입력 텐서 설정
            TensorBuffer inputBuffer = TensorBuffer.createFixedSize(new int[]{1, 28, 28, 1}, DataType.FLOAT32);
            inputBuffer.loadBuffer(inputImageBuffer.getBuffer());

            // 모델 실행
            float[][] output = new float[1][10];
            tflite.run(inputBuffer.getBuffer(), output);

            // 분류 결과 확인
            int label = getMaxLabel(output[0]);

            // 결과에 따라 다른 액티비티로 전환
            if (label != -1) {
                Intent intent = new Intent(InputActivity.this, CheckActivity.class);
                startActivity(intent);
                finish(); // InputActivity 종료
            } else {
                Intent intent = new Intent(InputActivity.this, WarningActivity.class);
                startActivity(intent);
                finish(); // InputActivity 종료
            }

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "모델 파일을 로드하는 중에 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            finish(); // InputActivity 종료
        }
    }

    private int getMaxLabel(float[] array) {
        int maxIndex = -1;
        float maxValue = Float.MIN_VALUE;
        for (int i = 0; i < array.length; i++) {
            if (array[i] > maxValue) {
                maxValue = array[i];
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Handler 작업 취소 (메모리 누수 방지)
        handler.removeCallbacksAndMessages(null);
        // Interpreter 리소스 해제
        if (tflite != null) {
            tflite.close();
        }
    }
}
