package es.rutafinder.map
import android.content.Context
import org.osmdroid.tileprovider.modules.OfflineTileProvider
import org.osmdroid.tileprovider.util.SimpleRegisterReceiver
import org.osmdroid.tileprovider.tilesource.FileBasedTileSource
import org.osmdroid.views.MapView
import java.io.File
object MapLoader {
 fun load(context:Context,map:MapView,file:File):String {
   val provider=OfflineTileProvider(SimpleRegisterReceiver(context), arrayOf(file)); map.setTileProvider(provider); map.setUseDataConnection(false)
   val archives=provider.archives; if(archives.isNotEmpty()){ val names=archives[0].tileSources; if(names.isNotEmpty()) map.setTileSource(FileBasedTileSource.getSource(names.first())) }
   map.invalidate(); return file.name
 }
}
