package com.trunkfit.controller;

import java.security.Principal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.trunkfit.dao.AppUserDao;
import com.trunkfit.dao.CompanyDao;
import com.trunkfit.model.AppUser;
import com.trunkfit.model.Company;
import com.trunkfit.service.AmazonClient;
import com.trunkfit.service.EmailSenderService;
import com.trunkfit.service.FileService;
import com.trunkfit.utils.EncryptedPasswordUtils;
import com.trunkfit.utils.JwtUtil;
import com.trunkfit.utils.WebUtils;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;

@Controller
public class MainController {
  
  @Autowired
  private AmazonClient amazonClient;


  @Autowired
  CompanyDao companyDao;
  
  @Autowired
  AppUserDao appUserDao;
  
  @Autowired
  FileService fileService;
  
  
  @Autowired
  JwtUtil jwtUtil;
 
  @Autowired
  EmailSenderService emailService;
  
  
  private static final String SUPER_ADMIN = "ROLE_SUPER_ADMIN";
  
  private static final String ADMIN = "ROLE_ADMIN";
  
  private static final String USER = "ROLE_USER";

  
  @RequestMapping(value ="/" , method = RequestMethod.GET)
  public String homePage(Model model, Principal principal) {
    if(!isLoggedIn(principal)) 
    {
      return "redirect:/login";
    }

    return "welcomePage";
}
  public static boolean hasNecessaryRole(Principal principal, String compareRole)
  { if(isLoggedIn(principal))
  {
   User currentUser = (User) ((Authentication) principal).getPrincipal();
   return currentUser.getAuthorities().stream().filter(ga->ga.getAuthority().contentEquals(compareRole)).findAny().isPresent();
  }
  return false;
   
  }
  private static boolean isLoggedIn(Principal principal)
  {
 return principal!=null && (User) ((Authentication) principal).getPrincipal() != null;
  }
  
  @RequestMapping(value = "/login" , method = RequestMethod.GET)
  public String loginPage(Model model,RedirectAttributes redirectAttributes) {
      return "login/loginPage";
  }
  @RequestMapping(value = "/welcome" , method = RequestMethod.GET)
  public String welcomePage(Model model, HttpSession session, Principal principal) {
      model.addAttribute("title", (User) ((Authentication) principal).getPrincipal());
      model.addAttribute("message", (User) ((Authentication) principal).getPrincipal() + " /n" + isLoggedIn(principal));

      return "welcomePage";
  }

  @RequestMapping(value = "/admin", method = RequestMethod.GET)
  public String adminPage(Model model, Principal principal) {
       
      User loginedUser = (User) ((Authentication) principal).getPrincipal();
      String userInfo = WebUtils.toString(loginedUser);
      model.addAttribute("userInfo",  userInfo);
       
      return "adminPage";
  }

  @RequestMapping(value = "/createCompanyPage", method = RequestMethod.GET)
  public String createCompanyPage(Model model, Principal principal) {
       
     if(hasNecessaryRole(principal, SUPER_ADMIN))
     {
       return "company/createCompanyPage";

     }
     return "redirect:/error";
  }
  

  @RequestMapping(value = "/logoutSuccessful", method = RequestMethod.GET)
  public String logoutSuccessfulPage(Model model) {
      model.addAttribute("title", "Logout");
      return "logout/logoutSuccessfulPage";
  }

  @RequestMapping(value = "/userInfo", method = RequestMethod.GET)
  public String userInfo(Model model, Principal principal) {

      
      User loginedUser = (User) ((Authentication) principal).getPrincipal();

      String userInfo = WebUtils.toString(loginedUser);
      model.addAttribute("userInfo", userInfo);

      return "userInfoPage";
  }

   @RequestMapping(value = "/addUser", method = RequestMethod.POST)
   public String addUser(HttpServletRequest request, Model model) {

     String username = request.getParameter("email");
     String password =  request.getParameter("password");

     if(this.createUser(username, password)) {

       return "registrationComplete";
     }

     return "redirect:/error";
}
   
