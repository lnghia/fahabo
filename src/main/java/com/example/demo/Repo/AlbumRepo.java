package com.example.demo.Repo;

import com.example.demo.domain.Album;
import com.example.demo.domain.Photo;
import jdk.dynalink.linker.LinkerServices;
import liquibase.pro.packaged.A;
import liquibase.pro.packaged.I;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlbumRepo extends JpaRepository<Album, Integer> {
    @Query(value = "SELECT * FROM albums WHERE id=:id AND is_deleted=false", nativeQuery = true)
    Album getById(@Param("id") int id);

    @Query(value = "SELECT * FROM albums WHERE family_id=:family_id AND title=:title AND is_deleted=FALSE", nativeQuery = true)
    Album findByFamilyIdAndTitle(@Param("family_id") int familyId,
                                 @Param("title") String title);

    @Query(value = "SELECT COUNT(DISTINCT id) FROM albums WHERE id=:album_id AND family_id=:family_id AND is_deleted=FALSE", nativeQuery = true)
    int countByAlbumIdAndFamilyId(@Param("album_id") int albumId,
                                  @Param("family_id") int familyId);

    @Query(value = "SELECT COUNT(DISTINCT photo_id) FROM photos_in_albums WHERE album_id=:albumId AND is_deleted=FALSE", nativeQuery = true)
    int countAllByAlbum(@Param("albumId") int albumId);

    @Query(value = "SELECT family_id FROM albums WHERE id=:id AND is_deleted=FALSE", nativeQuery = true)
    Integer getFamilyIdByAlbumId(@Param("id") int id);

    @Query(value = "SELECT DISTINCT * FROM albums " +
            "WHERE is_deleted=FALSE " +
            "AND family_id=:familyId " +
            "AND NOT id=:defaultAlbumId " +
            "AND (:searchText IS NULL OR :searchText='' OR title LIKE %:searchText%) " +
            "ORDER BY created_at DESC", nativeQuery = true,
            countQuery = "SELECT COUNT(DISTINCT id) FROM albums " +
                    "AND is_deleted=FALSE " +
                    "WHERE family_id=:familyId " +
                    "AND NOT id=:defaultAlbumId " +
                    "AND (:searchText IS NULL OR :searchText='' OR title LIKE %:searchText%)")
    List<Album> findAllByFamilyIdWithPagination(@Param("familyId") int familyId,
                                                @Param("defaultAlbumId") int defaultAlbumId,
                                                @Param("searchText") String searchText,
                                                Pageable pageable);

    @Query(value = "SELECT photo_id FROM (SELECT DISTINCT (photo_id), b.is_deleted, created_at, album_id FROM photos_in_albums AS a INNER JOIN photos AS b ON a.photo_id=b.id) AS C" +
            " WHERE album_id=:albumId AND is_deleted=FALSE ORDER BY created_at DESC",
            countQuery = "SELECT COUNT(DISTINCT photo_id) FROM photos_in_albums WHERE album_id=:albumId AND is_deleted=FALSE",
            nativeQuery = true)
    List<Integer> getPhotoIdsByAlbumIdWithPagination(@Param("albumId") int albumId, Pageable pageable);

    @Query(value = "SELECT photo_id FROM (SELECT photo_id, created_at, b.is_deleted, album_id FROM (photos_in_albums AS a INNER JOIN photos AS b ON a.photo_id=b.id)) AS c" +
            " WHERE c.album_id=:albumId AND c.is_deleted=FALSE ORDER BY c.created_at DESC LIMIT 9", nativeQuery = true)
    List<Integer> get9LatestPhotosFromAlbum(@Param("albumId") int albumId);

    @Query(value = "UPDATE albums SET is_deleted=TRUE WHERE is_deleted=FALSE AND family_id=:familyId", nativeQuery = true)
    void deleteAlbumsInFamily(@Param("familyId") int familyId);

    @Query(value = "SELECT uri FROM photos WHERE id IN (SELECT photos FROM photos_in_albums WHERE albums=:albumId) ORDER BY created_at TOP 1", nativeQuery = true)
    String getMostRecentImageUriInAlbum(@Param("albumId") int albumId);

//    @Query(value = "SELECT COUNT(DISTINCT user_id) " +
//            "FROM (SELECT user_id, albums.family_id, albums.id AS album_id FROM albums INNER JOIN users_in_families ON albums.family_id=users_in_families.family_id) AS tmp " +
//            "WHERE tmp.user_id=:userId AND album_id=:albumId", nativeQuery = true)
//    int countByAlbumIdAndUserId()
}
