package com.example.demo.ExpensesAndIncomes.Transaction.Helper;

import com.example.demo.DropBox.DropBoxHelper;
import com.example.demo.DropBox.ItemToUpload;
import com.example.demo.DropBox.UploadResult;
import com.example.demo.Event.Entity.PhotoInEvent;
import com.example.demo.ExpensesAndIncomes.Transaction.Entity.PhotosInTransactions;
import com.example.demo.ExpensesAndIncomes.Transaction.Entity.Transaction;
import com.example.demo.ExpensesAndIncomes.Transaction.Entity.TransactionAlbum;
import com.example.demo.ExpensesAndIncomes.Transaction.Entity.TransactionGroups;
import com.example.demo.ExpensesAndIncomes.Transaction.RequestBody.CreateTransactionReqBody;
import com.example.demo.ExpensesAndIncomes.Transaction.RequestBody.UpdateTransactionReqBody;
import com.example.demo.ExpensesAndIncomes.Transaction.Service.PhotosInTransactionsService;
import com.example.demo.ExpensesAndIncomes.Transaction.Service.TransactionAlbumService;
import com.example.demo.ExpensesAndIncomes.Transaction.Service.TransactionGroupService;
import com.example.demo.ExpensesAndIncomes.Transaction.Service.TransactionService;
import com.example.demo.ExpensesAndIncomes.TransactionCategory.Entity.TransactionCategory;
import com.example.demo.ExpensesAndIncomes.TransactionCategory.Service.TransactionCategoryService;
import com.example.demo.Helpers.Helper;
import com.example.demo.Service.Photo.PhotoService;
import com.example.demo.domain.Family.Family;
import com.example.demo.domain.Image;
import com.example.demo.domain.Photo;
import com.google.rpc.Help;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ExecutionException;

@Component
public class TransactionHelper {
    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionAlbumService transactionAlbumService;

    @Autowired
    private TransactionGroupService transactionGroupService;

    @Autowired
    private TransactionCategoryService transactionCategoryService;

    @Autowired
    private PhotoService photoService;

    @Autowired
    private PhotosInTransactionsService photosInTransactionsService;

    @Autowired
    private DropBoxHelper dropBoxHelper;

    public void deleteTransaction(Transaction transaction) {
        for (var photo : transaction.getTransactionAlbum().getPhotos()) {
            photosInTransactionsService.delete(photo);
        }
        TransactionGroups transactionGroups = transactionGroupService.findTransactionGroupBySubId(transaction.getId());
        transactionGroups.setDeleted(true);
        transactionGroupService.save(transactionGroups);
        transactionAlbumService.delete(transaction.getTransactionAlbum());
        transactionService.delete(transaction);
    }

    public void deleteAllTransactionInGroup(Transaction transaction) {
        TransactionGroups transactionGroup = transactionGroupService.findTransactionGroupBySubId(transaction.getId());
        int headId = transactionGroup.getHead().getId();

        ArrayList<TransactionGroups> transactionGroups = transactionGroupService.findAllTransactionsInGroup(headId);

        for (var i : transactionGroups) {
            deleteTransaction(i.getSub());
        }
    }

