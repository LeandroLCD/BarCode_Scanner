# 📱 Barcode Scanner Library
[![](https://jitpack.io/v/LeandroLCD/BarCode_Scanner.svg)](https://jitpack.io/#LeandroLCD/BarCode_Scanner)
![GitHub release (latest by date)](https://img.shields.io/github/v/release/LeandroLCD/BarCode_Scanner)
![GitHub last commit](https://img.shields.io/github/last-commit/LeandroLCD/BarCode_Scanner)
![GitHub issues](https://img.shields.io/github/issues/LeandroLCD/BarCode_Scanner)
![Tests](https://img.shields.io/badge/tests-passing-brightgreen)

Una librería modular para **escaneo de códigos de barras en Android**, diseñada con **arquitectura limpia**, **corutinas** y **Jetpack Compose**.  
Proporciona utilidades para **captura de cámara**, **detección de códigos**, **toma de fotos** y **gestión de permisos**.

---

## 🚀 Características principales

- 📷 Escaneo en tiempo real de códigos de barras desde la cámara.
- 🧩 Inyección flexible mediante interfaces (`UseCase`).
- ⚙️ Gestión automatizada de permisos (cámara, almacenamiento, etc).
- 🧠 Soporte para escaneo desde `Bitmap`, `ImageProxy` o `Uri`.
- 🧱 Totalmente integrable en proyectos **Compose**.
- 🪶 Ligero y sin dependencias externas pesadas.

---

## 🧩 Instalación

Agrega la dependencia a tu archivo **`libs.versions.toml`**:
**Nota:** Esta Librería requiere tener inicializador Dagger Hilt como inyector de dependencias.

```toml
[versions]
barcodeVersion = "1.0.0" # Reemplaza con la versión más reciente

[libraries]
barcode_scanner = { module = "com.github.LeandroLCD:BarCode_Scanner", version.ref = "barcodeVersion" }
```

Y en tu **`build.gradle` (app)**:

```gradle
dependencies {
    implementation(libs.barcode_scanner)
}
```

---

## 💡 Uso básico con `ScannerActivity`

### 1️⃣ Configura el lanzador

```kotlin
val scanActivity = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.StartActivityForResult()
) { onResult ->
    if (onResult.resultCode == RESULT_OK) {
        onResult.data?.also {
            val barcode = it.getStringExtra(EXTRA_BARCODE)
            val barcodeType = it.getStringExtra(EXTRA_BARCODE_TYPE)
            Log.d("scanActivity", "Código escaneado: $barcode, Tipo: $barcodeType")
        }
    } else {
        Log.d("scanActivity", "Error: ${onResult.data?.getStringExtra(EXTRA_ERROR)}")
    }
}
```

### 2️⃣ Lanza la actividad

```kotlin
scope.launch {
     val sendIntent = Intent(ACTION_BARCODE_SCAN)
     scanActivity.launch(sendIntent)
}
```

---

## 🧠 Casos de uso expuestos

La librería está construida bajo el principio de **Clean Architecture**, ofreciendo **Use Cases desacoplados** para mayor flexibilidad.

---

### 🔍 `IScanningBarcodeUseCase`

Permite escanear códigos de barras desde distintas fuentes: cámara, imágenes locales o `Bitmap`.

```kotlin
interface IScanningBarcodeUseCase {
    suspend operator fun invoke(imageProxy: ImageProxy): Result<Barcode?>
    suspend operator fun invoke(url: Uri): Result<Barcode?>
    suspend operator fun invoke(bitmap: Bitmap, rotationDegrees: Int): Result<Barcode?>
}
```

#### Ejemplo de uso:

```kotlin
val scanningUseCase: IScanningBarcodeUseCase = ...
val result = scanningUseCase(imageProxy)
result.onSuccess { barcode ->
    Log.d("Scanner", "Código: ${barcode?.rawValue}")
}.onFailure {
    Log.e("Scanner", "Error al escanear: ${it.message}")
}
```

---

### 📸 `IStartCameraUseCase`

Inicializa la cámara y devuelve un `PreviewView` listo para mostrar el stream.

```kotlin
interface IStartCameraUseCase {
    operator fun invoke(recognizerImage: (ImageProxy) -> Unit): Result<PreviewView>
}
```

#### Ejemplo de uso:

```kotlin
val startCameraUseCase: IStartCameraUseCase = ...
val preview = startCameraUseCase { imageProxy ->
    // Aquí puedes pasar el frame al IScanningBarcodeUseCase
}.getOrNull()
```

---

### 📷 `ITakePhotoUseCase`

Permite capturar una foto desde la cámara y obtener un `Bitmap` del resultado.

```kotlin
interface ITakePhotoUseCase {
    operator fun invoke(onCaptureSuccess: (Bitmap) -> Unit)
}
```

#### Ejemplo de uso:

```kotlin
val takePhotoUseCase: ITakePhotoUseCase = ...
takePhotoUseCase { bitmap ->
    Log.d("Camera", "Foto capturada con tamaño: ${bitmap.width}x${bitmap.height}")
}
```

---

## 🔐 Gestión de permisos: `PermissionManager`

La clase `PermissionManager` facilita la solicitud, verificación y actualización del estado de permisos en Android, incluyendo compatibilidad con **Jetpack Compose** y **Accompanist Permissions**.

### 📦 `PermissionState`

Representa el estado actual de un permiso.

```kotlin
@Parcelize
data class PermissionState(
    val perm: String,
    val isGranted: Boolean,
    val shouldShowRequestPermissionRationale: Boolean,
    val isPermanentlyDenied: Boolean = !isGranted && !shouldShowRequestPermissionRationale
) : Parcelable
```

---

### 🧰 `PermissionManager`

La clase PermissionManager es un componente clave en la aplicación, diseñado para centralizar y simplificar la gestión de permisos de Android. Su objetivo es abstraer la complejidad de verificar, solicitar y rastrear el estado de múltiples permisos, especialmente en el contexto de una aplicación Jetpack Compose. Utiliza la biblioteca accompanist-permissions para la lógica de solicitud UI y complementa esto con su propia lógica de seguimiento de estado para ofrecer una visión completa de cada permiso.

#### Construcción:

```kotlin
val permissionManager = PermissionManager.Builder()
    .addAllPermissions(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
    .build(activity)
```

#### Obtener estados:

```kotlin
val states = permissionManager.getPermissionStates()
states.forEach {
    Log.d("Permission", "${it.perm} -> ${it.isGranted}")
}
```

#### Integración con Compose:

```kotlin
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionsHandler(manager: PermissionManager) {
    manager.rememberMultiplePermissionsState { states ->
        states.forEach {
            if (!it.isGranted) {
                Log.w("Permission", "Permiso denegado: ${it.perm}")
            }
        }
    }
}
```

---

## 📦 Clases principales del módulo

| Clase / Interfaz | Descripción |
|------------------|-------------|
| `ScannerActivity` | Pantalla de escaneo completa |
| `CameraViewModel` | Viewmodel para uso en implementaciones personalizadas |
| `IScanningBarcodeUseCase` | Lógica de escaneo de códigos |
| `IStartCameraUseCase` | Inicialización y preview de cámara |
| `ITakePhotoUseCase` | Captura de imágenes |
| `PermissionManager` | Gestión de permisos múltiples |
| `PermissionState` | Representa el estado de cada permiso |

---

## 🧠 Ejemplo de integración completa

```kotlin
@Composable
fun BarcodeScannerApp(activity: Activity) {
    val permissionManager = remember {
        PermissionManager.Builder()
            .addPermission(Manifest.permission.CAMERA)
            .build(activity)
    }

    PermissionsHandler(manager = permissionManager)

    val barcodeScanner = remember { mutableStateOf("") }

    val scanActivity = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { onResult ->
        if (onResult.resultCode == RESULT_OK) {
            val barcode = onResult.data?.getStringExtra(EXTRA_BARCODE)
            val type = onResult.data?.getStringExtra(EXTRA_BARCODE_TYPE)
            barcodeScanner.value = "$barcode ($type)"
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Button(onClick = {
            val intent = Intent(ACTION_BARCODE_SCAN)
            scanActivity.launch(intent)
        }) {
            Text("Escanear código de barras")
        }

        Text(text = "Resultado: ${barcodeScanner.value}")
    }
}
```

---

## 🛠️ Requisitos

- Android 6.0 (API 23) o superior
- Kotlin 1.9+
- Jetpack Compose
- Accompanist Permissions
- Dagger Hilt
- MLKit Barcode Scanning (implícito en la librería)

---

## 🤝 Contribuciones

¡Las contribuciones son bienvenidas!  
Si deseas mejorar la librería o añadir nuevos casos de uso, abre un **issue** o envía un **pull request**.

---


---

## ✨ Autor

Desarrollado con ❤️ por **LeandroLCD**  
📦 GitHub: [github.com/LeandroLCD](https://github.com/LeandroLCD)

---
