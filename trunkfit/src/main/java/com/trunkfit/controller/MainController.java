package com.trunkfit.controller;

import java.security.Principal;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.trunkfit.dao.AppUserDao;
import com.trunkfit.dao.CompanyDao;
import com.trunkfit.model.AppUser;
import com.trunkfit.model.Company;
import com.trunkfit.model.Greeting;
import com.trunkfit.utils.EncryptedPasswordUtils;
import com.trunkfit.utils.WebUtils;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;

@Controller
public class MainController {
  

  
  @Autowired
  CompanyDao companyDao;
  
  @Autowired
  AppUserDao appUserDao;
  
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
  public String loginPage(Model model) {
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

  @RequestMapping(value = "/createCompany", method = RequestMethod.GET)
  public String createCompany(Model model, Principal principal) {
       
     if(hasNecessaryRole(principal, SUPER_ADMIN))
     {
       return "createCompany";

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

     AppUser user = new AppUser();
     user.setEncrytedPassword(EncryptedPasswordUtils.encrytePassword(password));
     user.setUserName(username);
     if(this.appUserDao.addUser(user)) {

       return "registrationComplete";
     }

     return "redirect:/error";
}
   

   @RequestMapping(value = "/addCompany", method = RequestMethod.POST)
   @Secured(SUPER_ADMIN)
   public String addCompany(HttpServletRequest request, Model model) {

     String companyName = request.getParameter("companyName");
     
     Company company = new Company();
     company.setCompanyName(companyName);
     System.out.println(companyName + " attirbute");
     if(this.companyDao.addCompany(company)) {
       createSuccessMessage(model,"Company","Creat");
       return "success/successPage";
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
   return "company/createCompany";
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
   
   
}
