package es.rutafinder.data
import org.osmdroid.util.GeoPoint

data class RouteTrack(val fileName:String, val segments:List<List<GeoPoint>>) {
    val label:String = fileName.substringBeforeLast('.').take(10)
    val points:List<GeoPoint> by lazy { segments.flatten() }
}
data class Candidate(val routes:List<RouteTrack>, val score:Double, val originGap:Double, val destinationGap:Double, val joinGap:Double=0.0)
