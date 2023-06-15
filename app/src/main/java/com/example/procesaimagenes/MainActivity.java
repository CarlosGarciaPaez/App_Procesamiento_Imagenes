package com.example.procesaimagenes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Button select,camara,btnGray;
    ImageView imageView;
    Bitmap bitmap;
    Mat mat, matGray;
    int SELECT_CODE = 100, CAMERA_CODE=101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (OpenCVLoader.initDebug()) Log.d("LOADED", "success");
        else Log.d("LOADED", "error");

        getPermission();

        camara = findViewById(R.id.camara);
        select = findViewById(R.id.select);
        imageView = findViewById(R.id.imageView);

        btnGray = findViewById(R.id.btnConvertToGray);

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,SELECT_CODE);
            }
        });

        camara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,CAMERA_CODE);
            }
        });

        btnGray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                convertirAGris();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==SELECT_CODE && data!=null){
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),data.getData());

                // Mostrar el bitmap rotado en el ImageView
                imageView.setImageBitmap(bitmap);

                mat = new Mat(); //Mat es una estructura de datos utilizada para representar matrices multidimensionales

                // Convertir el bitmap a una matriz de OpenCV
                Utils.bitmapToMat(bitmap,mat);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(requestCode==CAMERA_CODE && data!=null){
            bitmap = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(bitmap);
            
            mat = new Mat();
            Utils.bitmapToMat(bitmap,mat);
        }
    }
    void getPermission(){
        if(checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.CAMERA},102);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==102 && grantResults.length>0){
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED){
                getPermission();
            }
        }
    }
    public void convertirAGris(){
        // Convertir la imagen a escala de grises
        matGray = new Mat();
        Imgproc.cvtColor(mat, matGray, Imgproc.COLOR_BGR2GRAY);

        // Convertir la matriz de OpenCV a un bitmap
        Bitmap grayBitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.RGB_565);
        Utils.matToBitmap(matGray, grayBitmap);

        imageView.setImageBitmap(grayBitmap);
    }
    public void convertirABinaria(View view){
        // Convertir la imagen a escala de grises
        Mat matGray = new Mat();
        Imgproc.cvtColor(mat, matGray, Imgproc.COLOR_BGR2GRAY);

        // Aplicar umbralización para convertir a una imagen binaria
        Mat binaryImage = new Mat();
        Imgproc.threshold(matGray, binaryImage, 128, 255, Imgproc.THRESH_BINARY);

        // Convertir la matriz de OpenCV a un bitmap
        Bitmap binaryBitmap  = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.RGB_565);
        Utils.matToBitmap(binaryImage, binaryBitmap );

        imageView.setImageBitmap(binaryBitmap);
    }
    public void canalRojo(View view){
        // Convertir el bitmap a una matriz de OpenCV
        Mat image = new Mat();
        Utils.bitmapToMat(bitmap, image);
        Mat redChannel = new Mat();

        Core.extractChannel(image, redChannel, 0);
        // Convertir los canales a formato RGB para mostrarlos en los ImageViews
        Mat rgbRedChannel = new Mat();
        Imgproc.cvtColor(redChannel, rgbRedChannel, Imgproc.COLOR_BGR2RGB);
        Bitmap redBitmap = Bitmap.createBitmap(rgbRedChannel.cols(), rgbRedChannel.rows(), Bitmap.Config.RGB_565);
        Utils.matToBitmap(rgbRedChannel, redBitmap);
        imageView.setImageBitmap(redBitmap);
    }
    public void canalVerde(View view){
        // Convertir el bitmap a una matriz de OpenCV
        Mat image = new Mat();
        Utils.bitmapToMat(bitmap, image);
        Mat greenChannel = new Mat();

        Core.extractChannel(image, greenChannel, 1);
        // Convertir los canales a formato RGB para mostrarlos en los ImageViews
        Mat rgbGreenChannel = new Mat();
        Imgproc.cvtColor(greenChannel, rgbGreenChannel, Imgproc.COLOR_BGR2RGB);
        Bitmap greenBitmap = Bitmap.createBitmap(rgbGreenChannel.cols(), rgbGreenChannel.rows(), Bitmap.Config.RGB_565);
        Utils.matToBitmap(rgbGreenChannel, greenBitmap);
        imageView.setImageBitmap(greenBitmap);
    }

    public void canalAzul(View view){
        // Convertir el bitmap a una matriz de OpenCV
        Mat image = new Mat();
        Utils.bitmapToMat(bitmap, image);
        Mat blueChannel = new Mat();

        Core.extractChannel(image, blueChannel, 2);
        // Convertir los canales a formato RGB para mostrarlos en los ImageViews
        Mat rgbBlueChannel = new Mat();
        Imgproc.cvtColor(blueChannel, rgbBlueChannel, Imgproc.COLOR_BGR2RGB);
        Bitmap blueBitmap = Bitmap.createBitmap(rgbBlueChannel.cols(), rgbBlueChannel.rows(), Bitmap.Config.RGB_565);
        Utils.matToBitmap(rgbBlueChannel, blueBitmap);
        imageView.setImageBitmap(blueBitmap);
    }
    public void detectarContornos(View view){

        // Convertir el bitmap a una matriz de OpenCV
        Mat image = new Mat();
        Utils.bitmapToMat(bitmap, image);

        // Convertir la imagen a escala de grises
        Mat grayImage = new Mat();
        Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);

        // Aplicar un umbral para obtener una imagen binaria
        Mat binaryImage = new Mat();
        Imgproc.threshold(grayImage, binaryImage, 0, 255, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);

        // Encontrar los contornos en la imagen binaria
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(binaryImage, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        // Dibujar los contornos en la imagen original
        Mat resultImage = image.clone();
        Imgproc.drawContours(resultImage, contours, -1, new Scalar(0, 255, 0), 2);

        // Mostrar la imagen con los contornos
        Bitmap resultBitmap = Bitmap.createBitmap(resultImage.cols(), resultImage.rows(), Bitmap.Config.RGB_565);
        Utils.matToBitmap(resultImage, resultBitmap);
        imageView.setImageBitmap(resultBitmap);
    }
    public void rotarImagen(View view) {

        // Especifica el ángulo de rotación deseado (en grados)
        float angulo = 90.0f;

        Matrix matrix = new Matrix();
        matrix.postRotate(angulo);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        // Muestra el Bitmap rotado en el ImageView
        imageView.setImageBitmap(bitmap);
    }

}