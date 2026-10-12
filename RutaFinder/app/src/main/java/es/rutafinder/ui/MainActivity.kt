package es.rutafinder.ui
import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.LocationManager
import android.os.Bundle
import android.os.Environment
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import es.rutafinder.R
import es.rutafinder.data.*
import es.rutafinder.geo.GeoMath
import es.rutafinder.gpx.GpxParser
import es.rutafinder.map.MapLoader
import es.rutafinder.search.RouteEngine
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.*
import java.io.File
import kotlin.concurrent.thread

class MainActivity:AppCompatActivity(){
 private lateinit var map:MapView; private lateinit var status:TextView; private val routes= mutableListOf<RouteTrack>(); private var origin:GeoPoint?=null; private var destination:GeoPoint?=null
 private val root by lazy{File(Environment.getExternalStorageDirectory(),"RouteFinder")}; private val mapsDir by lazy{File(root,"Mapas")}; private val routesDir by lazy{File(root,"Rutas")}
 override fun onCreate(b:Bundle?){super.onCreate(b);setContentView(R.layout.activity_main);map=findViewById(R.id.map);status=findViewById(R.id.status); map.setMultiTouchControls(true);map.controller.setZoom(11.0);map.controller.setCenter(GeoPoint(40.25,-0.75)); root.mkdirs();mapsDir.mkdirs();routesDir.mkdirs(); requestPermissions(); bind(); reloadRoutes(); loadFirstMap()}
 private fun bind(){ findViewById<Button>(R.id.btnLocation).setOnClickListener{locate()};findViewById<Button>(R.id.btnDestination).setOnClickListener{status.text="Mantén pulsado en el mapa para marcar destino"};findViewById<Button>(R.id.btnSearch).setOnClickListener{search()};findViewById<Button>(R.id.btnMap).setOnClickListener{chooseMap()};findViewById<Button>(R.id.btnReload).setOnClickListener{reloadRoutes()};findViewById<Button>(R.id.btnClear).setOnClickListener{clearResults()}; map.overlays.add(object:MapEventsOverlay(object:MapEventsReceiver{override fun singleTapConfirmedHelper(p:GeoPoint)=false;override fun longPressHelper(p:GeoPoint):Boolean{destination=p;addMarker(p,"Destino",Color.RED);status.text="Destino: %.5f, %.5f".format(p.latitude,p.longitude);return true}}){}) }
 private fun requestPermissions(){ val ps=arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION);ActivityCompat.requestPermissions(this,ps,7)}
 private fun reloadRoutes(){status.text="Leyendo rutas...";thread{val parsed=routesDir.listFiles()?.filter{it.extension.lowercase() in listOf("gpx","txt") }?.mapNotNull{runCatching{GpxParser().parse(it)}.getOrNull()}?: emptyList();runOnUiThread{routes.clear();routes.addAll(parsed);status.text="${routes.size} rutas cargadas. Carpeta: ${routesDir.absolutePath}"}}}
 private fun loadFirstMap(){mapsDir.listFiles()?.firstOrNull{it.extension.equals("mbtiles",true)}?.let{runCatching{MapLoader.load(this,map,it);status.text="Mapa: ${it.name}"}}}
 private fun chooseMap(){val fs=mapsDir.listFiles()?.filter{it.extension.equals("mbtiles",true)}?: emptyList();if(fs.isEmpty()){toast("Copia los mapas en ${mapsDir.absolutePath}");return};AlertDialog.Builder(this).setTitle("Seleccionar mapa").setItems(fs.map{it.name}.toTypedArray()){_,i->runCatching{MapLoader.load(this,map,fs[i]);status.text="Mapa: ${fs[i].name}"}.onFailure{toast("No se pudo abrir el mapa")}}.show()}
 private fun locate(){if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){requestPermissions();return};val lm=getSystemService(LOCATION_SERVICE) as LocationManager;val l=runCatching{lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)?:lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)}.getOrNull();if(l==null){toast("Activa el GPS y espera una posición");return};val p=GeoPoint(l.latitude,l.longitude);origin=p;addMarker(p,"Origen",Color.GREEN);map.controller.animateTo(p);status.text="Origen GPS marcado"}
 private fun search(){val o=origin?:run{toast("Marca Mi posición");return};val d=destination?:run{toast("Mantén pulsado para marcar destino");return};status.text="Buscando...";thread{val found=RouteEngine(routes).search(o,d);runOnUiThread{clearRouteOverlays();if(found.isEmpty()){status.text="No hay rutas compatibles con los límites actuales";return@runOnUiThread};found.forEachIndexed{i,c->drawCandidate(c,i)};status.text=found.joinToString("  |  "){it.routes.joinToString(" + "){r->r.label}}}}}
 private fun drawCandidate(c:Candidate,index:Int){val colors=intArrayOf(Color.rgb(27,138,58),Color.rgb(21,101,192),Color.rgb(245,124,0),Color.MAGENTA,Color.CYAN);c.routes.forEachIndexed{j,r->val color=if(index==0) colors[j.coerceAtMost(1)] else colors[(index+1).coerceAtMost(colors.lastIndex)];r.segments.forEach{seg->map.overlays.add(Polyline().apply{setPoints(seg);outlinePaint.color=color;outlinePaint.strokeWidth=if(index==0) 9f else 6f;title=r.fileName})}; val m=GeoMath.nearestMidpoint(r.points);map.overlays.add(Marker(map).apply{position=m;title=r.fileName;snippet=r.label;setAnchor(Marker.ANCHOR_CENTER,Marker.ANCHOR_BOTTOM);showInfoWindow()})};map.invalidate()}
 private fun addMarker(p:GeoPoint,title:String,color:Int){map.overlays.removeAll{it is Marker && it.title==title};map.overlays.add(Marker(map).apply{position=p;this.title=title;setAnchor(Marker.ANCHOR_CENTER,Marker.ANCHOR_BOTTOM)});map.invalidate()}
 private fun clearRouteOverlays(){map.overlays.removeAll{it is Polyline || (it is Marker && it.title !in listOf("Origen","Destino"))}}
 private fun clearResults(){clearRouteOverlays();map.invalidate();status.text="Resultados limpiados"}
 private fun toast(s:String)=Toast.makeText(this,s,Toast.LENGTH_LONG).show()
 override fun onResume(){super.onResume();map.onResume()};override fun onPause(){map.onPause();super.onPause()}
}