   @RequestMapping(value = "/test", method = RequestMethod.GET)
   public String test(HttpServletRequest request, Model model) {

     return "pb";

}
   private boolean createUser(String username, String password) {
     AppUser user = new AppUser();
     user.setEncrytedPassword(EncryptedPasswordUtils.encrytePassword(password));
     user.setUserName(username);
    return this.appUserDao.addUser(user);
   }
   @RequestMapping(value = "/register", method = RequestMethod.POST)
   public String registerUser(HttpServletRequest request, Model model,RedirectAttributes redirectAttributes) {

     String username = request.getParameter("email");
     String password =  request.getParameter("password");
  
     AppUser existingUser = this.appUserDao.findUserAccountByUsername(username);
     if(existingUser==null)
     {
       String token =this.jwtUtil.generateToken(username, false);
       AppUser user = new AppUser();
       user.setEncrytedPassword(EncryptedPasswordUtils.encrytePassword(password));
       user.setUserName(username);
       user.setEnabled(true);
       user.setVerified(false);
       user.setConfirmationToken(token);
       if(this.appUserDao.addUser(user)) {         
         System.out.println("creating it");
         sendEmail(username,token);
        redirectAttributes.addFlashAttribute("registrationConfirmation", username + " has successfully been registered, please login to your email and click the verification link. The link will only be valid for 24 hours");
       
       }
     }
     else {
       System.out.println("error");
       redirectAttributes.addFlashAttribute("registrationError", "Email " + username + " is already registered, please use Forgot Password to recover account ");
     }

    return "redirect:login";
}

private  void sendEmail(String userName,String token) {
  SimpleMailMessage mailMessage = new SimpleMailMessage();
  mailMessage.setTo(userName);
  mailMessage.setSubject("Complete Registration!");
  mailMessage.setFrom("playgroundwordpress@gmail.com");
  mailMessage.setText("To confirm your account, please click here : "
  +"http://localhost:5000/confirm/"+token);
 this.emailService.sendEmail(mailMessage);
}
   @RequestMapping(value = "/addCompany", method = RequestMethod.POST)
   @Secured(SUPER_ADMIN)
   public String addCompany(HttpServletRequest request, Model model,  RedirectAttributes redirectAttributes) {

     String companyName = request.getParameter("companyName");
     
     Company company = new Company();
     company.setCompanyName(companyName);
     System.out.println(companyName + " attirbute");
     if(this.companyDao.addCompany(company)) {
       createSuccessMessage(model,"Company","Creat");
       model.addAttribute("companyMessage" ,"Success");
       redirectAttributes.addFlashAttribute("companyMessage", "Successful!");
       return "redirect:/getCompanies";
     }

   return "redirect:/error";
}
   private static void createSuccessMessage(Model model, String object, String event)
   {
     StringBuilder  sb= new StringBuilder();
     sb.append(object).append(" has successfully been ").append(event).append("ed");
     model.addAttribute("actionMessage" ,sb.toString());
   }
   @RequestMapping(value = "/addCompanyPage", method = RequestMethod.GET)
   @Secured(SUPER_ADMIN)
   public String addCompanyPage(HttpServletRequest request, Model model) {
   return "company/createCompanyPage";
}
   @RequestMapping(value = "/getCompanies", method = RequestMethod.GET)
   @Secured({SUPER_ADMIN, ADMIN})
   public String getCompanies(HttpServletRequest request, Model model, Principal principal) {
    model.addAttribute("companies",this.companyDao.getCompanies());
    return "company/companies";
}
   @RequestMapping(value = "/error", method = RequestMethod.POST)
   public String errorPage(HttpServletRequest request, Model model) {
   return "failure/403Page";
}
   
   @RequestMapping(value = "/confirm/{token}", method=RequestMethod.GET)
   public String confirmRegistrationToken(@PathVariable String token,RedirectAttributes redirectAttributes){
     AppUser userByToken= this.appUserDao.findUserAccountByConfirmationToken(token);

     if(userByToken==null)
     {
       System.out.println("getConfirmationToken() " + userByToken.getConfirmationToken());

     }
     else {
       String userName=this.jwtUtil.extractUsername(token);
       this.appUserDao.activateUser(userName);
       this.appUserDao.removeToken(userName);
       redirectAttributes.addFlashAttribute("verified", "You've successfully been verified, please login");
   }
     return "redirect:../login";
   }
   



   @RequestMapping(value = "/uploadFile", method=RequestMethod.POST)
   public String uploadFile(@RequestPart(value = "file") MultipartFile file) {
       return this.amazonClient.uploadFile(file);
   }
   
}
