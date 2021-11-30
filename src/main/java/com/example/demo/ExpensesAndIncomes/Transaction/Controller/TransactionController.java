package com.example.demo.ExpensesAndIncomes.Transaction.Controller;

import com.example.demo.DropBox.DropBoxRedirectedLinkGetter;
import com.example.demo.DropBox.GetRedirectedLinkExecutionResult;
import com.example.demo.DropBox.GetRedirectedLinkTask;
import com.example.demo.ExpensesAndIncomes.Transaction.Entity.PhotosInTransactions;
import com.example.demo.ExpensesAndIncomes.Transaction.Entity.Transaction;
import com.example.demo.ExpensesAndIncomes.Transaction.Helper.TransactionHelper;
import com.example.demo.ExpensesAndIncomes.Transaction.RequestBody.*;
import com.example.demo.ExpensesAndIncomes.Transaction.Service.TransactionGroupService;
import com.example.demo.ExpensesAndIncomes.Transaction.Service.TransactionService;
import com.example.demo.ExpensesAndIncomes.TransactionCategory.Entity.TransactionCategory;
import com.example.demo.ExpensesAndIncomes.TransactionCategory.Service.TransactionCategoryService;
import com.example.demo.Helpers.Helper;
import com.example.demo.ResponseFormat.Response;
import com.example.demo.Service.Family.FamilyService;
import com.example.demo.domain.CustomUserDetails;
import com.example.demo.domain.Family.Family;
import com.example.demo.domain.Image;
import com.example.demo.domain.User;
import jdk.jfr.Category;
import liquibase.pro.packaged.A;
import liquibase.pro.packaged.I;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api/v1/transactions")
@Slf4j
public class TransactionController {
    @Autowired
    private TransactionService transactionService;

    @Autowired
    private FamilyService familyService;

    @Autowired
    private TransactionGroupService transactionGroupService;

    @Autowired
    private TransactionHelper transactionHelper;

    @Autowired
    private TransactionCategoryService transactionCategoryService;

