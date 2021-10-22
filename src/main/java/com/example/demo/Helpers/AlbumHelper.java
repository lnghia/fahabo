package com.example.demo.Helpers;

import com.example.demo.Service.Album.AlbumService;
import com.example.demo.domain.Album;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class AlbumHelper {
    @Autowired
    private AlbumService albumService;

    public HashMap<String, Object> getJson(Album album){
        return new HashMap<>() {{
            put("id", album.getId());
            put("title", album.getTitle());
            put("description", album.getDescription());
            put("totalPhotos", albumService.countAllByAlbum(album.getId()));
        }};
    }
}
