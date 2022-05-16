package es.uji.ei1027.skillSharing.controller;

import es.uji.ei1027.skillSharing.dao.EstudianteDao;
import es.uji.ei1027.skillSharing.dao.UsuarioDao;
import es.uji.ei1027.skillSharing.modelo.Usuario;
import org.jasypt.util.password.BasicPasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;
import java.util.List;

class UsuarioValidator implements Validator {
    @Override
    public boolean supports(Class<?> cls) {
        return Usuario.class.equals(cls);
    }
    @Override
    public void validate(Object obj, Errors errors) {
        Usuario user = (Usuario) obj;

        if (user.getUsername().trim().equals("")) {
            errors.rejectValue("username", "empty_username",
                    "Cal introduir un nom d' usuari");
        }
        if (user.getPassword().trim().equals("")) {
            errors.rejectValue("password", "empty_password",
                    "Cal introduir una contrasenya");
        }

        if (user.getNif().trim().equals("")) {
            errors.rejectValue("nif", "empty_nif",
                    "Cal introduir un nif");
        }
    }
}
@Controller
@RequestMapping("/usuario")
public class UserController {
    private UsuarioDao usuarioDao;
    private EstudianteDao estudianteDao;
    @Autowired
    public void setUsuarioDao(UsuarioDao usuarioDao) {
        this.usuarioDao=usuarioDao;
    }
    @Autowired
    public void setEstudianteDao(EstudianteDao estudianteDao) {
        this.estudianteDao = estudianteDao;
    }
    @RequestMapping("/list")
    public String listUsuarios(HttpSession session, Model model) {
        if (session.getAttribute("user") == null){
            session.setAttribute("nextUrl","/usuario/list");
            model.addAttribute("usuario", new Usuario());
            return "login";
        }
        Usuario user = (Usuario) session.getAttribute("usuario");
        if (!user.isSkp()){
            return "redirect:/forbiden";
        }
        model.addAttribute("usuarios", usuarioDao.getUsuarios());
        return "usuario/list";
    }
    @RequestMapping(value="/add")
    public String addUsuario(HttpSession session,Model model) {
        if (session.getAttribute("user") == null){
            session.setAttribute("nextUrl","/usuario/add");
            return "redirect:/login";
        }
        Usuario user = (Usuario)session.getAttribute("user");
        if (!user.isSkp()){
            return "redirect:/forbiden";
        }
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("estudiantes",estudianteDao.getEstudiantesInactivos());
        return "usuario/add";
    }
    @RequestMapping(value="/add", method= RequestMethod.POST)
    public String processAddSubmit(HttpSession session,Model model,@ModelAttribute("usuario") Usuario usuario,
                                   BindingResult bindingResult) {
        if (session.getAttribute("user") == null){
            session.setAttribute("nextUrl","/usuario/add");
            return "redirect:/login";
        }
        Usuario user = (Usuario)session.getAttribute("user");
        if (!user.isSkp()){
            return "redirect:/forbiden";
        }
        UsuarioValidator usuarioValidator = new UsuarioValidator();
        usuarioValidator.validate(usuario, bindingResult);
        model.addAttribute("estudiantes",estudianteDao.getEstudiantesInactivos());

        if (bindingResult.hasErrors())
            return "usuario/add";
        //System.out.println(usuario.toString());
        List<Usuario> lu = usuarioDao.getUsuarios();
        for (Usuario u : lu){
            if (u.getUsername().equals(usuario.getUsername().trim())){
                bindingResult.rejectValue("username", "repeated_user","El usuari ja existeix");
                return "usuario/add";
            }
        }
        BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
        usuario.setPassword(passwordEncryptor.encryptPassword(usuario.getPassword()));
        usuarioDao.addUsuario(usuario);
        return "redirect:list";
    }
    @RequestMapping(value="/update/{username}", method = RequestMethod.GET)
    public String editUsuario(HttpSession session, Model model, @PathVariable String username) {
        if (session.getAttribute("user") == null){
            session.setAttribute("nextUrl","/usuario/update");
            return "redirect:/login";
        }
        Usuario user = (Usuario)session.getAttribute("user");
        if (!user.isSkp()){
            return "redirect:/forbiden";
        }
        model.addAttribute("usuario", usuarioDao.getUsuario(username));
        session.setAttribute("old_usuario", usuarioDao.getUsuario(username));
        return "usuario/update";
    }
    @RequestMapping(value="/update", method = RequestMethod.POST)
    public String processUpdateSubmit(HttpSession session,@ModelAttribute("usuario") Usuario usuario,
            BindingResult bindingResult) {
        if (session.getAttribute("user") == null){
            session.setAttribute("nextUrl","/usuario/update");
            return "redirect:/login";
        }
        Usuario user = (Usuario) session.getAttribute("user");
        if (!user.isSkp()){
            return "redirect:/forbiden";
        }
        Usuario old_usuario = (Usuario) session.getAttribute("old_usuario");
        if (!old_usuario.getUsername().equals(usuario.getUsername())){
            return "redirect/forbiden";
        }
        if (!old_usuario.getNif().equals(usuario.getNif())){
            return "redirect/forbiden";
        }
        if (!old_usuario.getPassword().equals(usuario.getPassword())){
            return "redirect/forbiden";
        }
        if (bindingResult.hasErrors())
            return "usuario/update";
        usuarioDao.updateUsuario(usuario);
        session.removeAttribute("old_usuario");
        return "redirect:list";
    }
    @RequestMapping(value="/delete/{username}")
    public String processDelete(HttpSession session, @PathVariable String username) {
        if (session.getAttribute("user") == null){
            session.setAttribute("nextUrl","/usuario/list");//el nexturl esta bien, no cambiar
            return "redirect:/login";
        }
        Usuario user = (Usuario)session.getAttribute("user");
        if (!user.isSkp()){
            return "redirect:/forbiden";
        }
        usuarioDao.deleteUsuario(username);
        return "redirect:../list";
    }
    @RequestMapping(value="/passwd/{username}", method = RequestMethod.GET)
    public String editPassword(HttpSession session, Model model, @PathVariable String username) {
        if (session.getAttribute("user") == null){
            session.setAttribute("nextUrl","/usuario/passwd/"+username);
            return "redirect:/login";
        }
        Usuario user = (Usuario)session.getAttribute("user");
        if (!user.isSkp()){
            return "redirect:/forbiden";
        }
        model.addAttribute("usuario", usuarioDao.getUsuario(username));
        return "usuario/passwd";
    }
    @RequestMapping(value="/passwd", method = RequestMethod.POST)
    public String processPasswdSubmit(HttpSession session, @ModelAttribute("usuario") Usuario usuario,
                                      BindingResult bindingResult) {
        if (session.getAttribute("user") == null){
            session.setAttribute("nextUrl","/usuario/passwd/"+usuario.getUsername());
            return "redirect:/login";
        }
        Usuario user = (Usuario)session.getAttribute("user");
        if (!user.isSkp()){
            return "redirect:/forbiden";
        }
        if (bindingResult.hasErrors())
            return "usuario/passwd";
        BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
        usuario.setPassword(passwordEncryptor.encryptPassword(usuario.getPassword()));
        usuarioDao.updateUsuario(usuario);
        return "redirect:update/"+usuario.getUsername();
    }

    // TODO Hay que realizar el controlador del estudiante (alex:creo q hay, a lo mejor hay q añadir cosas claro)
}
