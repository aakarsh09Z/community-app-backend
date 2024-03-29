package com.aakarsh09z.communityappbackend.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "_user_")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String userId;
    private String fullname;
    @Column(nullable = false, unique = true)
    private String email;
    private String password;
    private Boolean isVerified;
    private String profileImageUrl;
    @ManyToMany(mappedBy = "members")
    private List<Community> communities;
    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<Chat> sentMessages;
    @JsonIgnore
    @ManyToMany(mappedBy = "seenByUsers")
    private List<Chat> seenMessages;
    @JsonIgnore
    @OneToMany(mappedBy = "owner")
    private List<Post> posts;
    @JsonIgnore
    @ManyToMany(mappedBy = "likes")
    private List<Post> likedPosts;
    @JsonIgnore
    @ManyToMany(mappedBy = "savedByUsers")
    private List<Post> savedPosts;
    @JsonIgnore
    @OneToMany(mappedBy = "commenter")
    private List<Comment> comments;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}