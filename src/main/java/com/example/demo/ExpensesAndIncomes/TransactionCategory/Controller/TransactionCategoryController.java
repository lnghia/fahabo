package com.example.demo.ExpensesAndIncomes.TransactionCategory.Controller;

import com.dropbox.core.v2.DbxClientV2;
import com.example.demo.DropBox.*;
import com.example.demo.ExpensesAndIncomes.TransactionCategory.Entity.TransactionCategory;
import com.example.demo.ExpensesAndIncomes.TransactionCategory.RequestBody.CreateTransactionCategoryReqBody;
import com.example.demo.ExpensesAndIncomes.TransactionCategory.RequestBody.DeleteTransactionCategoryReqBody;
import com.example.demo.ExpensesAndIncomes.TransactionCategory.RequestBody.GetTransactionCategoryReqBody;
import com.example.demo.ExpensesAndIncomes.TransactionCategory.Service.TransactionCategoryService;
import com.example.demo.ExpensesAndIncomes.TransactionCategoryGroup.Entity.TransactionCategoryGroup;
import com.example.demo.ExpensesAndIncomes.TransactionCategoryGroup.Service.TransactionCategoryGroupService;
import com.example.demo.Helpers.Helper;
import com.example.demo.ResponseFormat.Response;
import com.example.demo.Service.Family.FamilyService;
import com.example.demo.domain.*;
import com.example.demo.domain.Family.Family;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api/v1/transaction_category")
@Slf4j
public class TransactionCategoryController {
    @Autowired
    private TransactionCategoryService transactionCategoryService;

    @Autowired
    private FamilyService familyService;

    @Autowired
    private TransactionCategoryGroupService transactionCategoryGroupService;

    @Autowired
    private DropBoxHelper dropBoxHelper;

    @PostMapping
    public ResponseEntity<Response> getCategories(@Valid @RequestBody GetTransactionCategoryReqBody reqBody) {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        Family family = familyService.findById(reqBody.familyId);

        log.info(String.format("Getting all transaction categories user: %d family: %d", user.getId(), reqBody.familyId));
        if (family.checkIfUserExist(user)) {
            ArrayList<TransactionCategory> categories = transactionCategoryService.findAll(reqBody.familyId, reqBody.type);
            ArrayList<HashMap<String, Object>> data = new ArrayList<>();
            List<String> icons = categories.stream().map(category -> {
                return category.getIcon();
            }).collect(Collectors.toList());

            DropBoxRedirectedLinkGetter getter = new DropBoxRedirectedLinkGetter();

            try {
                GetRedirectedLinkExecutionResult executionResult = getter.getRedirectedLinks(new ArrayList<>(categories.stream().map(category -> {
                    return new Image(Integer.toString(category.getId()), category.getIcon());
                }).collect(Collectors.toList())));

                if (executionResult != null) {
                    HashMap<String, GetRedirectedLinkTask.GetRedirectedLinkResult> success = executionResult.getSuccessfulResults();
                    for (var category : categories) {
                        String iconName = Integer.toString(category.getId());

                        data.add(category.getJson(
                                success.containsKey(iconName) ? success.get(iconName).getUri() : null)
                        );
                    }

                    return ResponseEntity.ok(new Response(data, new ArrayList<>()));
                }

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response(null, new ArrayList<>(List.of("unknownError"))));
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response(null, new ArrayList<>(List.of("unknownError"))));
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response(
                null,
                new ArrayList<>(
                        List.of("validation.unauthorized")
                )
        ));
    }

    @PostMapping("/create")
    public ResponseEntity<Response> createCategory(@Valid @RequestBody CreateTransactionCategoryReqBody reqBody) {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        Family family = familyService.findById(reqBody.familyId);
        Date now = new Date();

        log.info(String.format("Creating transaction category user: %d family: %d", user.getId(), reqBody.familyId));
        if (family.checkIfUserExist(user)) {
            ItemToUpload[] items = new ItemToUpload[1];
            items[0] = new ItemToUpload(reqBody.name + "_" + Integer.toString(reqBody.familyId) + "_" + now.getTime() + ".jpg", reqBody.icon);

            UploadResult result = null;
            try {
                result = dropBoxHelper.uploadImages(items, 1, 1);
                ArrayList<Image> success = result.successUploads;

                String iconUrl = (success != null && !success.isEmpty()) ? success.get(0).getMetadata().getUrl() : null;
                String iconReadyToViewUrl = (success != null && !success.isEmpty()) ? success.get(0).getUri() : null;

//                TransactionCategoryGroup transactionCategoryGroup = transactionCategoryGroupService.findById(Integer.parseInt(reqBody.parentId));
                TransactionCategory transactionCategory = new TransactionCategory(reqBody.name, reqBody.familyId, iconUrl);
                transactionCategory.setType(reqBody.type);
                transactionCategoryService.save(transactionCategory);

                return ResponseEntity.ok(new Response(transactionCategory.getJson(iconReadyToViewUrl), new ArrayList<>()));
            } catch (ExecutionException | InterruptedException e) {
                log.error("Failed to upload image in creating category");
                log.error(e.getMessage());
                e.printStackTrace();
            }

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response(null, new ArrayList<>(List.of("unknownError"))));
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response(
                null,
                new ArrayList<>(
                        List.of("validation.unauthorized")
                )
        ));
    }

    @PostMapping("/delete")
    public ResponseEntity<Response> deleteTransactionCategory(@RequestBody DeleteTransactionCategoryReqBody reqBody) {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        TransactionCategory transactionCategory = transactionCategoryService.findById(reqBody.categoryId);

        if (transactionCategory != null) {
            Family family = familyService.findById(transactionCategory.getFamilyId());
            if (family.checkIfUserExist(user)) {
                transactionCategory.setDeleted(true);
                transactionCategoryService.save(transactionCategory);

                return ResponseEntity.ok(new Response(new HashMap<>() {{
                    put("categoryId", reqBody.categoryId);
                }}, new ArrayList<>()));
            }

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response(null, new ArrayList<>(List.of("validation.unauthorized"))));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(null, new ArrayList<>(List.of("validation.transactionIdNotExist"))));
    }
}
