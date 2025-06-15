module CourseWorkServer {
    requires java.sql;
    requires jdk.jdi;
    requires jakarta.mail;
    exports com.courcach.Server.Services;
    exports com.courcach.Server.Services.ClassesForRequests;
    exports com.courcach.Server.Services.Admin;
    exports com.courcach.Server.Services.Client;
    exports com.courcach.Server.Services.Employee;
    exports com.courcach.Server.Services.User.Auth;
    exports com.courcach.Server.Services.User.ForgotPassword;
}