    public Transaction createTransactionFromAvailableOne(Transaction transaction) {
        Transaction transaction1 = new Transaction();

        transaction1.setTitle(transaction.getTitle());
        transaction1.setType(transaction.getType());
        transaction1.setCategory(transaction.getCategory());
        transaction1.setFamily(transaction.getFamily());
        transaction1.setCost(transaction.getCost());
        transaction1.setDate(Helper.getInstance().getNewDateAfterOccurrences(transaction.getDate(), transaction.getFamily().getTimezone(), transaction.getRepeatType(), 1));
        transaction1.setNote(transaction.getNote());
        transaction1.setCreatedAt(Helper.getInstance().getNowAsTimeZone(transaction.getFamily().getTimezone()));
        transaction1.setUpdatedAt(Helper.getInstance().getNowAsTimeZone(transaction.getFamily().getTimezone()));
        transactionService.save(transaction1);

        TransactionAlbum transactionAlbum = new TransactionAlbum();
        transactionAlbum.setTransaction(transaction);
        transactionAlbumService.save(transactionAlbum);

        TransactionGroups transactionGroups = new TransactionGroups();
        TransactionGroups transactionGroups1 = transactionGroupService.findTransactionGroupBySubId(transaction.getId());
        transactionGroups.setSub(transaction);
        transactionGroups.setHead(transactionGroups1.getHead());
        transactionGroupService.save(transactionGroups);

        Set<PhotosInTransactions> photos = transaction.getTransactionAlbum().getPhotos();

        for (var photoInTransaction : photos) {
            if (!photoInTransaction.isDeleted()) {
                PhotosInTransactions photosInTransactions = new PhotosInTransactions(photoInTransaction.getPhoto(), transactionAlbum);
                photosInTransactionsService.save(photosInTransactions);
            }
        }

        return transaction1;
    }

    public Transaction createSubTransaction(CreateTransactionReqBody reqBody, Family family, Date date, TransactionCategory transactionCategory, Transaction head, ArrayList<Photo> photos) {
        Transaction transaction = new Transaction();
//        date = Helper.getInstance().getNewDateAfterOccurrences(date, family.getTimezone(), reqBody.repeatType, 1);

        transaction.setTitle(reqBody.title);
        transaction.setType(reqBody.type);
        transaction.setCategory(transactionCategory);
        transaction.setFamily(family);
        transaction.setCost(reqBody.cost);
        transaction.setDate(date);
        transaction.setNote(reqBody.note);
        transaction.setCreatedAt(Helper.getInstance().getNowAsTimeZone(family.getTimezone()));
        transaction.setUpdatedAt(Helper.getInstance().getNowAsTimeZone(family.getTimezone()));
        transactionService.save(transaction);

        TransactionAlbum transactionAlbum = new TransactionAlbum();
        transactionAlbum.setTransaction(transaction);
        transactionAlbumService.save(transactionAlbum);

        TransactionGroups transactionGroups = new TransactionGroups();
        transactionGroups.setSub(transaction);
        transactionGroups.setHead(head);
        transactionGroupService.save(transactionGroups);

        for (var photo : photos) {
            PhotosInTransactions photosInTransactions = new PhotosInTransactions(photo, transactionAlbum);
            photosInTransactionsService.save(photosInTransactions);
        }

        return transaction;
    }

