package com.mirai.microplasticdetector

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import java.io.File

@Composable
fun ImageCaptureScreen(
    session: AnalysisSession,
    onBack: () -> Unit,
    onContinue: (Uri) -> Unit
) {

    // ------------------------------------------------
    // COLOURS
    // ------------------------------------------------

    val backgroundTop = Color(0xFF031827)
    val backgroundBottom = Color(0xFF051F31)

    val cardColor = Color(0xFF0A1B2B)

    val cyan = Color(0xFF00D9FF)
    val teal = Color(0xFF00EFA3)

    val white = Color.White
    val mutedText = Color(0xFF8EA6B8)

    // ------------------------------------------------
    // STATE
    // ------------------------------------------------

    var selectedImageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    var showCamera by remember {
        mutableStateOf(false)
    }

    var imageCount by remember {
        mutableStateOf(0)
    }

    var imageCapture by remember {
        mutableStateOf<ImageCapture?>(null)
    }

    val context = androidx.compose.ui.platform.LocalContext.current
    val lifecycleOwner =
        androidx.compose.ui.platform.LocalLifecycleOwner.current

    var cameraPermissionGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // ------------------------------------------------
    // CAMERA PERMISSION
    // ------------------------------------------------

    val cameraPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { granted ->

            cameraPermissionGranted = granted

            if (granted) {
                showCamera = true
            }
        }

    // ------------------------------------------------
    // GALLERY
    // ------------------------------------------------

    val galleryLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri ->

            if (uri != null) {

                selectedImageUri = uri
                showCamera = false
                imageCount++
            }
        }

    // ------------------------------------------------
    // CAMERA PROVIDER
    // ------------------------------------------------


    // ------------------------------------------------
    // RELEASE CAMERA
    // ------------------------------------------------

    DisposableEffect(Unit) {

        onDispose {

            try {

                ProcessCameraProvider
                    .getInstance(context)
                    .get()
                    .unbindAll()

            } catch (_: Exception) {
            }
        }
    }

    // ------------------------------------------------
    // BACKGROUND
    // ------------------------------------------------

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        backgroundTop,
                        backgroundBottom
                    )
                )
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 22.dp,
                    vertical = 18.dp
                )
        ) {

            // ------------------------------------------------
            // TOP BAR
            // ------------------------------------------------

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Button(
                    onClick = onBack,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = mutedText
                    ),
                    contentPadding = PaddingValues(
                        horizontal = 4.dp
                    )
                ) {

                    Text(
                        text = "←  PREPARATION",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(
                            Color.White.copy(alpha = 0.035f),
                            CircleShape
                        )
                        .border(
                            1.dp,
                            cyan.copy(alpha = 0.45f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = "03",
                        color = cyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ------------------------------------------------
            // WORKFLOW INDICATOR
            // ------------------------------------------------

            WorkflowProgress(
                currentStage = 3
            )

            Spacer(modifier = Modifier.height(22.dp))

            // ------------------------------------------------
            // HEADER
            // ------------------------------------------------

            Text(
                text = "ANALYSIS WORKFLOW",
                color = cyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "IMAGE CAPTURE",
                color = white,
                fontSize = 27.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Acquire a microscopic image of the prepared sample.",
                color = mutedText,
                fontSize = 13.sp,
                lineHeight = 19.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            // ------------------------------------------------
            // SAMPLE INFO
            // ------------------------------------------------

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        cardColor,
                        RoundedCornerShape(14.dp)
                    )
                    .border(
                        1.dp,
                        Color.White.copy(alpha = 0.06f),
                        RoundedCornerShape(14.dp)
                    )
                    .padding(
                        horizontal = 14.dp,
                        vertical = 11.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            teal,
                            CircleShape
                        )
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = session.sampleId,
                        color = cyan,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text =
                            "${session.source}  •  ${session.volumeMl} mL",
                        color = mutedText,
                        fontSize = 10.sp
                    )
                }

                Text(
                    text = "CAPTURE",
                    color = mutedText,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ------------------------------------------------
            // IMAGE PREVIEW
            // ------------------------------------------------

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(315.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF020A12))
                    .border(
                        1.dp,
                        when {
                            selectedImageUri != null ->
                                teal.copy(alpha = 0.5f)

                            showCamera ->
                                cyan.copy(alpha = 0.55f)

                            else ->
                                cyan.copy(alpha = 0.22f)
                        },
                        RoundedCornerShape(20.dp)
                    )
            ) {

                // ------------------------------------------------
                // CAMERA PREVIEW
                // ------------------------------------------------

                if (showCamera && selectedImageUri == null) {

                    AndroidView(
                        factory = { ctx ->

                            val previewView = PreviewView(ctx)

                            val cameraProviderFuture =
                                ProcessCameraProvider.getInstance(ctx)

                            cameraProviderFuture.addListener({

                                val cameraProvider =
                                    cameraProviderFuture.get()

                                val preview =
                                    Preview.Builder().build()

                                val capture =
                                    ImageCapture.Builder()
                                        .setCaptureMode(
                                            ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY
                                        )
                                        .build()

                                imageCapture = capture

                                preview.setSurfaceProvider(
                                    previewView.surfaceProvider
                                )

                                try {

                                    cameraProvider.unbindAll()

                                    cameraProvider.bindToLifecycle(
                                        lifecycleOwner,
                                        CameraSelector.DEFAULT_BACK_CAMERA,
                                        preview,
                                        capture
                                    )

                                } catch (exception: Exception) {

                                    exception.printStackTrace()
                                }

                            }, ContextCompat.getMainExecutor(ctx))

                            previewView
                        },
                        modifier = Modifier.fillMaxSize()
                    )

                    // Camera label

                    Text(
                        text = "CAMERA • LIVE",
                        color = teal,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(16.dp)
                            .background(
                                Color.Black.copy(alpha = 0.55f),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(
                                horizontal = 10.dp,
                                vertical = 6.dp
                            )
                    )
                }

                // ------------------------------------------------
                // SELECTED IMAGE
                // ------------------------------------------------

                else if (selectedImageUri != null) {

                    AndroidView(
                        factory = { ctx ->

                            android.widget.ImageView(ctx).apply {

                                scaleType =
                                    android.widget.ImageView
                                        .ScaleType.CENTER_CROP
                            }
                        },
                        update = { imageView ->

                            imageView.setImageURI(
                                selectedImageUri
                            )
                        },
                        modifier = Modifier.fillMaxSize()
                    )

                    Text(
                        text = "IMAGE READY",
                        color = Color.Black,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(16.dp)
                            .background(
                                teal,
                                RoundedCornerShape(8.dp)
                            )
                            .padding(
                                horizontal = 10.dp,
                                vertical = 6.dp
                            )
                    )
                }

                // ------------------------------------------------
                // NO IMAGE
                // ------------------------------------------------

                else {

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        horizontalAlignment =
                            Alignment.CenterHorizontally,
                        verticalArrangement =
                            Arrangement.Center
                    ) {

                        Box(
                            modifier = Modifier
                                .size(62.dp)
                                .background(
                                    cyan.copy(alpha = 0.07f),
                                    CircleShape
                                )
                                .border(
                                    1.dp,
                                    cyan.copy(alpha = 0.3f),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {

                            Text(
                                text = "🔬",
                                fontSize = 27.sp
                            )
                        }

                        Spacer(
                            modifier = Modifier.height(13.dp)
                        )

                        Text(
                            text = "SELECT IMAGE SOURCE",
                            color = Color(0xFF6D8798),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.8.sp
                        )

                        Spacer(
                            modifier = Modifier.height(14.dp)
                        )

                        // CAMERA + GALLERY

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement =
                                Arrangement.spacedBy(10.dp)
                        ) {

                            Button(
                                onClick = {

                                    if (cameraPermissionGranted) {

                                        showCamera = true

                                    } else {

                                        cameraPermissionLauncher
                                            .launch(
                                                Manifest.permission.CAMERA
                                            )
                                    }

                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors =
                                    ButtonDefaults.buttonColors(
                                        containerColor = cyan
                                    )
                            ) {

                                Text(
                                    text = "📷  CAMERA",
                                    color = Color.Black,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Button(
                                onClick = {

                                    galleryLauncher.launch(
                                        "image/*"
                                    )

                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors =
                                    ButtonDefaults.buttonColors(
                                        containerColor = teal
                                    )
                            ) {

                                Text(
                                    text = "🖼  GALLERY",
                                    color = Color.Black,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(
                            modifier = Modifier.height(12.dp)
                        )

                        Text(
                            text =
                                "Camera for live acquisition  •  Gallery for existing images",
                            color = mutedText,
                            fontSize = 9.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // ------------------------------------------------
                // CORNER MARKERS
                // ------------------------------------------------

                if (!showCamera && selectedImageUri == null) {

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(18.dp)
                    ) {

                        Text(
                            text = "┌",
                            color = cyan.copy(alpha = 0.7f),
                            fontSize = 25.sp,
                            modifier =
                                Modifier.align(
                                    Alignment.TopStart
                                )
                        )

                        Text(
                            text = "┐",
                            color = cyan.copy(alpha = 0.7f),
                            fontSize = 25.sp,
                            modifier =
                                Modifier.align(
                                    Alignment.TopEnd
                                )
                        )

                        Text(
                            text = "└",
                            color = cyan.copy(alpha = 0.7f),
                            fontSize = 25.sp,
                            modifier =
                                Modifier.align(
                                    Alignment.BottomStart
                                )
                        )

                        Text(
                            text = "┘",
                            color = cyan.copy(alpha = 0.7f),
                            fontSize = 25.sp,
                            modifier =
                                Modifier.align(
                                    Alignment.BottomEnd
                                )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ------------------------------------------------
            // STATUS
            // ------------------------------------------------

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .background(
                                when {
                                    selectedImageUri != null ->
                                        teal

                                    showCamera ->
                                        cyan

                                    else ->
                                        Color(0xFFFFB84D)
                                },
                                CircleShape
                            )
                    )

                    Spacer(
                        modifier = Modifier.width(7.dp)
                    )

                    Text(
                        text = when {
                            selectedImageUri != null ->
                                "IMAGE READY"

                            showCamera ->
                                "CAMERA ACTIVE"

                            else ->
                                "WAITING FOR IMAGE"
                        },
                        color = when {
                            selectedImageUri != null ->
                                teal

                            showCamera ->
                                cyan

                            else ->
                                mutedText
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                Text(
                    text = "CAPTURES  $imageCount",
                    color = cyan,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ------------------------------------------------
            // CAMERA BUTTON
            // ------------------------------------------------

            if (showCamera && selectedImageUri == null) {

                Button(
                    onClick = {

                        val capture =
                            imageCapture ?: return@Button

                        val photoFile =
                            File(
                                context.cacheDir,
                                "microplastic_${System.currentTimeMillis()}.jpg"
                            )

                        val outputOptions =
                            ImageCapture.OutputFileOptions
                                .Builder(photoFile)
                                .build()

                        capture.takePicture(
                            outputOptions,
                            ContextCompat.getMainExecutor(
                                context
                            ),
                            object :
                                ImageCapture.OnImageSavedCallback {

                                override fun onImageSaved(
                                    outputFileResults:
                                    ImageCapture.OutputFileResults
                                ) {

                                    selectedImageUri =
                                        Uri.fromFile(photoFile)

                                    imageCount++
                                    showCamera = false
                                }

                                override fun onError(
                                    exception:
                                    ImageCaptureException
                                ) {

                                    exception.printStackTrace()
                                }
                            }
                        )

                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(59.dp)
                        .shadow(
                            elevation = 12.dp,
                            shape = RoundedCornerShape(17.dp)
                        ),
                    shape = RoundedCornerShape(17.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = cyan
                    )
                ) {

                    Text(
                        text = "CAPTURE MICROSCOPIC IMAGE",
                        color = Color.Black,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        showCamera = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF142634)
                    )
                ) {

                    Text(
                        text = "← BACK TO IMAGE SOURCES",
                        color = white,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // ------------------------------------------------
            // IMAGE READY BUTTONS
            // ------------------------------------------------

            else if (selectedImageUri != null) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.spacedBy(10.dp)
                ) {

                    Button(
                        onClick = {

                            selectedImageUri = null

                        },
                        modifier = Modifier
                            .weight(0.85f)
                            .height(55.dp),
                        shape = RoundedCornerShape(15.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF142634)
                        )
                    ) {

                        Text(
                            text = "RETAKE",
                            color = white,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )
                    }

                    Button(
                        onClick = {
                            selectedImageUri?.let { uri ->
                                onContinue(uri)
                            }
                        },
                        modifier = Modifier
                            .weight(1.15f)
                            .height(55.dp)
                            .shadow(
                                elevation = 10.dp,
                                shape = RoundedCornerShape(15.dp)
                            ),
                        shape = RoundedCornerShape(15.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = teal
                        )
                    ) {

                        Text(
                            text = "USE IMAGE   →",
                            color = Color.Black,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )
                    }
                }
            }
        }
    }
}