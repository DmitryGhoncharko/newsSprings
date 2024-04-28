package com.example.shopshoesspring.controller;


import com.example.shopshoesspring.entity.*;
import com.example.shopshoesspring.repository.BannedUserRepository;
import com.example.shopshoesspring.repository.NewsRepository;
import com.example.shopshoesspring.repository.NewsThemeRepository;
import com.example.shopshoesspring.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Optional;


@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final NewsThemeRepository newsThemeRepository;
    private final NewsRepository newsRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BannedUserRepository bannedUserRepository;

    @GetMapping("/home")
    public String homePage() {
        return "admin/ahome";
    }

    @GetMapping("/addCategoryNewsPage")
    public String addCategoryNewsPage() {
        return "admin/addNewsTheme";
    }

    @GetMapping("/categoryListPage")
    public String categoryListPage(Model model) {
        model.addAttribute("newsThemeList", newsThemeRepository.findAll());
        return "admin/newsThemeList";
    }

    @GetMapping("/updateTheme/{id}")
    public String updateTheme(@PathVariable("id") Long id, Model model) {
        Optional<NewsTheme> newsThemeOptional = newsThemeRepository.findById(id);
        if (newsThemeOptional.isPresent()) {
            NewsTheme newsTheme = newsThemeOptional.get();
            model.addAttribute("newsTheme", newsTheme);
        }
        return "admin/updateThemePage";
    }

    @GetMapping("/deleteTheme/{id}")
    public String deleteTheme(@PathVariable("id") Long id) {
        newsThemeRepository.deleteById(id);
        return "redirect:/admin/categoryListPage";
    }

    @GetMapping("/addNewsPage")
    public String addNewsPage(Model model) {
        model.addAttribute("categories", newsThemeRepository.findAll());
        return "admin/addNewsPage";
    }

    @GetMapping("/newsListPage")
    public String newsListPage(Model model) {
        model.addAttribute("news", newsRepository.findAll());
        return "admin/newsListPage";
    }

    @GetMapping("/deleteNews/{id}")
    public String deleteNews(@PathVariable("id") Long id) {
        newsRepository.deleteById(id);
        return "redirect:/admin/newsListPage";
    }

    @GetMapping("/addUserPage")
    public String addUserPage() {
        return "admin/addUser";
    }

    @GetMapping("/usersListPage")
    public String usersListPage(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "admin/userListPage";
    }

    @GetMapping("/updateNews/{id}")
    public String updateNewsPage(@PathVariable("id") Long id, Model model) {
        Optional<News> newsOptional = newsRepository.findById(id);
        if (newsOptional.isPresent()) {
            News news = newsOptional.get();
            model.addAttribute("news", news);
            model.addAttribute("categories", newsThemeRepository.findAll());
        }
        return "admin/updateNews";
    }

    @GetMapping("/bannedList")
    public String bannedList(Model model) {
        model.addAttribute("bannedUsers", bannedUserRepository.findAll());
        return "admin/bannedList";
    }

    @PostMapping("/addNewsTheme")
    public String addNewsTheme(@RequestParam("newsThemeName") String newsThemeName) {
        NewsTheme newsTheme = new NewsTheme();
        newsTheme.setNewsThemeName(newsThemeName);
        newsThemeRepository.save(newsTheme);
        return "redirect:/admin/categoryListPage";
    }

    @PostMapping("/updateTheme")
    public String updateTheme(@RequestParam("id") Long id, @RequestParam("newsThemeName") String newsThemeName) {
        Optional<NewsTheme> newsThemeOptional = newsThemeRepository.findById(id);
        if (newsThemeOptional.isPresent()) {
            NewsTheme newsTheme = newsThemeOptional.get();
            newsTheme.setNewsThemeName(newsThemeName);
            newsThemeRepository.save(newsTheme);
        }
        return "redirect:/admin/categoryListPage";
    }

    @PostMapping("/addNews")
    public String addNews(@RequestParam("title") String title, @RequestParam("content") String content, @RequestParam("category") Long categoryId) {

        NewsTheme category = newsThemeRepository.findById(categoryId).orElseThrow(() -> new IllegalArgumentException("Invalid category id: " + categoryId));
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        Optional<User> userOptional = userRepository.findUserByUserLogin(currentUsername);

        News news = News.builder().title(title).content(content).category(category).creationDate(Instant.now()).likes(0).dislikes(0).author(userOptional.orElseThrow()).build();

        newsRepository.save(news);

        return "redirect:/admin/addNewsPage";
    }

    @PostMapping("/updateNews")
    public String updateNews(@RequestParam("id") Long id, @RequestParam("title") String title, @RequestParam("content") String content, @RequestParam("category") Long categoryId) {
        NewsTheme category = newsThemeRepository.findById(categoryId).orElseThrow(() -> new IllegalArgumentException("Invalid category id: " + categoryId));

        News updatedNews = newsRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid news id: " + id));

        updatedNews.setTitle(title);
        updatedNews.setContent(content);
        updatedNews.setCategory(category);

        newsRepository.save(updatedNews);

        return "redirect:/admin/newsListPage";
    }

    @GetMapping("/banUser/{userId}")
    public String banUser(@PathVariable("userId") Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            BannedUser bannedUser = new BannedUser();
            bannedUser.setUser(user);
            bannedUserRepository.save(bannedUser);
        }
        return "redirect:/admin/usersListPage";
    }

    @GetMapping("/unbanUser/{userId}")
    @Transactional
    public String unbanUser(@PathVariable("userId") Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            bannedUserRepository.deleteByUserId(userId);
        }
        return "redirect:/admin/usersListPage";
    }

    @PostMapping("/addUser")
    public String addUser(@RequestParam("userLogin") String userLogin, @RequestParam("userPassword") String userPassword, @RequestParam("userRole") String userRole) {
        UserRole userRoleForCreate = null;
        if (userRole.equals("admin")) {
            userRoleForCreate = new UserRole(1L, "ADMIN");
        } else {
            userRoleForCreate = new UserRole(2L, "CLIENT");
        }
        User newUser = User.builder().userLogin(userLogin).userPassword(passwordEncoder.encode(userPassword)).userBalance(500.5).userRole(userRoleForCreate).build();

        userRepository.save(newUser);

        return "redirect:/admin/usersListPage";
    }
}
