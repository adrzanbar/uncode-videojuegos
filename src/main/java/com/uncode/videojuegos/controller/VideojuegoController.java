package com.uncode.videojuegos.controller;

import java.util.UUID;
import java.time.LocalDate;

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
import com.uncode.videojuegos.model.entity.Estudio;
import com.uncode.videojuegos.model.entity.Videojuego;
import com.uncode.videojuegos.model.service.VideojuegoService;
import com.uncode.videojuegos.model.service.CategoriaService;
import com.uncode.videojuegos.model.service.EstudioService;
import com.uncode.videojuegos.model.service.exception.ServiceException;

@Controller
@RequestMapping("/videojuegos")
public class VideojuegoController {

    @Autowired
    private VideojuegoService videojuegoService;

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private EstudioService estudioService;

    @GetMapping
    public String getVideojuegos(Model model) {
        try {
            var videojuegos = videojuegoService.getAll();
            model.addAttribute("videojuegos", videojuegos);
        } catch (ServiceException e) {
            model.addAttribute("error", e.getMessage());
        } catch (Exception e) {
            model.addAttribute("error", "Ha ocurrido un error inesperado");
        }
        return "videojuego/index";
    }

    @GetMapping("/new")
    public String getNewVideojuego(Model model) {
        model.addAttribute("action", "new");
        model.addAttribute("videojuego",
                Videojuego.builder().categoria(Categoria.builder().build()).estudio(Estudio.builder().build()).build());
        try {
            model.addAttribute("categorias", categoriaService.getAll());
            model.addAttribute("estudios", estudioService.getAll());
        } catch (ServiceException e) {
            model.addAttribute("error", e.getMessage());
        } catch (Exception e) {
            model.addAttribute("error", "Ha ocurrido un error inesperado");
        }
        return "videojuego/form";
    }

    @PostMapping("/new")
    public String postVideojuego(@RequestParam String nombre,
            @RequestParam String rutaimg,
            @RequestParam float precio,
            @RequestParam short cantidad,
            @RequestParam String descripcion,
            @RequestParam(required = false) Boolean oferta,
            @RequestParam String lanzamiento,
            @RequestParam UUID categoriaId,
            @RequestParam UUID estudioId,
            Model model,
            RedirectAttributes redirectAttributes) {
        try {
            var id = videojuegoService.create(nombre, rutaimg, precio, cantidad, descripcion, oferta != null,
                    LocalDate.parse(lanzamiento),
                    categoriaId, estudioId);
            redirectAttributes.addFlashAttribute("success", "Videojuego creado correctamente");
            return "redirect:/videojuegos/" + id;
        } catch (ServiceException e) {
            model.addAttribute("error", e.getMessage());
        } catch (Exception e) {
            model.addAttribute("error", "Ha ocurrido un error inesperado");
        }
        model.addAttribute("action", "new");
        model.addAttribute("videojuego", Videojuego.builder().nombre(nombre).rutaimg(rutaimg).precio(precio)
                .cantidad(cantidad).descripcion(descripcion).oferta(oferta != null).build());
        return "videojuego/form";
    }

    @GetMapping("/{id}")
    public String getVideojuego(@PathVariable UUID id, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("action", "edit");
            model.addAttribute("videojuego", videojuegoService.get(id).orElseThrow(
                    () -> new ServiceException("Videojuego no encontrado")));
            model.addAttribute("categorias", categoriaService.getAll());
            model.addAttribute("estudios", estudioService.getAll());
            return "videojuego/form";
        } catch (ServiceException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ha ocurrido un error inesperado");
        }
        return "redirect:/videojuegos";
    }

    @PostMapping("/{id}")
    public String putVideojuego(@PathVariable UUID id,
            @RequestParam String nombre,
            @RequestParam String rutaimg,
            @RequestParam float precio,
            @RequestParam short cantidad,
            @RequestParam String descripcion,
            @RequestParam boolean oferta,
            @RequestParam String lanzamiento,
            @RequestParam UUID categoriaId,
            @RequestParam UUID estudioId,
            Model model,
            RedirectAttributes redirectAttributes) {
        try {
            videojuegoService.update(id, nombre, rutaimg, precio, cantidad, descripcion, oferta,
                    LocalDate.parse(lanzamiento),
                    categoriaId, estudioId);
            redirectAttributes.addFlashAttribute("success", "Videojuego actualizado correctamente");
            return "redirect:/videojuegos/" + id;
        } catch (ServiceException e) {
            model.addAttribute("error", e.getMessage());
        } catch (Exception e) {
            model.addAttribute("error", "Ha ocurrido un error inesperado");
        }
        return "videojuego/form";
    }

    @PostMapping("/{id}/delete")
    public String deleteVideojuego(@PathVariable UUID id, Model model, RedirectAttributes redirectAttributes) {
        try {
            videojuegoService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Videojuego eliminado correctamente");
        } catch (ServiceException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ha ocurrido un error inesperado");
        }
        return "redirect:/videojuegos";
    }
}
