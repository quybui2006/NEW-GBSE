package demo.config;

  import org.slf4j.Logger;
  import org.slf4j.LoggerFactory;
  import org.springframework.ui.Model;
  import org.springframework.web.bind.annotation.ControllerAdvice;
  import org.springframework.web.bind.annotation.ExceptionHandler;
  import org.springframework.web.servlet.resource.NoResourceFoundException;

  @ControllerAdvice
  public class GlobalExceptionHandler {

      private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

      @ExceptionHandler(NoResourceFoundException.class)
      public String handle404(Model model) {
          return "error/404";
      }

      @ExceptionHandler(Exception.class)
      public String handle500(Exception e, Model model) {
          log.error("Unhandled exception", e);
          return "error/500";
      }
  }