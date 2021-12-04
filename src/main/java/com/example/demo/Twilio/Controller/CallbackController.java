package com.example.demo.Twilio.Controller;

import com.example.demo.Communication.Call.Entity.UserInCallRoom;
import com.example.demo.Communication.Call.Service.UserInCallRoomService;
import com.example.demo.Communication.RequestBody.CallbackReqBody;
import com.example.demo.Communication.RequestBody.UsersInChatRoomReqBody;
import com.example.demo.DropBox.DropBoxRedirectedLinkGetter;
import com.example.demo.DropBox.GetRedirectedLinkExecutionResult;
import com.example.demo.ExpensesAndIncomes.TransactionCategoryGroup.RequestBody.RoomSizeReqBody;
import com.example.demo.Firebase.FirebaseMessageHelper;
import com.example.demo.Helpers.FamilyHelper;
import com.example.demo.Helpers.Helper;
import com.example.demo.ResponseFormat.Response;
import com.example.demo.Service.Family.FamilyService;
import com.example.demo.Service.UserService;
import com.example.demo.Twilio.TwilioAccessTokenProvider;
import com.example.demo.domain.CustomUserDetails;
import com.example.demo.domain.Family.Family;
import com.example.demo.domain.Image;
import com.example.demo.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api/v1/callback")
@Slf4j
public class CallbackController {
    @Autowired
    private UserInCallRoomService userInCallRoomService;

    @Autowired
    private UserService userService;

    @Autowired
    private FirebaseMessageHelper firebaseMessageHelper;

    @Autowired
    private FamilyService familyService;

    @Autowired
    private FamilyHelper familyHelper;

    @Autowired
    private TwilioAccessTokenProvider twilioAccessTokenProvider;

    @PostMapping
    public ResponseEntity<Response> callback(@RequestParam(value = "RoomName", defaultValue = "") String RoomName,
                                             @RequestParam(value = "ParticipantIdentity", defaultValue = "") String ParticipantIdentity,
                                             @RequestParam(value = "StatusCallbackEvent", defaultValue = "") String StatusCallbackEvent,
                                             @RequestParam(value = "AccountSid", defaultValue = "") String AccountSid,
                                             @RequestParam(value = "RoomSid", defaultValue = "") String RoomSid,
                                             @RequestParam(value = "RoomStatus", defaultValue = "") String RoomStatus,
                                             @RequestParam(value = "RoomType", defaultValue = "") String RoomType,
                                             @RequestParam(value = "Timestamp", defaultValue = "") String Timestamp,
                                             @RequestParam(value = "ParticipantStatus", defaultValue = "") String ParticipantStatus,
                                             @RequestParam(value = "ParticipantSid", defaultValue = "") String ParticipantSid,
                                             @RequestParam(value = "ParticipantDuration", defaultValue = "") String ParticipantDuration,
                                             @RequestParam(value = "RoomDuration", defaultValue = "") String RoomDuration,
                                             @RequestParam(value = "SequenceNumber", defaultValue = "") String SequenceNumber,
                                             @RequestParam(value = "ParticipantTrackSidStatus", defaultValue = "") String ParticipantTrackSidStatus,
                                             @RequestParam(value = "TrackKind", defaultValue = "") String TrackKind) {
        log.info("Handling twilio callback ... " + StatusCallbackEvent);

        String roomName = RoomName;
        int familyId = Integer.parseInt(roomName.split("_")[1]);
        Family family = familyService.findById(familyId);
        Helper helper = Helper.getInstance();
        String langCode = helper.getLangCode(family);

        if (StatusCallbackEvent.equals("participant-connected")) {
            int userId = Integer.parseInt(ParticipantIdentity);
            User user = userService.getUserById(userId);

            UserInCallRoom userInCallRoom = new UserInCallRoom(roomName, user);
            userInCallRoomService.saveUserInCallRoom(userInCallRoom);
        }
//        } else if (StatusCallbackEvent.equals("participant-disconnected")) {
//            int userId = Integer.parseInt(ParticipantIdentity);
//            User user = userService.getUserById(userId);
//
//            userInCallRoomService.deleteUserFromRoom(roomName, userId);
//        }
//        } else if (StatusCallbackEvent.equals("room-ended")) {
//            ArrayList<Integer> userIds = userInCallRoomService.findAllUserIdInRoomCall(roomName);
//            List<User> users = userIds.stream().map(id -> userService.getUserById(id)).collect(Collectors.toList());
//
//            firebaseMessageHelper.notifyUsers(
//                    users,
//                    family,
//                    helper.getMessageInLanguage("videoCallHasEndedTitle", langCode),
//                    helper.getMessageInLanguage("videoCallHasEndedBody", langCode),
//                    new HashMap<>() {{
//                        put("navigate", "END_VIDEO_CALL");
//                        put("id", roomName);
//                    }}
//            );
//        }

        return ResponseEntity.ok(new Response(null, new ArrayList<>()));
    }

