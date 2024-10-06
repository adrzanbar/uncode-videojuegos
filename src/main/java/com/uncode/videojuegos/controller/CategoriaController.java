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

    @PostMapping
    public String postCategoria(@RequestParam String nombre, Model model) {
        try {
            service.create(nombre);
            model.addAttribute("success", "Categoria guardada correctamente");
        } catch (ServiceException e) {
            model.addAttribute("error", e.getMessage());
        } catch (Exception e) {
            model.addAttribute("error", "Ha ocurrido un error inesperado");
        }
        return "redirect:/categorias";
    }

    @GetMapping("/new")
    public String getNewCategoria() {
        return "categoria/form";
    }

    @GetMapping("/{id}")
    public String getCategoria(@PathVariable UUID id, Model model) {
        try {
            var categoria = service.get(id).orElseThrow(
                () -> new ServiceException("Categoria no encontrada")
            );
            model.addAttribute("categoria", categoria);
        } catch (ServiceException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/categorias";
        } catch (Exception e) {
            model.addAttribute("error", "Ha ocurrido un error inesperado");
            return "redirect:/categorias";
        }
        return "categoria/form";
    }

    @PostMapping("/{id}")
    public String putCategoria(@PathVariable UUID id, @RequestParam String nombre, Model model) {
        try {
            service.update(id, nombre);
            model.addAttribute("success", "Categoria actualizada correctamente");
        } catch (ServiceException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/categorias";
        } catch (Exception e) {
            model.addAttribute("error", "Ha ocurrido un error inesperado");
            return "redirect:/categorias";
        }
        return "redirect:/categorias";
    }

    @PostMapping("/{id}/delete")
    public String deleteCategoria(@PathVariable UUID id, Model model) {
        try {
            service.delete(id);
            model.addAttribute("success", "Categoria eliminada correctamente");
        } catch (ServiceException e) {
            model.addAttribute("error", e.getMessage());
        } catch (Exception e) {
            model.addAttribute("error", "Ha ocurrido un error inesperado");
        }
        return "redirect:/categorias";
    }
}