    public ArrayList<Transaction> createTransaction(CreateTransactionReqBody reqBody, Family family) throws ParseException, ExecutionException, InterruptedException {
        ArrayList<Transaction> transactions = new ArrayList<>();
        Transaction transaction = new Transaction();
        Date date = Helper.getInstance().formatDateWithoutTime(reqBody.date);
        Date now = new Date();
        TransactionCategory transactionCategory = transactionCategoryService.findById(reqBody.familyId, reqBody.categoryId);
        ArrayList<Photo> photos = new ArrayList<>();

        transaction.setTitle(reqBody.title);
        transaction.setCategory(transactionCategory);
        transaction.setType(reqBody.type);
        transaction.setFamily(family);
        transaction.setCost(reqBody.cost);
        transaction.setDate(Helper.getInstance().formatDateWithoutTime(reqBody.date));
        transaction.setNote(reqBody.note);
        transaction.setCreatedAt(Helper.getInstance().getNowAsTimeZone(family.getTimezone()));
        transaction.setUpdatedAt(Helper.getInstance().getNowAsTimeZone(family.getTimezone()));
        transaction.setRepeatType(reqBody.repeatType);
        transactionService.save(transaction);

        TransactionAlbum transactionAlbum = new TransactionAlbum();
        transactionAlbum.setTransaction(transaction);
        transactionAlbumService.save(transactionAlbum);
        transactionAlbumService.save(transactionAlbum);

        TransactionGroups transactionGroups = new TransactionGroups();
        transactionGroups.setHead(transaction);
        transactionGroups.setSub(transaction);
        transactionGroupService.save(transactionGroups);

        if (reqBody.photos != null && reqBody.photos.length > 0 && reqBody.photos.length < Helper.getInstance().CHORE_PHOTO_MAX_NUM) {
            ItemToUpload[] items = new ItemToUpload[reqBody.photos.length];
            HashMap<String, Photo> newPhotos = new HashMap<>();
            HashMap<String, PhotosInTransactions> _newPhotos = new HashMap<>();
            for (int i = 0; i < reqBody.photos.length; ++i) {
                Photo photo = new Photo();
                PhotosInTransactions photosInTransactions = new PhotosInTransactions();

                photo.setCreatedAt(now);
                photo.setUpdatedAt(now);

                photoService.savePhoto(photo);
                photosInTransactions.setPhoto(photo);
                photosInTransactions.setTransactionAlbum(transactionAlbum);
                photosInTransactionsService.save(photosInTransactions);
                photo.setName(Helper.getInstance().generatePhotoNameToUploadToAlbum(
                        family.getId(),
                        transactionAlbum.getId(),
                        photo.getId()));
                photoService.savePhoto(photo);
                newPhotos.put(photo.getName(), photo);
                _newPhotos.put(photo.getName(), photosInTransactions);

                items[i] = new ItemToUpload(photo.getName(), reqBody.photos[i]);
            }

            UploadResult result = null;
            result = dropBoxHelper.uploadImages(items, 0, 0);
            ArrayList<Image> success = result.getSuccessUploads();
            ArrayList<Image> fail = result.getFailUploads();

            for (var image : success) {
                Photo photo = newPhotos.get(image.getName());
                photo.setUri(image.getMetadata().getUrl());
                photoService.savePhoto(photo);
                photos.add(photo);
            }
            for (var image : fail) {
                PhotosInTransactions photosInTransactions = _newPhotos.get(image.getName());
                Photo photo = newPhotos.get(image.getName());
                photosInTransactions.setDeleted(true);
                photosInTransactionsService.save(photosInTransactions);
                photo.setDeleted(true);
                photoService.savePhoto(photo);
            }
        }

        transactions.add(transaction);
//        if (reqBody.repeatType != null && !reqBody.repeatType.isBlank() && !reqBody.repeatType.isEmpty()) {
//            for (int i = 0; i < reqBody.occurrences; ++i) {
//                date = Helper.getInstance().getNewDateAfterOccurrences(date, family.getTimezone(), reqBody.repeatType, 1);
//                Transaction transaction1 = createSubTransaction(
//                        reqBody,
//                        family,
//                        date,
//                        transactionCategory,
//                        transaction,
//                        photos
//                );
//                transactions.add(transaction1);
//            }
//        }

        return transactions;
    }

    public ArrayList<Photo> updateATransaction(Transaction transaction, UpdateTransactionReqBody requestBody, ArrayList<Photo> photos) throws ParseException, ExecutionException, InterruptedException {
        Helper helper = Helper.getInstance();

        if (requestBody.categoryId != null && requestBody.categoryId != transaction.getCategory().getId()) {
            TransactionCategory category = transactionCategoryService.findById(transaction.getFamily().getId(), requestBody.categoryId);
            transaction.setCategory(category);
        }
        if (requestBody.date != null && !requestBody.date.equals(helper.formatDateWithoutTime(transaction.getDate()))) {
            transaction.setDate(helper.formatDateWithoutTime(requestBody.date));
        }
        if (requestBody.note != null && !requestBody.note.equals(transaction.getNote())) {
            transaction.setNote(requestBody.note);
        }
        if (requestBody.title != null && !requestBody.title.equals(transaction.getTitle())) {
            transaction.setTitle(requestBody.title);
        }
        if (requestBody.repeatType != null && !requestBody.repeatType.equals(transaction.getRepeatType())) {
            transaction.setRepeatType(requestBody.repeatType);
        }
        if (requestBody.type != null && !requestBody.type.equals(transaction.getType())) {
            transaction.setType(requestBody.type);
        }
        if (requestBody.cost != null && !requestBody.cost.equals(transaction.getCost())) {
            transaction.setCost(requestBody.cost);
        }
        photos = updateTransactionPhotos(transaction, requestBody, photos);
        transactionService.save(transaction);

        return photos;
    }