    @PostMapping("/room_size")
    public ResponseEntity<Response> checkRoomSize(@Valid @RequestBody RoomSizeReqBody reqBody) {
        Family family = familyService.findById(reqBody.familyId);
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();

        if (family.checkIfUserExist(user)) {
            userInCallRoomService.deleteUserFromRoom(reqBody.roomName, user.getId());
            int roomSize = userInCallRoomService.countPeopleLeftInRoom(reqBody.roomName);

            return ResponseEntity.ok(new Response(new HashMap<>() {{
                put("roomSize", roomSize);
            }}, new ArrayList<>()));
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response(
                new HashMap<String, String>() {{
                    put("familyName", family.getFamilyName());
                }},
                new ArrayList<>(List.of("validation.unauthorized"))));
    }

    @PostMapping("/end_room")
    public ResponseEntity<Response> endRoom(@Valid @RequestBody RoomSizeReqBody reqBody) {
        Family family = familyService.findById(reqBody.familyId);
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();

        if (family.checkIfUserExist(user)) {
            log.info("end room " + reqBody.roomName);
            String sid = twilioAccessTokenProvider.getRoomSid(reqBody.roomName);

            if (sid != null) {
                twilioAccessTokenProvider.endRoom(sid);
            }

            return ResponseEntity.ok(new Response("End room successfully.", new ArrayList<>()));
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response(
                new HashMap<String, String>() {{
                    put("familyName", family.getFamilyName());
                }},
                new ArrayList<>(List.of("validation.unauthorized"))));
    }

    @PostMapping("/getUsersInChatRoom")
    public ResponseEntity<Response> usersInChatRoom(@Valid @RequestBody UsersInChatRoomReqBody reqBody) {
        Family family = familyService.findById(reqBody.familyId);
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();

        if (family.checkIfUserExist(user)) {
            ArrayList<HashMap<String, Object>> data = new ArrayList<>();
            List<User> users = family.getUsersInFamily().stream().map(userInFamily -> userInFamily.getUser()).collect(Collectors.toList());

            DropBoxRedirectedLinkGetter getter = new DropBoxRedirectedLinkGetter();
            try {
                GetRedirectedLinkExecutionResult result = getter.getRedirectedLinks(new ArrayList<>(users.stream().map(user1 -> {
                    return new Image(user1.getName(), user1.getAvatar());
                }).collect(Collectors.toList())));

                if (result != null) {
                    data = new ArrayList<>(users.stream()
                            .map(user1 -> {
                                String status = (userInCallRoomService.checkIfUserIsInAVideoCall(user1.getId())) ? "BUSY" : "AVAILABLE";
                                return (result.getSuccessfulResults().containsKey(user1.getName())) ?
                                        user1.getShortJson(result.getSuccessfulResults().get(user1.getName()).getUri(), status) :
                                        user1.getShortJson(null, status);
                            }).collect(Collectors.toList()));

                    return ResponseEntity.ok(new Response(data, new ArrayList<>()));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            for (var userInFamily : family.getUsersInFamily()) {
                if (userInCallRoomService.checkIfUserIsInAVideoCall(userInFamily.getUser().getId())) {
                    data.add(userInFamily.getUser().getShortJson(null, "BUSY"));
                } else {
                    data.add(userInFamily.getUser().getShortJson(null, "AVAILABLE"));
                }
            }

            return ResponseEntity.ok(new Response(data, new ArrayList<>()));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(
                new HashMap<String, String>() {{
                    put("familyName", family.getFamilyName());
                }},
                new ArrayList<>(List.of("validation.unauthorized"))));
    }
}
