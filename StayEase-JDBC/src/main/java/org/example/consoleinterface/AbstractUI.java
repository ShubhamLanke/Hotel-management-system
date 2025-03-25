package org.example.consoleinterface;


import org.example.entity.User;
import org.example.utility.Response;

import java.util.List;

public abstract class AbstractUI {
//
//    protected final AuthenticationService authenticationService;
//
//    protected AbstractUI(AuthenticationService authenticationService) {
//        this.authenticationService = authenticationService;
//    }
//
//    public Response login() {
//        return authenticationService.login();
//    }
//
//    public boolean validateUser(User user) {
//        return authenticationService.validate(user);
//    }
//
//    public void displayOptions(List<String> options) {
//        options.forEach(System.out::println);
//    }
//

//    public abstract void adminPortal();
//    public abstract void userPortal();
//}
//

//class AdminUI extends AbstractUI {
//
//    public AdminUI(AuthenticationService authenticationService) {
//        super(authenticationService);
//    }
//
//    @Override
//    public void adminPortal() {
//        Response response = login();
//        if (response.isSuccess()) {
//            displayOptions(List.of("1. Manage Rooms", "2. Manage Users", "3. View Reports", "4. Logout"));
//            // Implement actual admin logic
//        } else {
//            System.out.println("Invalid Admin Credentials");
//        }
//    }
//
//    @Override
//    public void userPortal() {
//        throw new UnsupportedOperationException("Admin cannot access user portal");
//    }
//}
//

//class UserUI extends AbstractUI {
//
//    public UserUI(AuthenticationService authenticationService) {
//        super(authenticationService);
//    }
//
//    @Override
//    public void adminPortal() {
//        throw new UnsupportedOperationException("User cannot access admin portal");
//    }
//
//    @Override
//    public void userPortal() {
//        Response response = login();
//        if (response.isSuccess()) {
//            displayOptions(List.of("1. Book Room", "2. View Bookings", "3. Generate Invoice", "4. Logout"));
//            // Implement actual user logic
//        } else {
//            System.out.println("Invalid User Credentials");
//        }
//    }
}
