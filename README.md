# 🎬 Movies App - FAVFLIX
Aplicación Android desarrollada en Java para explorar películas, gestionar favoritos y compartir contenido.

## 📌 Características principales
### 🎨 Diseño e Interfaz
- Diseño de iconos: Personalizados para una experiencia visual coherente. <br>
  ![mdpi](/app/src/main/res/mipmap-mdpi/ic_launcher.png) ![hdpi](/app/src/main/res/mipmap-hdpi/ic_launcher.png) ![xhdpi](/app/src/main/res/mipmap-xhdpi/ic_launcher.png) ![xxhdpi](/app/src/main/res/mipmap-xxhdpi/ic_launcher.png)
- Uso de Fragments: Arquitectura modular para una navegación fluida.
- Elementos de lista: Adaptadores personalizados para mostrar películas.  <br> <br>
<div align="center">
  <img src="slides/asset1.png" width="200" height="350">
  <img src="slides/asset2.png" width="200" height="350">
  <img src="slides/asset3.png" width="200" height="350">
 </div>

### 🌐 Gestión de Datos
- Descarga desde servidor remoto (Utilizando la API pública [IMDbOT](https://github.com/TelegramPlayground/Free-Movie-Series-DB-API) ): 
  - Primera descarga: Búsqueda inicial de películas.
  - Segunda descarga: Detalles al hacer click en una película.
- Modo Offline:
  - Las películas favoritas se guardan localmente (SQLite/Room).
  - Eliminación permanente en "modo eliminar". <br> <br>
<div align="center">
  <img src="slides/asset4.png" width="200" height="200" style="margin-right: 20px;">
  <img src="slides/asset5.png" width="200" height="200">
</div>

### 🤝 Compartir Datos
Integración con FileProvider para compartir imágenes/películas vía apps externas (WhatsApp, correo, etc.).<br> <br>
<div align="center">
  <img src="slides/asset6.png" width="250" style="margin-bottom: 30px;">
  <br>
  <img src="slides/asset7.png" width="200" height="200" style="margin-right: 20px;">
  <img src="slides/asset8.png" width="200" height="200">
</div>

### 🔔 Notificaciones
- Prioridad baja:
  - Programadas con JobService cuando la app está en pausa.
  - Acciones manejadas por un BroadcastReceiver.
 <div align="center">
   <img src="slides/asset11.png" width="200" height="350">
 </div>
 
- Prioridad alta:
  - Implementadas con WorkManager en 2 fases:
  - Creación y mantenimiento en espera.
  - Actualización dinámica.

<div align="center">
  <img src="slides/asset9.png" width="200" height="100" style="margin-right: 20px;">
  <img src="slides/asset10.png" width="200" height="100">
</div>
    
### 🛠️ Tecnologías y Componentes
- Lenguaje: Java (Android SDK).
- Arquitectura: MVC
- Almacenamiento: SQLite (offline).
- Concurrencia: WorkManager, JobScheduler.
- Seguridad: FileProvider para acceso a archivos.

