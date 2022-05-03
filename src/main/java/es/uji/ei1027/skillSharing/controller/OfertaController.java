package es.uji.ei1027.skillSharing.controller;

import es.uji.ei1027.skillSharing.dao.OfertaDao;
import es.uji.ei1027.skillSharing.dao.SkillDao;
import es.uji.ei1027.skillSharing.modelo.Oferta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/oferta")
public class OfertaController {

    private OfertaDao ofertaDao;
    private SkillDao skillDao;

    @Autowired
    public void setOfertaDao(OfertaDao ofertaDao){this.ofertaDao=ofertaDao;}
    @Autowired
    public void setSkillDao(SkillDao skillDao){this.skillDao=skillDao;}

    @RequestMapping(value = "/add")
    public String addOferta(Model model){
        model.addAttribute("oferta", new Oferta());
        model.addAttribute("skills", skillDao.getSkillsActivas());
        return "oferta/add";
    }

    @RequestMapping(value="/add", method= RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("oferta") Oferta oferta,
                                  BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            System.out.println(oferta);
            return "oferta/add";
        }
        ofertaDao.addOferta(oferta);
        return "redirect:list";
    }

    @RequestMapping(value = "/update/{idOferta}", method = RequestMethod.GET)
    public String editOferta(Model model, @PathVariable String idOferta){
        Oferta o = ofertaDao.getOferta(idOferta);
        model.addAttribute("oferta", o);
        model.addAttribute("skills", skillDao.getSkillsActivas());
        model.addAttribute("skill", o.getSkill());
        return "oferta/update";
    }

    @RequestMapping(value="/update", method = RequestMethod.POST)
    public String processUpdateSubmit(
            @ModelAttribute("oferta") Oferta oferta,
            BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return "oferta/update";
        }
        ofertaDao.updateOferta(oferta);
        return "redirect:list";
    }

    @RequestMapping(value = "/delete/{idOferta}")
    public String  processDeleteOferta(@PathVariable String idOferta){
        ofertaDao.endOferta(idOferta);
        return "redirect:../../list";
    }

    @RequestMapping("/list")
    public String listOfertas(Model model){
        model.addAttribute("ofertas",ofertaDao.getOfertas());
        return "oferta/list";
    }
    //cosas inicio sesión que no se como hacer
    @RequestMapping("/listMisOfertas/{nif}")
    public String listMisOfertas(Model model, @PathVariable String nif){
        model.addAttribute("misOfertas",ofertaDao.getOfertasEstudiante(nif));
        return "oferta/listMisOfertas";
    }



}
