# Spring Boot Platform

Base platform for my Spring Boot projects, includes several essential platform additions useful for any Spring Boot in
general. Feel free to use these platform additions in your personal projects as well.

## Features

### Task Scope

A neat additional Spring CDI scope that acts like a Request scope, but can also be used outside HTTP requests, e.g. when
performing asynchronous or scheduled background operations. Arbitrary workload can be executed in Task scopes, and they
even can be nested. It also shines in tests, where you can start a task scope before the tests and stop it afterwards,
or do so multiple times within the same test (e.g. to test with different users).

The `TaskScopeRequestScopeFilter` will make sure that HTTP requests are executed within a new Task scope, so it
effectively becomes a more powerful replacement for Spring's Request scope.

Usage:

```java

@Component
@Scope(value = "task", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class TaskScopedComponent {
    // each task scope will have its own instance of this component.
}
```

Code can be executed in a new task scope execution context (also nesting possible):
```
TaskScope.newExecutionContext().execute(() -> {
    // code that runs in a task scope
});
```

The current execution context of a task scope can also be propagated to other Threads when doing parallel processing:

```
// start a new task scope in the current thread
TaskScope.init();
try {
    // get access to the current task scope's execution contect
    TaskScope.ExecutionContext ctx = TaskScope.currentExecutionContext();
    
    // load some data, parallel process it using the shared task scope
    someStreamOfData
        .parallel()
        .forEach(data -> ctx.execute(process(data)))
    ...
} finally {
    // stop the task scope, and release all beans associated with it
    TaskScope.destroy();
}
```

### Access Logging

The `AccessLogFilter`: logs the REST API calls (method, path, duration, status). Realized as a **Servlet Filter**.

It will log the following values per request:

- the request **method** (GET, POST, PUT, DELETE,...)
- the request **URI**
- the response **HTTP status** (numeric and string identifier)
- the **duration** of the call

Some of these values will be added to the **MDC** (_Message Diagnostic Context_):

- `ACCESS_LOG_METHOD`
- `ACCESS_LOG_URI`
- `ACCESS_LOG_STATUS`
- `ACCESS_LOG_DURATION_MS`

Example output:

```text
2021-09-21 21:34:48.977 - POST /api/notes -> 201 CREATED, 40.80 ms
2021-09-21 21:34:48.991 - GET /api/notes/1101 -> 200 OK, 4.09 ms
2021-09-21 21:34:48.999 - GET /api/notes -> 200 OK, 5.70 ms
2021-09-21 21:34:49.007 - PUT /api/notes/1101 -> 204 NO_CONTENT, 3.62 ms
2021-09-21 21:34:49.012 - GET /api/notes/1101 -> 200 OK, 2.41 ms
2021-09-21 21:34:49.018 - DELETE /api/notes/1101 -> 204 NO_CONTENT, 4.15 ms
2021-09-21 21:34:49.020 - DELETE /api/notes/1101 -> 204 NO_CONTENT, 1.25 ms
2021-09-21 21:34:49.025 - GET /api/notes -> 200 OK, 2.73 ms
2021-09-21 21:34:49.030 - GET /api/notes/1101 -> 404 NOT_FOUND, 3.53 ms
2021-09-21 21:34:49.080 - POST /api/notes -> 400 BAD_REQUEST, 8.51 ms
2021-09-21 21:34:49.125 - POST /api/notes -> 400 BAD_REQUEST, 3.01 ms
```

Very useful if you want to have a quick look at the traffic, or need to efficiently identify failed requests in a log
aggregator (filter for access log entries, the get the request id to find other log statements in the context of that
call).

### Performance Logging

The `PerformanceLoggingAspect` logs the invocation tree and times for performance analysis. Realized using **AOP**(_
Aspect-Oriented Programming_).

It is activated around invocations of the following types:

- any `@Controller` and `@RestController` invocation
- any `@Service` invocation
- any `@Repository` invocation
- any other invocation annotated with the provided `@PerformanceLogging` annotation.

Performance logging output:

```text
NotesController.update(..) -> 2.13 ms, self: 0.91 ms
  + NoteServiceImpl.save(..) -> 1.22 ms, self: 0.30 ms
    + CrudRepository.findById(..) -> 0.77 ms
    + CrudRepository.save(..) -> 0.15 ms
```

### JWT authentication

Authentication using **JWT** (JSON Web Tokens). The `BearerTokenAuthenticationFilter` will use a JWT supplied using the
`Autentication: Bearer {JWT}` request header to identify the user for a request, validate the JWT (signature, expiry
date/time)
and then make that information available to all services during that request.

The user information is exposed as a (immutable) `UserInfo` object, containing:

- the `login` (login id, user principal, extracted from the `sub` (subject) claim).
- the optional `tenant` (for multi-tenant applications, extracted from the optional `tenant` claim).
- information whether this user `isAuthenticated` or `isAnonymous` (mutually exclusive).
- the `roles` of a user (extracted from the optional `score` claim, list of identifiers).
- all `additionalClaims` of that user (all provided claims, without the previously extracted claims for tenant and
  roles).

To obtain the `UserInfo`, use the `UserInfoProvider`:

```
@Autowired
private UserInfoProvider userInfoProvider;

public void businessLogic() {
    UserInfo userInfo=userInfoProvider.get();
    // use the userInfo tenant to resolve tenant-specific configuration
    // use the userInfo tenant and login to load data for that user
}
```

During **development** time, two REST endpoints can be activated to work with JWTs (for local testing):

- `LoginController`, `dev/login`: can issue valid JWTs for testing. <br>The tenant, login, roles and additional claims,
  as well as the validity (valid-from and duration) can be passed as arguments, to create arbitrary tokens, even expired
  ones or some that are not yet valid.
- `UserInfoController`, `dev/user`: can be called by clients to obtain information about the logged in user.

# Modules

Published modules:

- `spring-boot-platform-api`: API classes you may use in your own code
- `spring-boot-platform-core`: CORE platform module, contains the Task Scope, Access Log and Performance Log
  functionality
- `spring-boot-platform-jwt`: JWT platform additional module, for JWT authentication

Internal modules:

- `test-app`: example Spring Boot application using the platform

# Build

Build using the Gradle Wrapper:

```shell
./gradlew
```