    public ArrayList<Photo> updateTransactionPhotos(Transaction transaction, UpdateTransactionReqBody reqBody, ArrayList<Photo> photos) throws ExecutionException, InterruptedException {
        if (reqBody.deletePhotos != null) {
            for (var id : reqBody.deletePhotos) {
                photosInTransactionsService.delete(id, transaction.getTransactionAlbum().getId());
            }
        }

        Date now = new Date();

        if (photos == null || photos.isEmpty()) {
            ItemToUpload[] items = new ItemToUpload[reqBody.photos.length];
            HashMap<String, Photo> newPhotos = new HashMap<>();
            HashMap<String, PhotosInTransactions> _newPhotos = new HashMap<>();
            ArrayList<Photo> ans = new ArrayList<>();
            TransactionAlbum transactionAlbum = transaction.getTransactionAlbum();
            for (int i = 0; i < reqBody.photos.length; ++i) {
                Photo photo = new Photo();
                PhotosInTransactions photosInTransactions = new PhotosInTransactions();

                photo.setCreatedAt(now);
                photo.setUpdatedAt(now);

                photoService.savePhoto(photo);
                photosInTransactions.setPhoto(photo);
                photosInTransactions.setTransactionAlbum(transactionAlbum);
                photosInTransactionsService.save(photosInTransactions);
                photo.setName(Helper.getInstance().generatePhotoNameToUploadToAlbum(
                        transaction.getFamily().getId(),
                        transactionAlbum.getId(),
                        photo.getId()));
                photoService.savePhoto(photo);
                newPhotos.put(photo.getName(), photo);
                _newPhotos.put(photo.getName(), photosInTransactions);

                items[i] = new ItemToUpload(photo.getName(), reqBody.photos[i]);
            }

            UploadResult result = null;
            result = dropBoxHelper.uploadImages(items, 0, 0);
            ArrayList<Image> success = result.getSuccessUploads();
            ArrayList<Image> fail = result.getFailUploads();

            for (var image : success) {
                Photo photo = newPhotos.get(image.getName());
                photo.setUri(image.getMetadata().getUrl());
                photoService.savePhoto(photo);
                ans.add(photo);
            }
            for (var image : fail) {
                PhotosInTransactions photosInTransactions = _newPhotos.get(image.getName());
                Photo photo = newPhotos.get(image.getName());
                photosInTransactions.setDeleted(true);
                photosInTransactionsService.save(photosInTransactions);
                photo.setDeleted(true);
                photoService.savePhoto(photo);
            }

            return ans;
        } else {
            for (var photo : photos) {
                PhotosInTransactions photosInTransactions = new PhotosInTransactions();
                photosInTransactions.setPhoto(photo);
                photosInTransactions.setTransactionAlbum(transaction.getTransactionAlbum());
                photosInTransactionsService.save(photosInTransactions);
            }
        }

        return photos;
    }

    public Transaction updateTransaction(Transaction transaction, UpdateTransactionReqBody requestBody) throws ParseException, ExecutionException, InterruptedException {
        ArrayList<Photo> photos = new ArrayList<>();
        photos = updateATransaction(transaction, requestBody, photos);
        if (requestBody.updateAll) {
            TransactionGroups transactionGroup = transactionGroupService.findTransactionGroupBySubId(transaction.getId());
            ArrayList<TransactionGroups> transactionGroups = transactionGroupService.findAllTransactionsInGroup(transactionGroup.getHead().getId());

            for (var transGroup : transactionGroups) {
                if (transGroup.getSub().getId() != transaction.getId()) {
                    updateATransaction(transGroup.getSub(), requestBody, photos);
                }
            }
        }

        return transaction;
    }
}
