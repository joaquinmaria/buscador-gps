package es.rutafinder.gpx
import es.rutafinder.data.RouteTrack
import org.osmdroid.util.GeoPoint
import org.xmlpull.v1.XmlPullParser
import android.util.Xml
import java.io.*
class GpxParser {
 fun parse(file:File):RouteTrack? {
   val bytes=file.readBytes(); val text=try{String(bytes,Charsets.UTF_8)}catch(_:Exception){String(bytes,charset("ISO-8859-1"))}
   val parser=Xml.newPullParser(); parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES,false); parser.setInput(StringReader(text))
   val segs= mutableListOf<MutableList<GeoPoint>>(); var current= mutableListOf<GeoPoint>(); var event=parser.eventType
   while(event!=XmlPullParser.END_DOCUMENT){ if(event==XmlPullParser.START_TAG){ when(parser.name.substringAfter(':')){ "trkseg"->{ if(current.isNotEmpty())segs.add(current); current= mutableListOf() }; "trkpt","rtept"->{ val lat=parser.getAttributeValue(null,"lat")?.toDoubleOrNull(); val lon=parser.getAttributeValue(null,"lon")?.toDoubleOrNull(); if(lat!=null&&lon!=null) current.add(GeoPoint(lat,lon)) } } }; event=parser.next() }
   if(current.isNotEmpty())segs.add(current); return if(segs.flatten().size>=2) RouteTrack(file.name,segs) else null
 }
}
