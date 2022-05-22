package es.uji.ei1027.skillSharing.controller;

import es.uji.ei1027.skillSharing.dao.DemandaDao;
import es.uji.ei1027.skillSharing.dao.OfertaDao;
import es.uji.ei1027.skillSharing.dao.SkillDao;
import es.uji.ei1027.skillSharing.modelo.Demanda;
import es.uji.ei1027.skillSharing.modelo.Oferta;
import es.uji.ei1027.skillSharing.modelo.Skill;
import es.uji.ei1027.skillSharing.modelo.Usuario;
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


class OfertaValidator implements Validator {
    @Override
    public boolean supports(Class<?> cls) {
        return Oferta.class.isAssignableFrom(cls);
    }
    @Override
    public void validate(Object obj, Errors errors) {
        Oferta of = (Oferta) obj;
        if (of.getIniFecha() == null)
            errors.rejectValue("iniFecha", "empty_fechai", "Cal introduir una data de inici");
        if (of.getFinFecha() == null)
            errors.rejectValue("finFecha", "empty_fechaf", "Cal introduir una data de finalització");
        if (of.getDescripcion().equals(""))
            errors.rejectValue("descripcion","empty_descr", "Cal introduir una descripció");
        if (of.getHoras() == 0 )
            errors.rejectValue("horas","horas0","El nombre de hores no pot ser 0");
        if (of.getHoras() < 0 )
            errors.rejectValue("horas","horas_neg","El nombre de hores no pot ser negatiu");

    }
}



@Controller
@RequestMapping("/oferta")
public class OfertaController {

    private OfertaDao ofertaDao;
    private SkillDao skillDao;
    private DemandaDao demandaDao;

    @Autowired
    public void setOfertaDao(OfertaDao ofertaDao){this.ofertaDao=ofertaDao;}
    @Autowired
    public void setSkillDao(SkillDao skillDao){this.skillDao=skillDao;}
    @Autowired
    public void setDemandaDao(DemandaDao demandaDao) {this.demandaDao = demandaDao;}


    @RequestMapping(value = "/add")
    public String addOferta(HttpSession session, Model model){
        if (session.getAttribute("user") == null){
            session.setAttribute("nextUrl","/oferta/add");
            return "redirect:/login";
        }
        model.addAttribute("oferta", new Oferta());
        model.addAttribute("skills", skillDao.getSkillsActivas());
        return "oferta/add";
    }

    @RequestMapping(value="/add", method= RequestMethod.POST) //alumno
    public String processAddSubmit(HttpSession session,Model model, @ModelAttribute("oferta") Oferta oferta,
                                  BindingResult bindingResult) {
        model.addAttribute("skills", skillDao.getSkillsActivas());

        if (session.getAttribute("user") == null){
            session.setAttribute("nextUrl","/oferta/add");
            return "redirect:/login";
        }
        OfertaValidator ofertaValidator = new OfertaValidator();
        ofertaValidator.validate(oferta, bindingResult);
        if (bindingResult.hasErrors()) {
            return "oferta/add";
        }
        Usuario user = (Usuario)session.getAttribute("user");
        oferta.setEstudiante(user.getNif());
        List<Demanda> demandaAsociadaSkill = demandaDao.getDemandasAsociadasASkill(oferta.getSkill());
        ofertaDao.addOferta(oferta);
        if (demandaAsociadaSkill.isEmpty())
            return "redirect:listMisOfertas";
        else
            return "redirect:/demanda/listDemandasUser/"+ oferta.getSkill()+"/"+ofertaDao.devuelveUltimoId();
    }

    @RequestMapping(value = "/update/{idOferta}", method = RequestMethod.GET) //alumno
    public String editOferta(HttpSession session, Model model, @PathVariable String idOferta){
        if (session.getAttribute("user") == null){
            session.setAttribute("nextUrl","/oferta/update/"+idOferta);
            return "redirect:/login";
        }
        Oferta o = ofertaDao.getOferta(idOferta);
        model.addAttribute("oferta", o);
        model.addAttribute("skills", skillDao.getSkillsActivas());
        model.addAttribute("skill", o.getSkill());
        return "oferta/update";
    }

    @RequestMapping(value="/update", method = RequestMethod.POST) //alumno
    public String processUpdateSubmit(Model model, HttpSession session,@ModelAttribute("oferta") Oferta oferta,
            BindingResult bindingResult){
        if (session.getAttribute("user") == null){
            session.setAttribute("nextUrl","/oferta/update/"+oferta.getIdOferta());
            return "redirect:/login";
        }
        model.addAttribute("skills", skillDao.getSkillsActivas());
        OfertaValidator ofertaValidator = new OfertaValidator();
        ofertaValidator.validate(oferta, bindingResult);
        if (bindingResult.hasErrors()) {
            return "oferta/update";
        }
        Usuario user = (Usuario) session.getAttribute("user");
        if (!user.getNif().equals(oferta.getEstudiante())){
            return "redirect:/forbiden";
        }
        ofertaDao.updateOferta(oferta);
        return "redirect:listMisOfertas";
    }

    @RequestMapping(value = "/delete/{idOferta}")
    public String  processDeleteOferta(HttpSession session,@PathVariable String idOferta){

        if (session.getAttribute("user") == null){
            session.setAttribute("nextUrl","/oferta/delete/"+idOferta);
            return "redirect:/login";
        }
        Usuario user = (Usuario)session.getAttribute("user");
        if (!user.isSkp()){
            Oferta of = (Oferta) ofertaDao.getOferta(idOferta);
            if (user.getNif().equals(of.getEstudiante())){
                ofertaDao.endOferta(idOferta);
                return "redirect:../../listMisOfertas";
            }else{
                return "redirect:/forbiden";
            }
        }else{
            ofertaDao.endOferta(idOferta);
            return "redirect:../listSKP";
        }
    }

    @RequestMapping("/list")
    public String listOfertas(Model model){
        model.addAttribute("ofertas",ofertaDao.getOfertas());
        return "oferta/list";
    }

    @RequestMapping("/listSKP")
    public String listOfertasSKP(Model model){

        model.addAttribute("ofertas",ofertaDao.getOfertas());
        return "oferta/listSKP";
    }


    @RequestMapping("/listOfertasUser")
    public String listOfertasUser(HttpSession session, Model model){

        if (session.getAttribute("user") == null){
            session.setAttribute("nextUrl","/usuario/list");
            return "redirect:../../login";
        }
        Usuario user = (Usuario)session.getAttribute("user");

        model.addAttribute("ofertas",ofertaDao.getTodasOfertasMenosMias(user.getNif()));
        return "oferta/listOfertasUser";
    }


    @RequestMapping("/listMisOfertas")
    public String listMisOfertas(HttpSession session,Model model){
        if (session.getAttribute("user") == null){
            session.setAttribute("nextUrl","/usuario/list");
            return "login";
        }
        Usuario user = (Usuario)session.getAttribute("user");

        model.addAttribute("misOfertas",ofertaDao.getOfertasEstudiante(user.getNif()));
        model.addAttribute("oferta",new Oferta());
        return "oferta/listMisOfertas";
    }


    @RequestMapping("/buscar")
    public String listBusqueda(HttpSession session,Model model, @ModelAttribute ("oferta") Oferta oferta){
        if (session.getAttribute("user") == null){
            session.setAttribute("nextUrl","/usuario/list");
            return "login";
        }
        model.addAttribute("ofertas", ofertaDao.getOfertasAsociadasASkill(oferta.getSkill()));
        return "oferta/list";
    }




}