    @PostMapping
    public ResponseEntity<Response> getAll(@Valid @RequestBody GetAllTransactionReqBody reqBody,
                                           @RequestParam(value = "page", defaultValue = "0") Integer page,
                                           @RequestParam(value = "size", defaultValue = "5") Integer size) {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        Family family = familyService.findById(reqBody.familyId);

        log.info(String.format("Getting transactions user: %d family: %d", user.getId(), reqBody.familyId));
        if (family.checkIfUserExist(user)) {
            try {
                ArrayList<Transaction> transactions = transactionService.findAllByFamily(
                        reqBody.familyId,
                        reqBody.searchText,
                        (reqBody.from != null) ? Helper.getInstance().formatDate(reqBody.from) : null,
                        (reqBody.to != null) ? Helper.getInstance().formatDate(reqBody.to) : null,
                        page,
                        size
                );

                ArrayList<Image> images = new ArrayList<>();
                for (var transaction : transactions) {
                    images.add(new Image(Integer.toString(transaction.getId()), transaction.getCategory().getIcon()));
                }

                DropBoxRedirectedLinkGetter getter = new DropBoxRedirectedLinkGetter();
                GetRedirectedLinkExecutionResult executionResult = getter.getRedirectedLinks(images);

                ArrayList<HashMap<String, Object>> data = new ArrayList<>();
                for (var transaction : transactions) {
                    String iconUrl = (executionResult != null) ? executionResult.getSuccessfulResults().get(Integer.toString(transaction.getId())).getUri() : null;
                    data.add(transaction.getJson(iconUrl));
                }

                return ResponseEntity.ok(new Response(data, new ArrayList<>()));
            } catch (ParseException e) {
                log.error("Couldn't parse date /transactions.", e.getMessage());
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of("validation.dateFormatInvalid"))));
            } catch (ExecutionException | InterruptedException e) {
                log.error("Couldn't get redirected links /transactions.");
                log.error(e.getMessage());
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response(null, new ArrayList<>(List.of("unknownError"))));
            }
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response(null, new ArrayList<>(List.of("validation.unauthorized"))));
    }

    @PostMapping("/detail")
    public ResponseEntity<Response> getDetail(@RequestBody GetDetailTransactionReqBody reqBody) {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        Transaction transaction = transactionService.findById(reqBody.transactionId);

        if (transaction != null) {
            if (transaction.getFamily().checkIfUserExist(user)) {
                ArrayList<Image> images = new ArrayList<>();
                images.add(new Image(Integer.toString(transaction.getId()), transaction.getCategory().getIcon()));

                DropBoxRedirectedLinkGetter getter = new DropBoxRedirectedLinkGetter();
                try {
                    GetRedirectedLinkExecutionResult executionResult = getter.getRedirectedLinks(images);
                    String iconUrl = (executionResult != null) ? executionResult.getSuccessfulResults().get(Integer.toString(transaction.getId())).getUri() : null;

                    return ResponseEntity.ok(new Response(transaction.getJson(iconUrl), new ArrayList<>()));
                } catch (InterruptedException | ExecutionException e) {
                    log.error("Couldn't get redirected links /transactions/detail.");
                    log.error(e.getMessage());
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response(null, new ArrayList<>(List.of("unknownError"))));
                }
            }

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response(null, new ArrayList<>(List.of("validation.unauthorized"))));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of("validation.transactionIdNotExist"))));
    }

    @PostMapping("/photos")
    public ResponseEntity<Response> getTransactionPhotos(@RequestBody GetDetailTransactionReqBody reqBody) {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        Transaction transaction = transactionService.findById(reqBody.transactionId);

        if (transaction != null) {
            if (transaction.getFamily().checkIfUserExist(user)) {
                ArrayList<Image> images = new ArrayList<>();
                Set<PhotosInTransactions> photos = transaction.getTransactionAlbum().getPhotos();

                for (var photo : photos) {
                    if (photo.isDeleted()) continue;
                    images.add(new Image(photo.getPhoto().getName(), photo.getPhoto().getUri()));
                }

                DropBoxRedirectedLinkGetter getter = new DropBoxRedirectedLinkGetter();
                try {
                    GetRedirectedLinkExecutionResult executionResult = getter.getRedirectedLinks(images);
                    ArrayList<HashMap<String, Object>> data = new ArrayList<>();
                    HashMap<String, GetRedirectedLinkTask.GetRedirectedLinkResult> success = executionResult.getSuccessfulResults();

                    for (var photo : photos) {
                        if (success.containsKey(photo.getPhoto().getName())) {
                            data.add(new HashMap<>() {{
                                put("uri", success.get(photo.getPhoto().getName()).getUri());
                            }});
                        }
                    }

                    return ResponseEntity.ok(new Response(data, new ArrayList<>()));
                } catch (InterruptedException | ExecutionException e) {
                    log.error("Couldn't get redirected links /transactions/detail.");
                    log.error(e.getMessage());
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response(null, new ArrayList<>(List.of("unknownError"))));
                }
            }

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response(null, new ArrayList<>(List.of("validation.unauthorized"))));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of("validation.transactionIdNotExist"))));
    }

    @PostMapping("/delete")
    public ResponseEntity<Response> deleteTransaction(@RequestBody DeleteTransactionReqBody reqBody) {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        Transaction transaction = transactionService.findById(reqBody.transactionId);

        if (transaction != null) {
            if (!transaction.isIs_deleted() && transaction.getFamily().checkIfUserExist(user)) {
                if (reqBody.deleteAll) {
                    transactionHelper.deleteAllTransactionInGroup(transaction);
                } else {
                    transactionHelper.deleteTransaction(transaction);
                }

                return ResponseEntity.ok(new Response(new HashMap<>() {{
                    put("transactionId", reqBody.transactionId);
                }}, new ArrayList<>()));
            }

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response(null, new ArrayList<>(List.of("validation.unauthorized"))));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of("validation.transactionIdNotExist"))));
    }

    @PostMapping("/create")
    public ResponseEntity<Response> createTransaction(@RequestBody CreateTransactionReqBody reqBody) {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        Family family = familyService.findById(reqBody.familyId);

        if (family.checkIfUserExist(user)) {
            try {
                ArrayList<Transaction> transactions = transactionHelper.createTransaction(reqBody, family);
                String iconUrl = null;

                if (transactions != null && !transactions.isEmpty()) {
                    TransactionCategory transactionCategory = transactions.get(0).getCategory();
                    DropBoxRedirectedLinkGetter getter = new DropBoxRedirectedLinkGetter();
                    GetRedirectedLinkExecutionResult result = getter.getRedirectedLinks(new ArrayList<>(List.of(new Image(transactionCategory.getTitle(), transactionCategory.getIcon()))));
                    if (result != null && !result.getSuccessfulResults().isEmpty()) {
                        iconUrl = result.getSuccessfulResults().get(transactionCategory.getTitle()).getUri();
                    }
                }
                final String iconUrlReadyToView = iconUrl;

                return ResponseEntity.ok(new Response(
                        transactions.get(0).getJson(iconUrlReadyToView),
                        new ArrayList<>()
                ));
            } catch (ParseException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of("validation.dateFormatInvalid"))));
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response(null, new ArrayList<>(List.of("validation.unauthorized"))));
    }

    @PostMapping("/update")
    public ResponseEntity<Response> updateTransaction(@RequestBody UpdateTransactionReqBody reqBody) {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        Transaction transaction = transactionService.findById(reqBody.transactionId);
        String iconUrl = null;

        if (transaction != null) {
            if (transaction.getFamily().checkIfUserExist(user)) {
                try {
                    transaction = transactionHelper.updateTransaction(transaction, reqBody);

                    ArrayList<Image> images = new ArrayList<>();
                    images.add(new Image(Integer.toString(transaction.getId()), transaction.getCategory().getIcon()));

                    DropBoxRedirectedLinkGetter getter = new DropBoxRedirectedLinkGetter();
                    GetRedirectedLinkExecutionResult executionResult = getter.getRedirectedLinks(images);
                    iconUrl = (executionResult != null) ? executionResult.getSuccessfulResults().get(Integer.toString(transaction.getId())).getUri() : null;

                    return ResponseEntity.ok(new Response(transaction.getJson(iconUrl), new ArrayList<>()));
                } catch (ParseException e) {
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of("validation.dateFormatInvalid"))));
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response(transaction.getJson(null), new ArrayList<>(List.of("unknownError"))));
            }

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response(null, new ArrayList<>(List.of("validation.unauthorized"))));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of("validation.transactionIdNotExist"))));
    }

    @PostMapping("/statistic")
    public ResponseEntity<Response> statistic(@Valid @RequestBody StatisticReqBody reqBody) {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        Family family = familyService.findById(reqBody.familyId);

        if (family.checkIfUserExist(user)) {
            ArrayList<TransactionCategory> transactionCategories = transactionCategoryService.findAll(reqBody.familyId, "");
            ArrayList<Transaction> transactions = transactionService.findTransactionsInMonthYear(reqBody.month, reqBody.year, reqBody.familyId);
            ArrayList<HashMap<String, Object>> data = new ArrayList<>();
            HashMap<String, HashMap<String, Object>> categoryName = new HashMap<>();

            ArrayList<Image> images = new ArrayList<>();
            for (var category : transactionCategories) {
                images.add(new Image(Integer.toString(category.getId()), category.getIcon()));
                HashMap<String, Object> tmp = new HashMap<>() {{
                    put("categoryName", category.getTitle());
                    put("categoryIcon", category.getIcon());
                    put("totalExpense", new BigDecimal(0));
                    put("totalIncome", new BigDecimal(0));
                }};
                categoryName.put(Integer.toString(category.getId()), tmp);
            }

            for (var transaction : transactions) {
                HashMap<String, Object> tmp;
                if (categoryName.containsKey(Integer.toString(transaction.getCategory().getId()))) {
                    tmp = categoryName.get(Integer.toString(transaction.getCategory().getId()));
                    if (transaction.getType().equals("EXPENSE")) {
                        BigDecimal temp = (BigDecimal) tmp.get("totalExpense");
                        tmp.put("totalExpense", temp.add(transaction.getCost()));
                    } else {
                        BigDecimal temp = (BigDecimal) tmp.get("totalIncome");
                        tmp.put("totalIncome", temp.add(transaction.getCost()));
                    }
                } else {
                    tmp = new HashMap<>();
                    tmp.put("categoryName", transaction.getCategory().getTitle());
                    if (transaction.getType().equals("EXPENSE")) {
                        BigDecimal temp = (BigDecimal) tmp.get("totalExpense");
                        tmp.put("totalExpense", transaction.getCost());
                    } else {
                        BigDecimal temp = (BigDecimal) tmp.get("totalIncome");
                        tmp.put("totalIncome", transaction.getCost());
                    }
                }
                categoryName.put(Integer.toString(transaction.getCategory().getId()), tmp);
            }

            DropBoxRedirectedLinkGetter getter = new DropBoxRedirectedLinkGetter();
            try {
                GetRedirectedLinkExecutionResult result = getter.getRedirectedLinks(images);

                if (result != null) {
                    HashMap<String, GetRedirectedLinkTask.GetRedirectedLinkResult> success = result.getSuccessfulResults();
                    for (var category : transactionCategories) {
                        if (success.containsKey(Integer.toString(category.getId()))) {
                            HashMap<String, Object> tmp = categoryName.get(Integer.toString(category.getId()));
                            tmp.put("categoryIcon", success.get(Integer.toString(category.getId())).uri);
                            categoryName.put(Integer.toString(category.getId()), tmp);
                        }
                    }
                }

                return ResponseEntity.ok(new Response(categoryName, new ArrayList<>()));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response(null, new ArrayList<>(List.of("validation.unauthorized"))));
    }
}
