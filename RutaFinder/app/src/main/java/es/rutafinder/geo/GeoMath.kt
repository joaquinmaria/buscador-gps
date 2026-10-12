package es.rutafinder.geo
import org.osmdroid.util.GeoPoint
import kotlin.math.*
object GeoMath {
 fun distance(a:GeoPoint,b:GeoPoint):Double { val r=6371000.0; val p1=Math.toRadians(a.latitude); val p2=Math.toRadians(b.latitude); val dp=Math.toRadians(b.latitude-a.latitude); val dl=Math.toRadians(b.longitude-a.longitude); val x=sin(dp/2).pow(2)+cos(p1)*cos(p2)*sin(dl/2).pow(2); return 2*r*atan2(sqrt(x),sqrt(1-x)) }
 fun minDistance(p:GeoPoint, pts:List<GeoPoint>):Double = pts.minOfOrNull { distance(p,it) } ?: Double.MAX_VALUE
 fun minDistance(a:List<GeoPoint>,b:List<GeoPoint>):Double { if(a.isEmpty()||b.isEmpty()) return Double.MAX_VALUE; val aa=sample(a,300); val bb=sample(b,300); var best=Double.MAX_VALUE; for(x in aa) for(y in bb){ val d=distance(x,y); if(d<best) best=d }; return best }
 fun sample(p:List<GeoPoint>,max:Int):List<GeoPoint>{ if(p.size<=max)return p; val step=ceil(p.size.toDouble()/max).toInt(); return p.filterIndexed{i,_->i%step==0} }
 fun nearestMidpoint(points:List<GeoPoint>):GeoPoint = points[points.size/2]
}
