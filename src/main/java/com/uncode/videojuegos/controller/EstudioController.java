package com.uncode.videojuegos.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.uncode.videojuegos.model.entity.Estudio;
import com.uncode.videojuegos.model.service.EstudioService;
import com.uncode.videojuegos.model.service.exception.ServiceException;

@Controller
@RequestMapping("/estudios")
public class EstudioController {

    @Autowired
    private EstudioService service;

    @GetMapping
    public String getEstudios(Model model) {
        try {
            var estudios = service.getAll();
            model.addAttribute("estudios", estudios);
        } catch (ServiceException e) {
            model.addAttribute("error", e.getMessage());
        } catch (Exception e) {
            model.addAttribute("error", "Ha ocurrido un error inesperado");
        }
        return "estudio/index";
    }

    @GetMapping("/new")
    public String getNewEstudio(Model model) {
        model.addAttribute("action", "new");
        model.addAttribute("estudio", Estudio.builder().build());
        return "estudio/form";
    }

    @PostMapping("/new")
    public String postEstudio(@RequestParam String nombre, Model model,
            RedirectAttributes redirectAttributes) {
        try {
            var id = service.create(nombre);
            redirectAttributes.addFlashAttribute("success", "Estudio creado correctamente");
            return "redirect:/estudios/" + id;
        } catch (ServiceException e) {
            model.addAttribute("error", e.getMessage());
        } catch (Exception e) {
            model.addAttribute("error", "Ha ocurrido un error inesperado");
        }
        model.addAttribute("action", "new");
        model.addAttribute("estudio", Estudio.builder().nombre(nombre).build());
        return "estudio/form";
    }

    @GetMapping("/{id}")
    public String getEstudio(@PathVariable UUID id, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("action", "edit");
            model.addAttribute("estudio", service.get(id).orElseThrow(
                    () -> new ServiceException("Estudio no encontrada")));
            return "estudio/form";
        } catch (ServiceException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ha ocurrido un error inesperado");
        }
        return "redirect:/estudios";
    }

    @PostMapping("/{id}")
    public String putEstudio(@PathVariable UUID id, @RequestParam String nombre, Model model,
            RedirectAttributes redirectAttributes) {
        try {
            service.update(id, nombre);
            redirectAttributes.addFlashAttribute("success", "Estudio actualizado correctamente");
            return "redirect:/estudios/" + id;
        } catch (ServiceException e) {
            model.addAttribute("error", e.getMessage());
        } catch (Exception e) {
            model.addAttribute("error", "Ha ocurrido un error inesperado");
        }
        return "estudio/form";
    }

    @PostMapping("/{id}/delete")
    public String deleteEstudio(@PathVariable UUID id, Model model, RedirectAttributes redirectAttributes) {
        try {
            service.delete(id);
            redirectAttributes.addFlashAttribute("success", "Estudio eliminado correctamente");
        } catch (ServiceException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ha ocurrido un error inesperado");
        }
        return "redirect:/estudios";
    }
}
