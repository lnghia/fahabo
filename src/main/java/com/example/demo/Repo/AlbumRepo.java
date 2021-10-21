package com.example.demo.Repo;

import com.example.demo.domain.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AlbumRepo extends JpaRepository<Album, Integer> {
    @Query(value = "SELECT * FROM albums WHERE id=:id AND is_deleted=false", nativeQuery = true)
    Album getById(@Param("id") int id);

    @Query(value = "SELECT * FROM albums WHERE family_id=:family_id AND title=:title AND is_deleted=false", nativeQuery = true)
    Album findByFamilyIdAndTitle(@Param("family_id") int familyId,
                                 @Param("title") String title);

    @Query(value = "SELECT COUNT(DISTINCT id) FROM albums WHERE id=:album_id AND family_id=:family_id AND is_deleted=false", nativeQuery = true)
    int countByAlbumIdAndFamilyId(@Param("album_id") int albumId,
                                  @Param("family_id") int familyId);

//    @Query(value = "SELECT COUNT(DISTINCT user_id) " +
//            "FROM (SELECT user_id, albums.family_id, albums.id AS album_id FROM albums INNER JOIN users_in_families ON albums.family_id=users_in_families.family_id) AS tmp " +
//            "WHERE tmp.user_id=:userId AND album_id=:albumId", nativeQuery = true)
//    int countByAlbumIdAndUserId()
}
