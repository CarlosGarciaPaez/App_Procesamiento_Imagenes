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
import android.os.Build;
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
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Button select,camara,btnGray;
    ImageView imageView;
    Bitmap bitmap;
    Mat mat, matGray;
    int SELECT_CODE = 100, CAMERA_CODE=101;
    private Button colorButton;
    private int selectedColor = 0;
    private Button highlightButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageView);

        if (OpenCVLoader.initDebug()) Log.d("LOADED", "success");
        else Log.d("LOADED", "err");

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.CAMERA},102);
            }
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
        // Obtener el drawable del ImageView
        Drawable drawable = imageView.getDrawable();
        // Convertir el drawable a un bitmap
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        // Convertir la imagen a escala de grises
        Mat matGray = new Mat();
        Utils.bitmapToMat(bitmap, matGray);
        Imgproc.cvtColor(matGray, matGray, Imgproc.COLOR_BGR2GRAY);

        // Convertir la matriz de OpenCV a un bitmap
        Bitmap grayBitmap = Bitmap.createBitmap(matGray.cols(), matGray.rows(), Bitmap.Config.RGB_565);
        Utils.matToBitmap(matGray, grayBitmap);
        imageView.setImageBitmap(grayBitmap);
    }
    public void convertirABinaria(View view){
        // Obtener el drawable del ImageView
        Drawable drawable = imageView.getDrawable();
        // Convertir el drawable a un bitmap
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        // Convertir el bitmap a una matriz de OpenCV en escala de grises
        Mat matGray = new Mat();
        Utils.bitmapToMat(bitmap, matGray);
        Imgproc.cvtColor(matGray, matGray, Imgproc.COLOR_BGR2GRAY);
        // Aplicar umbralización para convertir a una imagen binaria
        Mat binaryImage = new Mat();
        Imgproc.threshold(matGray, binaryImage, 128, 255, Imgproc.THRESH_BINARY);
        // Convertir la matriz de OpenCV a un bitmap
        Bitmap binaryBitmap  = Bitmap.createBitmap(binaryImage.cols(), binaryImage.rows(), Bitmap.Config.RGB_565);
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
        Imgproc.cvtColor(redChannel, rgbRedChannel, Imgproc.COLOR_GRAY2RGB);
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
        Imgproc.cvtColor(greenChannel, rgbGreenChannel, Imgproc.COLOR_GRAY2RGB);
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
        Imgproc.cvtColor(blueChannel, rgbBlueChannel, Imgproc.COLOR_GRAY2RGB);
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
    public void pantalla_2 (View view){
        //setContentView(R.layout.activity_main2);
    }

    public void objetosRed(View view) {

        Mat img=new Mat();
        Utils.bitmapToMat(bitmap, img);

        Mat hsv_img = new Mat();
        Imgproc.cvtColor(img, hsv_img, Imgproc.COLOR_BGR2HSV);

        Scalar min_rojo = new Scalar(0, 100, 100);
        Scalar max_rojo = new Scalar(10, 255, 255);

        Mat mascara_roja = new Mat();
        Core.inRange(hsv_img, min_rojo, max_rojo, mascara_roja);

        Mat objetos_rojos = new Mat();
        Core.bitwise_and(img, img, objetos_rojos, mascara_roja);

        // Ahora puedes hacer lo que desees con los resultados (objetos_rojos, objetos_verdes, objetos_azules)
        // Por ejemplo, convertirlos a Bitmap y ostracism en un ImageView:
        Bitmap resultBitmapRojo = Bitmap.createBitmap(objetos_rojos.cols(), objetos_rojos.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(objetos_rojos, resultBitmapRojo);
        imageView.setImageBitmap(resultBitmapRojo);
    }

    public void objetosGreen(View view) {
        Mat img=new Mat();
        Utils.bitmapToMat(bitmap, img);

        Mat hsv_img = new Mat();
        Imgproc.cvtColor(img, hsv_img, Imgproc.COLOR_BGR2HSV);

        Scalar min_verde = new Scalar(40, 100, 100);
        Scalar max_verde = new Scalar(80, 255, 255);

        Mat mascara_verde = new Mat();
        Core.inRange(hsv_img, min_verde, max_verde, mascara_verde);

        Mat objetos_verdes = new Mat();
        Core.bitwise_and(img, img, objetos_verdes, mascara_verde);

        // Ahora puedes hacer lo que desees con los resultados (objetos_rojos, objetos_verdes, objetos_azules)
        // Por ejemplo, convertirlos a Bitmap y mostrarlos en un ImageView:
        Bitmap resultBitmapVerde = Bitmap.createBitmap(objetos_verdes.cols(), objetos_verdes.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(objetos_verdes, resultBitmapVerde);
        imageView.setImageBitmap(resultBitmapVerde);
    }
    public void objetosBlue(View view) {

        Mat img=new Mat();
        Utils.bitmapToMat(bitmap, img);

        Mat hsv_img = new Mat();
        Imgproc.cvtColor(img, hsv_img, Imgproc.COLOR_BGR2HSV);

        Scalar min_azul = new Scalar(100, 100, 100);
        Scalar max_azul = new Scalar(130, 255, 255);


        Mat mascara_azul = new Mat();
        Core.inRange(hsv_img, min_azul, max_azul, mascara_azul);

        Mat objetos_azules = new Mat();
        Core.bitwise_and(img, img, objetos_azules, mascara_azul);

        // Ahora puedes hacer lo que desees con los resultados (objetos_rojos, objetos_verdes, objetos_azules)
        // Por ejemplo, convertirlos a Bitmap y mostrarlos en un ImageView:
        Bitmap resultBitmapAzul = Bitmap.createBitmap(objetos_azules.cols(), objetos_azules.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(objetos_azules, resultBitmapAzul);
        imageView.setImageBitmap(resultBitmapAzul);
    }
    public void escalarImagen(View view){
        int width = imageView.getWidth();
        int height = imageView.getHeight();
        Drawable drawable = imageView.getDrawable();
        // Convertir el drawable a un bitmap
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

        Mat image = new Mat();

        Utils.bitmapToMat(bitmap, image);

        // Define el tamaño deseado para la imagen escalada
        Size newSize = new Size(width/2, height/2);

        // Crea una matriz de destino para la imagen escalada
        Mat scaledImage = new Mat(newSize, image.type());

        // Redimensiona la imagen utilizando la interpolación INTER_CUBIC
        Imgproc.resize(image, scaledImage, newSize, 25, 1, Imgproc.INTER_CUBIC);
        Bitmap imagenEscalada = Bitmap.createBitmap(scaledImage.cols(), scaledImage.rows(), Bitmap.Config.RGB_565);
        Utils.matToBitmap(scaledImage, imagenEscalada);
        imageView.setImageBitmap(imagenEscalada);

        //imageEscalada = cv2.resize(image,(800,400),interpolation=cv2.INTER_CUBIC)
    }




}