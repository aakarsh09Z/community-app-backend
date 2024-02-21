package com.aakarsh09z.communityappbackend.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;
    private String caption;
    private LocalDateTime timestamp;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "likes",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> likes;
    @ManyToOne
    @JoinColumn(name = "community_posts")
    private Community community;
    @JsonIgnore
    @OneToMany(mappedBy="post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;
    @ManyToMany
    @JoinTable(name = "saved",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> savedByUsers;
}
