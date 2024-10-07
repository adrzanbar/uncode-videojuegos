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

import com.uncode.videojuegos.model.entity.Categoria;
import com.uncode.videojuegos.model.service.CategoriaService;
import com.uncode.videojuegos.model.service.exception.ServiceException;

@Controller
@RequestMapping("/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaService service;

    @GetMapping
    public String getCategorias(Model model) {
        try {
            var categorias = service.getAll();
            model.addAttribute("categorias", categorias);
        } catch (ServiceException e) {
            model.addAttribute("error", e.getMessage());
        } catch (Exception e) {
            model.addAttribute("error", "Ha ocurrido un error inesperado");
        }
        return "categoria/index";
    }

    @GetMapping("/new")
    public String getNewCategoria(Model model) {
        model.addAttribute("action", "new");
        model.addAttribute("categoria", Categoria.builder().build());
        return "categoria/form";
    }

    @PostMapping("/new")
    public String postCategoria(@RequestParam String nombre, Model model,
            RedirectAttributes redirectAttributes) {
        try {
            var id = service.create(nombre);
            redirectAttributes.addFlashAttribute("success", "Categoría creada correctamente");
            return "redirect:/categorias/" + id;
        } catch (ServiceException e) {
            model.addAttribute("error", e.getMessage());
        } catch (Exception e) {
            model.addAttribute("error", "Ha ocurrido un error inesperado");
        }
        model.addAttribute("action", "new");
        model.addAttribute("categoria", Categoria.builder().nombre(nombre).build());
        return "categoria/form";
    }

    @GetMapping("/{id}")
    public String getCategoria(@PathVariable UUID id, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("action", "edit");
            model.addAttribute("categoria", service.get(id).orElseThrow(
                    () -> new ServiceException("Categoria no encontrada")));
            return "categoria/form";
        } catch (ServiceException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ha ocurrido un error inesperado");
        }
        return "redirect:/categorias";
    }

    @PostMapping("/{id}")
    public String putCategoria(@PathVariable UUID id, @RequestParam String nombre, Model model,
            RedirectAttributes redirectAttributes) {
        try {
            service.update(id, nombre);
            redirectAttributes.addFlashAttribute("success", "Categoría actualizada correctamente");
            return "redirect:/categorias/" + id;
        } catch (ServiceException e) {
            model.addAttribute("error", e.getMessage());
        } catch (Exception e) {
            model.addAttribute("error", "Ha ocurrido un error inesperado");
        }
        return "categoria/form";
    }

    @PostMapping("/{id}/delete")
    public String deleteCategoria(@PathVariable UUID id, Model model, RedirectAttributes redirectAttributes) {
        try {
            service.delete(id);
            redirectAttributes.addFlashAttribute("success", "Categoria eliminada correctamente");
        } catch (ServiceException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ha ocurrido un error inesperado");
        }
        return "redirect:/categorias";
    }
}
