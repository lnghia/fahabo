package com.example.demo.Album.Helper;

import com.example.demo.Album.Service.Album.AlbumService;
import com.example.demo.Album.Service.AlbumsPhotos.AlbumsPhotosService;
import com.example.demo.Album.Entity.Album;
import com.example.demo.Family.Entity.Family;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;

@Component
public class AlbumHelper {
    @Autowired
    private AlbumService albumService;

    @Autowired
    private AlbumsPhotosService albumsPhotosService;

    public HashMap<String, Object> getJson(Album album, String uri){
        return new HashMap<>() {{
            put("id", album.getId());
            put("title", album.getTitle());
            put("description", album.getDescription());
            put("totalPhotos", albumService.countAllByAlbum(album.getId()));
            put("uri", uri);
        }};
    }

    public void deleteAlbumsInFamily(int familyId){
        albumsPhotosService.deletePhotosAlbumsRelationByFamilyId(familyId);
        albumsPhotosService.deletePhotosInFamilyAlbums(familyId);
        albumService.deleteAlbumsInFamily(familyId);
    }

    public void pointFamilyAlbumsToTmpFamily(Family family, Family tmpFamily){
        for(var album : family.getAlbums()){
            album.setFamily(tmpFamily);
            albumService.saveAlbum(album);
        }
        family.getDefaultAlbum().setFamily(tmpFamily);
        albumService.saveAlbum(family.getDefaultAlbum());
    }

    public Album createDefaultAlbum(Family family, Date now){
        Album newAlbum = new Album("Default album");
        newAlbum.setFamily(family);
        newAlbum.setCreatedAt(now);
        newAlbum.setUpdatedAt(now);
        albumService.saveAlbum(newAlbum);

        return newAlbum;
    }
}
