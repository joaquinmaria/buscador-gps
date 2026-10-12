# RutaFinder Android

Proyecto Kotlin para Android 6.0.1 o superior. Usa mapas MBTiles y rutas GPX sin conexión.

## Instalación para desarrollar
1. Abre esta carpeta con Android Studio.
2. Deja que Gradle descargue las dependencias.
3. Conecta el Samsung SM-A300FU con depuración USB activada.
4. Ejecuta la configuración `app`, o usa Build > Build APK(s).

## Carpetas en el teléfono
La aplicación crea:
- `/RouteFinder/Mapas/` para archivos `.mbtiles`.
- `/RouteFinder/Rutas/` para archivos `.gpx` (también admite `.txt` para pruebas).

Copia `teruel_sur.mbtiles` a la carpeta Mapas y todos los GPX a la carpeta Rutas. Después pulsa `Recargar GPX`.

## Uso
1. Pulsa `Mi posición`.
2. Mantén pulsado el destino en el mapa.
3. Pulsa `Buscar`.
4. Se muestran hasta cinco opciones. La mejor aparece más gruesa. Se permiten uno o dos ficheros.
5. La etiqueta visible usa los primeros 10 caracteres; al tocar el marcador aparece el nombre completo.

## Decisiones
- Todas las rutas se consideran transitables en ambos sentidos.
- Admite `trkpt`, múltiples `trkseg` y `rtept`.
- Enlace máximo inicial entre dos rutas: 200 m.
- Acceso máximo inicial desde origen/destino: 1.500 m.

## Nota de compilación
No se incluye un APK precompilado. Android Studio genera el APK firmado de depuración. El proyecto tampoco incluye el fichero MBTiles de 1,5 GB ni rutas privadas.
