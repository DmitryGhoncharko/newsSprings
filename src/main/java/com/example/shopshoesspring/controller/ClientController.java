package com.example.shopshoesspring.controller;

import com.example.shopshoesspring.dto.CommentForm;
import com.example.shopshoesspring.entity.News;
import com.example.shopshoesspring.entity.Newscomment;
import com.example.shopshoesspring.entity.User;
import com.example.shopshoesspring.repository.NewsCommentRepository;
import com.example.shopshoesspring.repository.NewsRepository;
import com.example.shopshoesspring.repository.NewsThemeRepository;
import com.example.shopshoesspring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
@RequestMapping("/client")
public class ClientController {

    private final NewsThemeRepository newsThemeRepository;
    private final NewsRepository newsRepository;
    private final NewsCommentRepository newsCommentRepository;
    private final UserRepository userRepository;

    @GetMapping("/home")
    public String homePage() {
        return "client/сhome";
    }

    @GetMapping("/newsCategoryPage")
    public String newsCategoryPage(Model model) {
        model.addAttribute("news", newsThemeRepository.findAll());
        return "client/caregoryPage";
    }

    @GetMapping("/news/{newsId}")
    public String viewNews(@PathVariable("newsId") Long categoryId, Model model) {
        List<News> newsList = newsRepository.findByCategoryId(categoryId);
        model.addAttribute("news", newsList);
        return "client/newsPage";
    }

    @GetMapping("/newsDetail/{newsId}")
    public String viewNewsDetails(@PathVariable("newsId") Long newsId, Model model) {

        News news = newsRepository.findById(newsId).orElse(null);

        if (news == null) {

            return "redirect:/error";
        }
        model.addAttribute("news", news);
        return "client/newsDetailsPage";
    }

    @GetMapping("/like/{newsId}")
    public String likeNews(@PathVariable("newsId") Long newsId) {
        News news = newsRepository.findById(newsId).orElse(null);
        if (news != null) {
            news.setLikes(news.getLikes() + 1);
            newsRepository.save(news);
        }
        return "redirect:/client/newsDetail/" + newsId;
    }

    @GetMapping("/dislike/{newsId}")
    public String dislikeNews(@PathVariable("newsId") Long newsId) {
        News news = newsRepository.findById(newsId).orElse(null);
        if (news != null) {
            news.setDislikes(news.getDislikes() + 1);
            newsRepository.save(news);
        }
        return "redirect:/client/newsDetail/" + newsId;
    }

    @GetMapping("/newsComment/{newsId}")
    public String viewNewsComments(@PathVariable("newsId") Long newsId, Model model) {
        List<Newscomment> newscomments = newsCommentRepository.findByNewsId(newsId);
        model.addAttribute("comments", newscomments);
        if(newscomments.size() > 0) {
            model.addAttribute("newsId", newscomments.get(0).getNews().getId());
        }
        return "client/newsCommentsPage";
    }
    @PostMapping("/addComment")
    public String addComment(@ModelAttribute("commentForm") CommentForm commentForm) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<News> newsOptional = newsRepository.findById(commentForm.getNewsId());
        String currentUsername = authentication.getName();
        Optional<User> userOptional = userRepository.findUserByUserLogin(currentUsername);
        String commentText = commentForm.getComment();
        Newscomment comment = new Newscomment();
        comment.setUser(userOptional.get());
        comment.setNews(newsOptional.get());
        comment.setComment(commentText);
        comment.setCommentDate(Instant.now());
        newsCommentRepository.save(comment);

        return "redirect:/client/newsComment/" + newsOptional.get().getId();
    }

}