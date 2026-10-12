package es.rutafinder.search
import es.rutafinder.data.*
import es.rutafinder.geo.GeoMath
import org.osmdroid.util.GeoPoint
class RouteEngine(private val routes:List<RouteTrack>) {
 fun search(origin:GeoPoint,destination:GeoPoint,maxResults:Int=5,accessLimit:Double=1500.0,joinLimit:Double=200.0):List<Candidate>{
   val singles=routes.map { r-> val a=GeoMath.minDistance(origin,r.points); val b=GeoMath.minDistance(destination,r.points); Candidate(listOf(r),a+b,a,b) }.filter{it.originGap<=accessLimit&&it.destinationGap<=accessLimit}.sortedBy{it.score}
   val nearOrigin=routes.map{it to GeoMath.minDistance(origin,it.points)}.filter{it.second<=accessLimit}.sortedBy{it.second}.take(20)
   val nearDest=routes.map{it to GeoMath.minDistance(destination,it.points)}.filter{it.second<=accessLimit}.sortedBy{it.second}.take(20)
   val pairs= mutableListOf<Candidate>(); for((a,oa) in nearOrigin) for((b,db) in nearDest) if(a!==b){ val j=GeoMath.minDistance(a.points,b.points); if(j<=joinLimit) pairs.add(Candidate(listOf(a,b),oa+db+j,oa,db,j)) }
   return (singles+pairs).distinctBy{it.routes.joinToString("|"){r->r.fileName}}.sortedBy{it.score}.take(maxResults)
 }
}
